package com.avenor.launcher

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChangeIgnoreConsumed
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.graphics.drawable.toBitmap
import java.time.ZonedDateTime
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


@Composable
internal fun HomeScreen(
    clock: () -> ZonedDateTime = { ZonedDateTime.now() },
    favoriteState: FavoriteReadState = FavoriteReadState.Readable(emptyList()),
    favoriteAvailability: Map<LaunchableIdentity, FavoriteAvailability> = emptyMap(),
    favoriteListState: LazyListState = rememberLazyListState(),
    favoriteNestedScrollConnection: NestedScrollConnection? = null,
    companionFavoriteListState: LazyListState = rememberLazyListState(),
    companionFavoriteNestedScrollConnection: NestedScrollConnection? = null,
    editMode: Boolean = false,
    onRetryFavorites: () -> Unit = {},
    onRequestEditMode: () -> Unit = {},
    onLaunchFavorite: (FavoriteAvailability) -> Unit = {},
    onLongPressFavorite: (LaunchableEntry) -> Unit = {},
    onAddFavoritesToList: (String) -> Unit = {},
    onAddProvisionalFavorites: () -> Unit = {},
    onAddFavoritesToBar: (String) -> Unit = {},
    onAddProvisionalFavoriteBar: () -> Unit = {},
    favoriteRevealContainerId: String? = null,
    favoriteRevealContainerType: FavoriteContainerType? = null,
    favoriteRevealIdentity: LaunchableIdentity? = null,
    onFavoriteRevealComplete: () -> Unit = {},
    onCommitFavoriteComposition: suspend (
        transform: (FavoriteAggregate) -> FavoriteAggregate,
    ) -> FavoriteAggregate? = { transform -> transform(FavoriteAggregate()) },
    accessibilityLockController: AccessibilityLockController = EmptyAccessibilityLockController,
) {
    val context = LocalContext.current
    var now by remember { mutableStateOf(clock()) }
    var dragSession by remember { mutableStateOf<FavoriteDragSession?>(null) }
    var favoriteBarDragSession by remember {
        mutableStateOf<FavoriteBarDragSession?>(null)
    }
    var favoriteBarContainerDragSession by remember {
        mutableStateOf<FavoriteBarContainerDragSession?>(null)
    }
    var favoriteBarContainerCommittedGeneration by remember { mutableIntStateOf(-1) }
    var listDragSession by remember { mutableStateOf<FavoriteListDragSession?>(null) }
    var listDragCommittedGeneration by remember { mutableIntStateOf(-1) }
    val editListStates = remember { mutableMapOf<String, LazyListState>() }
    val favoriteBarStates = remember { mutableStateMapOf<String, LazyListState>() }
    val favoriteBarBoundsInWindow = remember { mutableStateMapOf<String, Rect>() }
    val favoriteBarItemWidthPx = with(LocalDensity.current) {
        dimensionResource(R.dimen.home_favorite_bar_item_width).toPx()
    }
    val favoriteBarItemStridePx = with(LocalDensity.current) {
        favoriteBarItemWidthPx + dimensionResource(
            R.dimen.home_favorite_bar_item_spacing,
        ).toPx()
    }
    LaunchedEffect(
        favoriteRevealContainerId,
        favoriteRevealContainerType,
        favoriteRevealIdentity,
        favoriteState,
        editMode,
    ) {
        val containerId = favoriteRevealContainerId ?: return@LaunchedEffect
        val identity = favoriteRevealIdentity ?: return@LaunchedEffect
        if (!editMode || favoriteState !is FavoriteReadState.Readable) {
            return@LaunchedEffect
        }
        val aggregate = favoriteState.aggregate
        val containerType = favoriteRevealContainerType
            ?: FavoriteContainerType.VerticalList
        val containers = when (containerType) {
            FavoriteContainerType.VerticalList -> aggregate.verticalLists
            FavoriteContainerType.FavoriteBar -> aggregate.favoriteBars
        }
        val containerIndex = containers.indexOfFirst { it.id == containerId }
        val container = containers.getOrNull(containerIndex)
        val itemIndex = container?.identities?.indexOf(identity) ?: -1
        if (container == null || itemIndex < 0) {
            onFavoriteRevealComplete()
            return@LaunchedEffect
        }
        val listState = when (containerType) {
            FavoriteContainerType.VerticalList -> when (containerIndex) {
                0 -> editListStates.getOrPut(container.id) { favoriteListState }
                1 -> editListStates.getOrPut(container.id) { companionFavoriteListState }
                else -> null
            }
            FavoriteContainerType.FavoriteBar -> favoriteBarStates.getOrPut(container.id) {
                LazyListState()
            }
        }
        if (listState == null) {
            onFavoriteRevealComplete()
            return@LaunchedEffect
        }
        withFrameNanos { }
        val visibleItems = listState.layoutInfo.visibleItemsInfo
        val target = visibleItems.firstOrNull {
            it.index == itemIndex
        }
        if (target != null) {
            val viewportStart = listState.layoutInfo.viewportStartOffset
            val viewportEnd = listState.layoutInfo.viewportEndOffset
            val targetEnd = target.offset + target.size
            when {
                target.offset < viewportStart -> {
                    listState.scrollBy((target.offset - viewportStart).toFloat())
                }
                targetEnd > viewportEnd -> {
                    listState.scrollBy((targetEnd - viewportEnd).toFloat())
                }
            }
        } else if (visibleItems.isNotEmpty() &&
            containerType == FavoriteContainerType.FavoriteBar
        ) {
            val viewportStart = listState.layoutInfo.viewportStartOffset
            val viewportEnd = listState.layoutInfo.viewportEndOffset
            val firstVisible = visibleItems.first()
            val targetStart = firstVisible.offset +
                ((itemIndex - firstVisible.index) * favoriteBarItemStridePx)
            val targetEnd = targetStart + favoriteBarItemWidthPx
            when {
                targetStart < viewportStart -> {
                    listState.scrollBy(targetStart - viewportStart)
                }
                targetEnd > viewportEnd -> {
                    listState.scrollBy(targetEnd - viewportEnd)
                }
            }
        } else if (visibleItems.isNotEmpty()) {
            val firstVisibleIndex = visibleItems.first().index
            val lastVisibleIndex = visibleItems.last().index
            if (itemIndex < firstVisibleIndex) {
                listState.scrollToItem(itemIndex)
            } else if (itemIndex > lastVisibleIndex) {
                listState.scrollToItem(
                    (itemIndex - visibleItems.size + 1).coerceAtLeast(0),
                )
            }
        }
        onFavoriteRevealComplete()
    }
    var dragGeneration by remember { mutableIntStateOf(0) }
    var undoAggregate by remember { mutableStateOf<FavoriteAggregate?>(null) }
    var editSessionId by remember { mutableIntStateOf(0) }
    var undoSequence by remember { mutableIntStateOf(0) }
    var pendingEditAggregate by remember { mutableStateOf<FavoriteAggregate?>(null) }
    var committedEditAggregate by remember { mutableStateOf<FavoriteAggregate?>(null) }
    var editMutationJob by remember { mutableStateOf<Job?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val editScope = rememberCoroutineScope()
    var dragRootOriginInWindow by remember { mutableStateOf(Offset.Zero) }
    var primaryListBoundsInWindow by remember { mutableStateOf(Rect.Zero) }
    var companionListBoundsInWindow by remember { mutableStateOf(Rect.Zero) }
    var primaryContainerBoundsInWindow by remember { mutableStateOf(Rect.Zero) }
    var companionContainerBoundsInWindow by remember { mutableStateOf(Rect.Zero) }
    // Band at a row's top and bottom edge that a cross-group drag reads as an insertion boundary
    // instead of the favorite's body, because adjacent rows leave no gap between them.
    val insertionBoundaryBandPx = with(LocalDensity.current) {
        dimensionResource(R.dimen.home_favorite_insertion_boundary_band).toPx()
    }
    // Band at a group's leading and trailing edge where an active drag scrolls that group, so
    // favorites outside the viewport stay reachable without releasing the drag.
    val edgeScrollBandPx = with(LocalDensity.current) {
        dimensionResource(R.dimen.home_favorite_edge_scroll_band).toPx()
    }
    val edgeScrollSpeedPxPerSecond = with(LocalDensity.current) {
        integerResource(R.integer.home_favorite_edge_scroll_dp_per_second).dp.toPx()
    }
    val hapticFeedback = LocalHapticFeedback.current
    val undoLabel = stringResource(R.string.undo)
    val favoriteListSizeMessage = stringResource(R.string.favorite_list_size)
    val favoriteListRemovedMessage = stringResource(R.string.favorite_list_removed)
    val favoriteBarRemovedMessage = stringResource(R.string.favorite_bar_removed)
    val favoriteRemovedMessage = stringResource(R.string.favorite_removed)
    val undoUnavailableMessage = stringResource(R.string.favorite_undo_unavailable)
    val advanceDrag: (Offset) -> Unit = { amount ->
        val previous = dragSession
        val advanced = previous?.advanced(
            amount = amount,
            primaryBoundsInWindow = primaryListBoundsInWindow,
            primaryListState = favoriteListState,
            companionBoundsInWindow = companionListBoundsInWindow,
            companionListState = companionFavoriteListState,
            boundaryBandPx = insertionBoundaryBandPx,
        )
        dragSession = advanced
        // A tick marks the moment the feedback changes, so an exchange or a moved insertion
        // boundary is felt without watching the moving rows.
        if (advanced != null && previous != null && advanced.feedbackChangedFrom(previous)) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
        }
    }
    val edgeScroll = dragSession?.edgeScroll(
        primaryBoundsInWindow = primaryListBoundsInWindow,
        primaryListState = favoriteListState,
        companionBoundsInWindow = companionListBoundsInWindow,
        companionListState = companionFavoriteListState,
        bandPx = edgeScrollBandPx,
    )

    LaunchedEffect(clock) {
        while (true) {
            val remainingMillis = 60_000L - (System.currentTimeMillis() % 60_000L)
            delay(remainingMillis)
            now = clock()
        }
    }

    LaunchedEffect(editMode) {
        if (!editMode) {
            editSessionId += 1
            dragSession = null
            favoriteBarDragSession = null
            favoriteBarContainerDragSession = null
            listDragSession = null
            undoAggregate = null
            pendingEditAggregate = null
            committedEditAggregate = null
            editMutationJob?.cancel()
            snackbarHostState.currentSnackbarData?.dismiss()
        } else {
            editSessionId += 1
        }
    }

    LaunchedEffect(favoriteState, editMode) {
        val readable = favoriteState as? FavoriteReadState.Readable
            ?: return@LaunchedEffect
        val committed = committedEditAggregate ?: return@LaunchedEffect
        if (editMode &&
            readable.aggregate != committed &&
            editMutationJob?.isActive != true
        ) {
            if (undoAggregate != null) {
                undoSequence += 1
                undoAggregate = null
                snackbarHostState.currentSnackbarData?.dismiss()
            }
            pendingEditAggregate = null
            committedEditAggregate = readable.aggregate
        }
    }

    fun commitEditAggregate(
        transform: (FavoriteAggregate) -> FavoriteAggregate,
        message: String = "",
        recordUndo: Boolean = false,
        onCommitted: () -> Unit = {},
        onFailed: () -> Unit = {},
) {
        if (favoriteState !is FavoriteReadState.Readable) return
        val previousJob = editMutationJob
        val session = editSessionId
        editMutationJob = editScope.launch {
            previousJob?.join()
            val base = pendingEditAggregate
                ?: committedEditAggregate
                ?: (favoriteState as? FavoriteReadState.Readable)?.aggregate
                ?: return@launch
            val updated = transform(base)
            if (!isValidAggregate(updated)) return@launch
            pendingEditAggregate = updated
            val persisted = onCommitFavoriteComposition(transform)
            if (session != editSessionId || !editMode) return@launch
            if (persisted == null) {
                if (pendingEditAggregate == updated) pendingEditAggregate = null
                onFailed()
                return@launch
            }
            pendingEditAggregate = persisted
            committedEditAggregate = persisted
            if (pendingEditAggregate == persisted) pendingEditAggregate = null
            onCommitted()
            if (!recordUndo) {
                undoSequence += 1
                undoAggregate = null
                snackbarHostState.currentSnackbarData?.dismiss()
                return@launch
            }
            undoSequence += 1
            val sequence = undoSequence
            undoAggregate = base
            snackbarHostState.currentSnackbarData?.dismiss()
            editScope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = undoLabel,
                    duration = SnackbarDuration.Long,
                )
                if (result == SnackbarResult.ActionPerformed &&
                    sequence == undoSequence &&
                    session == editSessionId
                ) {
                    val snapshot = undoAggregate ?: return@launch
                    undoAggregate = null
                    undoSequence += 1
                    commitEditAggregate(
                        transform = { snapshot },
                        onFailed = {
                            editScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = undoUnavailableMessage,
                                    duration = SnackbarDuration.Short,
                                )
                            }
                        },
                    )
                }
            }
        }
    }

    fun removeFavoriteFromContainer(
        containerId: String,
        identity: LaunchableIdentity,
    ) {
        commitEditAggregate(
            { aggregate ->
                aggregate.copy(
                    verticalLists = aggregate.verticalLists.mapNotNull { container ->
                        if (container.id != containerId) {
                            container
                        } else {
                            container.copy(
                                identities = container.identities - identity,
                            ).takeIf { it.identities.isNotEmpty() }
                        }
                    },
                    favoriteBars = aggregate.favoriteBars.mapNotNull { container ->
                        if (container.id != containerId) {
                            container
                        } else {
                            container.copy(
                                identities = container.identities - identity,
                            ).takeIf { it.identities.isNotEmpty() }
                        }
                    },
                )
            },
            favoriteRemovedMessage,
            recordUndo = true,
        )
    }

    fun removeFavoriteBar(containerId: String) {
        commitEditAggregate(
            transform = { aggregate ->
                aggregate.copy(
                    favoriteBars = aggregate.favoriteBars.filterNot { it.id == containerId },
                )
            },
            message = favoriteBarRemovedMessage,
            recordUndo = true,
        )
    }

    fun startFavoriteBarContainerDrag(
        bar: FavoriteContainer,
        index: Int,
        touchInWindow: Offset,
        displayedBars: List<FavoriteContainer>,
    ) {
        val bounds = favoriteBarBoundsInWindow[bar.id] ?: return
        val state = favoriteBarStates[bar.id]
        favoriteBarContainerDragSession = FavoriteBarContainerDragSession(
            sourceContainer = bar,
            currentIndex = index,
            originInWindow = bounds.topLeft,
            size = IntSize(bounds.width.roundToInt(), bounds.height.roundToInt()),
            touchStartInWindow = touchInWindow,
            displayedBars = displayedBars,
            initialDisplayedBars = displayedBars,
            visibleIdentities = state?.layoutInfo?.visibleItemsInfo
                ?.mapNotNull { bar.identities.getOrNull(it.index) }
                .orEmpty(),
            visibleScrollOffset = state?.firstVisibleItemScrollOffset ?: 0,
            canScrollBackward = state?.canScrollBackward == true,
            canScrollForward = state?.canScrollForward == true,
        )
        favoriteBarContainerCommittedGeneration = -1
        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
    }

    fun advanceFavoriteBarContainerDrag(amount: Offset) {
        val previous = favoriteBarContainerDragSession ?: return
        val moved = previous.copy(delta = previous.delta + amount)
        val targetIndex = moved.displayedBars.indexOfFirst { bar ->
            favoriteBarBoundsInWindow[bar.id]?.contains(moved.touchInWindow) == true
        }
        if (targetIndex < 0 || targetIndex == moved.currentIndex) {
            favoriteBarContainerDragSession = moved.copy(targetContainerId = null)
            return
        }
        val targetContainerId = moved.displayedBars[targetIndex].id
        val reordered = moved.displayedBars.toMutableList().also { bars ->
            val source = bars[moved.currentIndex]
            bars[moved.currentIndex] = bars[targetIndex]
            bars[targetIndex] = source
        }
        val advanced = moved.copy(
            currentIndex = targetIndex,
            displayedBars = reordered,
            targetContainerId = targetContainerId,
            exchangeGeneration = moved.exchangeGeneration + 1,
        )
        favoriteBarContainerDragSession = advanced
        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
        val sourceId = advanced.sourceContainer.id
        val generation = advanced.exchangeGeneration
        val committedBars = advanced.displayedBars
        commitEditAggregate(
            transform = { aggregate ->
                val currentIndex = aggregate.favoriteBars.indexOfFirst { it.id == sourceId }
                if (currentIndex < 0 || targetIndex !in aggregate.favoriteBars.indices) {
                    aggregate
                } else {
                    aggregate.copy(
                        favoriteBars = aggregate.favoriteBars.toMutableList().also { bars ->
                            val source = bars.removeAt(currentIndex)
                            bars.add(targetIndex, source)
                        },
                    )
                }
            },
            onCommitted = {
                val active = favoriteBarContainerDragSession
                if (active?.sourceContainer?.id == sourceId &&
                    active.exchangeGeneration == generation &&
                    active.displayedBars == committedBars
                ) {
                    favoriteBarContainerCommittedGeneration = generation
                    if (active.released) favoriteBarContainerDragSession = null
                }
            },
            onFailed = {
                val active = favoriteBarContainerDragSession
                if (active?.sourceContainer?.id == sourceId &&
                    active.exchangeGeneration == generation
                ) {
                    favoriteBarContainerDragSession = null
                    favoriteBarContainerCommittedGeneration = -1
                }
            },
        )
    }

    fun finishFavoriteBarContainerDrag() {
        val session = favoriteBarContainerDragSession ?: return
        if (session.displayedBars == session.initialDisplayedBars) {
            favoriteBarContainerDragSession = null
            return
        }
        val released = session.copy(released = true)
        favoriteBarContainerDragSession =
            if (favoriteBarContainerCommittedGeneration == released.exchangeGeneration) {
                null
            } else {
                released
            }
    }

    fun advanceAndPersistFavoriteBarDrag(amount: Offset) {
        val session = favoriteBarDragSession ?: return
        var residualX = session.residualX + amount.x
        var displayed = session.displayedIdentities
        var sourceIndex = displayed.indexOf(session.identity)
        if (sourceIndex < 0) return
        var exchanged = false
        val threshold = favoriteBarItemStridePx / 2f
        while (residualX >= threshold && sourceIndex < displayed.lastIndex) {
            displayed = displayed.exchangedAt(sourceIndex, sourceIndex + 1)
            sourceIndex += 1
            residualX -= favoriteBarItemStridePx
            exchanged = true
        }
        while (residualX <= -threshold && sourceIndex > 0) {
            displayed = displayed.exchangedAt(sourceIndex, sourceIndex - 1)
            sourceIndex -= 1
            residualX += favoriteBarItemStridePx
            exchanged = true
        }
        favoriteBarDragSession = session.copy(
            displayedIdentities = displayed,
            delta = session.delta + amount,
            residualX = residualX,
        )
        if (!exchanged) return
        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
        val generation = session.generation
        commitEditAggregate(
            transform = { aggregate ->
                aggregate.copy(
                    favoriteBars = aggregate.favoriteBars.map { bar ->
                        if (bar.id == session.barId) {
                            bar.copy(identities = displayed)
                        } else {
                            bar
                        }
                    },
                )
            },
            onFailed = {
                if (favoriteBarDragSession?.generation == generation) {
                    favoriteBarDragSession = null
                }
            },
        )
    }
    fun advanceAndPersistDrag(amount: Offset) {
        val previous = dragSession
        advanceDrag(amount)
        val advanced = dragSession
        if (previous == null || advanced == null) return
        val sameContainerOrderChanged =
            previous.displayedPrimary != advanced.displayedPrimary ||
                previous.displayedCompanion != advanced.displayedCompanion
        if (!sameContainerOrderChanged) return

        val generation = advanced.generation
        val visiblePrimary = advanced.displayedPrimary
        val visibleCompanion = advanced.displayedCompanion
        commitEditAggregate(
            transform = { aggregate ->
                aggregate.replaceVerticalComposition(
                    visiblePrimary,
                    visibleCompanion,
                )
            },
            onCommitted = {
                val active = dragSession
                if (active?.generation == generation &&
                    active.released &&
                    active.displayedPrimary == visiblePrimary &&
                    active.displayedCompanion == visibleCompanion
                ) {
                    dragSession = null
                }
            },
            onFailed = {
                if (dragSession?.generation == generation) {
                    dragSession = null
                }
            },
        )
    }

    fun startListDrag(
        container: FavoriteContainer,
        index: Int,
        bounds: Rect,
        touchInWindow: Offset,
        listState: LazyListState,
        displayedLists: List<FavoriteContainer>,
    ) {
        val visibleIdentities = listState.layoutInfo.visibleItemsInfo.mapNotNull { item ->
            container.identities.getOrNull(item.index)
        }
        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
        listDragSession = FavoriteListDragSession(
            sourceContainer = container,
            currentIndex = index,
            originInWindow = bounds.topLeft,
            size = IntSize(bounds.width.roundToInt(), bounds.height.roundToInt()),
            touchStartInWindow = touchInWindow,
            displayedLists = displayedLists,
            initialDisplayedLists = displayedLists,
            visibleIdentities = visibleIdentities,
            visibleScrollOffset = listState.firstVisibleItemScrollOffset,
        )
        listDragCommittedGeneration = -1
    }

    fun advanceListDrag(amount: Offset) {
        val previous = listDragSession ?: return
        val sourceState = editListStates[previous.sourceContainer.id]
        val moved = previous.copy(
            delta = previous.delta + amount,
            visibleIdentities = sourceState?.layoutInfo?.visibleItemsInfo
                ?.mapNotNull { item ->
                    previous.sourceContainer.identities.getOrNull(item.index)
                }
                ?: previous.visibleIdentities,
            visibleScrollOffset = sourceState?.firstVisibleItemScrollOffset
                ?: previous.visibleScrollOffset,
        )
        val targetIndex = when {
            primaryContainerBoundsInWindow.contains(moved.touchInWindow) -> 0
            companionContainerBoundsInWindow.contains(moved.touchInWindow) -> 1
            else -> {
                listDragSession = moved
                return
            }
        }
        if (targetIndex == moved.currentIndex ||
            targetIndex !in moved.displayedLists.indices
        ) {
            listDragSession = moved
            return
        }

        val reordered = moved.displayedLists.toMutableList().also { lists ->
            val source = lists[moved.currentIndex]
            lists[moved.currentIndex] = lists[targetIndex]
            lists[targetIndex] = source
        }
        val advanced = moved.copy(
            currentIndex = targetIndex,
            displayedLists = reordered,
            exchangeGeneration = moved.exchangeGeneration + 1,
        )
        listDragSession = advanced
        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
        val sourceId = advanced.sourceContainer.id
        val committedLists = advanced.displayedLists
        val exchangeGeneration = advanced.exchangeGeneration
        commitEditAggregate(
            transform = { aggregate ->
                val currentIndex = aggregate.verticalLists.indexOfFirst { it.id == sourceId }
                if (currentIndex < 0 || targetIndex !in aggregate.verticalLists.indices) {
                    aggregate
                } else {
                    aggregate.moveVerticalList(currentIndex, targetIndex)
                }
            },
            onCommitted = {
                val active = listDragSession
                if (active?.sourceContainer?.id == sourceId &&
                    active.exchangeGeneration == exchangeGeneration &&
                    active.displayedLists == committedLists
                ) {
                    listDragCommittedGeneration = exchangeGeneration
                    if (active.released) {
                        listDragSession = null
                    }
                }
            },
            onFailed = {
                if (listDragSession?.sourceContainer?.id == sourceId &&
                    listDragSession?.exchangeGeneration == exchangeGeneration
                ) {
                    listDragSession = null
                    listDragCommittedGeneration = -1
                }
            },
        )
    }

    fun finishListDrag() {
        val session = listDragSession ?: return
        if (session.displayedLists == session.initialDisplayedLists) {
            listDragSession = null
            return
        }
        val released = session.copy(released = true)
        listDragSession = if (
            listDragCommittedGeneration == released.exchangeGeneration
        ) {
            null
        } else {
            released
        }
    }

    // Edge scrolling moves the group under the finger while the touch point itself stays put, so
    // the target is resolved again for the group's new geometry after every scrolled frame.
    LaunchedEffect(edgeScroll) {
        val request = edgeScroll ?: return@LaunchedEffect
        val listState = if (request.inCompanion) {
            companionFavoriteListState
        } else {
            favoriteListState
        }
        var previousFrameNanos = withFrameNanos { it }
        while (true) {
            val frameNanos = withFrameNanos { it }
            val elapsedSeconds = (frameNanos - previousFrameNanos) / NANOS_PER_SECOND
            previousFrameNanos = frameNanos
            val canScroll = if (request.forward) {
                listState.canScrollForward
            } else {
                listState.canScrollBackward
            }
            if (!canScroll) break
            val distance = edgeScrollSpeedPxPerSecond * elapsedSeconds
            listState.scrollBy(if (request.forward) distance else -distance)
            advanceAndPersistDrag(Offset.Zero)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(dimensionResource(R.dimen.home_content_padding))
            .onGloballyPositioned { dragRootOriginInWindow = it.positionInWindow() },
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .editSurface(editMode),
            ) {
                Column(
                    modifier = Modifier.padding(
                        start = dimensionResource(R.dimen.home_information_margin),
                        top = dimensionResource(R.dimen.home_information_margin),
                        end = dimensionResource(R.dimen.home_information_margin),
                    ),
                ) {
                    Text(
                        text = HomeDateTimeFormatter.time(context, now),
                        modifier = Modifier
                            .heightIn(min = dimensionResource(R.dimen.home_time_min_height))
                            .testTag("home_time")
                            .clickable(enabled = !editMode, role = Role.Button) {
                                context.launchClockDestination()
                            },
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = dimensionResource(R.dimen.home_time_text_size).value.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = dimensionResource(R.dimen.home_time_line_height).value.sp,
                        textAlign = TextAlign.Start,
                    )
                    Text(
                        text = HomeDateTimeFormatter.dateAndWeekday(context, now),
                        modifier = Modifier
                            .heightIn(min = dimensionResource(R.dimen.home_date_height))
                            .testTag("home_date")
                            .clickable(enabled = !editMode, role = Role.Button) {
                                val calendarUri = CalendarContract.CONTENT_URI
                                    .buildUpon()
                                    .appendPath("time")
                                    .appendPath(now.toInstant().toEpochMilli().toString())
                                    .build()
                                context.launchPlatformDestination(
                                    intent = Intent(Intent.ACTION_VIEW, calendarUri),
                                    failureMessage = R.string.calendar_unavailable,
                                )
                            }
                            // The inset only shifts the visible text; the clickable stays outside
                            // it so the whole date row remains the focusable touch target.
                            .padding(
                                start = dimensionResource(R.dimen.home_date_text_start_inset),
                            )
                            .wrapContentHeight(align = Alignment.CenterVertically),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = dimensionResource(R.dimen.home_date_text_size).value.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = dimensionResource(R.dimen.home_date_line_height).value.sp,
                        textAlign = TextAlign.Start,
                    )
                }
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .pointerInput(accessibilityLockController, editMode) {
                            detectTapGestures(
                                onDoubleTap = {
                                    if (!accessibilityLockController.availableForValidation ||
                                        editMode ||
                                        !accessibilityLockController.isSystemEnabled()
                                    ) {
                                        return@detectTapGestures
                                    }
                                    if (accessibilityLockController.requestLock() !=
                                        LockRequestResult.Requested
                                    ) {
                                        Toast.makeText(
                                            context,
                                            R.string.unable_to_lock_screen,
                                            Toast.LENGTH_SHORT,
                                        ).show()
                                    }
                                },
                                onLongPress = {
                                    if (!editMode) {
                                        hapticFeedback.performHapticFeedback(
                                            HapticFeedbackType.LongPress,
                                        )
                                        onRequestEditMode()
                                    }
                                },
                            )
                        }
                        .testTag("home_double_tap_lock_region"),
                )
            }
            Spacer(Modifier.height(dimensionResource(R.dimen.home_module_spacing)))
            Box(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .fillMaxWidth(),
            ) {
                when (favoriteState) {
                FavoriteReadState.Loading -> HomeFavoriteMessage(
                    message = stringResource(R.string.loading_favorites),
                    showProgress = true,
                    onRetry = null,
                )

                FavoriteReadState.ReadFailure -> HomeFavoriteMessage(
                    message = stringResource(R.string.unable_to_load_favorites),
                    showProgress = false,
                    onRetry = onRetryFavorites,
                )

                    is FavoriteReadState.Readable -> {
                    if (favoriteState.aggregate.verticalLists.isEmpty() && editMode) {
                        HomeFavoriteProvisionalList(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onAddProvisionalFavorites,
                            testTag = "favorite_provisional_add_0",
                        )
                    } else if (favoriteState.aggregate.verticalLists.isEmpty() &&
                        favoriteState.aggregate.favoriteBars.isEmpty()
                    ) {
                        Text(
                            text = stringResource(R.string.home_empty_favorites),
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.testTag("home_favorites_empty"),
                        )
                    } else if (!editMode && favoriteState.aggregate.verticalLists.isNotEmpty()) {
                        HomeFavoriteComposition(
                            verticalLists = favoriteState.aggregate.verticalLists,
                            availabilityByIdentity = favoriteAvailability,
                            favoriteListState = favoriteListState,
                            favoriteNestedScrollConnection = favoriteNestedScrollConnection,
                            companionFavoriteListState = companionFavoriteListState,
                            companionFavoriteNestedScrollConnection =
                                companionFavoriteNestedScrollConnection,
                            onLaunchFavorite = onLaunchFavorite,
                            onLongPressFavorite = onLongPressFavorite,
                        )
                    } else if (editMode) {
                        val persistedEditAggregate = pendingEditAggregate
                            ?: committedEditAggregate
                            ?: favoriteState.aggregate
                        val editAggregate = listDragSession?.let { session ->
                            persistedEditAggregate.copy(
                                verticalLists = session.displayedLists,
                            )
                        } ?: persistedEditAggregate
                        val primaryContainer = editAggregate.verticalLists.getOrNull(0)
                        val companionContainer = editAggregate.verticalLists.getOrNull(1)
                        val primaryEditListState = primaryContainer?.let { container ->
                            editListStates.getOrPut(container.id) {
                                favoriteListState
                            }
                        } ?: favoriteListState
                        val companionEditListState = companionContainer?.let { container ->
                            editListStates.getOrPut(container.id) {
                                companionFavoriteListState
                            }
                        } ?: companionFavoriteListState
                        val primaryIdentities = primaryContainer?.identities.orEmpty()
                        val companionIdentities = companionContainer?.identities.orEmpty()
                        val activeSession = dragSession?.takeIf { it.hasInGroupExchange }
                        val activeDraggedIdentity = dragSession
                            ?.takeUnless { it.released }
                            ?.identity
                        val primaryDisplayed = activeSession?.displayedPrimary
                            ?: primaryIdentities
                        val companionDisplayed = activeSession?.displayedCompanion
                            ?: companionIdentities
                        // A release completes the current exchange or insertion when the touch
                        // point is inside either group; any other area restores the saved state.
                        val endDrag: () -> Unit = {
                            val session = dragSession
                            if (session != null &&
                                (session.crossGroupTarget != null || session.hasInsertion) &&
                                (
                                    primaryListBoundsInWindow.contains(session.touchInWindow) ||
                                        companionListBoundsInWindow
                                            .contains(session.touchInWindow)
                                    )
                            ) {
                                val committed = session.committedComposition()
                                val generation = session.generation
                                dragSession = session.copy(released = true)
                                commitEditAggregate(
                                    transform = { aggregate ->
                                        aggregate.replaceVerticalComposition(
                                            committed.first,
                                            committed.second,
                                        )
                                    },
                                    onCommitted = {
                                        if (dragSession?.generation == generation) {
                                            dragSession = null
                                        }
                                    },
                                    onFailed = {
                                        if (dragSession?.generation == generation) {
                                            dragSession = null
                                        }
                                    },
                                )
                            } else if (session?.hasInGroupExchange == true) {
                                dragSession = session.copy(released = true)
                            } else {
                                dragSession = null
                            }
                        }
                        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                            val primaryContentHeight = primaryContainer?.let { container ->
                                dimensionResource(container.listSize.rowHeightResource()) *
                                    container.identities.size
                            } ?: 0.dp
                            val companionContentHeight = companionContainer?.let { container ->
                                dimensionResource(container.listSize.rowHeightResource()) *
                                    container.identities.size
                            } ?: 0.dp
                            val addControlHeight = if (editMode) {
                                dimensionResource(R.dimen.home_favorite_add_control_height)
                            } else {
                                0.dp
                            }
                            val contentHeight = (
                                maxOf(
                                    primaryContentHeight,
                                    companionContentHeight,
                                ).coerceAtLeast(addControlHeight)
                            ).coerceAtMost(maxHeight)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(contentHeight),
                                horizontalArrangement = Arrangement.spacedBy(
                                    dimensionResource(R.dimen.home_favorite_group_spacing),
                                ),
                            ) {
                                if (primaryContainer != null) {
                                    HomeFavoriteList(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight(),
                                        identities = primaryDisplayed,
                                        availabilityByIdentity = favoriteAvailability,
                                        listState = primaryEditListState,
                                        nestedScrollConnection = favoriteNestedScrollConnection,
                                        editMode = editMode,
                                        compact = false,
                                        listSize = primaryContainer.listSize,
                                        draggedIdentity = activeDraggedIdentity,
                                        exchangeTargetIdentity = dragSession?.crossGroupTarget,
                                        insertionBoundaryIndex = dragSession?.insertionBoundaryIn(
                                            companion = false,
                                        ),
                                        onBoundsInWindow = { primaryListBoundsInWindow = it },
                                        onLaunchFavorite = onLaunchFavorite,
                                        onLongPressFavorite = onLongPressFavorite,
                                        onRemoveFavorite = { identity ->
                                            removeFavoriteFromContainer(
                                                primaryContainer.id,
                                                identity,
                                            )
                                        },
                                        listIndex = 0,
                                        listCount = editAggregate.verticalLists.size,
                                        onChangeListSize = { size ->
                                            commitEditAggregate(
                                                { aggregate ->
                                                    aggregate.updateVerticalList(
                                                        primaryContainer.id,
                                                    ) {
                                                        it.copy(listSize = size)
                                                    }
                                                },
                                                favoriteListSizeMessage,
                                            )
                                        },
                                        onRemoveList = {
                                            commitEditAggregate(
                                                { aggregate ->
                                                    aggregate.updateVerticalList(
                                                        primaryContainer.id,
                                                    ) { null }
                                                },
                                                favoriteListRemovedMessage,
                                                recordUndo = true,
                                            )
                                        },
                                        onAddFavorites = {
                                            onAddFavoritesToList(primaryContainer.id)
                                        },
                                        onContainerBoundsInWindow = {
                                            primaryContainerBoundsInWindow = it
                                        },
                                        sourceListPlaceholder =
                                            listDragSession?.sourceContainer?.id ==
                                                primaryContainer.id,
                                        listExchangeHighlight = listDragSession?.let { session ->
                                            session.sourceContainer.id != primaryContainer.id &&
                                                primaryContainerBoundsInWindow
                                                    .contains(session.touchInWindow)
                                        } == true,
                                        listDragActive = listDragSession != null,
                                        onListDragStart = { touch ->
                                            startListDrag(
                                                container = primaryContainer,
                                                index = 0,
                                                bounds = primaryContainerBoundsInWindow,
                                                touchInWindow = touch,
                                                listState = primaryEditListState,
                                                displayedLists = editAggregate.verticalLists,
                                            )
                                        },
                                        onListDrag = ::advanceListDrag,
                                        onListDragEnd = ::finishListDrag,
                                        onListDragCancel = {
                                            listDragSession = null
                                        },
                                        onDragStart = { identity, origin, size, touch ->
                                            dragGeneration += 1
                                            dragSession = FavoriteDragSession(
                                                generation = dragGeneration,
                                                identity = identity,
                                                listSize = primaryContainer.listSize,
                                                originInWindow = origin,
                                                size = size,
                                                touchStartInWindow = touch,
                                                displayedPrimary =
                                                    primaryIdentities,
                                                displayedCompanion =
                                                    companionIdentities,
                                            )
                                        },
                                        onDrag = ::advanceAndPersistDrag,
                                        onDragEnd = endDrag,
                                        onDragCancel = { dragSession = null },
                                    )
                                }
                                if (companionContainer != null) {
                                    HomeFavoriteList(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight(),
                                        identities = companionDisplayed,
                                        availabilityByIdentity = favoriteAvailability,
                                        listState = companionEditListState,
                                        nestedScrollConnection =
                                            companionFavoriteNestedScrollConnection,
                                        editMode = editMode,
                                        compact = false,
                                        listSize = companionContainer.listSize,
                                        draggedIdentity = activeDraggedIdentity,
                                        exchangeTargetIdentity = dragSession?.crossGroupTarget,
                                        insertionBoundaryIndex = dragSession?.insertionBoundaryIn(
                                            companion = true,
                                        ),
                                        onBoundsInWindow = { companionListBoundsInWindow = it },
                                        onLaunchFavorite = onLaunchFavorite,
                                        onLongPressFavorite = onLongPressFavorite,
                                        onRemoveFavorite = { identity ->
                                            removeFavoriteFromContainer(
                                                companionContainer.id,
                                                identity,
                                            )
                                        },
                                        listIndex = 1,
                                        listCount = editAggregate.verticalLists.size,
                                        onChangeListSize = { size ->
                                            commitEditAggregate(
                                                { aggregate ->
                                                    aggregate.updateVerticalList(
                                                        companionContainer.id,
                                                    ) {
                                                        it.copy(listSize = size)
                                                    }
                                                },
                                                favoriteListSizeMessage,
                                            )
                                        },
                                        onRemoveList = {
                                            commitEditAggregate(
                                                { aggregate ->
                                                    aggregate.updateVerticalList(
                                                        companionContainer.id,
                                                    ) { null }
                                                },
                                                favoriteListRemovedMessage,
                                                recordUndo = true,
                                            )
                                        },
                                        onAddFavorites = {
                                            onAddFavoritesToList(companionContainer.id)
                                        },
                                        onContainerBoundsInWindow = {
                                            companionContainerBoundsInWindow = it
                                        },
                                        sourceListPlaceholder =
                                            listDragSession?.sourceContainer?.id ==
                                                companionContainer.id,
                                        listExchangeHighlight = listDragSession?.let { session ->
                                            session.sourceContainer.id != companionContainer.id &&
                                                companionContainerBoundsInWindow
                                                    .contains(session.touchInWindow)
                                        } == true,
                                        listDragActive = listDragSession != null,
                                        onListDragStart = { touch ->
                                            startListDrag(
                                                container = companionContainer,
                                                index = 1,
                                                bounds = companionContainerBoundsInWindow,
                                                touchInWindow = touch,
                                                listState = companionEditListState,
                                                displayedLists = editAggregate.verticalLists,
                                            )
                                        },
                                        onListDrag = ::advanceListDrag,
                                        onListDragEnd = ::finishListDrag,
                                        onListDragCancel = {
                                            listDragSession = null
                                        },
                                        onDragStart = { identity, origin, size, touch ->
                                            dragGeneration += 1
                                            dragSession = FavoriteDragSession(
                                                generation = dragGeneration,
                                                identity = identity,
                                                listSize = companionContainer.listSize,
                                                originInWindow = origin,
                                                size = size,
                                                touchStartInWindow = touch,
                                                displayedPrimary =
                                                    primaryIdentities,
                                                displayedCompanion =
                                                    companionIdentities,
                                            )
                                        },
                                        onDrag = ::advanceAndPersistDrag,
                                        onDragEnd = endDrag,
                                        onDragCancel = { dragSession = null },
                                    )
                                }
                                if (companionContainer == null) {
                                    HomeFavoriteProvisionalList(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight(),
                                        onClick = onAddProvisionalFavorites,
                                        testTag = "favorite_provisional_add_1",
                                    )
                                }
                            }
                        }
                    }
                    }
                }
            }
            (favoriteState as? FavoriteReadState.Readable)?.aggregate?.let { aggregate ->
                val renderedAggregate = if (editMode) {
                    pendingEditAggregate ?: committedEditAggregate ?: aggregate
                } else {
                    aggregate
                }
                val itemReorderedBars = favoriteBarDragSession?.let { session ->
                    renderedAggregate.favoriteBars.map { bar ->
                        if (bar.id == session.barId) {
                            bar.copy(identities = session.displayedIdentities)
                        } else {
                            bar
                        }
                    }
                } ?: renderedAggregate.favoriteBars
                val renderedBars = favoriteBarContainerDragSession?.displayedBars
                    ?: itemReorderedBars
                if (renderedBars.isNotEmpty() ||
                    (editMode && renderedBars.size < 5)
                ) {
                    if (aggregate.verticalLists.isNotEmpty() || editMode) {
                        Spacer(Modifier.height(dimensionResource(R.dimen.home_module_spacing)))
                    }
                    HomeFavoriteBars(
                        favoriteBars = renderedBars,
                        availabilityByIdentity = favoriteAvailability,
                        editMode = editMode,
                        onLaunchFavorite = onLaunchFavorite,
                        onLongPressFavorite = onLongPressFavorite,
                        onAddFavoritesToBar = onAddFavoritesToBar,
                        onAddProvisionalFavoriteBar = onAddProvisionalFavoriteBar,
                        favoriteBarStates = favoriteBarStates,
                        favoriteBarBoundsInWindow = favoriteBarBoundsInWindow,
                        draggedIdentity = favoriteBarDragSession?.identity,
                        draggedBarId = favoriteBarContainerDragSession?.sourceContainer?.id,
                        highlightedBarId =
                            favoriteBarContainerDragSession?.targetContainerId,
                        onRemoveFavorite = ::removeFavoriteFromContainer,
                        onRemoveFavoriteBar = ::removeFavoriteBar,
                        onBarDragStart = { bar, index, touch ->
                            startFavoriteBarContainerDrag(
                                bar = bar,
                                index = index,
                                touchInWindow = touch,
                                displayedBars = renderedBars,
                            )
                        },
                        onBarDrag = ::advanceFavoriteBarContainerDrag,
                        onBarDragEnd = ::finishFavoriteBarContainerDrag,
                        onBarDragCancel = { favoriteBarContainerDragSession = null },
                        onDragStart = { bar, identity, origin, size ->
                            dragGeneration += 1
                            favoriteBarDragSession = FavoriteBarDragSession(
                                generation = dragGeneration,
                                barId = bar.id,
                                identity = identity,
                                displayedIdentities = bar.identities,
                                originInWindow = origin,
                                size = size,
                            )
                        },
                        onDrag = ::advanceAndPersistFavoriteBarDrag,
                        onDragEnd = { favoriteBarDragSession = null },
                        onDragCancel = { favoriteBarDragSession = null },
                    )
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
        listDragSession?.let { session ->
            HomeFavoriteListDragPreview(
                session = session,
                availabilityByIdentity = favoriteAvailability,
                rootOriginInWindow = dragRootOriginInWindow,
            )
        }
        dragSession?.let { session ->
            if (!session.released) {
                HomeFavoriteDragPreview(
                    session = session,
                    availability = favoriteAvailability[session.identity]
                        ?: FavoriteAvailability.Unknown(null),
                    rootOriginInWindow = dragRootOriginInWindow,
                )
            }
        }
        favoriteBarDragSession?.let { session ->
            HomeFavoriteBarDragPreview(
                session = session,
                availability = favoriteAvailability[session.identity]
                    ?: FavoriteAvailability.Unknown(null),
                rootOriginInWindow = dragRootOriginInWindow,
            )
        }
        favoriteBarContainerDragSession?.let { session ->
            HomeFavoriteBarContainerDragPreview(
                session = session,
                availabilityByIdentity = favoriteAvailability,
                rootOriginInWindow = dragRootOriginInWindow,
            )
        }
    }
}

