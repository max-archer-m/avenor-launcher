package com.avenor.launcher

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.compose.ui.graphics.asImageBitmap

@Composable
internal fun HomeModuleStylePanel(
    selectedModule: OrderedFavoriteModule?,
    enabled: Boolean,
    maximumHeight: Dp,
    onChangeSize: (FavoriteListSize) -> Unit,
    onChangeNamePlacement: (FavoriteNamePlacement) -> Unit,
    onChangeItemsPerRow: (Int) -> Unit,
) {
    val panelShape = RoundedCornerShape(
        dimensionResource(R.dimen.style_settings_panel_corner_radius),
    )
    val animationDuration = integerResource(
        R.integer.short_property_animation_duration_ms,
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = maximumHeight)
            .animateContentSize(
                animationSpec = tween(durationMillis = animationDuration),
            )
            .shadow(
                elevation = dimensionResource(R.dimen.style_settings_panel_elevation),
                shape = panelShape,
                clip = false,
            )
            .clip(panelShape)
            .background(colorResource(R.color.avenor_sheet_surface))
            .verticalScroll(rememberScrollState())
            .testTag("home_style_panel"),
    ) {
        if (selectedModule == null) {
            HomeStylePanelRow(
                label = stringResource(R.string.home_select_favorite_list_prompt),
            )
        } else if (selectedModule.type == OrderedFavoriteModuleType.Vertical) {
            HomeApplicationSizeRow(
                selected = selectedModule.applicationSize,
                enabled = enabled,
                onSelect = onChangeSize,
            )
            HomeStyleArrangementRow(
                placement = selectedModule.namePlacement,
                value = selectedModule.itemsPerRow,
                maximum = if (selectedModule.namePlacement == FavoriteNamePlacement.Right) 2 else 4,
                enabled = enabled,
                onChangePlacement = onChangeNamePlacement,
                onChangeCount = onChangeItemsPerRow,
            )
        } else {
            HomeStylePanelRow(
                label = stringResource(R.string.home_ribbon_fixed_style),
            )
        }
    }
}

@Composable
private fun HomeStylePanelRow(label: String, value: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.style_settings_panel_row_height))
            .padding(horizontal = dimensionResource(R.dimen.style_settings_panel_row_inset)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium,
            fontSize = dimensionResource(R.dimen.style_settings_secondary_text_size).value.sp,
            lineHeight = dimensionResource(R.dimen.style_settings_secondary_line_height).value.sp,
        )
        value?.let { Text(text = it, color = MaterialTheme.colorScheme.onSurface) }
    }
}

@Composable
private fun HomeApplicationSizeRow(
    selected: FavoriteListSize,
    enabled: Boolean,
    onSelect: (FavoriteListSize) -> Unit,
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.style_settings_panel_row_height))
            .padding(horizontal = dimensionResource(R.dimen.style_settings_panel_row_inset)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.home_application_size),
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.width(dimensionResource(R.dimen.style_settings_title_control_gap)))
        Row(
            modifier = Modifier.weight(1f).horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FavoriteListSize.values().forEach { option ->
                val iconSize = dimensionResource(option.iconSizeResource())
                val iconPixels = with(LocalDensity.current) { iconSize.roundToPx() }
                val icon = remember(option, iconPixels) {
                    context.packageManager.defaultActivityIcon
                        .toBitmap(iconPixels, iconPixels)
                        .asImageBitmap()
                }
                Row(
                    modifier = Modifier
                        .height(dimensionResource(R.dimen.style_settings_panel_row_height))
                        .clickable(
                            enabled = enabled && option != selected,
                            role = Role.RadioButton,
                            onClick = { onSelect(option) },
                        )
                        .alpha(if (enabled) 1f else 0.38f)
                        .padding(
                            horizontal = dimensionResource(
                                R.dimen.style_settings_size_option_horizontal_padding,
                            ),
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = option == selected,
                        onClick = null,
                        modifier = Modifier.size(
                            dimensionResource(R.dimen.style_settings_indicator_size),
                        ),
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.onSurface,
                            unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledSelectedColor = MaterialTheme.colorScheme.onSurface,
                            disabledUnselectedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    )
                    Spacer(Modifier.width(dimensionResource(R.dimen.style_settings_indicator_icon_gap)))
                    Image(bitmap = icon, contentDescription = null, modifier = Modifier.size(iconSize))
                    Spacer(Modifier.width(dimensionResource(R.dimen.style_settings_icon_label_gap)))
                    Text(
                        text = stringResource(
                            when (option) {
                                FavoriteListSize.Large -> R.string.favorite_list_large
                                FavoriteListSize.Medium -> R.string.favorite_list_medium
                                FavoriteListSize.Small -> R.string.favorite_list_small
                            },
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeStyleArrangementRow(
    placement: FavoriteNamePlacement,
    value: Int,
    maximum: Int,
    enabled: Boolean,
    onChangePlacement: (FavoriteNamePlacement) -> Unit,
    onChangeCount: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.style_settings_panel_row_height))
            .padding(horizontal = dimensionResource(R.dimen.style_settings_panel_row_inset))
            .testTag("home_application_arrangement"),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.home_application_arrangement),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
        )
        Spacer(Modifier.width(dimensionResource(R.dimen.style_settings_title_control_gap)))
        Row(
            modifier = Modifier.weight(1f).horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HomeNamePlacementSelector(selected = placement, enabled = enabled, onSelect = onChangePlacement)
            Spacer(Modifier.width(dimensionResource(R.dimen.style_settings_control_gap)))
            HomeItemsPerRowStepper(
                value = value,
                maximum = maximum,
                enabled = enabled,
                onChange = onChangeCount,
            )
        }
    }
}

