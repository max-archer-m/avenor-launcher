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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlin.math.roundToInt

internal enum class AvenorSurface {
    Home,
    Drawer,
}

private const val DRAWER_MOVEMENT_PER_GESTURE_DP = 1.5f
private const val TARGET_FLING_THRESHOLD_DP_PER_SECOND = 1_000f

@Composable
internal fun AvenorApp() {
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
    AvenorApp(
        inventoryLoader = inventoryLoader,
        entryLauncher = entryLauncher,
        favoriteStore = favoriteStore,
        informationLauncher = informationLauncher,
        shortcutController = shortcutController,
        settingsPlatform = settingsPlatform,
        licenseText = licenseText,
    )
}

@Composable
internal fun AvenorApp(
    inventoryLoader: LaunchableInventoryLoader,
    entryLauncher: LaunchableEntryLauncher = LaunchableEntryLauncher { false },
    favoriteStore: FavoriteStore? = null,
    informationLauncher: ApplicationInformationLauncher = ApplicationInformationLauncher { false },
    shortcutController: ApplicationShortcutController = EmptyApplicationShortcutController,
    settingsPlatform: SettingsPlatform = EmptySettingsPlatform,
    licenseText: String = "",
) {
    val androidContext = LocalContext.current
    val density = LocalDensity.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
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
    var settingsOpen by remember { mutableStateOf(false) }
    var shortcutOwner by remember { mutableStateOf<LaunchableIdentity?>(null) }
    var applicationShortcuts by remember { mutableStateOf(emptyList<ApplicationShortcut>()) }
    var editMembership by remember { mutableStateOf<Set<LaunchableIdentity>>(emptySet()) }
    val homeActivationGuard = remember { RapidActivationGuard() }
    val unavailableFavoriteMessage = stringResource(R.string.favorite_application_unavailable)
    val launchFailureMessage = stringResource(R.string.application_unable_to_open)
    val favoritesChangedMessage = stringResource(R.string.favorites_changed_edit_ended)
    val inventoryFailureMessage = stringResource(R.string.inventory_update_failed_edit_ended)

    LaunchedEffect(effectiveFavoriteStore) {
        effectiveFavoriteStore.load()
    }

    LaunchedEffect(inventoryCoordinator) {
        inventoryCoordinator.load(showLoading = true)
    }

    DisposableEffect(inventoryCoordinator) {
        val cacheInvalidationObservation = inventoryCoordinator.observe { }
        onDispose { cacheInvalidationObservation?.stop() }
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
        if (favoriteState !is FavoriteReadState.Readable) selectedEntry = null
        if (homeEditMode) {
            val readable = favoriteState as? FavoriteReadState.Readable
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

    LaunchedEffect(favoriteState, inventoryState, effectiveFavoriteStore, inventoryCoordinator) {
        val readableFavorites = favoriteState as? FavoriteReadState.Readable
            ?: return@LaunchedEffect
        val completeInventory = inventoryState as? LaunchableInventoryState.Content
            ?: return@LaunchedEffect
        val resolved = inventoryCoordinator.resolveFavorites(
            identities = readableFavorites.identities,
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

    DisposableEffect(lifecycleOwner, inventoryLoader) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (inventoryCoordinator.state.value is LaunchableInventoryState.Content) {
                    scope.launch { inventoryCoordinator.load(showLoading = false) }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    fun settleTo(target: AvenorSurface) {
        drawerTransitionOwnsGesture = false
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
        val gestureProgress =
            (gestureDisplacementPx / fullGestureDistancePx).coerceIn(0f, 1f)
        progress = if (origin == AvenorSurface.Home) gestureProgress else 1f - gestureProgress
        drawerOffsetPx = if (origin == AvenorSurface.Home) {
            (drawerOffsetPx - (consumedTargetDisplacement * DRAWER_MOVEMENT_PER_GESTURE_DP))
                .coerceIn(0f, containerHeightPx)
        } else {
            (drawerOffsetPx + (consumedTargetDisplacement * DRAWER_MOVEMENT_PER_GESTURE_DP))
                .coerceIn(0f, containerHeightPx)
        }
    }

    fun finishGesture(origin: AvenorSurface, targetVelocity: Float) {
        val completedByDistance = gestureDisplacementPx >= completionThresholdPx
        val completedByFling = targetVelocity >= targetFlingThresholdPx
        val target = if (completedByDistance || completedByFling) {
            if (origin == AvenorSurface.Home) AvenorSurface.Drawer else AvenorSurface.Home
        } else {
            origin
        }
        settleTo(target)
    }

    BackHandler(enabled = homeEditMode) {
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

    val drawerNestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (source != NestedScrollSource.UserInput) return Offset.Zero
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
                if (source != NestedScrollSource.UserInput || available.y <= 0f) {
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
                if (!drawerTransitionOwnsGesture) return Velocity.Zero
                finishGesture(AvenorSurface.Drawer, available.y)
                return Velocity(x = 0f, y = available.y)
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                if (!drawerTransitionOwnsGesture) return Velocity.Zero
                finishGesture(AvenorSurface.Drawer, available.y)
                return Velocity(x = 0f, y = available.y)
            }
        }
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
                .then(if (homeEditMode) Modifier else gestureModifier)
                .testTag("home_surface"),
        ) {
            HomeScreen(
                favoriteState = favoriteState,
                favoriteAvailability = favoriteAvailability,
                marqueePaused = progress > 0f || selectedEntry != null,
                editMode = homeEditMode,
                onRetryFavorites = { scope.launch { effectiveFavoriteStore.load() } },
                onLongPressFavorite = { entry ->
                    selectedEntryFromHome = true
                    selectedEntry = entry
                },
                onReorderFavorites = { identities ->
                    scope.launch {
                        if (!effectiveFavoriteStore.replaceOrder(identities)) {
                            Toast.makeText(
                                androidContext,
                                R.string.favorite_reorder_unavailable,
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
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

                        homeActivationGuard.tryAcquire() &&
                            !entryLauncher.launch(availability.entry) -> {
                            Toast.makeText(
                                androidContext,
                                launchFailureMessage,
                                Toast.LENGTH_SHORT,
                            ).show()
                            scope.launch { inventoryCoordinator.load(showLoading = false) }
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
                    modifier = Modifier.nestedScroll(drawerNestedScrollConnection),
                    listState = drawerListState,
                    active = !settingsOpen &&
                        (settledSurface == AvenorSurface.Drawer || progress > 0f),
                    marqueePaused = settingsOpen ||
                        progress > 0f && progress < 1f || selectedEntry != null,
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
                    shortcutController.launch(shortcut)
                    selectedEntry = null
                    selectedEntryFromHome = false
                },
            )
        }

        if (settingsOpen) {
            SettingsScreen(
                platform = settingsPlatform,
                licenseText = licenseText,
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
            mutableState.value = FavoriteReadState.Readable(current.identities + identity)
        }
        return true
    }
    override suspend fun remove(identity: LaunchableIdentity): Boolean {
        val current = mutableState.value as FavoriteReadState.Readable
        mutableState.value = FavoriteReadState.Readable(current.identities - identity)
        return true
    }
    override suspend fun removeAll(identities: Set<LaunchableIdentity>): Boolean {
        val current = mutableState.value as FavoriteReadState.Readable
        mutableState.value = FavoriteReadState.Readable(
            current.identities.filterNot(identities::contains),
        )
        return true
    }
    override suspend fun replaceOrder(identities: List<LaunchableIdentity>): Boolean {
        val current = mutableState.value as? FavoriteReadState.Readable ?: return false
        if (!isValidReplacement(current.identities, identities)) return false
        mutableState.value = FavoriteReadState.Readable(identities)
        return true
    }
}
