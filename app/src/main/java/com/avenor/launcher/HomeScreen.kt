package com.avenor.launcher

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChangeIgnoreConsumed
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.graphics.drawable.toBitmap
import java.time.ZonedDateTime
import kotlin.math.roundToInt
import kotlinx.coroutines.delay

@Composable
internal fun HomeScreen(
    clock: () -> ZonedDateTime = { ZonedDateTime.now() },
    favoriteState: FavoriteReadState = FavoriteReadState.Readable(emptyList()),
    favoriteAvailability: Map<LaunchableIdentity, FavoriteAvailability> = emptyMap(),
    favoriteListState: LazyListState = rememberLazyListState(),
    favoriteNestedScrollConnection: NestedScrollConnection? = null,
    companionFavoriteListState: LazyListState = rememberLazyListState(),
    companionFavoriteNestedScrollConnection: NestedScrollConnection? = null,
    editMode: Boolean = false,
    onRetryFavorites: () -> Unit = {},
    onLaunchFavorite: (FavoriteAvailability) -> Unit = {},
    onLongPressFavorite: (LaunchableEntry) -> Unit = {},
    onCommitFavoriteComposition: (
        primaryIdentities: List<LaunchableIdentity>,
        companionIdentities: List<LaunchableIdentity>,
    ) -> Unit = { _, _ -> },
    accessibilityLockController: AccessibilityLockController = EmptyAccessibilityLockController,
) {
    val context = LocalContext.current
    var now by remember { mutableStateOf(clock()) }
    var dragSession by remember { mutableStateOf<FavoriteDragSession?>(null) }
    var dragRootOriginInWindow by remember { mutableStateOf(Offset.Zero) }
    var primaryListBoundsInWindow by remember { mutableStateOf(Rect.Zero) }
    var companionListBoundsInWindow by remember { mutableStateOf(Rect.Zero) }
    // Band at a row's top and bottom edge that a cross-group drag reads as an insertion boundary
    // instead of the favorite's body, because adjacent rows leave no gap between them.
    val insertionBoundaryBandPx = with(LocalDensity.current) {
        dimensionResource(R.dimen.home_favorite_insertion_boundary_band).toPx()
    }
    // Band at a group's leading and trailing edge where an active drag scrolls that group, so
    // favorites outside the viewport stay reachable without releasing the drag.
    val edgeScrollBandPx = with(LocalDensity.current) {
        dimensionResource(R.dimen.home_favorite_edge_scroll_band).toPx()
    }
    val edgeScrollSpeedPxPerSecond = with(LocalDensity.current) {
        integerResource(R.integer.home_favorite_edge_scroll_dp_per_second).dp.toPx()
    }
    val hapticFeedback = LocalHapticFeedback.current
    val advanceDrag: (Offset) -> Unit = { amount ->
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
        if (advanced != null && previous != null && advanced.feedbackChangedFrom(previous)) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
        }
    }
    val edgeScroll = dragSession?.edgeScroll(
        primaryBoundsInWindow = primaryListBoundsInWindow,
        companionBoundsInWindow = companionListBoundsInWindow,
        bandPx = edgeScrollBandPx,
    )

    LaunchedEffect(clock) {
        while (true) {
            val remainingMillis = 60_000L - (System.currentTimeMillis() % 60_000L)
            delay(remainingMillis)
            now = clock()
        }
    }

    LaunchedEffect(editMode) {
        if (!editMode) dragSession = null
    }

    // Edge scrolling moves the group under the finger while the touch point itself stays put, so
    // the target is resolved again for the group's new geometry after every scrolled frame.
    LaunchedEffect(edgeScroll) {
        val request = edgeScroll ?: return@LaunchedEffect
        val listState = if (request.inCompanion) {
            companionFavoriteListState
        } else {
            favoriteListState
        }
        var previousFrameNanos = withFrameNanos { it }
        while (true) {
            val frameNanos = withFrameNanos { it }
            val elapsedSeconds = (frameNanos - previousFrameNanos) / NANOS_PER_SECOND
            previousFrameNanos = frameNanos
            val canScroll = if (request.forward) {
                listState.canScrollForward
            } else {
                listState.canScrollBackward
            }
            if (!canScroll) continue
            val distance = edgeScrollSpeedPxPerSecond * elapsedSeconds
            listState.scrollBy(if (request.forward) distance else -distance)
            advanceDrag(Offset.Zero)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(
                horizontal = dimensionResource(R.dimen.home_horizontal_padding),
                vertical = dimensionResource(R.dimen.home_vertical_padding),
            )
            .onGloballyPositioned { dragRootOriginInWindow = it.positionInWindow() },
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .editSurface(editMode),
            ) {
                Column {
                    Text(
                        text = HomeDateTimeFormatter.time(context, now),
                        modifier = Modifier
                            .heightIn(min = dimensionResource(R.dimen.home_time_min_height))
                            .testTag("home_time")
                            .clickable(enabled = !editMode, role = Role.Button) {
                                context.launchClockDestination()
                            },
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = dimensionResource(R.dimen.home_time_text_size).value.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = dimensionResource(R.dimen.home_time_line_height).value.sp,
                        textAlign = TextAlign.Start,
                    )
                    Text(
                        text = HomeDateTimeFormatter.dateAndWeekday(context, now),
                        modifier = Modifier
                            .heightIn(min = dimensionResource(R.dimen.home_date_height))
                            .testTag("home_date")
                            .clickable(enabled = !editMode, role = Role.Button) {
                                val calendarUri = CalendarContract.CONTENT_URI
                                    .buildUpon()
                                    .appendPath("time")
                                    .appendPath(now.toInstant().toEpochMilli().toString())
                                    .build()
                                context.launchPlatformDestination(
                                    intent = Intent(Intent.ACTION_VIEW, calendarUri),
                                    failureMessage = R.string.calendar_unavailable,
                                )
                            }
                            // The inset only shifts the visible text; the clickable stays outside
                            // it so the whole date row remains the focusable touch target.
                            .padding(
                                start = dimensionResource(R.dimen.home_date_text_start_inset),
                            )
                            .wrapContentHeight(align = Alignment.CenterVertically),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = dimensionResource(R.dimen.home_date_text_size).value.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = dimensionResource(R.dimen.home_date_line_height).value.sp,
                        textAlign = TextAlign.Start,
                    )
                }
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .then(
                            if (accessibilityLockController.availableForValidation && !editMode) {
                                Modifier.pointerInput(accessibilityLockController) {
                                    detectTapGestures(
                                        onDoubleTap = {
                                            if (!accessibilityLockController.isSystemEnabled()) {
                                                return@detectTapGestures
                                            }
                                            if (accessibilityLockController.requestLock() !=
                                                LockRequestResult.Requested
                                            ) {
                                                Toast.makeText(
                                                    context,
                                                    R.string.unable_to_lock_screen,
                                                    Toast.LENGTH_SHORT,
                                                ).show()
                                            }
                                        },
                                    )
                                }
                            } else {
                                Modifier
                            },
                        )
                        .testTag("home_double_tap_lock_region"),
                )
            }
            Spacer(Modifier.height(dimensionResource(R.dimen.home_module_spacing)))
            when (favoriteState) {
                FavoriteReadState.Loading -> HomeFavoriteMessage(
                    message = stringResource(R.string.loading_favorites),
                    showProgress = true,
                    onRetry = null,
                )

                FavoriteReadState.ReadFailure -> HomeFavoriteMessage(
                    message = stringResource(R.string.unable_to_load_favorites),
                    showProgress = false,
                    onRetry = onRetryFavorites,
                )

                is FavoriteReadState.Readable -> {
                    if (favoriteState.identities.isEmpty()) {
                        Text(
                            text = stringResource(R.string.home_empty_favorites),
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.testTag("home_favorites_empty"),
                        )
                    } else {
                        val activeSession = dragSession?.takeIf { it.hasInGroupExchange }
                        val primaryDisplayed = activeSession?.displayedPrimary
                            ?: favoriteState.primaryIdentities
                        val companionDisplayed = activeSession?.displayedCompanion
                            ?: favoriteState.companionIdentities
                        // A release completes the current exchange or insertion when the touch
                        // point is inside either group; any other area restores the saved state.
                        val endDrag: () -> Unit = {
                            val session = dragSession
                            dragSession = null
                            if (session != null && session.hasPendingChange &&
                                (
                                    primaryListBoundsInWindow.contains(session.touchInWindow) ||
                                        companionListBoundsInWindow
                                            .contains(session.touchInWindow)
                                    )
                            ) {
                                val committed = session.committedComposition()
                                onCommitFavoriteComposition(committed.first, committed.second)
                            }
                        }
                        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                            val primaryRowHeight = dimensionResource(
                                R.dimen.home_favorite_row_min_height,
                            )
                            val companionRowHeight = dimensionResource(
                                R.dimen.home_companion_favorite_row_min_height,
                            )
                            val contentHeight = maxOf(
                                primaryRowHeight * favoriteState.primaryIdentities.size,
                                companionRowHeight * favoriteState.companionIdentities.size,
                            ).coerceAtMost(maxHeight)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(contentHeight),
                                horizontalArrangement = Arrangement.spacedBy(
                                    dimensionResource(R.dimen.home_favorite_group_spacing),
                                ),
                            ) {
                                HomeFavoriteList(
                                    modifier = Modifier
                                        .weight(55f)
                                        .fillMaxHeight(),
                                    identities = primaryDisplayed,
                                    availabilityByIdentity = favoriteAvailability,
                                    listState = favoriteListState,
                                    nestedScrollConnection = favoriteNestedScrollConnection,
                                    editMode = editMode,
                                    compact = false,
                                    draggedIdentity = dragSession?.identity,
                                    exchangeTargetIdentity = dragSession?.crossGroupTarget,
                                    insertionBoundaryIndex = dragSession?.insertionBoundaryIn(
                                        companion = false,
                                    ),
                                    onBoundsInWindow = { primaryListBoundsInWindow = it },
                                    onLaunchFavorite = onLaunchFavorite,
                                    onLongPressFavorite = onLongPressFavorite,
                                    onDragStart = { identity, origin, size, touch ->
                                        dragSession = FavoriteDragSession(
                                            identity = identity,
                                            fromCompanion = false,
                                            originInWindow = origin,
                                            size = size,
                                            touchStartInWindow = touch,
                                            displayedPrimary = favoriteState.primaryIdentities,
                                            displayedCompanion = favoriteState.companionIdentities,
                                        )
                                    },
                                    onDrag = advanceDrag,
                                    onDragEnd = endDrag,
                                    onDragCancel = { dragSession = null },
                                )
                                HomeFavoriteList(
                                    modifier = Modifier
                                        .weight(45f)
                                        .fillMaxHeight(),
                                    identities = companionDisplayed,
                                    availabilityByIdentity = favoriteAvailability,
                                    listState = companionFavoriteListState,
                                    nestedScrollConnection =
                                        companionFavoriteNestedScrollConnection,
                                    editMode = editMode,
                                    compact = true,
                                    draggedIdentity = dragSession?.identity,
                                    exchangeTargetIdentity = dragSession?.crossGroupTarget,
                                    insertionBoundaryIndex = dragSession?.insertionBoundaryIn(
                                        companion = true,
                                    ),
                                    onBoundsInWindow = { companionListBoundsInWindow = it },
                                    onLaunchFavorite = onLaunchFavorite,
                                    onLongPressFavorite = onLongPressFavorite,
                                    onDragStart = { identity, origin, size, touch ->
                                        dragSession = FavoriteDragSession(
                                            identity = identity,
                                            fromCompanion = true,
                                            originInWindow = origin,
                                            size = size,
                                            touchStartInWindow = touch,
                                            displayedPrimary = favoriteState.primaryIdentities,
                                            displayedCompanion = favoriteState.companionIdentities,
                                        )
                                    },
                                    onDrag = advanceDrag,
                                    onDragEnd = endDrag,
                                    onDragCancel = { dragSession = null },
                                )
                            }
                        }
                    }
                }
            }
        }
        dragSession?.let { session ->
            HomeFavoriteDragPreview(
                session = session,
                availability = favoriteAvailability[session.identity]
                    ?: FavoriteAvailability.Unknown(null),
                rootOriginInWindow = dragRootOriginInWindow,
            )
        }
    }
}

