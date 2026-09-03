package com.avenor.launcher.ui.home.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.core.graphics.drawable.toBitmap
import com.avenor.launcher.ApplicationDragContainerDescriptor
import com.avenor.launcher.ApplicationDragTargetMode
import com.avenor.launcher.ApplicationEdgeScroll
import com.avenor.launcher.FavoriteAvailability
import com.avenor.launcher.FavoriteContainer
import com.avenor.launcher.LaunchableEntry
import com.avenor.launcher.LaunchableIdentity
import com.avenor.launcher.FavoriteListSize
import com.avenor.launcher.FavoriteNamePlacement
import com.avenor.launcher.HomeFavoriteRow
import com.avenor.launcher.HomeApplicationMovement
import com.avenor.launcher.HomeModuleAddFavoriteEntry
import com.avenor.launcher.OrderedFavoriteModule
import com.avenor.launcher.R
import com.avenor.launcher.applicationDragDescriptor
import com.avenor.launcher.applicationDragKey
import com.avenor.launcher.stableKey

internal class HomeFavoriteRibbonLayoutRegistry(
    val listStates: MutableMap<String, LazyListState>,
    val ribbonBoundsInWindow: MutableMap<String, Rect>,
    val applicationContainerBoundsInWindow: MutableMap<String, Rect>,
    val applicationContainerDescriptors: MutableMap<String, ApplicationDragContainerDescriptor>,
    val applicationItemBoundsInWindow: MutableMap<String, Rect>,
)

internal data class HomeFavoriteRibbonDragState(
    val applicationDropTargetKey: String?,
    val applicationEdgeScroll: ApplicationEdgeScroll?,
    val applicationDropTargetIdentity: LaunchableIdentity?,
    val applicationDropTargetMode: ApplicationDragTargetMode?,
    val applicationDropTargetIndex: Int?,
    val draggedIdentity: LaunchableIdentity?,
    val draggedRibbonId: String?,
    val highlightedRibbonId: String?,
)

internal interface HomeFavoriteRibbonActions {
    fun launchFavorite(availability: FavoriteAvailability)

    fun longPressFavorite(entry: LaunchableEntry)

    fun addFavorites(ribbonId: String)

    fun removeFavorite(
        ribbonId: String,
        identity: LaunchableIdentity,
    )

    fun removeRibbon(ribbonId: String)
}

internal interface HomeFavoriteRibbonDragActions {
    fun startRibbonDrag(
        ribbon: FavoriteContainer,
        index: Int,
        touch: Offset,
    )

    fun dragRibbon(delta: Offset)

    fun finishRibbonDrag()

    fun cancelRibbonDrag()

    fun startApplicationDrag(
        ribbon: FavoriteContainer,
        identity: LaunchableIdentity,
        origin: Offset,
        size: IntSize,
        touch: Offset,
    )

    fun dragApplication(delta: Offset)

    fun finishApplicationDrag()

    fun cancelApplicationDrag()
}