/**
 * State of an active favorite drag: the source geometry, the accumulated pointer delta, the touch
 * point that started the gesture, and the live visible order of both groups. In-group exchanges
 * change the visible order during the drag; a released session keeps that order visible until the
 * persistence callback completes.
 */
private data class FavoriteDragSession(
    val generation: Int,
    val identity: LaunchableIdentity,
    val listSize: FavoriteListSize,
    val originInWindow: Offset,
    val size: IntSize,
    val touchStartInWindow: Offset,
    val displayedPrimary: List<LaunchableIdentity>,
    val displayedCompanion: List<LaunchableIdentity>,
    val delta: Offset = Offset.Zero,
    val hasInGroupExchange: Boolean = false,
    val lastInGroupExchangeTouchY: Float? = null,
    val released: Boolean = false,
    val crossGroupTarget: LaunchableIdentity? = null,
    val insertion: FavoriteInsertionTarget? = null,
) {
    /** Live position of the finger, derived from the start point so a row swap cannot shift it. */
    val touchInWindow: Offset get() = touchStartInWindow + delta

    /** Group that shows the dragged favorite, which the drag keeps unchanged until the release. */
    val inCompanion: Boolean get() = identity in displayedCompanion

    /** Whether a cross-group insertion boundary is currently marked. */
    val hasInsertion: Boolean get() = insertion != null

    /** Whether a release would produce a composition that differs from the saved one. */
    val hasPendingChange: Boolean
        get() = hasInGroupExchange || crossGroupTarget != null || hasInsertion

    /** Boundary the requested group should mark with an insertion line, if any. */
    fun insertionBoundaryIn(companion: Boolean): Int? =
        insertion?.takeIf { it.intoCompanion == companion }?.boundaryIndex

    /** Returns this session with cross-group feedback dropped and visible orders untouched. */
    fun withoutCrossGroupFeedback(): FavoriteDragSession =
        if (crossGroupTarget == null && insertion == null) {
            this
        } else {
            copy(crossGroupTarget = null, insertion = null)
        }
}