/**
 * State of an active favorite drag: the source geometry, the accumulated pointer delta, the touch
 * point that started the gesture, and the live visible order of both groups. Only an in-group
 * exchange changes a visible order during the drag; a cross-group target and an insertion boundary
 * stay pure feedback until the release, so merely moving across the other group changes nothing.
 */
private data class FavoriteDragSession(
    val identity: LaunchableIdentity,
    val fromCompanion: Boolean,
    val originInWindow: Offset,
    val size: IntSize,
    val touchStartInWindow: Offset,
    val displayedPrimary: List<LaunchableIdentity>,
    val displayedCompanion: List<LaunchableIdentity>,
    val delta: Offset = Offset.Zero,
    val hasInGroupExchange: Boolean = false,
    val crossGroupTarget: LaunchableIdentity? = null,
    val insertion: FavoriteInsertionTarget? = null,
) {
    /** Live position of the finger, derived from the start point so a row swap cannot shift it. */
    val touchInWindow: Offset get() = touchStartInWindow + delta

    /** Group that shows the dragged favorite, which the drag keeps unchanged until the release. */
    val inCompanion: Boolean get() = identity in displayedCompanion

    /** Whether a cross-group insertion boundary is currently marked. */
    val hasInsertion: Boolean get() = insertion != null

    /** Whether a release would produce a composition that differs from the saved one. */
    val hasPendingChange: Boolean
        get() = hasInGroupExchange || crossGroupTarget != null || hasInsertion

    /** Boundary the requested group should mark with an insertion line, if any. */
    fun insertionBoundaryIn(companion: Boolean): Int? =
        insertion?.takeIf { it.intoCompanion == companion }?.boundaryIndex

    /** Returns this session with cross-group feedback dropped and visible orders untouched. */
    fun withoutCrossGroupFeedback(): FavoriteDragSession =
        if (crossGroupTarget == null && insertion == null) {
            this
        } else {
            copy(crossGroupTarget = null, insertion = null)
        }
}

