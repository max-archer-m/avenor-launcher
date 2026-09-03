package com.avenor.launcher

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.toSize
import com.avenor.launcher.ui.home.components.HomeFavoriteItemFrame
import com.avenor.launcher.ui.home.components.HomeOrderedFavoriteRibbon
import com.avenor.launcher.ui.home.components.homeEditSurface

@Composable
internal fun HomeOrderedModuleContent(
    module: OrderedFavoriteModule,
    availabilityByIdentity: Map<LaunchableIdentity, FavoriteAvailability>,
    ribbonListState: LazyListState?,
    editMode: Boolean,
    showAddEntry: Boolean,
    addEntryEnabled: Boolean,
    onAddToModule: () -> Unit,
    onLaunchFavorite: (FavoriteAvailability) -> Unit,
    onLongPressFavorite: (LaunchableEntry) -> Unit,
    applicationEditing: Boolean = false,
    applicationMutationEnabled: Boolean = true,
    onRemoveFavorite: (LaunchableIdentity) -> Unit = {},
    applicationMovement: HomeApplicationMovement? = null,
) {
    when (module.type) {
        OrderedFavoriteModuleType.Vertical -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .homeEditSurface(enabled = editMode),
            ) {
                val cells: List<LaunchableIdentity?> =
                    module.identities.map { it } + if (showAddEntry) listOf(null) else emptyList()
                cells.chunked(module.itemsPerRow).forEach { rowItems ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        rowItems.forEach { identity ->
                            val itemModifier = Modifier.weight(1f)
                            if (identity == null) {
                                HomeModuleAddFavoriteEntry(
                                    modifier = itemModifier.onGloballyPositioned(
                                        onGloballyPositioned = { coordinates ->
                                            applicationMovement?.updateAdd(
                                                id = module.id,
                                                bounds = Rect(offset = coordinates.positionInWindow(), size = coordinates.size.toSize()),
                                            )
                                        },
                                    ),
                                    module = module,
                                    enabled = addEntryEnabled,
                                    onClick = onAddToModule,
                                )
                            } else {
                                val availability = availabilityByIdentity[identity]
                                    ?: FavoriteAvailability.Unknown(presentationEntry = null)
                                HomeFavoriteItemFrame(
                                    modifier = itemModifier,
                                    identity = identity,
                                    movement = applicationMovement,
                                    showRemove = applicationEditing,
                                    removeEnabled = applicationMutationEnabled,
                                    namePlacement = module.namePlacement,
                                    iconSize = dimensionResource(id = module.applicationSize.iconSizeResource()),
                                    onRemove = { onRemoveFavorite(identity) },
                                    content = {
                                        if (module.namePlacement == FavoriteNamePlacement.Below) {
                                            HomeFavoriteBelowItem(
                                                modifier = Modifier.fillMaxWidth(),
                                                availability = availability,
                                                listSize = module.applicationSize,
                                                interactionEnabled = !editMode,
                                                onClick = { onLaunchFavorite(availability) },
                                                onLongClick = {
                                                    availability.presentationEntry?.let(block = onLongPressFavorite)
                                                },
                                            )
                                        } else {
                                            HomeFavoriteRow(
                                                modifier = Modifier.fillMaxWidth(),
                                                availability = availability,
                                                onClick = { onLaunchFavorite(availability) },
                                                onLongClick = {
                                                    availability.presentationEntry?.let(block = onLongPressFavorite)
                                                },
                                                editMode = false,
                                                interactionEnabled = !editMode,
                                                compact = false,
                                                listSize = module.applicationSize,
                                                exchangeHighlight = false,
                                                onRowBoundsInWindow = { _, _ -> },
                                                onHandleBoundsInWindow = {},
                                            )
                                        }
                                    },
                                )
                            }
                        }
                        repeat(module.itemsPerRow - rowItems.size) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        OrderedFavoriteModuleType.Ribbon -> {
            HomeOrderedFavoriteRibbon(
                module = module,
                availabilityByIdentity = availabilityByIdentity,
                listState = requireNotNull(value = ribbonListState),
                editMode = editMode,
                showAddEntry = showAddEntry,
                addEntryEnabled = addEntryEnabled,
                onAddToModule = onAddToModule,
                onLaunchFavorite = onLaunchFavorite,
                onLongPressFavorite = onLongPressFavorite,
                applicationEditing = applicationEditing,
                applicationMutationEnabled = applicationMutationEnabled,
                onRemoveFavorite = onRemoveFavorite,
                applicationMovement = applicationMovement,
            )
        }
    }
}