private data class FavoriteBarDragSession(
    val generation: Int,
    val barId: String,
    val identity: LaunchableIdentity,
    val displayedIdentities: List<LaunchableIdentity>,
    val originInWindow: Offset,
    val size: IntSize,
    val delta: Offset = Offset.Zero,
    val residualX: Float = 0f,
)

private data class FavoriteBarContainerDragSession(
    val sourceContainer: FavoriteContainer,
    val currentIndex: Int,
    val originInWindow: Offset,
    val size: IntSize,
    val touchStartInWindow: Offset,
    val displayedBars: List<FavoriteContainer>,
    val initialDisplayedBars: List<FavoriteContainer>,
    val visibleIdentities: List<LaunchableIdentity>,
    val visibleScrollOffset: Int,
    val canScrollBackward: Boolean,
    val canScrollForward: Boolean,
    val targetContainerId: String? = null,
    val released: Boolean = false,
    val exchangeGeneration: Int = 0,
    val delta: Offset = Offset.Zero,
) {
    val touchInWindow: Offset get() = touchStartInWindow + delta
}

/**
 * Cross-group insertion boundary of an active drag. A boundary is pure feedback: it marks where a
 * release would insert the dragged favorite, and the lists are not changed before that release.
 */
private data class FavoriteInsertionTarget(
    val intoCompanion: Boolean,
    val boundaryIndex: Int,
)

