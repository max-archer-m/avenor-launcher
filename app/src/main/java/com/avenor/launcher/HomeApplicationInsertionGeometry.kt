package com.avenor.launcher

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

internal data class ApplicationInsertion(
    val boundary: Int,
    val lineStart: Offset,
    val lineEnd: Offset,
    val clipBounds: Rect,
)

/** Real-cell hit regions resolve to item edges for vertical layouts and shared gap axes for ribbons. */
internal fun resolveApplicationInsertion(
    module: OrderedFavoriteModule,
    pointer: Offset,
    viewport: Rect,
    moduleBounds: Rect,
    itemBounds: Map<LaunchableIdentity, Rect>,
    addBounds: Rect?,
    movingIdentity: LaunchableIdentity? = null,
): ApplicationInsertion? {
    val clip = viewport.intersect(other = moduleBounds)
    if (clip.isEmpty || !clip.contains(offset = pointer) || addBounds?.contains(offset = pointer) == true) {
        return null
    }
    val identities = applicationLayoutIdentities(module = module, movingIdentity = movingIdentity)
    val cells = identities.mapIndexedNotNull(
        transform = { index, identity -> itemBounds[identity]?.let(block = { index to it }) },
    )
    if (cells.isEmpty()) return null
    val horizontal = module.type == OrderedFavoriteModuleType.Ribbon || module.itemsPerRow > 1

    fun edge(cell: Pair<Int, Rect>, after: Boolean): ApplicationInsertion? =
        if (module.type == OrderedFavoriteModuleType.Ribbon) {
            ribbonApplicationInsertion(
                identities = identities, boundary = cell.first + if (after) 1 else 0,
                itemBounds = itemBounds, addBounds = addBounds, clip = clip,
            )
        } else {
            applicationInsertionEdge(cell = cell, after = after, horizontal = horizontal, clip = clip)
        }

    val hit = cells.firstOrNull(predicate = { (_, bounds) -> bounds.contains(offset = pointer) })
    if (hit != null) {
        return edge(
            cell = hit,
            after = if (horizontal) pointer.x >= hit.second.center.x else pointer.y >= hit.second.center.y,
        )
    }

    val rows = if (module.type == OrderedFavoriteModuleType.Ribbon) {
        listOf(element = cells)
    } else {
        cells.groupBy(keySelector = { it.first / module.itemsPerRow }).values.toList()
    }
    val sameRow = rows.firstOrNull(
        predicate = { row -> pointer.y >= row.first().second.top && pointer.y <= row.first().second.bottom },
    )
    if (horizontal && sameRow != null) {
        // Includes ribbon gaps and unused cells in a partially filled final row, never the add cell.
        val following = sameRow.firstOrNull(predicate = { pointer.x < it.second.left })
        val preceding = sameRow.lastOrNull(predicate = { pointer.x > it.second.right })
        return when {
            following == null && preceding != null -> edge(cell = preceding, after = true)
            preceding == null && following != null -> edge(cell = following, after = false)
            following != null && preceding != null -> {
                if (following.second.left - pointer.x <= pointer.x - preceding.second.right) {
                    edge(cell = following, after = false)
                } else {
                    edge(cell = preceding, after = true)
                }
            }
            else -> null
        }
    }
    val following = rows.firstOrNull(predicate = { pointer.y < it.first().second.top })?.first()
    val preceding = rows.lastOrNull(predicate = { pointer.y > it.last().second.bottom })?.last()
    return when {
        following == null && preceding != null -> edge(cell = preceding, after = true)
        preceding == null && following != null -> edge(cell = following, after = false)
        following != null && preceding != null -> {
            if (following.second.top - pointer.y <= pointer.y - preceding.second.bottom) {
                edge(cell = following, after = false)
            } else {
                edge(cell = preceding, after = true)
            }
        }
        else -> null
    }
}

private fun applicationInsertionEdge(
    cell: Pair<Int, Rect>,
    after: Boolean,
    horizontal: Boolean,
    clip: Rect,
): ApplicationInsertion? {
    val (index, bounds) = cell
    val lineStart: Offset
    val lineEnd: Offset
    if (horizontal) {
        val x = if (after) bounds.right else bounds.left
        if (x < clip.left || x > clip.right) return null
        lineStart = Offset(x = x, y = maxOf(clip.top, bounds.top))
        lineEnd = Offset(x = x, y = minOf(clip.bottom, bounds.bottom))
        if (lineEnd.y <= lineStart.y) return null
    } else {
        val y = if (after) bounds.bottom else bounds.top
        if (y < clip.top || y > clip.bottom) return null
        lineStart = Offset(x = maxOf(clip.left, bounds.left), y = y)
        lineEnd = Offset(x = minOf(clip.right, bounds.right), y = y)
        if (lineEnd.x <= lineStart.x) return null
    }
    return ApplicationInsertion(
        boundary = index + if (after) 1 else 0,
        lineStart = lineStart,
        lineEnd = lineEnd,
        clipBounds = clip,
    )
}

