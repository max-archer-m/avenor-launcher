package com.avenor.launcher

import android.annotation.SuppressLint
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.performTouchInput
import androidx.test.espresso.action.ViewActions.swipeRight
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DrawerBackgroundOpacityTest {
    @get:Rule
    val composeRule = createComposeRule()

    private var persisted = DrawerDisplaySettings()
    private val previews = mutableListOf<Int?>()
    private val commits = mutableListOf<DrawerDisplaySettings>()

    private fun setContent() {
        persisted = DrawerDisplaySettings()
        previews.clear()
        commits.clear()
        composeRule.setContent {
            AvenorTheme {
                var settings by remember { mutableStateOf(persisted) }
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

    @SuppressLint("CheckResult")
    @Test
    fun sliderReleaseCommitsExactlyOneSaveWithTheNewOpacity() {
        setContent()
        composeRule.onNodeWithTag("drawer_background_opacity_slider")
            .performTouchInput { swipeRight() }
        composeRule.waitForIdle()
        assertEquals(1, commits.size)
        assertEquals(100, commits.single().backgroundOpacity)
        assertEquals(null, previews.last())
    }

    @Test
    fun dragPreviewUpdatesTheBackgroundWithoutCommitting() {
        setContent()
        composeRule.onNodeWithTag("drawer_background_opacity_slider")
            .performTouchInput {
                down(centerLeft)
                moveTo(center)
            }
        composeRule.waitForIdle()
        assertTrue(previews.any { it != null && it != persisted.backgroundOpacity })
        assertEquals(0, commits.size)

        // Removing the panel mid-drag reverts the preview and commits nothing.
        composeRule.setContent { }
        composeRule.waitForIdle()
        assertEquals(null, previews.last())
        assertEquals(0, commits.size)
    }
}
