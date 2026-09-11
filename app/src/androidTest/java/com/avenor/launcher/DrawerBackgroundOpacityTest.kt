package com.avenor.launcher

import android.annotation.SuppressLint
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import com.avenor.launcher.ui.drawer.DrawerDisplaySettings
import com.avenor.launcher.ui.drawer.DrawerDisplaySettingsPanel

class DrawerBackgroundOpacityTest {
    @get:Rule
    val composeRule = createComposeRule()

    private var persisted = DrawerDisplaySettings()
    private val previews = mutableListOf<Int?>()
    private val commits = mutableListOf<DrawerDisplaySettings>()
    private var panelVisible by mutableStateOf(true)

    /**
     * The panel's height animation moves the slider while gestures inject; settle the
     * animation clock deterministically before interacting with panel content.
     */
    private fun settlePanelAnimation() {
        composeRule.mainClock.autoAdvance = false
        composeRule.mainClock.advanceTimeBy(milliseconds = 500)
        composeRule.mainClock.autoAdvance = true
        composeRule.waitForIdle()
    }

    private fun setContent() {
        persisted = DrawerDisplaySettings()
        previews.clear()
        commits.clear()
        panelVisible = true
        composeRule.setContent {
            AvenorTheme {
                var settings by remember { mutableStateOf(persisted) }
                if (panelVisible) {
                    DrawerDisplaySettingsPanel(
                        settings = settings,
                        enabled = true,
                        onChangeSettings = { candidate ->
                            commits.add(candidate)
                            settings = candidate
                        },
                        onPreviewOpacity = { preview -> previews.add(preview) },
                        onDismiss = {},
                    )
                }
            }
        }
        settlePanelAnimation()
    }

    @SuppressLint("CheckResult")
    @Test
    fun sliderReleaseCommitsExactlyOneSaveWithTheNewOpacity() {
        setContent()
        val slider = composeRule.onNodeWithTag("drawer_background_opacity_slider")
        // Bisect the gesture failure first: the slider must be visible and enabled,
        // otherwise a swipe cannot commit.
        slider.assertIsDisplayed()
        slider.assertIsEnabled()
        // TEMPORARY PROBE (remove before the final commit): dump the slider geometry so
        // the injected-gesture coordinate space can be compared with the layout space.
        composeRule.runOnIdle {
            val node = slider.fetchSemanticsNode()
            val panel = composeRule.onNodeWithTag(
                "drawer_display_settings_panel",
            ).fetchSemanticsNode()
            android.util.Log.e(
                "OpacityProbe",
                "slider boundsInRoot=${node.boundsInRoot} " +
                    "boundsInWindow=${node.boundsInWindow} " +
                    "panel boundsInRoot=${panel.boundsInRoot}",
            )
        }
        slider.performTouchInput { swipeRight() }
        composeRule.waitForIdle()
        composeRule.runOnIdle {
            android.util.Log.e(
                "OpacityProbe",
                "after swipe previews=$previews commits=${commits.size}",
            )
        }
        assertEquals(1, commits.size)
        assertEquals(100, commits.single().backgroundOpacity)
        assertEquals(null, previews.last())
    }

    @Test
    fun dragPreviewUpdatesTheBackgroundWithoutCommitting() {
        setContent()
        val slider = composeRule.onNodeWithTag("drawer_background_opacity_slider")
        slider.assertIsDisplayed()
        slider.assertIsEnabled()
        slider.performTouchInput {
            down(centerLeft)
            moveTo(centerRight)
        }
        composeRule.waitForIdle()
        assertTrue(previews.any { it != null && it != persisted.backgroundOpacity })
        assertEquals(0, commits.size)

        // Removing the panel mid-drag reverts the preview and commits nothing.
        panelVisible = false
        composeRule.waitForIdle()
        assertEquals(null, previews.last())
        assertEquals(0, commits.size)
    }
}