internal fun resolveApplicationDestination(
    modules: List<OrderedFavoriteModule>,
    pointer: Offset,
    viewport: Rect,
    moduleBounds: Map<String, Rect>,
    itemBounds: Map<LaunchableIdentity, Rect>,
    addBounds: Map<String, Rect>,
    movingIdentity: LaunchableIdentity? = null,
): Pair<OrderedFavoriteModule, ApplicationInsertion>? {
    if (!viewport.contains(offset = pointer)) return null
    val hit = modules.firstOrNull(predicate = { moduleBounds[it.id]?.contains(offset = pointer) == true })
    if (hit != null) {
        val insertion = resolveApplicationInsertion(
            module = hit, pointer = pointer, viewport = viewport,
            moduleBounds = moduleBounds.getValue(key = hit.id), itemBounds = itemBounds, addBounds = addBounds[hit.id],
            movingIdentity = movingIdentity,
        ) ?: return null
        return hit to insertion
    }
    // Only a gap bounded by two modules is an insertion destination. Space before the first
    // or after the last module, including the main add-entry row, is not an implicit boundary.
    val preceding = modules.lastOrNull(predicate = { moduleBounds[it.id]?.bottom?.let(block = { y -> y <= pointer.y }) == true })
        ?: return null
    val following = modules.firstOrNull(predicate = { moduleBounds[it.id]?.top?.let(block = { y -> y > pointer.y }) == true })
        ?: return null
    val beforeNext = moduleBounds.getValue(key = following.id).top - pointer.y <=
        pointer.y - moduleBounds.getValue(key = preceding.id).bottom
    val module = if (beforeNext) following else preceding
    val identities = applicationLayoutIdentities(module = module, movingIdentity = movingIdentity)
    // A lifted singleton retains its add entry, not a synthetic start/end insertion boundary.
    if (identities.isEmpty()) return null
    val index = if (beforeNext) 0 else identities.lastIndex
    val bounds = itemBounds[identities[index]] ?: return null
    val clip = viewport.intersect(other = moduleBounds.getValue(key = module.id))
    if (clip.isEmpty) return null
    val insertion = if (module.type == OrderedFavoriteModuleType.Ribbon) {
        ribbonApplicationInsertion(
            identities = identities, boundary = if (beforeNext) 0 else identities.size,
            itemBounds = itemBounds, addBounds = addBounds[module.id], clip = clip,
        )
    } else {
        applicationInsertionEdge(cell = index to bounds, after = !beforeNext, horizontal = module.itemsPerRow > 1, clip = clip)
    } ?: return null
    return module to insertion
}

/** A semantic ribbon boundary owns one visual axis, independent of which half selected it. */
private fun ribbonApplicationInsertion(
    identities: List<LaunchableIdentity>,
    boundary: Int,
    itemBounds: Map<LaunchableIdentity, Rect>,
    addBounds: Rect?,
    clip: Rect,
): ApplicationInsertion? {
    if (identities.isEmpty() || boundary !in 0..identities.size) return null
    val preceding = identities.getOrNull(index = boundary - 1)?.let(block = { itemBounds[it] })
    val following = identities.getOrNull(index = boundary)?.let(block = { itemBounds[it] })
    val x: Float
    val heightBounds: Rect
    if (boundary == 0) {
        heightBounds = following ?: return null
        x = heightBounds.left
    } else {
        heightBounds = preceding ?: return null
        val next = if (boundary == identities.size) addBounds else following
        // Never substitute a viewport edge or a different visible application for a missing neighbor.
        x = (heightBounds.right + (next ?: return null).left) / 2
    }
    if (clip.isEmpty || x < clip.left || x > clip.right) return null
    val top = maxOf(clip.top, heightBounds.top)
    val bottom = minOf(clip.bottom, heightBounds.bottom)
    if (bottom <= top) return null
    return ApplicationInsertion(
        boundary = boundary,
        lineStart = Offset(x = x, y = top),
        lineEnd = Offset(x = x, y = bottom),
        clipBounds = clip,
    )
}