@Composable
internal fun HomeOrderedFavoriteRibbon(
    module: OrderedFavoriteModule,
    availabilityByIdentity: Map<LaunchableIdentity, FavoriteAvailability>,
    listState: LazyListState,
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
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = dimensionResource(id = R.dimen.home_favorite_bar_height))
            .homeEditSurface(enabled = editMode),
        state = listState,
        userScrollEnabled = applicationMovement?.activeIdentity == null,
        horizontalArrangement = Arrangement.spacedBy(
            space = dimensionResource(id = R.dimen.home_favorite_bar_item_spacing),
        ),
        content = {
            itemsIndexed(
                items = module.identities,
                key = { _, identity -> identity.stableKey() },
                itemContent = { _, identity ->
                    val availability = availabilityByIdentity[identity]
                        ?: FavoriteAvailability.Unknown(presentationEntry = null)
                    HomeFavoriteItemFrame(
                        identity = identity,
                        movement = applicationMovement,
                        ribbon = true,
                        modifier = Modifier.width(
                            width = dimensionResource(id = R.dimen.home_favorite_bar_item_width),
                        ),
                        showRemove = applicationEditing,
                        removeEnabled = applicationMutationEnabled,
                        namePlacement = FavoriteNamePlacement.Right,
                        iconSize = dimensionResource(id = R.dimen.home_favorite_icon_size),
                        onRemove = { onRemoveFavorite(identity) },
                        content = {
                            HomeFavoriteRow(
                                modifier = Modifier.fillMaxWidth(),
                                availability = availability,
                                onClick = { onLaunchFavorite(availability) },
                                onLongClick = {
                                    availability.presentationEntry?.let(block = onLongPressFavorite)
                                },
                                editMode = false,
                                interactionEnabled = !editMode,
                                compact = true,
                                listSize = FavoriteListSize.Medium,
                                exchangeHighlight = false,
                                onRowBoundsInWindow = { _, _ -> },
                                onHandleBoundsInWindow = {},
                            )
                        },
                    )
                },
            )
            if (showAddEntry) {
                item(
                    key = "add:${module.id}",
                    content = {
                        DisposableEffect(
                            key1 = applicationMovement,
                            key2 = module.id,
                            effect = { onDispose { applicationMovement?.updateAdd(id = module.id, bounds = null) } },
                        )
                        HomeModuleAddFavoriteEntry(
                            modifier = Modifier.onGloballyPositioned(
                                onGloballyPositioned = { coordinates ->
                                    applicationMovement?.updateAdd(
                                        id = module.id,
                                        bounds = Rect(offset = coordinates.positionInWindow(), size = coordinates.size.toSize()),
                                    )
                                },
                            ).width(
                                width = dimensionResource(
                                    id = R.dimen.home_favorite_bar_item_width,
                                ),
                            ),
                            module = module,
                            enabled = addEntryEnabled,
                            onClick = onAddToModule,
                        )
                    },
                )
            }
        },
    )
}

@Composable
internal fun HomeFavoriteRibbon(
    favoriteRibbons: List<FavoriteContainer>,
    availabilityByIdentity: Map<LaunchableIdentity, FavoriteAvailability>,
    editMode: Boolean,
    layoutRegistry: HomeFavoriteRibbonLayoutRegistry,
    dragState: HomeFavoriteRibbonDragState,
    actions: HomeFavoriteRibbonActions,
    dragActions: HomeFavoriteRibbonDragActions,
) {
    val borderAlpha = integerResource(
        id = R.integer.home_favorite_bar_border_alpha_percent,
    ) / 100f
    val fadeAlpha = integerResource(
        id = R.integer.home_favorite_bar_overflow_fade_alpha_percent,
    ) / 100f
    val fadeWidthPx = with(
        receiver = LocalDensity.current,
        block = {
            dimensionResource(id = R.dimen.home_favorite_bar_overflow_fade_width).toPx()
        },
    )
    val fadeColor = MaterialTheme.colorScheme.background.copy(alpha = fadeAlpha)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .homeEditSurface(enabled = editMode),
        verticalArrangement = Arrangement.spacedBy(
            space = dimensionResource(id = R.dimen.home_favorite_bar_spacing),
        ),
        content = {
            favoriteRibbons.forEachIndexed(
                action = { ribbonIndex, ribbon ->
                    FavoriteRibbon(
                        ribbon = ribbon,
                        ribbonIndex = ribbonIndex,
                        ribbonCount = favoriteRibbons.size,
                        availabilityByIdentity = availabilityByIdentity,
                        editMode = editMode,
                        borderAlpha = borderAlpha,
                        fadeColor = fadeColor,
                        fadeWidthPx = fadeWidthPx,
                        layoutRegistry = layoutRegistry,
                        dragState = dragState,
                        actions = actions,
                        dragActions = dragActions,
                    )
                },
            )
        },
    )
}

