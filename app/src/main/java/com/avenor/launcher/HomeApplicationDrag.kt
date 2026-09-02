package com.avenor.launcher

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

internal enum class ApplicationDragAxis {
    Vertical,
    Horizontal,
}

internal enum class ApplicationDragTargetMode {
    Exchange,
    Insertion,
}

internal data class ApplicationDragContainerDescriptor(
    val key: String,
    val type: FavoriteContainerType,
    val axis: ApplicationDragAxis,
    val bounds: Rect,
    val identities: List<LaunchableIdentity> = emptyList(),
)

internal data class ApplicationDragTargetSession(
    val sourceContainerKey: String,
    val sourceIdentity: LaunchableIdentity,
    val sourceContainerType: FavoriteContainerType,
    val sourceAxis: ApplicationDragAxis,
    val touchStartInWindow: Offset,
    val delta: Offset = Offset.Zero,
    val targetContainerKey: String? = null,
    val targetContainerType: FavoriteContainerType? = null,
    val targetAxis: ApplicationDragAxis? = null,
    val targetMode: ApplicationDragTargetMode? = null,
    val targetIdentity: LaunchableIdentity? = null,
    val targetIndex: Int? = null,
) {
    val touchInWindow: Offset get() = touchStartInWindow + delta

    fun advanced(
        amount: Offset,
        containerDescriptors: Map<String, ApplicationDragContainerDescriptor>,
        itemBoundsInWindow: Map<String, Rect>,
    ): ApplicationDragTargetSession {
        val moved = copy(delta = delta + amount)
        val descriptor = containerDescriptors.values.firstOrNull { candidate ->
            candidate.key != sourceContainerKey &&
                candidate.bounds.contains(moved.touchInWindow)
        }
        if (descriptor == null) {
            return moved.copy(
                targetContainerKey = null,
                targetContainerType = null,
                targetAxis = null,
                targetMode = null,
                targetIdentity = null,
                targetIndex = null,
            )
        }
        val itemIndex = descriptor.identities.indexOfFirst { identity ->
            itemBoundsInWindow[
                "${descriptor.key}:${identity.stableKey()}"
            ]?.contains(moved.touchInWindow) == true
        }
        val coordinate = if (descriptor.axis == ApplicationDragAxis.Vertical) {
            moved.touchInWindow.y
        } else {
            moved.touchInWindow.x
        }
        val orderedBounds = descriptor.identities.mapIndexedNotNull { index, identity ->
            itemBoundsInWindow[
                "${descriptor.key}:${identity.stableKey()}"
            ]?.let { index to it }
        }
        val insertionIndex = if (itemIndex >= 0) {
            val identity = descriptor.identities[itemIndex]
            val bounds = itemBoundsInWindow[
                "${descriptor.key}:${identity.stableKey()}"
            ] ?: Rect.Zero
            val center = if (descriptor.axis == ApplicationDragAxis.Vertical) {
                (bounds.top + bounds.bottom) / 2f
            } else {
                (bounds.left + bounds.right) / 2f
            }
            val edgeBand = if (descriptor.axis == ApplicationDragAxis.Vertical) {
                bounds.height / 3f
            } else {
                bounds.width / 3f
            }
            when {
                coordinate < center - edgeBand -> itemIndex
                coordinate > center + edgeBand -> itemIndex + 1
                else -> null
            }
        } else {
            val before = orderedBounds.firstOrNull { (_, bounds) ->
                coordinate < if (descriptor.axis == ApplicationDragAxis.Vertical) {
                    bounds.top
                } else {
                    bounds.left
                }
            }?.first
            before ?: orderedBounds.lastOrNull()?.first?.plus(1) ?: 0
        }
        return moved.copy(
            targetContainerKey = descriptor.key,
            targetContainerType = descriptor.type,
            targetAxis = descriptor.axis,
            targetMode = if (itemIndex >= 0 && insertionIndex == null) {
                ApplicationDragTargetMode.Exchange
            } else {
                ApplicationDragTargetMode.Insertion
            },
            targetIdentity = if (insertionIndex == null) {
                descriptor.identities.getOrNull(itemIndex)
            } else {
                null
            },
            targetIndex = insertionIndex ?: itemIndex.coerceAtLeast(0),
        )
    }

    fun showsContainerHighlight(containerKey: String): Boolean =
        targetContainerKey == containerKey

    fun edgeScroll(
        descriptors: Map<String, ApplicationDragContainerDescriptor>,
        bandPx: Float,
        primaryListState: LazyListState,
        companionListState: LazyListState,
        editListStates: Map<String, LazyListState>,
        favoriteBarStates: Map<String, LazyListState>,
    ): ApplicationEdgeScroll? {
        val request = edgeScrollCandidate(descriptors, bandPx) ?: return null
        val state = when (request.axis) {
            ApplicationDragAxis.Vertical ->
                editListStates[request.containerKey.substringAfter(':')]
                ?: if (request.containerKey == "vertical-list:${PRIMARY_LIST_ID}") {
                    primaryListState
                } else {
                    companionListState
                }
            ApplicationDragAxis.Horizontal ->
                favoriteBarStates[request.containerKey.substringAfter(':')]
        } ?: return null
        return request.takeIf { state.canScroll(it.forward) }
    }
}

