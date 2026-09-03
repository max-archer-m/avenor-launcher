package com.avenor.launcher

import android.content.ComponentName
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Process
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class HomeApplicationMovementUiTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun singleColumnMovesByInsertionWithoutLaunchingOrRemoving() = verifyMovement()

    @Test
    fun multiColumnBelowNamesMoveWithoutLosingTheOwningPointer() = verifyMovement(below = true)

    @Test
    fun ribbonUsesTheSameExclusiveWholeItemMovement() = verifyMovement(ribbon = true)

    @Test
    fun verticalToRibbonUsesTheDestinationInsertionAxis() = verifyMovement(destinationRibbon = true)

    @Test
    fun ribbonToVerticalKeepsTheSourcePreviewUntilRelease() = verifyMovement(ribbon = true, destinationRibbon = false)

    @Test
    fun verticalToVerticalUsesAnExplicitDestinationModule() = verifyMovement(destinationRibbon = false)

    @Test
    fun ribbonToRibbonUsesAnExplicitDestinationModule() = verifyMovement(ribbon = true, destinationRibbon = true)

    @Test
    fun finalApplicationCanDropOntoCreateListWithoutAnInsertionLine() = verifyMovement(creationType = OrderedFavoriteModuleType.Vertical, singleSource = true)

    @Test
    fun belowNameApplicationCanDropOntoCreateRibbon() = verifyMovement(below = true, creationType = OrderedFavoriteModuleType.Ribbon)

    private fun verifyMovement(
        below: Boolean = false,
        ribbon: Boolean = false,
        destinationRibbon: Boolean? = null,
        creationType: OrderedFavoriteModuleType? = null,
        singleSource: Boolean = false,
    ) {
        val count = if (singleSource) 1 else if (destinationRibbon != null) 4 else if (ribbon) 2 else 3
        val entries = (1..count).map(
            transform = { index ->
                LaunchableEntry(
                    identity = LaunchableIdentity(
                        profileSerialNumber = 1,
                        componentName = ComponentName("test.app$index", "Main"),
                    ),
                    user = Process.myUserHandle(), label = "App $index", icon = ColorDrawable(Color.TRANSPARENT),
                )
            },
        )
        val module = OrderedFavoriteModule(
            id = "source",
            type = if (ribbon) OrderedFavoriteModuleType.Ribbon else OrderedFavoriteModuleType.Vertical,
            identities = entries.take(n = if (destinationRibbon != null) 2 else count).map(transform = { it.identity }),
            namePlacement = if (below) FavoriteNamePlacement.Below else FavoriteNamePlacement.Right,
            itemsPerRow = if (below) 2 else 1,
        )
        val target = destinationRibbon?.let(
            block = {
                OrderedFavoriteModule(
                    id = "target", type = if (it) OrderedFavoriteModuleType.Ribbon else OrderedFavoriteModuleType.Vertical,
                    identities = entries.drop(n = 2).map(transform = { entry -> entry.identity }),
                )
            },
        )
        val modules = listOf(element = module) + listOfNotNull(element = target)
        val containers = modules.map(
            transform = {
                FavoriteContainer(
                    id = it.id, type = if (it.type == OrderedFavoriteModuleType.Ribbon) FavoriteContainerType.FavoriteBar else FavoriteContainerType.VerticalList,
                    identities = it.identities, namePlacement = it.namePlacement, itemsPerRow = it.itemsPerRow,
                )
            },
        )
        val state = FavoriteReadState.Readable(
            aggregate = FavoriteAggregate(
                favoriteBars = containers.filter(predicate = { it.type == FavoriteContainerType.FavoriteBar }),
                verticalLists = containers.filter(predicate = { it.type == FavoriteContainerType.VerticalList }),
            ),
            orderedModules = modules,
        )
        var timeout = 0L
        var change: ApplicationOrderChange? = null
        var removals = 0
        var launches = 0
        composeRule.setContent(
            composable = {
                timeout = LocalViewConfiguration.current.longPressTimeoutMillis
                AvenorTheme(content = {
                    HomeScreen(
                        favoriteState = state,
                        favoriteAvailability = entries.associate(transform = { it.identity to FavoriteAvailability.Available(entry = it) }),
                        editMode = true,
                        onRemoveApplication = { removals += 1 },
                        onLaunchFavorite = { launches += 1 },
                        onCommitApplicationOrder = { change = it },
                    )
                })
            },
        )
        val root = composeRule.onNodeWithTag(testTag = "home_ordered_favorite_modules")
        val origin = root.fetchSemanticsNode().boundsInRoot.topLeft
        val cells = composeRule.onAllNodesWithTag(testTag = if (below) "home_favorite_below_item" else "home_favorite_row")
        val source = cells[0].fetchSemanticsNode().boundsInRoot
        val last = cells[entries.lastIndex].fetchSemanticsNode().boundsInRoot
        composeRule.onAllNodesWithTag(testTag = "remove_favorite_item")[0].performClick()
        composeRule.runOnIdle(action = { assertEquals(1, removals) })
        composeRule.onNodeWithTag(testTag = "home_application_movement_preview").assertDoesNotExist()

        root.performTouchInput(block = { down(position = source.center - origin) })
        composeRule.mainClock.advanceTimeBy(milliseconds = timeout + 50)
        composeRule.onNodeWithTag(testTag = "home_application_movement_preview").assertIsDisplayed()
        composeRule.onNodeWithTag(testTag = "home_application_placeholder").assertIsDisplayed()
        val destination = if (creationType != null) {
            composeRule.onNodeWithTag(
                testTag = if (creationType == OrderedFavoriteModuleType.Vertical) "home_add_favorite_list" else "home_add_favorite_ribbon",
            ).fetchSemanticsNode().boundsInRoot.center
        } else if (below || (destinationRibbon ?: ribbon)) {
            Offset(x = last.right - 1f, y = last.center.y)
        } else {
            Offset(x = last.center.x, y = last.bottom - 1f)
        }
        root.performTouchInput(block = { moveTo(position = destination - origin) })
        if (creationType == null) {
            composeRule.onNodeWithTag(testTag = "home_application_insertion_line").assertIsDisplayed()
            composeRule.onNodeWithTag(testTag = "home_module_creation_drop_outline").assertDoesNotExist()
        } else {
            composeRule.onNodeWithTag(testTag = "home_application_insertion_line").assertDoesNotExist()
            composeRule.onNodeWithTag(testTag = "home_module_creation_drop_outline").assertIsDisplayed()
        }
        composeRule.runOnIdle(action = { assertNull(change) })
        root.performTouchInput(block = { up() })
        composeRule.runOnIdle(action = {
            assertNotNull(change)
            val expected = if (creationType != null) emptyList() else if (target == null) module.identities.drop(n = 1) else target.identities
            assertEquals(expected + module.identities.first(), change?.reorderedIdentities())
            assertEquals(target?.id ?: module.id, change?.destinationModuleId)
            assertEquals(creationType, change?.newModuleType)
            assertEquals(0, launches)
            assertEquals(1, removals)
        })
        composeRule.onNodeWithTag(testTag = "home_application_movement_preview").assertDoesNotExist()
        composeRule.onNodeWithTag(testTag = "home_application_placeholder").assertDoesNotExist()
        composeRule.onNodeWithTag(testTag = "home_module_creation_drop_outline").assertDoesNotExist()
    }
}