@Composable
private fun FavoriteRibbon(
    ribbon: FavoriteContainer,
    ribbonIndex: Int,
    ribbonCount: Int,
    availabilityByIdentity: Map<LaunchableIdentity, FavoriteAvailability>,
    editMode: Boolean,
    borderAlpha: Float,
    fadeColor: Color,
    fadeWidthPx: Float,
    layoutRegistry: HomeFavoriteRibbonLayoutRegistry,
    dragState: HomeFavoriteRibbonDragState,
    actions: HomeFavoriteRibbonActions,
    dragActions: HomeFavoriteRibbonDragActions,
) {
    val applicationDragKey = ribbon.applicationDragKey()
    DisposableEffect(
        key1 = ribbon.id,
        effect = {
            onDispose {
                layoutRegistry.ribbonBoundsInWindow.remove(key = ribbon.id)
                layoutRegistry.applicationContainerBoundsInWindow.remove(
                    key = applicationDragKey,
                )
                layoutRegistry.applicationContainerDescriptors.remove(key = applicationDragKey)
                layoutRegistry.applicationItemBoundsInWindow.keys
                    .filter(predicate = { key -> key.startsWith(prefix = "$applicationDragKey:") })
                    .forEach(action = layoutRegistry.applicationItemBoundsInWindow::remove)
            }
        },
    )
    val listState = layoutRegistry.listStates.getOrPut(
        key = ribbon.id,
        defaultValue = { LazyListState() },
    )
    val ribbonShape = RoundedCornerShape(
        size = dimensionResource(id = R.dimen.home_favorite_bar_corner_radius),
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = dimensionResource(id = R.dimen.home_favorite_bar_height))
            .then(
                other = if (editMode) {
                    Modifier.border(
                        width = dimensionResource(id = R.dimen.home_favorite_bar_border_width),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = borderAlpha),
                        shape = ribbonShape,
                    )
                } else {
                    Modifier
                },
            )
            .onGloballyPositioned(
                onGloballyPositioned = { coordinates ->
                    layoutRegistry.ribbonBoundsInWindow[ribbon.id] = Rect(
                        offset = coordinates.positionInWindow(),
                        size = coordinates.size.toSize(),
                    )
                },
            )
            .then(
                other = if (ribbon.id == dragState.draggedRibbonId) {
                    Modifier
                        .clearAndSetSemantics(properties = {})
                        .alpha(alpha = 0f)
                } else {
                    Modifier
                },
            )
            .then(
                other = if (
                    ribbon.id == dragState.highlightedRibbonId ||
                    dragState.applicationDropTargetKey == applicationDragKey
                ) {
                    Modifier.border(
                        width = dimensionResource(
                            id = R.dimen.home_favorite_exchange_border_width,
                        ),
                        color = colorResource(id = R.color.home_favorite_exchange_border),
                        shape = ribbonShape,
                    )
                } else {
                    Modifier
                },
            )
            .testTag(tag = "home_favorite_bar_$ribbonIndex"),
        content = {
            if (editMode) {
                FavoriteRibbonRemoveControl(
                    index = ribbonIndex,
                    ribbonId = ribbon.id,
                    actions = actions,
                )
                HomeFavoriteRibbonRailDivider()
            }
            FavoriteRibbonApplications(
                ribbon = ribbon,
                ribbonIndex = ribbonIndex,
                availabilityByIdentity = availabilityByIdentity,
                editMode = editMode,
                listState = listState,
                applicationDragKey = applicationDragKey,
                fadeColor = fadeColor,
                fadeWidthPx = fadeWidthPx,
                layoutRegistry = layoutRegistry,
                dragState = dragState,
                actions = actions,
                dragActions = dragActions,
                modifier = Modifier
                    .weight(weight = 1f)
                    .fillMaxHeight(),
            )
            if (editMode) {
                HomeFavoriteRibbonRailDivider()
                FavoriteRibbonReorderControl(
                    index = ribbonIndex,
                    visible = ribbonCount >= 2,
                    ribbon = ribbon,
                    dragActions = dragActions,
                )
            }
        },
    )
}

