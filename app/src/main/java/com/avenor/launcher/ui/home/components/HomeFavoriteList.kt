package com.avenor.launcher.ui.home.components

import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChangeIgnoreConsumed
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize
import com.avenor.launcher.ApplicationEdgeScroll
import com.avenor.launcher.FavoriteAvailability
import com.avenor.launcher.FavoriteDragAnchor
import com.avenor.launcher.FavoriteListSize
import com.avenor.launcher.LaunchableEntry
import com.avenor.launcher.LaunchableIdentity
import com.avenor.launcher.R
import com.avenor.launcher.applicationDragKey
import com.avenor.launcher.ui.home.components.HomeFavoriteAddControl
import com.avenor.launcher.ui.home.components.awaitHomeHandleLongPress
import com.avenor.launcher.ui.home.components.homeEditSurface

@Composable
internal fun HomeFavoriteList(
    modifier: Modifier,
    identities: List<LaunchableIdentity>,
    availabilityByIdentity: Map<LaunchableIdentity, FavoriteAvailability>,
    listState: LazyListState,
    nestedScrollConnection: NestedScrollConnection?,
    editMode: Boolean,
    compact: Boolean,
    listSize: FavoriteListSize? = null,
    draggedIdentity: LaunchableIdentity?,
    exchangeTargetIdentity: LaunchableIdentity?,
    insertionBoundaryIndex: Int?,
    onBoundsInWindow: (Rect) -> Unit,
    onApplicationItemBounds: (LaunchableIdentity, Rect) -> Unit = { _, _ -> },
    applicationDropHighlight: Boolean = false,
    applicationDragKey: String? = null,
    applicationDragActive: Boolean = false,
    applicationEdgeScroll: ApplicationEdgeScroll? = null,
    onLaunchFavorite: (FavoriteAvailability) -> Unit,
    onLongPressFavorite: (LaunchableEntry) -> Unit,
    onRemoveFavorite: (LaunchableIdentity) -> Unit = {},
    listIndex: Int? = null,
    listCount: Int = 0,
    onChangeListSize: (FavoriteListSize) -> Unit = {},
    onRemoveList: () -> Unit = {},
    onAddFavorites: () -> Unit = {},
    onContainerBoundsInWindow: (Rect) -> Unit = {},
    onContainerDisposed: () -> Unit = {},
    sourceListPlaceholder: Boolean = false,
    listExchangeHighlight: Boolean = false,
    listDragActive: Boolean = false,
    onListDragStart: (Offset) -> Unit = {},
    onListDrag: (Offset) -> Unit = {},
    onListDragEnd: () -> Unit = {},
    onListDragCancel: () -> Unit = {},
    onDragStart: (LaunchableIdentity, Offset, IntSize, Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
    testTag: String? = null,
) {
    DisposableEffect(Unit) {
        onDispose(onContainerDisposed)
    }
    val controlBarHeight = dimensionResource(
        R.dimen.home_favorite_list_control_bar_height,
    )
    val sourceSlotAlpha = integerResource(R.integer.home_drag_source_slot_alpha_percent) / 100f
    val insertionLineColor = colorResource(R.color.home_favorite_insertion_line)
    val insertionLineThickness = with(LocalDensity.current) {
        dimensionResource(R.dimen.home_favorite_insertion_line_thickness).toPx()
    }
    val hapticFeedback = LocalHapticFeedback.current
    // Handle hot zones and row geometry of the currently composed rows. Only the drag gesture reads
    // them, so a plain map keeps the layout reporting out of composition.
    val dragAnchors = remember { mutableMapOf<String, FavoriteDragAnchor>() }
    var viewportOriginInWindow by remember { mutableStateOf(Offset.Zero) }
    val currentOnDragStart by rememberUpdatedState(onDragStart)
    val currentOnDrag by rememberUpdatedState(onDrag)
    val currentOnDragEnd by rememberUpdatedState(onDragEnd)
    val currentOnDragCancel by rememberUpdatedState(onDragCancel)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                onContainerBoundsInWindow(
                    Rect(
                        offset = coordinates.positionInWindow(),
                        size = coordinates.size.toSize(),
                    ),
                )
            }
            .homeEditSurface(enabled = editMode)
            .then(
                if (!listExchangeHighlight && !applicationDropHighlight) {
                    Modifier
                } else {
                    Modifier.border(
                        width = dimensionResource(R.dimen.home_favorite_exchange_border_width),
                        color = colorResource(R.color.home_favorite_exchange_border),
                        shape = RoundedCornerShape(
                            dimensionResource(R.dimen.home_favorite_exchange_border_radius),
                        ),
                    )
                },
            )
            .then(
                if (sourceListPlaceholder) Modifier.clearAndSetSemantics {} else Modifier,
            )
    ) {
        if (editMode && listIndex != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(controlBarHeight)
                    .alpha(if (sourceListPlaceholder) 0f else 1f)
                    .then(
                        if (sourceListPlaceholder) {
                            Modifier.clearAndSetSemantics {}
                        } else {
                            Modifier
                        },
                    ),
            ) {
                FavoriteListControlBar(
                    index = listIndex,
                    listCount = listCount,
                    selectedSize = listSize ?: FavoriteListSize.Medium,
                    onChangeSize = onChangeListSize,
                    onRemoveList = onRemoveList,
                    onListDragStart = onListDragStart,
                    onListDrag = onListDrag,
                    onListDragEnd = onListDragEnd,
                    onListDragCancel = onListDragCancel,
                )
            }
        }
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .alpha(if (sourceListPlaceholder) 0f else 1f)
                .then(
                    if (sourceListPlaceholder) {
                        Modifier.clearAndSetSemantics {}
                    } else {
                        Modifier
                    },
                )
                .then(
                    if (applicationDragActive) {
                        Modifier
                    } else {
                        nestedScrollConnection?.let { Modifier.nestedScroll(it) } ?: Modifier
                    },
                )
                .then(
                    // An empty group has no row to carry the line, so its only boundary
                    // is marked on the group itself.
                    if (identities.isNotEmpty() || insertionBoundaryIndex == null) {
                        Modifier
                    } else {
                        Modifier.drawWithContent {
                            drawContent()
                            drawRect(
                                color = insertionLineColor,
                                topLeft = Offset.Zero,
                                size = Size(size.width, insertionLineThickness),
                            )
                        }
                    },
                )
                .onGloballyPositioned { coordinates ->
                    val origin = coordinates.positionInWindow()
                    viewportOriginInWindow = origin
                    onBoundsInWindow(
                        Rect(offset = origin, size = coordinates.size.toSize()),
                    )
                }
                .drawWithContent {
                    drawContent()
                    if (applicationEdgeScroll?.containerKey == applicationDragKey) {
                        drawRect(
                            color = insertionLineColor.copy(
                                alpha = applicationEdgeScroll?.proximity ?: 0f,
                            ),
                            topLeft = if (applicationEdgeScroll?.forward ?: false) {
                                Offset(0f, size.height - insertionLineThickness)
                            } else {
                                Offset.Zero
                            },
                            size = Size(size.width, insertionLineThickness),
                        )
                    }
                }
                .then(
                    if (!editMode || listDragActive) {
                        Modifier
                    } else {
                        Modifier.pointerInput(Unit) {
                            detectFavoriteDrag(
                                anchorAt = { local ->
                                    val inWindow = viewportOriginInWindow + local
                                    dragAnchors.values.firstOrNull {
                                        it.handleHitZoneInWindow().contains(inWindow)
                                    }
                                },
                                onLongPress = {
                                    hapticFeedback.performHapticFeedback(
                                        HapticFeedbackType.LongPress,
                                    )
                                },
                                onDragStart = { anchor, local ->
                                    currentOnDragStart(
                                        anchor.identity,
                                        anchor.rowOriginInWindow,
                                        anchor.rowSize,
                                        viewportOriginInWindow + local,
                                    )
                                },
                                onDrag = { currentOnDrag(it) },
                                onDragEnd = { currentOnDragEnd() },
                                onDragCancel = { currentOnDragCancel() },
                            )
                        }
                    },
                )
                .testTag(testTag ?: if (compact) "home_companion_favorites" else "home_favorites"),
            state = listState,
            userScrollEnabled = !applicationDragActive,
        ) {
            itemsIndexed(
                items = identities,
                key = { _, identity -> identity.stableKey() },
            ) { index, identity ->
                val availability = availabilityByIdentity[identity]
                    ?: FavoriteAvailability.Unknown(null)
                val entry = availability.presentationEntry
                val dragging = identity == draggedIdentity
                // A pending cross-group insertion marks its boundary on the adjacent row edge; the last
                // row also carries the boundary that follows it.
                val lineAtTop = insertionBoundaryIndex == index
                val lineAtBottom = insertionBoundaryIndex == identities.size &&
                        index == identities.lastIndex
                val anchorKey = identity.stableKey()
                DisposableEffect(anchorKey) {
                    onDispose { dragAnchors.remove(anchorKey) }
                }
                HomeFavoriteRow(
                    modifier = Modifier
                        .drawWithContent {
                            drawContent()
                            if (lineAtTop) {
                                drawRect(
                                    color = insertionLineColor,
                                    topLeft = Offset.Zero,
                                    size = Size(size.width, insertionLineThickness),
                                )
                            }
                            if (lineAtBottom) {
                                drawRect(
                                    color = insertionLineColor,
                                    topLeft = Offset(0f, size.height - insertionLineThickness),
                                    size = Size(size.width, insertionLineThickness),
                                )
                            }
                        }
                        .then(
                            if (dragging) {
                                Modifier.alpha(sourceSlotAlpha)
                            } else {
                                // The drag preview already carries the movement, so an exchanged
                                // favorite
                                // is shown directly in its new slot; only appearance and removal keep a
                                // fade.
                                Modifier.animateItem(placementSpec = null)
                            },
                        ),
                    availability = availability,
                    onClick = { onLaunchFavorite(availability) },
                    onLongClick = {
                        if (entry != null) onLongPressFavorite(entry)
                    },
                    onRemoveFavorite = { onRemoveFavorite(identity) },
                    editMode = editMode,
                    compact = compact,
                    listSize = listSize,
                    exchangeHighlight = identity == exchangeTargetIdentity,
                    onRowBoundsInWindow = { origin, size ->
                        onApplicationItemBounds(
                            identity,
                            Rect(offset = origin, size = size.toSize()),
                        )
                        dragAnchors[anchorKey] =
                            (dragAnchors[anchorKey] ?: FavoriteDragAnchor(identity))
                                .copy(rowOriginInWindow = origin, rowSize = size)
                    },
                    onHandleBoundsInWindow = { bounds ->
                        dragAnchors[anchorKey] =
                            (dragAnchors[anchorKey] ?: FavoriteDragAnchor(identity))
                                .copy(handleBoundsInWindow = bounds)
                    },
                )
            }
            if (editMode && listIndex != null) {
                item(key = "favorite_add_$listIndex") {
                    HomeFavoriteAddControl(
                        onClick = onAddFavorites,
                        testTag = "favorite_add_$listIndex",
                    )
                }
            }
        }
    }
}

