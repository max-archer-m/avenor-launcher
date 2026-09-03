package com.avenor.launcher

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

/** Existing destinations use an order boundary; new-module destinations use type plus boundary zero. */
internal data class ApplicationOrderChange(
    val moduleId: String,
    val identity: LaunchableIdentity,
    val originalOrder: List<LaunchableIdentity>,
    val boundary: Int,
    val destinationModuleId: String = moduleId,
    val destinationOrder: List<LaunchableIdentity> = originalOrder,
    val newModuleType: OrderedFavoriteModuleType? = null,
) {
    fun reorderedIdentities(): List<LaunchableIdentity>? {
        val sourceIndex = originalOrder.indexOf(element = identity)
        if (sourceIndex < 0) return null
        if (newModuleType != null) return listOf(element = identity).takeIf(predicate = { boundary == 0 })
        if (destinationModuleId != moduleId) {
            if (identity in destinationOrder || boundary !in 0..destinationOrder.size) return null
            return destinationOrder.toMutableList().apply(block = { add(index = boundary, element = identity) })
        }
        if (boundary !in 0..originalOrder.size) return null
        val targetIndex = boundary - if (sourceIndex < boundary) 1 else 0
        if (targetIndex == sourceIndex) return originalOrder
        return originalOrder.toMutableList().apply(
            block = {
                removeAt(index = sourceIndex)
                add(index = targetIndex, element = identity)
            },
        )
    }
}

/** Validates both orders against current durable state, never against a whole historical aggregate. */
internal fun applyApplicationOrderChange(
    aggregate: OrderedFavoriteAggregate,
    change: ApplicationOrderChange,
    newModuleId: String? = null,
): OrderedFavoriteAggregate? {
    val source = aggregate.modules.firstOrNull(predicate = { it.id == change.moduleId }) ?: return null
    if (source.identities != change.originalOrder) return null
    if (change.newModuleType != null) {
        val identities = change.reorderedIdentities() ?: return null
        if (newModuleId.isNullOrBlank() || aggregate.modules.any(predicate = { it.id == newModuleId })) return null
        val created = OrderedFavoriteModule(id = newModuleId, type = change.newModuleType, identities = identities)
        val remaining = source.identities.filterNot(predicate = { it == change.identity })
        return aggregate.copy(
            modules = aggregate.modules.mapNotNull(
                transform = { module ->
                    if (module.id != source.id) module else if (remaining.isEmpty()) null else module.copy(identities = remaining)
                },
            ) + created,
        )
    }
    val destination = aggregate.modules.firstOrNull(predicate = { it.id == change.destinationModuleId }) ?: return null
    val expectedDestination = if (source.id == destination.id) change.originalOrder else change.destinationOrder
    if (source.identities != change.originalOrder || destination.identities != expectedDestination) return null
    val reordered = change.reorderedIdentities() ?: return null
    return aggregate.copy(
        modules = aggregate.modules.mapNotNull(
            transform = { module ->
                when (module.id) {
                    destination.id -> module.copy(identities = reordered)
                    source.id -> {
                        val remaining = module.identities.filterNot(predicate = { it == change.identity })
                        if (remaining.isEmpty()) null else module.copy(identities = remaining)
                    }
                    else -> module
                }
            },
        ),
    )
}

internal data class ApplicationInsertion(
    val boundary: Int,
    val lineStart: Offset,
    val lineEnd: Offset,
)

/** Pure geometry: real cells only, with a distinct edge for each visual before/after candidate. */
internal fun resolveApplicationInsertion(
    module: OrderedFavoriteModule,
    pointer: Offset,
    viewport: Rect,
    moduleBounds: Rect,
    itemBounds: Map<LaunchableIdentity, Rect>,
    addBounds: Rect?,
): ApplicationInsertion? {
    val clip = viewport.intersect(other = moduleBounds)
    if (clip.isEmpty || !clip.contains(offset = pointer) || addBounds?.contains(offset = pointer) == true) {
        return null
    }
    val cells = module.identities.mapIndexedNotNull(
        transform = { index, identity -> itemBounds[identity]?.let(block = { index to it }) },
    )
    if (cells.isEmpty()) return null
    val horizontal = module.type == OrderedFavoriteModuleType.Ribbon || module.itemsPerRow > 1

    fun edge(cell: Pair<Int, Rect>, after: Boolean): ApplicationInsertion? = applicationInsertionEdge(
        cell = cell, after = after, horizontal = horizontal, clip = clip,
    )

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
    )
}

