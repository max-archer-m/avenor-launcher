package com.avenor.launcher

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CancellationException
import java.util.UUID

/** One removal, not a historical copy of the entire Home composition. */
internal data class FavoriteRemovalSnapshot(
    val identity: LaunchableIdentity,
    val module: OrderedFavoriteModule,
    val moduleIndex: Int,
    val applicationIndex: Int,
    val moduleOrder: List<String>,
)

internal fun captureFavoriteRemoval(
    aggregate: OrderedFavoriteAggregate,
    identity: LaunchableIdentity,
): FavoriteRemovalSnapshot? {
    val moduleIndex = aggregate.modules.indexOfFirst(
        predicate = { module -> identity in module.identities },
    )
    if (moduleIndex < 0) return null
    val module = aggregate.modules[moduleIndex]
    return FavoriteRemovalSnapshot(
        identity = identity,
        module = module,
        moduleIndex = moduleIndex,
        applicationIndex = module.identities.indexOf(element = identity),
        moduleOrder = aggregate.modules.map(transform = { it.id }),
    )
}

internal fun removeOrderedFavorite(
    aggregate: OrderedFavoriteAggregate,
    identity: LaunchableIdentity,
): OrderedFavoriteAggregate = aggregate.copy(
    modules = aggregate.modules.mapNotNull(
        transform = { module ->
            val retained = module.identities.filterNot(predicate = { it == identity })
            when {
                retained.isEmpty() -> null
                retained == module.identities -> module
                else -> module.copy(identities = retained)
            }
        },
    ),
)

internal fun restoreOrderedFavorite(
    aggregate: OrderedFavoriteAggregate,
    snapshot: FavoriteRemovalSnapshot,
): OrderedFavoriteAggregate {
    if (snapshot.identity in aggregate.identities) return aggregate
    val modules = aggregate.modules.toMutableList()
    val moduleIndex = modules.indexOfFirst(predicate = { it.id == snapshot.module.id })
    if (moduleIndex < 0) {
        // Restore only this identity. Other members may have disappeared through reliable
        // inventory reconciliation since removal, and must never be resurrected by Undo.
        modules.add(
            index = restoredFavoritePosition(
                originalOrder = snapshot.moduleOrder,
                originalIndex = snapshot.moduleIndex,
                currentOrder = modules.map(transform = { it.id }),
            ),
            element = snapshot.module.copy(identities = listOf(element = snapshot.identity)),
        )
    } else {
        val module = modules[moduleIndex]
        if (module.type != snapshot.module.type) return aggregate
        val identities = module.identities.toMutableList()
        identities.add(
            index = restoredFavoritePosition(
                originalOrder = snapshot.module.identities,
                originalIndex = snapshot.applicationIndex,
                currentOrder = identities,
            ),
            element = snapshot.identity,
        )
        modules[moduleIndex] = module.copy(identities = identities)
    }
    return aggregate.copy(modules = modules)
}

private fun <T> restoredFavoritePosition(
    originalOrder: List<T>,
    originalIndex: Int,
    currentOrder: List<T>,
): Int {
    val successor = originalOrder.drop(n = originalIndex + 1)
        .firstOrNull(predicate = { it in currentOrder })
    if (successor != null) return currentOrder.indexOf(element = successor)
    val predecessor = originalOrder.take(n = originalIndex)
        .lastOrNull(predicate = { it in currentOrder })
    if (predecessor != null) return currentOrder.indexOf(element = predecessor) + 1
    return originalIndex.coerceIn(minimumValue = 0, maximumValue = currentOrder.size)
}

/** App-scoped so normal-Home action-sheet removal and edit-mode removal share one lifecycle. */
@Stable
internal class HomeFavoriteEditor(private val store: FavoriteStore) {
    var isSaving by mutableStateOf(value = false)
        private set

    var undoRemoval by mutableStateOf<FavoriteRemovalSnapshot?>(value = null)
        private set

    private var undoGeneration = 0L
    private val confirmedAbsent = mutableSetOf<LaunchableIdentity>()

    fun invalidateUndo() {
        undoGeneration += 1
        undoRemoval = null
    }

