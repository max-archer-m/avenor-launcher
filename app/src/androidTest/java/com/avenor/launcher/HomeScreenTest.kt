package com.avenor.launcher

import android.content.ComponentName
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Process
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.doubleClick
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.swipeDown
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
    fun oneVerticalListUsesOneFullWidthComposition() {
        val identity = LaunchableIdentity(
            profileSerialNumber = 42,
            componentName = ComponentName("com.example.single", "MainActivity"),
        )
        composeRule.setContent {
            AvenorTheme {
                HomeScreen(
                    favoriteState = FavoriteReadState.Readable(
                        FavoriteAggregate(
                            verticalLists = listOf(
                                FavoriteContainer(
                                    id = "vertical-list-custom-1",
                                    type = FavoriteContainerType.VerticalList,
                                    identities = listOf(identity),
                                ),
                            ),
                        ),
                    ),
                )
            }
        }

        composeRule.onNodeWithTag("home_favorite_list_0").assertIsDisplayed()
        composeRule.onAllNodesWithTag("home_favorite_list_1").assertCountEquals(0)
    }

    @Test
    fun twoVerticalListsUseTwoIndependentCompositionSlots() {
        val first = LaunchableIdentity(
            profileSerialNumber = 42,
            componentName = ComponentName("com.example.first", "MainActivity"),
        )
        val second = LaunchableIdentity(
            profileSerialNumber = 43,
            componentName = ComponentName("com.example.second", "MainActivity"),
        )
        composeRule.setContent {
            AvenorTheme {
                HomeScreen(
                    favoriteState = FavoriteReadState.Readable(
                        FavoriteAggregate(
                            verticalLists = listOf(
                                FavoriteContainer(
                                    id = "vertical-list-custom-1",
                                    type = FavoriteContainerType.VerticalList,
                                    identities = listOf(first),
                                    listSize = FavoriteListSize.Large,
                                ),
                                FavoriteContainer(
                                    id = "vertical-list-custom-2",
                                    type = FavoriteContainerType.VerticalList,
                                    identities = listOf(second),
                                    listSize = FavoriteListSize.Small,
                                ),
                            ),
                        ),
                    ),
                )
            }
        }

        composeRule.onNodeWithTag("home_favorite_list_0").assertIsDisplayed()
        composeRule.onNodeWithTag("home_favorite_list_1").assertIsDisplayed()
    }

    @Test
    fun favoriteBarDisplaysAndLaunchesItsExactEntry() {
        val entry = LaunchableEntry(
            identity = LaunchableIdentity(
                profileSerialNumber = 42,
                componentName = ComponentName("com.example.bar", "MainActivity"),
            ),
            user = Process.myUserHandle(),
            label = "Favorite bar application",
            icon = ColorDrawable(Color.TRANSPARENT),
        )
        var selectedAvailability: FavoriteAvailability? = null
        composeRule.setContent {
            AvenorTheme {
                HomeScreen(
                    favoriteState = FavoriteReadState.Readable(
                        FavoriteAggregate(
                            favoriteBars = listOf(
                                FavoriteContainer(
                                    id = "favorite-bar-1",
                                    type = FavoriteContainerType.FavoriteBar,
                                    identities = listOf(entry.identity),
                                ),
                            ),
                        ),
                    ),
                    favoriteAvailability = mapOf(
                        entry.identity to FavoriteAvailability.Available(entry),
                    ),
                    onLaunchFavorite = { selectedAvailability = it },
                )
            }
        }

        composeRule.onNodeWithTag("home_favorite_bar_0").assertIsDisplayed()
        composeRule.onNodeWithText("Favorite bar application").performClick()
        composeRule.runOnIdle {
            assertEquals(FavoriteAvailability.Available(entry), selectedAvailability)
        }
        composeRule.onAllNodesWithTag("home_favorites_empty").assertCountEquals(0)
    }

    @Test
    fun editModeExposesExistingAndProvisionalFavoriteBarTargets() {
        val identity = LaunchableIdentity(
            profileSerialNumber = 42,
            componentName = ComponentName("com.example.bar", "MainActivity"),
        )
        var existingTarget: String? = null
        var provisionalRequested = false
        composeRule.setContent {
            AvenorTheme {
                HomeScreen(
                    favoriteState = FavoriteReadState.Readable(
                        FavoriteAggregate(
                            favoriteBars = listOf(
                                FavoriteContainer(
                                    id = "favorite-bar-1",
                                    type = FavoriteContainerType.FavoriteBar,
                                    identities = listOf(identity),
                                ),
                            ),
                        ),
                    ),
                    editMode = true,
                    onAddFavoritesToBar = { existingTarget = it },
                    onAddProvisionalFavoriteBar = { provisionalRequested = true },
                )
            }
        }

        composeRule.onNodeWithTag("favorite_bar_add_0").assertIsDisplayed().performClick()
        composeRule.runOnIdle { assertEquals("favorite-bar-1", existingTarget) }
        composeRule.onNodeWithTag("favorite_bar_provisional_add")
            .assertIsDisplayed()
            .performClick()
        composeRule.runOnIdle { assertEquals(true, provisionalRequested) }
    }

    @Test
    fun favoriteBarRevealScrollsOnlyItsTargetIntoView() {
        val entries = List(6) { index ->
            LaunchableEntry(
                identity = LaunchableIdentity(
                    profileSerialNumber = 42,
                    componentName = ComponentName("com.example.bar$index", "MainActivity"),
                ),
                user = Process.myUserHandle(),
                label = "Favorite bar application $index",
                icon = ColorDrawable(Color.TRANSPARENT),
            )
        }
        var revealCompleted = false
        composeRule.setContent {
            AvenorTheme {
                HomeScreen(
                    favoriteState = FavoriteReadState.Readable(
                        FavoriteAggregate(
                            favoriteBars = listOf(
                                FavoriteContainer(
                                    id = "favorite-bar-reveal",
                                    type = FavoriteContainerType.FavoriteBar,
                                    identities = entries.map { it.identity },
                                ),
                            ),
                        ),
                    ),
                    favoriteAvailability = entries.associate { entry ->
                        entry.identity to FavoriteAvailability.Available(entry)
                    },
                    editMode = true,
                    favoriteRevealContainerId = "favorite-bar-reveal",
                    favoriteRevealContainerType = FavoriteContainerType.FavoriteBar,
                    favoriteRevealIdentity = entries.last().identity,
                    onFavoriteRevealComplete = { revealCompleted = true },
                )
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithText("Favorite bar application 5").assertIsDisplayed()
        composeRule.runOnIdle { assertEquals(true, revealCompleted) }
    }

    @Test
    fun removingTheFinalFavoriteBarItemDeletesItsContainerAndOffersUndo() {
        val identity = LaunchableIdentity(
            profileSerialNumber = 42,
            componentName = ComponentName("com.example.removed.bar", "MainActivity"),
        )
        val initial = FavoriteAggregate(
            favoriteBars = listOf(
                FavoriteContainer(
                    id = "favorite-bar-remove",
                    type = FavoriteContainerType.FavoriteBar,
                    identities = listOf(identity),
                ),
            ),
        )
        var committed = initial
        composeRule.setContent {
            AvenorTheme {
                HomeScreen(
                    favoriteState = FavoriteReadState.Readable(initial),
                    editMode = true,
                    onCommitFavoriteComposition = { transform ->
                        transform(committed).also { committed = it }
                    },
                )
            }
        }

        composeRule.onNodeWithTag("remove_favorite_bar_item").performClick()
        composeRule.waitForIdle()
        composeRule.runOnIdle { assertEquals(emptyList<FavoriteContainer>(), committed.favoriteBars) }
        composeRule.onNodeWithText("Removed from favorites").assertIsDisplayed()
        composeRule.onNodeWithText("Undo").performClick()
        composeRule.waitForIdle()
        composeRule.runOnIdle { assertEquals(initial, committed) }
    }

    @Test
    fun editModeKeepsThePersistedListComposition() {
        val identity = LaunchableIdentity(
            profileSerialNumber = 42,
            componentName = ComponentName("com.example.edit", "MainActivity"),
        )
        composeRule.setContent {
            AvenorTheme {
                HomeScreen(
                    favoriteState = FavoriteReadState.Readable(
                        FavoriteAggregate(
                            verticalLists = listOf(
                                FavoriteContainer(
                                    id = "vertical-list-edit-custom",
                                    type = FavoriteContainerType.VerticalList,
                                    identities = listOf(identity),
                                    listSize = FavoriteListSize.Large,
                                ),
                            ),
                        ),
                    ),
                    editMode = true,
                )
            }
        }

        composeRule.onNodeWithTag("home_favorites").assertIsDisplayed()
        composeRule.onAllNodesWithTag("home_companion_favorites").assertCountEquals(0)
    }

    @Test
    fun failedEditMutationRetainsNewerReliableInventoryState() {
        val removedEntry = LaunchableEntry(
            identity = LaunchableIdentity(
                profileSerialNumber = 42,
                componentName = ComponentName("com.example.removed", "MainActivity"),
            ),
            user = Process.myUserHandle(),
            label = "Removed during failed save",
            icon = ColorDrawable(Color.TRANSPARENT),
        )
        val refreshedEntry = LaunchableEntry(
            identity = LaunchableIdentity(
                profileSerialNumber = 42,
                componentName = ComponentName("com.example.refreshed", "MainActivity"),
            ),
            user = Process.myUserHandle(),
            label = "Reliable refreshed application",
            icon = ColorDrawable(Color.TRANSPARENT),
        )
        val initial = FavoriteAggregate(
            verticalLists = listOf(
                FavoriteContainer(
                    id = "inventory-interruption-list",
                    type = FavoriteContainerType.VerticalList,
                    identities = listOf(removedEntry.identity),
                ),
            ),
        )
        val refreshed = initial.copy(
            verticalLists = listOf(
                initial.verticalLists.single().copy(
                    identities = listOf(refreshedEntry.identity),
                ),
            ),
        )
        val favoriteState = mutableStateOf<FavoriteReadState>(
            FavoriteReadState.Readable(initial),
        )

        composeRule.setContent {
            AvenorTheme {
                HomeScreen(
                    favoriteState = favoriteState.value,
                    favoriteAvailability = mapOf(
                        removedEntry.identity to FavoriteAvailability.Available(removedEntry),
                        refreshedEntry.identity to FavoriteAvailability.Available(refreshedEntry),
                    ),
                    editMode = true,
                    onCommitFavoriteComposition = {
                        favoriteState.value = FavoriteReadState.Readable(refreshed)
                        null
                    },
                )
            }
        }

        composeRule.onNodeWithTag("remove_favorite_item").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Reliable refreshed application").assertIsDisplayed()
        composeRule.onAllNodesWithText("Removed during failed save").assertCountEquals(0)
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
        composeRule.onAllNodesWithText("Add favorite").assertCountEquals(0)
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
    fun upwardSwipeOverDateOpensDrawerWithoutActivatingDate() {
        composeRule.setContent {
            AvenorTheme {
                AvenorApp(
                    inventoryLoader = LaunchableInventoryLoader { awaitCancellation() },
                )
            }
        }

        composeRule.onNodeWithTag("home_date").performTouchInput {
            swipeUp(startY = centerY, endY = centerY - HOME_DRAWER_SWIPE_DISTANCE_PX)
        }

        composeRule.onNodeWithTag("drawer_loading").assertIsDisplayed()
    }

    @Test
    fun editModeKeepsHomeDrawerGestureDisabled() {
        val entry = LaunchableEntry(
            identity = LaunchableIdentity(
                profileSerialNumber = 0,
                componentName = ComponentName("com.example.edit", "MainActivity"),
            ),
            user = Process.myUserHandle(),
            label = "Editable favorite",
            icon = ColorDrawable(Color.TRANSPARENT),
        )
        val companion = LaunchableEntry(
            identity = LaunchableIdentity(
                profileSerialNumber = 0,
                componentName = ComponentName("com.example.edit.companion", "MainActivity"),
            ),
            user = Process.myUserHandle(),
            label = "Companion favorite",
            icon = ColorDrawable(Color.TRANSPARENT),
        )
        composeRule.setContent {
            AvenorTheme {
                AvenorApp(
                    inventoryLoader = LaunchableInventoryLoader {
                        completeSnapshot(entry, companion)
                    },
                    favoriteStore = TestFavoriteStore(
                        listOf(entry.identity, companion.identity),
                    ),
                )
            }
        }

        composeRule.onNodeWithText("Editable favorite").performTouchInput { longClick() }
        composeRule.onNodeWithTag("edit_favorites_action").performClick()
        composeRule.onNodeWithTag("favorite_reorder_handle").assertIsDisplayed()
        composeRule.onNodeWithTag("home_surface").performTouchInput { swipeUp() }

        composeRule.onAllNodesWithTag("drawer_surface").assertCountEquals(0)
        composeRule.onNodeWithTag("favorite_reorder_handle").assertIsDisplayed()
    }

    @Test
    fun downwardDrawerSwipeDoesNotLaunchApplication() {
        val entry = LaunchableEntry(
            identity = LaunchableIdentity(
                profileSerialNumber = 0,
                componentName = ComponentName("com.example.drawer", "MainActivity"),
            ),
            user = Process.myUserHandle(),
            label = "Drawer swipe target",
            icon = ColorDrawable(Color.TRANSPARENT),
        )
        var launches = 0
        composeRule.setContent {
            AvenorTheme {
                AvenorApp(
                    inventoryLoader = LaunchableInventoryLoader { completeSnapshot(entry) },
                    entryLauncher = LaunchableEntryLauncher {
                        launches += 1
                        true
                    },
                )
            }
        }

        composeRule.onNodeWithTag("home_surface").performTouchInput { swipeUp() }
        composeRule.onNodeWithText("Drawer swipe target").performTouchInput { swipeDown() }

        composeRule.runOnIdle { assertEquals(0, launches) }
        composeRule.onNodeWithTag("home_time").assertIsDisplayed()
    }

    @Test
    fun drawerTransitionUsesContractedProgressAndDisplacement() {
        assertEquals(0.5f, drawerGestureProgress(100f, 200f), 0.001f)
        assertEquals(150f, drawerInteractiveDisplacement(100f), 0.001f)
    }

    @Test
    fun drawerTransitionReleaseUsesDistanceAndFlingBoundaries() {
        assertEquals(
            AvenorSurface.Home,
            transitionTarget(AvenorSurface.Home, 119f, 120f, 0f, 1_000f),
        )
        assertEquals(
            AvenorSurface.Drawer,
            transitionTarget(AvenorSurface.Home, 120f, 120f, 0f, 1_000f),
        )
        assertEquals(
            AvenorSurface.Drawer,
            transitionTarget(AvenorSurface.Home, 80f, 120f, 1_001f, 1_000f),
        )
        assertEquals(
            AvenorSurface.Home,
            transitionTarget(AvenorSurface.Home, 80f, 120f, -1_001f, 1_000f),
        )
        assertEquals(
            AvenorSurface.Home,
            transitionTarget(AvenorSurface.Drawer, 120f, 120f, 0f, 1_000f),
        )
        assertEquals(
            AvenorSurface.Drawer,
            transitionTarget(AvenorSurface.Drawer, 80f, 120f, -1_001f, 1_000f),
        )
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
    fun drawerSectionsOmitTheNumericSectionWhenNoEntryFallsIntoIt() {
        val labels = listOf("Amazon", "百度", "Chrome")
        val entries = labels.mapIndexed { index, label ->
            LaunchableEntry(
                identity = LaunchableIdentity(
                    profileSerialNumber = 0,
                    componentName = ComponentName("com.example.numeric.$index", "MainActivity"),
                ),
                user = Process.myUserHandle(),
                label = label,
                icon = ColorDrawable(Color.TRANSPARENT),
            )
        }

        val sections = buildDrawerSections(entries, Locale.SIMPLIFIED_CHINESE)

        assertEquals(listOf("A", "B", "C"), sections.map(DrawerSection::label))
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
    fun homeFavoriteActionSheetOffersEditForOneFavorite() {
        val entry = LaunchableEntry(
            identity = LaunchableIdentity(
                profileSerialNumber = 0,
                componentName = ComponentName("com.example.only", "MainActivity"),
            ),
            user = Process.myUserHandle(),
            label = "Only favorite",
            icon = ColorDrawable(Color.TRANSPARENT),
        )
        composeRule.setContent {
            AvenorTheme {
                ApplicationActionSheet(
                    entry = entry,
                    favoriteState = FavoriteReadState.Readable(listOf(entry.identity)),
                    onDismiss = {},
                    onAddFavorite = {},
                    onRemoveFavorite = {},
                    onEditFavorites = {},
                    canEditFavorites = true,
                    informationLauncher = ApplicationInformationLauncher { true },
                )
            }
        }

        composeRule.onNodeWithTag("edit_favorites_action").assertIsDisplayed()
    }

    @Test
    fun editModeShowsListManagementControls() {
        val identity = LaunchableIdentity(
            profileSerialNumber = 0,
            componentName = ComponentName("com.example.edit-controls", "MainActivity"),
        )
        var addTarget: String? = null
        composeRule.setContent {
            AvenorTheme {
                HomeScreen(
                    favoriteState = FavoriteReadState.Readable(listOf(identity)),
                    editMode = true,
                    onAddFavoritesToList = { addTarget = it },
                )
            }
        }

        composeRule.onNodeWithTag("favorite_list_control_bar_0").assertIsDisplayed()
        composeRule.onNodeWithTag("favorite_add_0").assertIsDisplayed()
        composeRule.onNodeWithTag("favorite_add_0").performClick()
        composeRule.runOnIdle { assertEquals(PRIMARY_LIST_ID, addTarget) }
        composeRule.onAllNodesWithTag("reorder_favorite_list_0").assertCountEquals(0)
        composeRule.onNodeWithTag("favorite_list_size_0").assertIsDisplayed()
        composeRule.onNodeWithTag("favorite_list_size_0").performClick()
        composeRule.onNodeWithTag("favorite_list_size_menu_0").assertIsDisplayed()
        composeRule.onNodeWithText("Large").assertIsDisplayed()
        composeRule.onNodeWithTag("favorite_list_size_0").performClick()
        composeRule.onNodeWithTag("remove_favorite_list_0").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Remove list").assertIsDisplayed()
        composeRule.onAllNodesWithText("Remove list").assertCountEquals(0)
        composeRule.onNodeWithTag("remove_favorite_list_0").performClick()
        composeRule.onNodeWithText("Remove favorite list?").assertIsDisplayed()
        composeRule.onNodeWithText(
            "All applications in this list will be removed from favorites.",
        ).assertIsDisplayed()
        composeRule.onNodeWithTag("cancel_remove_favorite_list_0").performClick()
        composeRule.onAllNodesWithText("Remove favorite list?").assertCountEquals(0)
        composeRule.onNodeWithTag("remove_favorite_item").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Remove favorite").assertIsDisplayed()
        composeRule.onAllNodesWithText("Remove favorite").assertCountEquals(0)
    }

    @Test
    fun editModeShowsProvisionalListWhenFavoritesAreEmpty() {
        composeRule.setContent {
            AvenorTheme {
                HomeScreen(
                    favoriteState = FavoriteReadState.Readable(FavoriteAggregate()),
                    editMode = true,
                )
            }
        }

        composeRule.onNodeWithTag("favorite_provisional_add_0").assertIsDisplayed()
        composeRule.onAllNodesWithTag("home_favorites_empty").assertCountEquals(0)
    }

    @Test
    fun editModeShowsListReorderHandlesOnlyForTwoLists() {
        val first = LaunchableIdentity(
            profileSerialNumber = 0,
            componentName = ComponentName("com.example.list-one", "MainActivity"),
        )
        val second = LaunchableIdentity(
            profileSerialNumber = 0,
            componentName = ComponentName("com.example.list-two", "MainActivity"),
        )
        composeRule.setContent {
            AvenorTheme {
                HomeScreen(
                    favoriteState = FavoriteReadState.Readable(
                        FavoriteAggregate(
                            verticalLists = listOf(
                                FavoriteContainer(
                                    id = "list-one",
                                    type = FavoriteContainerType.VerticalList,
                                    identities = listOf(first),
                                ),
                                FavoriteContainer(
                                    id = "list-two",
                                    type = FavoriteContainerType.VerticalList,
                                    identities = listOf(second),
                                ),
                            ),
                        ),
                    ),
                    editMode = true,
                )
            }
        }

        composeRule.onNodeWithTag("reorder_favorite_list_0").assertIsDisplayed()
        composeRule.onNodeWithTag("reorder_favorite_list_1").assertIsDisplayed()
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

        composeRule.onAllNodesWithTag("application_shortcut_region").assertCountEquals(0)
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
        composeRule.onAllNodesWithTag("application_action_sheet").assertCountEquals(0)
    }

    @Test
    fun drawerSettingsGearJumpsToDedicatedEntryBeforeOpening() {
        val entry = LaunchableEntry(
            identity = LaunchableIdentity(
                0,
                ComponentName("com.example.settings", "MainActivity"),
            ),
            user = Process.myUserHandle(),
            label = "Settings source",
            icon = ColorDrawable(Color.TRANSPARENT),
        )
        var settingsOpened = false
        composeRule.setContent {
            AvenorTheme {
                DrawerScreen(
                    inventoryLoader = LaunchableInventoryLoader { completeSnapshot(entry) },
                    onOpenSettings = { settingsOpened = true },
                )
            }
        }

        composeRule.onNodeWithTag("drawer_index_settings").performClick()
        composeRule.runOnIdle { assertEquals(false, settingsOpened) }
        composeRule.onNodeWithTag("drawer_settings_anchor").assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_settings_entry").assertIsDisplayed().performClick()
        composeRule.runOnIdle { assertEquals(true, settingsOpened) }
    }

    @Test
    fun settingsPresentsBasicOfflineLoop() {
        var homeSettingsOpened = false
        var repositoryOpened = false
        var backRequested = false
        val platform = object : SettingsPlatform {
            override fun isDefaultHome() = true

            override fun openDefaultHomeSettings(): Boolean {
                homeSettingsOpened = true
                return true
            }

            override fun openProjectRepository(): Boolean {
                repositoryOpened = true
                return true
            }

            override fun versionText() = "v1.1.0(2)"
        }
        composeRule.setContent {
            AvenorTheme {
                SettingsScreen(
                    platform = platform,
                    licenseText = "Apache License test content",
                    onBack = { backRequested = true },
                )
            }
        }

        composeRule.onNodeWithTag("settings_title").assertTextEquals("Settings")
        composeRule.onNodeWithText("Avenor is the default launcher").assertIsDisplayed()
        composeRule.onNodeWithText("v1.1.0(2)").assertIsDisplayed()
        composeRule.onNodeWithTag("settings_default_home").performClick()
        composeRule.runOnIdle { assertEquals(true, homeSettingsOpened) }

        composeRule.onNodeWithTag("settings_project_repository").performClick()
        composeRule.runOnIdle { assertEquals(true, repositoryOpened) }

        composeRule.onNodeWithTag("settings_back").performClick()
        composeRule.runOnIdle { assertEquals(true, backRequested) }

        composeRule.onNodeWithTag("settings_license").performClick()
        composeRule.onNodeWithTag("avenor_license_sheet").assertIsDisplayed()
        composeRule.onNodeWithText("Apache License test content").assertIsDisplayed()

    }
    @Test
    fun disabledDoubleTapLockHasNoHomeAction() {
        val controller = TestAccessibilityLockController(systemEnabled = false, connected = false)
        composeRule.setContent {
            AvenorTheme {
                HomeScreen(accessibilityLockController = controller)
            }
        }

        composeRule.onNodeWithTag("home_double_tap_lock_region")
            .performTouchInput { doubleClick() }

        composeRule.runOnIdle { assertEquals(0, controller.lockRequests) }
    }

    @Test
    fun enabledDoubleTapLockRequestsOneActionFromEligibleBlankSpace() {
        val controller = TestAccessibilityLockController(systemEnabled = true, connected = true)
        composeRule.setContent {
            AvenorTheme {
                HomeScreen(accessibilityLockController = controller)
            }
        }

        composeRule.onNodeWithTag("home_double_tap_lock_region")
            .performTouchInput { doubleClick() }

        composeRule.runOnIdle { assertEquals(1, controller.lockRequests) }
    }

    @Test
    fun enableOrientedAccessibilityHandoffRequiresProminentDisclosure() {
        val controller = TestAccessibilityLockController(systemEnabled = false, connected = false)
        composeRule.setContent {
            AvenorTheme {
                SettingsScreen(
                    platform = EmptySettingsPlatform,
                    licenseText = "",
                    accessibilityLockController = controller,
                    onBack = {},
                )
            }
        }

        composeRule.onNodeWithTag("settings_double_tap_lock").performClick()
        composeRule.onNodeWithTag("double_tap_lock_explanation_sheet").assertIsDisplayed()
        composeRule.onNodeWithTag("open_accessibility_settings").performClick()
        composeRule.onNodeWithTag("accessibility_prominent_disclosure").assertIsDisplayed()
        composeRule.runOnIdle { assertEquals(0, controller.settingsRequests) }

        composeRule.onNodeWithTag("accessibility_disclosure_cancel").performClick()
        composeRule.onAllNodesWithTag("accessibility_prominent_disclosure").assertCountEquals(0)
        composeRule.runOnIdle { assertEquals(0, controller.settingsRequests) }
    }

    @Test
    fun disclosureContinueOpensSystemSettingsWithoutRetainedToggle() {
        val controller = TestAccessibilityLockController(systemEnabled = false, connected = false)
        composeRule.setContent {
            AvenorTheme {
                SettingsScreen(
                    platform = EmptySettingsPlatform,
                    licenseText = "",
                    accessibilityLockController = controller,
                    onBack = {},
                )
            }
        }

        composeRule.onNodeWithTag("settings_double_tap_lock").performClick()
        composeRule.onNodeWithTag("open_accessibility_settings").performClick()
        composeRule.onNodeWithTag("accessibility_disclosure_continue").performClick()

        composeRule.runOnIdle { assertEquals(1, controller.settingsRequests) }
        composeRule.onAllNodesWithTag("accessibility_prominent_disclosure").assertCountEquals(0)
    }

}

// Exceeds the Home-Drawer completion distance even when the gesture starts on a short node.
private const val HOME_DRAWER_SWIPE_DISTANCE_PX = 900f

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
            mutableState.value = FavoriteReadState.Readable(
                readable.aggregate.replaceVerticalList(
                    id = PRIMARY_LIST_ID,
                    identities = readable.primaryIdentities + identity,
                ),
            )
        }
        return true
    }
    override suspend fun remove(identity: LaunchableIdentity): Boolean =
        removeAll(setOf(identity))
    override suspend fun removeAll(identities: Set<LaunchableIdentity>): Boolean {
        val readable = mutableState.value as FavoriteReadState.Readable
        mutableState.value = FavoriteReadState.Readable(
            readable.aggregate.removeIdentities(identities),
        )
        return true
    }

    override suspend fun replaceOrder(identities: List<LaunchableIdentity>): Boolean {
        val readable = mutableState.value as? FavoriteReadState.Readable ?: return false
        if (!isValidReplacement(readable.primaryIdentities, identities)) return false
        mutableState.value = FavoriteReadState.Readable(
            readable.aggregate.replaceVerticalList(
                id = PRIMARY_LIST_ID,
                identities = identities,
            ),
        )
        return true
    }

    override suspend fun replaceComposition(
        primaryIdentities: List<LaunchableIdentity>,
        companionIdentities: List<LaunchableIdentity>,
    ): Boolean {
        val readable = mutableState.value as? FavoriteReadState.Readable ?: return false
        val replacement = primaryIdentities + companionIdentities
        val currentVerticalIdentities =
            readable.aggregate.verticalLists.flatMap(FavoriteContainer::identities)
        if (!isValidReplacement(currentVerticalIdentities, replacement)) return false
        mutableState.value = FavoriteReadState.Readable(
            readable.aggregate.replaceVerticalComposition(
                primaryIdentities,
                companionIdentities,
            ),
        )
        return true
    }

    override suspend fun replaceAggregate(aggregate: FavoriteAggregate): Boolean {
        if (!isValidAggregate(aggregate)) return false
        mutableState.value = FavoriteReadState.Readable(aggregate)
        return true
    }

    override suspend fun updateAggregate(
        transform: (FavoriteAggregate) -> FavoriteAggregate,
    ): FavoriteAggregate? {
        val current = mutableState.value as? FavoriteReadState.Readable ?: return null
        val updated = transform(current.aggregate)
        if (!isValidAggregate(updated)) return null
        mutableState.value = FavoriteReadState.Readable(updated)
        return updated
    }

    fun readableIdentities(): List<LaunchableIdentity> =
        (state.value as FavoriteReadState.Readable).identities
}

private class TestAccessibilityLockController(
    private var systemEnabled: Boolean,
    connected: Boolean,
) : AccessibilityLockController {
    private val mutableConnectionState = MutableStateFlow(connected)
    override val availableForValidation = true
    override val connectionState: StateFlow<Boolean> = mutableConnectionState
    var settingsRequests = 0
        private set
    var lockRequests = 0
        private set

    override fun isSystemEnabled() = systemEnabled

    override fun openAccessibilitySettings(): Boolean {
        settingsRequests += 1
        return true
    }

    override fun requestLock(): LockRequestResult {
        lockRequests += 1
        return LockRequestResult.Requested
    }
}