/**
 * Cross-group insertion boundary of an active drag. A boundary is pure feedback: it marks where a
 * release would insert the dragged favorite, and the lists are not changed before that release.
 */
private data class FavoriteInsertionTarget(
    val intoCompanion: Boolean,
    val boundaryIndex: Int,
)

/**
 * Composition a release commits, starting from the currently visible orders. A pending cross-group
 * target exchanges position and group membership with the dragged favorite, so both group counts
 * stay unchanged. A pending insertion instead removes the dragged favorite from its group and
 * inserts it at the marked boundary of the other group, which keeps the relative order of that
 * group's existing favorites.
 */
private fun FavoriteDragSession.committedComposition():
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
 * feedback that a release performs, so moving the finger across the other group never changes the
 * lists on its own. Leaving a valid target drops that feedback again. The slot is compared with the
 * source's current visible index, so the dragged favorite, the source slot, and invalid areas
 * produce no in-group exchange.
 */
private fun FavoriteDragSession.advanced(
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
        return moved
            .withDisplayedOrder(sourceInCompanion, sourceOrder.exchangedAt(sourceSlot, slot))
            .copy(hasInGroupExchange = true, crossGroupTarget = null, insertion = null)
    }
    val targetOrder = moved.displayedOrder(targetInCompanion)
    return when (target) {
        is FavoriteDragTarget.Boundary -> moved.copy(
            crossGroupTarget = null,
            insertion = FavoriteInsertionTarget(
                intoCompanion = targetInCompanion,
                boundaryIndex = target.index.coerceIn(0, targetOrder.size),
            ),
        )

        is FavoriteDragTarget.Body -> {
            if (target.slot !in targetOrder.indices) return moved.withoutCrossGroupFeedback()
            moved.copy(crossGroupTarget = targetOrder[target.slot], insertion = null)
        }
    }
}

