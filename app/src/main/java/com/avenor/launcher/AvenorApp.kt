package com.avenor.launcher

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
    AvenorApp(inventoryLoader, entryLauncher, favoriteStore, informationLauncher)
}

@Composable
internal fun AvenorApp(
    inventoryLoader: LaunchableInventoryLoader,
    entryLauncher: LaunchableEntryLauncher = LaunchableEntryLauncher { false },
    favoriteStore: FavoriteStore? = null,
    informationLauncher: ApplicationInformationLauncher = ApplicationInformationLauncher { false },
) {
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
    var inventoryEntries by remember { mutableStateOf<List<LaunchableEntry>>(emptyList()) }
    var selectedEntry by remember { mutableStateOf<LaunchableEntry?>(null) }

    LaunchedEffect(effectiveFavoriteStore) {
        effectiveFavoriteStore.load()
    }

    LaunchedEffect(inventoryLoader) {
        runCatching { inventoryLoader.load() }
            .onSuccess { inventoryEntries = it }
    }

    DisposableEffect(lifecycleOwner, inventoryLoader) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                scope.launch {
                    runCatching { inventoryLoader.load() }
                        .onSuccess { inventoryEntries = it }
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

    BackHandler(enabled = settledSurface == AvenorSurface.Drawer || progress > 0f) {
        settleTo(AvenorSurface.Home)
    }

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
                .then(gestureModifier)
                .testTag("home_surface"),
        ) {
            HomeScreen(
                favoriteState = favoriteState,
                inventoryEntries = inventoryEntries,
                onRetryFavorites = { scope.launch { effectiveFavoriteStore.load() } },
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
                    entryLauncher = entryLauncher,
                    modifier = Modifier.nestedScroll(drawerNestedScrollConnection),
                    listState = drawerListState,
                    active = settledSurface == AvenorSurface.Drawer || progress > 0f,
                    marqueePaused = progress > 0f && progress < 1f,
                    onInventoryLoaded = { inventoryEntries = it },
                    onLongPress = { entry -> selectedEntry = entry },
                )
            }
        }

        selectedEntry?.let { entry ->
            ApplicationActionSheet(
                entry = entry,
                favoriteState = favoriteState,
                onDismiss = { selectedEntry = null },
                onAddFavorite = {
                    scope.launch { effectiveFavoriteStore.add(entry.identity) }
                    selectedEntry = null
                },
                informationLauncher = informationLauncher,
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
}
