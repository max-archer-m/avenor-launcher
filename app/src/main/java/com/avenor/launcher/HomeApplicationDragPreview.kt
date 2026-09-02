package com.avenor.launcher

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlin.math.roundToInt

/**
 * State of an active favorite drag: the source geometry, the accumulated pointer delta, the touch
 * point that started the gesture, and the live visible order of both groups. In-group exchanges
 * change the visible order during the drag; a released session keeps that order visible until the
 * persistence callback completes.
 */
internal data class FavoriteDragSession(
    val generation: Int,
    val identity: LaunchableIdentity,
    val listSize: FavoriteListSize,
    val originInWindow: Offset,
    val size: IntSize,
    val touchStartInWindow: Offset,
    val displayedPrimary: List<LaunchableIdentity>,
    val displayedCompanion: List<LaunchableIdentity>,
    val delta: Offset = Offset.Zero,
    val hasInGroupExchange: Boolean = false,
    val lastInGroupExchangeTouchY: Float? = null,
    val released: Boolean = false,
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

internal data class FavoriteBarDragSession(
    val generation: Int,
    val barId: String,
    val identity: LaunchableIdentity,
    val displayedIdentities: List<LaunchableIdentity>,
    val originInWindow: Offset,
    val size: IntSize,
    val delta: Offset = Offset.Zero,
    val residualX: Float = 0f,
)

/**
 * Cross-group insertion boundary of an active drag. A boundary is pure feedback: it marks where a
 * release would insert the dragged favorite, and the lists are not changed before that release.
 */
internal data class FavoriteInsertionTarget(
    val intoCompanion: Boolean,
    val boundaryIndex: Int,
)

@Composable
internal fun HomeFavoriteDragPreview(
    session: FavoriteDragSession,
    availability: FavoriteAvailability,
    rootOriginInWindow: Offset,
) {
    val density = LocalDensity.current
    val previewAlpha = integerResource(R.integer.home_drag_preview_alpha_percent) / 100f
    val previewElevation = with(density) {
        dimensionResource(R.dimen.home_reorder_drag_elevation).toPx()
    }
    val topLeft = session.originInWindow +
        session.delta -
        rootOriginInWindow
    Box(
        modifier = Modifier
            .offset { IntOffset(topLeft.x.roundToInt(), topLeft.y.roundToInt()) }
            .size(
                width = with(density) { session.size.width.toDp() },
                height = with(density) { session.size.height.toDp() },
            )
            .alpha(previewAlpha)
            .testTag("home_favorite_drag_preview"),
    ) {
        HomeFavoritePreviewContent(
            availability = availability,
            listSize = session.listSize,
            maxWidth = with(density) { session.size.width.toDp() },
            shadowElevation = previewElevation,
        )
    }
}

@Composable
internal fun HomeFavoriteBarDragPreview(
    session: FavoriteBarDragSession,
    availability: FavoriteAvailability,
    rootOriginInWindow: Offset,
) {
    val density = LocalDensity.current
    val topLeft = session.originInWindow + session.delta - rootOriginInWindow
    val previewAlpha = integerResource(R.integer.home_drag_preview_alpha_percent) / 100f
    val previewElevation = with(density) {
        dimensionResource(R.dimen.home_reorder_drag_elevation).toPx()
    }
    Box(
        modifier = Modifier
            .offset { IntOffset(topLeft.x.roundToInt(), topLeft.y.roundToInt()) }
            .size(
                width = with(density) { session.size.width.toDp() },
                height = with(density) { session.size.height.toDp() },
            )
            .alpha(previewAlpha)
            .clearAndSetSemantics {}
            .testTag("home_favorite_bar_drag_preview"),
    ) {
        HomeFavoritePreviewContent(
            availability = availability,
            listSize = FavoriteListSize.Medium,
            maxWidth = dimensionResource(R.dimen.home_favorite_bar_item_width),
            shadowElevation = previewElevation,
        )
    }
}