internal fun resolveApplicationDestination(
    modules: List<OrderedFavoriteModule>,
    pointer: Offset,
    viewport: Rect,
    moduleBounds: Map<String, Rect>,
    itemBounds: Map<LaunchableIdentity, Rect>,
    addBounds: Map<String, Rect>,
): Pair<OrderedFavoriteModule, ApplicationInsertion>? {
    if (!viewport.contains(offset = pointer)) return null
    val hit = modules.firstOrNull(predicate = { moduleBounds[it.id]?.contains(offset = pointer) == true })
    if (hit != null) {
        val insertion = resolveApplicationInsertion(
            module = hit, pointer = pointer, viewport = viewport,
            moduleBounds = moduleBounds.getValue(key = hit.id), itemBounds = itemBounds, addBounds = addBounds[hit.id],
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
    val index = if (beforeNext) 0 else module.identities.lastIndex
    val bounds = itemBounds[module.identities[index]] ?: return null
    val clip = viewport.intersect(other = moduleBounds.getValue(key = module.id))
    if (clip.isEmpty) return null
    val insertion = applicationInsertionEdge(
        cell = index to bounds, after = !beforeNext,
        horizontal = module.type == OrderedFavoriteModuleType.Ribbon || module.itemsPerRow > 1, clip = clip,
    ) ?: return null
    return module to insertion
}

internal data class HomeApplicationMovementSession(
    val module: OrderedFavoriteModule,
    val identity: LaunchableIdentity,
    val availability: FavoriteAvailability,
    val sourceBounds: Rect,
    val pointerOffset: Offset,
    val pointer: Offset,
    val insertion: ApplicationInsertion? = null,
    val destination: OrderedFavoriteModule? = null,
    val creation: Pair<OrderedFavoriteModuleType, Rect>? = null,
) {
    val previewOrigin: Offset get() = pointer - pointerOffset
}

/** Transient gesture state only. All durable writes continue through the App's favorite editor. */
@Stable
internal class HomeApplicationMovement {
    // Item/layout scopes observe ownership only; pointer updates invalidate just the overlay.
    var activeIdentity by mutableStateOf<LaunchableIdentity?>(value = null)
        private set
    var session by mutableStateOf<HomeApplicationMovementSession?>(value = null)
        private set
    var edgeFeedback by mutableStateOf<HomeApplicationScrollRequest?>(value = null)
        private set
    var viewport = Rect.Zero
        private set
    private val moduleBounds = mutableMapOf<String, Rect>()
    private val itemBounds = mutableMapOf<LaunchableIdentity, Rect>()
    private val removeBounds = mutableMapOf<LaunchableIdentity, Rect>()
    private val addBounds = mutableMapOf<String, Rect>()
    private val creationBounds = mutableMapOf<OrderedFavoriteModuleType, Rect>()
    private var modules: List<OrderedFavoriteModule> = emptyList()
    var geometryRevision by mutableStateOf(value = 0L)
        private set

    fun updateEdgeFeedback(request: HomeApplicationScrollRequest?) {
        edgeFeedback = request.takeIf(predicate = { activeIdentity != null })
    }

    fun updateModules(current: List<OrderedFavoriteModule>) {
        if (modules == current) return
        modules = current
        geometryRevision += 1
        refreshCandidate()
    }

    fun visibleRibbonAt(pointer: Offset): Pair<String, Rect>? {
        return modules.firstNotNullOfOrNull(
            transform = { module ->
                if (module.type != OrderedFavoriteModuleType.Ribbon) return@firstNotNullOfOrNull null
                val bounds = moduleBounds[module.id]?.intersect(other = viewport) ?: return@firstNotNullOfOrNull null
                (module.id to bounds).takeIf(predicate = {
                    !bounds.isEmpty && pointer.x in bounds.left..bounds.right && pointer.y in bounds.top..bounds.bottom
                })
            },
        )
    }

    fun updateViewport(bounds: Rect) {
        if (viewport == bounds) return
        viewport = bounds
        geometryRevision += 1
        refreshCandidate()
    }

    fun updateModule(id: String, bounds: Rect?) {
        if (moduleBounds[id] == bounds) return
        geometryRevision += 1
        if (bounds == null) {
            moduleBounds.remove(key = id)
            addBounds.remove(key = id)
        } else {
            moduleBounds[id] = bounds
        }
        refreshCandidate()
    }

    fun updateItem(identity: LaunchableIdentity, bounds: Rect?, remove: Rect? = null) {
        if (itemBounds[identity] == bounds && removeBounds[identity] == remove) return
        if (bounds == null) itemBounds.remove(key = identity) else itemBounds[identity] = bounds
        if (remove == null) removeBounds.remove(key = identity) else removeBounds[identity] = remove
        refreshCandidate()
    }

    fun updateAdd(id: String, bounds: Rect?) {
        if (addBounds[id] == bounds) return
        if (bounds == null) addBounds.remove(key = id) else addBounds[id] = bounds
        refreshCandidate()
    }

    fun updateCreationTarget(type: OrderedFavoriteModuleType, bounds: Rect?) {
        if (creationBounds[type] == bounds) return
        if (bounds == null) creationBounds.remove(key = type) else creationBounds[type] = bounds
        refreshCandidate()
    }

    fun hitIdentity(pointer: Offset): LaunchableIdentity? {
        if (!viewport.contains(offset = pointer)) return null
        return itemBounds.entries.firstOrNull(
            predicate = { (identity, bounds) ->
                bounds.contains(offset = pointer) && removeBounds[identity]?.contains(offset = pointer) != true
            },
        )?.key
    }

    fun start(
        identity: LaunchableIdentity,
        module: OrderedFavoriteModule,
        availability: FavoriteAvailability,
        pointer: Offset,
    ): Boolean {
        if (session != null || identity !in module.identities ||
            (availability !is FavoriteAvailability.Available && availability !is FavoriteAvailability.Disabled)
        ) return false
        val bounds = itemBounds[identity] ?: return false
        session = HomeApplicationMovementSession(
            module = module,
            identity = identity,
            availability = availability,
            sourceBounds = bounds,
            pointerOffset = pointer - bounds.topLeft,
            pointer = pointer,
        )
        activeIdentity = identity
        refreshCandidate()
        return true
    }

    fun move(pointer: Offset) {
        session = session?.copy(pointer = pointer)
        refreshCandidate()
    }

    private fun refreshCandidate() {
        val current = session ?: return
        val creation = creationBounds.entries.firstOrNull(
            predicate = { (_, bounds) -> viewport.contains(offset = current.pointer) && bounds.contains(offset = current.pointer) },
        )?.let(block = { it.key to it.value })
        if (creation != null) {
            session = current.copy(insertion = null, destination = null, creation = creation)
            return
        }
        val resolved = resolveApplicationDestination(
            modules = modules.ifEmpty(defaultValue = { listOf(element = current.module) }),
            pointer = current.pointer, viewport = viewport, moduleBounds = moduleBounds,
            itemBounds = itemBounds, addBounds = addBounds,
        )
        session = current.copy(
            insertion = resolved?.second,
            destination = resolved?.first,
            creation = null,
        )
    }

    fun cancel() {
        session = null
        activeIdentity = null
        edgeFeedback = null
    }

    fun finish(): ApplicationOrderChange? {
        refreshCandidate()
        val current = session ?: return null
        cancel()
        if (current.creation != null) {
            return ApplicationOrderChange(
                moduleId = current.module.id, identity = current.identity,
                originalOrder = current.module.identities, boundary = 0,
                newModuleType = current.creation.first,
            )
        }
        val insertion = current.insertion ?: return null
        val destination = current.destination ?: return null
        val change = ApplicationOrderChange(
            moduleId = current.module.id,
            identity = current.identity,
            originalOrder = current.module.identities,
            boundary = insertion.boundary,
            destinationModuleId = destination.id,
            destinationOrder = destination.identities,
        )
        return change.takeUnless(predicate = { it.destinationModuleId == it.moduleId && it.reorderedIdentities() == it.originalOrder })
    }
}