@Composable

internal fun HomeFavoriteProvisionalList(
    modifier: Modifier,
    onClick: () -> Unit,
    testTag: String,
    applicationDropHighlight: Boolean,
    onBoundsInWindow: (Rect) -> Unit,
    onDisposed: () -> Unit,
) {
    DisposableEffect(Unit) {
        onDispose(onDisposed)
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                onBoundsInWindow(
                    Rect(
                        offset = coordinates.positionInWindow(),
                        size = coordinates.size.toSize(),
                    ),
                )
            }
            .homeEditSurface(enabled = true)
            .then(
                if (applicationDropHighlight) {
                    Modifier.border(
                        width = dimensionResource(
                            R.dimen.home_favorite_exchange_border_width,
                        ),
                        color = colorResource(R.color.home_favorite_exchange_border),
                        shape = RoundedCornerShape(
                            dimensionResource(R.dimen.home_favorite_exchange_border_radius),
                        ),
                    )
                } else {
                    Modifier
                },
            ),
    ) {
        HomeFavoriteAddControl(
            onClick = onClick,
            testTag = testTag,
            labelRes = R.string.new_favorite_list,
        )
    }
}

/**
 * Geometry a composed favorite row contributes to its group's drag gesture:
 * the bounds of its drag handle in window coordinates, and the row's own
 * origin and size, which anchor the preview and give the handle's hot zone
 * its height.
 */