/** Nanoseconds in a second, which turns a frame interval into an edge-scroll distance. */
private const val NANOS_PER_SECOND = 1_000_000_000f

/**
 * Whether the target feedback differs from [previous]: the visible order of a group changed, or the
 * marked cross-group exchange target or insertion boundary moved. Pointer movement on its own
 * leaves
 * the feedback untouched, so this marks exactly the moments at which a release would produce a
 * different composition.
 */
private fun FavoriteDragSession.feedbackChangedFrom(previous: FavoriteDragSession): Boolean =
    displayedPrimary != previous.displayedPrimary ||
        displayedCompanion != previous.displayedCompanion ||
        crossGroupTarget != previous.crossGroupTarget ||
        insertion != previous.insertion

/**
 * Edge scroll the active drag currently asks for. While the touch point rests in a group's leading
 * or trailing band, that group scrolls, so favorites outside the viewport stay reachable without
 * releasing the drag. The band is capped at half the group so its two edges cannot overlap. Returns
 * null when the touch point is outside both groups or away from the group's edges.
 */
private fun FavoriteDragSession.edgeScroll(
    primaryBoundsInWindow: Rect,
    companionBoundsInWindow: Rect,
    bandPx: Float,
): FavoriteEdgeScroll? {
    val inCompanionGroup = when {
        primaryBoundsInWindow.contains(touchInWindow) -> false
        companionBoundsInWindow.contains(touchInWindow) -> true
        else -> return null
    }
    val bounds = if (inCompanionGroup) companionBoundsInWindow else primaryBoundsInWindow
    val band = bandPx.coerceAtMost(bounds.height / 2f)
    return when {
        touchInWindow.y < bounds.top + band ->
            FavoriteEdgeScroll(inCompanionGroup, forward = false)

        touchInWindow.y >= bounds.bottom - band ->
            FavoriteEdgeScroll(inCompanionGroup, forward = true)

        else -> null
    }
}

