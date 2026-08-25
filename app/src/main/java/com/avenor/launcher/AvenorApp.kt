package com.avenor.launcher

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlin.math.roundToInt

internal enum class AvenorSurface {
    Home,
    Drawer,
}

private data class FavoriteAddTarget(
    val containerId: String?,
    val label: String,
    val provisional: Boolean,
)

private data class FavoriteRevealRequest(
    val containerId: String,
    val identity: LaunchableIdentity,
)

private fun nextVerticalListId(containers: List<FavoriteContainer>): String {
    var suffix = 1
    while (containers.any { it.id == "vertical-list-$suffix" }) {
        suffix += 1
    }
    return "vertical-list-$suffix"
}

private const val DRAWER_MOVEMENT_PER_GESTURE_DP = 1.5f
private const val TARGET_FLING_THRESHOLD_DP_PER_SECOND = 1_000f

internal fun drawerGestureProgress(
    displacementPx: Float,
    fullGestureDistancePx: Float,
): Float = (displacementPx / fullGestureDistancePx).coerceIn(0f, 1f)

internal fun drawerInteractiveDisplacement(gestureDisplacementPx: Float): Float =
    gestureDisplacementPx * DRAWER_MOVEMENT_PER_GESTURE_DP

internal fun transitionTarget(
    origin: AvenorSurface,
    gestureDisplacementPx: Float,
    completionThresholdPx: Float,
    targetVelocity: Float,
    targetFlingThresholdPx: Float,
): AvenorSurface {
    val completes = gestureDisplacementPx >= completionThresholdPx ||
        targetVelocity >= targetFlingThresholdPx
    return if (completes) {
        if (origin == AvenorSurface.Home) AvenorSurface.Drawer else AvenorSurface.Home
    } else {
        origin
    }
}

@Composable
internal fun AvenorApp(systemHomeEvents: Flow<Unit>? = null) {
    val context = LocalContext.current
    val inventoryLoader = remember(context) {
        AndroidLaunchableInventoryLoader(context)
    }
    val entryLauncher = remember(context) {
        AndroidLaunchableEntryLauncher(context)
    }
    val favoriteStore = remember(context) { AtomicFileFavoriteStore(context) }
    val informationLauncher = remember(context) { AndroidApplicationInformationLauncher(context) }
    val shortcutController = remember(context) {
        AndroidApplicationShortcutController(context)
    }
    val settingsPlatform = remember(context) { AndroidSettingsPlatform(context) }
    val licenseText = remember(context) { readAvenorLicense(context) }
    val accessibilityLockController = remember(context) {
        if (BuildConfig.DEBUG) {
            AndroidAccessibilityLockController(
                context = context,
                serviceComponent = debugAccessibilityLockServiceComponent(context),
            )
        } else {
            EmptyAccessibilityLockController
        }
    }
    AvenorApp(
        systemHomeEvents = systemHomeEvents,
        inventoryLoader = inventoryLoader,
        entryLauncher = entryLauncher,
        favoriteStore = favoriteStore,
        informationLauncher = informationLauncher,
        shortcutController = shortcutController,
        settingsPlatform = settingsPlatform,
        licenseText = licenseText,
        accessibilityLockController = accessibilityLockController,
    )
}