/**
 * Composition a release commits, starting from the currently visible orders. A pending cross-group
 * target exchanges position and group membership with the dragged favorite, so both group counts
 * stay unchanged. A pending insertion instead removes the dragged favorite from its group and
 * inserts it at the marked boundary of the other group, which keeps the relative order of that
 * group's existing favorites.
 */
private fun FavoriteDragSession.committedComposition():
    Pair<List<LaunchableIdentity>, List<LaunchableIdentity>> {
    val sourceInCompanion = inCompanion
    val exchanged = crossGroupTarget
    if (exchanged != null) {
        val sourceOrder = displayedOrder(sourceInCompanion)
        val targetOrder = displayedOrder(!sourceInCompanion)
        val sourceSlot = sourceOrder.indexOf(identity)
        val targetSlot = targetOrder.indexOf(exchanged)
        if (sourceSlot < 0 || targetSlot < 0) return displayedPrimary to displayedCompanion
        val committedSource = sourceOrder.replacedAt(sourceSlot, exchanged)
        val committedTarget = targetOrder.replacedAt(targetSlot, identity)
        return if (sourceInCompanion) {
            committedTarget to committedSource
        } else {
            committedSource to committedTarget
        }
    }
    val target = insertion ?: return displayedPrimary to displayedCompanion
    val sourceOrder = displayedOrder(sourceInCompanion).filterNot { it == identity }
    val targetOrder = displayedOrder(target.intoCompanion).toMutableList().also {
        it.add(target.boundaryIndex.coerceIn(0, it.size), identity)
    }
    return if (target.intoCompanion) {
        sourceOrder to targetOrder
    } else {
        targetOrder to sourceOrder
    }
}
/**
 * Applies a pointer movement and resolves the target under the touch point. A favorite body in the
 * dragged favorite's own group exchanges positions immediately. In the other group, a favorite body
 * only marks a cross-group exchange and a gap only marks a cross-group insertion; both stay pure
 * feedback that a release performs. The slot is compared with the source's current visible index,
 * so the dragged favorite, the source slot, and invalid areas produce no in-group exchange.
 */
private fun FavoriteDragSession.advanced(
    amount: Offset,
    primaryBoundsInWindow: Rect,
    primaryListState: LazyListState,
    companionBoundsInWindow: Rect,
    companionListState: LazyListState,
    boundaryBandPx: Float,
): FavoriteDragSession {
    val moved = copy(delta = delta + amount)
    val targetInCompanion = when {
        primaryBoundsInWindow.contains(moved.touchInWindow) -> false
        companionBoundsInWindow.contains(moved.touchInWindow) -> true
        else -> return moved.withoutCrossGroupFeedback()
    }
    val sourceInCompanion = moved.inCompanion
    val sameGroup = targetInCompanion == sourceInCompanion
    val target = resolveGroupTarget(
        touchInWindow = moved.touchInWindow,
        listBoundsInWindow = if (targetInCompanion) {
            companionBoundsInWindow
        } else {
            primaryBoundsInWindow
        },
        listState = if (targetInCompanion) companionListState else primaryListState,
        // The source group only accepts exchanges, so its rows keep their full body.
        boundaryBandPx = if (sameGroup) 0f else boundaryBandPx,
    ) ?: return moved.withoutCrossGroupFeedback()
    val sourceOrder = moved.displayedOrder(sourceInCompanion)
    val sourceSlot = sourceOrder.indexOf(identity)
    if (sourceSlot < 0) return moved
    if (sameGroup) {
        // An in-group gap accepts neither an exchange nor an insertion.
        val slot = (target as? FavoriteDragTarget.Body)?.slot
            ?: return moved.withoutCrossGroupFeedback()
        if (slot == sourceSlot || slot !in sourceOrder.indices) {
            return moved.withoutCrossGroupFeedback()
        }
        val adjacentSlot = sourceSlot + if (slot > sourceSlot) 1 else -1
        if (adjacentSlot !in sourceOrder.indices) {
            return moved.withoutCrossGroupFeedback()
        }
        val sourceListState = if (sourceInCompanion) {
            companionListState
        } else {
            primaryListState
        }
        if (!sourceListState.hasCrossedExchangeThreshold(
                touchInWindow = moved.touchInWindow,
                listBoundsInWindow = if (sourceInCompanion) {
                    companionBoundsInWindow
                } else {
                    primaryBoundsInWindow
                },
                sourceSlot = sourceSlot,
                targetSlot = adjacentSlot,
                lastExchangeTouchY = moved.lastInGroupExchangeTouchY,
            )
        ) {
            return moved.withoutCrossGroupFeedback()
        }
        return moved
            .withDisplayedOrder(
                sourceInCompanion,
                sourceOrder.exchangedAt(sourceSlot, adjacentSlot),
            )
            .copy(
                hasInGroupExchange = true,
                lastInGroupExchangeTouchY = moved.touchInWindow.y,
                crossGroupTarget = null,
                insertion = null,
            )
    }
    val targetOrder = moved.displayedOrder(targetInCompanion)
    return when (target) {
        is FavoriteDragTarget.Boundary -> moved.copy(
            crossGroupTarget = null,
            insertion = FavoriteInsertionTarget(
                intoCompanion = targetInCompanion,
                boundaryIndex = target.index.coerceIn(0, targetOrder.size),
            ),
            lastInGroupExchangeTouchY = null,
        )

        is FavoriteDragTarget.Body -> {
            if (target.slot !in targetOrder.indices) return moved.withoutCrossGroupFeedback()
            moved.copy(
                crossGroupTarget = targetOrder[target.slot],
                insertion = null,
                lastInGroupExchangeTouchY = null,
            )
        }
    }
}

/** Nanoseconds in a second, which turns a frame interval into an edge-scroll distance. */
private const val NANOS_PER_SECOND = 1_000_000_000f

/**
 * Whether the target feedback differs from [previous]: the visible order of a group changed, or the
 * marked cross-group exchange target or insertion boundary moved. Pointer movement on its own
 * leaves
 * the feedback untouched, so this marks exactly the moments at which a release would produce a
 * different composition.
 */
private fun FavoriteDragSession.feedbackChangedFrom(previous: FavoriteDragSession): Boolean =
    displayedPrimary != previous.displayedPrimary ||
        displayedCompanion != previous.displayedCompanion ||
        crossGroupTarget != previous.crossGroupTarget ||
        insertion != previous.insertion

/**
 * Edge scroll the active drag currently asks for. While the touch point rests in a group's leading
 * or trailing band, that group scrolls, so favorites outside the viewport stay reachable without
 * releasing the drag. The band is capped at half the group so its two edges cannot overlap. Returns
 * null when the touch point is outside both groups or away from the group's edges.
 */
private fun FavoriteDragSession.edgeScroll(
    primaryBoundsInWindow: Rect,
    primaryListState: LazyListState,
    companionBoundsInWindow: Rect,
    companionListState: LazyListState,
    bandPx: Float,
): FavoriteEdgeScroll? {
    val inCompanionGroup = when {
        primaryBoundsInWindow.contains(touchInWindow) -> false
        companionBoundsInWindow.contains(touchInWindow) -> true
        else -> return null
    }
    val bounds = if (inCompanionGroup) companionBoundsInWindow else primaryBoundsInWindow
    val listState = if (inCompanionGroup) companionListState else primaryListState
    val band = bandPx.coerceAtMost(bounds.height / 2f)
    val forward = when {
        touchInWindow.y < bounds.top + band -> false
        touchInWindow.y >= bounds.bottom - band -> true
        else -> return null
    }

    val canScroll = if (forward) {
        listState.canScrollForward
    } else {
        listState.canScrollBackward
    }
    if (!canScroll) return null

    return FavoriteEdgeScroll(inCompanionGroup, forward)
}

/**
 * Group an active drag scrolls and the direction it scrolls. Equal requests keep the running scroll
 * alive, so a drag that stays in the same band scrolls continuously instead of restarting.
 */
private data class FavoriteEdgeScroll(
    val inCompanion: Boolean,
    val forward: Boolean,
)
private data class FavoriteListDragSession(
    val sourceContainer: FavoriteContainer,
    val currentIndex: Int,
    val originInWindow: Offset,
    val size: IntSize,
    val touchStartInWindow: Offset,
    val displayedLists: List<FavoriteContainer>,
    val initialDisplayedLists: List<FavoriteContainer>,
    val visibleIdentities: List<LaunchableIdentity>,
    val visibleScrollOffset: Int,
    val released: Boolean = false,
    val exchangeGeneration: Int = 0,
    val delta: Offset = Offset.Zero,
) {
    val touchInWindow: Offset get() = touchStartInWindow + delta
}

/** Visible order of the requested group. */
private fun FavoriteDragSession.displayedOrder(companion: Boolean): List<LaunchableIdentity> =
    if (companion) displayedCompanion else displayedPrimary

/** Returns this session with the requested group's visible order replaced. */
private fun FavoriteDragSession.withDisplayedOrder(
    companion: Boolean,
    order: List<LaunchableIdentity>,
): FavoriteDragSession =
    if (companion) copy(displayedCompanion = order) else copy(displayedPrimary = order)

/**
 * Target a drag resolves inside a group: an existing favorite's body, or a boundary between rows.
 */
private sealed interface FavoriteDragTarget {
    data class Body(val slot: Int) : FavoriteDragTarget
    data class Boundary(val index: Int) : FavoriteDragTarget
}

/**
 * Resolves the target the touch point addresses in a group. Slot geometry does not change when two
 * favorites exchange places, so the resolved body stays correct even while the exchange target is
 * still moving into the source slot. A row's leading and trailing band resolves to the boundary on
 * that side, so every boundary a cross-group insertion may use is reachable without spacing the
 * rows apart; the band is capped at a third of the row so a body always remains. Space that no row
 * covers
 * resolves to the first or last boundary, which also makes an empty group a valid insertion target.
 * Returns null only when the touch point is outside the group.
 */
private fun resolveGroupTarget(
    touchInWindow: Offset,
    listBoundsInWindow: Rect,
    listState: LazyListState,
    boundaryBandPx: Float,
): FavoriteDragTarget? {
    if (!listBoundsInWindow.contains(touchInWindow)) return null
    val localY = touchInWindow.y - listBoundsInWindow.top
    val visibleItems = listState.layoutInfo.visibleItemsInfo
    val first = visibleItems.firstOrNull() ?: return FavoriteDragTarget.Boundary(0)
    val last = visibleItems.last()
    if (localY < first.offset) return FavoriteDragTarget.Boundary(first.index)
    if (localY >= last.offset + last.size) return FavoriteDragTarget.Boundary(last.index + 1)
    val item = visibleItems.firstOrNull { info ->
        localY >= info.offset && localY < info.offset + info.size
    } ?: return null
    val band = boundaryBandPx.coerceAtMost(item.size / 3f)
    return when {
        localY < item.offset + band -> FavoriteDragTarget.Boundary(item.index)
        localY >= item.offset + item.size - band -> FavoriteDragTarget.Boundary(item.index + 1)
        else -> FavoriteDragTarget.Body(item.index)
    }
}

/**
 * Prevents one pointer position from cascading through multiple rows after an exchange. The next
 * exchange needs another half-row of movement in the same direction; edge scrolling remains the
 * only mechanism that moves the list without continued pointer movement.
 */
private fun LazyListState.hasCrossedExchangeThreshold(
    touchInWindow: Offset,
    listBoundsInWindow: Rect,
    sourceSlot: Int,
    targetSlot: Int,
    lastExchangeTouchY: Float?,
): Boolean {
    val targetItem = layoutInfo.visibleItemsInfo.firstOrNull { it.index == targetSlot }
        ?: return false
    if (lastExchangeTouchY != null) {
        val requiredDistance = targetItem.size / 2f
        return if (targetSlot > sourceSlot) {
            touchInWindow.y >= lastExchangeTouchY + requiredDistance
        } else {
            touchInWindow.y <= lastExchangeTouchY - requiredDistance
        }
    }
    val localY = touchInWindow.y - listBoundsInWindow.top
    val targetCenter = targetItem.offset + targetItem.size / 2f
    return if (targetSlot < sourceSlot) {
        localY < targetCenter
    } else {
        localY >= targetCenter
    }
}

/** Returns this list with the entries at the two given positions swapped. */
private fun List<LaunchableIdentity>.exchangedAt(
    firstIndex: Int,
    secondIndex: Int,
): List<LaunchableIdentity> = toMutableList().also {
    val first = it[firstIndex]
    it[firstIndex] = it[secondIndex]
    it[secondIndex] = first
}

/** Returns this list with the entry at the given position replaced. */
private fun List<LaunchableIdentity>.replacedAt(
    index: Int,
    identity: LaunchableIdentity,
): List<LaunchableIdentity> = toMutableList().also { it[index] = identity }

@Composable
private fun HomeFavoriteDragPreview(
    session: FavoriteDragSession,
    availability: FavoriteAvailability,
    rootOriginInWindow: Offset,
) {
    val density = LocalDensity.current
    val previewAlpha = integerResource(R.integer.home_drag_preview_alpha_percent) / 100f
    val previewElevation = with(density) {
        dimensionResource(R.dimen.home_reorder_drag_elevation).toPx()
    }
    val topLeft = session.originInWindow +
        session.delta -
        rootOriginInWindow
    Box(
        modifier = Modifier
            .offset { IntOffset(topLeft.x.roundToInt(), topLeft.y.roundToInt()) }
            .size(
                width = with(density) { session.size.width.toDp() },
                height = with(density) { session.size.height.toDp() },
            )
            .alpha(previewAlpha)
            .testTag("home_favorite_drag_preview"),
    ) {
        HomeFavoritePreviewContent(
            availability = availability,
            listSize = session.listSize,
            maxWidth = with(density) { session.size.width.toDp() },
            shadowElevation = previewElevation,
        )
    }
}

@Composable
private fun HomeFavoriteBarDragPreview(
    session: FavoriteBarDragSession,
    availability: FavoriteAvailability,
    rootOriginInWindow: Offset,
) {
    val density = LocalDensity.current
    val topLeft = session.originInWindow + session.delta - rootOriginInWindow
    val previewAlpha = integerResource(R.integer.home_drag_preview_alpha_percent) / 100f
    val previewElevation = with(density) {
        dimensionResource(R.dimen.home_reorder_drag_elevation).toPx()
    }
    Box(
        modifier = Modifier
            .offset { IntOffset(topLeft.x.roundToInt(), topLeft.y.roundToInt()) }
            .size(
                width = with(density) { session.size.width.toDp() },
                height = with(density) { session.size.height.toDp() },
            )
            .alpha(previewAlpha)
            .clearAndSetSemantics {}
            .testTag("home_favorite_bar_drag_preview"),
    ) {
        HomeFavoritePreviewContent(
            availability = availability,
            listSize = FavoriteListSize.Medium,
            maxWidth = dimensionResource(R.dimen.home_favorite_bar_item_width),
            shadowElevation = previewElevation,
        )
    }
}

