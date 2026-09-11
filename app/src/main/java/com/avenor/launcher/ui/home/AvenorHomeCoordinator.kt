package com.avenor.launcher.ui.home

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.geometry.Offset
import com.avenor.launcher.ApplicationOrderChange
import com.avenor.launcher.FavoriteAggregate
import com.avenor.launcher.FavoriteContainerType
import com.avenor.launcher.FavoriteReadState
import com.avenor.launcher.FavoriteStore
import com.avenor.launcher.HomeFavoriteEditor
import com.avenor.launcher.LaunchableIdentity
import com.avenor.launcher.R
import com.avenor.launcher.ui.drawer.DrawerDragDrop
import com.avenor.launcher.ui.drawer.DrawerDragJourney
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** The Home-owned state and durable operations coordinated by the App composition root. */
internal data class FavoriteRevealRequest(
    val containerId: String,
    val containerType: FavoriteContainerType,
    val identity: LaunchableIdentity,
)

/**
 * Keeps Home editing, Drawer-to-Home handoff, and durable favorite mutations together while
 * leaving surface transitions and UI composition in [com.avenor.launcher.AvenorApp]. The App writes the current
 * reliable favorite state into [favoriteState] on every recomposition.
 */
@Stable
internal class AvenorHomeCoordinator(
    private val context: Context,
    private val scope: CoroutineScope,
    private val favoriteStore: FavoriteStore,
    private val favoriteEditor: HomeFavoriteEditor,
) {
    var favoriteState by mutableStateOf<FavoriteReadState>(FavoriteReadState.Loading)
    var editMode by mutableStateOf(false)
        private set
    var stylePanelExpanded by mutableStateOf(false)
        private set
    var selectedModuleId by mutableStateOf<String?>(null)
        private set
    var editMembership by mutableStateOf<Set<LaunchableIdentity>>(emptySet())
    var drawerDragJourney by mutableStateOf<DrawerDragJourney?>(null)
        private set
    var drawerDragTouchPosition by mutableStateOf(Offset.Zero)
    var drawerDragDropping by mutableStateOf(false)
        private set
    var favoriteRevealRequest by mutableStateOf<FavoriteRevealRequest?>(null)
        private set

    fun refreshEditMembership() {
        if (editMode) {
            editMembership = (favoriteStore.state.value as? FavoriteReadState.Readable)
                ?.identities
                ?.toSet()
                .orEmpty()
        }
    }

    fun requestEditMode() {
        editMembership = (favoriteState as? FavoriteReadState.Readable)
            ?.identities
            ?.toSet()
            .orEmpty()
        editMode = true
    }

    fun dismissEditMode() {
        editMode = false
    }

    fun updateStylePanelExpanded(expanded: Boolean) {
        stylePanelExpanded = expanded
    }

    fun selectModule(moduleId: String?) {
        selectedModuleId = moduleId
    }

    fun clearEditSelection() {
        stylePanelExpanded = false
        selectedModuleId = null
    }

    fun clearDrawerDrag() {
        drawerDragJourney = null
        drawerDragDropping = false
    }

    fun completeFavoriteReveal() {
        favoriteRevealRequest = null
    }

    fun startDrawerDrag(journey: DrawerDragJourney): Boolean {
        if (favoriteEditor.isSaving) return false
        favoriteEditor.invalidateUndo()
        drawerDragJourney = journey
        drawerDragTouchPosition = journey.touchStartInWindow
        editMode = true
        clearEditSelection()
        return true
    }

    fun moveDrawerDrag(position: Offset) {
        drawerDragTouchPosition = position
    }

    fun endDrawerDrag(cancelled: Boolean) {
        if (cancelled) {
            editMode = false
            selectedModuleId = null
        } else {
            drawerDragDropping = true
        }
    }

    suspend fun commitDrawerDrop(identity: LaunchableIdentity, drop: DrawerDragDrop): Boolean {
        val insertion = drop as? DrawerDragDrop.Insertion
        val creation = drop as? DrawerDragDrop.Creation
        return favoriteEditor.insertExternalFavorite(
            identity = identity,
            destinationModuleId = insertion?.moduleId,
            boundary = insertion?.boundary ?: 0,
            newModuleType = creation?.moduleType,
        )
    }

    fun finishDrawerDrag(saved: Boolean, cancelled: Boolean) {
        drawerDragDropping = false
        drawerDragJourney = null
        if (!saved && !cancelled) {
            Toast.makeText(
                context,
                R.string.drawer_drag_unable_to_save_favorite,
                Toast.LENGTH_SHORT,
            ).show()
        }
        editMode = false
        selectedModuleId = null
    }

    fun commitApplicationOrder(change: ApplicationOrderChange, complete: () -> Unit) {
        scope.launch {
            try {
                if (!favoriteEditor.reorderApplication(change = change)) {
                    Toast.makeText(
                        context,
                        R.string.unable_to_move_favorite,
                        Toast.LENGTH_SHORT,
                    ).show()
                }
                // The Flow collector may lag behind the completed write. Hand off only after
                // Home receives the current reliable state, never an old source projection.
                snapshotFlow { favoriteState }.first { presented ->
                    presented == favoriteStore.state.value
                }
            } finally {
                complete()
            }
        }
    }

    suspend fun commitFavoriteComposition(
        transform: (FavoriteAggregate) -> FavoriteAggregate,
    ): FavoriteAggregate? {
        val aggregate = favoriteEditor.updateComposition(transform = transform) ?: return null
        if (editMode) editMembership = aggregate.identities.toSet()
        return aggregate
    }
}