@Composable
internal fun AvenorApp(
    systemHomeEvents: Flow<Unit>? = null,
    inventoryLoader: LaunchableInventoryLoader,
    entryLauncher: LaunchableEntryLauncher = LaunchableEntryLauncher { false },
    favoriteStore: FavoriteStore? = null,
    informationLauncher: ApplicationInformationLauncher = ApplicationInformationLauncher { false },
    shortcutController: ApplicationShortcutController = EmptyApplicationShortcutController,
    settingsPlatform: SettingsPlatform = EmptySettingsPlatform,
    licenseText: String = "",
    accessibilityLockController: AccessibilityLockController = EmptyAccessibilityLockController,
) {
    val androidContext = LocalContext.current
    val density = LocalDensity.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val homeFavoriteListState = rememberLazyListState()
    val companionFavoriteListState = rememberLazyListState()
    val drawerListState = rememberLazyListState()
    val fullGestureDistancePx = with(density) {
        dimensionResource(R.dimen.drawer_transition_full_gesture_distance).toPx()
    }
    val completionThresholdPx = with(density) {
        dimensionResource(R.dimen.drawer_entry_drag_threshold).toPx()
    }
    val targetFlingThresholdPx = with(density) {
        TARGET_FLING_THRESHOLD_DP_PER_SECOND.dp.toPx()
    }
    var settledSurface by remember { mutableStateOf(AvenorSurface.Home) }
    var drawerActivated by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    var drawerOffsetPx by remember { mutableFloatStateOf(0f) }
    var containerHeightPx by remember { mutableFloatStateOf(0f) }
    var gestureDisplacementPx by remember { mutableFloatStateOf(0f) }
    var drawerTransitionOwnsGesture by remember { mutableStateOf(false) }
    var homeTransitionOwnsGesture by remember { mutableStateOf(false) }
    var settleJob by remember { mutableStateOf<Job?>(null) }
    val effectiveFavoriteStore = favoriteStore ?: remember { InMemoryFavoriteStore() }
    val favoriteState by effectiveFavoriteStore.state.collectAsState()
    val inventoryCoordinator = remember(inventoryLoader) {
        LaunchableInventoryCoordinator(inventoryLoader)
    }
    val inventoryState by inventoryCoordinator.state.collectAsState()
    var favoriteAvailability by remember {
        mutableStateOf<Map<LaunchableIdentity, FavoriteAvailability>>(emptyMap())
    }
    var selectedEntry by remember { mutableStateOf<LaunchableEntry?>(null) }
    var selectedEntryFromHome by remember { mutableStateOf(false) }
    var homeEditMode by remember { mutableStateOf(false) }
    var favoriteAddTarget by remember { mutableStateOf<FavoriteAddTarget?>(null) }
    var favoriteSelection by remember { mutableStateOf<List<LaunchableIdentity>>(emptyList()) }
    var favoriteSelectionSaving by remember { mutableStateOf(false) }
    var favoriteRevealRequest by remember { mutableStateOf<FavoriteRevealRequest?>(null) }
    var settingsOpen by remember { mutableStateOf(false) }
    var externalLaunchPendingReturn by remember { mutableStateOf(false) }
    var shortcutOwner by remember { mutableStateOf<LaunchableIdentity?>(null) }
    var applicationShortcuts by remember { mutableStateOf(emptyList<ApplicationShortcut>()) }
    var editMembership by remember { mutableStateOf<Set<LaunchableIdentity>>(emptySet()) }
    val homeActivationGuard = remember { RapidActivationGuard() }
    val unavailableFavoriteMessage = stringResource(R.string.favorite_application_unavailable)
    val launchFailureMessage = stringResource(R.string.application_unable_to_open)
    val favoritesChangedMessage = stringResource(R.string.favorites_changed_edit_ended)
    val inventoryFailureMessage = stringResource(R.string.inventory_update_failed_edit_ended)
    val addToListLabel = stringResource(R.string.drawer_selection_add_to_list)
    val createListLabel = stringResource(R.string.drawer_selection_create_list)

    LaunchedEffect(effectiveFavoriteStore) {
        effectiveFavoriteStore.load()
    }

    LaunchedEffect(inventoryCoordinator) {
        inventoryCoordinator.load(showLoading = true)
    }

    LaunchedEffect(selectedEntry, shortcutController) {
        shortcutOwner = null
        applicationShortcuts = emptyList()
        val entry = selectedEntry ?: return@LaunchedEffect
        val loaded = shortcutController.load(entry)
        if (selectedEntry?.identity == entry.identity) {
            shortcutOwner = entry.identity
            applicationShortcuts = loaded
        }
    }
    LaunchedEffect(favoriteState) {
        val state = favoriteState
        if (state !is FavoriteReadState.Readable) selectedEntry = null
        if (homeEditMode) {
            val readable = state as? FavoriteReadState.Readable
            if (readable == null) {
                homeEditMode = false
                Toast.makeText(androidContext, inventoryFailureMessage, Toast.LENGTH_SHORT).show()
            } else if (readable.identities.toSet() != editMembership) {
                homeEditMode = false
                Toast.makeText(androidContext, favoritesChangedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(inventoryState) {
        if (homeEditMode && inventoryState is LaunchableInventoryState.Error) {
            homeEditMode = false
            Toast.makeText(androidContext, inventoryFailureMessage, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(inventoryState, favoriteAddTarget, favoriteSelectionSaving) {
        if (favoriteAddTarget == null || favoriteSelectionSaving) return@LaunchedEffect
        val content = inventoryState as? LaunchableInventoryState.Content
            ?: return@LaunchedEffect
        val availableIdentities = content.snapshot.entries
            .mapTo(mutableSetOf<LaunchableIdentity>(), LaunchableEntry::identity)
        val temporarilyUnavailable = content.snapshot.profileReadStatus
            .filterValues { it == ProfileInventoryReadStatus.Unavailable }
            .keys
        favoriteSelection = favoriteSelection.filter { identity ->
            identity in availableIdentities || identity.profileSerialNumber in temporarilyUnavailable
        }
    }

    val favoriteMembership = (favoriteState as? FavoriteReadState.Readable)?.identities?.toSet()
    // Availability follows which favorites exist, not their order, so keying on membership keeps a
    // reorder from rebuilding the map and refreshing every row instead of the moved positions.
    LaunchedEffect(
        favoriteMembership,
        inventoryState,
        effectiveFavoriteStore,
        inventoryCoordinator,
    ) {
        val identities = favoriteMembership ?: return@LaunchedEffect
        val completeInventory = inventoryState as? LaunchableInventoryState.Content
            ?: return@LaunchedEffect
        val resolved = inventoryCoordinator.resolveFavorites(
            identities = identities.toList(),
            snapshot = completeInventory.snapshot,
        )
        favoriteAvailability = resolved
        val confirmedRemovedIdentities = resolved
            .filterValues { it == FavoriteAvailability.ConfirmedRemoved }
            .keys
        if (confirmedRemovedIdentities.isNotEmpty()) {
            effectiveFavoriteStore.removeAll(confirmedRemovedIdentities)
        }
    }

    fun settleTo(target: AvenorSurface) {
        drawerTransitionOwnsGesture = false
        homeTransitionOwnsGesture = false
        settleJob?.cancel()
        settleJob = scope.launch {
            val startProgress = progress
            val startOffset = drawerOffsetPx
            val targetProgress = if (target == AvenorSurface.Drawer) 1f else 0f
            val targetOffset = if (target == AvenorSurface.Drawer) 0f else containerHeightPx
            animate(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = spring(),
            ) { fraction, _ ->
                val boundedFraction = fraction.coerceIn(0f, 1f)
                progress = startProgress + ((targetProgress - startProgress) * boundedFraction)
                drawerOffsetPx = startOffset + ((targetOffset - startOffset) * boundedFraction)
            }
            settledSurface = target
            gestureDisplacementPx = 0f
        }
    }

    fun beginGesture(origin: AvenorSurface) {
        settleJob?.cancel()
        gestureDisplacementPx = if (origin == AvenorSurface.Home) {
            progress * fullGestureDistancePx
        } else {
            (1f - progress) * fullGestureDistancePx
        }
        if (origin == AvenorSurface.Home) drawerActivated = true
    }

    fun dragTowardTarget(origin: AvenorSurface, targetDisplacement: Float) {
        val previousDisplacement = gestureDisplacementPx
        gestureDisplacementPx =
            (gestureDisplacementPx + targetDisplacement).coerceAtLeast(0f)
        val consumedTargetDisplacement = gestureDisplacementPx - previousDisplacement
        val gestureProgress = drawerGestureProgress(
            gestureDisplacementPx,
            fullGestureDistancePx,
        )
        progress = if (origin == AvenorSurface.Home) gestureProgress else 1f - gestureProgress
        drawerOffsetPx = if (origin == AvenorSurface.Home) {
            (drawerOffsetPx - drawerInteractiveDisplacement(consumedTargetDisplacement))
                .coerceIn(0f, containerHeightPx)
        } else {
            (drawerOffsetPx + drawerInteractiveDisplacement(consumedTargetDisplacement))
                .coerceIn(0f, containerHeightPx)
        }
    }

    fun finishGesture(origin: AvenorSurface, targetVelocity: Float) {
        val target = transitionTarget(
            origin = origin,
            gestureDisplacementPx = gestureDisplacementPx,
            completionThresholdPx = completionThresholdPx,
            targetVelocity = targetVelocity,
            targetFlingThresholdPx = targetFlingThresholdPx,
        )
        if (target == AvenorSurface.Home &&
            favoriteAddTarget != null &&
            !favoriteSelectionSaving
        ) {
            favoriteAddTarget = null
            favoriteSelection = emptyList()
        }
        settleTo(target)
    }

    fun returnToHome() {
        settleJob?.cancel()
        settledSurface = AvenorSurface.Home
        drawerActivated = false
        progress = 0f
        drawerOffsetPx = containerHeightPx
        gestureDisplacementPx = 0f
        drawerTransitionOwnsGesture = false
        homeTransitionOwnsGesture = false
        selectedEntry = null
        selectedEntryFromHome = false
        settingsOpen = false
        homeEditMode = false
        favoriteRevealRequest = null
        if (!favoriteSelectionSaving) {
            favoriteAddTarget = null
            favoriteSelection = emptyList()
        }
    }

    fun closeFavoriteSelection() {
        favoriteAddTarget = null
        favoriteSelection = emptyList()
        favoriteSelectionSaving = false
        settleTo(AvenorSurface.Home)
    }

    fun openFavoriteSelection(containerId: String) {
        val readable = favoriteState as? FavoriteReadState.Readable ?: return
        val index = readable.aggregate.verticalLists.indexOfFirst { it.id == containerId }
        if (index < 0) return
        favoriteAddTarget = FavoriteAddTarget(
            containerId = containerId,
            label = addToListLabel,
            provisional = false,
        )
        favoriteSelection = emptyList()
        favoriteSelectionSaving = false
        drawerActivated = true
        settleTo(AvenorSurface.Drawer)
    }

    fun openProvisionalFavoriteSelection() {
        val state = favoriteState
        if (state !is FavoriteReadState.Readable ||
            state.aggregate.verticalLists.size >= 2
        ) {
            return
        }
        favoriteAddTarget = FavoriteAddTarget(
            containerId = null,
            label = createListLabel,
            provisional = true,
        )
        favoriteSelection = emptyList()
        favoriteSelectionSaving = false
        drawerActivated = true
        settleTo(AvenorSurface.Drawer)
    }

    fun confirmFavoriteSelection() {
        val target = favoriteAddTarget ?: return
        val selected = favoriteSelection
        if (selected.isEmpty() || favoriteSelectionSaving) return
        favoriteSelectionSaving = true
        scope.launch {
            val inventorySnapshot = (inventoryCoordinator.state.value
                as? LaunchableInventoryState.Content)?.snapshot
            val currentInventoryIdentities = inventorySnapshot?.entries
                ?.mapTo(mutableSetOf<LaunchableIdentity>(), LaunchableEntry::identity)
                .orEmpty()
            val temporarilyUnavailableIdentities = inventorySnapshot?.profileReadStatus
                ?.filterValues { it == ProfileInventoryReadStatus.Unavailable }
                ?.keys
                .orEmpty()
            var updatedAggregate: FavoriteAggregate? = null
            var targetInvalid = false
            var noValidSelection = false
            val savedAggregate = inventorySnapshot?.let {
                effectiveFavoriteStore.updateAggregate { aggregate ->
                    val container = target.containerId?.let { containerId ->
                        aggregate.verticalLists.firstOrNull { it.id == containerId }
                    }
                    if ((target.containerId != null && container == null) ||
                        (target.provisional && aggregate.verticalLists.size >= 2)
                    ) {
                        targetInvalid = true
                        return@updateAggregate aggregate
                    }
                    val appendable = selected.filter { identity ->
                        (identity in currentInventoryIdentities ||
                            identity.profileSerialNumber in temporarilyUnavailableIdentities) &&
                            identity !in aggregate.identities
                    }
                    if (appendable.isEmpty()) {
                        noValidSelection = true
                        updatedAggregate = aggregate
                        return@updateAggregate aggregate
                    }
                    val updated = if (target.provisional) {
                        aggregate.copy(
                            verticalLists = aggregate.verticalLists + FavoriteContainer(
                                id = nextVerticalListId(aggregate.verticalLists),
                                type = FavoriteContainerType.VerticalList,
                                identities = appendable,
                                listSize = FavoriteListSize.Medium,
                            ),
                        )
                    } else {
                        container!!.copy(
                            identities = container.identities + appendable,
                        ).let { updatedContainer ->
                            aggregate.updateVerticalList(container.id) { updatedContainer }
                        }
                    }
                    updatedAggregate = updated
                    updated
                }
            }
            if (targetInvalid) {
                favoriteSelectionSaving = false
                closeFavoriteSelection()
                Toast.makeText(
                    androidContext,
                    R.string.favorite_reorder_unavailable,
                    Toast.LENGTH_SHORT,
                ).show()
                return@launch
            }
            if (savedAggregate != null) {
                updatedAggregate?.let { editMembership = it.identities.toSet() }
                if (!noValidSelection) {
                    updatedAggregate?.let { aggregate ->
                        val targetContainerId = target.containerId
                            ?: aggregate.verticalLists.lastOrNull()?.id
                        val revealedIdentity = selected.firstOrNull { identity ->
                            targetContainerId?.let { containerId ->
                                aggregate.verticalLists
                                    .firstOrNull { it.id == containerId }
                                    ?.identities
                                    ?.contains(identity) == true
                            } == true
                        }
                        if (targetContainerId != null && revealedIdentity != null) {
                            favoriteRevealRequest = FavoriteRevealRequest(
                                containerId = targetContainerId,
                                identity = revealedIdentity,
                            )
                        }
                    }
                }
                closeFavoriteSelection()
            } else {
                favoriteSelectionSaving = false
                if (!homeEditMode) {
                    favoriteAddTarget = null
                    favoriteSelection = emptyList()
                }
                Toast.makeText(
                    androidContext,
                    R.string.favorite_reorder_unavailable,
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    LaunchedEffect(systemHomeEvents) {
        systemHomeEvents?.collect {
            returnToHome()
        }
    }

    DisposableEffect(inventoryCoordinator) {
        val cacheInvalidationObservation = inventoryCoordinator.observe {
            scope.launch {
                inventoryCoordinator.load(showLoading = false)
            }
        }
        onDispose { cacheInvalidationObservation?.stop() }
    }

    DisposableEffect(lifecycleOwner, inventoryCoordinator) {
        var hasResumed = false
        var wasPaused = false
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> wasPaused = true
                Lifecycle.Event.ON_RESUME -> {
                    if (hasResumed && wasPaused && externalLaunchPendingReturn) {
                        returnToHome()
                        externalLaunchPendingReturn = false
                        scope.launch {
                            if (inventoryCoordinator.state.value is LaunchableInventoryState.Content) {
                                inventoryCoordinator.load(showLoading = false)
                            }
                        }
                    }
                    hasResumed = true
                    wasPaused = false
                }
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    BackHandler(enabled = favoriteAddTarget != null && !favoriteSelectionSaving) {
        closeFavoriteSelection()
    }

    BackHandler(enabled = favoriteAddTarget != null && favoriteSelectionSaving) {}

    BackHandler(enabled = homeEditMode && favoriteAddTarget == null) {
        homeEditMode = false
    }

    BackHandler(enabled = !homeEditMode && !settingsOpen &&
        (settledSurface == AvenorSurface.Drawer || progress > 0f)
    ) {
        settleTo(AvenorSurface.Home)
    }

    BackHandler(enabled = settingsOpen) { settingsOpen = false }

    val gestureModifier = Modifier.pointerInput(
        settledSurface,
        containerHeightPx,
        fullGestureDistancePx,
        completionThresholdPx,
        targetFlingThresholdPx,
    ) {
        val velocityTracker = VelocityTracker()
        var gestureOrigin = settledSurface

        detectVerticalDragGestures(
            onDragStart = { startPosition ->
                gestureOrigin = settledSurface
                beginGesture(gestureOrigin)
                velocityTracker.resetTracking()
                velocityTracker.addPosition(0L, startPosition)
            },
            onVerticalDrag = { change, dragAmount ->
                velocityTracker.addPosition(change.uptimeMillis, change.position)
                val targetDisplacement = if (gestureOrigin == AvenorSurface.Home) {
                    -dragAmount
                } else {
                    dragAmount
                }
                dragTowardTarget(gestureOrigin, targetDisplacement)
            },
            onDragCancel = {
                settleTo(gestureOrigin)
            },
            onDragEnd = {
                val verticalVelocity = velocityTracker.calculateVelocity().y
                val targetVelocity = if (gestureOrigin == AvenorSurface.Home) {
                    -verticalVelocity
                } else {
                    verticalVelocity
                }
                finishGesture(gestureOrigin, targetVelocity)
            },
        )
    }

    val currentFavoriteSelectionSaving by rememberUpdatedState(
        favoriteSelectionSaving,
    )
    val drawerNestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (source != NestedScrollSource.UserInput ||
                    currentFavoriteSelectionSaving
                ) {
                    return Offset.Zero
                }
                val shouldTakeOwnership = drawerTransitionOwnsGesture ||
                    (available.y > 0f && !drawerListState.canScrollBackward)
                if (!shouldTakeOwnership) return Offset.Zero
                if (!drawerTransitionOwnsGesture) {
                    beginGesture(AvenorSurface.Drawer)
                    drawerTransitionOwnsGesture = true
                }
                dragTowardTarget(AvenorSurface.Drawer, available.y)
                return Offset(x = 0f, y = available.y)
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                if (source != NestedScrollSource.UserInput ||
                    available.y <= 0f ||
                    currentFavoriteSelectionSaving
                ) {
                    return Offset.Zero
                }
                if (!drawerTransitionOwnsGesture) {
                    beginGesture(AvenorSurface.Drawer)
                    drawerTransitionOwnsGesture = true
                }
                dragTowardTarget(AvenorSurface.Drawer, available.y)
                return Offset(x = 0f, y = available.y)
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                if (currentFavoriteSelectionSaving) return Velocity.Zero
                if (!drawerTransitionOwnsGesture) return Velocity.Zero
                finishGesture(AvenorSurface.Drawer, available.y)
                return Velocity(x = 0f, y = available.y)
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                if (currentFavoriteSelectionSaving) return Velocity.Zero
                if (!drawerTransitionOwnsGesture) return Velocity.Zero
                finishGesture(AvenorSurface.Drawer, available.y)
                return Velocity(x = 0f, y = available.y)
            }
        }
    }

    fun homeTransitionNestedScrollConnection(listState: LazyListState): NestedScrollConnection =
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (source != NestedScrollSource.UserInput ||
                    (!homeTransitionOwnsGesture && available.y >= 0f)
                ) {
                    return Offset.Zero
                }
                if (!homeTransitionOwnsGesture && listState.canScrollForward) {
                    return Offset.Zero
                }
                if (!homeTransitionOwnsGesture) {
                    beginGesture(AvenorSurface.Home)
                    homeTransitionOwnsGesture = true
                }
                dragTowardTarget(AvenorSurface.Home, -available.y)
                return Offset(x = 0f, y = available.y)
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                if (source != NestedScrollSource.UserInput || available.y >= 0f) {
                    return Offset.Zero
                }
                if (!homeTransitionOwnsGesture) {
                    beginGesture(AvenorSurface.Home)
                    homeTransitionOwnsGesture = true
                }
                dragTowardTarget(AvenorSurface.Home, -available.y)
                return Offset(x = 0f, y = available.y)
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                if (!homeTransitionOwnsGesture) return Velocity.Zero
                finishGesture(AvenorSurface.Home, -available.y)
                return Velocity(x = 0f, y = available.y)
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                if (!homeTransitionOwnsGesture) return Velocity.Zero
                finishGesture(AvenorSurface.Home, -available.y)
                return Velocity(x = 0f, y = available.y)
            }
        }

    val homeNestedScrollConnection = remember(homeFavoriteListState) {
        homeTransitionNestedScrollConnection(homeFavoriteListState)
    }
    val companionNestedScrollConnection = remember(companionFavoriteListState) {
        homeTransitionNestedScrollConnection(companionFavoriteListState)
    }

    val drawerPointerSafetyModifier = Modifier.pointerInput(Unit) {
        awaitEachGesture {
            awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            var multiplePointersDetected = false
            var pointersRemainPressed: Boolean
            do {
                val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                if (!multiplePointersDetected && event.changes.count { it.pressed } > 1) {
                    multiplePointersDetected = true
                    if (drawerTransitionOwnsGesture) settleTo(AvenorSurface.Drawer)
                }
                if (multiplePointersDetected) {
                    event.changes.forEach { change -> change.consume() }
                }
                pointersRemainPressed = event.changes.any { it.pressed }
            } while (pointersRemainPressed)
        }
    }
    val homePointerSafetyModifier = Modifier.pointerInput(Unit) {
        awaitEachGesture {
            awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            var multiplePointersDetected = false
            var pointersRemainPressed: Boolean
            do {
                val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                if (!multiplePointersDetected && event.changes.count { it.pressed } > 1) {
                    multiplePointersDetected = true
                    settleTo(AvenorSurface.Home)
                }
                if (multiplePointersDetected) {
                    event.changes.forEach { change -> change.consume() }
                }
                pointersRemainPressed = event.changes.any { it.pressed }
            } while (pointersRemainPressed)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                val previousHeight = containerHeightPx
                containerHeightPx = size.height.toFloat()
                if (settledSurface == AvenorSurface.Home && progress == 0f &&
                    (drawerOffsetPx == 0f || drawerOffsetPx == previousHeight)
                ) {
                    drawerOffsetPx = containerHeightPx
                }
            },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha((1f - (2f * progress)).coerceIn(0f, 1f))
                .then(homePointerSafetyModifier)
                .then(if (homeEditMode) Modifier else gestureModifier)
                .testTag("home_surface"),
        ) {
            HomeScreen(
                favoriteState = favoriteState,
                favoriteListState = homeFavoriteListState,
                favoriteNestedScrollConnection =
                    homeNestedScrollConnection.takeUnless { homeEditMode },
                companionFavoriteListState = companionFavoriteListState,
                companionFavoriteNestedScrollConnection =
                    companionNestedScrollConnection.takeUnless { homeEditMode },
                accessibilityLockController = accessibilityLockController,
                favoriteAvailability = favoriteAvailability,
                editMode = homeEditMode,
                onRetryFavorites = { scope.launch { effectiveFavoriteStore.load() } },
                onRequestEditMode = {
                    editMembership = (favoriteState as? FavoriteReadState.Readable)
                        ?.identities
                        ?.toSet()
                        .orEmpty()
                    homeEditMode = true
                },
                onLongPressFavorite = { entry ->
                    selectedEntryFromHome = true
                    selectedEntry = entry
                },
                onAddFavoritesToList = ::openFavoriteSelection,
                onAddProvisionalFavorites = ::openProvisionalFavoriteSelection,
                favoriteRevealContainerId = favoriteRevealRequest?.containerId,
                favoriteRevealIdentity = favoriteRevealRequest?.identity,
                onFavoriteRevealComplete = { favoriteRevealRequest = null },
                onCommitFavoriteComposition = { transform ->
                    val aggregate = effectiveFavoriteStore.updateAggregate(transform)
                    if (aggregate == null) {
                        Toast.makeText(
                            androidContext,
                            R.string.favorite_reorder_unavailable,
                            Toast.LENGTH_SHORT,
                        ).show()
                        null
                    } else {
                        if (homeEditMode) {
                            editMembership = aggregate.identities.toSet()
                        }
                        aggregate
                    }
                },
                onLaunchFavorite = { availability ->
                    when {
                        availability !is FavoriteAvailability.Available -> {
                            Toast.makeText(
                                androidContext,
                                unavailableFavoriteMessage,
                                Toast.LENGTH_SHORT,
                            ).show()
                        }

                        homeActivationGuard.tryAcquire() -> {
                            if (entryLauncher.launch(availability.entry)) {
                                externalLaunchPendingReturn = true
                            } else {
                                Toast.makeText(
                                    androidContext,
                                    launchFailureMessage,
                                    Toast.LENGTH_SHORT,
                                ).show()
                                scope.launch { inventoryCoordinator.load(showLoading = false) }
                            }
                        }
                    }
                },
            )
        }

        if (drawerActivated) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset { IntOffset(0, drawerOffsetPx.roundToInt()) }
                    .alpha((2f * progress).coerceIn(0f, 1f))
                    .then(drawerPointerSafetyModifier)
                    .testTag("drawer_surface"),
            ) {
                DrawerScreen(
                    inventoryLoader = inventoryLoader,
                    inventoryCoordinator = inventoryCoordinator,
                    initialLoadHandledExternally = true,
                    entryLauncher = entryLauncher,
                    onExternalLaunch = { externalLaunchPendingReturn = true },
                    modifier = Modifier.nestedScroll(drawerNestedScrollConnection),
                    listState = drawerListState,
                    active = !settingsOpen &&
                        (settledSurface == AvenorSurface.Drawer || progress > 0f),
                    favoriteSelectionTarget = favoriteAddTarget?.label,
                    favoriteSelection = favoriteSelection,
                    favoriteMembership = favoriteMembership.orEmpty(),
                    favoriteSelectionSaving = favoriteSelectionSaving,
                    onToggleFavoriteSelection = { identity ->
                        if (identity !in favoriteMembership.orEmpty() &&
                            !favoriteSelectionSaving
                        ) {
                            favoriteSelection = if (identity in favoriteSelection) {
                                favoriteSelection - identity
                            } else {
                                favoriteSelection + identity
                            }
                        }
                    },
                    onCancelFavoriteSelection = ::closeFavoriteSelection,
                    onConfirmFavoriteSelection = ::confirmFavoriteSelection,
                    onLongPress = { entry ->
                        selectedEntryFromHome = false
                        selectedEntry = entry
                    },
                    onOpenSettings = {
                        selectedEntry = null
                        settingsOpen = true
                    },
                )
            }
        }

        selectedEntry?.let { entry ->
            ApplicationActionSheet(
                entry = entry,
                favoriteState = favoriteState,
                onDismiss = {
                    selectedEntry = null
                    selectedEntryFromHome = false
                },
                onAddFavorite = {
                    scope.launch { effectiveFavoriteStore.add(entry.identity) }
                    selectedEntry = null
                },
                onRemoveFavorite = {
                    scope.launch { effectiveFavoriteStore.remove(entry.identity) }
                    selectedEntry = null
                },
                onEditFavorites = {
                    editMembership = (favoriteState as? FavoriteReadState.Readable)
                        ?.identities
                        ?.toSet()
                        .orEmpty()
                    homeEditMode = true
                    selectedEntry = null
                    selectedEntryFromHome = false
                },
                canEditFavorites = selectedEntryFromHome,
                informationLauncher = informationLauncher,
                shortcuts = applicationShortcuts.takeIf {
                    shortcutOwner == entry.identity
                }.orEmpty(),
                onShortcut = { shortcut ->
                    if (shortcutController.launch(shortcut)) {
                        externalLaunchPendingReturn = true
                    }
                    selectedEntry = null
                    selectedEntryFromHome = false
                },
            )
        }

        if (settingsOpen) {
            SettingsScreen(
                platform = settingsPlatform,
                licenseText = licenseText,
                accessibilityLockController = accessibilityLockController,
                onBack = { settingsOpen = false },
            )
        }
    }
}

private class InMemoryFavoriteStore : FavoriteStore {
    private val mutableState = kotlinx.coroutines.flow.MutableStateFlow<FavoriteReadState>(
        FavoriteReadState.Readable(emptyList()),
    )
    override val state: kotlinx.coroutines.flow.StateFlow<FavoriteReadState> = mutableState
    override suspend fun load() = Unit
    override suspend fun add(identity: LaunchableIdentity): Boolean {
        val current = mutableState.value as FavoriteReadState.Readable
        if (identity !in current.identities) {
            mutableState.value = FavoriteReadState.Readable(
                current.aggregate.replaceVerticalList(
                    id = PRIMARY_LIST_ID,
                    identities = current.primaryIdentities + identity,
                ),
            )
        }
        return true
    }
    override suspend fun remove(identity: LaunchableIdentity): Boolean {
        val current = mutableState.value as FavoriteReadState.Readable
        mutableState.value = FavoriteReadState.Readable(
            current.aggregate.removeIdentity(identity),
        )
        return true
    }
    override suspend fun removeAll(identities: Set<LaunchableIdentity>): Boolean {
        val current = mutableState.value as FavoriteReadState.Readable
        mutableState.value = FavoriteReadState.Readable(
            current.aggregate.removeIdentities(identities),
        )
        return true
    }
    override suspend fun replaceOrder(identities: List<LaunchableIdentity>): Boolean {
        val current = mutableState.value as? FavoriteReadState.Readable ?: return false
        if (!isValidReplacement(current.primaryIdentities, identities)) return false
        mutableState.value = FavoriteReadState.Readable(
            current.aggregate.replaceVerticalList(
                id = PRIMARY_LIST_ID,
                identities = identities,
            ),
        )
        return true
    }

    override suspend fun replaceComposition(
        primaryIdentities: List<LaunchableIdentity>,
        companionIdentities: List<LaunchableIdentity>,
    ): Boolean {
        val current = mutableState.value as? FavoriteReadState.Readable ?: return false
        val replacement = primaryIdentities + companionIdentities
        val currentVerticalIdentities =
            current.aggregate.verticalLists.flatMap(FavoriteContainer::identities)
        if (!isValidReplacement(currentVerticalIdentities, replacement)) {
            return false
        }
        mutableState.value = FavoriteReadState.Readable(
            current.aggregate.replaceVerticalComposition(
                primaryIdentities,
                companionIdentities,
            ),
        )
        return true
    }

    override suspend fun replaceAggregate(aggregate: FavoriteAggregate): Boolean {
        if (!isValidAggregate(aggregate)) return false
        mutableState.value = FavoriteReadState.Readable(aggregate)
        return true
    }

    override suspend fun updateAggregate(
        transform: (FavoriteAggregate) -> FavoriteAggregate,
    ): FavoriteAggregate? {
        val current = mutableState.value as? FavoriteReadState.Readable ?: return null
        val updated = transform(current.aggregate)
        if (!isValidAggregate(updated)) return null
        mutableState.value = FavoriteReadState.Readable(updated)
        return updated
    }
}