@Composable
private fun FavoriteRibbonApplications(
    ribbon: FavoriteContainer,
    ribbonIndex: Int,
    availabilityByIdentity: Map<LaunchableIdentity, FavoriteAvailability>,
    editMode: Boolean,
    listState: LazyListState,
    applicationDragKey: String,
    fadeColor: Color,
    fadeWidthPx: Float,
    layoutRegistry: HomeFavoriteRibbonLayoutRegistry,
    dragState: HomeFavoriteRibbonDragState,
    actions: HomeFavoriteRibbonActions,
    dragActions: HomeFavoriteRibbonDragActions,
    modifier: Modifier,
) {
    val ribbonShape = RoundedCornerShape(
        size = dimensionResource(id = R.dimen.home_favorite_bar_corner_radius),
    )
    Box(
        modifier = modifier
            .onGloballyPositioned(
                onGloballyPositioned = { coordinates ->
                    val bounds = Rect(
                        offset = coordinates.positionInWindow(),
                        size = coordinates.size.toSize(),
                    )
                    layoutRegistry.applicationContainerBoundsInWindow[applicationDragKey] = bounds
                    layoutRegistry.applicationContainerDescriptors[applicationDragKey] =
                        ribbon.applicationDragDescriptor(bounds = bounds)
                },
            )
            .clip(shape = ribbonShape)
            .drawWithContent(
                onDraw = {
                    drawContent()
                    if (dragState.applicationEdgeScroll?.containerKey == applicationDragKey) {
                        drawRect(
                            color = fadeColor.copy(
                                alpha = dragState.applicationEdgeScroll.proximity,
                            ),
                            topLeft = if (dragState.applicationEdgeScroll.forward) {
                                Offset(x = size.width - fadeWidthPx, y = 0f)
                            } else {
                                Offset.Zero
                            },
                            size = Size(width = fadeWidthPx, height = size.height),
                        )
                    }
                    if (listState.canScrollBackward) {
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    fadeColor,
                                    fadeColor.copy(alpha = 0f),
                                ),
                                startX = 0f,
                                endX = fadeWidthPx,
                            ),
                            size = Size(width = fadeWidthPx, height = size.height),
                        )
                    }
                    if (listState.canScrollForward) {
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    fadeColor.copy(alpha = 0f),
                                    fadeColor,
                                ),
                                startX = size.width - fadeWidthPx,
                                endX = size.width,
                            ),
                            topLeft = Offset(x = size.width - fadeWidthPx, y = 0f),
                            size = Size(width = fadeWidthPx, height = size.height),
                        )
                    }
                },
            ),
        content = {
            LazyRow(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                horizontalArrangement = Arrangement.spacedBy(
                    space = dimensionResource(id = R.dimen.home_favorite_bar_item_spacing),
                ),
                verticalAlignment = Alignment.CenterVertically,
                content = {
                    itemsIndexed(
                        items = ribbon.identities,
                        key = { _, identity -> identity.stableKey() },
                        itemContent = { index, identity ->
                            val availability = availabilityByIdentity[identity]
                                ?: FavoriteAvailability.Unknown(presentationEntry = null)
                            HomeFavoriteRibbonItem(
                                availability = availability,
                                editMode = editMode,
                                sourcePlaceholder = identity == dragState.draggedIdentity,
                                ribbon = ribbon,
                                identity = identity,
                                applicationDragKey = applicationDragKey,
                                layoutRegistry = layoutRegistry,
                                actions = actions,
                                dragActions = dragActions,
                                exchangeHighlight =
                                    dragState.applicationDropTargetKey == applicationDragKey &&
                                            dragState.applicationDropTargetMode ==
                                            ApplicationDragTargetMode.Exchange &&
                                            dragState.applicationDropTargetIdentity == identity,
                                insertionHighlight =
                                    dragState.applicationDropTargetKey == applicationDragKey &&
                                            dragState.applicationDropTargetMode ==
                                            ApplicationDragTargetMode.Insertion &&
                                            dragState.applicationDropTargetIndex == index,
                            )
                        },
                    )
                    if (editMode) {
                        item(
                            key = "favorite_bar_add_${ribbon.id}",
                            content = {
                                Box(
                                    modifier = Modifier
                                        .width(
                                            width = dimensionResource(
                                                id = R.dimen.home_favorite_bar_item_width,
                                            ),
                                        )
                                        .fillMaxHeight(),
                                    contentAlignment = Alignment.Center,
                                    content = {
                                        HomeFavoriteAddControl(
                                            onClick = {
                                                actions.addFavorites(ribbonId = ribbon.id)
                                            },
                                            testTag = "favorite_bar_add_$ribbonIndex",
                                            labelRes = R.string.add_apps,
                                        )
                                    },
                                )
                            },
                        )
                    }
                },
            )
        },
    )
}

@Composable
internal fun HomeFavoriteRibbonRailDivider() {
    Box(
        modifier = Modifier
            .width(width = dimensionResource(id = R.dimen.home_favorite_bar_rail_divider_width))
            .fillMaxHeight()
            .background(color = colorResource(id = R.color.home_favorite_bar_rail_divider)),
    )
}

