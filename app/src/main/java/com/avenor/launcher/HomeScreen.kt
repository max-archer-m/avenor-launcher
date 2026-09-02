package com.avenor.launcher

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventTimeoutCancellationException
import androidx.compose.ui.input.pointer.PointerInputChange
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
import java.util.UUID
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.graphics.drawable.toBitmap
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.milliseconds
import com.avenor.launcher.ui.home.components.HomeBasicInformation
import com.avenor.launcher.ui.home.components.homeEditSurface


@Composable
internal fun HomeScreen(
    favoriteState: FavoriteReadState = FavoriteReadState.Readable(emptyList()),
    favoriteAvailability: Map<LaunchableIdentity, FavoriteAvailability> = emptyMap(),
    favoriteListState: LazyListState = rememberLazyListState(),
    favoriteNestedScrollConnection: NestedScrollConnection? = null,
    companionFavoriteListState: LazyListState = rememberLazyListState(),
    companionFavoriteNestedScrollConnection: NestedScrollConnection? = null,
    editMode: Boolean = false,
    stylePanelExpanded: Boolean = false,
    selectedModuleId: String? = null,
    onRetryFavorites: () -> Unit = {},
    onRequestEditMode: () -> Unit = {},
    onStylePanelExpandedChange: (Boolean) -> Unit = {},
    onSelectModule: (String) -> Unit = {},
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
    onCommitModuleOrder: suspend (List<String>) -> Boolean = { false },
    accessibilityLockController: AccessibilityLockController = EmptyAccessibilityLockController,
) {
    val context = LocalContext.current
    var dragSession by remember { mutableStateOf<FavoriteDragSession?>(null) }
    var favoriteBarDragSession by remember {
        mutableStateOf<FavoriteBarDragSession?>(null)
    }
    var applicationDragTargetSession by remember {
        mutableStateOf<ApplicationDragTargetSession?>(null)
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
    val applicationContainerBoundsInWindow = remember {
        mutableStateMapOf<String, Rect>()
    }
    val applicationContainerDescriptors = remember {
        mutableStateMapOf<String, ApplicationDragContainerDescriptor>()
    }
    val applicationItemBoundsInWindow = remember {
        mutableStateMapOf<String, Rect>()
    }
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
    val editTransaction = remember { HomeEditTransaction() }
    var editMutationJob by remember { mutableStateOf<Job?>(null) }
    var moduleDragSession by remember { mutableStateOf<ModuleDragSession?>(null) }
    val moduleBoundsInWindow = remember { mutableStateMapOf<String, Rect>() }
    var moduleListBoundsInWindow by remember { mutableStateOf(Rect.Zero) }
    val currentFavoriteState by rememberUpdatedState(favoriteState)
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
    val edgeScrollStartDelayMillis =
        integerResource(R.integer.home_favorite_edge_scroll_start_delay_ms).toLong()
    val hapticFeedback = LocalHapticFeedback.current
    val undoLabel = stringResource(R.string.undo)
    val favoriteListSizeMessage = stringResource(R.string.favorite_list_size)
    val favoriteListRemovedMessage = stringResource(R.string.favorite_list_removed)
    val favoriteBarRemovedMessage = stringResource(R.string.favorite_bar_removed)
    val favoriteRemovedMessage = stringResource(R.string.favorite_removed)
    val undoUnavailableMessage = stringResource(R.string.favorite_undo_unavailable)
    val moduleStyleSaveFailureMessage = stringResource(
        R.string.unable_to_save_module_style,
    )
    val moduleOrderSaveFailureMessage = stringResource(
        R.string.unable_to_save_module_order,
    )
    fun cancelActiveDragSessions() {
        dragSession = null
        favoriteBarDragSession = null
        applicationDragTargetSession = null
        favoriteBarContainerDragSession = null
        listDragSession = null
        moduleDragSession = null
    }
    val advanceDrag: (Offset) -> Unit = { amount ->
        applicationDragTargetSession = applicationDragTargetSession?.advanced(
            amount = amount,
            containerDescriptors = applicationContainerDescriptors,
            itemBoundsInWindow = applicationItemBoundsInWindow,
        )
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
        advanced?.let { current ->
            if (current.feedbackChangedFrom(previous)) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
            }
        }
    }
    val applicationEdgeScroll = applicationDragTargetSession?.edgeScroll(
        descriptors = applicationContainerDescriptors,
        bandPx = edgeScrollBandPx,
        primaryListState = favoriteListState,
        companionListState = companionFavoriteListState,
        editListStates = editListStates,
        favoriteBarStates = favoriteBarStates,
    )

    LaunchedEffect(editMode) {
        if (!editMode) {
            editTransaction.leave()
            cancelActiveDragSessions()
            editMutationJob?.cancel()
            snackbarHostState.currentSnackbarData?.dismiss()
        } else {
            editTransaction.enter(
                (currentFavoriteState as? FavoriteReadState.Readable)?.aggregate,
            )
        }
    }

    fun startModuleDrag(
        module: OrderedFavoriteModule,
        modules: List<OrderedFavoriteModule>,
        touchInWindow: Offset,
    ): Boolean {
        if (modules.size < 2 || editMutationJob?.isActive == true) return false
        val sourceIndex = modules.indexOfFirst { it.id == module.id }
        val sourceBounds = moduleBoundsInWindow[module.id] ?: return false
        if (sourceIndex < 0) return false
        moduleDragSession = ModuleDragSession(
            sourceModule = module,
            sourceSelected = module.id == selectedModuleId,
            sourceAvailability = module.identities.associateWith { identity ->
                favoriteAvailability[identity] ?: FavoriteAvailability.Unknown(null)
            },
            initialModules = modules,
            remainingModules = modules.filterNot { it.id == module.id },
            insertionIndex = sourceIndex,
            originInWindow = sourceBounds.topLeft,
            size = IntSize(
                sourceBounds.width.roundToInt(),
                sourceBounds.height.roundToInt(),
            ),
            touchStartInWindow = touchInWindow,
        )
        return true
    }

    fun advanceModuleDrag(amount: Offset) {
        val previous = moduleDragSession ?: return
        moduleDragSession = previous.advanced(
            amount = amount,
            listBoundsInWindow = moduleListBoundsInWindow,
            moduleBoundsInWindow = moduleBoundsInWindow,
        )
    }

    fun enqueueEditMutation(mutation: suspend () -> Unit) {
        val previousJob = editMutationJob
        val mutationJob = editScope.launch(start = CoroutineStart.LAZY) {
            previousJob?.join()
            mutation()
        }
        editMutationJob = mutationJob
        mutationJob.invokeOnCompletion {
            editScope.launch {
                if (editMutationJob === mutationJob) editMutationJob = null
            }
        }
        mutationJob.start()
    }

    fun finishModuleDrag() {
        val session = moduleDragSession ?: return
        moduleDragSession = null
        val reordered = session.completedModules() ?: return

        val editSession = editTransaction.sessionId
        editTransaction.beginModuleOrder(reordered)
        enqueueEditMutation moduleOrderMutation@{
            val persisted = onCommitModuleOrder(reordered.map { it.id })
            if (editSession != editTransaction.sessionId || !editMode) {
                return@moduleOrderMutation
            }
            editTransaction.completeModuleOrder()
            if (!persisted) {
                Toast.makeText(
                    context,
                    moduleOrderSaveFailureMessage,
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    val moduleEdgeScrollDirection = moduleDragSession?.let { session ->
        val bounds = moduleListBoundsInWindow
        val band = edgeScrollBandPx.coerceAtMost(bounds.height / 2f)
        when {
            bounds == Rect.Zero || !bounds.contains(session.touchInWindow) -> 0
            session.touchInWindow.y < bounds.top + band -> -1
            session.touchInWindow.y > bounds.bottom - band -> 1
            else -> 0
        }
    } ?: 0

    LaunchedEffect(moduleDragSession?.sourceModule?.id, moduleEdgeScrollDirection) {
        if (moduleEdgeScrollDirection == 0) return@LaunchedEffect
        delay(duration = edgeScrollStartDelayMillis.milliseconds)
        var previousFrameNanos = withFrameNanos { it }
        while (moduleDragSession != null) {
            val session = moduleDragSession ?: break
            val bounds = moduleListBoundsInWindow
            val band = edgeScrollBandPx.coerceAtMost(bounds.height / 2f)
            val direction = when {
                !bounds.contains(session.touchInWindow) -> 0
                session.touchInWindow.y < bounds.top + band -> -1
                session.touchInWindow.y > bounds.bottom - band -> 1
                else -> 0
            }
            if (direction == 0 || direction != moduleEdgeScrollDirection) break
            val frameNanos = withFrameNanos { it }
            val elapsedSeconds = (frameNanos - previousFrameNanos) / 1_000_000_000f
            previousFrameNanos = frameNanos
            val edgeDistance = if (direction < 0) {
                (session.touchInWindow.y - bounds.top).coerceIn(0f, band)
            } else {
                (bounds.bottom - session.touchInWindow.y).coerceIn(0f, band)
            }
            val proximity = if (band == 0f) 0f else 1f - (edgeDistance / band)
            val consumed = favoriteListState.scrollBy(
                direction * edgeScrollSpeedPxPerSecond * proximity * elapsedSeconds,
            )
            if (consumed == 0f) break
            advanceModuleDrag(Offset.Zero)
        }
    }

    LaunchedEffect(stylePanelExpanded) {
        if (!stylePanelExpanded) moduleDragSession = null
    }

    LaunchedEffect(favoriteState, editMode) {
        val readable = favoriteState as? FavoriteReadState.Readable
            ?: return@LaunchedEffect
        val committed = editTransaction.committedAggregate ?: return@LaunchedEffect
        if (editMode &&
            readable.aggregate != committed &&
            editMutationJob?.isActive != true
        ) {
            cancelActiveDragSessions()
            if (editTransaction.reconcileExternal(readable.aggregate)) {
                snackbarHostState.currentSnackbarData?.dismiss()
            }
        }
    }

    fun commitEditAggregate(
        transform: (FavoriteAggregate) -> FavoriteAggregate,
        message: String = "",
        recordUndo: Boolean = false,
        onCommitted: () -> Unit = {},
        onFailed: () -> Unit = {
            Toast.makeText(
                context,
                R.string.favorite_reorder_unavailable,
                Toast.LENGTH_SHORT,
            ).show()
        },
) {
        if (favoriteState !is FavoriteReadState.Readable) return
        val session = editTransaction.sessionId
        enqueueEditMutation mutation@{
            val base = editTransaction.baseAggregate(
                (currentFavoriteState as? FavoriteReadState.Readable)?.aggregate,
            )
                ?: return@mutation
            val updated = transform(base)
            if (!isValidAggregate(updated)) return@mutation
            editTransaction.beginMutation(updated)
            val persisted = onCommitFavoriteComposition(transform)
            if (session != editTransaction.sessionId || !editMode) return@mutation
            if (persisted == null) {
                editTransaction.discardPending(updated)
                withFrameNanos { }
                if (session != editTransaction.sessionId || !editMode) return@mutation
                editTransaction.restoreCommitted(
                    (currentFavoriteState as? FavoriteReadState.Readable)?.aggregate,
                )
                cancelActiveDragSessions()
                onFailed()
                return@mutation
            }
            editTransaction.completeMutation(persisted)
            onCommitted()
            if (!recordUndo) {
                editTransaction.clearUndo()
                snackbarHostState.currentSnackbarData?.dismiss()
                return@mutation
            }
            val sequence = editTransaction.recordUndo(base)
            snackbarHostState.currentSnackbarData?.dismiss()
            editScope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = undoLabel,
                    duration = SnackbarDuration.Long,
                )
                if (result == SnackbarResult.ActionPerformed &&
                    session == editTransaction.sessionId
                ) {
                    val snapshot = editTransaction.consumeUndo(sequence) ?: return@launch
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

    fun commitVerticalModuleStyle(
        moduleId: String,
        transform: (FavoriteContainer) -> FavoriteContainer,
    ) {
        if (editMutationJob?.isActive == true) return
        commitEditAggregate(
            transform = { aggregate ->
                aggregate.updateVerticalList(moduleId, transform)
            },
            onFailed = {
                Toast.makeText(
                    context,
                    moduleStyleSaveFailureMessage,
                    Toast.LENGTH_SHORT,
                ).show()
            },
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

    fun commitCrossContainerDrag(targetSession: ApplicationDragTargetSession): Boolean {
        val targetKey = targetSession.targetContainerKey ?: return false
        val targetType = targetSession.targetContainerType ?: return false
        if (targetKey == targetSession.sourceContainerKey) return false
        val sourceId = targetSession.sourceContainerKey.substringAfter(':')
        val targetId = targetKey.substringAfter(':')
        val provisionalTarget = targetKey.startsWith(PROVISIONAL_VERTICAL_LIST_DRAG_KEY_PREFIX) ||
            targetKey == PROVISIONAL_FAVORITE_BAR_DRAG_KEY
        val targetIdentity = targetSession.targetIdentity
        val targetIndex = targetSession.targetIndex
        val provisionalContainerId = if (provisionalTarget) UUID.randomUUID().toString() else null
        // The pointer has already been released when this function is called. Remove the active
        // target before starting the asynchronous save so edge scrolling cannot continue while the
        // aggregate mutation is pending.
        applicationDragTargetSession = null
        commitEditAggregate(
            transform = transform@{ aggregate ->
                if (provisionalTarget) {
                    val source = aggregate.containerForDragKey(targetSession.sourceContainerKey)
                        ?: return@transform aggregate
                    if (targetIdentity != null) return@transform aggregate
                    val movedAggregate = aggregate.removeIdentityFromContainer(
                        source.id,
                        targetSession.sourceIdentity,
                    )
                    val newContainer = FavoriteContainer(
                        id = provisionalContainerId ?: return@transform aggregate,
                        type = targetType,
                        identities = listOf(targetSession.sourceIdentity),
                    )
                    return@transform if (targetType ==
                        FavoriteContainerType.VerticalList
                    ) {
                        movedAggregate.copy(
                            verticalLists = movedAggregate.verticalLists + newContainer,
                        )
                    } else {
                        movedAggregate.copy(
                            favoriteBars = movedAggregate.favoriteBars + newContainer,
                        )
                    }
                }
                if (targetId.isBlank()) return@transform aggregate
                aggregate.moveFavorite(
                    sourceContainerId = sourceId,
                    targetContainerId = targetId,
                    identity = targetSession.sourceIdentity,
                    targetIndex = targetIndex,
                    exchangeIdentity = targetIdentity,
                )
            },
            onCommitted = {
                dragSession = null
                favoriteBarDragSession = null
                applicationDragTargetSession = null
            },
            onFailed = {
                dragSession = null
                favoriteBarDragSession = null
                applicationDragTargetSession = null
            },
        )
        return true
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
        applicationDragTargetSession = applicationDragTargetSession?.advanced(
            amount = amount,
            containerDescriptors = applicationContainerDescriptors,
            itemBoundsInWindow = applicationItemBoundsInWindow,
        )
        val session = favoriteBarDragSession ?: return
        val targetSession = applicationDragTargetSession
        val sourceBounds = targetSession?.let { active ->
            applicationContainerBoundsInWindow[active.sourceContainerKey]
        }
        if (targetSession == null || sourceBounds?.contains(targetSession.touchInWindow) != true) {
            favoriteBarDragSession = session.copy(
                delta = session.delta + amount,
                residualX = 0f,
            )
            return
        }
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
        val primaryViewport =
            favoriteListState.firstVisibleItemIndex to
                favoriteListState.firstVisibleItemScrollOffset
        val companionViewport =
            companionFavoriteListState.firstVisibleItemIndex to
                companionFavoriteListState.firstVisibleItemScrollOffset
        val previous = dragSession
        advanceDrag(amount)
        val advanced = dragSession
        if (previous == null || advanced == null) return
        val primaryOrderChanged = previous.displayedPrimary != advanced.displayedPrimary
        val companionOrderChanged = previous.displayedCompanion != advanced.displayedCompanion
        val orderChanged = primaryOrderChanged || companionOrderChanged
        if (!orderChanged) return

        // Stable item keys normally anchor the first visible item after a reorder. During a drag,
        // keep the numeric viewport instead so exchanging the first two rows cannot move the list.
        if (primaryOrderChanged) {
            favoriteListState.requestScrollToItem(primaryViewport.first, primaryViewport.second)
        }
        if (companionOrderChanged) {
            companionFavoriteListState.requestScrollToItem(
                companionViewport.first,
                companionViewport.second,
            )
        }

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

    LaunchedEffect(
        applicationEdgeScroll?.containerKey,
        applicationEdgeScroll?.axis,
        applicationEdgeScroll?.forward,
    ) {
        val initialRequest = applicationEdgeScroll ?: return@LaunchedEffect
        delay(duration = edgeScrollStartDelayMillis.milliseconds)
        var previousFrame = 0L
        while (true) {
            val request = applicationDragTargetSession?.edgeScroll(
                descriptors = applicationContainerDescriptors,
                bandPx = edgeScrollBandPx,
                primaryListState = favoriteListState,
                companionListState = companionFavoriteListState,
                editListStates = editListStates,
                favoriteBarStates = favoriteBarStates,
            ) ?: break
            if (request.containerKey != initialRequest.containerKey ||
                request.axis != initialRequest.axis ||
                request.forward != initialRequest.forward
            ) {
                break
            }
            val state = when (request.axis) {
                ApplicationDragAxis.Vertical -> when {
                    request.containerKey == PROVISIONAL_VERTICAL_LIST_DRAG_KEY_0 ||
                        request.containerKey == PROVISIONAL_VERTICAL_LIST_DRAG_KEY_1 -> null
                    else -> editListStates[request.containerKey.substringAfter(':')]
                        ?: if (request.containerKey == "vertical-list:${PRIMARY_LIST_ID}") {
                            favoriteListState
                        } else {
                            companionFavoriteListState
                        }
                }
                ApplicationDragAxis.Horizontal ->
                    favoriteBarStates[request.containerKey.substringAfter(':')]
            } ?: break
            if (previousFrame == 0L) {
                previousFrame = withFrameNanos { it }
                continue
            }
            val frame = withFrameNanos { it }
            val elapsedSeconds = (frame - previousFrame) / NANOS_PER_SECOND
            previousFrame = frame
            val distance = edgeScrollSpeedPxPerSecond *
                request.proximity.coerceIn(0f, 1f) *
                elapsedSeconds
            val consumed = state.scrollBy(
                if (request.forward) distance else -distance,
            )
            if (consumed == 0f) break
            if (dragSession != null) {
                advanceAndPersistDrag(Offset.Zero)
            } else if (favoriteBarDragSession != null) {
                advanceAndPersistFavoriteBarDrag(Offset.Zero)
            }
            if (!state.canScroll(request.forward)) break
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(dimensionResource(R.dimen.home_content_padding))
            .onGloballyPositioned { dragRootOriginInWindow = it.positionInWindow() },
    ) {
        val stylePanelMaximumHeight = (
            maxHeight -
                dimensionResource(R.dimen.home_edit_dock_height) -
                dimensionResource(R.dimen.home_style_panel_minimum_list_viewport)
            ).coerceAtLeast(0.dp)
        Column(modifier = Modifier.fillMaxSize()) {
            if (!editMode || !stylePanelExpanded) {
                HomeBasicInformation(
                    editMode = editMode,
                    accessibilityLockController = accessibilityLockController,
                    onRequestEditMode = onRequestEditMode,
                )
            }
            if (!editMode || !stylePanelExpanded) {
                Spacer(Modifier.height(dimensionResource(R.dimen.home_module_spacing)))
            }
            Box(
                modifier = Modifier
                    .weight(1f)
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
                    val orderedModules = favoriteState.orderedModules
                    val hasFavorites = orderedModules?.isNotEmpty()
                        ?: favoriteState.aggregate.identities.isNotEmpty()
                    if (!hasFavorites && editMode && orderedModules == null) {
                        HomeFavoriteProvisionalList(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onAddProvisionalFavorites,
                            testTag = "favorite_provisional_add_0",
                            applicationDropHighlight =
                                applicationDragTargetSession?.showsContainerHighlight(
                                    PROVISIONAL_VERTICAL_LIST_DRAG_KEY_0,
                                ) == true,
                            onBoundsInWindow = {
                                applicationContainerBoundsInWindow[
                                    PROVISIONAL_VERTICAL_LIST_DRAG_KEY_0
                                ] = it
                                applicationContainerDescriptors[
                                    PROVISIONAL_VERTICAL_LIST_DRAG_KEY_0
                                ] = ApplicationDragContainerDescriptor(
                                    key = PROVISIONAL_VERTICAL_LIST_DRAG_KEY_0,
                                    type = FavoriteContainerType.VerticalList,
                                    axis = ApplicationDragAxis.Vertical,
                                    bounds = it,
                                )
                            },
                            onDisposed = {
                                applicationContainerBoundsInWindow.remove(
                                    PROVISIONAL_VERTICAL_LIST_DRAG_KEY_0,
                                )
                                applicationContainerDescriptors.remove(
                                    PROVISIONAL_VERTICAL_LIST_DRAG_KEY_0,
                                )
                            },
                        )
                    } else if (!hasFavorites && !editMode) {
                        Text(
                            text = stringResource(R.string.home_empty_favorites),
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.testTag("home_favorites_empty"),
                        )
                    } else if (!editMode) {
                        HomeOrderedModuleComposition(
                            modules = orderedModules
                                ?: (
                                    favoriteState.aggregate.verticalLists.map { container ->
                                        OrderedFavoriteModule(
                                            id = container.id,
                                            type = OrderedFavoriteModuleType.Vertical,
                                            identities = container.identities,
                                        )
                                    } + favoriteState.aggregate.favoriteBars.map { container ->
                                        OrderedFavoriteModule(
                                            id = container.id,
                                            type = OrderedFavoriteModuleType.Ribbon,
                                            identities = container.identities,
                                        )
                                    }
                                ),
                            availabilityByIdentity = favoriteAvailability,
                            listState = favoriteListState,
                            nestedScrollConnection = favoriteNestedScrollConnection,
                            editMode = false,
                            selectionEnabled = false,
                            selectionInteractionEnabled = false,
                            selectedModuleId = null,
                            onSelectModule = {},
                            addEntriesEnabled = false,
                            onAddToModule = {},
                            onCreateVerticalModule = {},
                            onCreateRibbon = {},
                            onLaunchFavorite = onLaunchFavorite,
                            onLongPressFavorite = onLongPressFavorite,
                        )
                    } else if (orderedModules != null) {
                        val previewAggregate = editTransaction.previewAggregate(
                            favoriteState.aggregate,
                        )
                        val styledModules = orderedModules.withPresentationFrom(previewAggregate)
                        val displayedModules = moduleDragSession?.remainingModules
                            ?: editTransaction.pendingModuleOrder
                            ?: styledModules
                        HomeOrderedModuleComposition(
                            modules = displayedModules,
                            availabilityByIdentity = favoriteAvailability,
                            listState = favoriteListState,
                            nestedScrollConnection = null,
                            editMode = true,
                            selectionEnabled = stylePanelExpanded,
                            selectionInteractionEnabled = editMutationJob?.isActive != true &&
                                moduleDragSession == null,
                            selectionVisualEnabled = editMutationJob?.isActive != true,
                            selectedModuleId = selectedModuleId,
                            onSelectModule = onSelectModule,
                            addEntriesEnabled = editMutationJob?.isActive != true &&
                                moduleDragSession == null,
                            onAddToModule = { module ->
                                when (module.type) {
                                    OrderedFavoriteModuleType.Vertical ->
                                        onAddFavoritesToList(module.id)
                                    OrderedFavoriteModuleType.Ribbon ->
                                        onAddFavoritesToBar(module.id)
                                }
                            },
                            onCreateVerticalModule = onAddProvisionalFavorites,
                            onCreateRibbon = onAddProvisionalFavoriteBar,
                            onLaunchFavorite = {},
                            onLongPressFavorite = {},
                            moduleEdgeScrollDirection = moduleEdgeScrollDirection,
                            moduleInsertionIndex = moduleDragSession?.insertionIndex,
                            onModuleBoundsInWindow = { id, bounds ->
                                moduleBoundsInWindow[id] = bounds
                            },
                            onModuleDisposed = { id -> moduleBoundsInWindow.remove(id) },
                            onModuleListBoundsInWindow = { moduleListBoundsInWindow = it },
                            onModuleDragStart = { module, touch ->
                                startModuleDrag(module, styledModules, touch)
                            },
                            onModuleDrag = ::advanceModuleDrag,
                            onModuleDragEnd = ::finishModuleDrag,
                            onModuleDragCancel = { moduleDragSession = null },
                        )
                    } else if (editMode) {
                        val persistedEditAggregate = editTransaction.previewAggregate(
                            favoriteState.aggregate,
                        )
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
                        val applicationTarget = applicationDragTargetSession
                        // A release completes the current exchange or insertion when the touch
                        // point is inside either group; any other area restores the saved state.
                        val endDrag: () -> Unit = endDrag@{
                            val session = dragSession
                            val applicationTarget = applicationDragTargetSession
                            if (applicationTarget?.targetContainerType != null &&
                                applicationTarget.targetContainerKey !=
                                    applicationTarget.sourceContainerKey
                            ) {
                                commitCrossContainerDrag(applicationTarget)
                                return@endDrag
                            }
                            applicationDragTargetSession = null
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
                                // In-group exchanges are persisted as they happen. End the session
                                // immediately so releasing near an edge cannot re-enable the legacy
                                // edge-scroll effect during the release recomposition.
                                dragSession = null
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
                                        exchangeTargetIdentity =
                                            if (applicationTarget?.targetContainerKey ==
                                                primaryContainer.applicationDragKey() &&
                                                applicationTarget.targetMode ==
                                                ApplicationDragTargetMode.Exchange
                                            ) {
                                                applicationTarget.targetIdentity
                                            } else {
                                                dragSession?.crossGroupTarget
                                            },
                                        insertionBoundaryIndex =
                                            if (applicationTarget?.targetContainerKey ==
                                                primaryContainer.applicationDragKey() &&
                                                applicationTarget.targetMode ==
                                                ApplicationDragTargetMode.Insertion
                                            ) {
                                                applicationTarget.targetIndex
                                            } else {
                                                dragSession?.insertionBoundaryIn(companion = false)
                                            },
                                        onBoundsInWindow = {
                                            primaryListBoundsInWindow = it
                                            applicationContainerBoundsInWindow[
                                                primaryContainer.applicationDragKey()
                                            ] = it
                                            applicationContainerDescriptors[
                                                primaryContainer.applicationDragKey()
                                            ] = primaryContainer.applicationDragDescriptor(it)
                                        },
                                        applicationDropHighlight =
                                            applicationDragTargetSession?.showsContainerHighlight(
                                                primaryContainer.applicationDragKey(),
                                            ) == true,
                                        applicationDragKey =
                                            primaryContainer.applicationDragKey(),
                                        applicationDragActive =
                                            applicationDragTargetSession != null,
                                        applicationEdgeScroll = applicationEdgeScroll,
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
                                        onContainerDisposed = {
                                            applicationContainerBoundsInWindow.remove(
                                                primaryContainer.applicationDragKey(),
                                            )
                                            applicationContainerDescriptors.remove(
                                                primaryContainer.applicationDragKey(),
                                            )
                                            applicationItemBoundsInWindow.keys
                                                .filter {
                                                    it.startsWith(
                                                        "${primaryContainer.applicationDragKey()}:",
                                                    )
                                                }
                                                .forEach(applicationItemBoundsInWindow::remove)
                                        },
                                        onApplicationItemBounds = { identity, bounds ->
                                            applicationItemBoundsInWindow[
                                                "${primaryContainer.applicationDragKey()}:${identity.stableKey()}"
                                            ] = bounds
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
                                            applicationDragTargetSession =
                                                ApplicationDragTargetSession(
                                                    sourceContainerKey =
                                                        primaryContainer.applicationDragKey(),
                                                    sourceIdentity = identity,
                                                    sourceContainerType =
                                                        FavoriteContainerType.VerticalList,
                                                    sourceAxis = ApplicationDragAxis.Vertical,
                                                    touchStartInWindow = touch,
                                                )
                                        },
                                        onDrag = ::advanceAndPersistDrag,
                                        onDragEnd = endDrag,
                                        onDragCancel = {
                                            dragSession = null
                                            applicationDragTargetSession = null
                                        },
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
                                        exchangeTargetIdentity =
                                            if (applicationTarget?.targetContainerKey ==
                                                companionContainer.applicationDragKey() &&
                                                applicationTarget.targetMode ==
                                                ApplicationDragTargetMode.Exchange
                                            ) {
                                                applicationTarget.targetIdentity
                                            } else {
                                                dragSession?.crossGroupTarget
                                            },
                                        insertionBoundaryIndex =
                                            if (applicationTarget?.targetContainerKey ==
                                                companionContainer.applicationDragKey() &&
                                                applicationTarget.targetMode ==
                                                ApplicationDragTargetMode.Insertion
                                            ) {
                                                applicationTarget.targetIndex
                                            } else {
                                                dragSession?.insertionBoundaryIn(companion = true)
                                            },
                                        onBoundsInWindow = {
                                            companionListBoundsInWindow = it
                                            applicationContainerBoundsInWindow[
                                                companionContainer.applicationDragKey()
                                            ] = it
                                            applicationContainerDescriptors[
                                                companionContainer.applicationDragKey()
                                            ] = companionContainer.applicationDragDescriptor(it)
                                        },
                                        applicationDropHighlight =
                                            applicationDragTargetSession?.showsContainerHighlight(
                                                companionContainer.applicationDragKey(),
                                            ) == true,
                                        applicationDragKey =
                                            companionContainer.applicationDragKey(),
                                        applicationDragActive =
                                            applicationDragTargetSession != null,
                                        applicationEdgeScroll = applicationEdgeScroll,
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
                                        onContainerDisposed = {
                                            applicationContainerBoundsInWindow.remove(
                                                companionContainer.applicationDragKey(),
                                            )
                                            applicationContainerDescriptors.remove(
                                                companionContainer.applicationDragKey(),
                                            )
                                            applicationItemBoundsInWindow.keys
                                                .filter {
                                                    it.startsWith(
                                                        "${companionContainer.applicationDragKey()}:",
                                                    )
                                                }
                                                .forEach(applicationItemBoundsInWindow::remove)
                                        },
                                        onApplicationItemBounds = { identity, bounds ->
                                            applicationItemBoundsInWindow[
                                                "${companionContainer.applicationDragKey()}:${identity.stableKey()}"
                                            ] = bounds
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
                                            applicationDragTargetSession =
                                                ApplicationDragTargetSession(
                                                    sourceContainerKey =
                                                        companionContainer.applicationDragKey(),
                                                    sourceIdentity = identity,
                                                    sourceContainerType =
                                                        FavoriteContainerType.VerticalList,
                                                    sourceAxis = ApplicationDragAxis.Vertical,
                                                    touchStartInWindow = touch,
                                                )
                                        },
                                        onDrag = ::advanceAndPersistDrag,
                                        onDragEnd = endDrag,
                                        onDragCancel = {
                                            dragSession = null
                                            applicationDragTargetSession = null
                                        },
                                    )
                                }
                                if (companionContainer == null) {
                                    HomeFavoriteProvisionalList(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight(),
                                        onClick = onAddProvisionalFavorites,
                                        testTag = "favorite_provisional_add_1",
                                        applicationDropHighlight =
                                            applicationDragTargetSession?.showsContainerHighlight(
                                                PROVISIONAL_VERTICAL_LIST_DRAG_KEY_1,
                                            ) == true,
                                        onBoundsInWindow = {
                                            applicationContainerBoundsInWindow[
                                                PROVISIONAL_VERTICAL_LIST_DRAG_KEY_1
                                            ] = it
                                            applicationContainerDescriptors[
                                                PROVISIONAL_VERTICAL_LIST_DRAG_KEY_1
                                            ] = ApplicationDragContainerDescriptor(
                                                key = PROVISIONAL_VERTICAL_LIST_DRAG_KEY_1,
                                                type = FavoriteContainerType.VerticalList,
                                                axis = ApplicationDragAxis.Vertical,
                                                bounds = it,
                                            )
                                        },
                                        onDisposed = {
                                                applicationContainerBoundsInWindow.remove(
                                                    PROVISIONAL_VERTICAL_LIST_DRAG_KEY_1,
                                                )
                                                applicationContainerDescriptors.remove(
                                                    PROVISIONAL_VERTICAL_LIST_DRAG_KEY_1,
                                                )
                                        },
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
                    editTransaction.previewAggregate(aggregate)
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
                if (editMode &&
                    favoriteState.orderedModules == null &&
                    (renderedBars.isNotEmpty() || renderedBars.size < 5)
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
                        favoriteBarStates = favoriteBarStates,
                        favoriteBarBoundsInWindow = favoriteBarBoundsInWindow,
                        applicationContainerBoundsInWindow =
                            applicationContainerBoundsInWindow,
                        applicationContainerDescriptors =
                            applicationContainerDescriptors,
                        applicationItemBoundsInWindow = applicationItemBoundsInWindow,
                        applicationDropTargetKey =
                            applicationDragTargetSession?.targetContainerKey,
                        applicationDropTargetIdentity =
                            applicationDragTargetSession?.targetIdentity,
                        applicationDropTargetMode =
                            applicationDragTargetSession?.targetMode,
                        applicationDropTargetIndex =
                            applicationDragTargetSession?.targetIndex,
                        applicationEdgeScroll = applicationEdgeScroll,
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
                        onDragStart = { bar, identity, origin, size, touch ->
                            dragGeneration += 1
                            favoriteBarDragSession = FavoriteBarDragSession(
                                generation = dragGeneration,
                                barId = bar.id,
                                identity = identity,
                                displayedIdentities = bar.identities,
                                originInWindow = origin,
                                size = size,
                            )
                            applicationDragTargetSession = ApplicationDragTargetSession(
                                sourceContainerKey = bar.applicationDragKey(),
                                sourceIdentity = identity,
                                sourceContainerType = FavoriteContainerType.FavoriteBar,
                                sourceAxis = ApplicationDragAxis.Horizontal,
                                touchStartInWindow = touch,
                            )
                        },
                        onDrag = ::advanceAndPersistFavoriteBarDrag,
                        onDragEnd = {
                            val target = applicationDragTargetSession
                            if (target?.targetContainerType != null &&
                                target.targetContainerKey != target.sourceContainerKey
                            ) {
                                commitCrossContainerDrag(target)
                            } else {
                                favoriteBarDragSession = null
                                applicationDragTargetSession = null
                            }
                        },
                        onDragCancel = {
                            favoriteBarDragSession = null
                            applicationDragTargetSession = null
                        },
                    )
                }
            }
            if (editMode) {
                val orderedModules = (favoriteState as? FavoriteReadState.Readable)
                    ?.orderedModules
                    .orEmpty()
                val previewAggregate = editTransaction.previewAggregate(
                    (favoriteState as? FavoriteReadState.Readable)?.aggregate
                        ?: FavoriteAggregate(),
                )
                val displayedModules = orderedModules.withPresentationFrom(previewAggregate)
                val selectedModule = displayedModules.firstOrNull {
                    it.id == selectedModuleId
                }
                val styleSaving = editMutationJob?.isActive == true || moduleDragSession != null
                if (stylePanelExpanded) {
                    HomeModuleStylePanel(
                        selectedModule = selectedModule,
                        enabled = !styleSaving,
                        maximumHeight = stylePanelMaximumHeight,
                        onChangeSize = { size ->
                            selectedModule?.let { module ->
                                commitVerticalModuleStyle(module.id) {
                                    it.copy(listSize = size)
                                }
                            }
                        },
                        onChangeNamePlacement = { placement ->
                            selectedModule?.let { module ->
                                commitVerticalModuleStyle(module.id) { container ->
                                    container.copy(
                                        namePlacement = placement,
                                        itemsPerRow = if (
                                            placement == FavoriteNamePlacement.Right
                                        ) {
                                            container.itemsPerRow.coerceAtMost(2)
                                        } else {
                                            container.itemsPerRow
                                        },
                                    )
                                }
                            }
                        },
                        onChangeItemsPerRow = { count ->
                            selectedModule?.let { module ->
                                commitVerticalModuleStyle(module.id) {
                                    it.copy(itemsPerRow = count)
                                }
                            }
                        },
                    )
                }
                HomeEditDock(
                    hasFavorites = orderedModules.isNotEmpty(),
                    expanded = stylePanelExpanded,
                    onToggleExpanded = {
                        onStylePanelExpandedChange(!stylePanelExpanded)
                    },
                )
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
        moduleDragSession?.let { session ->
            HomeModuleDragPreview(
                session = session,
                rootOriginInWindow = dragRootOriginInWindow,
            )
        }
    }
}


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
internal fun HomeFavoritePreviewContent(
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
    val hapticFeedback = LocalHapticFeedback.current
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
                                        onLongPress = {
                                            hapticFeedback.performHapticFeedback(
                                                HapticFeedbackType.LongPress,
                                            )
                                        },
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
    onApplicationItemBounds: (LaunchableIdentity, Rect) -> Unit = { _, _ -> },
    applicationDropHighlight: Boolean = false,
    applicationDragKey: String? = null,
    applicationDragActive: Boolean = false,
    applicationEdgeScroll: ApplicationEdgeScroll? = null,
    onLaunchFavorite: (FavoriteAvailability) -> Unit,
    onLongPressFavorite: (LaunchableEntry) -> Unit,
    onRemoveFavorite: (LaunchableIdentity) -> Unit = {},
    listIndex: Int? = null,
    listCount: Int = 0,
    onChangeListSize: (FavoriteListSize) -> Unit = {},
    onRemoveList: () -> Unit = {},
    onAddFavorites: () -> Unit = {},
    onContainerBoundsInWindow: (Rect) -> Unit = {},
    onContainerDisposed: () -> Unit = {},
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
    DisposableEffect(Unit) {
        onDispose(onContainerDisposed)
    }
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
            .homeEditSurface(enabled = editMode)
            .then(
                if (!listExchangeHighlight && !applicationDropHighlight) {
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
                    if (applicationDragActive) {
                        Modifier
                    } else {
                        nestedScrollConnection?.let { Modifier.nestedScroll(it) } ?: Modifier
                    },
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
                .drawWithContent {
                    drawContent()
                    if (applicationEdgeScroll?.containerKey == applicationDragKey) {
                        drawRect(
                            color = insertionLineColor.copy(
                                alpha = applicationEdgeScroll?.proximity ?: 0f,
                            ),
                            topLeft = if (applicationEdgeScroll?.forward ?: false) {
                                Offset(0f, size.height - insertionLineThickness)
                            } else {
                                Offset.Zero
                            },
                            size = Size(size.width, insertionLineThickness),
                        )
                    }
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
                                onLongPress = {
                                    hapticFeedback.performHapticFeedback(
                                        HapticFeedbackType.LongPress,
                                    )
                                },
                                onDragStart = { anchor, local ->
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
            userScrollEnabled = !applicationDragActive,
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
                        onApplicationItemBounds(
                            identity,
                            Rect(offset = origin, size = size.toSize()),
                        )
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
    favoriteBarStates: MutableMap<String, LazyListState>,
    favoriteBarBoundsInWindow: MutableMap<String, Rect>,
    applicationContainerBoundsInWindow: MutableMap<String, Rect>,
    applicationContainerDescriptors:
        MutableMap<String, ApplicationDragContainerDescriptor>,
    applicationItemBoundsInWindow: MutableMap<String, Rect>,
    applicationDropTargetKey: String?,
    applicationEdgeScroll: ApplicationEdgeScroll? = null,
    applicationDropTargetIdentity: LaunchableIdentity?,
    applicationDropTargetMode: ApplicationDragTargetMode?,
    applicationDropTargetIndex: Int?,
    draggedIdentity: LaunchableIdentity?,
    draggedBarId: String?,
    highlightedBarId: String?,
    onRemoveFavorite: (String, LaunchableIdentity) -> Unit,
    onRemoveFavoriteBar: (String) -> Unit,
    onBarDragStart: (FavoriteContainer, Int, Offset) -> Unit,
    onBarDrag: (Offset) -> Unit,
    onBarDragEnd: () -> Unit,
    onBarDragCancel: () -> Unit,
    onDragStart: (FavoriteContainer, LaunchableIdentity, Offset, IntSize, Offset) -> Unit,
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
            .homeEditSurface(enabled = editMode),
        verticalArrangement = Arrangement.spacedBy(
            dimensionResource(R.dimen.home_favorite_bar_spacing),
        ),
    ) {
        favoriteBars.forEachIndexed { barIndex, bar ->
            DisposableEffect(bar.id) {
                onDispose {
                    favoriteBarBoundsInWindow.remove(bar.id)
                    applicationContainerBoundsInWindow.remove(bar.applicationDragKey())
                    applicationContainerDescriptors.remove(bar.applicationDragKey())
                    applicationItemBoundsInWindow.keys
                        .filter { it.startsWith("${bar.applicationDragKey()}:") }
                        .forEach(applicationItemBoundsInWindow::remove)
                }
            }
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
                        if (bar.id == highlightedBarId ||
                            applicationDropTargetKey == bar.applicationDragKey()
                        ) {
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
                        .onGloballyPositioned { coordinates ->
                            applicationContainerBoundsInWindow[
                                bar.applicationDragKey()
                            ] = Rect(
                                offset = coordinates.positionInWindow(),
                                size = coordinates.size.toSize(),
                            )
                            applicationContainerDescriptors[
                                bar.applicationDragKey()
                            ] = bar.applicationDragDescriptor(
                                Rect(
                                    offset = coordinates.positionInWindow(),
                                    size = coordinates.size.toSize(),
                                ),
                            )
                        }
                        .clip(barShape)
                        .drawWithContent {
                            drawContent()
                            if (applicationEdgeScroll?.containerKey == bar.applicationDragKey()) {
                                drawRect(
                                    color = fadeColor.copy(
                                        alpha = applicationEdgeScroll.proximity,
                                    ),
                                    topLeft = if (applicationEdgeScroll.forward) {
                                        Offset(size.width - fadeWidthPx, 0f)
                                    } else {
                                        Offset.Zero
                                    },
                                    size = Size(fadeWidthPx, size.height),
                                )
                            }
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
                        itemsIndexed(
                            items = bar.identities,
                            key = { _, identity -> identity.stableKey() },
                        ) { index, identity ->
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
                                onDragStart = { origin, size, touch ->
                                    onDragStart(bar, identity, origin, size, touch)
                                },
                                onDrag = onDrag,
                                onDragEnd = onDragEnd,
                                onDragCancel = onDragCancel,
                                onItemBoundsInWindow = { bounds ->
                                    applicationItemBoundsInWindow[
                                        "${bar.applicationDragKey()}:${identity.stableKey()}"
                                    ] = bounds
                                },
                                exchangeHighlight =
                                    applicationDropTargetKey == bar.applicationDragKey() &&
                                        applicationDropTargetMode ==
                                        ApplicationDragTargetMode.Exchange &&
                                        applicationDropTargetIdentity == identity,
                                insertionHighlight =
                                    applicationDropTargetKey == bar.applicationDragKey() &&
                                        applicationDropTargetMode ==
                                        ApplicationDragTargetMode.Insertion &&
                                        applicationDropTargetIndex == index,
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
    val hapticFeedback = LocalHapticFeedback.current
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
                                onLongPress = {
                                    hapticFeedback.performHapticFeedback(
                                        HapticFeedbackType.LongPress,
                                    )
                                },
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
    onDragStart: (Offset, IntSize, Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
    onItemBoundsInWindow: (Rect) -> Unit = {},
    exchangeHighlight: Boolean = false,
    insertionHighlight: Boolean = false,
) {
    val entry = availability.presentationEntry
    val iconSize = dimensionResource(R.dimen.home_favorite_icon_size)
    val iconPixels = with(LocalDensity.current) { iconSize.roundToPx() }
    val disabledAlpha = integerResource(R.integer.disabled_content_alpha_percent) / 100f
    val itemBackgroundAlpha = integerResource(
        R.integer.home_favorite_bar_item_background_alpha_percent,
    ) / 100f
    val borderAlpha = integerResource(R.integer.home_favorite_bar_border_alpha_percent) / 100f
    val handleTargetWidthPx = with(LocalDensity.current) {
        dimensionResource(R.dimen.home_favorite_bar_drag_target_width).toPx()
    }
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
                onItemBoundsInWindow(
                    Rect(offset = it.positionInWindow(), size = it.size.toSize()),
                )
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
            .then(
                if (exchangeHighlight) {
                    Modifier.border(
                        width = dimensionResource(R.dimen.home_favorite_exchange_border_width),
                        color = colorResource(R.color.home_favorite_exchange_border),
                        shape = RoundedCornerShape(
                            dimensionResource(R.dimen.home_favorite_exchange_border_radius),
                        ),
                    )
                } else {
                    Modifier
                },
            )
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
                        detectReorderDrag(
                            onPressChanged = {},
                            onLongPress = {
                                hapticFeedback.performHapticFeedback(
                                    HapticFeedbackType.LongPress,
                                )
                            },
                            onDragStart = { localTouch ->
                                onDragStart(
                                    itemOriginInWindow,
                                    itemSize,
                                    Offset(
                                        x = itemOriginInWindow.x + itemSize.width -
                                            handleTargetWidthPx + localTouch.x,
                                        y = itemOriginInWindow.y + localTouch.y,
                                    ),
                                )
                            },
                            onDragEnd = onDragEnd,
                            onDragCancel = onDragCancel,
                            onDrag = onDrag,
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
        if (insertionHighlight) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(dimensionResource(R.dimen.home_favorite_insertion_line_thickness))
                    .fillMaxHeight()
                    .background(colorResource(R.color.home_favorite_insertion_line)),
            )
        }
    }
}

@Composable
private fun HomeFavoriteProvisionalList(
    modifier: Modifier,
    onClick: () -> Unit,
    testTag: String,
    applicationDropHighlight: Boolean,
    onBoundsInWindow: (Rect) -> Unit,
    onDisposed: () -> Unit,
) {
    DisposableEffect(Unit) {
        onDispose(onDisposed)
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                onBoundsInWindow(
                    Rect(
                        offset = coordinates.positionInWindow(),
                        size = coordinates.size.toSize(),
                    ),
                )
            }
            .homeEditSurface(enabled = true)
            .then(
                if (applicationDropHighlight) {
                    Modifier.border(
                        width = dimensionResource(
                            R.dimen.home_favorite_exchange_border_width,
                        ),
                        color = colorResource(R.color.home_favorite_exchange_border),
                        shape = RoundedCornerShape(
                            dimensionResource(R.dimen.home_favorite_exchange_border_radius),
                        ),
                    )
                } else {
                    Modifier
                },
            ),
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
     * Waits for the platform long-press timeout without treating consumption by the item's existing
     * click or scroll modifiers as cancellation. No event is consumed before recognition, so a
     * movement beyond touch slop remains available to the owning LazyColumn or LazyRow.
     */
    private suspend fun AwaitPointerEventScope.awaitHandleLongPress(
        down: PointerInputChange,
    ): PointerInputChange? {
        var current = down
        return try {
            withTimeout(viewConfiguration.longPressTimeoutMillis) {
                while (true) {
                    val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                    if (event.changes.any { it.id != down.id && it.pressed }) {
                        return@withTimeout null
                    }
                    val change = event.changes.firstOrNull { it.id == down.id }
                        ?: return@withTimeout null
                    if (change.changedToUpIgnoreConsumed()) return@withTimeout null
                    if ((change.position - down.position).getDistance() >=
                        viewConfiguration.touchSlop
                    ) {
                        return@withTimeout null
                    }
                    current = change
                }
                @Suppress("UNREACHABLE_CODE")
                null
            }
        } catch (_: PointerEventTimeoutCancellationException) {
            current
        }
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
        onLongPress: () -> Unit,
        onDragStart: (FavoriteDragAnchor, Offset) -> Unit,
        onDrag: (Offset) -> Unit,
        onDragEnd: () -> Unit,
        onDragCancel: () -> Unit,
    ) {
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            val anchor = anchorAt(down.position) ?: return@awaitEachGesture
            val longPress = awaitHandleLongPress(down) ?: return@awaitEachGesture
            var dragging = false
            var cancelled = false
            try {
                longPress.consume()
                onLongPress()
                while (true) {
                    val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                    if (event.changes.any { it.id != down.id && it.pressed }) {
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
                    change.consume()
                    if (!dragging) {
                        if (movement == Offset.Zero) continue
                        dragging = true
                        onDragStart(anchor, longPress.position)
                    }
                    onDrag(movement)
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
        onLongPress: () -> Unit,
        onDragStart: (Offset) -> Unit,
        onDrag: (Offset) -> Unit,
        onDragEnd: () -> Unit,
        onDragCancel: () -> Unit,
    ) {
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            val longPress = awaitHandleLongPress(down) ?: return@awaitEachGesture
            var dragging = false
            var cancelled = false
            try {
                longPress.consume()
                onLongPress()
                while (true) {
                    val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                    if (event.changes.any { it.id != down.id && it.pressed }) {
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
                    change.consume()
                    if (!dragging) {
                        if (movement == Offset.Zero) continue
                        dragging = true
                        onPressChanged(true)
                        onDragStart(longPress.position)
                    }
                    onDrag(movement)
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

    private suspend fun PointerInputScope.detectModuleReorderDrag(
        onLongPress: () -> Unit,
        onDragStart: (Offset) -> Boolean,
        onDrag: (Offset) -> Unit,
        onDragEnd: () -> Unit,
        onDragCancel: () -> Unit,
    ) {
        awaitEachGesture {
            val down = awaitFirstDown(
                requireUnconsumed = false,
                pass = PointerEventPass.Initial,
            )
            val longPress = awaitHandleLongPress(down) ?: return@awaitEachGesture
            var dragging = false
            var cancelled = false
            try {
                longPress.consume()
                if (!onDragStart(longPress.position)) return@awaitEachGesture
                dragging = true
                onLongPress()
                while (true) {
                    val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                    if (event.changes.any { it.id != down.id && it.pressed }) {
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
                        if (movement != Offset.Zero) onDrag(movement)
                        change.consume()
                        break
                    }
                    change.consume()
                    if (movement != Offset.Zero) onDrag(movement)
                }
                dragging = false
                if (cancelled) onDragCancel() else onDragEnd()
            } finally {
                if (dragging) onDragCancel()
            }
        }
    }

    @Composable
    private fun HomeEditDock(
        hasFavorites: Boolean,
        expanded: Boolean,
        onToggleExpanded: () -> Unit,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.home_edit_dock_height))
                .testTag("home_edit_dock"),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(
                    when {
                        !hasFavorites -> R.string.home_edit_add_favorites
                        expanded -> R.string.home_edit_select_and_move_modules
                        else -> R.string.home_edit_move_applications
                    },
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = dimensionResource(R.dimen.home_edit_dock_text_padding)),
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                fontSize = dimensionResource(R.dimen.home_edit_dock_text_size).value.sp,
            )
            Box(
                modifier = Modifier
                    .size(
                        width = dimensionResource(R.dimen.home_edit_dock_affordance_width),
                        height = dimensionResource(R.dimen.home_edit_dock_height),
                    )
                    .clickable(role = Role.Button, onClick = onToggleExpanded)
                    .testTag(if (expanded) "home_style_panel_collapse" else "home_style_panel_expand"),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_chevron_right),
                    contentDescription = stringResource(
                        if (expanded) {
                            R.string.home_collapse_style_panel
                        } else {
                            R.string.home_expand_style_panel
                        },
                    ),
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.home_edit_dock_icon_size))
                        .rotate(if (expanded) 90f else -90f),
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
    }
    @Composable
    private fun HomeOrderedModuleComposition(
        modules: List<OrderedFavoriteModule>,
        availabilityByIdentity: Map<LaunchableIdentity, FavoriteAvailability>,
        listState: LazyListState,
        nestedScrollConnection: NestedScrollConnection?,
        editMode: Boolean,
        selectionEnabled: Boolean,
        selectionInteractionEnabled: Boolean,
        selectionVisualEnabled: Boolean = selectionInteractionEnabled,
        selectedModuleId: String?,
        onSelectModule: (String) -> Unit,
        addEntriesEnabled: Boolean,
        onAddToModule: (OrderedFavoriteModule) -> Unit,
        onCreateVerticalModule: () -> Unit,
        onCreateRibbon: () -> Unit,
        onLaunchFavorite: (FavoriteAvailability) -> Unit,
        onLongPressFavorite: (LaunchableEntry) -> Unit,
        modifier: Modifier = Modifier,
        showModuleAddEntries: Boolean = editMode,
        showMainAddEntries: Boolean = editMode,
        moduleEdgeScrollDirection: Int = 0,
        moduleInsertionIndex: Int? = null,
        onModuleBoundsInWindow: (String, Rect) -> Unit = { _, _ -> },
        onModuleDisposed: (String) -> Unit = {},
        onModuleListBoundsInWindow: (Rect) -> Unit = {},
        onModuleDragStart: (OrderedFavoriteModule, Offset) -> Boolean = { _, _ -> false },
        onModuleDrag: (Offset) -> Unit = {},
        onModuleDragEnd: () -> Unit = {},
        onModuleDragCancel: () -> Unit = {},
    ) {
        val ribbonListStates = remember {
            mutableMapOf<String, LazyListState>()
        }
        val localModuleBounds = remember { mutableMapOf<String, Rect>() }
        var listOriginInWindow by remember { mutableStateOf(Offset.Zero) }
        val currentModules by rememberUpdatedState(modules)
        val currentOnModuleDragStart by rememberUpdatedState(onModuleDragStart)
        val currentOnModuleDrag by rememberUpdatedState(onModuleDrag)
        val currentOnModuleDragEnd by rememberUpdatedState(onModuleDragEnd)
        val currentOnModuleDragCancel by rememberUpdatedState(onModuleDragCancel)
        val moduleHapticFeedback = LocalHapticFeedback.current
        val insertionLineColor = colorResource(R.color.home_favorite_insertion_line)
        val insertionLineThickness = dimensionResource(
            R.dimen.home_favorite_insertion_line_thickness,
        )
        val edgeFeedbackBand = dimensionResource(R.dimen.home_favorite_edge_scroll_band)
        val edgeFeedbackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f)
        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
                .then(
                    nestedScrollConnection?.let { Modifier.nestedScroll(it) } ?: Modifier,
            )
                .onGloballyPositioned { coordinates ->
                    val origin = coordinates.positionInWindow()
                    listOriginInWindow = origin
                    onModuleListBoundsInWindow(Rect(origin, coordinates.size.toSize()))
                }
                .then(
                    // Keep the pointer-input node stable for the complete expanded-panel
                    // lifetime. Removing and recreating it when a drag disables other editing
                    // actions would cancel the gesture that owns the active module movement.
                    if (selectionEnabled) {
                        Modifier.pointerInput(Unit) {
                            detectModuleReorderDrag(
                                onLongPress = {
                                    moduleHapticFeedback.performHapticFeedback(
                                        HapticFeedbackType.LongPress,
                                    )
                                },
                                onDragStart = { localTouch ->
                                    val touchInWindow = listOriginInWindow + localTouch
                                    val module = currentModules.firstOrNull { candidate ->
                                        localModuleBounds[candidate.id]
                                            ?.contains(touchInWindow) == true
                                    } ?: return@detectModuleReorderDrag false
                                    currentOnModuleDragStart(module, touchInWindow)
                                },
                                onDrag = { currentOnModuleDrag(it) },
                                onDragEnd = { currentOnModuleDragEnd() },
                                onDragCancel = { currentOnModuleDragCancel() },
                            )
                        }
                    } else {
                        Modifier
                    },
                )
                .drawWithContent {
                    drawContent()
                    if (moduleEdgeScrollDirection != 0) {
                        val band = edgeFeedbackBand.toPx().coerceAtMost(size.height / 2f)
                        if (moduleEdgeScrollDirection < 0) {
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        edgeFeedbackColor,
                                        edgeFeedbackColor.copy(alpha = 0f),
                                    ),
                                    startY = 0f,
                                    endY = band,
                                ),
                                size = Size(size.width, band),
                            )
                        } else {
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        edgeFeedbackColor.copy(alpha = 0f),
                                        edgeFeedbackColor,
                                    ),
                                    startY = size.height - band,
                                    endY = size.height,
                                ),
                                topLeft = Offset(0f, size.height - band),
                                size = Size(size.width, band),
                            )
                        }
                    }
                }
                .testTag("home_ordered_favorite_modules"),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(R.dimen.home_module_spacing),
            ),
        ) {
            itemsIndexed(
                items = modules,
                key = { _, module -> module.id },
            ) { index, module ->
                DisposableEffect(module.id) {
                    onDispose {
                        localModuleBounds.remove(module.id)
                        onModuleDisposed(module.id)
                    }
                }
                val showsTopInsertion = moduleInsertionIndex == index
                val showsBottomInsertion = index == modules.lastIndex &&
                    moduleInsertionIndex == modules.size
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { coordinates ->
                            val origin = coordinates.positionInWindow()
                            val bounds = Rect(origin, coordinates.size.toSize())
                            localModuleBounds[module.id] = bounds
                            onModuleBoundsInWindow(module.id, bounds)
                        }
                        .drawWithContent {
                            drawContent()
                            val stroke = insertionLineThickness.toPx()
                            if (showsTopInsertion) {
                                drawLine(
                                    insertionLineColor,
                                    Offset(0f, stroke / 2f),
                                    Offset(size.width, stroke / 2f),
                                    stroke,
                                )
                            }
                            if (showsBottomInsertion) {
                                drawLine(
                                    insertionLineColor,
                                    Offset(0f, size.height - stroke / 2f),
                                    Offset(size.width, size.height - stroke / 2f),
                                    stroke,
                                )
                            }
                        },
                ) {
                val ribbonListState = if (module.type == OrderedFavoriteModuleType.Ribbon) {
                    ribbonListStates.getOrPut(module.id) { LazyListState() }
                } else {
                    null
                }
                HomeOrderedModuleContent(
                    module = module,
                    availabilityByIdentity = availabilityByIdentity,
                    ribbonListState = ribbonListState,
                    editMode = editMode,
                    showAddEntry = showModuleAddEntries,
                    addEntryEnabled = addEntriesEnabled,
                    onAddToModule = { onAddToModule(module) },
                    onLaunchFavorite = onLaunchFavorite,
                    onLongPressFavorite = onLongPressFavorite,
                )
                if (selectionEnabled) {
                    HomeModuleSelectionLayer(
                        modifier = Modifier.matchParentSize(),
                        selected = module.id == selectedModuleId,
                        enabled = selectionInteractionEnabled,
                        visualEnabled = selectionVisualEnabled,
                        onSelect = { onSelectModule(module.id) },
                    )
                }
                }
            }
            if (showMainAddEntries) {
                item(key = "main-list-add-entries") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(
                            dimensionResource(R.dimen.home_add_favorite_entry_gap),
                        ),
                    ) {
                        HomeMainListAddFavoriteEntry(
                            modifier = Modifier.weight(1f),
                            label = stringResource(R.string.home_add_favorite_list),
                            testTag = "home_add_favorite_list",
                            enabled = addEntriesEnabled,
                            onClick = onCreateVerticalModule,
                        )
                        HomeMainListAddFavoriteEntry(
                            modifier = Modifier.weight(1f),
                            label = stringResource(R.string.home_add_favorite_ribbon),
                            testTag = "home_add_favorite_ribbon",
                            enabled = addEntriesEnabled,
                            onClick = onCreateRibbon,
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun HomeModuleDragPreview(
        session: ModuleDragSession,
        rootOriginInWindow: Offset,
    ) {
        val density = LocalDensity.current
        val width = with(density) { session.size.width.toDp() }
        val height = with(density) { session.size.height.toDp() }
        val shadowElevation = dimensionResource(R.dimen.home_module_drag_shadow_elevation)
        val shape = RoundedCornerShape(
            dimensionResource(R.dimen.home_module_selection_radius),
        )
        HomeOrderedModuleComposition(
            modules = listOf(session.sourceModule),
            availabilityByIdentity = session.sourceAvailability,
            listState = rememberLazyListState(),
            nestedScrollConnection = null,
            editMode = true,
            selectionEnabled = true,
            selectionInteractionEnabled = false,
            selectionVisualEnabled = true,
            selectedModuleId = session.sourceModule.id.takeIf { session.sourceSelected },
            onSelectModule = {},
            addEntriesEnabled = true,
            onAddToModule = {},
            onCreateVerticalModule = {},
            onCreateRibbon = {},
            onLaunchFavorite = {},
            onLongPressFavorite = {},
            showModuleAddEntries = true,
            showMainAddEntries = false,
            modifier = Modifier
                .offset {
                    val position = session.originInWindow - rootOriginInWindow + session.delta
                    IntOffset(position.x.roundToInt(), position.y.roundToInt())
                }
                .size(width, height)
                .shadow(shadowElevation, shape, clip = false)
                .clearAndSetSemantics { }
                .testTag("home_module_drag_preview"),
        )
    }

    @Composable
    private fun HomeMainListAddFavoriteEntry(
        modifier: Modifier,
        label: String,
        testTag: String,
        enabled: Boolean,
        onClick: () -> Unit,
    ) {
        val shape = RoundedCornerShape(
            dimensionResource(R.dimen.home_favorite_bar_corner_radius),
        )
        Row(
            modifier = modifier
                .heightIn(min = dimensionResource(R.dimen.home_add_favorite_entry_min_height))
                .clip(shape)
                .background(colorResource(R.color.home_add_favorite_surface))
                .border(
                    dimensionResource(R.dimen.home_favorite_bar_border_width),
                    colorResource(R.color.home_add_favorite_border),
                    shape,
                )
                .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
                .padding(horizontal = dimensionResource(R.dimen.home_favorite_bar_item_inset))
                .testTag(testTag),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_add),
                contentDescription = null,
                modifier = Modifier.size(dimensionResource(R.dimen.home_add_favorite_icon_size)),
                tint = MaterialTheme.colorScheme.onBackground.copy(
                    alpha = if (enabled) 1f else 0.38f,
                ),
            )
            Spacer(Modifier.width(dimensionResource(R.dimen.home_add_favorite_entry_gap)))
            Text(
                text = label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground.copy(
                    alpha = if (enabled) 1f else 0.38f,
                ),
                fontWeight = FontWeight.Normal,
                fontSize = dimensionResource(R.dimen.home_favorite_text_size).value.sp,
                lineHeight = dimensionResource(R.dimen.home_favorite_line_height).value.sp,
            )
        }
    }

    @Composable
    internal fun HomeModuleAddFavoriteEntry(
        modifier: Modifier,
        module: OrderedFavoriteModule,
        enabled: Boolean,
        onClick: () -> Unit,
    ) {
        val shape = RoundedCornerShape(
            dimensionResource(R.dimen.home_favorite_bar_corner_radius),
        )
        val iconSlotSize = dimensionResource(
            if (module.type == OrderedFavoriteModuleType.Ribbon) {
                R.dimen.home_favorite_icon_size
            } else {
                module.applicationSize.iconSizeResource()
            },
        )
        val surfaceModifier = modifier
            .then(
                if (module.type == OrderedFavoriteModuleType.Ribbon) {
                    Modifier.height(dimensionResource(R.dimen.home_favorite_bar_height))
                } else if (module.namePlacement == FavoriteNamePlacement.Below) {
                    Modifier.height(
                        dimensionResource(module.applicationSize.belowItemHeightResource()),
                    )
                } else {
                    Modifier.height(dimensionResource(module.applicationSize.rowHeightResource()))
                },
            )
            .clip(shape)
            .background(colorResource(R.color.home_add_favorite_surface))
            .border(
                dimensionResource(R.dimen.home_favorite_bar_border_width),
                colorResource(R.color.home_add_favorite_border),
                shape,
            )
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .testTag("home_add_favorite_${module.id}")
        if (module.type == OrderedFavoriteModuleType.Vertical &&
            module.namePlacement == FavoriteNamePlacement.Below
        ) {
            Column(
                modifier = surfaceModifier.padding(
                    start = dimensionResource(R.dimen.home_favorite_below_horizontal_inset),
                    top = dimensionResource(R.dimen.home_favorite_below_top_inset),
                    end = dimensionResource(R.dimen.home_favorite_below_horizontal_inset),
                    bottom = dimensionResource(R.dimen.home_favorite_below_bottom_inset),
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                HomeAddFavoriteIconSlot(iconSlotSize, enabled)
                Spacer(
                    Modifier.height(
                        dimensionResource(R.dimen.home_favorite_below_icon_label_gap),
                    ),
                )
                HomeAddFavoriteLabel(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    size = module.applicationSize,
                    enabled = enabled,
                )
            }
        } else {
            Row(
                modifier = surfaceModifier.padding(
                    horizontal = dimensionResource(R.dimen.home_favorite_bar_item_inset),
                ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                HomeAddFavoriteIconSlot(iconSlotSize, enabled)
                Spacer(
                    Modifier.width(
                        dimensionResource(
                            if (module.type == OrderedFavoriteModuleType.Ribbon) {
                                R.dimen.home_favorite_bar_icon_label_gap
                            } else {
                                R.dimen.home_favorite_icon_label_gap
                            },
                        ),
                    ),
                )
                HomeAddFavoriteLabel(
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start,
                    size = if (module.type == OrderedFavoriteModuleType.Ribbon) {
                        FavoriteListSize.Medium
                    } else {
                        module.applicationSize
                    },
                    enabled = enabled,
                )
            }
        }
    }

    @Composable
    private fun HomeAddFavoriteIconSlot(
        size: androidx.compose.ui.unit.Dp,
        enabled: Boolean,
    ) {
        Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(R.drawable.ic_add),
                contentDescription = null,
                modifier = Modifier.size(dimensionResource(R.dimen.home_add_favorite_icon_size)),
                tint = MaterialTheme.colorScheme.onBackground.copy(
                    alpha = if (enabled) 1f else 0.38f,
                ),
            )
        }
    }

    @Composable
    private fun HomeAddFavoriteLabel(
        modifier: Modifier,
        textAlign: TextAlign,
        size: FavoriteListSize,
        enabled: Boolean,
    ) {
        Text(
            text = stringResource(R.string.home_add_favorite),
            modifier = modifier,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = textAlign,
            color = MaterialTheme.colorScheme.onBackground.copy(
                alpha = if (enabled) 1f else 0.38f,
            ),
            fontWeight = FontWeight.Normal,
            fontSize = dimensionResource(size.textSizeResource()).value.sp,
            lineHeight = dimensionResource(size.lineHeightResource()).value.sp,
        )
    }

    @Composable
    private fun HomeModuleSelectionLayer(
        modifier: Modifier,
        selected: Boolean,
        enabled: Boolean,
        visualEnabled: Boolean,
        onSelect: () -> Unit,
    ) {
        val selectedShape = RoundedCornerShape(
            dimensionResource(R.dimen.home_module_selection_radius),
        )
        val selectedBorder = dimensionResource(R.dimen.home_module_selection_stroke)
        val markColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(
            alpha = if (visualEnabled) 1f else 0.38f,
        )
        val markInset = dimensionResource(R.dimen.home_module_mark_inset)
        val markArm = dimensionResource(R.dimen.home_module_mark_arm)
        val markStroke = dimensionResource(R.dimen.home_module_mark_stroke)
        val markRadius = dimensionResource(R.dimen.home_module_mark_radius)
        Box(
            modifier = modifier
                .then(
                    if (selected) {
                        Modifier.border(
                            width = selectedBorder,
                            color = MaterialTheme.colorScheme.onBackground.copy(
                                alpha = if (visualEnabled) 1f else 0.38f,
                            ),
                            shape = selectedShape,
                        )
                    } else {
                        Modifier.drawWithContent {
                            drawContent()
                            val inset = markInset.toPx()
                            val arm = markArm.toPx()
                            val stroke = markStroke.toPx()
                            val radius = markRadius.toPx()
                            val left = inset
                            val top = inset
                            val right = size.width - inset
                            val bottom = size.height - inset
                            drawCornerMark(
                                color = markColor,
                                corner = Offset(left, top),
                                horizontalEnd = Offset(left + arm, top),
                                verticalEnd = Offset(left, top + arm),
                                radius = radius,
                                stroke = stroke,
                                startAngle = 180f,
                            )
                            drawCornerMark(
                                color = markColor,
                                corner = Offset(right, top),
                                horizontalEnd = Offset(right - arm, top),
                                verticalEnd = Offset(right, top + arm),
                                radius = radius,
                                stroke = stroke,
                                startAngle = 270f,
                            )
                            drawCornerMark(
                                color = markColor,
                                corner = Offset(left, bottom),
                                horizontalEnd = Offset(left + arm, bottom),
                                verticalEnd = Offset(left, bottom - arm),
                                radius = radius,
                                stroke = stroke,
                                startAngle = 90f,
                            )
                            drawCornerMark(
                                color = markColor,
                                corner = Offset(right, bottom),
                                horizontalEnd = Offset(right - arm, bottom),
                                verticalEnd = Offset(right, bottom - arm),
                                radius = radius,
                                stroke = stroke,
                                startAngle = 0f,
                            )
                        }
                    },
                )
                .clickable(enabled = enabled, role = Role.Button, onClick = onSelect)
                .testTag(if (selected) "home_module_selected" else "home_module_selectable"),
        )
    }

    @Composable
    private fun HomeFavoriteMessage(
        message: String,
        showProgress: Boolean,
        onRetry: (() -> Unit)?,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (showProgress) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_inventory_error),
                    contentDescription = null,
                    modifier = Modifier.size(
                        dimensionResource(R.dimen.home_favorite_error_icon_size),
                    ),
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
    internal fun HomeFavoriteBelowItem(
        modifier: Modifier,
        availability: FavoriteAvailability,
        listSize: FavoriteListSize,
        onClick: () -> Unit,
        onLongClick: () -> Unit,
    ) {
        val entry = availability.presentationEntry
        val iconSize = dimensionResource(listSize.iconSizeResource())
        val iconPixels = with(LocalDensity.current) { iconSize.roundToPx() }
        val disabledAlpha = integerResource(R.integer.disabled_content_alpha_percent) / 100f
        val interactionSource = remember(entry?.identity) { MutableInteractionSource() }
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
        Column(
            modifier = modifier
                .height(dimensionResource(listSize.belowItemHeightResource()))
                .combinedClickable(
                    interactionSource = interactionSource,
                    indication = ripple(color = colorResource(R.color.home_favorite_ripple)),
                    role = Role.Button,
                    onClick = onClick,
                    onLongClick = {
                        if (entry != null) {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            onLongClick()
                        }
                    },
                )
                .padding(
                    start = dimensionResource(R.dimen.home_favorite_below_horizontal_inset),
                    top = dimensionResource(R.dimen.home_favorite_below_top_inset),
                    end = dimensionResource(R.dimen.home_favorite_below_horizontal_inset),
                    bottom = dimensionResource(R.dimen.home_favorite_below_bottom_inset),
                )
                .alpha(if (availability is FavoriteAvailability.Available) 1f else disabledAlpha)
                .testTag("home_favorite_below_item"),
            horizontalAlignment = Alignment.CenterHorizontally,
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
                Image(bitmap = bitmap, contentDescription = null, modifier = Modifier.size(iconSize))
            }
            Spacer(Modifier.height(dimensionResource(R.dimen.home_favorite_below_icon_label_gap)))
            Text(
                text = displayText,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                fontSize = dimensionResource(listSize.textSizeResource()).value.sp,
                lineHeight = dimensionResource(listSize.lineHeightResource()).value.sp,
            )
        }
    }

    @Composable
    @OptIn(ExperimentalFoundationApi::class)
    internal fun HomeFavoriteRow(
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
        val ribbonShape = RoundedCornerShape(
            dimensionResource(R.dimen.home_favorite_bar_corner_radius),
        )
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
                .then(
                    if (compact) {
                        Modifier
                            .background(
                                MaterialTheme.colorScheme.onBackground.copy(
                                    alpha = integerResource(
                                        R.integer.home_favorite_bar_item_background_alpha_percent,
                                    ) / 100f,
                                ),
                                ribbonShape,
                            )
                            .border(
                                width = dimensionResource(R.dimen.home_favorite_bar_border_width),
                                color = MaterialTheme.colorScheme.onBackground.copy(
                                    alpha = integerResource(
                                        R.integer.home_favorite_bar_border_alpha_percent,
                                    ) / 100f,
                                ),
                                shape = ribbonShape,
                            )
                    } else {
                        Modifier
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
                        end = when {
                            editMode -> handleTargetSize
                            compact -> dimensionResource(R.dimen.home_favorite_bar_item_inset)
                            else -> 0.dp
                        },
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
            Spacer(
                Modifier.width(
                    if (compact) {
                        dimensionResource(R.dimen.home_favorite_bar_icon_label_gap)
                    } else {
                        dimensionResource(R.dimen.home_favorite_icon_label_gap)
                    },
                ),
            )
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

    private fun List<OrderedFavoriteModule>.withPresentationFrom(
        aggregate: FavoriteAggregate,
    ): List<OrderedFavoriteModule> {
        val verticalById = aggregate.verticalLists.associateBy(FavoriteContainer::id)
        return map { module ->
            val container = verticalById[module.id]
            if (module.type != OrderedFavoriteModuleType.Vertical || container == null) {
                module
            } else if (
                module.applicationSize == container.listSize &&
                module.namePlacement == container.namePlacement &&
                module.itemsPerRow == container.itemsPerRow
            ) {
                module
            } else {
                module.copy(
                    applicationSize = container.listSize,
                    namePlacement = container.namePlacement,
                    itemsPerRow = container.itemsPerRow,
                )
            }
        }
    }

    internal fun LaunchableIdentity.stableKey(): String =
        "$profileSerialNumber:${componentName.flattenToString()}"

    internal fun FavoriteListSize.iconSizeResource(): Int = when (this) {
        FavoriteListSize.Large -> R.dimen.home_favorite_large_icon_size
        FavoriteListSize.Medium -> R.dimen.home_favorite_icon_size
        FavoriteListSize.Small -> R.dimen.home_companion_favorite_icon_size
    }

    private fun FavoriteListSize.rowHeightResource(): Int = when (this) {
        FavoriteListSize.Large -> R.dimen.home_favorite_large_row_min_height
        FavoriteListSize.Medium -> R.dimen.home_favorite_row_min_height
        FavoriteListSize.Small -> R.dimen.home_companion_favorite_row_min_height
    }

    private fun FavoriteListSize.belowItemHeightResource(): Int = when (this) {
        FavoriteListSize.Large -> R.dimen.home_favorite_large_below_height
        FavoriteListSize.Medium -> R.dimen.home_favorite_medium_below_height
        FavoriteListSize.Small -> R.dimen.home_favorite_small_below_height
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
