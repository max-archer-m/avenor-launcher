package com.avenor.launcher

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource

@Composable
internal fun DrawerDisplaySettingsPanel(
    settings: DrawerDisplaySettings,
    enabled: Boolean,
    onChangeSettings: (DrawerDisplaySettings) -> Unit,
    onDismiss: () -> Unit,
) {
    BackHandler(onBack = onDismiss)
    var panelBounds by remember { mutableStateOf(Rect.Zero) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(key1 = panelBounds) {
                detectTapGestures { position ->
                    if (position !in panelBounds) {
                        onDismiss()
                    }
                }
            }
            .testTag(tag = "drawer_display_settings_modal"),
    ) {
        val panelShape = RoundedCornerShape(
            size = dimensionResource(id = R.dimen.style_settings_panel_corner_radius),
        )
        Column(
            modifier = Modifier
                .align(alignment = Alignment.BottomCenter)
                .windowInsetsPadding(insets = WindowInsets.safeDrawing)
                .padding(
                    start = dimensionResource(
                        id = R.dimen.drawer_display_settings_horizontal_margin,
                    ),
                    end = dimensionResource(
                        id = R.dimen.drawer_display_settings_horizontal_margin,
                    ),
                    bottom = dimensionResource(
                        id = R.dimen.drawer_display_settings_bottom_margin,
                    ),
                )
                .fillMaxWidth()
                .shadow(
                    elevation = dimensionResource(id = R.dimen.style_settings_panel_elevation),
                    shape = panelShape,
                    clip = false,
                )
                .clip(shape = panelShape)
                .background(color = colorResource(id = R.color.avenor_sheet_surface))
                .onGloballyPositioned { coordinates ->
                    panelBounds = coordinates.boundsInParent()
                }
                .testTag(tag = "drawer_display_settings_panel"),
        ) {
            val options = DrawerApplicationSize.values()
            StyleApplicationSizeRow(
                title = stringResource(id = R.string.drawer_application_size),
                optionLabels = options.map { option ->
                    stringResource(
                        id = when (option) {
                            DrawerApplicationSize.Large -> R.string.favorite_list_large
                            DrawerApplicationSize.Medium -> R.string.favorite_list_medium
                            DrawerApplicationSize.Small -> R.string.favorite_list_small
                        },
                    )
                },
                optionIconSizes = options.map { option ->
                    dimensionResource(id = option.iconSizeResource())
                },
                selectedIndex = options.indexOf(element = settings.applicationSize),
                enabled = enabled,
                onSelectIndex = { index ->
                    onChangeSettings(
                        settings.copy(applicationSize = options[index]),
                    )
                },
                optionTestTagPrefix = "drawer_application_size_option",
                modifier = Modifier.testTag(tag = "drawer_application_size_setting"),
            )
            val placementOptions = DrawerNamePlacement.values()
            val validRange = validItemsPerRowRange(
                namePlacement = settings.namePlacement,
            )
            StyleArrangementRow(
                title = stringResource(id = R.string.drawer_application_arrangement),
                optionLabels = placementOptions.map { placement ->
                    stringResource(
                        id = when (placement) {
                            DrawerNamePlacement.Right -> R.string.drawer_name_right
                            DrawerNamePlacement.Below -> R.string.drawer_name_below
                        },
                    )
                },
                selectedIndex = placementOptions.indexOf(element = settings.namePlacement),
                value = settings.itemsPerRow,
                minimum = validRange.first,
                maximum = validRange.last,
                decrementLabel = stringResource(id = R.string.home_decrement_symbol),
                incrementLabel = stringResource(id = R.string.home_increment_symbol),
                enabled = enabled,
                onSelectIndex = { index ->
                    val placement = placementOptions[index]
                    onChangeSettings(
                        settings.copy(
                            namePlacement = placement,
                            itemsPerRow = settings.itemsPerRow.coerceIn(
                                range = validItemsPerRowRange(namePlacement = placement),
                            ),
                        ),
                    )
                },
                onChangeValue = { value ->
                    onChangeSettings(settings.copy(itemsPerRow = value))
                },
                testTagPrefix = "drawer",
            )
        }
    }
}
