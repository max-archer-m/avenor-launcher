package com.avenor.launcher.ui.home.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.avenor.launcher.FavoriteListSize
import com.avenor.launcher.R
import com.avenor.launcher.iconSizeResource
import com.avenor.launcher.textSizeResource
import com.avenor.launcher.ui.home.components.detectHomeReorderDrag

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
internal fun FavoriteListSizeControl(
    index: Int,
    selectedSize: FavoriteListSize,
    onChangeSize: (FavoriteListSize) -> Unit,
) {
    var expanded by remember(index) { mutableStateOf(false) }
    val interactionSource = remember(index) { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val targetWidth = dimensionResource(R.dimen.home_favorite_list_control_target_width)
    val targetHeight = dimensionResource(R.dimen.home_favorite_list_control_bar_height)
    val stateSize = dimensionResource(R.dimen.home_favorite_list_control_state_size)
    val selectedLabel = when (selectedSize) {
        FavoriteListSize.Large -> stringResource(R.string.favorite_list_large_short)
        FavoriteListSize.Medium -> stringResource(R.string.favorite_list_medium_short)
        FavoriteListSize.Small -> stringResource(R.string.favorite_list_small_short)
    }
    val accessibilityLabel = stringResource(
        R.string.favorite_list_size_format,
        when (selectedSize) {
            FavoriteListSize.Large -> stringResource(R.string.favorite_list_large)
            FavoriteListSize.Medium -> stringResource(R.string.favorite_list_medium)
            FavoriteListSize.Small -> stringResource(R.string.favorite_list_small)
        },
    )
    Box {
        Box(
            modifier = Modifier
                .width(targetWidth)
                .height(targetHeight)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    role = Role.Button,
                    onClick = { expanded = true },
                )
                .semantics { contentDescription = accessibilityLabel }
                .testTag("favorite_list_size_$index"),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(stateSize)
                    .clip(
                        RoundedCornerShape(
                            dimensionResource(
                                R.dimen.home_favorite_list_control_surface_radius,
                            ),
                        ),
                    )
                    .then(
                        if (pressed) {
                            Modifier.background(
                                colorResource(R.color.home_favorite_list_control_pressed),
                            )
                        } else {
                            Modifier
                        },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = selectedLabel,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = dimensionResource(
                        R.dimen.home_favorite_list_size_control_text_size,
                    ).value.sp,
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(
                dimensionResource(R.dimen.home_favorite_list_size_menu_corner_radius),
            ),
            modifier = Modifier.testTag("favorite_list_size_menu_$index"),
        ) {
            FavoriteListSize.values().forEach { size ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = size == selectedSize,
                                onClick = null,
                            )
                            Spacer(
                                modifier = Modifier.width(
                                    dimensionResource(
                                        R.dimen.home_favorite_list_size_menu_indicator_icon_gap,
                                    ),
                                ),
                            )
                            val iconSize = dimensionResource(size.iconSizeResource())
                            val iconPixels = with(LocalDensity.current) {
                                iconSize.roundToPx()
                            }
                            val context = LocalContext.current
                            val defaultIconBitmap = remember(key1 = size, key2 = iconPixels) {
                                requireNotNull(context.getDrawable(R.mipmap.ic_launcher))
                                    .toBitmap(width = iconPixels, height = iconPixels)
                                    .asImageBitmap()
                            }
                            Image(
                                bitmap = defaultIconBitmap,
                                contentDescription = null,
                                modifier = Modifier.size(iconSize),
                            )
                            Spacer(
                                modifier = Modifier.width(
                                    dimensionResource(R.dimen.home_favorite_icon_label_gap),
                                ),
                            )
                            Text(
                                text = when (size) {
                                    FavoriteListSize.Large ->
                                        stringResource(R.string.favorite_list_large)

                                    FavoriteListSize.Medium ->
                                        stringResource(R.string.favorite_list_medium)

                                    FavoriteListSize.Small ->
                                        stringResource(R.string.favorite_list_small)
                                },
                                fontSize = dimensionResource(size.textSizeResource()).value.sp,
                            )
                        }
                    },
                    onClick = {
                        expanded = false
                        if (size != selectedSize) onChangeSize(size)
                    },
                    modifier = Modifier
                        .height(
                            dimensionResource(
                                R.dimen.home_favorite_list_size_menu_item_height,
                            ),
                        )
                        .testTag("favorite_list_size_${index}_${size.name}"),
                )
            }
        }
    }
}

@Composable