@Composable
private fun FavoriteRibbonRemoveControl(
    index: Int,
    ribbonId: String,
    actions: HomeFavoriteRibbonActions,
) {
    var dialogVisible by remember(
        key1 = index,
        calculation = { mutableStateOf(value = false) },
    )
    val interactionSource = remember(
        key1 = index,
        calculation = { MutableInteractionSource() },
    )
    val description = stringResource(id = R.string.remove_favorite_bar)

    Box(
        modifier = Modifier
            .width(width = dimensionResource(id = R.dimen.home_favorite_bar_control_target_width))
            .fillMaxHeight()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = { dialogVisible = true },
            )
            .semantics(
                properties = { contentDescription = description },
            )
            .testTag(tag = "remove_favorite_bar_$index"),
        contentAlignment = Alignment.Center,
        content = {
            Box(
                modifier = Modifier
                    .size(
                        size = dimensionResource(
                            id = R.dimen.home_favorite_list_remove_badge_size,
                        ),
                    )
                    .clip(
                        shape = RoundedCornerShape(
                            size = dimensionResource(
                                id = R.dimen.home_favorite_list_control_surface_radius,
                            ),
                        ),
                    )
                    .background(color = MaterialTheme.colorScheme.error),
                contentAlignment = Alignment.Center,
                content = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = null,
                        modifier = Modifier.size(
                            size = dimensionResource(
                                id = R.dimen.home_favorite_list_remove_icon_size,
                            ),
                        ),
                        tint = colorResource(id = R.color.home_favorite_remove_icon),
                    )
                },
            )
        },
    )
    if (dialogVisible) {
        AlertDialog(
            onDismissRequest = { dialogVisible = false },
            title = {
                Text(text = stringResource(id = R.string.remove_favorite_bar_title))
            },
            text = {
                Text(text = stringResource(id = R.string.remove_favorite_bar_body))
            },
            dismissButton = {
                TextButton(
                    onClick = { dialogVisible = false },
                    content = {
                        Text(text = stringResource(id = R.string.cancel))
                    },
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        dialogVisible = false
                        actions.removeRibbon(ribbonId = ribbonId)
                    },
                    modifier = Modifier.testTag(tag = "confirm_remove_favorite_bar_$index"),
                    content = {
                        Text(
                            text = stringResource(id = R.string.remove),
                            color = MaterialTheme.colorScheme.error,
                        )
                    },
                )
            },
        )
    }
}