/**
 * Group an active drag scrolls and the direction it scrolls. Equal requests keep the running scroll
 * alive, so a drag that stays in the same band scrolls continuously instead of restarting.
 */
private data class FavoriteEdgeScroll(
    val inCompanion: Boolean,
    val forward: Boolean,
)

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

/** Returns this list with the entries at the two given positions swapped. */
private fun List<LaunchableIdentity>.exchangedAt(
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

@Composable
private fun HomeFavoriteDragPreview(
    session: FavoriteDragSession,
    availability: FavoriteAvailability,
    rootOriginInWindow: Offset,
) {
    val density = LocalDensity.current
    val previewAlpha = integerResource(R.integer.home_drag_preview_alpha_percent) / 100f
    val previewElevation = with(density) {
        dimensionResource(R.dimen.home_reorder_drag_elevation).toPx()
    }
    val topLeft = session.originInWindow + session.delta - rootOriginInWindow
    HomeFavoriteRow(
        modifier = Modifier
            .offset { IntOffset(topLeft.x.roundToInt(), topLeft.y.roundToInt()) }
            .size(
                width = with(density) { session.size.width.toDp() },
                height = with(density) { session.size.height.toDp() },
            )
            .alpha(previewAlpha)
            .graphicsLayer { shadowElevation = previewElevation }
            .testTag("home_favorite_drag_preview"),
        availability = availability,
        onClick = {},
        onLongClick = {},
        editMode = true,
        compact = session.fromCompanion,
        exchangeHighlight = false,
        onRowBoundsInWindow = { _, _ -> },
        onHandleBoundsInWindow = {},
    )
}

@Composable
private fun HomeFavoriteList(
    modifier: Modifier,
    identities: List<LaunchableIdentity>,
    availabilityByIdentity: Map<LaunchableIdentity, FavoriteAvailability>,
    listState: LazyListState,
    nestedScrollConnection: NestedScrollConnection?,
    editMode: Boolean,
    compact: Boolean,
    draggedIdentity: LaunchableIdentity?,
    exchangeTargetIdentity: LaunchableIdentity?,
    insertionBoundaryIndex: Int?,
    onBoundsInWindow: (Rect) -> Unit,
    onLaunchFavorite: (FavoriteAvailability) -> Unit,
    onLongPressFavorite: (LaunchableEntry) -> Unit,
    onDragStart: (LaunchableIdentity, Offset, IntSize, Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
) {
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
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .then(
                nestedScrollConnection?.let { Modifier.nestedScroll(it) } ?: Modifier,
            )
            .then(
                // An empty group has no row to carry the line, so its only boundary is marked on
                // the group itself.
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
            .editSurface(editMode)
            .onGloballyPositioned { coordinates ->
                val origin = coordinates.positionInWindow()
                viewportOriginInWindow = origin
                onBoundsInWindow(
                    Rect(offset = origin, size = coordinates.size.toSize()),
                )
            }
            .then(
                if (!editMode) {
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
                            onDragStart = { anchor, local ->
                                hapticFeedback.performHapticFeedback(
                                    HapticFeedbackType.LongPress,
                                )
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
            .testTag(if (compact) "home_companion_favorites" else "home_favorites"),
        state = listState,
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
                editMode = editMode,
                compact = compact,
                exchangeHighlight = identity == exchangeTargetIdentity,
                onRowBoundsInWindow = { origin, size ->
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
    }
}

/**
 * Geometry a composed favorite row contributes to its group's drag gesture: the bounds of its drag
 * handle in window coordinates, and the row's own origin and size, which anchor the preview and
 * give the handle's hot zone its height.
 */
private data class FavoriteDragAnchor(
    val identity: LaunchableIdentity,
    val handleBoundsInWindow: Rect = Rect.Zero,
    val rowOriginInWindow: Offset = Offset.Zero,
    val rowSize: IntSize = IntSize.Zero,
)

/**
 * The hot zone a drag handle accepts a press in: the handle's own horizontal extent, but the full
 * height of its row, so a taller row keeps a hot zone as tall as the row it moves.
 */
private fun FavoriteDragAnchor.handleHitZoneInWindow(): Rect {
    if (handleBoundsInWindow == Rect.Zero || rowSize.height == 0) return handleBoundsInWindow
    return Rect(
        left = handleBoundsInWindow.left,
        top = rowOriginInWindow.y,
        right = handleBoundsInWindow.right,
        bottom = rowOriginInWindow.y + rowSize.height,
    )
}

/**
 * Runs favorite drags for one group. The gesture belongs to the group instead of the dragged row,
 * because a cross-group exchange or an edge scroll disposes that row and would cancel a row-owned
 * gesture in the middle of the drag. A press that misses every drag handle is left untouched so the
 * group keeps scrolling; once a drag owns the pointer its movement is consumed in the initial pass,
 * which keeps the group from scrolling with it.
 */
private suspend fun PointerInputScope.detectFavoriteDrag(
    anchorAt: (Offset) -> FavoriteDragAnchor?,
    onDragStart: (FavoriteDragAnchor, Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
        val anchor = anchorAt(down.position) ?: return@awaitEachGesture
        var dragging = false
        var beforeSlop = Offset.Zero
        var cancelled = false
        try {
            while (true) {
                val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                if (event.changes.any { it.id != down.id }) {
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
                if (dragging) {
                    change.consume()
                    onDrag(movement)
                    continue
                }
                beforeSlop += movement
                if (beforeSlop.getDistance() < viewConfiguration.touchSlop) continue
                change.consume()
                dragging = true
                onDragStart(anchor, change.position)
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

@Composable
private fun HomeFavoriteMessage(
    message: String,
    showProgress: Boolean,
    onRetry: (() -> Unit)?,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (showProgress) CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
        if (!showProgress) {
            Icon(
                painter = painterResource(R.drawable.ic_inventory_error),
                contentDescription = null,
                modifier = Modifier.size(dimensionResource(R.dimen.home_favorite_error_icon_size)),
            )
        }
        Text(text = message, color = MaterialTheme.colorScheme.onBackground)
        onRetry?.let { retry ->
            TextButton(onClick = retry) { Text(stringResource(R.string.retry)) }
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun HomeFavoriteRow(
    modifier: Modifier,
    availability: FavoriteAvailability,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    editMode: Boolean,
    compact: Boolean,
    exchangeHighlight: Boolean,
    onRowBoundsInWindow: (Offset, IntSize) -> Unit,
    onHandleBoundsInWindow: (Rect) -> Unit,
) {
    val entry = availability.presentationEntry
    val iconSize = dimensionResource(
        if (compact) R.dimen.home_companion_favorite_icon_size
        else R.dimen.home_favorite_icon_size,
    )
    val disabledAlpha = integerResource(R.integer.disabled_content_alpha_percent) / 100f
    val iconPixels = with(LocalDensity.current) { iconSize.roundToPx() }
    val interactionSource = remember(entry?.identity) { MutableInteractionSource() }
    val hapticFeedback = LocalHapticFeedback.current
    val handleTargetSize = dimensionResource(R.dimen.home_reorder_handle_target_size)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(
                min = dimensionResource(
                    if (compact) R.dimen.home_companion_favorite_row_min_height
                    else R.dimen.home_favorite_row_min_height,
                ),
            )
            .then(
                // A cross-group exchange marks its target favorite with a border; a group gap keeps
                // no border because in-group gaps never accept a placement.
                if (!exchangeHighlight) {
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
            .combinedClickable(
                enabled = !editMode,
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                role = Role.Button,
                onClick = onClick,
                onLongClick = {
                    if (entry != null) {
                        hapticFeedback.performHapticFeedback(
                            androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress,
                        )
                        onLongClick()
                    }
                },
            )
            .alpha(if (availability is FavoriteAvailability.Available) 1f else disabledAlpha)
            .onGloballyPositioned {
                onRowBoundsInWindow(it.positionInWindow(), it.size)
            }
            .testTag("home_favorite_row"),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (entry == null) {
            Icon(
                painter = painterResource(R.drawable.ic_inventory_error),
                contentDescription = null,
                modifier = Modifier.size(iconSize),
                tint = MaterialTheme.colorScheme.onBackground,
            )
        } else {
            val bitmap = entry.iconBitmap?.asImageBitmap() ?: remember(entry.icon, iconPixels) {
                entry.icon.toBitmap(iconPixels, iconPixels).asImageBitmap()
            }
            Image(bitmap = bitmap, contentDescription = null, modifier = Modifier.size(iconSize))
        }
        Spacer(Modifier.width(dimensionResource(R.dimen.home_favorite_icon_label_gap)))
        val displayText = when (availability) {
            is FavoriteAvailability.Available -> availability.entry.label
            is FavoriteAvailability.Disabled -> entry?.let {
                stringResource(R.string.favorite_disabled_format, it.label)
            } ?: stringResource(R.string.favorite_application_disabled)
            is FavoriteAvailability.TemporarilyUnavailable,
            is FavoriteAvailability.Unknown,
            -> entry?.let {
                stringResource(R.string.favorite_unavailable_format, it.label)
            } ?: stringResource(R.string.favorite_application_unavailable)
            FavoriteAvailability.ConfirmedRemoved ->
                stringResource(R.string.favorite_application_unavailable)
        }
        Text(
            text = displayText,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = dimensionResource(
                if (compact) R.dimen.home_companion_favorite_text_size
                else R.dimen.home_favorite_text_size,
            ).value.sp,
            lineHeight = dimensionResource(
                if (compact) R.dimen.home_companion_favorite_line_height
                else R.dimen.home_favorite_line_height,
            ).value.sp,
        )
        if (editMode) {
            Icon(
                painter = painterResource(R.drawable.ic_drag_handle),
                contentDescription = stringResource(R.string.favorite_reorder_handle),
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .size(handleTargetSize)
                    // The geometry is reported outside the padding so the hot zone covers the
                    // whole target size instead of only the glyph the padding leaves behind.
                    .onGloballyPositioned {
                        onHandleBoundsInWindow(
                            Rect(offset = it.positionInWindow(), size = it.size.toSize()),
                        )
                    }
                    .padding(
                        (
                            handleTargetSize -
                                dimensionResource(R.dimen.home_reorder_handle_size)
                        ) / 2,
                    )
                    .testTag("favorite_reorder_handle"),
            )
        }
    }
}

@Composable
private fun Modifier.editSurface(enabled: Boolean): Modifier = if (!enabled) this else {
    clip(RoundedCornerShape(dimensionResource(R.dimen.home_edit_surface_radius)))
        .background(colorResource(R.color.home_edit_surface))
}

private fun LaunchableIdentity.stableKey(): String =
    "$profileSerialNumber:${componentName.flattenToString()}"

private fun Context.launchClockDestination() {
    val alarmIntent = Intent(AlarmClock.ACTION_SHOW_ALARMS)
    val clockMainIntent = try {
        packageManager
            .resolveDefaultActivity(alarmIntent)
            ?.activityInfo
            ?.packageName
            ?.let { packageManager.getLaunchIntentForPackage(it) }
    } catch (_: SecurityException) {
        null
    }

    launchPlatformDestination(
        intent = clockMainIntent ?: alarmIntent,
        failureMessage = R.string.clock_unavailable,
    )
}

private fun PackageManager.resolveDefaultActivity(intent: Intent) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        resolveActivity(
            intent,
            PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong()),
        )
    } else {
        @Suppress("DEPRECATION")
        resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
    }

private fun Context.launchPlatformDestination(
    intent: Intent,
    @StringRes failureMessage: Int,
) {
    try {
        startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(this, failureMessage, Toast.LENGTH_SHORT).show()
    } catch (_: SecurityException) {
        Toast.makeText(this, failureMessage, Toast.LENGTH_SHORT).show()
    }
}
