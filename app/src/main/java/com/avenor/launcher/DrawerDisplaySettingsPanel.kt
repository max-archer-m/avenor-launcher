package com.avenor.launcher

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource

@Composable
internal fun DrawerDisplaySettingsPanel(
    settings: DrawerDisplaySettings,
    enabled: Boolean,
    onChangeSettings: (DrawerDisplaySettings) -> Unit,
    onDismiss: () -> Unit,
) {
    DrawerPanelAppearance {
        BackHandler(onBack = onDismiss)
        var panelBounds by remember { mutableStateOf(Rect.Zero) }
        val selection = listOf(
            settings.applicationSize,
            settings.namePlacement,
            settings.sectionAnchorPresentation,
            settings.backgroundMode,
        )
        var settledSelection by remember { mutableStateOf(selection) }
        var selectionPending by remember { mutableStateOf(false) }
        var selectionRequest by remember { mutableIntStateOf(0) }
        var initialSelection by remember { mutableStateOf(true) }
        val animationDuration = integerResource(R.integer.short_property_animation_duration_ms)
        LaunchedEffect(selection, selectionRequest) {
            if (!initialSelection) {
                // Use the selector's animation clock (including reduced-motion settings),
                // not a wall-clock delay. A rollback restarts this interval as well.
                Animatable(0f).animateTo(1f, animationSpec = tween(animationDuration))
            }
            initialSelection = false
            settledSelection = selection
            selectionPending = false
        }
        val mutationEnabled = enabled && !selectionPending && selection == settledSelection
        fun changeSettings(candidate: DrawerDisplaySettings) {
            if (!enabled || selectionPending || selection != settledSelection || candidate == settings) {
                return
            }
            if (candidate.applicationSize != settings.applicationSize ||
                candidate.namePlacement != settings.namePlacement ||
                candidate.sectionAnchorPresentation != settings.sectionAnchorPresentation ||
                candidate.backgroundMode != settings.backgroundMode
            ) {
                // Close the gate synchronously, before another activation can arrive.
                selectionPending = true
                selectionRequest++
            }
            onChangeSettings(candidate)
        }
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
                val placementOptions = DrawerNamePlacement.values()
                val validRange = validItemsPerRowRange(
                    namePlacement = settings.namePlacement,
                )
                StyleArrangementBlock(
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
                    enabled = mutationEnabled,
                    onSelectIndex = { index ->
                        val placement = placementOptions[index]
                        changeSettings(
                            settings.copy(
                                namePlacement = placement,
                                itemsPerRow = settings.itemsPerRow.coerceIn(
                                    range = validItemsPerRowRange(namePlacement = placement),
                                ),
                            ),
                        )
                    },
                    onChangeValue = { value ->
                        changeSettings(settings.copy(itemsPerRow = value))
                    },
                    testTagPrefix = "drawer",
                )
                val anchorOptions = DrawerSectionAnchorPresentation.entries
                StyleSelectorBlock(
                    title = stringResource(R.string.drawer_section_anchor_presentation),
                    optionLabels = listOf(
                        stringResource(R.string.drawer_section_anchor_inline),
                        stringResource(R.string.drawer_section_anchor_left),
                    ),
                    selectedIndex = anchorOptions.indexOf(settings.sectionAnchorPresentation),
                    enabled = mutationEnabled,
                    onSelectIndex = { index ->
                        changeSettings(settings.copy(sectionAnchorPresentation = anchorOptions[index]))
                    },
                    testTagPrefix = "drawer_section_anchor",
                )
                val backgroundOptions = DrawerBackgroundMode.entries
                StyleSelectorBlock(
                    title = stringResource(R.string.drawer_background),
                    optionLabels = listOf(
                        stringResource(R.string.drawer_background_transparent),
                        stringResource(R.string.drawer_background_frosted_glass),
                    ),
                    selectedIndex = backgroundOptions.indexOf(settings.backgroundMode),
                    enabled = mutationEnabled,
                    onSelectIndex = { index ->
                        changeSettings(settings.copy(backgroundMode = backgroundOptions[index]))
                    },
                    testTagPrefix = "drawer_background",
                    wide = true,
                )
                val options = DrawerApplicationSize.values()
                StyleApplicationSizeBlock(
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
                    enabled = mutationEnabled,
                    onSelectIndex = { index ->
                        changeSettings(
                            settings.copy(applicationSize = options[index]),
                        )
                    },
                    optionTestTagPrefix = "drawer_application_size_option",
                    modifier = Modifier.testTag(tag = "drawer_application_size_setting"),
                )
            }
        }
    }
}