@Composable
private fun FavoriteRibbonReorderControl(
    index: Int,
    visible: Boolean,
    ribbon: FavoriteContainer,
    dragActions: HomeFavoriteRibbonDragActions,
) {
    var originInWindow by remember(
        key1 = index,
        calculation = { mutableStateOf(value = Offset.Zero) },
    )
    var dragging by remember(
        key1 = index,
        calculation = { mutableStateOf(value = false) },
    )
    val description = stringResource(id = R.string.favorite_bar_reorder_handle)
    val hapticFeedback = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .width(width = dimensionResource(id = R.dimen.home_favorite_bar_reorder_target_width))
            .fillMaxHeight()
            .then(
                other = if (visible) {
                    Modifier
                        .onGloballyPositioned(
                            onGloballyPositioned = { coordinates ->
                                originInWindow = coordinates.positionInWindow()
                            },
                        )
                        .pointerInput(
                            key1 = index,
                            block = {
                                detectHomeReorderDrag(
                                    onPressChanged = { pressed -> dragging = pressed },
                                    onLongPress = {
                                        hapticFeedback.performHapticFeedback(
                                            hapticFeedbackType = HapticFeedbackType.LongPress,
                                        )
                                    },
                                    onDragStart = { touch ->
                                        dragActions.startRibbonDrag(
                                            ribbon = ribbon,
                                            index = index,
                                            touch = originInWindow + touch,
                                        )
                                    },
                                    onDrag = { delta ->
                                        dragActions.dragRibbon(delta = delta)
                                    },
                                    onDragEnd = {
                                        dragging = false
                                        dragActions.finishRibbonDrag()
                                    },
                                    onDragCancel = {
                                        dragging = false
                                        dragActions.cancelRibbonDrag()
                                    },
                                )
                            },
                        )
                        .semantics(
                            properties = {
                                contentDescription = description
                                role = Role.Button
                            },
                        )
                } else {
                    Modifier.clearAndSetSemantics(properties = {})
                },
            )
            .testTag(tag = "reorder_favorite_bar_$index"),
        contentAlignment = Alignment.Center,
        content = {
            if (visible) {
                Box(
                    modifier = Modifier
                        .size(
                            size = dimensionResource(
                                id = R.dimen.home_favorite_bar_control_state_size,
                            ),
                        )
                        .clip(
                            shape = RoundedCornerShape(
                                size = dimensionResource(
                                    id = R.dimen.home_favorite_list_control_surface_radius,
                                ),
                            ),
                        )
                        .then(
                            other = if (dragging) {
                                Modifier.background(
                                    color = colorResource(
                                        id = R.color.home_favorite_list_control_pressed,
                                    ),
                                )
                            } else {
                                Modifier
                            },
                        ),
                    contentAlignment = Alignment.Center,
                    content = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_drag_handle),
                            contentDescription = null,
                            modifier = Modifier.size(
                                size = dimensionResource(
                                    id = R.dimen.home_favorite_bar_control_icon_size,
                                ),
                            ),
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    },
                )
            }
        },
    )
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun HomeFavoriteRibbonItem(
    availability: FavoriteAvailability,
    editMode: Boolean,
    sourcePlaceholder: Boolean,
    ribbon: FavoriteContainer,
    identity: LaunchableIdentity,
    applicationDragKey: String,
    layoutRegistry: HomeFavoriteRibbonLayoutRegistry,
    actions: HomeFavoriteRibbonActions,
    dragActions: HomeFavoriteRibbonDragActions,
    exchangeHighlight: Boolean = false,
    insertionHighlight: Boolean = false,
) {
    val entry = availability.presentationEntry
    val iconSize = dimensionResource(id = R.dimen.home_favorite_icon_size)
    val iconPixels = with(
        receiver = LocalDensity.current,
        block = { iconSize.roundToPx() },
    )
    val disabledAlpha = integerResource(
        id = R.integer.disabled_content_alpha_percent,
    ) / 100f
    val itemBackgroundAlpha = integerResource(
        id = R.integer.home_favorite_bar_item_background_alpha_percent,
    ) / 100f
    val borderAlpha = integerResource(
        id = R.integer.home_favorite_bar_border_alpha_percent,
    ) / 100f
    val handleTargetWidthPx = with(
        receiver = LocalDensity.current,
        block = {
            dimensionResource(id = R.dimen.home_favorite_bar_drag_target_width).toPx()
        },
    )
    val interactionSource = remember(
        key1 = entry?.identity,
        calculation = { MutableInteractionSource() },
    )
    val removeInteractionSource = remember(
        key1 = entry?.identity,
        calculation = { MutableInteractionSource() },
    )
    val hapticFeedback = LocalHapticFeedback.current
    val displayText = when (availability) {
        is FavoriteAvailability.Available -> availability.entry.label
        is FavoriteAvailability.Disabled -> entry?.let(
            block = {
                stringResource(
                    id = R.string.favorite_disabled_format,
                    formatArgs = arrayOf(it.label),
                )
            },
        ) ?: stringResource(id = R.string.favorite_application_disabled)

        is FavoriteAvailability.TemporarilyUnavailable,
        is FavoriteAvailability.Unknown,
            -> entry?.let(
            block = {
                stringResource(
                    id = R.string.favorite_unavailable_format,
                    formatArgs = arrayOf(it.label),
                )
            },
        ) ?: stringResource(id = R.string.favorite_application_unavailable)

        FavoriteAvailability.ConfirmedRemoved ->
            stringResource(id = R.string.favorite_application_unavailable)
    }
    var itemOriginInWindow by remember(
        key1 = entry?.identity,
        calculation = { mutableStateOf(value = Offset.Zero) },
    )
    var itemSize by remember(
        key1 = entry?.identity,
        calculation = { mutableStateOf(value = IntSize.Zero) },
    )

    Box(
        modifier = Modifier
            .widthIn(max = dimensionResource(id = R.dimen.home_favorite_bar_item_width))
            .fillMaxHeight()
            .onGloballyPositioned(
                onGloballyPositioned = { coordinates ->
                    itemOriginInWindow = coordinates.positionInWindow()
                    itemSize = coordinates.size
                    layoutRegistry.applicationItemBoundsInWindow[
                        "$applicationDragKey:${identity.stableKey()}"
                    ] = Rect(
                        offset = coordinates.positionInWindow(),
                        size = coordinates.size.toSize(),
                    )
                },
            )
            .background(
                color = MaterialTheme.colorScheme.onBackground.copy(
                    alpha = itemBackgroundAlpha,
                ),
                shape = RoundedCornerShape(
                    size = dimensionResource(id = R.dimen.home_favorite_bar_corner_radius),
                ),
            )
            .border(
                width = dimensionResource(id = R.dimen.home_favorite_bar_border_width),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = borderAlpha),
                shape = RoundedCornerShape(
                    size = dimensionResource(id = R.dimen.home_favorite_bar_corner_radius),
                ),
            )
            .combinedClickable(
                enabled = !editMode,
                interactionSource = interactionSource,
                indication = if (editMode) {
                    null
                } else {
                    ripple(color = colorResource(id = R.color.home_favorite_ripple))
                },
                role = Role.Button,
                onClick = {
                    actions.launchFavorite(availability = availability)
                },
                onLongClick = {
                    if (entry != null) {
                        hapticFeedback.performHapticFeedback(
                            hapticFeedbackType = HapticFeedbackType.LongPress,
                        )
                        actions.longPressFavorite(entry = entry)
                    }
                },
            )
            .alpha(
                alpha = if (availability is FavoriteAvailability.Available) 1f else disabledAlpha,
            )
            .then(
                other = if (sourcePlaceholder) Modifier.alpha(alpha = 0f) else Modifier,
            )
            .then(
                other = if (exchangeHighlight) {
                    Modifier.border(
                        width = dimensionResource(
                            id = R.dimen.home_favorite_exchange_border_width,
                        ),
                        color = colorResource(id = R.color.home_favorite_exchange_border),
                        shape = RoundedCornerShape(
                            size = dimensionResource(
                                id = R.dimen.home_favorite_exchange_border_radius,
                            ),
                        ),
                    )
                } else {
                    Modifier
                },
            )
            .testTag(tag = "home_favorite_bar_item"),
        content = {
            FavoriteRibbonItemIntrinsicWidthContent(
                displayText = displayText,
                iconSize = iconSize,
            )
            Row(
                modifier = Modifier
                    .matchParentSize()
                    .padding(
                        start = dimensionResource(id = R.dimen.home_favorite_bar_item_inset),
                        end = if (editMode) {
                            dimensionResource(id = R.dimen.home_favorite_bar_drag_target_width)
                        } else {
                            dimensionResource(id = R.dimen.home_favorite_bar_item_inset)
                        },
                    ),
                verticalAlignment = Alignment.CenterVertically,
                content = {
                    if (entry == null) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_inventory_error),
                            contentDescription = null,
                            modifier = Modifier.size(size = iconSize),
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    } else {
                        val bitmap = entry.iconBitmap?.asImageBitmap() ?: remember(
                            key1 = entry.icon,
                            key2 = iconPixels,
                            calculation = {
                                entry.icon.toBitmap(
                                    width = iconPixels,
                                    height = iconPixels,
                                ).asImageBitmap()
                            },
                        )
                        Image(
                            bitmap = bitmap,
                            contentDescription = null,
                            modifier = Modifier.size(size = iconSize),
                        )
                    }
                    Spacer(
                        modifier = Modifier.width(
                            width = dimensionResource(
                                id = R.dimen.home_favorite_bar_icon_label_gap,
                            ),
                        ),
                    )
                    Text(
                        text = displayText,
                        modifier = Modifier.weight(weight = 1f),
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = dimensionResource(
                            id = R.dimen.home_favorite_text_size,
                        ).value.sp,
                        lineHeight = dimensionResource(
                            id = R.dimen.home_favorite_line_height,
                        ).value.sp,
                    )
                },
            )
            if (editMode) {
                FavoriteRibbonItemEditControls(
                    ribbon = ribbon,
                    identity = identity,
                    itemOriginInWindow = itemOriginInWindow,
                    itemSize = itemSize,
                    handleTargetWidthPx = handleTargetWidthPx,
                    removeInteractionSource = removeInteractionSource,
                    hapticFeedback = hapticFeedback,
                    actions = actions,
                    dragActions = dragActions,
                )
            }
            if (insertionHighlight) {
                Box(
                    modifier = Modifier
                        .align(alignment = Alignment.CenterStart)
                        .width(
                            width = dimensionResource(
                                id = R.dimen.home_favorite_insertion_line_thickness,
                            ),
                        )
                        .fillMaxHeight()
                        .background(
                            color = colorResource(id = R.color.home_favorite_insertion_line),
                        ),
                )
            }
        },
    )
}