/**
 * The hot zone a drag handle accepts a press in: the handle's own horizontal
 * extent, but the full height of its row, so a taller row keeps a hot zone
 * as tall as the row it moves.
 */
internal fun FavoriteDragAnchor.handleHitZoneInWindow(): Rect {
    if (handleBoundsInWindow == Rect.Zero || rowSize.height == 0) return handleBoundsInWindow
    return Rect(
        left = handleBoundsInWindow.left,
        top = rowOriginInWindow.y,
        right = handleBoundsInWindow.right,
        bottom = rowOriginInWindow.y + rowSize.height,
    )
}

/**
 * Runs favorite drags for one group. The gesture belongs to the group
 * instead of the dragged row, because a cross-group exchange or an edge
 * scroll disposes that row and would cancel a row-owned gesture in the
 * middle of the drag. A press that misses every drag handle is left
 * untouched so the group keeps scrolling; once a drag owns the pointer its
 * movement is consumed in the initial pass, which keeps the group from
 * scrolling with it.
 */
internal suspend fun PointerInputScope.detectFavoriteDrag(
    anchorAt: (Offset) -> FavoriteDragAnchor?,
    onLongPress: () -> Unit,
    onDragStart: (FavoriteDragAnchor, Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
        val anchor = anchorAt(down.position) ?: return@awaitEachGesture
        val longPress = awaitHomeHandleLongPress(down = down) ?: return@awaitEachGesture
        var dragging = false
        var cancelled = false
        try {
            longPress.consume()
            onLongPress()
            while (true) {
                val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                if (event.changes.any { it.id != down.id && it.pressed }) {
                    cancelled = true
                    break
                }
                val change = event.changes.firstOrNull { it.id == down.id }
                if (change == null) {
                    cancelled = true
                    break
                }
                val movement = change.positionChangeIgnoreConsumed()
                if (change.changedToUpIgnoreConsumed()) {
                    if (dragging && movement != Offset.Zero) {
                        change.consume()
                        onDrag(movement)
                    }
                    break
                }
                change.consume()
                if (!dragging) {
                    if (movement == Offset.Zero) continue
                    dragging = true
                    onDragStart(anchor, longPress.position)
                }
                onDrag(movement)
            }
            if (dragging) {
                dragging = false
                if (cancelled) {
                    onDragCancel()
                } else {
                    onDragEnd()
                }
            }
        } finally {
            if (dragging) {
                dragging = false
                onDragCancel()
            }
        }
    }
}