@Composable
private fun HomeFavoriteBarContainerDragPreview(
    session: FavoriteBarContainerDragSession,
    availabilityByIdentity: Map<LaunchableIdentity, FavoriteAvailability>,
    rootOriginInWindow: Offset,
) {
    val density = LocalDensity.current
    val topLeft = session.originInWindow + session.delta - rootOriginInWindow
    val width = with(density) { session.size.width.toDp() }
    val height = with(density) { session.size.height.toDp() }
    val borderAlpha =
        integerResource(R.integer.home_favorite_bar_border_alpha_percent) / 100f
    val fadeAlpha =
        integerResource(R.integer.home_favorite_bar_overflow_fade_alpha_percent) / 100f
    val shape = RoundedCornerShape(dimensionResource(R.dimen.home_favorite_bar_corner_radius))
    Row(
        modifier = Modifier
            .offset { IntOffset(topLeft.x.roundToInt(), topLeft.y.roundToInt()) }
            .size(width, height)
            .clip(shape)
            .background(colorResource(R.color.home_edit_surface))
            .border(
                dimensionResource(R.dimen.home_favorite_bar_border_width),
                MaterialTheme.colorScheme.onBackground.copy(alpha = borderAlpha),
                shape,
            )
            .clearAndSetSemantics {}
            .testTag("home_favorite_bar_container_drag_preview"),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(dimensionResource(R.dimen.home_favorite_bar_control_target_width))
                .fillMaxHeight(),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(dimensionResource(R.dimen.home_favorite_list_remove_badge_size))
                    .clip(
                        RoundedCornerShape(
                            dimensionResource(
                                R.dimen.home_favorite_list_control_surface_radius,
                            ),
                        ),
                    )
                    .background(MaterialTheme.colorScheme.error),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = null,
                    modifier = Modifier.size(
                        dimensionResource(R.dimen.home_favorite_list_remove_icon_size),
                    ),
                    tint = colorResource(R.color.home_favorite_remove_icon),
                )
            }
        }
        FavoriteBarRailDivider()
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clipToBounds(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .offset {
                        IntOffset(-session.visibleScrollOffset, 0)
                    },
                horizontalArrangement = Arrangement.spacedBy(
                    dimensionResource(R.dimen.home_favorite_bar_item_spacing),
                ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                session.visibleIdentities.forEach { identity ->
                    Box(
                        modifier = Modifier
                            .width(dimensionResource(R.dimen.home_favorite_bar_item_width))
                            .fillMaxHeight(),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        HomeFavoritePreviewContent(
                            availability = availabilityByIdentity[identity]
                                ?: FavoriteAvailability.Unknown(null),
                            listSize = FavoriteListSize.Medium,
                            maxWidth = dimensionResource(R.dimen.home_favorite_bar_item_width),
                            shadowElevation = 0f,
                        )
                    }
                }
            }
            val fadeColor = MaterialTheme.colorScheme.background.copy(alpha = fadeAlpha)
            val fadeWidth = dimensionResource(R.dimen.home_favorite_bar_overflow_fade_width)
            if (session.canScrollBackward) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .width(fadeWidth)
                        .fillMaxHeight()
                        .background(
                            Brush.horizontalGradient(
                                listOf(fadeColor, fadeColor.copy(alpha = 0f)),
                            ),
                        ),
                )
            }
            if (session.canScrollForward) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .width(fadeWidth)
                        .fillMaxHeight()
                        .background(
                            Brush.horizontalGradient(
                                listOf(fadeColor.copy(alpha = 0f), fadeColor),
                            ),
                        ),
                )
            }
        }
        FavoriteBarRailDivider()
        Box(
            modifier = Modifier
                .width(dimensionResource(R.dimen.home_favorite_bar_control_target_width))
                .fillMaxHeight(),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_drag_handle),
                contentDescription = null,
                modifier = Modifier.size(
                    dimensionResource(R.dimen.home_favorite_bar_control_icon_size),
                ),
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}

@Composable
private fun HomeFavoriteListDragPreview(
    session: FavoriteListDragSession,
    availabilityByIdentity: Map<LaunchableIdentity, FavoriteAvailability>,
    rootOriginInWindow: Offset,
) {
    val density = LocalDensity.current
    val width = with(density) { session.size.width.toDp() }
    val height = with(density) { session.size.height.toDp() }
    val topLeft = session.originInWindow + session.delta - rootOriginInWindow
    val previewAlpha = integerResource(R.integer.home_drag_preview_alpha_percent) / 100f
    val rowHeight = dimensionResource(session.sourceContainer.listSize.rowHeightResource())
    val dividerColor = colorResource(R.color.home_favorite_list_control_border)
    val dividerWidth = with(LocalDensity.current) {
        dimensionResource(R.dimen.home_favorite_list_control_border_width).toPx()
    }
    Column(
        modifier = Modifier
            .offset { IntOffset(topLeft.x.roundToInt(), topLeft.y.roundToInt()) }
            .size(width, height)
            .clip(RoundedCornerShape(dimensionResource(R.dimen.home_edit_surface_radius)))
            .background(colorResource(R.color.home_edit_surface))
            .alpha(previewAlpha)
            .clearAndSetSemantics {}
            .testTag("home_favorite_list_drag_preview"),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.home_favorite_list_control_bar_height))
                .drawWithContent {
                    drawContent()
                    drawLine(
                        color = dividerColor,
                        start = Offset(0f, size.height - dividerWidth / 2f),
                        end = Offset(size.width, size.height - dividerWidth / 2f),
                        strokeWidth = dividerWidth,
                    )
                },
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(Modifier.weight(1f))
                    Box(
                        Modifier
                            .width(
                                dimensionResource(
                                    R.dimen.home_favorite_list_control_target_width,
                                ),
                            )
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .width(
                                    dimensionResource(
                                        R.dimen.home_favorite_list_control_target_width,
                                    ),
                                )
                                .height(
                                    dimensionResource(
                                        R.dimen.home_favorite_list_control_bar_height,
                                    ),
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(
                                        dimensionResource(
                                            R.dimen.home_favorite_list_remove_badge_size,
                                        ),
                                    )
                                    .clip(
                                        RoundedCornerShape(
                                            dimensionResource(
                                                R.dimen
                                                    .home_favorite_list_control_surface_radius,
                                            ),
                                        ),
                                    )
                                    .background(MaterialTheme.colorScheme.error),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_close),
                                    contentDescription = null,
                                    tint = colorResource(R.color.home_favorite_remove_icon),
                                    modifier = Modifier.size(
                                        dimensionResource(
                                            R.dimen.home_favorite_list_remove_icon_size,
                                        ),
                                    ),
                                )
                            }
                        }
                    }
                    Box(
                        Modifier
                            .width(
                                dimensionResource(
                                    R.dimen.home_favorite_list_control_target_width,
                                ),
                            )
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = when (session.sourceContainer.listSize) {
                                FavoriteListSize.Large ->
                                    stringResource(R.string.favorite_list_large_short)
                                FavoriteListSize.Medium ->
                                    stringResource(R.string.favorite_list_medium_short)
                                FavoriteListSize.Small ->
                                    stringResource(R.string.favorite_list_small_short)
                            },
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = dimensionResource(
                                R.dimen.home_favorite_list_size_control_text_size,
                            ).value.sp,
                        )
                    }
                    Box(
                        Modifier
                            .width(
                                dimensionResource(
                                    R.dimen.home_favorite_list_control_target_width,
                                ),
                            )
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .width(
                                    dimensionResource(
                                        R.dimen.home_favorite_list_control_target_width,
                                    ),
                                )
                                .height(
                                    dimensionResource(
                                        R.dimen.home_favorite_list_control_bar_height,
                                    ),
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_drag_handle),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(
                                    dimensionResource(
                                        R.dimen.home_favorite_list_reorder_icon_size,
                                    ),
                                ),
                            )
                        }
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clipToBounds(),
        ) {
            Column(
                modifier = Modifier.offset {
                    IntOffset(x = 0, y = -session.visibleScrollOffset)
                },
            ) {
                session.visibleIdentities.forEach { identity ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(rowHeight),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    start = dimensionResource(
                                        R.dimen.home_favorite_list_icon_start_margin,
                                    ),
                                ),
                        ) {
                            HomeFavoritePreviewContent(
                                availability = availabilityByIdentity[identity]
                                    ?: FavoriteAvailability.Unknown(null),
                                listSize = session.sourceContainer.listSize,
                                maxWidth = width,
                                shadowElevation = 0f,
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
private fun HomeFavoritePreviewContent(
    availability: FavoriteAvailability,
    listSize: FavoriteListSize,
    maxWidth: androidx.compose.ui.unit.Dp,
    shadowElevation: Float,
) {
    val entry = availability.presentationEntry
    val iconSize = dimensionResource(listSize.iconSizeResource())
    val iconPixels = with(LocalDensity.current) { iconSize.roundToPx() }
    val displayText = when (availability) {
        is FavoriteAvailability.Available -> availability.entry.label
        is FavoriteAvailability.Disabled -> entry?.let {
            stringResource(R.string.favorite_disabled_format, it.label)
        } ?: stringResource(R.string.favorite_application_disabled)
        is FavoriteAvailability.TemporarilyUnavailable,
        is FavoriteAvailability.Unknown,
        -> entry?.let {
            stringResource(R.string.favorite_unavailable_format, it.label)
        } ?: stringResource(R.string.favorite_application_unavailable)
        FavoriteAvailability.ConfirmedRemoved ->
            stringResource(R.string.favorite_application_unavailable)
    }
    Row(
        modifier = Modifier
            .widthIn(max = maxWidth)
            .wrapContentWidth()
            .graphicsLayer { this.shadowElevation = shadowElevation },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (entry == null) {
            Icon(
                painter = painterResource(R.drawable.ic_inventory_error),
                contentDescription = null,
                modifier = Modifier.size(iconSize),
                tint = MaterialTheme.colorScheme.onBackground,
            )
        } else {
            val bitmap = entry.iconBitmap?.asImageBitmap() ?: remember(entry.icon, iconPixels) {
                entry.icon.toBitmap(iconPixels, iconPixels).asImageBitmap()
            }
            Image(bitmap = bitmap, contentDescription = null, modifier = Modifier.size(iconSize))
        }
        Spacer(Modifier.width(dimensionResource(R.dimen.home_favorite_icon_label_gap)))
        Text(
            text = displayText,
            modifier = Modifier.widthIn(max = maxWidth),
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = dimensionResource(listSize.textSizeResource()).value.sp,
            lineHeight = dimensionResource(listSize.lineHeightResource()).value.sp,
        )
    }
}

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
private fun FavoriteListSizeControl(
    index: Int,
    selectedSize: FavoriteListSize,
    onChangeSize: (FavoriteListSize) -> Unit,
) {
    var expanded by remember(index) { mutableStateOf(false) }
    val interactionSource = remember(index) { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val targetWidth = dimensionResource(R.dimen.home_favorite_list_control_target_width)
    val targetHeight = dimensionResource(R.dimen.home_favorite_list_control_bar_height)
    val stateSize = dimensionResource(R.dimen.home_favorite_list_control_state_size)
    val selectedLabel = when (selectedSize) {
        FavoriteListSize.Large -> stringResource(R.string.favorite_list_large_short)
        FavoriteListSize.Medium -> stringResource(R.string.favorite_list_medium_short)
        FavoriteListSize.Small -> stringResource(R.string.favorite_list_small_short)
    }
    val accessibilityLabel = stringResource(
        R.string.favorite_list_size_format,
        when (selectedSize) {
            FavoriteListSize.Large -> stringResource(R.string.favorite_list_large)
            FavoriteListSize.Medium -> stringResource(R.string.favorite_list_medium)
            FavoriteListSize.Small -> stringResource(R.string.favorite_list_small)
        },
    )
    Box {
        Box(
            modifier = Modifier
                .width(targetWidth)
                .height(targetHeight)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    role = Role.Button,
                    onClick = { expanded = true },
                )
                .semantics { contentDescription = accessibilityLabel }
                .testTag("favorite_list_size_$index"),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(stateSize)
                    .clip(
                        RoundedCornerShape(
                            dimensionResource(
                                R.dimen.home_favorite_list_control_surface_radius,
                            ),
                        ),
                    )
                    .then(
                        if (pressed) {
                            Modifier.background(
                                colorResource(R.color.home_favorite_list_control_pressed),
                            )
                        } else {
                            Modifier
                        },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = selectedLabel,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = dimensionResource(
                        R.dimen.home_favorite_list_size_control_text_size,
                    ).value.sp,
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(
                dimensionResource(R.dimen.home_favorite_list_size_menu_corner_radius),
            ),
            modifier = Modifier.testTag("favorite_list_size_menu_$index"),
        ) {
            FavoriteListSize.values().forEach { size ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = size == selectedSize,
                                onClick = null,
                            )
                            Spacer(
                                modifier = Modifier.width(
                                    dimensionResource(
                                        R.dimen.home_favorite_list_size_menu_indicator_icon_gap,
                                    ),
                                ),
                            )
                            val iconSize = dimensionResource(size.iconSizeResource())
                            val iconPixels = with(LocalDensity.current) {
                                iconSize.roundToPx()
                            }
                            val context = LocalContext.current
                            val defaultIconBitmap = remember(size, iconPixels) {
                                requireNotNull(context.getDrawable(R.mipmap.ic_launcher))
                                    .toBitmap(iconPixels, iconPixels)
                                    .asImageBitmap()
                            }
                            Image(
                                bitmap = defaultIconBitmap,
                                contentDescription = null,
                                modifier = Modifier.size(iconSize),
                            )
                            Spacer(
                                modifier = Modifier.width(
                                    dimensionResource(R.dimen.home_favorite_icon_label_gap),
                                ),
                            )
                            Text(
                                text = when (size) {
                                    FavoriteListSize.Large ->
                                        stringResource(R.string.favorite_list_large)
                                    FavoriteListSize.Medium ->
                                        stringResource(R.string.favorite_list_medium)
                                    FavoriteListSize.Small ->
                                        stringResource(R.string.favorite_list_small)
                                },
                                fontSize = dimensionResource(size.textSizeResource()).value.sp,
                            )
                        }
                    },
                    onClick = {
                        expanded = false
                        if (size != selectedSize) onChangeSize(size)
                    },
                    modifier = Modifier
                        .height(
                            dimensionResource(
                                R.dimen.home_favorite_list_size_menu_item_height,
                            ),
                        )
                        .testTag("favorite_list_size_${index}_${size.name}"),
                )
            }
        }
    }
}

