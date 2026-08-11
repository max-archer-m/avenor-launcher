package com.avenor.launcher

import android.content.ComponentName
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Process
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.swipeUp
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.awaitCancellation
import java.util.Locale
import org.junit.Assert.assertEquals
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

    @Test
    fun upwardSwipeOpensDrawerLoadingSurface() {
        composeRule.setContent {
            AvenorTheme {
                AvenorApp(
                    inventoryLoader = LaunchableInventoryLoader { awaitCancellation() },
                )
            }
        }

        composeRule.onNodeWithTag("home_surface").performTouchInput {
            swipeUp()
        }

        composeRule.onNodeWithTag("drawer_loading").assertIsDisplayed()
    }

    @Test
    fun loadedInventoryDisplaysApplicationList() {
        val entry = LaunchableEntry(
            identity = LaunchableIdentity(
                profileSerialNumber = 0,
                componentName = ComponentName("com.example", "com.example.MainActivity"),
            ),
            user = Process.myUserHandle(),
            label = "Example application",
            icon = ColorDrawable(Color.TRANSPARENT),
        )
        composeRule.setContent {
            AvenorTheme {
                DrawerScreen(
                    inventoryLoader = LaunchableInventoryLoader { listOf(entry) },
                )
            }
        }

        composeRule.onNodeWithTag("drawer_application_list").assertIsDisplayed()
        composeRule.onNodeWithText("Example application").assertIsDisplayed()
    }

    @Test
    fun selectingApplicationLaunchesItsExactEntry() {
        val entry = LaunchableEntry(
            identity = LaunchableIdentity(
                profileSerialNumber = 42,
                componentName = ComponentName("com.example", "com.example.ExactActivity"),
            ),
            user = Process.myUserHandle(),
            label = "Exact application",
            icon = ColorDrawable(Color.TRANSPARENT),
        )
        var launchedEntry: LaunchableEntry? = null
        composeRule.setContent {
            AvenorTheme {
                DrawerScreen(
                    inventoryLoader = LaunchableInventoryLoader { listOf(entry) },
                    entryLauncher = LaunchableEntryLauncher {
                        launchedEntry = it
                        true
                    },
                )
            }
        }

        composeRule.onNodeWithText("Exact application").performClick()

        composeRule.runOnIdle { assertEquals(entry, launchedEntry) }
    }

    @Test
    fun retryRecoversFromInventoryFailure() {
        var attempts = 0
        val entry = LaunchableEntry(
            identity = LaunchableIdentity(
                profileSerialNumber = 0,
                componentName = ComponentName("com.example", "com.example.MainActivity"),
            ),
            user = Process.myUserHandle(),
            label = "Recovered application",
            icon = ColorDrawable(Color.TRANSPARENT),
        )
        composeRule.setContent {
            AvenorTheme {
                DrawerScreen(
                    inventoryLoader = LaunchableInventoryLoader {
                        attempts += 1
                        if (attempts == 1) error("Expected test failure")
                        listOf(entry)
                    },
                )
            }
        }

        composeRule.onNodeWithTag("drawer_error_icon").assertIsDisplayed()
        composeRule.onNodeWithText("Retry").performClick()

        composeRule.onNodeWithText("Recovered application").assertIsDisplayed()
    }

    @Test
    fun activeDrawerRefreshesAfterPlatformInventoryChange() {
        fun entry(label: String) = LaunchableEntry(
            identity = LaunchableIdentity(
                profileSerialNumber = 0,
                componentName = ComponentName("com.example.live", "MainActivity"),
            ),
            user = Process.myUserHandle(),
            label = label,
            icon = ColorDrawable(Color.TRANSPARENT),
        )

        var entries = listOf(entry("Before update"))
        var inventoryChanged: () -> Unit = {}
        val inventory = object : LaunchableInventoryLoader, LaunchableInventoryMonitor {
            override suspend fun load(): List<LaunchableEntry> = entries

            override fun observe(
                onInventoryChanged: () -> Unit,
            ): LaunchableInventoryObservation {
                inventoryChanged = onInventoryChanged
                return LaunchableInventoryObservation { inventoryChanged = {} }
            }
        }

        composeRule.setContent {
            AvenorTheme {
                DrawerScreen(inventoryLoader = inventory)
            }
        }
        composeRule.onNodeWithText("Before update").assertIsDisplayed()

        composeRule.runOnIdle {
            entries = listOf(entry("After update"))
            inventoryChanged()
        }

        composeRule.onNodeWithText("After update").assertIsDisplayed()
    }

    @Test
    fun chineseLabelsAreSortedByPinyinAlongsideLatinLabels() {
        val labels = listOf("微信", "Chrome", "百度", "Amazon", "爱奇艺")
        val entries = labels.mapIndexed { index, label ->
            LaunchableEntry(
                identity = LaunchableIdentity(
                    profileSerialNumber = 0,
                    componentName = ComponentName("com.example.$index", "MainActivity"),
                ),
                user = Process.myUserHandle(),
                label = label,
                icon = ColorDrawable(Color.TRANSPARENT),
            )
        }

        val sortedLabels = entries
            .sortedWith(LaunchableEntryComparator(Locale.SIMPLIFIED_CHINESE))
            .map(LaunchableEntry::label)

        assertEquals(listOf("爱奇艺", "Amazon", "百度", "Chrome", "微信"), sortedLabels)
    }

    @Test
    fun drawerSectionsUseTheNormalizedCompleteLabel() {
        val labels = listOf("微信", "2FAS", "Éclair", "百度", "Amazon")
        val entries = labels.mapIndexed { index, label ->
            LaunchableEntry(
                identity = LaunchableIdentity(
                    profileSerialNumber = 0,
                    componentName = ComponentName("com.example.section.$index", "MainActivity"),
                ),
                user = Process.myUserHandle(),
                label = label,
                icon = ColorDrawable(Color.TRANSPARENT),
            )
        }

        val sections = buildDrawerSections(entries, Locale.SIMPLIFIED_CHINESE)

        assertEquals(listOf("#", "A", "B", "E", "W"), sections.map(DrawerSection::label))
        assertEquals(listOf("2FAS"), sections.first().entries.map(LaunchableEntry::label))
    }

    @Test
    fun liveUpdatePreservesAnchorRelativePosition() {
        val sections = listOf(
            DrawerSection(label = "#", entries = emptyList()),
            DrawerSection(label = "A", entries = emptyList()),
            DrawerSection(label = "B", entries = emptyList()),
        )
        val position = captureDrawerListPosition(
            sections = sections,
            firstVisibleItemIndex = 1,
            firstVisibleItemScrollOffset = 12,
        )

        assertEquals(
            DrawerRestorationTarget(itemIndex = 1, scrollOffset = 12),
            resolveDrawerRestorationTarget(checkNotNull(position), sections),
        )
    }

    @Test
    fun removedAnchorMovesToNextAvailableAnchor() {
        val position = DrawerListPosition(
            sectionLabel = "B",
            relativeItemIndex = 0,
            scrollOffset = 20,
        )
        val updatedSections = listOf(
            DrawerSection(label = "#", entries = emptyList()),
            DrawerSection(label = "C", entries = emptyList()),
        )

        assertEquals(
            DrawerRestorationTarget(itemIndex = 1, scrollOffset = 0),
            resolveDrawerRestorationTarget(position, updatedSections),
        )
    }

    @Test
    fun legacyIconBackgroundUsesItsDominantEdgeColor() {
        val icon = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888).apply {
            eraseColor(Color.BLUE)
        }

        assertEquals(Color.rgb(8, 8, 248), icon.inferLegacyBackgroundColor())
    }

    @Test
    fun transparentLegacyIconUsesSafeFallbackBackground() {
        val icon = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888).apply {
            eraseColor(Color.TRANSPARENT)
            for (y in 20 until 80) {
                for (x in 20 until 80) setPixel(x, y, Color.BLUE)
            }
        }

        assertEquals(Color.WHITE, icon.inferLegacyBackgroundColor())
    }

    @Test
    fun rapidActivationGuardSuppressesImmediateDuplicate() {
        var now = 1_000L
        val guard = RapidActivationGuard(
            minimumIntervalMillis = 600L,
            elapsedRealtime = { now },
        )

        assertEquals(true, guard.tryAcquire())
        assertEquals(false, guard.tryAcquire())
        now += 600L
        assertEquals(true, guard.tryAcquire())
    }
}
