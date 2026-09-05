package com.avenor.launcher

import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DrawerBackgroundTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun capabilityChangesOnlyGlassTreatmentAndNeverTheStoredSelection() {
        val settings = DrawerDisplaySettings(backgroundMode = DrawerBackgroundMode.FrostedGlass)
        assertEquals(DrawerBackgroundTreatment.Glass, drawerBackgroundTreatment(settings.backgroundMode, true))
        assertEquals(DrawerBackgroundTreatment.OpaqueFallback, drawerBackgroundTreatment(settings.backgroundMode, false))
        assertEquals(DrawerBackgroundTreatment.Glass, drawerBackgroundTreatment(settings.backgroundMode, true))
        for (available in listOf(false, true)) {
            assertEquals(DrawerBackgroundTreatment.Clear, drawerBackgroundTreatment(DrawerBackgroundMode.Transparent, available))
        }
    }

    @Test
    fun closingBlurSessionRestoresBlurAndPreservesUnrelatedWindowChanges() {
        composeRule.runOnIdle {
            val window = composeRule.activity.window
            val originalFlags = window.attributes.flags
            val originalRadius = window.attributes.blurBehindRadius
            try {
                val session = DrawerWindowBlur(window)
                assertTrue(session.apply(32))
                assertEquals(32, window.attributes.blurBehindRadius)
                // Simulate an unrelated owner changing a flag while Drawer owns blur.
                val unrelatedFlag = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                if (originalFlags and unrelatedFlag == 0) window.addFlags(unrelatedFlag)
                else window.clearFlags(unrelatedFlag)
                session.apply(0)
                assertEquals(0, window.attributes.flags and WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
                session.close()
                assertEquals(originalRadius, window.attributes.blurBehindRadius)
                assertEquals(originalFlags xor unrelatedFlag, window.attributes.flags)
            } finally {
                val attributes = window.attributes
                attributes.flags = originalFlags
                attributes.setBlurBehindRadius(originalRadius)
                window.attributes = attributes
            }
        }
    }

    @Test
    fun shadowPreservesIconSizeAndSingleDescriptionAndDoesNotEnterPanel() {
        composeRule.setContent {
            AvenorTheme {
                DrawerBackgroundSurface(DrawerBackgroundMode.Transparent, active = false) {
                    assertNotNull(MaterialTheme.typography.bodyLarge.shadow)
                    DrawerIcon(painterResource(R.drawable.ic_settings), "Settings icon", Modifier.size(40.dp))
                    DrawerPanelAppearance {
                        assertNull(MaterialTheme.typography.bodyLarge.shadow)
                    }
                }
            }
        }
        composeRule.onAllNodesWithContentDescription("Settings icon").assertCountEquals(1)
        composeRule.onNodeWithContentDescription("Settings icon")
            .assertWidthIsEqualTo(40.dp).assertHeightIsEqualTo(40.dp)
    }

    @Test
    fun backgroundSelectionSubmitsCompleteState() {
        val initial = DrawerDisplaySettings(
            applicationSize = DrawerApplicationSize.Small,
            namePlacement = DrawerNamePlacement.Below,
            itemsPerRow = 4,
            sectionAnchorPresentation = DrawerSectionAnchorPresentation.LeftSide,
        )
        var submitted: DrawerDisplaySettings? = null
        composeRule.setContent {
            AvenorTheme {
                DrawerDisplaySettingsPanel(initial, true, { submitted = it }, {})
            }
        }
        composeRule.onNodeWithTag("drawer_background_0").performClick()
        composeRule.runOnIdle {
            assertEquals(initial.copy(backgroundMode = DrawerBackgroundMode.Transparent), submitted)
        }
    }
}