@Composable
private fun FavoriteListControlBar(
    index: Int,
    listCount: Int,
    selectedSize: FavoriteListSize,
    onChangeSize: (FavoriteListSize) -> Unit,
    onRemoveList: () -> Unit,
    onListDragStart: (Offset) -> Unit,
    onListDrag: (Offset) -> Unit,
    onListDragEnd: () -> Unit,
    onListDragCancel: () -> Unit,
) {
    var removeDialogVisible by remember(index) { mutableStateOf(false) }
    var reorderOriginInWindow by remember(index) { mutableStateOf(Offset.Zero) }
    var reorderDragging by remember(index) { mutableStateOf(false) }
    val dividerColor = colorResource(R.color.home_favorite_list_control_border)
    val dividerWidth = with(LocalDensity.current) {
        dimensionResource(R.dimen.home_favorite_list_control_border_width).toPx()
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.home_favorite_list_control_bar_height))
            .drawWithContent {
                drawContent()
                drawLine(
                    color = dividerColor,
                    start = Offset(0f, size.height - dividerWidth / 2f),
                    end = Offset(size.width, size.height - dividerWidth / 2f),
                    strokeWidth = dividerWidth,
                )
            }
            .testTag("favorite_list_control_bar_$index"),
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(Modifier.weight(1f))
                FavoriteListRemoveControl(
                    onClick = { removeDialogVisible = true },
                    testTag = "remove_favorite_list_$index",
                )
                FavoriteListSizeControl(
                    index = index,
                    selectedSize = selectedSize,
                    onChangeSize = onChangeSize,
                )
                Box(
                    modifier = Modifier
                        .width(
                            dimensionResource(R.dimen.home_favorite_list_control_target_width),
                        )
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center,
                ) {
                    if (listCount == 2) {
                        val stateSize = dimensionResource(
                            R.dimen.home_favorite_list_control_state_size,
                        )
                        val iconSize = dimensionResource(
                            R.dimen.home_favorite_list_reorder_icon_size,
                        )
                        val contentDescription =
                            stringResource(R.string.favorite_list_reorder_handle)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .onGloballyPositioned {
                                    reorderOriginInWindow = it.positionInWindow()
                                }
                                .pointerInput(index) {
                                    detectReorderDrag(
                                        onPressChanged = { reorderDragging = it },
                                        onDragStart = { localTouch ->
                                            onListDragStart(reorderOriginInWindow + localTouch)
                                        },
                                        onDrag = onListDrag,
                                        onDragEnd = {
                                            reorderDragging = false
                                            onListDragEnd()
                                        },
                                        onDragCancel = {
                                            reorderDragging = false
                                            onListDragCancel()
                                        },
                                    )
                                }
                                .semantics {
                                    this.contentDescription = contentDescription
                                    role = Role.Button
                                }
                                .testTag("reorder_favorite_list_$index"),
                            contentAlignment = Alignment.Center,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(stateSize)
                                    .clip(
                                        RoundedCornerShape(
                                            dimensionResource(
                                                R.dimen
                                                    .home_favorite_list_control_surface_radius,
                                            ),
                                        ),
                                    )
                                    .then(
                                        if (reorderDragging) {
                                            Modifier.background(
                                                colorResource(
                                                    R.color
                                                        .home_favorite_list_control_pressed,
                                                ),
                                            )
                                        } else {
                                            Modifier
                                        },
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_drag_handle),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.size(iconSize),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    if (removeDialogVisible) {
        AlertDialog(
            onDismissRequest = { removeDialogVisible = false },
            title = {
                Text(stringResource(R.string.remove_favorite_list_title))
            },
            text = {
                Text(stringResource(R.string.remove_list))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        removeDialogVisible = false
                        onRemoveList()
                    },
                    modifier = Modifier.testTag("confirm_remove_favorite_list_$index"),
                ) {
                    Text(
                        text = stringResource(R.string.remove),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { removeDialogVisible = false },
                    modifier = Modifier.testTag("cancel_remove_favorite_list_$index"),
                ) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

@Composable
private fun FavoriteListRemoveControl(
    onClick: () -> Unit,
    testTag: String,
) {
    val targetWidth = dimensionResource(R.dimen.home_favorite_list_control_target_width)
    val targetHeight = dimensionResource(R.dimen.home_favorite_list_control_bar_height)
    val badgeSize = dimensionResource(R.dimen.home_favorite_list_remove_badge_size)
    val iconSize = dimensionResource(R.dimen.home_favorite_list_remove_icon_size)
    val contentDescription = stringResource(R.string.remove_favorite_list)
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressedAlpha = integerResource(
        R.integer.home_favorite_list_remove_pressed_alpha_percent,
    ) / 100f
    Box(
        modifier = Modifier
            .width(targetWidth)
            .height(targetHeight)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            )
            .semantics { this.contentDescription = contentDescription }
            .testTag(testTag),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(badgeSize)
                .clip(
                    RoundedCornerShape(
                        dimensionResource(R.dimen.home_favorite_list_control_surface_radius),
                    ),
                )
                .background(MaterialTheme.colorScheme.error)
                .alpha(if (pressed) pressedAlpha else 1f),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = null,
                modifier = Modifier.size(iconSize),
                tint = colorResource(R.color.home_favorite_remove_icon),
            )
        }
    }
}

@Composable
private fun HomeFavoriteList(
    modifier: Modifier,
    identities: List<LaunchableIdentity>,
    availabilityByIdentity: Map<LaunchableIdentity, FavoriteAvailability>,
    listState: LazyListState,
    nestedScrollConnection: NestedScrollConnection?,
    editMode: Boolean,
    compact: Boolean,
    listSize: FavoriteListSize? = null,
    draggedIdentity: LaunchableIdentity?,
    exchangeTargetIdentity: LaunchableIdentity?,
    insertionBoundaryIndex: Int?,
    onBoundsInWindow: (Rect) -> Unit,
    onLaunchFavorite: (FavoriteAvailability) -> Unit,
    onLongPressFavorite: (LaunchableEntry) -> Unit,
    onRemoveFavorite: (LaunchableIdentity) -> Unit = {},
    listIndex: Int? = null,
    listCount: Int = 0,
    onChangeListSize: (FavoriteListSize) -> Unit = {},
    onRemoveList: () -> Unit = {},
    onAddFavorites: () -> Unit = {},
    onContainerBoundsInWindow: (Rect) -> Unit = {},
    sourceListPlaceholder: Boolean = false,
    listExchangeHighlight: Boolean = false,
    listDragActive: Boolean = false,
    onListDragStart: (Offset) -> Unit = {},
    onListDrag: (Offset) -> Unit = {},
    onListDragEnd: () -> Unit = {},
    onListDragCancel: () -> Unit = {},
    onDragStart: (LaunchableIdentity, Offset, IntSize, Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
    testTag: String? = null,
) {
    val controlBarHeight = dimensionResource(
        R.dimen.home_favorite_list_control_bar_height,
    )
    val sourceSlotAlpha = integerResource(R.integer.home_drag_source_slot_alpha_percent) / 100f
    val insertionLineColor = colorResource(R.color.home_favorite_insertion_line)
    val insertionLineThickness = with(LocalDensity.current) {
        dimensionResource(R.dimen.home_favorite_insertion_line_thickness).toPx()
    }
    val hapticFeedback = LocalHapticFeedback.current
    // Handle hot zones and row geometry of the currently composed rows. Only the drag gesture reads
    // them, so a plain map keeps the layout reporting out of composition.
    val dragAnchors = remember { mutableMapOf<String, FavoriteDragAnchor>() }
    var viewportOriginInWindow by remember { mutableStateOf(Offset.Zero) }
    val currentOnDragStart by rememberUpdatedState(onDragStart)
    val currentOnDrag by rememberUpdatedState(onDrag)
    val currentOnDragEnd by rememberUpdatedState(onDragEnd)
    val currentOnDragCancel by rememberUpdatedState(onDragCancel)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                onContainerBoundsInWindow(
                    Rect(
                        offset = coordinates.positionInWindow(),
                        size = coordinates.size.toSize(),
                    ),
                )
            }
            .editSurface(editMode)
            .then(
                if (!listExchangeHighlight) {
                    Modifier
                } else {
                    Modifier.border(
                        width = dimensionResource(R.dimen.home_favorite_exchange_border_width),
                        color = colorResource(R.color.home_favorite_exchange_border),
                        shape = RoundedCornerShape(
                            dimensionResource(R.dimen.home_favorite_exchange_border_radius),
                        ),
                    )
                },
            )
            .then(
                if (sourceListPlaceholder) Modifier.clearAndSetSemantics {} else Modifier,
            )
    ) {
        if (editMode && listIndex != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(controlBarHeight)
                    .alpha(if (sourceListPlaceholder) 0f else 1f)
                    .then(
                        if (sourceListPlaceholder) {
                            Modifier.clearAndSetSemantics {}
                        } else {
                            Modifier
                        },
                    ),
            ) {
                FavoriteListControlBar(
                    index = listIndex,
                    listCount = listCount,
                    selectedSize = listSize ?: FavoriteListSize.Medium,
                    onChangeSize = onChangeListSize,
                    onRemoveList = onRemoveList,
                    onListDragStart = onListDragStart,
                    onListDrag = onListDrag,
                    onListDragEnd = onListDragEnd,
                    onListDragCancel = onListDragCancel,
                )
            }
        }
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .alpha(if (sourceListPlaceholder) 0f else 1f)
                .then(
                    if (sourceListPlaceholder) {
                        Modifier.clearAndSetSemantics {}
                    } else {
                        Modifier
                    },
                )
                .then(
                    nestedScrollConnection?.let { Modifier.nestedScroll(it) } ?: Modifier,
                )
                .then(
                    // An empty group has no row to carry the line, so its only boundary
                    // is marked on the group itself.
                    if (identities.isNotEmpty() || insertionBoundaryIndex == null) {
                        Modifier
                    } else {
                        Modifier.drawWithContent {
                            drawContent()
                            drawRect(
                                color = insertionLineColor,
                                topLeft = Offset.Zero,
                                size = Size(size.width, insertionLineThickness),
                            )
                        }
                    },
                )
                .onGloballyPositioned { coordinates ->
                    val origin = coordinates.positionInWindow()
                    viewportOriginInWindow = origin
                    onBoundsInWindow(
                        Rect(offset = origin, size = coordinates.size.toSize()),
                    )
                }
                .then(
                    if (!editMode || listDragActive) {
                        Modifier
                    } else {
                        Modifier.pointerInput(Unit) {
                            detectFavoriteDrag(
                                anchorAt = { local ->
                                    val inWindow = viewportOriginInWindow + local
                                    dragAnchors.values.firstOrNull {
                                        it.handleHitZoneInWindow().contains(inWindow)
                                    }
                                },
                                onDragStart = { anchor, local ->
                                    hapticFeedback.performHapticFeedback(
                                        HapticFeedbackType.LongPress,
                                    )
                                    currentOnDragStart(
                                        anchor.identity,
                                        anchor.rowOriginInWindow,
                                        anchor.rowSize,
                                        viewportOriginInWindow + local,
                                    )
                                },
                                onDrag = { currentOnDrag(it) },
                                onDragEnd = { currentOnDragEnd() },
                                onDragCancel = { currentOnDragCancel() },
                            )
                        }
                    },
                )
                .testTag(testTag ?: if (compact) "home_companion_favorites" else "home_favorites"),
            state = listState,
        ) {
            itemsIndexed(
                items = identities,
                key = { _, identity -> identity.stableKey() },
            ) { index, identity ->
                val availability = availabilityByIdentity[identity]
                    ?: FavoriteAvailability.Unknown(null)
                val entry = availability.presentationEntry
                val dragging = identity == draggedIdentity
                // A pending cross-group insertion marks its boundary on the adjacent row edge; the last
                // row also carries the boundary that follows it.
                val lineAtTop = insertionBoundaryIndex == index
                val lineAtBottom = insertionBoundaryIndex == identities.size &&
                        index == identities.lastIndex
                val anchorKey = identity.stableKey()
                DisposableEffect(anchorKey) {
                    onDispose { dragAnchors.remove(anchorKey) }
                }
                HomeFavoriteRow(
                    modifier = Modifier
                        .drawWithContent {
                            drawContent()
                            if (lineAtTop) {
                                drawRect(
                                    color = insertionLineColor,
                                    topLeft = Offset.Zero,
                                    size = Size(size.width, insertionLineThickness),
                                )
                            }
                            if (lineAtBottom) {
                                drawRect(
                                    color = insertionLineColor,
                                    topLeft = Offset(0f, size.height - insertionLineThickness),
                                    size = Size(size.width, insertionLineThickness),
                                )
                            }
                        }
                        .then(
                            if (dragging) {
                                Modifier.alpha(sourceSlotAlpha)
                            } else {
                                // The drag preview already carries the movement, so an exchanged
                                // favorite
                                // is shown directly in its new slot; only appearance and removal keep a
                                // fade.
                                Modifier.animateItem(placementSpec = null)
                            },
                        ),
                    availability = availability,
                    onClick = { onLaunchFavorite(availability) },
                    onLongClick = {
                        if (entry != null) onLongPressFavorite(entry)
                    },
                    onRemoveFavorite = { onRemoveFavorite(identity) },
                    editMode = editMode,
                    compact = compact,
                    listSize = listSize,
                    exchangeHighlight = identity == exchangeTargetIdentity,
                    onRowBoundsInWindow = { origin, size ->
                        dragAnchors[anchorKey] =
                            (dragAnchors[anchorKey] ?: FavoriteDragAnchor(identity))
                                .copy(rowOriginInWindow = origin, rowSize = size)
                    },
                    onHandleBoundsInWindow = { bounds ->
                        dragAnchors[anchorKey] =
                            (dragAnchors[anchorKey] ?: FavoriteDragAnchor(identity))
                                .copy(handleBoundsInWindow = bounds)
                    },
                )
            }
            if (editMode && listIndex != null) {
                item(key = "favorite_add_$listIndex") {
                    HomeFavoriteAddControl(
                        onClick = onAddFavorites,
                        testTag = "favorite_add_$listIndex",
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeFavoriteAddControl(
    onClick: () -> Unit,
    testTag: String,
    @StringRes labelRes: Int = R.string.add_apps,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.home_favorite_list_control_bar_height))
            .clickable(role = Role.Button, onClick = onClick)
            .testTag(testTag),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(labelRes),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun HomeFavoriteBars(
    favoriteBars: List<FavoriteContainer>,
    availabilityByIdentity: Map<LaunchableIdentity, FavoriteAvailability>,
    editMode: Boolean,
    onLaunchFavorite: (FavoriteAvailability) -> Unit,
    onLongPressFavorite: (LaunchableEntry) -> Unit,
    onAddFavoritesToBar: (String) -> Unit,
    onAddProvisionalFavoriteBar: () -> Unit,
    favoriteBarStates: MutableMap<String, LazyListState>,
    favoriteBarBoundsInWindow: MutableMap<String, Rect>,
    draggedIdentity: LaunchableIdentity?,
    draggedBarId: String?,
    highlightedBarId: String?,
    onRemoveFavorite: (String, LaunchableIdentity) -> Unit,
    onRemoveFavoriteBar: (String) -> Unit,
    onBarDragStart: (FavoriteContainer, Int, Offset) -> Unit,
    onBarDrag: (Offset) -> Unit,
    onBarDragEnd: () -> Unit,
    onBarDragCancel: () -> Unit,
    onDragStart: (FavoriteContainer, LaunchableIdentity, Offset, IntSize) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
) {
    val borderAlpha =
        integerResource(R.integer.home_favorite_bar_border_alpha_percent) / 100f
    val fadeAlpha =
        integerResource(R.integer.home_favorite_bar_overflow_fade_alpha_percent) / 100f
    val fadeWidthPx = with(LocalDensity.current) {
        dimensionResource(R.dimen.home_favorite_bar_overflow_fade_width).toPx()
    }
    val fadeColor = MaterialTheme.colorScheme.background.copy(alpha = fadeAlpha)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .editSurface(editMode),
        verticalArrangement = Arrangement.spacedBy(
            dimensionResource(R.dimen.home_favorite_bar_spacing),
        ),
    ) {
        favoriteBars.forEachIndexed { barIndex, bar ->
            val listState = favoriteBarStates.getOrPut(bar.id) { LazyListState() }
            val barShape = RoundedCornerShape(
                dimensionResource(R.dimen.home_favorite_bar_corner_radius),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.home_favorite_bar_height))
                    .then(
                        if (editMode) {
                            Modifier.border(
                                width = dimensionResource(
                                    R.dimen.home_favorite_bar_border_width,
                                ),
                                color = MaterialTheme.colorScheme.onBackground.copy(
                                    alpha = borderAlpha,
                                ),
                                shape = barShape,
                            )
                        } else {
                            Modifier
                        },
                    )
                    .onGloballyPositioned { coordinates ->
                        val origin = coordinates.positionInWindow()
                        favoriteBarBoundsInWindow[bar.id] = Rect(
                            offset = origin,
                            size = coordinates.size.toSize(),
                        )
                    }
                    .then(
                        if (bar.id == draggedBarId) {
                            Modifier
                                .clearAndSetSemantics {}
                                .alpha(0f)
                        } else {
                            Modifier
                        },
                    )
                    .then(
                        if (bar.id == highlightedBarId) {
                            Modifier.border(
                                width = dimensionResource(
                                    R.dimen.home_favorite_exchange_border_width,
                                ),
                                color = colorResource(R.color.home_favorite_exchange_border),
                                shape = barShape,
                            )
                        } else {
                            Modifier
                        },
                    )
                    .testTag("home_favorite_bar_$barIndex"),
            ) {
                if (editMode) {
                    FavoriteBarRemoveControl(
                        index = barIndex,
                        onRemove = { onRemoveFavoriteBar(bar.id) },
                    )
                    FavoriteBarRailDivider()
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(barShape)
                        .drawWithContent {
                            drawContent()
                            if (listState.canScrollBackward) {
                                drawRect(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(fadeColor, fadeColor.copy(alpha = 0f)),
                                        startX = 0f,
                                        endX = fadeWidthPx,
                                    ),
                                    size = Size(fadeWidthPx, size.height),
                                )
                            }
                            if (listState.canScrollForward) {
                                drawRect(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(fadeColor.copy(alpha = 0f), fadeColor),
                                        startX = size.width - fadeWidthPx,
                                        endX = size.width,
                                    ),
                                    topLeft = Offset(size.width - fadeWidthPx, 0f),
                                    size = Size(fadeWidthPx, size.height),
                                )
                            }
                        },
                ) {
                    LazyRow(
                        modifier = Modifier.fillMaxSize(),
                        state = listState,
                        horizontalArrangement = Arrangement.spacedBy(
                            dimensionResource(R.dimen.home_favorite_bar_item_spacing),
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        items(
                            items = bar.identities,
                            key = LaunchableIdentity::stableKey,
                        ) { identity ->
                            val availability = availabilityByIdentity[identity]
                                ?: FavoriteAvailability.Unknown(null)
                            HomeFavoriteBarItem(
                                availability = availability,
                                editMode = editMode,
                                sourcePlaceholder = identity == draggedIdentity,
                                onClick = { onLaunchFavorite(availability) },
                                onLongClick = {
                                    availability.presentationEntry?.let(onLongPressFavorite)
                                },
                                onRemoveFavorite = { onRemoveFavorite(bar.id, identity) },
                                onDragStart = { origin, size ->
                                    onDragStart(bar, identity, origin, size)
                                },
                                onDrag = onDrag,
                                onDragEnd = onDragEnd,
                                onDragCancel = onDragCancel,
                            )
                        }
                        if (editMode) {
                            item(key = "favorite_bar_add_${bar.id}") {
                                Box(
                                    modifier = Modifier
                                        .width(
                                            dimensionResource(
                                                R.dimen.home_favorite_bar_item_width,
                                            ),
                                        )
                                        .fillMaxHeight(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    HomeFavoriteAddControl(
                                        onClick = { onAddFavoritesToBar(bar.id) },
                                        testTag = "favorite_bar_add_$barIndex",
                                        labelRes = R.string.add_apps,
                                    )
                                }
                            }
                        }
                    }
                }
                if (editMode) {
                    FavoriteBarRailDivider()
                    FavoriteBarReorderControl(
                        index = barIndex,
                        visible = favoriteBars.size >= 2,
                        onDragStart = { touch ->
                            onBarDragStart(bar, barIndex, touch)
                        },
                        onDrag = onBarDrag,
                        onDragEnd = onBarDragEnd,
                        onDragCancel = onBarDragCancel,
                    )
                }
            }
        }
        if (editMode && favoriteBars.size < 5) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.home_favorite_bar_height))
                    .border(
                        width = dimensionResource(R.dimen.home_favorite_bar_border_width),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = borderAlpha),
                        shape = RoundedCornerShape(
                            dimensionResource(R.dimen.home_favorite_bar_corner_radius),
                        ),
                    )
                    .clip(RoundedCornerShape(
                        dimensionResource(R.dimen.home_favorite_bar_corner_radius),
                    )),
                contentAlignment = Alignment.Center,
            ) {
                HomeFavoriteAddControl(
                    onClick = onAddProvisionalFavoriteBar,
                    testTag = "favorite_bar_provisional_add",
                    labelRes = R.string.new_favorite_bar,
                )
            }
        }
    }
}

