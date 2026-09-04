package com.avenor.launcher

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp

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
    val options = FavoriteListSize.values()
    StyleApplicationSizeRow(
        title = stringResource(id = R.string.home_application_size),
        optionLabels = options.map { option ->
            stringResource(
                id = when (option) {
                    FavoriteListSize.Large -> R.string.favorite_list_large
                    FavoriteListSize.Medium -> R.string.favorite_list_medium
                    FavoriteListSize.Small -> R.string.favorite_list_small
                },
            )
        },
        optionIconSizes = options.map { option ->
            dimensionResource(id = option.iconSizeResource())
        },
        selectedIndex = options.indexOf(element = selected),
        enabled = enabled,
        onSelectIndex = { index -> onSelect(options[index]) },
    )
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
    val options = FavoriteNamePlacement.values()
    StyleArrangementRow(
        title = stringResource(id = R.string.home_application_arrangement),
        optionLabels = options.map { option ->
            stringResource(
                id = when (option) {
                    FavoriteNamePlacement.Right -> R.string.home_name_right
                    FavoriteNamePlacement.Below -> R.string.home_name_below
                },
            )
        },
        selectedIndex = options.indexOf(element = placement),
        value = value,
        minimum = 1,
        maximum = maximum,
        decrementLabel = stringResource(id = R.string.home_decrement_symbol),
        incrementLabel = stringResource(id = R.string.home_increment_symbol),
        enabled = enabled,
        onSelectIndex = { index -> onChangePlacement(options[index]) },
        onChangeValue = onChangeCount,
        testTagPrefix = "home",
    )
}