internal fun FavoriteListControlBar(
    index: Int,
    listCount: Int,
    selectedSize: FavoriteListSize,
    onChangeSize: (FavoriteListSize) -> Unit,
    onRemoveList: () -> Unit,
    onListDragStart: (Offset) -> Unit,
    onListDrag: (Offset) -> Unit,
    onListDragEnd: () -> Unit,
    onListDragCancel: () -> Unit,
) {
    var removeDialogVisible by remember(index) { mutableStateOf(false) }
    var reorderOriginInWindow by remember(index) { mutableStateOf(Offset.Zero) }
    var reorderDragging by remember(index) { mutableStateOf(false) }
    val hapticFeedback = LocalHapticFeedback.current
    val dividerColor = colorResource(R.color.home_favorite_list_control_border)
    val dividerWidth = with(LocalDensity.current) {
        dimensionResource(R.dimen.home_favorite_list_control_border_width).toPx()
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.home_favorite_list_control_bar_height))
            .drawWithContent {
                drawContent()
                drawLine(
                    color = dividerColor,
                    start = Offset(0f, size.height - dividerWidth / 2f),
                    end = Offset(size.width, size.height - dividerWidth / 2f),
                    strokeWidth = dividerWidth,
                )
            }
            .testTag("favorite_list_control_bar_$index"),
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(Modifier.weight(1f))
                FavoriteListRemoveControl(
                    onClick = { removeDialogVisible = true },
                    testTag = "remove_favorite_list_$index",
                )
                FavoriteListSizeControl(
                    index = index,
                    selectedSize = selectedSize,
                    onChangeSize = onChangeSize,
                )
                Box(
                    modifier = Modifier
                        .width(
                            dimensionResource(R.dimen.home_favorite_list_control_target_width),
                        )
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center,
                ) {
                    if (listCount == 2) {
                        val stateSize = dimensionResource(
                            R.dimen.home_favorite_list_control_state_size,
                        )
                        val iconSize = dimensionResource(
                            R.dimen.home_favorite_list_reorder_icon_size,
                        )
                        val contentDescription =
                            stringResource(R.string.favorite_list_reorder_handle)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .onGloballyPositioned {
                                    reorderOriginInWindow = it.positionInWindow()
                                }
                                .pointerInput(index) {
                                    detectHomeReorderDrag(
                                        onPressChanged = { reorderDragging = it },
                                        onLongPress = {
                                            hapticFeedback.performHapticFeedback(
                                                HapticFeedbackType.LongPress,
                                            )
                                        },
                                        onDragStart = { localTouch ->
                                            onListDragStart(reorderOriginInWindow + localTouch)
                                        },
                                        onDrag = onListDrag,
                                        onDragEnd = {
                                            reorderDragging = false
                                            onListDragEnd()
                                        },
                                        onDragCancel = {
                                            reorderDragging = false
                                            onListDragCancel()
                                        },
                                    )
                                }
                                .semantics {
                                    this.contentDescription = contentDescription
                                    role = Role.Button
                                }
                                .testTag("reorder_favorite_list_$index"),
                            contentAlignment = Alignment.Center,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(stateSize)
                                    .clip(
                                        RoundedCornerShape(
                                            dimensionResource(
                                                R.dimen
                                                    .home_favorite_list_control_surface_radius,
                                            ),
                                        ),
                                    )
                                    .then(
                                        if (reorderDragging) {
                                            Modifier.background(
                                                colorResource(
                                                    R.color
                                                        .home_favorite_list_control_pressed,
                                                ),
                                            )
                                        } else {
                                            Modifier
                                        },
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_drag_handle),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.size(iconSize),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    if (removeDialogVisible) {
        AlertDialog(
            onDismissRequest = { removeDialogVisible = false },
            title = {
                Text(stringResource(R.string.remove_favorite_list_title))
            },
            text = {
                Text(stringResource(R.string.remove_list))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        removeDialogVisible = false
                        onRemoveList()
                    },
                    modifier = Modifier.testTag("confirm_remove_favorite_list_$index"),
                ) {
                    Text(
                        text = stringResource(R.string.remove),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { removeDialogVisible = false },
                    modifier = Modifier.testTag("cancel_remove_favorite_list_$index"),
                ) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

@Composable

internal fun FavoriteListRemoveControl(
    onClick: () -> Unit,
    testTag: String,
) {
    val targetWidth = dimensionResource(R.dimen.home_favorite_list_control_target_width)
    val targetHeight = dimensionResource(R.dimen.home_favorite_list_control_bar_height)
    val badgeSize = dimensionResource(R.dimen.home_favorite_list_remove_badge_size)
    val iconSize = dimensionResource(R.dimen.home_favorite_list_remove_icon_size)
    val contentDescription = stringResource(R.string.remove_favorite_list)
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressedAlpha = integerResource(
        R.integer.home_favorite_list_remove_pressed_alpha_percent,
    ) / 100f
    Box(
        modifier = Modifier
            .width(targetWidth)
            .height(targetHeight)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            )
            .semantics { this.contentDescription = contentDescription }
            .testTag(testTag),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(badgeSize)
                .clip(
                    RoundedCornerShape(
                        dimensionResource(R.dimen.home_favorite_list_control_surface_radius),
                    ),
                )
                .background(MaterialTheme.colorScheme.error)
                .alpha(if (pressed) pressedAlpha else 1f),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = null,
                modifier = Modifier.size(iconSize),
                tint = colorResource(R.color.home_favorite_remove_icon),
            )
        }
    }
}
