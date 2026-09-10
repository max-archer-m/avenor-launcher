package com.avenor.launcher

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap

@Composable
internal fun StyleApplicationSizeBlock(
    title: String,
    optionLabels: List<String>,
    optionIconSizes: List<Dp>,
    selectedIndex: Int,
    enabled: Boolean,
    onSelectIndex: (Int) -> Unit,
    optionTestTagPrefix: String? = null,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
) {
    require(optionLabels.size == optionIconSizes.size)
    require(selectedIndex in optionLabels.indices)
    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        StyleTitleLine(text = title)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(
                    height = dimensionResource(
                        id = R.dimen.style_settings_application_size_line_height,
                    ),
                )
                .padding(
                    horizontal = dimensionResource(id = R.dimen.style_settings_panel_row_inset),
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .weight(weight = 1f)
                    .selectableGroup()
                    .semantics { contentDescription = title }
                    .horizontalScroll(state = rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                optionLabels.indices.forEach { index ->
                    val iconSize = optionIconSizes[index]
                    val iconPixels = with(LocalDensity.current) { iconSize.roundToPx() }
                    val icon = remember(key1 = index, key2 = iconPixels) {
                        context.packageManager.defaultActivityIcon
                            .toBitmap(width = iconPixels, height = iconPixels)
                            .asImageBitmap()
                    }
                    Row(
                        modifier = Modifier
                            .height(
                                height = dimensionResource(
                                    id = R.dimen.style_settings_application_size_line_height,
                                ),
                            )
                            .selectable(
                                selected = index == selectedIndex,
                                enabled = enabled,
                                role = Role.RadioButton,
                                onClick = { if (index != selectedIndex) onSelectIndex(index) },
                            )
                            .padding(
                                end = dimensionResource(
                                    id = R.dimen.style_settings_size_option_end_padding,
                                ),
                            )
                            .then(
                                optionTestTagPrefix?.let { prefix ->
                                    Modifier.testTag(tag = "${prefix}_$index")
                                } ?: Modifier,
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = index == selectedIndex,
                            onClick = null,
                            modifier = Modifier.size(
                                size = dimensionResource(id = R.dimen.style_settings_indicator_size),
                            ).clearAndSetSemantics { },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.onSurface,
                                unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledSelectedColor = MaterialTheme.colorScheme.onSurface,
                                disabledUnselectedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        )
                        Spacer(
                            modifier = Modifier.width(
                                width = dimensionResource(
                                    id = R.dimen.style_settings_indicator_icon_gap,
                                ),
                            ),
                        )
                        Image(
                            bitmap = icon,
                            contentDescription = null,
                            modifier = Modifier.size(size = iconSize),
                        )
                        Spacer(
                            modifier = Modifier.width(
                                width = dimensionResource(id = R.dimen.style_settings_icon_label_gap),
                            ),
                        )
                        Text(
                            text = optionLabels[index],
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StyleTitleLine(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(id = R.dimen.style_settings_title_line_height))
            .padding(horizontal = dimensionResource(id = R.dimen.style_settings_panel_row_inset)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
        )
    }
}

@Composable
internal fun StyleArrangementBlock(
    title: String,
    optionLabels: List<String>,
    selectedIndex: Int,
    value: Int,
    minimum: Int,
    maximum: Int,
    decrementLabel: String,
    incrementLabel: String,
    enabled: Boolean,
    onSelectIndex: (Int) -> Unit,
    onChangeValue: (Int) -> Unit,
    testTagPrefix: String,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
) {
    require(optionLabels.size == 2)
    require(selectedIndex in optionLabels.indices)
    require(value in minimum..maximum)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag(tag = "${testTagPrefix}_application_arrangement"),
    ) {
        StyleTitleLine(text = title)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(height = dimensionResource(id = R.dimen.style_settings_content_line_height))
                .padding(
                    horizontal = dimensionResource(id = R.dimen.style_settings_panel_row_inset),
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .weight(weight = 1f)
                    .horizontalScroll(state = rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StyleTwoOptionSelector(
                    title = title,
                    optionLabels = optionLabels,
                    selectedIndex = selectedIndex,
                    enabled = enabled,
                    onSelectIndex = onSelectIndex,
                    testTagPrefix = "${testTagPrefix}_name_placement",
                )
                Spacer(
                    modifier = Modifier.width(
                        width = dimensionResource(id = R.dimen.style_settings_control_gap),
                    ),
                )
                StyleItemsPerRowStepper(
                    value = value,
                    minimum = minimum,
                    maximum = maximum,
                    decrementLabel = decrementLabel,
                    incrementLabel = incrementLabel,
                    enabled = enabled,
                    onChangeValue = onChangeValue,
                    testTagPrefix = "${testTagPrefix}_items_per_row",
                )
            }
        }
    }
}

@Composable
internal fun StyleSelectorBlock(
    title: String,
    optionLabels: List<String>,
    selectedIndex: Int,
    enabled: Boolean,
    onSelectIndex: (Int) -> Unit,
    testTagPrefix: String,
    wide: Boolean = false,
) {
    require(optionLabels.size == 2)
    require(selectedIndex in optionLabels.indices)
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        StyleTitleLine(text = title)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.style_settings_content_line_height))
                .padding(horizontal = dimensionResource(R.dimen.style_settings_panel_row_inset)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f).horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StyleTwoOptionSelector(
                    title = title,
                    optionLabels = optionLabels,
                    selectedIndex = selectedIndex,
                    enabled = enabled,
                    onSelectIndex = onSelectIndex,
                    testTagPrefix = testTagPrefix,
                    wide = wide,
                )
            }
        }
    }
}

