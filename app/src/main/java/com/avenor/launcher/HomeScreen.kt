package com.avenor.launcher

import com.avenor.launcher.ui.home.components.HomeFavoriteEnterBatch
import com.avenor.launcher.ui.home.components.HomeFavoriteEnterKey
import com.avenor.launcher.ui.home.components.HomeFavoriteExitOverlay
import com.avenor.launcher.ui.home.components.homeFavoriteEnter
import com.avenor.launcher.ui.home.components.rememberHomeFavoriteEnterBatch

import androidx.activity.compose.BackHandler

import android.annotation.SuppressLint
import android.widget.Toast
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
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.material3.LocalTextStyle
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
import kotlin.time.Duration.Companion.milliseconds
import com.avenor.launcher.ui.home.components.HomeBasicInformation
import com.avenor.launcher.ui.home.components.HomeMainListAddFavoriteEntry
import com.avenor.launcher.ui.home.components.HomeApplicationMovementOverlay
import com.avenor.launcher.ui.home.components.HomeApplicationAutoScroll
import androidx.compose.runtime.SideEffect
import com.avenor.launcher.ui.drawer.DrawerDragDrop
import com.avenor.launcher.ui.drawer.DrawerDragJourney
import com.avenor.launcher.ui.home.components.detectHomeApplicationMovement
import com.avenor.launcher.ui.home.components.HomeFavoriteRibbonRailDivider
import com.avenor.launcher.ui.home.components.HomeFavoriteAddControl
import com.avenor.launcher.ui.home.components.HomeFavoriteRibbon
import com.avenor.launcher.ui.home.components.HomeFavoriteRibbonActions
import com.avenor.launcher.ui.home.components.HomeFavoriteRibbonDragActions
import com.avenor.launcher.ui.home.components.HomeFavoriteRibbonDragState
import com.avenor.launcher.ui.home.components.HomeFavoriteRibbonLayoutRegistry
import com.avenor.launcher.ui.home.components.awaitHomeHandleLongPress
import com.avenor.launcher.ui.home.components.detectHomeReorderDrag
import com.avenor.launcher.ui.home.components.homeEditSurface
import com.avenor.launcher.ui.drawer.drawerForegroundShadow
import com.avenor.launcher.ui.style.HomeModuleStylePanel
import com.avenor.launcher.ui.home.components.HomeEditDock
import com.avenor.launcher.ui.home.components.HomeFavoriteBarContainerDragPreview
import com.avenor.launcher.ui.home.components.HomeFavoriteList
import com.avenor.launcher.ui.home.components.HomeFavoriteListDragPreview
import com.avenor.launcher.ui.home.components.HomeFavoriteMessage
import com.avenor.launcher.ui.home.components.HomeFavoriteProvisionalList
import com.avenor.launcher.ui.home.components.HomeModuleDragPreview
import com.avenor.launcher.ui.home.components.HomeOrderedModuleComposition
import com.avenor.launcher.ui.home.components.stableKey
import com.avenor.launcher.ui.home.components.withPresentationFrom


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
    applicationEditingSaving: Boolean = false,
    onRemoveApplication: (LaunchableIdentity) -> Unit = {},
    onCommitApplicationOrder: (change: ApplicationOrderChange, onComplete: () -> Unit) -> Unit = { _, complete -> complete() },
    removalSnackbarHostState: SnackbarHostState? = null,
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
    drawerDragJourney: DrawerDragJourney? = null,
    drawerDragTouchInWindow: Offset = Offset.Zero,
    drawerDragDropping: Boolean = false,
    onDrawerDragCommit: suspend (LaunchableIdentity, DrawerDragDrop) -> Boolean = { _, _ -> false },
    onDrawerDragJourneyFinished: (saved: Boolean, cancelled: Boolean) -> Unit = { _, _ -> },
) {
    val context = LocalContext.current
    val orderedApplicationMovement = remember(calculation = { HomeApplicationMovement() })
    LaunchedEffect(drawerDragJourney) {
        val journey = drawerDragJourney ?: return@LaunchedEffect
        val started = orderedApplicationMovement.startExternalJourney(
            identity = journey.entry.identity,
            pointer = drawerDragTouchInWindow,
        )
        if (!started) onDrawerDragJourneyFinished(false, true)
    }
    LaunchedEffect(drawerDragTouchInWindow) {
        if (drawerDragJourney != null) orderedApplicationMovement.move(drawerDragTouchInWindow)
    }
    LaunchedEffect(drawerDragDropping) {
        if (!drawerDragDropping) return@LaunchedEffect
        val journey = drawerDragJourney ?: return@LaunchedEffect
        val drop = orderedApplicationMovement.finishExternalJourney()
        // An invalid release (no drop target) resolves as a cancellation, not as a
        // save failure; only a resolved drop that fails to persist reports failure.
        val saved = journey != null && drop != null &&
                onDrawerDragCommit(journey.entry.identity, drop)
        onDrawerDragJourneyFinished(saved, drop == null)
    }
    val favoriteEnterBatch = rememberHomeFavoriteEnterBatch(
        modules = (favoriteState as? FavoriteReadState.Readable)?.orderedModules,
    )
    val applicationMovementActive = orderedApplicationMovement.activeIdentity != null
    BackHandler(enabled = editMode && orderedApplicationMovement.isDragging, onBack = {
        if (orderedApplicationMovement.session?.module?.id == EXTERNAL_JOURNEY_MODULE_ID) {
            // Back during the Drawer journey is a cancellation: end the whole journey
            // without a mutation and without the save-failure feedback path.
            orderedApplicationMovement.cancel()
            onDrawerDragJourneyFinished(false, true)
        } else {
            orderedApplicationMovement.cancel()
        }
    })
    LaunchedEffect(editMode, stylePanelExpanded, favoriteState, favoriteAvailability) {
        val session = orderedApplicationMovement.session ?: return@LaunchedEffect
        val module = (favoriteState as? FavoriteReadState.Readable)?.orderedModules
            ?.firstOrNull(predicate = { it.id == session.module.id })
        val availability = favoriteAvailability[session.identity]
        val invalidSource = orderedApplicationMovement.isDragging &&
                session.module.id != EXTERNAL_JOURNEY_MODULE_ID &&
                (module != session.module || (availability !is FavoriteAvailability.Available && availability !is FavoriteAvailability.Disabled))
        if (!editMode || stylePanelExpanded || invalidSource || availability == FavoriteAvailability.ConfirmedRemoved
        ) orderedApplicationMovement.cancel()
    }
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
    val favoriteRibbonLayoutRegistry = remember(
        calculation = {
            HomeFavoriteRibbonLayoutRegistry(
                listStates = favoriteBarStates,
                ribbonBoundsInWindow = favoriteBarBoundsInWindow,
                applicationContainerBoundsInWindow = applicationContainerBoundsInWindow,
                applicationContainerDescriptors = applicationContainerDescriptors,
                applicationItemBoundsInWindow = applicationItemBoundsInWindow,
            )
        },
    )
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
    val snackbarHostState = removalSnackbarHostState ?: remember { SnackbarHostState() }
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
        orderedApplicationMovement.cancel()
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
        if (modules.size < 2 || editMutationJob?.isActive == true || applicationEditingSaving) return false
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
        if (editMutationJob?.isActive == true || applicationEditingSaving) return
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
            .onGloballyPositioned { dragRootOriginInWindow = it.positionInWindow() },
    ) {
        val contentPadding = dimensionResource(R.dimen.home_content_padding)
        val animationDuration = integerResource(R.integer.short_property_animation_duration_ms)
        val orderedModules = (favoriteState as? FavoriteReadState.Readable)
            ?.orderedModules
            .orEmpty()
        val previewAggregate = editTransaction.previewAggregate(
            (favoriteState as? FavoriteReadState.Readable)?.aggregate ?: FavoriteAggregate(),
        )
        val displayedModules = orderedModules.withPresentationFrom(previewAggregate)
        val selectedModule = displayedModules.firstOrNull { it.id == selectedModuleId }
        val styleSaving = applicationEditingSaving || editMutationJob?.isActive == true ||
                moduleDragSession != null
        val stylePanelMaximumHeight = (
                maxHeight -
                        dimensionResource(R.dimen.home_edit_dock_height) -
                        contentPadding -
                        dimensionResource(R.dimen.home_style_panel_minimum_list_viewport)
                ).coerceAtLeast(0.dp)
        Column(modifier = Modifier.fillMaxSize()) {
            if (editMode) {
                HomeEditDock(
                    hasFavorites = orderedModules.isNotEmpty(),
                    expanded = stylePanelExpanded,
                    onToggleExpanded = {
                        if (!applicationMovementActive) {
                            onStylePanelExpandedChange(!stylePanelExpanded)
                        }
                    },
                )
                AnimatedContent(
                    targetState = stylePanelExpanded,
                    modifier = Modifier.padding(horizontal = contentPadding),
                    transitionSpec = {
                        expandVertically(
                            animationSpec = tween(durationMillis = animationDuration),
                            expandFrom = Alignment.Top,
                        ) togetherWith shrinkVertically(
                            animationSpec = tween(durationMillis = animationDuration),
                            shrinkTowards = Alignment.Top,
                        )
                    },
                    label = "home_panel_slot_swap",
                ) { panelExpanded ->
                    if (panelExpanded) {
                        Column {
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
                            Spacer(Modifier.height(dimensionResource(R.dimen.home_module_spacing)))
                        }
                    } else {
                        Column {
                            HomeBasicInformation(
                                editMode = true,
                                accessibilityLockController = accessibilityLockController,
                                onRequestEditMode = onRequestEditMode,
                            )
                            Spacer(Modifier.height(dimensionResource(R.dimen.home_module_spacing)))
                        }
                    }
                }
            } else {
                HomeBasicInformation(
                    editMode = false,
                    accessibilityLockController = accessibilityLockController,
                    onRequestEditMode = onRequestEditMode,
                    modifier = Modifier.padding(
                        top = contentPadding,
                        start = contentPadding,
                        end = contentPadding,
                    ),
                )
                Spacer(Modifier.height(dimensionResource(R.dimen.home_module_spacing)))
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(
                        start = contentPadding,
                        end = contentPadding,
                        bottom = contentPadding,
                    )
                    .onGloballyPositioned(onGloballyPositioned = { coordinates ->
                        favoriteEnterBatch.exitTransitions?.viewport = Rect(
                            offset = coordinates.positionInWindow(),
                            size = coordinates.size.toSize(),
                        )
                    }),
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
                                enterBatch = favoriteEnterBatch,
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
                            val styledModules =
                                orderedModules.withPresentationFrom(previewAggregate)
                            val displayedModules = moduleDragSession?.remainingModules
                                ?: editTransaction.pendingModuleOrder
                                ?: styledModules
                            HomeOrderedModuleComposition(
                                enterBatch = favoriteEnterBatch,
                                modules = displayedModules,
                                availabilityByIdentity = favoriteAvailability,
                                listState = favoriteListState,
                                nestedScrollConnection = null,
                                editMode = true,
                                selectionEnabled = stylePanelExpanded,
                                selectionInteractionEnabled = !applicationEditingSaving &&
                                        editMutationJob?.isActive != true &&
                                        moduleDragSession == null,
                                selectionVisualEnabled = !applicationEditingSaving && editMutationJob?.isActive != true,
                                selectedModuleId = selectedModuleId,
                                onSelectModule = onSelectModule,
                                addEntriesEnabled = !applicationEditingSaving &&
                                        editMutationJob?.isActive != true &&
                                        moduleDragSession == null && !applicationMovementActive,
                                onRemoveFavorite = onRemoveApplication,
                                applicationMovement = orderedApplicationMovement,
                                onCommitApplicationOrder = { change ->
                                    onCommitApplicationOrder(change) {
                                        orderedApplicationMovement.complete(
                                            change = change
                                        )
                                    }
                                },
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
                    HomeFavoriteRibbon(
                        favoriteRibbons = renderedBars,
                        availabilityByIdentity = favoriteAvailability,
                        editMode = editMode,
                        layoutRegistry = favoriteRibbonLayoutRegistry,
                        dragState = HomeFavoriteRibbonDragState(
                            applicationDropTargetKey =
                                applicationDragTargetSession?.targetContainerKey,
                            applicationEdgeScroll = applicationEdgeScroll,
                            applicationDropTargetIdentity =
                                applicationDragTargetSession?.targetIdentity,
                            applicationDropTargetMode =
                                applicationDragTargetSession?.targetMode,
                            applicationDropTargetIndex =
                                applicationDragTargetSession?.targetIndex,
                            draggedIdentity = favoriteBarDragSession?.identity,
                            draggedRibbonId =
                                favoriteBarContainerDragSession?.sourceContainer?.id,
                            highlightedRibbonId =
                                favoriteBarContainerDragSession?.targetContainerId,
                        ),
                        actions = object : HomeFavoriteRibbonActions {
                            override fun launchFavorite(availability: FavoriteAvailability) {
                                onLaunchFavorite(availability)
                            }

                            override fun longPressFavorite(entry: LaunchableEntry) {
                                onLongPressFavorite(entry)
                            }

                            override fun addFavorites(ribbonId: String) {
                                onAddFavoritesToBar(ribbonId)
                            }

                            override fun removeFavorite(
                                ribbonId: String,
                                identity: LaunchableIdentity,
                            ) {
                                removeFavoriteFromContainer(
                                    containerId = ribbonId,
                                    identity = identity,
                                )
                            }

                            override fun removeRibbon(ribbonId: String) {
                                removeFavoriteBar(containerId = ribbonId)
                            }
                        },
                        dragActions = object : HomeFavoriteRibbonDragActions {
                            override fun startRibbonDrag(
                                ribbon: FavoriteContainer,
                                index: Int,
                                touch: Offset,
                            ) {
                                startFavoriteBarContainerDrag(
                                    bar = ribbon,
                                    index = index,
                                    touchInWindow = touch,
                                    displayedBars = renderedBars,
                                )
                            }

                            override fun dragRibbon(delta: Offset) {
                                advanceFavoriteBarContainerDrag(amount = delta)
                            }

                            override fun finishRibbonDrag() {
                                finishFavoriteBarContainerDrag()
                            }

                            override fun cancelRibbonDrag() {
                                favoriteBarContainerDragSession = null
                            }

                            override fun startApplicationDrag(
                                ribbon: FavoriteContainer,
                                identity: LaunchableIdentity,
                                origin: Offset,
                                size: IntSize,
                                touch: Offset,
                            ) {
                                dragGeneration += 1
                                favoriteBarDragSession = FavoriteBarDragSession(
                                    generation = dragGeneration,
                                    barId = ribbon.id,
                                    identity = identity,
                                    displayedIdentities = ribbon.identities,
                                    originInWindow = origin,
                                    size = size,
                                )
                                applicationDragTargetSession = ApplicationDragTargetSession(
                                    sourceContainerKey = ribbon.applicationDragKey(),
                                    sourceIdentity = identity,
                                    sourceContainerType = FavoriteContainerType.FavoriteBar,
                                    sourceAxis = ApplicationDragAxis.Horizontal,
                                    touchStartInWindow = touch,
                                )
                            }

                            override fun dragApplication(delta: Offset) {
                                advanceAndPersistFavoriteBarDrag(amount = delta)
                            }

                            override fun finishApplicationDrag() {
                                val target = applicationDragTargetSession
                                if (target?.targetContainerType != null &&
                                    target.targetContainerKey != target.sourceContainerKey
                                ) {
                                    commitCrossContainerDrag(targetSession = target)
                                } else {
                                    favoriteBarDragSession = null
                                    applicationDragTargetSession = null
                                }
                            }

                            override fun cancelApplicationDrag() {
                                favoriteBarDragSession = null
                                applicationDragTargetSession = null
                            }
                        },
                    )
                }
            }
        }
        HomeFavoriteExitOverlay(
            owner = favoriteEnterBatch.exitTransitions,
            rootOrigin = dragRootOriginInWindow
        )
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
            snackbar = { data ->
                Snackbar(
                    action = {
                        data.visuals.actionLabel?.let(
                            block = { label ->
                                TextButton(
                                    enabled = !applicationEditingSaving && editMutationJob?.isActive != true &&
                                            !applicationMovementActive,
                                    onClick = { data.performAction() },
                                    content = {
                                        Text(
                                            text = label,
                                            color = MaterialTheme.colorScheme.inversePrimary
                                        )
                                    },
                                )
                            },
                        )
                    },
                    content = { Text(text = data.visuals.message) },
                )
            },
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
        HomeApplicationMovementOverlay(
            movement = orderedApplicationMovement,
            rootOrigin = dragRootOriginInWindow
        )
        moduleDragSession?.let { session ->
            HomeModuleDragPreview(
                session = session,
                rootOriginInWindow = dragRootOriginInWindow,
            )
        }
    }
}


internal data class FavoriteBarContainerDragSession(
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


internal data class FavoriteListDragSession(
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

internal data class FavoriteDragAnchor(
    val identity: LaunchableIdentity,
    val handleBoundsInWindow: Rect = Rect.Zero,
    val rowOriginInWindow: Offset = Offset.Zero,
    val rowSize: IntSize = IntSize.Zero,
)
