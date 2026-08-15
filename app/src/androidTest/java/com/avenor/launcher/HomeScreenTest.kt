package com.avenor.launcher

import android.content.ComponentName
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Process
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.swipeUp
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    fun unavailableFavoriteRemainsVisibleInStoredPosition() {
        val identity = LaunchableIdentity(
            profileSerialNumber = 42,
            componentName = ComponentName("com.example.unavailable", "MainActivity"),
        )
        composeRule.setContent {
            AvenorTheme {
                HomeScreen(
                    favoriteState = FavoriteReadState.Readable(listOf(identity)),
                    favoriteAvailability = mapOf(
                        identity to FavoriteAvailability.Unknown(null),
                    ),
                )
            }
        }

        composeRule.onNodeWithTag("home_favorite_row").assertIsDisplayed()
        composeRule.onNodeWithText("Application unavailable").assertIsDisplayed()
    }

    @Test
    fun selectingAvailableFavoriteRequestsItsExactEntry() {
        val entry = LaunchableEntry(
            identity = LaunchableIdentity(
                profileSerialNumber = 42,
                componentName = ComponentName("com.example.favorite", "MainActivity"),
            ),
            user = Process.myUserHandle(),
            label = "Favorite application",
            icon = ColorDrawable(Color.TRANSPARENT),
        )
        var selectedAvailability: FavoriteAvailability? = null
        composeRule.setContent {
            AvenorTheme {
                HomeScreen(
                    favoriteState = FavoriteReadState.Readable(listOf(entry.identity)),
                    favoriteAvailability = mapOf(
                        entry.identity to FavoriteAvailability.Available(entry),
                    ),
                    onLaunchFavorite = { selectedAvailability = it },
                )
            }
        }

        composeRule.onNodeWithText("Favorite application").performClick()

        composeRule.runOnIdle {
            assertEquals(FavoriteAvailability.Available(entry), selectedAvailability)
        }
    }

    @Test
    fun failedHomeLaunchDoesNotDeleteFavorite() {
        val entry = LaunchableEntry(
            identity = LaunchableIdentity(
                profileSerialNumber = 42,
                componentName = ComponentName("com.example.failure", "MainActivity"),
            ),
            user = Process.myUserHandle(),
            label = "Launch failure favorite",
            icon = ColorDrawable(Color.TRANSPARENT),
        )
        val favoriteStore = TestFavoriteStore(listOf(entry.identity))
        composeRule.setContent {
            AvenorTheme {
                AvenorApp(
                    inventoryLoader = LaunchableInventoryLoader { completeSnapshot(entry) },
                    entryLauncher = LaunchableEntryLauncher { false },
                    favoriteStore = favoriteStore,
                )
            }
        }

        composeRule.onNodeWithText("Launch failure favorite").performClick()

        composeRule.runOnIdle {
            assertEquals(listOf(entry.identity), favoriteStore.readableIdentities())
        }
    }

    @Test
    fun disabledFavoriteKeepsPresentationAndManagementEntry() {
        val entry = LaunchableEntry(
            identity = LaunchableIdentity(
                profileSerialNumber = 42,
                componentName = ComponentName("com.example.disabled", "MainActivity"),
            ),
            user = Process.myUserHandle(),
            label = "Disabled application",
            icon = ColorDrawable(Color.TRANSPARENT),
        )
        var selectedAvailability: FavoriteAvailability? = null
        var managedEntry: LaunchableEntry? = null
        composeRule.setContent {
            AvenorTheme {
                HomeScreen(
                    favoriteState = FavoriteReadState.Readable(listOf(entry.identity)),
                    favoriteAvailability = mapOf(
                        entry.identity to FavoriteAvailability.Disabled(entry),
                    ),
                    onLaunchFavorite = { selectedAvailability = it },
                    onLongPressFavorite = { managedEntry = it },
                )
            }
        }

        composeRule.onNodeWithText("Disabled application — Disabled").performClick()
        composeRule.onNodeWithText("Disabled application — Disabled").performTouchInput {
            longClick()
        }

        composeRule.runOnIdle {
            assertEquals(FavoriteAvailability.Disabled(entry), selectedAvailability)
            assertEquals(entry, managedEntry)
        }
    }

    @Test
    fun incompleteProfileAndUnknownIdentityAreNeverConfirmedRemoved() = runBlocking {
        val identity = LaunchableIdentity(7, ComponentName("com.example", "MainActivity"))
        val loader = object : LaunchableInventoryLoader, LaunchableIdentityStatusResolver {
            override suspend fun load() = LaunchableInventorySnapshot(
                entries = emptyList(),
                profileReadStatus = mapOf(7L to ProfileInventoryReadStatus.Unavailable),
            )

            override suspend fun resolveMissingIdentity(
                identity: LaunchableIdentity,
                snapshot: LaunchableInventorySnapshot,
                lastKnownEntry: LaunchableEntry?,
            ) = FavoriteAvailability.TemporarilyUnavailable(lastKnownEntry)
        }
        val snapshot = loader.load()

        val result = LaunchableInventoryCoordinator(loader).resolveFavorites(listOf(identity), snapshot)

        assertEquals(
            FavoriteAvailability.TemporarilyUnavailable(null),
            result.getValue(identity),
        )
    }

    @Test
    fun reconciliationRemovesOnlyConfirmedExactIdentity() = runBlocking {
        val removed = LaunchableIdentity(7, ComponentName("com.example", "RemovedActivity"))
        val retained = LaunchableIdentity(7, ComponentName("com.example", "OtherActivity"))
        val clone = LaunchableIdentity(8, ComponentName("com.example", "RemovedActivity"))
        val loader = object : LaunchableInventoryLoader, LaunchableIdentityStatusResolver {
            override suspend fun load() = LaunchableInventorySnapshot(
                entries = emptyList(),
                profileReadStatus = mapOf(
                    7L to ProfileInventoryReadStatus.Complete,
                    8L to ProfileInventoryReadStatus.Unavailable,
                ),
            )

            override suspend fun resolveMissingIdentity(
                identity: LaunchableIdentity,
                snapshot: LaunchableInventorySnapshot,
                lastKnownEntry: LaunchableEntry?,
            ): FavoriteAvailability = when (identity) {
                removed -> FavoriteAvailability.ConfirmedRemoved
                else -> FavoriteAvailability.Unknown(lastKnownEntry)
            }
        }
        val snapshot = loader.load()

        val result = LaunchableInventoryCoordinator(loader)
            .resolveFavorites(listOf(removed, retained, clone), snapshot)

        assertEquals(FavoriteAvailability.ConfirmedRemoved, result.getValue(removed))
        assertEquals(FavoriteAvailability.Unknown(null), result.getValue(retained))
        assertEquals(FavoriteAvailability.Unknown(null), result.getValue(clone))
    }

    @Test
    fun actionSheetOffersAddForNonFavoriteEntry() {
        val entry = LaunchableEntry(
            identity = LaunchableIdentity(
                profileSerialNumber = 0,
                componentName = ComponentName("com.example.add", "MainActivity"),
            ),
            user = Process.myUserHandle(),
            label = "Add candidate",
            icon = ColorDrawable(Color.TRANSPARENT),
        )
        var addRequested = false
        composeRule.setContent {
            AvenorTheme {
                ApplicationActionSheet(
                    entry = entry,
                    favoriteState = FavoriteReadState.Readable(emptyList()),
                    onDismiss = {},
                    onAddFavorite = { addRequested = true },
                    onRemoveFavorite = {},
                    informationLauncher = ApplicationInformationLauncher { true },
                )
            }
        }

        composeRule.onNodeWithText("Add favorite").performClick()

        composeRule.runOnIdle { assertEquals(true, addRequested) }
    }

    @Test
    fun actionSheetDoesNotOfferDuplicateAddForFavoriteEntry() {
        val entry = LaunchableEntry(
            identity = LaunchableIdentity(
                profileSerialNumber = 0,
                componentName = ComponentName("com.example.existing", "MainActivity"),
            ),
            user = Process.myUserHandle(),
            label = "Existing favorite",
            icon = ColorDrawable(Color.TRANSPARENT),
        )
        var removeRequested = false
        composeRule.setContent {
            AvenorTheme {
                ApplicationActionSheet(
                    entry = entry,
                    favoriteState = FavoriteReadState.Readable(listOf(entry.identity)),
                    onDismiss = {},
                    onAddFavorite = {},
                    onRemoveFavorite = { removeRequested = true },
                    informationLauncher = ApplicationInformationLauncher { true },
                )
            }
        }

        composeRule.onNodeWithText("Remove favorite").assertIsDisplayed()
        composeRule.onNodeWithText("Add favorite").assertDoesNotExist()
        composeRule.onNodeWithText("Remove favorite").performClick()
        composeRule.runOnIdle { assertEquals(true, removeRequested) }
    }

    @Test
    fun actionSheetDisablesFavoriteMutationWhenPersistenceIsUnavailable() {
        val entry = LaunchableEntry(
            identity = LaunchableIdentity(
                profileSerialNumber = 0,
                componentName = ComponentName("com.example.failure", "MainActivity"),
            ),
            user = Process.myUserHandle(),
            label = "Unavailable favorites",
            icon = ColorDrawable(Color.TRANSPARENT),
        )
        composeRule.setContent {
            AvenorTheme {
                ApplicationActionSheet(
                    entry = entry,
                    favoriteState = FavoriteReadState.ReadFailure,
                    onDismiss = {},
                    onAddFavorite = {},
                    onRemoveFavorite = {},
                    informationLauncher = ApplicationInformationLauncher { true },
                )
            }
        }

        composeRule.onNodeWithTag("favorite_action").assertIsNotEnabled()
        composeRule.onNodeWithText("Favorites unavailable").assertIsDisplayed()
    }

    @Test
    fun actionSheetInformationActionUsesExactEntryAndDismisses() {
        val entry = LaunchableEntry(
            identity = LaunchableIdentity(
                profileSerialNumber = 7,
                componentName = ComponentName("com.example.details", "ExactActivity"),
            ),
            user = Process.myUserHandle(),
            label = "Application details",
            icon = ColorDrawable(Color.TRANSPARENT),
            profileBadge = ColorDrawable(Color.WHITE),
        )
        var openedEntry: LaunchableEntry? = null
        var dismissed = false
        composeRule.setContent {
            AvenorTheme {
                ApplicationActionSheet(
                    entry = entry,
                    favoriteState = FavoriteReadState.Readable(emptyList()),
                    onDismiss = { dismissed = true },
                    onAddFavorite = {},
                    onRemoveFavorite = {},
                    informationLauncher = ApplicationInformationLauncher {
                        openedEntry = it
                        true
                    },
                )
            }
        }

        composeRule.onNodeWithTag("application_action_sheet_profile_badge").assertIsDisplayed()
        composeRule.onNodeWithTag("application_information_action").performClick()

        composeRule.runOnIdle {
            assertEquals(entry, openedEntry)
            assertEquals(true, dismissed)
        }
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
    fun firstDrawerEntryDoesNotRepeatTheApplicationOwnedInitialLoad() {
        var loadCount = 0
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
                AvenorApp(
                    inventoryLoader = LaunchableInventoryLoader {
                        loadCount += 1
                        completeSnapshot(entry)
                    },
                )
            }
        }

        composeRule.waitUntil { loadCount == 1 }
        composeRule.onNodeWithTag("home_surface").performTouchInput { swipeUp() }
        composeRule.onNodeWithText("Example application").assertIsDisplayed()
        composeRule.runOnIdle { assertEquals(1, loadCount) }
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
                    inventoryLoader = LaunchableInventoryLoader { completeSnapshot(entry) },
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
                    inventoryLoader = LaunchableInventoryLoader { completeSnapshot(entry) },
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
                        completeSnapshot(entry)
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
        var inventoryChanged: (LaunchableInventoryChange) -> Unit = {}
        val inventory = object : LaunchableInventoryLoader, LaunchableInventoryMonitor {
            override suspend fun load(): LaunchableInventorySnapshot =
                completeSnapshot(*entries.toTypedArray())

            override fun observe(
                onInventoryChanged: (LaunchableInventoryChange) -> Unit,
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
            inventoryChanged(LaunchableInventoryChange.PackageChanged)
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
    fun drawerUsesSectionsPreparedByTheBackgroundInventorySnapshot() {
        val entry = LaunchableEntry(
            identity = LaunchableIdentity(
                profileSerialNumber = 0,
                componentName = ComponentName("com.example", "com.example.MainActivity"),
            ),
            user = Process.myUserHandle(),
            label = "Example application",
            icon = ColorDrawable(Color.TRANSPARENT),
        )
        val preparedSections = listOf(DrawerSection("E", listOf(entry)))
        val snapshot = LaunchableInventorySnapshot(
            entries = listOf(entry),
            profileReadStatus = mapOf(0L to ProfileInventoryReadStatus.Complete),
            drawerSections = preparedSections,
            drawerSectionsLocale = Locale.US,
        )

        assertEquals(preparedSections, snapshot.drawerSectionsFor(Locale.US))
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

    @Test
    fun sharedMarqueePriorityAndPauseAreDeterministic() {
        val overflowing = setOf("pressed", "focused", "centered")

        assertEquals(
            "pressed",
            selectActiveMarqueeKey(false, false, "pressed", "focused", "centered", overflowing),
        )
        assertEquals(
            "focused",
            selectActiveMarqueeKey(false, false, null, "focused", "centered", overflowing),
        )
        assertEquals(
            "centered",
            selectActiveMarqueeKey(false, false, null, null, "centered", overflowing),
        )
        assertEquals(
            null,
            selectActiveMarqueeKey(true, false, "pressed", null, null, overflowing),
        )
        assertEquals(
            null,
            selectActiveMarqueeKey(false, true, "pressed", null, null, overflowing),
        )
    }

    @Test
    fun homeFavoriteActionSheetOffersEditForTwoOrMoreFavorites() {
        val entry = LaunchableEntry(
            identity = LaunchableIdentity(
                profileSerialNumber = 0,
                componentName = ComponentName("com.example.first", "MainActivity"),
            ),
            user = Process.myUserHandle(),
            label = "First favorite",
            icon = ColorDrawable(Color.TRANSPARENT),
        )
        val second = LaunchableIdentity(
            profileSerialNumber = 0,
            componentName = ComponentName("com.example.second", "MainActivity"),
        )
        var editRequested = false
        composeRule.setContent {
            AvenorTheme {
                ApplicationActionSheet(
                    entry = entry,
                    favoriteState = FavoriteReadState.Readable(listOf(entry.identity, second)),
                    onDismiss = {},
                    onAddFavorite = {},
                    onRemoveFavorite = {},
                    onEditFavorites = { editRequested = true },
                    canEditFavorites = true,
                    informationLauncher = ApplicationInformationLauncher { true },
                )
            }
        }

        composeRule.onNodeWithTag("edit_favorites_action").performClick()
        composeRule.runOnIdle { assertEquals(true, editRequested) }
    }

    @Test
    fun actionSheetShowsAndInvokesExactApplicationShortcut() {
        val entry = LaunchableEntry(
            identity = LaunchableIdentity(
                profileSerialNumber = 7,
                componentName = ComponentName("com.example.shortcuts", "MainActivity"),
            ),
            user = Process.myUserHandle(),
            label = "Shortcut application",
            icon = ColorDrawable(Color.TRANSPARENT),
        )
        val shortcut = ApplicationShortcut(
            packageName = "com.example.shortcuts",
            shortcutId = "exact-shortcut",
            label = "Exact shortcut",
            icon = null,
            user = entry.user,
            rank = 2,
        )
        var invoked: ApplicationShortcut? = null
        composeRule.setContent {
            AvenorTheme {
                ApplicationActionSheet(
                    entry = entry,
                    favoriteState = FavoriteReadState.Readable(emptyList()),
                    onDismiss = {},
                    onAddFavorite = {},
                    onRemoveFavorite = {},
                    shortcuts = listOf(shortcut),
                    onShortcut = { invoked = it },
                    informationLauncher = ApplicationInformationLauncher { true },
                )
            }
        }

        composeRule.onNodeWithTag("application_shortcut_region").assertIsDisplayed()
        composeRule.onNodeWithText("Exact shortcut").performClick()
        composeRule.runOnIdle { assertEquals(shortcut, invoked) }
    }

    @Test
    fun actionSheetOmitsShortcutRegionWhenNoneAreAvailable() {
        val entry = LaunchableEntry(
            identity = LaunchableIdentity(0, ComponentName("com.example.none", "MainActivity")),
            user = Process.myUserHandle(),
            label = "No shortcuts",
            icon = ColorDrawable(Color.TRANSPARENT),
        )
        composeRule.setContent {
            AvenorTheme {
                ApplicationActionSheet(
                    entry = entry,
                    favoriteState = FavoriteReadState.Readable(emptyList()),
                    onDismiss = {},
                    onAddFavorite = {},
                    onRemoveFavorite = {},
                    informationLauncher = ApplicationInformationLauncher { true },
                )
            }
        }

        composeRule.onNodeWithTag("application_shortcut_region").assertDoesNotExist()
    }

    @Test
    fun actionSheetScrollsApplicationShortcutsWithinAvailableHeight() {
        val entry = LaunchableEntry(
            identity = LaunchableIdentity(
                0,
                ComponentName("com.example.many.shortcuts", "MainActivity"),
            ),
            user = Process.myUserHandle(),
            label = "Many shortcuts",
            icon = ColorDrawable(Color.TRANSPARENT),
        )
        val shortcuts = List(20) { index ->
            ApplicationShortcut(
                packageName = entry.identity.componentName.packageName,
                shortcutId = "shortcut-$index",
                label = "Shortcut $index",
                icon = null,
                user = entry.user,
                rank = index,
            )
        }
        composeRule.setContent {
            AvenorTheme {
                ApplicationActionSheet(
                    entry = entry,
                    favoriteState = FavoriteReadState.Readable(emptyList()),
                    onDismiss = {},
                    onAddFavorite = {},
                    onRemoveFavorite = {},
                    informationLauncher = ApplicationInformationLauncher { true },
                    shortcuts = shortcuts,
                )
            }
        }

        composeRule.onNodeWithText("Shortcut 19").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun homeActionSheetLoadsAndLaunchesShortcutForExactEntry() {
        val entry = LaunchableEntry(
            identity = LaunchableIdentity(
                profileSerialNumber = 11,
                componentName = ComponentName("com.example.source", "ExactActivity"),
            ),
            user = Process.myUserHandle(),
            label = "Shortcut source",
            icon = ColorDrawable(Color.TRANSPARENT),
        )
        val shortcut = ApplicationShortcut(
            packageName = entry.identity.componentName.packageName,
            shortcutId = "open-exact",
            label = "Open exact shortcut",
            icon = null,
            user = entry.user,
            rank = 0,
        )
        var loadedEntry: LaunchableEntry? = null
        var launchedShortcut: ApplicationShortcut? = null
        val controller = object : ApplicationShortcutController {
            override suspend fun load(entry: LaunchableEntry): List<ApplicationShortcut> {
                loadedEntry = entry
                return listOf(shortcut)
            }

            override fun launch(shortcut: ApplicationShortcut): Boolean {
                launchedShortcut = shortcut
                return true
            }
        }
        composeRule.setContent {
            AvenorTheme {
                AvenorApp(
                    inventoryLoader = LaunchableInventoryLoader { completeSnapshot(entry) },
                    favoriteStore = TestFavoriteStore(listOf(entry.identity)),
                    shortcutController = controller,
                )
            }
        }

        composeRule.onNodeWithText("Shortcut source").performTouchInput { longClick() }
        composeRule.onNodeWithText("Open exact shortcut").assertIsDisplayed().performClick()

        composeRule.runOnIdle {
            assertEquals(entry, loadedEntry)
            assertEquals(shortcut, launchedShortcut)
        }
        composeRule.onNodeWithTag("application_action_sheet").assertDoesNotExist()
    }
}

private fun completeSnapshot(vararg entries: LaunchableEntry): LaunchableInventorySnapshot =
    LaunchableInventorySnapshot(
        entries = entries.toList(),
        profileReadStatus = entries
            .map { it.identity.profileSerialNumber }
            .distinct()
            .associateWith { ProfileInventoryReadStatus.Complete },
    )

private class TestFavoriteStore(initial: List<LaunchableIdentity>) : FavoriteStore {
    private val mutableState = MutableStateFlow<FavoriteReadState>(
        FavoriteReadState.Readable(initial),
    )
    override val state: StateFlow<FavoriteReadState> = mutableState
    override suspend fun load() = Unit
    override suspend fun add(identity: LaunchableIdentity): Boolean {
        val readable = mutableState.value as FavoriteReadState.Readable
        if (identity !in readable.identities) {
            mutableState.value = FavoriteReadState.Readable(readable.identities + identity)
        }
        return true
    }
    override suspend fun remove(identity: LaunchableIdentity): Boolean =
        removeAll(setOf(identity))
    override suspend fun removeAll(identities: Set<LaunchableIdentity>): Boolean {
        val readable = mutableState.value as FavoriteReadState.Readable
        mutableState.value = FavoriteReadState.Readable(
            readable.identities.filterNot(identities::contains),
        )
        return true
    }

    override suspend fun replaceOrder(identities: List<LaunchableIdentity>): Boolean {
        val readable = mutableState.value as? FavoriteReadState.Readable ?: return false
        if (!isValidReplacement(readable.identities, identities)) return false
        mutableState.value = FavoriteReadState.Readable(identities)
        return true
    }

    fun readableIdentities(): List<LaunchableIdentity> =
        (state.value as FavoriteReadState.Readable).identities
}