@Composable
private fun StyleTwoOptionSelector(
    title: String,
    optionLabels: List<String>,
    selectedIndex: Int,
    enabled: Boolean,
    onSelectIndex: (Int) -> Unit,
    testTagPrefix: String,
    wide: Boolean = false,
) {
    val animationDuration = integerResource(
        id = R.integer.short_property_animation_duration_ms,
    )
    val frameShape = RoundedCornerShape(
        size = dimensionResource(id = R.dimen.style_settings_selector_frame_radius),
    )
    Box(
        modifier = Modifier
            .size(
                width = dimensionResource(
                    id = if (wide) R.dimen.drawer_background_selector_width
                    else R.dimen.style_settings_selector_width,
                ),
                height = dimensionResource(id = R.dimen.style_settings_stepper_target_size),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.style_settings_selector_height))
                .clip(frameShape)
                .background(colorResource(R.color.avenor_sheet_surface))
                .border(
                    width = dimensionResource(R.dimen.style_settings_selector_border_width),
                    color = MaterialTheme.colorScheme.onSurface.copy(
                        alpha = integerResource(R.integer.style_settings_selector_border_alpha_percent) / 100f,
                    ),
                    shape = frameShape,
                ),
        )
        Row(
            modifier = Modifier.fillMaxWidth()
                .selectableGroup()
                .semantics { contentDescription = title }
                .padding(horizontal = dimensionResource(R.dimen.style_settings_selector_inner_padding)),
        ) {
            optionLabels.forEachIndexed { index, label ->
                val selected = index == selectedIndex
                val backgroundColor by animateColorAsState(
                    targetValue = if (selected) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        Color.Transparent
                    },
                    animationSpec = tween(durationMillis = animationDuration),
                    label = "${testTagPrefix}_background",
                )
                val contentColor by animateColorAsState(
                    targetValue = if (selected) {
                        colorResource(id = R.color.avenor_sheet_surface)
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    animationSpec = tween(durationMillis = animationDuration),
                    label = "${testTagPrefix}_content",
                )
                Box(
                    modifier = Modifier
                        .weight(weight = 1f)
                        .height(dimensionResource(R.dimen.style_settings_stepper_target_size))
                        .clip(
                            shape = RoundedCornerShape(
                                size = dimensionResource(
                                    id = R.dimen.style_settings_selector_thumb_radius,
                                ),
                            ),
                        )
                        .selectable(
                            selected = selected,
                            enabled = enabled,
                            role = Role.RadioButton,
                            onClick = { if (!selected) onSelectIndex(index) },
                        )
                        .testTag(tag = "${testTagPrefix}_$index"),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .height(dimensionResource(R.dimen.style_settings_selector_thumb_height))
                            .clip(RoundedCornerShape(dimensionResource(R.dimen.style_settings_selector_thumb_radius)))
                            .background(backgroundColor),
                    )
                    Text(
                        text = label,
                        color = contentColor,
                        fontWeight = FontWeight.Medium,
                        fontSize = dimensionResource(
                            id = R.dimen.style_settings_secondary_text_size,
                        ).value.sp,
                        lineHeight = dimensionResource(
                            id = R.dimen.style_settings_secondary_line_height,
                        ).value.sp,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@Composable
private fun StyleItemsPerRowStepper(
    value: Int,
    minimum: Int,
    maximum: Int,
    decrementLabel: String,
    incrementLabel: String,
    enabled: Boolean,
    onChangeValue: (Int) -> Unit,
    testTagPrefix: String,
) {
    val valueLabel = stringResource(R.string.style_settings_items_per_row)
    Row(verticalAlignment = Alignment.CenterVertically) {
        StyleStepperControl(
            text = decrementLabel,
            actionLabel = stringResource(R.string.style_settings_decrease_items_per_row),
            enabled = enabled && value > minimum,
            available = value > minimum,
            testTag = "${testTagPrefix}_decrement",
            onClick = { onChangeValue(value - 1) },
        )
        Box(
            modifier = Modifier
                .size(size = dimensionResource(id = R.dimen.style_settings_stepper_target_size))
                .clearAndSetSemantics {
                    contentDescription = valueLabel
                    stateDescription = value.toString()
                    if (!enabled) disabled()
                }
                .testTag(tag = "${testTagPrefix}_value"),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(
                        width = dimensionResource(id = R.dimen.style_settings_stepper_value_width),
                        height = dimensionResource(
                            id = R.dimen.style_settings_stepper_visible_height,
                        ),
                    )
                    .clip(
                        shape = RoundedCornerShape(
                            size = dimensionResource(id = R.dimen.style_settings_stepper_radius),
                        ),
                    )
                    .background(color = colorResource(id = R.color.style_settings_control_surface)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = value.toString(),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )
            }
        }
        StyleStepperControl(
            text = incrementLabel,
            actionLabel = stringResource(R.string.style_settings_increase_items_per_row),
            enabled = enabled && value < maximum,
            available = value < maximum,
            testTag = "${testTagPrefix}_increment",
            onClick = { onChangeValue(value + 1) },
        )
    }
}

@Composable
private fun StyleStepperControl(
    text: String,
    actionLabel: String,
    enabled: Boolean,
    available: Boolean,
    testTag: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(size = dimensionResource(id = R.dimen.style_settings_stepper_target_size))
            .clip(RoundedCornerShape(dimensionResource(R.dimen.style_settings_stepper_radius)))
            .clickable(
                enabled = enabled,
                role = Role.Button,
                onClickLabel = actionLabel,
                onClick = onClick,
            )
            .semantics { contentDescription = actionLabel }
            .alpha(alpha = styleSettingsContentAlpha(available))
            .testTag(tag = testTag),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(size = dimensionResource(id = R.dimen.style_settings_stepper_visible_size))
                .clip(
                    shape = RoundedCornerShape(
                        size = dimensionResource(id = R.dimen.style_settings_stepper_radius),
                    ),
                )
                .background(color = colorResource(id = R.color.style_settings_control_surface)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.clearAndSetSemantics { },
            )
        }
    }
}

@Composable
private fun styleSettingsContentAlpha(enabled: Boolean): Float =
    if (enabled) 1f else integerResource(R.integer.disabled_content_alpha_percent) / 100f

/**
 * The one shared style settings panel surface consumed by both hosts: border, corner
 * radius, and surface color. Hosts keep only their own placement, sizing, scrolling, and
 * insets; they must not re-assemble these surface values.
 */
@Composable
internal fun Modifier.styleSettingsPanelSurface(): Modifier {
    val panelCornerRadius = dimensionResource(R.dimen.style_settings_panel_corner_radius)
    val panelShape = RoundedCornerShape(size = panelCornerRadius)
    return this
        .clip(shape = panelShape)
        .background(color = colorResource(R.color.avenor_sheet_surface))
        .border(
            width = dimensionResource(R.dimen.style_settings_panel_border_width),
            color = colorResource(R.color.style_settings_panel_border),
            shape = panelShape,
        )
}
