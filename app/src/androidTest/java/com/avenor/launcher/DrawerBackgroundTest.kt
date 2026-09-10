package com.avenor.launcher

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import com.avenor.launcher.ui.drawer.DrawerBackgroundSurface
import com.avenor.launcher.ui.drawer.DrawerIcon
import com.avenor.launcher.ui.drawer.DrawerPanelAppearance

class DrawerBackgroundTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun foregroundShadowIsPresentAtEveryOpacity() {
        for (opacity in listOf(0, 50, 100)) {
            composeRule.setContent {
                AvenorTheme {
                    DrawerBackgroundSurface(opacity = opacity) {
                        assertNotNull(MaterialTheme.typography.bodyLarge.shadow)
                    }
                }
            }
        }
    }

    @Test
    fun opaquePanelsRetainTheirOwnContrastTreatment() {
        composeRule.setContent {
            AvenorTheme {
                DrawerBackgroundSurface(opacity = 100) {
                    DrawerPanelAppearance {
                        assertNull(MaterialTheme.typography.bodyLarge.shadow)
                    }
                }
            }
        }
    }

    @Test
    fun shadowPreservesIconSizeAndSingleDescription() {
        composeRule.setContent {
            AvenorTheme {
                DrawerBackgroundSurface(opacity = 50) {
                    DrawerIcon(
                        painter = painterResource(R.drawable.ic_settings),
                        contentDescription = "Settings icon",
                        modifier = Modifier.size(40.dp),
                    )
                }
            }
        }
        composeRule.onAllNodesWithContentDescription("Settings icon").assertCountEquals(1)
        composeRule.onNodeWithContentDescription("Settings icon")
            .assertWidthIsEqualTo(40.dp).assertHeightIsEqualTo(40.dp)
    }

    @Test
    fun backgroundSurfaceExposesItsTag() {
        composeRule.setContent {
            AvenorTheme {
                DrawerBackgroundSurface(opacity = 50) {
                    Box(Modifier.size(1.dp))
                }
            }
        }
        composeRule.onNodeWithTag("drawer_background_surface").assertExists()
    }
}
