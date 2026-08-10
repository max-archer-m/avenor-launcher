package com.avenor.launcher

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun homeDisplaysCurrentInformationRegion() {
        composeRule.setContent {
            AvenorTheme {
                HomeScreen()
            }
        }

        composeRule.onNodeWithTag("home_time").assertIsDisplayed()
        composeRule.onNodeWithTag("home_date").assertIsDisplayed()
    }
}
