package com.avenor.launcher.ui.home

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.unit.IntSize
import com.avenor.launcher.ApplicationDragContainerDescriptor
import com.avenor.launcher.ApplicationDragTargetSession
import com.avenor.launcher.FavoriteAggregate
import com.avenor.launcher.FavoriteAvailability
import com.avenor.launcher.FavoriteBarDragSession
import com.avenor.launcher.FavoriteBarContainerDragSession
import com.avenor.launcher.FavoriteContainer
import com.avenor.launcher.FavoriteContainerType
import com.avenor.launcher.FavoriteDragSession
import com.avenor.launcher.FavoriteListDragSession
import com.avenor.launcher.FavoriteReadState
import com.avenor.launcher.HomeApplicationMovement
import com.avenor.launcher.HomeEditTransaction
import com.avenor.launcher.LaunchableIdentity
import com.avenor.launcher.ModuleDragSession
import com.avenor.launcher.OrderedFavoriteModule
import com.avenor.launcher.R
import com.avenor.launcher.containerForDragKey
import com.avenor.launcher.advanced
import com.avenor.launcher.exchangedAt
import com.avenor.launcher.feedbackChangedFrom
import com.avenor.launcher.isValidAggregate
import com.avenor.launcher.moveFavorite
import com.avenor.launcher.moveVerticalList
import com.avenor.launcher.PROVISIONAL_FAVORITE_BAR_DRAG_KEY
import com.avenor.launcher.PROVISIONAL_VERTICAL_LIST_DRAG_KEY_PREFIX
import com.avenor.launcher.removeIdentityFromContainer
import com.avenor.launcher.replaceVerticalComposition
import com.avenor.launcher.ui.home.components.HomeFavoriteRibbonLayoutRegistry
import com.avenor.launcher.updateVerticalList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.roundToInt

/**
 * The edit-mode orchestration core extracted from `HomeScreen`: the drag-session state,
 * the single unresolved mutation queue, and the aggregate-mutation methods that the
 * gesture handlers and effects drive. Composition-updated values (edit mode, the live
 * favorite state, localized messages, density metrics, and the host callbacks) are
 * written back by `HomeScreen` on every recomposition.
 */
internal class HomeFavoriteEditOrchestration(
    val orderedApplicationMovement: HomeApplicationMovement,
    val favoriteListState: LazyListState,
    val companionFavoriteListState: LazyListState,
    val snackbarHostState: SnackbarHostState,
    val editScope: CoroutineScope,
    val context: Context,
    val hapticFeedback: HapticFeedback,
) {
    var dragSession by mutableStateOf<FavoriteDragSession?>(null)
    var favoriteBarDragSession by mutableStateOf<FavoriteBarDragSession?>(null)
    var applicationDragTargetSession by mutableStateOf<ApplicationDragTargetSession?>(null)
    var favoriteBarContainerDragSession by mutableStateOf<FavoriteBarContainerDragSession?>(null)
    var favoriteBarContainerCommittedGeneration by mutableIntStateOf(-1)
    var listDragSession by mutableStateOf<FavoriteListDragSession?>(null)
    var listDragCommittedGeneration by mutableIntStateOf(-1)
    var editMutationJob by mutableStateOf<Job?>(null)
    var moduleDragSession by mutableStateOf<ModuleDragSession?>(null)
    val moduleBoundsInWindow = mutableStateMapOf<String, Rect>()
    var moduleListBoundsInWindow by mutableStateOf(Rect.Zero)
    var primaryListBoundsInWindow by mutableStateOf(Rect.Zero)
    var companionListBoundsInWindow by mutableStateOf(Rect.Zero)
    var primaryContainerBoundsInWindow by mutableStateOf(Rect.Zero)
    var companionContainerBoundsInWindow by mutableStateOf(Rect.Zero)
    val editListStates = mutableMapOf<String, LazyListState>()
    val favoriteBarStates = mutableStateMapOf<String, LazyListState>()
    val favoriteBarBoundsInWindow = mutableStateMapOf<String, Rect>()
    val applicationContainerBoundsInWindow = mutableStateMapOf<String, Rect>()
    val applicationContainerDescriptors =
        mutableStateMapOf<String, ApplicationDragContainerDescriptor>()
    val applicationItemBoundsInWindow = mutableStateMapOf<String, Rect>()
    val favoriteRibbonLayoutRegistry = HomeFavoriteRibbonLayoutRegistry(
        listStates = favoriteBarStates,
        ribbonBoundsInWindow = favoriteBarBoundsInWindow,
        applicationContainerBoundsInWindow = applicationContainerBoundsInWindow,
        applicationContainerDescriptors = applicationContainerDescriptors,
        applicationItemBoundsInWindow = applicationItemBoundsInWindow,
    )
    val editTransaction = HomeEditTransaction()

    var editMode by mutableStateOf(false)
    var applicationEditingSaving by mutableStateOf(false)
    var selectedModuleId by mutableStateOf<String?>(null)
    var favoriteState by mutableStateOf<FavoriteReadState>(FavoriteReadState.Loading)
    var favoriteAvailability by mutableStateOf<Map<LaunchableIdentity, FavoriteAvailability>>(
        emptyMap(),
    )
    var undoLabel by mutableStateOf("")
    var favoriteRemovedMessage by mutableStateOf("")
    var favoriteBarRemovedMessage by mutableStateOf("")
    var undoUnavailableMessage by mutableStateOf("")
    var moduleStyleSaveFailureMessage by mutableStateOf("")
    var moduleOrderSaveFailureMessage by mutableStateOf("")
    var insertionBoundaryBandPx by mutableStateOf(0f)
    var edgeScrollBandPx by mutableStateOf(0f)
    var edgeScrollSpeedPxPerSecond by mutableStateOf(0f)
    var edgeScrollStartDelayMillis by mutableStateOf(0L)
    var favoriteBarItemStridePx by mutableStateOf(0f)
    var onCommitFavoriteComposition by mutableStateOf<
        suspend ((FavoriteAggregate) -> FavoriteAggregate) -> FavoriteAggregate?
    >(
        { transform -> transform(FavoriteAggregate()) },
    )
    var onCommitModuleOrder by mutableStateOf<suspend (List<String>) -> Boolean>({ false })

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

    fun startModuleDrag(
        module: OrderedFavoriteModule,
        modules: List<OrderedFavoriteModule>,
        touchInWindow: Offset,
    ): Boolean {
        if (modules.size < 2 || editMutationJob?.isActive == true || applicationEditingSaving) {
            return false
        }
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
                (favoriteState as? FavoriteReadState.Readable)?.aggregate,
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
                    (favoriteState as? FavoriteReadState.Readable)?.aggregate,
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
}