@Composable
private fun FavoriteBarRailDivider() {
    Box(
        modifier = Modifier
            .width(dimensionResource(R.dimen.home_favorite_bar_rail_divider_width))
            .fillMaxHeight()
            .background(colorResource(R.color.home_favorite_bar_rail_divider)),
    )
}

@Composable
private fun FavoriteBarRemoveControl(
    index: Int,
    onRemove: () -> Unit,
) {
    var dialogVisible by remember(index) { mutableStateOf(false) }
    val interactionSource = remember(index) { MutableInteractionSource() }
    val description = stringResource(R.string.remove_favorite_bar)
    Box(
        modifier = Modifier
            .width(dimensionResource(R.dimen.home_favorite_bar_control_target_width))
            .fillMaxHeight()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = { dialogVisible = true },
            )
            .semantics {
                contentDescription = description
            }
            .testTag("remove_favorite_bar_$index"),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(dimensionResource(R.dimen.home_favorite_list_remove_badge_size))
                .clip(
                    RoundedCornerShape(
                        dimensionResource(R.dimen.home_favorite_list_control_surface_radius),
                    ),
                )
                .background(MaterialTheme.colorScheme.error),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = null,
                modifier = Modifier.size(
                    dimensionResource(R.dimen.home_favorite_list_remove_icon_size),
                ),
                tint = colorResource(R.color.home_favorite_remove_icon),
            )
        }
    }
    if (dialogVisible) {
        AlertDialog(
            onDismissRequest = { dialogVisible = false },
            title = { Text(stringResource(R.string.remove_favorite_bar_title)) },
            text = { Text(stringResource(R.string.remove_favorite_bar_body)) },
            dismissButton = {
                TextButton(onClick = { dialogVisible = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        dialogVisible = false
                        onRemove()
                    },
                    modifier = Modifier.testTag("confirm_remove_favorite_bar_$index"),
                ) {
                    Text(
                        text = stringResource(R.string.remove),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
        )
    }
}

@Composable
private fun FavoriteBarReorderControl(
    index: Int,
    visible: Boolean,
    onDragStart: (Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
) {
    var originInWindow by remember(index) { mutableStateOf(Offset.Zero) }
    var dragging by remember(index) { mutableStateOf(false) }
    val description = stringResource(R.string.favorite_bar_reorder_handle)
    Box(
        modifier = Modifier
            .width(dimensionResource(R.dimen.home_favorite_bar_reorder_target_width))
            .fillMaxHeight()
            .then(
                if (visible) {
                    Modifier
                        .onGloballyPositioned { originInWindow = it.positionInWindow() }
                        .pointerInput(index) {
                            detectReorderDrag(
                                onPressChanged = { dragging = it },
                                onDragStart = { touch -> onDragStart(originInWindow + touch) },
                                onDrag = onDrag,
                                onDragEnd = {
                                    dragging = false
                                    onDragEnd()
                                },
                                onDragCancel = {
                                    dragging = false
                                    onDragCancel()
                                },
                            )
                        }
                        .semantics {
                            contentDescription = description
                            role = Role.Button
                        }
                } else {
                    Modifier.clearAndSetSemantics {}
                },
            )
            .testTag("reorder_favorite_bar_$index"),
        contentAlignment = Alignment.Center,
    ) {
        if (visible) {
            Box(
                modifier = Modifier
                    .size(dimensionResource(R.dimen.home_favorite_bar_control_state_size))
                    .clip(RoundedCornerShape(
                        dimensionResource(R.dimen.home_favorite_list_control_surface_radius),
                    ))
                    .then(if (dragging) {
                        Modifier.background(
                            colorResource(R.color.home_favorite_list_control_pressed),
                        )
                    } else Modifier),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_drag_handle),
                    contentDescription = null,
                    modifier = Modifier.size(
                        dimensionResource(R.dimen.home_favorite_bar_control_icon_size),
                    ),
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun HomeFavoriteBarItem(
    availability: FavoriteAvailability,
    editMode: Boolean,
    sourcePlaceholder: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onRemoveFavorite: () -> Unit,
    onDragStart: (Offset, IntSize) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
) {
    val entry = availability.presentationEntry
    val iconSize = dimensionResource(R.dimen.home_favorite_icon_size)
    val iconPixels = with(LocalDensity.current) { iconSize.roundToPx() }
    val disabledAlpha = integerResource(R.integer.disabled_content_alpha_percent) / 100f
    val itemBackgroundAlpha = integerResource(
        R.integer.home_favorite_bar_item_background_alpha_percent,
    ) / 100f
    val borderAlpha = integerResource(R.integer.home_favorite_bar_border_alpha_percent) / 100f
    val interactionSource = remember(entry?.identity) { MutableInteractionSource() }
    val removeInteractionSource = remember(entry?.identity) { MutableInteractionSource() }
    val hapticFeedback = LocalHapticFeedback.current
    val displayText = when (availability) {
        is FavoriteAvailability.Available -> availability.entry.label
        is FavoriteAvailability.Disabled -> entry?.let {
            stringResource(R.string.favorite_disabled_format, it.label)
        } ?: stringResource(R.string.favorite_application_disabled)
        is FavoriteAvailability.TemporarilyUnavailable,
        is FavoriteAvailability.Unknown,
        -> entry?.let {
            stringResource(R.string.favorite_unavailable_format, it.label)
        } ?: stringResource(R.string.favorite_application_unavailable)
        FavoriteAvailability.ConfirmedRemoved ->
            stringResource(R.string.favorite_application_unavailable)
    }
    var itemOriginInWindow by remember(entry?.identity) { mutableStateOf(Offset.Zero) }
    var itemSize by remember(entry?.identity) { mutableStateOf(IntSize.Zero) }
    Box(
        modifier = Modifier
            .widthIn(max = dimensionResource(R.dimen.home_favorite_bar_item_width))
            .fillMaxHeight()
            .onGloballyPositioned {
                itemOriginInWindow = it.positionInWindow()
                itemSize = it.size
            }
            .background(
                MaterialTheme.colorScheme.onBackground.copy(alpha = itemBackgroundAlpha),
                RoundedCornerShape(
                    dimensionResource(R.dimen.home_favorite_bar_corner_radius),
                ),
            )
            .border(
                width = dimensionResource(R.dimen.home_favorite_bar_border_width),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = borderAlpha),
                shape = RoundedCornerShape(
                    dimensionResource(R.dimen.home_favorite_bar_corner_radius),
                ),
            )
            .combinedClickable(
                enabled = !editMode,
                interactionSource = interactionSource,
                indication = if (editMode) null else ripple(
                    color = colorResource(R.color.home_favorite_ripple),
                ),
                role = Role.Button,
                onClick = onClick,
                onLongClick = {
                    if (entry != null) {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        onLongClick()
                    }
                },
            )
            .alpha(if (availability is FavoriteAvailability.Available) 1f else disabledAlpha)
            .then(if (sourcePlaceholder) Modifier.alpha(0f) else Modifier)
            .testTag("home_favorite_bar_item"),
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = dimensionResource(R.dimen.home_favorite_bar_item_inset))
                .alpha(0f)
                .clearAndSetSemantics {},
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(Modifier.size(iconSize))
            Spacer(
                Modifier.width(
                    dimensionResource(R.dimen.home_favorite_bar_icon_label_gap),
                ),
            )
            Text(
                text = displayText,
                modifier = Modifier.widthIn(
                    max = dimensionResource(R.dimen.home_favorite_bar_label_max_width),
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = dimensionResource(R.dimen.home_favorite_text_size).value.sp,
                lineHeight = dimensionResource(R.dimen.home_favorite_line_height).value.sp,
            )
        }
        Row(
            modifier = Modifier
                .matchParentSize()
                .padding(
                    start = dimensionResource(R.dimen.home_favorite_bar_item_inset),
                    end = if (editMode) {
                        dimensionResource(R.dimen.home_favorite_bar_drag_target_width)
                    } else {
                        dimensionResource(R.dimen.home_favorite_bar_item_inset)
                    },
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (entry == null) {
                Icon(
                    painter = painterResource(R.drawable.ic_inventory_error),
                    contentDescription = null,
                    modifier = Modifier.size(iconSize),
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            } else {
                val bitmap = entry.iconBitmap?.asImageBitmap() ?: remember(entry.icon, iconPixels) {
                    entry.icon.toBitmap(iconPixels, iconPixels).asImageBitmap()
                }
                Image(bitmap = bitmap, contentDescription = null, modifier = Modifier.size(iconSize))
            }
            Spacer(
                Modifier.width(
                    dimensionResource(R.dimen.home_favorite_bar_icon_label_gap),
                ),
            )
            Text(
                text = displayText,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = dimensionResource(R.dimen.home_favorite_text_size).value.sp,
                lineHeight = dimensionResource(R.dimen.home_favorite_line_height).value.sp,
            )
        }
        if (editMode) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(dimensionResource(R.dimen.home_favorite_bar_remove_target_size))
                    .clickable(
                        interactionSource = removeInteractionSource,
                        indication = null,
                        role = Role.Button,
                        onClick = onRemoveFavorite,
                    )
                    .testTag("remove_favorite_bar_item"),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.home_favorite_bar_remove_target_size))
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.error),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = stringResource(R.string.remove_favorite_item),
                        modifier = Modifier.size(
                            dimensionResource(R.dimen.home_favorite_bar_remove_icon_size),
                        ),
                        tint = colorResource(R.color.home_favorite_remove_icon),
                    )
                }
            }
            Icon(
                painter = painterResource(R.drawable.ic_drag_handle),
                contentDescription = stringResource(R.string.favorite_reorder_handle),
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .width(dimensionResource(R.dimen.home_favorite_bar_drag_target_width))
                    .fillMaxHeight()
                    .pointerInput(entry?.identity) {
                        detectDragGestures(
                            onDragStart = { onDragStart(itemOriginInWindow, itemSize) },
                            onDragEnd = onDragEnd,
                            onDragCancel = onDragCancel,
                            onDrag = { change, amount ->
                                change.consume()
                                onDrag(amount)
                            },
                        )
                    }
                    .padding(
                        (
                            dimensionResource(R.dimen.home_favorite_bar_drag_target_width) -
                                dimensionResource(R.dimen.home_reorder_handle_size)
                            ) / 2,
                    )
                    .testTag("favorite_bar_reorder_handle"),
            )
        }
    }
}