@Composable
private fun FavoriteRibbonItemIntrinsicWidthContent(
    displayText: String,
    iconSize: Dp,
) {
    Row(
        modifier = Modifier
            .fillMaxHeight()
            .padding(
                horizontal = dimensionResource(id = R.dimen.home_favorite_bar_item_inset),
            )
            .alpha(alpha = 0f)
            .clearAndSetSemantics(properties = {}),
        verticalAlignment = Alignment.CenterVertically,
        content = {
            Spacer(modifier = Modifier.size(size = iconSize))
            Spacer(
                modifier = Modifier.width(
                    width = dimensionResource(id = R.dimen.home_favorite_bar_icon_label_gap),
                ),
            )
            Text(
                text = displayText,
                modifier = Modifier.widthIn(
                    max = dimensionResource(id = R.dimen.home_favorite_bar_label_max_width),
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = dimensionResource(id = R.dimen.home_favorite_text_size).value.sp,
                lineHeight = dimensionResource(id = R.dimen.home_favorite_line_height).value.sp,
            )
        },
    )
}

@Composable
private fun BoxScope.FavoriteRibbonItemEditControls(
    ribbon: FavoriteContainer,
    identity: LaunchableIdentity,
    itemOriginInWindow: Offset,
    itemSize: IntSize,
    handleTargetWidthPx: Float,
    removeInteractionSource: MutableInteractionSource,
    hapticFeedback: HapticFeedback,
    actions: HomeFavoriteRibbonActions,
    dragActions: HomeFavoriteRibbonDragActions,
) {
    Box(
        modifier = Modifier
            .align(alignment = Alignment.TopStart)
            .size(size = dimensionResource(id = R.dimen.home_favorite_bar_remove_target_size))
            .clickable(
                interactionSource = removeInteractionSource,
                indication = null,
                role = Role.Button,
                onClick = {
                    actions.removeFavorite(
                        ribbonId = ribbon.id,
                        identity = identity,
                    )
                },
            )
            .testTag(tag = "remove_favorite_bar_item"),
        contentAlignment = Alignment.Center,
        content = {
            Box(
                modifier = Modifier
                    .size(
                        size = dimensionResource(
                            id = R.dimen.home_favorite_bar_remove_target_size,
                        ),
                    )
                    .clip(shape = CircleShape)
                    .background(color = MaterialTheme.colorScheme.error),
                contentAlignment = Alignment.Center,
                content = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = stringResource(id = R.string.remove_favorite_item),
                        modifier = Modifier.size(
                            size = dimensionResource(
                                id = R.dimen.home_favorite_bar_remove_icon_size,
                            ),
                        ),
                        tint = colorResource(id = R.color.home_favorite_remove_icon),
                    )
                },
            )
        },
    )
    Icon(
        painter = painterResource(id = R.drawable.ic_drag_handle),
        contentDescription = stringResource(id = R.string.favorite_reorder_handle),
        tint = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier
            .align(alignment = Alignment.CenterEnd)
            .width(width = dimensionResource(id = R.dimen.home_favorite_bar_drag_target_width))
            .fillMaxHeight()
            .pointerInput(
                key1 = identity,
                block = {
                    detectHomeReorderDrag(
                        onPressChanged = {},
                        onLongPress = {
                            hapticFeedback.performHapticFeedback(
                                hapticFeedbackType = HapticFeedbackType.LongPress,
                            )
                        },
                        onDragStart = { localTouch ->
                            dragActions.startApplicationDrag(
                                ribbon = ribbon,
                                identity = identity,
                                origin = itemOriginInWindow,
                                size = itemSize,
                                touch = Offset(
                                    x = itemOriginInWindow.x + itemSize.width -
                                            handleTargetWidthPx + localTouch.x,
                                    y = itemOriginInWindow.y + localTouch.y,
                                ),
                            )
                        },
                        onDragEnd = { dragActions.finishApplicationDrag() },
                        onDragCancel = { dragActions.cancelApplicationDrag() },
                        onDrag = { delta -> dragActions.dragApplication(delta = delta) },
                    )
                },
            )
            .padding(
                all = (dimensionResource(id = R.dimen.home_favorite_bar_drag_target_width) - dimensionResource(
                    id = R.dimen.home_reorder_handle_size
                )) / 2
            )
            .testTag(tag = "favorite_bar_reorder_handle"),
    )
}
