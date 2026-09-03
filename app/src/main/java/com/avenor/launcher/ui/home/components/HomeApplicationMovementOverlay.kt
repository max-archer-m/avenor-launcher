package com.avenor.launcher.ui.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.IntOffset
import com.avenor.launcher.FavoriteNamePlacement
import com.avenor.launcher.HomeApplicationMovement
import com.avenor.launcher.HomeFavoriteBelowItem
import com.avenor.launcher.HomeFavoriteRow
import com.avenor.launcher.OrderedFavoriteModuleType
import com.avenor.launcher.R
import kotlin.math.roundToInt

@Composable
internal fun HomeApplicationMovementOverlay(
    movement: HomeApplicationMovement,
    rootOrigin: Offset,
) {
    val session = movement.session ?: return
    val density = LocalDensity.current
    val width = with(receiver = density, block = { session.sourceBounds.width.toDp() })
    val height = with(receiver = density, block = { session.sourceBounds.height.toDp() })
    val ribbon = session.module.type == OrderedFavoriteModuleType.Ribbon
    val shape = if (ribbon) {
        RoundedCornerShape(size = dimensionResource(id = R.dimen.home_favorite_bar_corner_radius))
    } else {
        RectangleShape
    }
    val lineColor = MaterialTheme.colorScheme.onBackground
    val lineWidth = dimensionResource(id = R.dimen.home_favorite_insertion_line_thickness)
    val edgeColor = lineColor.copy(alpha = integerResource(id = R.integer.home_favorite_edge_feedback_alpha_percent) / 100f)
    movement.edgeFeedback?.let(
        block = { edge ->
            Canvas(
                modifier = Modifier.fillMaxSize().testTag(tag = "home_application_edge_feedback"),
                onDraw = {
                    val bounds = edge.bounds.translate(offset = -rootOrigin)
                    val startSide = edge.owner.direction < 0
                    val colors = if (startSide) listOf(edgeColor, edgeColor.copy(alpha = 0f)) else listOf(edgeColor.copy(alpha = 0f), edgeColor)
                    if (edge.horizontal) {
                        val left = if (startSide) bounds.left else bounds.right - edge.band
                        drawRect(
                            brush = Brush.horizontalGradient(colors = colors, startX = left, endX = left + edge.band),
                            topLeft = Offset(x = left, y = bounds.top),
                            size = Size(width = edge.band, height = bounds.height),
                        )
                    } else {
                        val top = if (startSide) bounds.top else bounds.bottom - edge.band
                        drawRect(
                            brush = Brush.verticalGradient(colors = colors, startY = top, endY = top + edge.band),
                            topLeft = Offset(x = bounds.left, y = top),
                            size = Size(width = bounds.width, height = edge.band),
                        )
                    }
                },
            )
        },
    )
    session.creation?.let(
        block = { (_, targetBounds) ->
            val outlineWidth = dimensionResource(id = R.dimen.home_module_selection_stroke)
            val outlineRadius = dimensionResource(id = R.dimen.home_module_selection_radius)
            Canvas(
                modifier = Modifier.fillMaxSize().testTag(tag = "home_module_creation_drop_outline"),
                onDraw = {
                    val bounds = targetBounds.translate(offset = -rootOrigin)
                    val viewport = movement.viewport.translate(offset = -rootOrigin)
                    val stroke = outlineWidth.toPx()
                    val radius = (outlineRadius.toPx() - stroke / 2f).coerceAtLeast(minimumValue = 0f)
                    clipRect(
                        left = viewport.left, top = viewport.top, right = viewport.right, bottom = viewport.bottom,
                        block = {
                            drawRoundRect(
                                color = lineColor,
                                topLeft = bounds.topLeft + Offset(x = stroke / 2f, y = stroke / 2f),
                                size = Size(width = bounds.width - stroke, height = bounds.height - stroke),
                                cornerRadius = CornerRadius(x = radius, y = radius),
                                style = Stroke(width = stroke),
                            )
                        },
                    )
                },
            )
        },
    )
    session.insertion?.let(
        block = { insertion ->
            Canvas(
                modifier = Modifier.fillMaxSize().testTag(tag = "home_application_insertion_line"),
                onDraw = {
                    drawLine(
                        color = lineColor,
                        start = insertion.lineStart - rootOrigin,
                        end = insertion.lineEnd - rootOrigin,
                        strokeWidth = lineWidth.toPx(),
                    )
                },
            )
        },
    )
    Box(
        modifier = Modifier
            .offset(
                offset = {
                    val origin = session.previewOrigin - rootOrigin
                    IntOffset(x = origin.x.roundToInt(), y = origin.y.roundToInt())
                },
            )
            .size(width = width, height = height)
            .shadow(
                elevation = dimensionResource(id = R.dimen.home_module_drag_shadow_elevation),
                shape = shape,
                clip = false,
            )
            .clearAndSetSemantics(properties = {})
            .testTag(tag = "home_application_movement_preview"),
        content = {
            if (!ribbon && session.module.namePlacement == FavoriteNamePlacement.Below) {
                HomeFavoriteBelowItem(
                    modifier = Modifier.fillMaxWidth(),
                    availability = session.availability,
                    listSize = session.module.applicationSize,
                    onClick = {},
                    onLongClick = {},
                    interactionEnabled = false,
                )
            } else {
                HomeFavoriteRow(
                    modifier = Modifier.fillMaxWidth(),
                    availability = session.availability,
                    listSize = session.module.applicationSize,
                    onClick = {},
                    onLongClick = {},
                    editMode = false,
                    compact = ribbon,
                    exchangeHighlight = false,
                    onRowBoundsInWindow = { _, _ -> },
                    onHandleBoundsInWindow = {},
                    interactionEnabled = false,
                )
            }
        },
    )
}
