package com.avenor.launcher

import android.content.ComponentName
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsBackupRestoreCoordinatorTest {
    @Test
    fun successfulRestoreReplacesFavoritesAndDisplaySettings() = runBlocking {
        val favorites = FakeFavoritesAccess()
        val settings = FakeSettingsAccess()
        val coordinator = SettingsBackupRestoreCoordinator(
            favorites = favorites,
            settings = settings,
        )
        val backup = SettingsBackupState(
            aggregate = aggregateWithIdentity(serial = 2),
            settings = DrawerDisplaySettings(
                backgroundMode = DrawerBackgroundMode.Transparent,
            ),
        )

        val succeeded = coordinator.restore(backup)

        assertTrue(succeeded)
        assertEquals(backup.aggregate, favorites.aggregate)
        assertEquals(backup.settings, settings.settings)
    }

    @Test
    fun failedSettingsRestoreRollsFavoritesBackToPreviousState() = runBlocking {
        val previous = aggregateWithIdentity(serial = 1)
        val favorites = FakeFavoritesAccess(initial = previous)
        val settings = FakeSettingsAccess()
        settings.restoreFails = true
        val coordinator = SettingsBackupRestoreCoordinator(
            favorites = favorites,
            settings = settings,
        )
        val backup = SettingsBackupState(
            aggregate = aggregateWithIdentity(serial = 2),
            settings = DrawerDisplaySettings(
                backgroundMode = DrawerBackgroundMode.Transparent,
            ),
        )

        val succeeded = coordinator.restore(backup)

        assertFalse(succeeded)
        assertEquals(previous, favorites.aggregate)
        assertEquals(
            listOf(backup.aggregate, previous),
            favorites.restoreCalls,
        )
        assertTrue(settings.restoreCalls.isNotEmpty())
    }

    @Test
    fun restoreWithoutReadableFavoriteStateChangesNothing() = runBlocking {
        val favorites = FakeFavoritesAccess(initial = null)
        val settings = FakeSettingsAccess()
        val coordinator = SettingsBackupRestoreCoordinator(
            favorites = favorites,
            settings = settings,
        )
        val backup = SettingsBackupState(
            aggregate = aggregateWithIdentity(serial = 2),
            settings = DrawerDisplaySettings(),
        )

        val succeeded = coordinator.restore(backup)

        assertFalse(succeeded)
        assertTrue(favorites.restoreCalls.isEmpty())
        assertTrue(settings.restoreCalls.isEmpty())
    }

    @Test
    fun restoreWithoutReadableDisplaySettingsChangesNothing() = runBlocking {
        val favorites = FakeFavoritesAccess()
        val settings = FakeSettingsAccess(initial = null)
        val coordinator = SettingsBackupRestoreCoordinator(
            favorites = favorites,
            settings = settings,
        )
        val backup = SettingsBackupState(
            aggregate = aggregateWithIdentity(serial = 2),
            settings = DrawerDisplaySettings(),
        )

        val succeeded = coordinator.restore(backup)

        assertFalse(succeeded)
        assertTrue(favorites.restoreCalls.isEmpty())
        assertTrue(settings.restoreCalls.isEmpty())
    }

    private fun aggregateWithIdentity(serial: Long) = OrderedFavoriteAggregate(
        modules = listOf(
            OrderedFavoriteModule(
                id = "vertical-list-1",
                type = OrderedFavoriteModuleType.Vertical,
                identities = listOf(
                    LaunchableIdentity(
                        serial,
                        ComponentName("com.example", "Main"),
                    ),
                ),
            ),
        ),
    )

    private class FakeFavoritesAccess(
        initial: OrderedFavoriteAggregate?,
    ) : BackupFavoritesAccess {
        var aggregate: OrderedFavoriteAggregate? = initial
        var restoreFails = false
        val restoreCalls = mutableListOf<OrderedFavoriteAggregate>()

        override fun currentOrderedAggregate(): OrderedFavoriteAggregate? = aggregate

        override suspend fun restoreAggregate(
            aggregate: OrderedFavoriteAggregate,
        ): Boolean {
            restoreCalls.add(aggregate)
            if (restoreFails) return false
            this.aggregate = aggregate
            return true
        }
    }

    private class FakeSettingsAccess(
        initial: DrawerDisplaySettings? = DrawerDisplaySettings(),
    ) : BackupSettingsAccess {
        var settings: DrawerDisplaySettings? = initial
        var restoreFails = false
        val restoreCalls = mutableListOf<DrawerDisplaySettings>()

        override fun currentSettings(): DrawerDisplaySettings? = settings

        override suspend fun restoreSettings(settings: DrawerDisplaySettings): Boolean {
            restoreCalls.add(settings)
            if (restoreFails) return false
            this.settings = settings
            return true
        }
    }
}