@Composable
private fun HomeFavoriteProvisionalList(
    modifier: Modifier,
    onClick: () -> Unit,
    testTag: String,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .editSurface(enabled = true),
    ) {
        HomeFavoriteAddControl(
            onClick = onClick,
            testTag = testTag,
            labelRes = R.string.new_favorite_list,
        )
    }
}

    /**
     * Geometry a composed favorite row contributes to its group's drag gesture:
     * the bounds of its drag handle in window coordinates, and the row's own
     * origin and size, which anchor the preview and give the handle's hot zone
     * its height.
     */
    private data class FavoriteDragAnchor(
        val identity: LaunchableIdentity,
        val handleBoundsInWindow: Rect = Rect.Zero,
        val rowOriginInWindow: Offset = Offset.Zero,
        val rowSize: IntSize = IntSize.Zero,
    )

    /**
     * The hot zone a drag handle accepts a press in: the handle's own horizontal
     * extent, but the full height of its row, so a taller row keeps a hot zone
     * as tall as the row it moves.
     */
    private fun FavoriteDragAnchor.handleHitZoneInWindow(): Rect {
        if (handleBoundsInWindow == Rect.Zero || rowSize.height == 0) return handleBoundsInWindow
        return Rect(
            left = handleBoundsInWindow.left,
            top = rowOriginInWindow.y,
            right = handleBoundsInWindow.right,
            bottom = rowOriginInWindow.y + rowSize.height,
        )
    }

    /**
     * Runs favorite drags for one group. The gesture belongs to the group
     * instead of the dragged row, because a cross-group exchange or an edge
     * scroll disposes that row and would cancel a row-owned gesture in the
     * middle of the drag. A press that misses every drag handle is left
     * untouched so the group keeps scrolling; once a drag owns the pointer its
     * movement is consumed in the initial pass, which keeps the group from
     * scrolling with it.
     */
    private suspend fun PointerInputScope.detectFavoriteDrag(
        anchorAt: (Offset) -> FavoriteDragAnchor?,
        onDragStart: (FavoriteDragAnchor, Offset) -> Unit,
        onDrag: (Offset) -> Unit,
        onDragEnd: () -> Unit,
        onDragCancel: () -> Unit,
    ) {
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            val anchor = anchorAt(down.position) ?: return@awaitEachGesture
            var dragging = false
            var beforeSlop = Offset.Zero
            var cancelled = false
            try {
                while (true) {
                    val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                    if (event.changes.any { it.id != down.id }) {
                        cancelled = true
                        break
                    }
                    val change = event.changes.firstOrNull { it.id == down.id }
                    if (change == null) {
                        cancelled = true
                        break
                    }
                    val movement = change.positionChangeIgnoreConsumed()
                    if (change.changedToUpIgnoreConsumed()) {
                        if (dragging && movement != Offset.Zero) {
                            change.consume()
                            onDrag(movement)
                        }
                        break
                    }
                    if (dragging) {
                        change.consume()
                        onDrag(movement)
                        continue
                    }
                    beforeSlop += movement
                    if (beforeSlop.getDistance() < viewConfiguration.touchSlop) continue
                    change.consume()
                    dragging = true
                    onDragStart(anchor, change.position)
                }
                if (dragging) {
                    dragging = false
                    if (cancelled) {
                        onDragCancel()
                    } else {
                        onDragEnd()
                    }
                }
            } finally {
                if (dragging) {
                    dragging = false
                    onDragCancel()
                }
            }
        }
    }

    private suspend fun PointerInputScope.detectReorderDrag(
        onPressChanged: (Boolean) -> Unit,
        onDragStart: (Offset) -> Unit,
        onDrag: (Offset) -> Unit,
        onDragEnd: () -> Unit,
        onDragCancel: () -> Unit,
    ) {
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            onPressChanged(true)
            var dragging = false
            var beforeSlop = Offset.Zero
            var cancelled = false
            try {
                while (true) {
                    val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                    if (event.changes.any { it.id != down.id }) {
                        cancelled = true
                        break
                    }
                    val change = event.changes.firstOrNull { it.id == down.id }
                    if (change == null) {
                        cancelled = true
                        break
                    }
                    val movement = change.positionChangeIgnoreConsumed()
                    if (change.changedToUpIgnoreConsumed()) {
                        if (dragging && movement != Offset.Zero) {
                            change.consume()
                            onDrag(movement)
                        }
                        break
                    }
                    if (dragging) {
                        change.consume()
                        onDrag(movement)
                        continue
                    }
                    beforeSlop += movement
                    if (beforeSlop.getDistance() < viewConfiguration.touchSlop) continue
                    change.consume()
                    dragging = true
                    onDragStart(down.position)
                    onDrag(beforeSlop)
                }
                if (dragging) {
                    dragging = false
                    if (cancelled) onDragCancel() else onDragEnd()
                }
            } finally {
                onPressChanged(false)
                if (dragging) onDragCancel()
            }
        }
    }
    @Composable
    private fun HomeFavoriteMessage(
        message: String,
        showProgress: Boolean,
        onRetry: (() -> Unit)?,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (showProgress) CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
            if (!showProgress) {
                Icon(
                    painter = painterResource(R.drawable.ic_inventory_error),
                    contentDescription = null,
                    modifier = Modifier.size(dimensionResource(R.dimen.home_favorite_error_icon_size)),
                )
            }
            Text(text = message, color = MaterialTheme.colorScheme.onBackground)
            onRetry?.let { retry ->
                TextButton(onClick = retry) { Text(stringResource(R.string.retry)) }
            }
        }
    }

    @Composable
    private fun HomeFavoriteComposition(
        verticalLists: List<FavoriteContainer>,
        availabilityByIdentity: Map<LaunchableIdentity, FavoriteAvailability>,
        favoriteListState: LazyListState,
        favoriteNestedScrollConnection: NestedScrollConnection?,
        companionFavoriteListState: LazyListState,
        companionFavoriteNestedScrollConnection: NestedScrollConnection?,
        onLaunchFavorite: (FavoriteAvailability) -> Unit,
        onLongPressFavorite: (LaunchableEntry) -> Unit,
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val listStates = remember {
                mutableMapOf<String, LazyListState>()
            }
            val contentHeight = verticalLists
                .maxOf { container ->
                    dimensionResource(container.listSize.rowHeightResource()) *
                            container.identities.size
                }
                .coerceAtMost(maxHeight)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(contentHeight),
                horizontalArrangement = Arrangement.spacedBy(
                    dimensionResource(R.dimen.home_favorite_group_spacing),
                ),
            ) {
                verticalLists.forEachIndexed { index, container ->
                    val listState = listStates.getOrPut(container.id) {
                        when (index) {
                            0 -> favoriteListState
                            1 -> companionFavoriteListState
                            else -> LazyListState()
                        }
                    }
                    val nestedScrollConnection = when (index) {
                        0 -> favoriteNestedScrollConnection
                        1 -> companionFavoriteNestedScrollConnection
                        else -> null
                    }
                    HomeFavoriteList(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        identities = container.identities,
                        availabilityByIdentity = availabilityByIdentity,
                        listState = listState,
                        nestedScrollConnection = nestedScrollConnection,
                        editMode = false,
                        compact = false,
                        listSize = container.listSize,
                        draggedIdentity = null,
                        exchangeTargetIdentity = null,
                        insertionBoundaryIndex = null,
                        onBoundsInWindow = {},
                        onLaunchFavorite = onLaunchFavorite,
                        onLongPressFavorite = onLongPressFavorite,
                        onDragStart = { _, _, _, _ -> },
                        onDrag = {},
                        onDragEnd = {},
                        onDragCancel = {},
                        testTag = "home_favorite_list_$index",
                    )
                }
            }
        }
    }

    @Composable
    @OptIn(ExperimentalFoundationApi::class)
    private fun HomeFavoriteRow(
        modifier: Modifier,
        availability: FavoriteAvailability,
        onClick: () -> Unit,
        onLongClick: () -> Unit,
        onRemoveFavorite: () -> Unit = {},
        editMode: Boolean,
        compact: Boolean,
        listSize: FavoriteListSize? = null,
        exchangeHighlight: Boolean,
        onRowBoundsInWindow: (Offset, IntSize) -> Unit,
        onHandleBoundsInWindow: (Rect) -> Unit,
    ) {
        val entry = availability.presentationEntry
        val iconSize = dimensionResource(
            listSize?.iconSizeResource()
                ?: if (compact) R.dimen.home_companion_favorite_icon_size
                else R.dimen.home_favorite_icon_size,
        )
        val disabledAlpha = integerResource(R.integer.disabled_content_alpha_percent) / 100f
        val iconPixels = with(LocalDensity.current) { iconSize.roundToPx() }
        val interactionSource = remember(entry?.identity) { MutableInteractionSource() }
        val hapticFeedback = LocalHapticFeedback.current
        val handleTargetSize = dimensionResource(R.dimen.home_reorder_handle_target_size)
        val removeTargetSize = dimensionResource(
            R.dimen.home_favorite_bar_remove_target_size,
        )
        val removeIconSize = dimensionResource(R.dimen.home_favorite_bar_remove_icon_size)
        val iconStartMargin = dimensionResource(R.dimen.home_favorite_list_icon_start_margin)
        val removeInteractionSource = remember(entry?.identity) { MutableInteractionSource() }
        Box(
            modifier = modifier
                .fillMaxWidth()
                .then(
                    if (listSize == null) {
                        Modifier.heightIn(
                            min = if (compact) {
                                dimensionResource(R.dimen.home_companion_favorite_row_min_height)
                            } else {
                                dimensionResource(R.dimen.home_favorite_row_min_height)
                            },
                        )
                    } else {
                        Modifier.height(dimensionResource(listSize.rowHeightResource()))
                    },
                )
                .onGloballyPositioned {
                    onRowBoundsInWindow(it.positionInWindow(), it.size)
                }
                .then(
                    // A cross-group exchange marks its target favorite with a border; a group gap keeps
                    // no border because in-group gaps never accept a placement.
                    if (!exchangeHighlight) {
                        Modifier
                    } else {
                        Modifier.border(
                            width = dimensionResource(R.dimen.home_favorite_exchange_border_width),
                            color = colorResource(R.color.home_favorite_exchange_border),
                            shape = RoundedCornerShape(
                                dimensionResource(R.dimen.home_favorite_exchange_border_radius),
                            ),
                        )
                    },
                )
                .combinedClickable(
                    enabled = !editMode,
                    interactionSource = interactionSource,
                    indication = if (editMode) {
                        null
                    } else {
                        ripple(color = colorResource(R.color.home_favorite_ripple))
                    },
                    role = Role.Button,
                    onClick = onClick,
                    onLongClick = {
                        if (entry != null) {
                            hapticFeedback.performHapticFeedback(
                                androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress,
                            )
                            onLongClick()
                        }
                    },
                )
                .alpha(if (availability is FavoriteAvailability.Available) 1f else disabledAlpha)
                .testTag("home_favorite_row"),
        ) {
            Row(
                modifier = Modifier
                    .matchParentSize()
                    .padding(
                        start = iconStartMargin,
                        end = if (editMode) handleTargetSize else 0.dp,
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
            Box(
                modifier = Modifier.size(iconSize),
                contentAlignment = Alignment.Center,
            ) {
                if (entry == null) {
                    Icon(
                        painter = painterResource(R.drawable.ic_inventory_error),
                        contentDescription = null,
                        modifier = Modifier.size(iconSize),
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                } else {
                    val bitmap = entry.iconBitmap?.asImageBitmap()
                        ?: remember(entry.icon, iconPixels) {
                            entry.icon.toBitmap(iconPixels, iconPixels).asImageBitmap()
                        }
                    Image(
                        bitmap = bitmap,
                        contentDescription = null,
                        modifier = Modifier.size(iconSize),
                    )
                }
            }
            Spacer(Modifier.width(dimensionResource(R.dimen.home_favorite_icon_label_gap)))
            val displayText = when (availability) {
                is FavoriteAvailability.Available -> availability.entry.label
                is FavoriteAvailability.Disabled -> entry?.let {
                    stringResource(R.string.favorite_disabled_format, it.label)
                } ?: stringResource(R.string.favorite_application_disabled)

                is FavoriteAvailability.TemporarilyUnavailable,
                is FavoriteAvailability.Unknown,
                    -> entry?.let {
                    stringResource(R.string.favorite_unavailable_format, it.label)
                } ?: stringResource(R.string.favorite_application_unavailable)

                FavoriteAvailability.ConfirmedRemoved ->
                    stringResource(R.string.favorite_application_unavailable)
            }
            Text(
                text = displayText,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = dimensionResource(
                    listSize?.textSizeResource()
                        ?: if (compact) R.dimen.home_companion_favorite_text_size
                        else R.dimen.home_favorite_text_size,
                ).value.sp,
                lineHeight = dimensionResource(
                    listSize?.lineHeightResource()
                        ?: if (compact) R.dimen.home_companion_favorite_line_height
                        else R.dimen.home_favorite_line_height,
                ).value.sp,
            )
            }
            if (editMode) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .size(removeTargetSize)
                        .clickable(
                            interactionSource = removeInteractionSource,
                            indication = null,
                            role = Role.Button,
                            onClick = onRemoveFavorite,
                        )
                        .testTag("remove_favorite_item"),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(removeTargetSize)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.error),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_close),
                            contentDescription = stringResource(R.string.remove_favorite_item),
                            modifier = Modifier.size(removeIconSize),
                            tint = colorResource(R.color.home_favorite_remove_icon),
                        )
                    }
                }
                Icon(
                    painter = painterResource(R.drawable.ic_drag_handle),
                    contentDescription = stringResource(R.string.favorite_reorder_handle),
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .width(handleTargetSize)
                        .fillMaxHeight()
                        .onGloballyPositioned {
                            onHandleBoundsInWindow(
                                Rect(offset = it.positionInWindow(), size = it.size.toSize()),
                            )
                        }
                        .padding(
                            (
                                handleTargetSize -
                                    dimensionResource(R.dimen.home_reorder_handle_size)
                                ) / 2,
                        )
                        .testTag("favorite_reorder_handle"),
                )
            }
        }
    }

    @Composable
    private fun Modifier.editSurface(enabled: Boolean): Modifier = if (!enabled) this else {
        background(
            colorResource(R.color.home_edit_surface),
            RoundedCornerShape(dimensionResource(R.dimen.home_edit_surface_radius)),
        )
    }

    private fun LaunchableIdentity.stableKey(): String =
        "$profileSerialNumber:${componentName.flattenToString()}"

    private fun FavoriteListSize.iconSizeResource(): Int = when (this) {
        FavoriteListSize.Large -> R.dimen.home_favorite_large_icon_size
        FavoriteListSize.Medium -> R.dimen.home_favorite_icon_size
        FavoriteListSize.Small -> R.dimen.home_companion_favorite_icon_size
    }

    private fun FavoriteListSize.rowHeightResource(): Int = when (this) {
        FavoriteListSize.Large -> R.dimen.home_favorite_large_row_min_height
        FavoriteListSize.Medium -> R.dimen.home_favorite_row_min_height
        FavoriteListSize.Small -> R.dimen.home_companion_favorite_row_min_height
    }

    private fun FavoriteListSize.textSizeResource(): Int = when (this) {
        FavoriteListSize.Large -> R.dimen.home_favorite_large_text_size
        FavoriteListSize.Medium -> R.dimen.home_favorite_text_size
        FavoriteListSize.Small -> R.dimen.home_companion_favorite_text_size
    }

    private fun FavoriteListSize.lineHeightResource(): Int = when (this) {
        FavoriteListSize.Large -> R.dimen.home_favorite_large_line_height
        FavoriteListSize.Medium -> R.dimen.home_favorite_line_height
        FavoriteListSize.Small -> R.dimen.home_companion_favorite_line_height
    }

    private fun Context.launchClockDestination() {
        val alarmIntent = Intent(AlarmClock.ACTION_SHOW_ALARMS)
        val clockMainIntent = try {
            packageManager
                .resolveDefaultActivity(alarmIntent)
                ?.activityInfo
                ?.packageName
                ?.let { packageManager.getLaunchIntentForPackage(it) }
        } catch (_: SecurityException) {
            null
        }

        launchPlatformDestination(
            intent = clockMainIntent ?: alarmIntent,
            failureMessage = R.string.clock_unavailable,
        )
    }

    private fun PackageManager.resolveDefaultActivity(intent: Intent) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            resolveActivity(
                intent,
                PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong()),
            )
        } else {
            @Suppress("DEPRECATION")
            resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        }

    private fun Context.launchPlatformDestination(
        intent: Intent,
        @StringRes failureMessage: Int,
    ) {
        try {
            startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, failureMessage, Toast.LENGTH_SHORT).show()
        } catch (_: SecurityException) {
            Toast.makeText(this, failureMessage, Toast.LENGTH_SHORT).show()
        }
    }