internal fun ApplicationDragTargetSession.edgeScrollCandidate(
    descriptors: Map<String, ApplicationDragContainerDescriptor>,
    bandPx: Float,
): ApplicationEdgeScroll? {
    val activeKey = targetContainerKey ?: sourceContainerKey
    val descriptor = descriptors[activeKey] ?: return null
    if (!descriptor.bounds.contains(touchInWindow)) return null
    val axis = descriptor.axis
    val coordinate = if (axis == ApplicationDragAxis.Vertical) {
        touchInWindow.y
    } else {
        touchInWindow.x
    }
    val start = if (axis == ApplicationDragAxis.Vertical) {
        descriptor.bounds.top
    } else {
        descriptor.bounds.left
    }
    val end = if (axis == ApplicationDragAxis.Vertical) {
        descriptor.bounds.bottom
    } else {
        descriptor.bounds.right
    }
    val band = bandPx.coerceAtMost((end - start) / 2f)
    if (band <= 0f) return null
    val distanceFromEdge = when {
        coordinate < start + band -> start + band - coordinate
        coordinate >= end - band -> coordinate - (end - band)
        else -> return null
    }
    val forward = coordinate >= end - band
    return ApplicationEdgeScroll(
        containerKey = activeKey,
        axis = axis,
        forward = forward,
        proximity = (distanceFromEdge / band).coerceIn(0f, 1f),
        touchInWindow = touchInWindow,
    )
}

internal data class ApplicationEdgeScroll(
    val containerKey: String,
    val axis: ApplicationDragAxis,
    val forward: Boolean,
    val proximity: Float,
    val touchInWindow: Offset,
)

internal fun LazyListState.canScroll(forward: Boolean): Boolean =
    if (forward) canScrollForward else canScrollBackward

internal fun FavoriteContainer.applicationDragKey(): String = when (type) {
    FavoriteContainerType.VerticalList -> "vertical-list:$id"
    FavoriteContainerType.FavoriteBar -> "favorite-bar:$id"
}

internal fun FavoriteContainer.applicationDragDescriptor(
    bounds: Rect,
): ApplicationDragContainerDescriptor =
    ApplicationDragContainerDescriptor(
        key = applicationDragKey(),
        type = type,
        axis = when (type) {
            FavoriteContainerType.VerticalList -> ApplicationDragAxis.Vertical
            FavoriteContainerType.FavoriteBar -> ApplicationDragAxis.Horizontal
        },
        bounds = bounds,
        identities = identities,
    )

internal const val PROVISIONAL_VERTICAL_LIST_DRAG_KEY_PREFIX = "vertical-list:provisional:"
internal const val PROVISIONAL_VERTICAL_LIST_DRAG_KEY_0 =
    "${PROVISIONAL_VERTICAL_LIST_DRAG_KEY_PREFIX}0"
internal const val PROVISIONAL_VERTICAL_LIST_DRAG_KEY_1 =
    "${PROVISIONAL_VERTICAL_LIST_DRAG_KEY_PREFIX}1"
internal const val PROVISIONAL_FAVORITE_BAR_DRAG_KEY = "favorite-bar:provisional"

/**
 * Composition a release commits, starting from the currently visible orders. A pending cross-group
 * target exchanges position and group membership with the dragged favorite, so both group counts
 * stay unchanged. A pending insertion instead removes the dragged favorite from its group and
 * inserts it at the marked boundary of the other group, which keeps the relative order of that
 * group's existing favorites.
 */
internal fun FavoriteDragSession.committedComposition():
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
internal fun FavoriteDragSession.advanced(
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
internal const val NANOS_PER_SECOND = 1_000_000_000f

/**
 * Whether the target feedback differs from [previous]: the visible order of a group changed, or the
 * marked cross-group exchange target or insertion boundary moved. Pointer movement on its own
 * leaves
 * the feedback untouched, so this marks exactly the moments at which a release would produce a
 * different composition.
 */
internal fun FavoriteDragSession.feedbackChangedFrom(previous: FavoriteDragSession): Boolean =
    displayedPrimary != previous.displayedPrimary ||
        displayedCompanion != previous.displayedCompanion ||
        crossGroupTarget != previous.crossGroupTarget ||
        insertion != previous.insertion

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
internal fun List<LaunchableIdentity>.exchangedAt(
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
