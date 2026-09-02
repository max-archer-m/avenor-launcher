package com.avenor.launcher

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource

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
) {
    when (module.type) {
        OrderedFavoriteModuleType.Vertical -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .editSurface(editMode),
            ) {
                val cells: List<LaunchableIdentity?> =
                    module.identities.map { it } + if (showAddEntry) listOf(null) else emptyList()
                cells.chunked(module.itemsPerRow).forEach { rowItems ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        rowItems.forEach { identity ->
                            val itemModifier = Modifier.weight(1f)
                            if (identity == null) {
                                HomeModuleAddFavoriteEntry(
                                    modifier = itemModifier,
                                    module = module,
                                    enabled = addEntryEnabled,
                                    onClick = onAddToModule,
                                )
                            } else if (module.namePlacement == FavoriteNamePlacement.Below) {
                                val availability = availabilityByIdentity[identity]
                                    ?: FavoriteAvailability.Unknown(null)
                                HomeFavoriteBelowItem(
                                    modifier = itemModifier,
                                    availability = availability,
                                    listSize = module.applicationSize,
                                    onClick = { onLaunchFavorite(availability) },
                                    onLongClick = {
                                        availability.presentationEntry?.let(onLongPressFavorite)
                                    },
                                )
                            } else {
                                val availability = availabilityByIdentity[identity]
                                    ?: FavoriteAvailability.Unknown(null)
                                HomeFavoriteRow(
                                    modifier = itemModifier,
                                    availability = availability,
                                    onClick = { onLaunchFavorite(availability) },
                                    onLongClick = {
                                        availability.presentationEntry?.let(onLongPressFavorite)
                                    },
                                    editMode = false,
                                    compact = false,
                                    listSize = module.applicationSize,
                                    exchangeHighlight = false,
                                    onRowBoundsInWindow = { _, _ -> },
                                    onHandleBoundsInWindow = {},
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
            val state = requireNotNull(ribbonListState)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.home_favorite_bar_height))
                    .editSurface(editMode),
                state = state,
                horizontalArrangement = Arrangement.spacedBy(
                    dimensionResource(R.dimen.home_favorite_bar_item_spacing),
                ),
            ) {
                items(
                    items = module.identities,
                    key = { it.stableKey() },
                ) { identity ->
                    val availability = availabilityByIdentity[identity]
                        ?: FavoriteAvailability.Unknown(null)
                    HomeFavoriteRow(
                        modifier = Modifier.width(
                            dimensionResource(R.dimen.home_favorite_bar_item_width),
                        ),
                        availability = availability,
                        onClick = { onLaunchFavorite(availability) },
                        onLongClick = {
                            availability.presentationEntry?.let(onLongPressFavorite)
                        },
                        editMode = false,
                        compact = true,
                        listSize = FavoriteListSize.Medium,
                        exchangeHighlight = false,
                        onRowBoundsInWindow = { _, _ -> },
                        onHandleBoundsInWindow = {},
                    )
                }
                if (showAddEntry) {
                    item(key = "add:${module.id}") {
                        HomeModuleAddFavoriteEntry(
                            modifier = Modifier.width(
                                dimensionResource(R.dimen.home_favorite_bar_item_width),
                            ),
                            module = module,
                            enabled = addEntryEnabled,
                            onClick = onAddToModule,
                        )
                    }
                }
            }
        }
    }
}