    fun dismissUndo(snapshot: FavoriteRemovalSnapshot) {
        if (undoRemoval === snapshot) invalidateUndo()
    }

    fun reconcileAvailability(availability: Map<LaunchableIdentity, FavoriteAvailability>) {
        availability.forEach(
            action = { (identity, state) ->
                when (state) {
                    FavoriteAvailability.ConfirmedRemoved -> confirmedAbsent.add(element = identity)
                    is FavoriteAvailability.Available, is FavoriteAvailability.Disabled ->
                        confirmedAbsent.remove(element = identity)
                    else -> Unit
                }
            },
        )
        val removedIdentity = undoRemoval?.identity
        if (removedIdentity != null && removedIdentity in confirmedAbsent) invalidateUndo()
    }

    suspend fun remove(identity: LaunchableIdentity): Boolean {
        if (isSaving) return false
        isSaving = true
        val generation = undoGeneration
        var snapshot: FavoriteRemovalSnapshot? = null
        return try {
            val persisted = store.updateOrderedAggregate(
                transform = { aggregate ->
                    snapshot = captureFavoriteRemoval(aggregate = aggregate, identity = identity)
                    removeOrderedFavorite(aggregate = aggregate, identity = identity)
                },
            ) ?: return false
            if (snapshot != null && identity !in persisted.identities && generation == undoGeneration) {
                invalidateUndo()
                if (identity !in confirmedAbsent) undoRemoval = snapshot
            }
            true
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: Exception) {
            false
        } finally {
            isSaving = false
        }
    }

    suspend fun undo(
        snapshot: FavoriteRemovalSnapshot,
        revalidate: suspend () -> Boolean = { true },
    ): Boolean {
        if (isSaving || undoRemoval !== snapshot) return false
        isSaving = true
        return try {
            if (!revalidate()) return false
            val persisted = store.updateOrderedAggregate(
                transform = { aggregate ->
                    if (undoRemoval !== snapshot || snapshot.identity in confirmedAbsent) {
                        aggregate
                    } else {
                        restoreOrderedFavorite(aggregate = aggregate, snapshot = snapshot)
                    }
                },
            )
            persisted != null && snapshot.identity in persisted.identities
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: Exception) {
            false
        } finally {
            dismissUndo(snapshot = snapshot)
            isSaving = false
        }
    }

    suspend fun reorderApplication(change: ApplicationOrderChange): Boolean {
        if (isSaving) return false
        val reordered = change.reorderedIdentities() ?: return false
        if (change.newModuleType == null && change.destinationModuleId == change.moduleId && reordered == change.originalOrder) return true
        isSaving = true
        var applied = false
        return try {
            val persisted = store.updateOrderedAggregate(
                transform = { aggregate ->
                    val updated = applyApplicationOrderChange(
                        aggregate = aggregate, change = change,
                        newModuleId = if (change.newModuleType == null) null else UUID.randomUUID().toString(),
                    )
                    // Revalidate both modules under the store mutex, including reliable inventory changes.
                    if (updated == null) {
                        aggregate
                    } else {
                        applied = true
                        updated
                    }
                },
            )
            (persisted != null && applied).also(block = { saved -> if (saved) invalidateUndo() })
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: Exception) {
            false
        } finally {
            isSaving = false
        }
    }

    suspend fun updateComposition(
        transform: (FavoriteAggregate) -> FavoriteAggregate,
    ): FavoriteAggregate? {
        if (isSaving) return null
        isSaving = true
        return try {
            val before = store.state.value
            store.updateAggregate(transform = transform).also(
                block = { saved ->
                    if (saved != null && before != store.state.value) invalidateUndo()
                },
            )
        } finally {
            isSaving = false
        }
    }

    suspend fun reorderModules(moduleIds: List<String>): Boolean {
        if (isSaving) return false
        isSaving = true
        return try {
            val before = store.state.value
            store.replaceModuleOrder(moduleIds = moduleIds).also(
                block = { saved ->
                    if (saved && before != store.state.value) invalidateUndo()
                },
            )
        } finally {
            isSaving = false
        }
    }
}
