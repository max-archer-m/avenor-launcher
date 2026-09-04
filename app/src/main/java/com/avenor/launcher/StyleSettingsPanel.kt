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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap

@Composable
internal fun StyleApplicationSizeRow(
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

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height = dimensionResource(id = R.dimen.style_settings_panel_row_height))
            .padding(
                horizontal = dimensionResource(id = R.dimen.style_settings_panel_row_inset),
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
        )
        Spacer(
            modifier = Modifier.width(
                width = dimensionResource(id = R.dimen.style_settings_title_control_gap),
            ),
        )
        Row(
            modifier = Modifier
                .weight(weight = 1f)
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
                                id = R.dimen.style_settings_panel_row_height,
                            ),
                        )
                        .clickable(
                            enabled = enabled && index != selectedIndex,
                            role = Role.RadioButton,
                            onClick = { onSelectIndex(index) },
                        )
                        .alpha(alpha = if (enabled) 1f else 0.38f)
                        .padding(
                            horizontal = dimensionResource(
                                id = R.dimen.style_settings_size_option_horizontal_padding,
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
                        ),
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

@Composable
internal fun StyleArrangementRow(
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
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height = dimensionResource(id = R.dimen.style_settings_panel_row_height))
            .padding(
                horizontal = dimensionResource(id = R.dimen.style_settings_panel_row_inset),
            )
            .testTag(tag = "${testTagPrefix}_application_arrangement"),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
        )
        Spacer(
            modifier = Modifier.width(
                width = dimensionResource(id = R.dimen.style_settings_title_control_gap),
            ),
        )
        Row(
            modifier = Modifier
                .weight(weight = 1f)
                .horizontalScroll(state = rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StyleTwoOptionSelector(
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

@Composable
private fun StyleTwoOptionSelector(
    optionLabels: List<String>,
    selectedIndex: Int,
    enabled: Boolean,
    onSelectIndex: (Int) -> Unit,
    testTagPrefix: String,
) {
    val animationDuration = integerResource(
        id = R.integer.short_property_animation_duration_ms,
    )
    val frameShape = RoundedCornerShape(
        size = dimensionResource(id = R.dimen.style_settings_selector_frame_radius),
    )
    Row(
        modifier = Modifier
            .size(
                width = dimensionResource(id = R.dimen.style_settings_selector_width),
                height = dimensionResource(id = R.dimen.style_settings_selector_height),
            )
            .clip(shape = frameShape)
            .background(color = colorResource(id = R.color.avenor_sheet_surface))
            .border(
                width = dimensionResource(id = R.dimen.style_settings_selector_border_width),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                shape = frameShape,
            )
            .padding(
                all = dimensionResource(id = R.dimen.style_settings_selector_inner_padding),
            ),
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
                    .fillMaxHeight()
                    .clip(
                        shape = RoundedCornerShape(
                            size = dimensionResource(
                                id = R.dimen.style_settings_selector_thumb_radius,
                            ),
                        ),
                    )
                    .background(color = backgroundColor)
                    .clickable(
                        enabled = enabled && !selected,
                        role = Role.RadioButton,
                        onClick = { onSelectIndex(index) },
                    )
                    .alpha(alpha = if (enabled) 1f else 0.38f)
                    .testTag(tag = "${testTagPrefix}_$index"),
                contentAlignment = Alignment.Center,
            ) {
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
    Row(verticalAlignment = Alignment.CenterVertically) {
        StyleStepperControl(
            text = decrementLabel,
            enabled = enabled && value > minimum,
            testTag = "${testTagPrefix}_decrement",
            onClick = { onChangeValue(value - 1) },
        )
        Box(
            modifier = Modifier
                .size(size = dimensionResource(id = R.dimen.style_settings_stepper_target_size))
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
            enabled = enabled && value < maximum,
            testTag = "${testTagPrefix}_increment",
            onClick = { onChangeValue(value + 1) },
        )
    }
}

@Composable
private fun StyleStepperControl(
    text: String,
    enabled: Boolean,
    testTag: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(size = dimensionResource(id = R.dimen.style_settings_stepper_target_size))
            .clickable(
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .alpha(alpha = if (enabled) 1f else 0.38f)
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
            Text(text = text, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