internal suspend fun PointerInputScope.detectModuleReorderDrag(
    onLongPress: () -> Unit,
    onDragStart: (Offset) -> Boolean,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
) {
    awaitEachGesture {
        val down = awaitFirstDown(
            requireUnconsumed = false,
            pass = PointerEventPass.Initial,
        )
        val longPress = awaitHomeHandleLongPress(down = down) ?: return@awaitEachGesture
        var dragging = false
        var cancelled = false
        try {
            longPress.consume()
            if (!onDragStart(longPress.position)) return@awaitEachGesture
            dragging = true
            onLongPress()
            while (true) {
                val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                if (event.changes.any { it.id != down.id && it.pressed }) {
                    cancelled = true
                    break
                }
                val change = event.changes.firstOrNull { it.id == down.id }
                if (change == null) {
                    cancelled = true
                    break
                }
                val movement = change.positionChangeIgnoreConsumed()
                if (change.changedToUpIgnoreConsumed()) {
                    if (movement != Offset.Zero) onDrag(movement)
                    change.consume()
                    break
                }
                change.consume()
                if (movement != Offset.Zero) onDrag(movement)
            }
            dragging = false
            if (cancelled) onDragCancel() else onDragEnd()
        } finally {
            if (dragging) onDragCancel()
        }
    }
}