@Composable
private fun HomeNamePlacementSelector(
    selected: FavoriteNamePlacement,
    enabled: Boolean,
    onSelect: (FavoriteNamePlacement) -> Unit,
) {
    val animationDuration = integerResource(R.integer.short_property_animation_duration_ms)
    val frameShape = RoundedCornerShape(
        dimensionResource(R.dimen.style_settings_selector_frame_radius),
    )
    Row(
        modifier = Modifier
            .size(
                width = dimensionResource(R.dimen.style_settings_selector_width),
                height = dimensionResource(R.dimen.style_settings_selector_height),
            )
            .clip(frameShape)
            .background(colorResource(R.color.avenor_sheet_surface))
            .border(
                width = dimensionResource(R.dimen.style_settings_selector_border_width),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                shape = frameShape,
            )
            .padding(dimensionResource(R.dimen.style_settings_selector_inner_padding)),
    ) {
        listOf(
            FavoriteNamePlacement.Right to stringResource(R.string.home_name_right),
            FavoriteNamePlacement.Below to stringResource(R.string.home_name_below),
        ).forEach { (option, label) ->
            val isSelected = option == selected
            val background by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                animationSpec = tween(durationMillis = animationDuration),
                label = "home-name-placement-background",
            )
            val contentColor by animateColorAsState(
                targetValue = if (isSelected) {
                    colorResource(R.color.avenor_sheet_surface)
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                animationSpec = tween(durationMillis = animationDuration),
                label = "home-name-placement-content",
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(
                        RoundedCornerShape(
                            dimensionResource(R.dimen.style_settings_selector_thumb_radius),
                        ),
                    )
                    .background(background)
                    .clickable(
                        enabled = enabled && !isSelected,
                        role = Role.RadioButton,
                        onClick = { onSelect(option) },
                    )
                    .alpha(if (enabled) 1f else 0.38f),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = contentColor,
                    fontWeight = FontWeight.Medium,
                    fontSize = dimensionResource(R.dimen.style_settings_secondary_text_size).value.sp,
                    lineHeight = dimensionResource(R.dimen.style_settings_secondary_line_height).value.sp,
                )
            }
        }
    }
}

@Composable
private fun HomeItemsPerRowStepper(
    value: Int,
    maximum: Int,
    enabled: Boolean,
    onChange: (Int) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        HomeStepperControl(
            text = stringResource(R.string.home_decrement_symbol),
            enabled = enabled && value > 1,
            testTag = "home_items_per_row_decrement",
            onClick = { onChange(value - 1) },
        )
        Box(
            modifier = Modifier
                .size(dimensionResource(R.dimen.style_settings_stepper_target_size))
                .testTag("home_items_per_row_value"),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(
                        width = dimensionResource(R.dimen.style_settings_stepper_value_width),
                        height = dimensionResource(R.dimen.style_settings_stepper_visible_height),
                    )
                    .clip(RoundedCornerShape(dimensionResource(R.dimen.style_settings_stepper_radius)))
                    .background(colorResource(R.color.style_settings_control_surface)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = value.toString(),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )
            }
        }
        HomeStepperControl(
            text = stringResource(R.string.home_increment_symbol),
            enabled = enabled && value < maximum,
            testTag = "home_items_per_row_increment",
            onClick = { onChange(value + 1) },
        )
    }
}

@Composable
private fun HomeStepperControl(
    text: String,
    enabled: Boolean,
    testTag: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(dimensionResource(R.dimen.style_settings_stepper_target_size))
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .alpha(if (enabled) 1f else 0.38f)
            .testTag(testTag),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(dimensionResource(R.dimen.style_settings_stepper_visible_size))
                .clip(RoundedCornerShape(dimensionResource(R.dimen.style_settings_stepper_radius)))
                .background(colorResource(R.color.style_settings_control_surface)),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = text, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
