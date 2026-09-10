package com.avenor.launcher

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.testTag
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
        // The panel opens and closes with one height property animation — growing from 0 to
        // its content height on open and retracting back on close — on the shared short
        // property-animation token, matching the Home style panel. Host-driven removals
        // outside this panel remain immediate.
        val animationDuration = integerResource(R.integer.short_property_animation_duration_ms)
        var appeared by remember { mutableStateOf(false) }
        var dismissing by remember { mutableStateOf(false) }
        LaunchedEffect(key1 = Unit) { appeared = true }
        fun requestDismiss() {
            if (!dismissing) dismissing = true
        }
        if (dismissing) {
            LaunchedEffect(key1 = Unit) {
                // Cover the exit animation on its own clock, then hand removal to the host.
                Animatable(0f).animateTo(1f, animationSpec = tween(animationDuration))
                onDismiss()
            }
        }
        BackHandler(onBack = { requestDismiss() })
        var panelBounds by remember { mutableStateOf(Rect.Zero) }
        var modalRootOrigin by remember { mutableStateOf(Offset.Zero) }
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
        val mutationEnabled = enabled && !dismissing &&
            !selectionPending && selection == settledSelection
        fun changeSettings(candidate: DrawerDisplaySettings) {
            if (
                !enabled || dismissing || selectionPending ||
                selection != settledSelection || candidate == settings
            ) {
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
                .onGloballyPositioned { coordinates ->
                    modalRootOrigin = coordinates.positionInRoot()
                }
                .pointerInput(key1 = panelBounds) {
                    detectTapGestures { position ->
                        // The tap lands in modal coordinates while the panel bounds track the
                        // root during the height animation; compare both in root space.
                        if (modalRootOrigin + position !in panelBounds) {
                            requestDismiss()
                        }
                    }
                }
                .testTag(tag = "drawer_display_settings_modal"),
        ) {
            AnimatedVisibility(
                visible = appeared && !dismissing,
                enter = expandVertically(
                    animationSpec = tween(durationMillis = animationDuration),
                    expandFrom = Alignment.Bottom,
                ),
                exit = shrinkVertically(
                    animationSpec = tween(durationMillis = animationDuration),
                    shrinkTowards = Alignment.Bottom,
                ),
                modifier = Modifier.align(alignment = Alignment.BottomCenter),
            ) {
                Column(
                    modifier = Modifier
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
                        .styleSettingsPanelSurface()
                        .onGloballyPositioned { coordinates ->
                            panelBounds = coordinates.boundsInRoot()
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
                            changeSettings(
                                settings.copy(
                                    sectionAnchorPresentation = anchorOptions[index],
                                ),
                            )
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
}
