package com.avenor.launcher

import android.content.ComponentName
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Process
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DrawerDisplaySettingsUiTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun ordinaryEntryOpensModalAndOutsideSelectionDismissesIt() {
        composeRule.setContent {
            AvenorTheme {
                DrawerScreen(inventoryLoader = inventory())
            }
        }

        composeRule.onNodeWithTag(testTag = "drawer_display_settings_entry").performClick()
        composeRule.onNodeWithTag(testTag = "drawer_display_settings_panel").assertIsDisplayed()

        composeRule.onNodeWithTag(testTag = "drawer_display_settings_modal")
            .performTouchInput { click() }
        composeRule.onNodeWithTag(testTag = "drawer_display_settings_panel").assertDoesNotExist()
    }

    @Test
    fun applicationSizeSelectionImmediatelyUpdatesDrawerGeometry() {
        composeRule.setContent {
            var settings by remember { mutableStateOf(DrawerDisplaySettings()) }
            AvenorTheme {
                DrawerScreen(
                    inventoryLoader = inventory(),
                    displaySettings = settings,
                    onChangeDisplaySettings = { candidateSettings ->
                        settings = candidateSettings
                    },
                )
            }
        }

        composeRule.onNodeWithTag(testTag = "drawer_application_row")
            .assertHeightIsEqualTo(expectedHeight = 56.dp)
        composeRule.onNodeWithTag(testTag = "drawer_display_settings_entry").performClick()
        composeRule.onNodeWithTag(testTag = "drawer_application_size_option_0").performClick()

        composeRule.onNodeWithTag(testTag = "drawer_application_row")
            .assertHeightIsEqualTo(expectedHeight = 64.dp)
        composeRule.onNodeWithTag(testTag = "drawer_display_settings_panel").assertIsDisplayed()
    }

    @Test
    fun searchModeHidesDisplaySettingsEntry() {
        composeRule.setContent {
            AvenorTheme {
                DrawerScreen(inventoryLoader = inventory())
            }
        }

        composeRule.onNodeWithTag(testTag = "drawer_search_field").performClick()
        composeRule.onNodeWithTag(testTag = "drawer_search_input")
            .performTextInput(text = "Example")

        composeRule.onNodeWithTag(testTag = "drawer_display_settings_entry").assertDoesNotExist()
    }

    @Test
    fun unresolvedSaveDisablesFurtherSizeMutation() {
        var changeCount = 0
        composeRule.setContent {
            AvenorTheme {
                DrawerScreen(
                    inventoryLoader = inventory(),
                    displaySettingsMutationEnabled = false,
                    onChangeDisplaySettings = { changeCount += 1 },
                )
            }
        }

        composeRule.onNodeWithTag(testTag = "drawer_display_settings_entry").performClick()
        composeRule.onNodeWithTag(testTag = "drawer_application_size_option_0").performClick()

        composeRule.runOnIdle { assertEquals(0, changeCount) }
    }

    @Test
    fun belowPlacementEnablesFourColumnsAndRightClampsToTwo() {
        composeRule.setContent {
            var settings by remember { mutableStateOf(DrawerDisplaySettings()) }
            AvenorTheme {
                DrawerScreen(
                    inventoryLoader = inventory(),
                    displaySettings = settings,
                    onChangeDisplaySettings = { candidateSettings ->
                        settings = candidateSettings
                    },
                )
            }
        }

        composeRule.onNodeWithTag(testTag = "drawer_display_settings_entry").performClick()
        composeRule.onNodeWithTag(testTag = "drawer_name_placement_1").performClick()
        repeat(times = 3) {
            composeRule.onNodeWithTag(
                testTag = "drawer_items_per_row_increment",
            ).performClick()
        }
        composeRule.onNodeWithTag(testTag = "drawer_items_per_row_value")
            .assertIsDisplayed()

        composeRule.onNodeWithTag(testTag = "drawer_name_placement_0").performClick()

        composeRule.runOnIdle {
            assertEquals(DrawerNamePlacement.Right, settings.namePlacement)
            assertEquals(2, settings.itemsPerRow)
        }
    }

    private fun inventory(): LaunchableInventoryLoader = LaunchableInventoryLoader {
        LaunchableInventorySnapshot(
            entries = listOf(
                LaunchableEntry(
                    identity = LaunchableIdentity(
                        profileSerialNumber = 0,
                        componentName = ComponentName(
                            "com.example.application",
                            "MainActivity",
                        ),
                    ),
                    user = Process.myUserHandle(),
                    label = "Example application",
                    icon = ColorDrawable(Color.TRANSPARENT),
                ),
            ),
            profileReadStatus = mapOf(0L to ProfileInventoryReadStatus.Complete),
        )
    }
}
