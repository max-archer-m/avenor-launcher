package com.avenor.launcher

import android.content.ComponentName
import androidx.test.core.app.ApplicationProvider
import java.io.DataOutputStream
import java.io.File
import java.util.UUID
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class OrderedFavoriteModuleStoreTest {
    @Test
    fun cleanLoadCreatesOneReadableEmptyOrderedModel() = runBlocking {
        val file = temporaryFile()
        val store = OrderedFavoriteModuleStore(file)

        store.load()

        assertEquals(
            OrderedFavoriteReadState.Readable(OrderedFavoriteAggregate()),
            store.state.value,
        )
        assertTrue(file.isFile)
        file.delete()
    }

    @Test
    fun unreadableOrderedDataRemainsFailureAndIsNotReplaced() = runBlocking {
        val file = temporaryFile()
        val unreadableBytes = byteArrayOf(0x01, 0x02, 0x03)
        file.writeBytes(unreadableBytes)
        val store = OrderedFavoriteModuleStore(file)

        store.load()

        assertEquals(OrderedFavoriteReadState.ReadFailure, store.state.value)
        assertEquals(unreadableBytes.toList(), file.readBytes().toList())
        file.delete()
    }

    @Test
    fun readableLegacyFavoritesAreAdoptedAsEmptyWithoutChangingLegacyData() = runBlocking {
        val file = temporaryFile()
        val legacyFile = temporaryFile()
        val identity = LaunchableIdentity(
            1,
            ComponentName("com.example", "Main"),
        )
        DataOutputStream(legacyFile.outputStream()).use { output ->
            output.writeInt(0x4156454E)
            output.writeInt(3)
            output.writeInt(1)
            output.writeUTF("vertical-list-1")
            output.writeInt(FavoriteContainerType.VerticalList.ordinal)
            output.writeInt(FavoriteListSize.Medium.ordinal)
            output.writeInt(1)
            output.writeLong(identity.profileSerialNumber)
            output.writeUTF(identity.componentName.flattenToString())
        }
        val legacyBytes = legacyFile.readBytes()

        val store = OrderedFavoriteModuleStore(file, legacyFile)
        store.load()

        assertEquals(
            OrderedFavoriteReadState.Readable(OrderedFavoriteAggregate()),
            store.state.value,
        )
        assertEquals(legacyBytes.toList(), legacyFile.readBytes().toList())
        file.delete()
        legacyFile.delete()
    }

    @Test
    fun unreadableLegacyFavoritesRemainFailureAndAreNotReplaced() = runBlocking {
        val file = temporaryFile()
        val legacyFile = temporaryFile()
        DataOutputStream(legacyFile.outputStream()).use { output ->
            output.writeInt(0x4156454E)
            output.writeInt(99)
        }
        val legacyBytes = legacyFile.readBytes()

        val store = OrderedFavoriteModuleStore(file, legacyFile)
        store.load()

        assertEquals(OrderedFavoriteReadState.ReadFailure, store.state.value)
        assertEquals(legacyBytes.toList(), legacyFile.readBytes().toList())
        assertTrue(!file.exists())
        file.delete()
        legacyFile.delete()
    }

    @Test
    fun recoverableLegacyBackupIsReadBeforeAdoption() = runBlocking {
        val file = temporaryFile()
        val legacyFile = temporaryFile()
        val legacyBackup = File(legacyFile.path + ".bak")
        val identity = LaunchableIdentity(1, ComponentName("com.example", "Main"))
        DataOutputStream(legacyBackup.outputStream()).use { output ->
            output.writeInt(0x4156454E)
            output.writeInt(1)
            output.writeInt(1)
            output.writeLong(identity.profileSerialNumber)
            output.writeUTF(identity.componentName.flattenToString())
        }
        val legacyBytes = legacyBackup.readBytes()

        val store = OrderedFavoriteModuleStore(file, legacyFile)
        store.load()

        assertEquals(
            OrderedFavoriteReadState.Readable(OrderedFavoriteAggregate()),
            store.state.value,
        )
        assertEquals(legacyBytes.toList(), legacyFile.readBytes().toList())
        assertFalse(legacyBackup.exists())
        file.delete()
        legacyFile.delete()
    }

    @Test
    fun invalidLegacyAggregateDoesNotCreateOrderedState() = runBlocking {
        val file = temporaryFile()
        val legacyFile = temporaryFile()
        val identity = LaunchableIdentity(1, ComponentName("com.example", "Main"))
        DataOutputStream(legacyFile.outputStream()).use { output ->
            output.writeInt(0x4156454E)
            output.writeInt(3)
            output.writeInt(2)
            repeat(2) { index ->
                output.writeUTF("vertical-list-${index + 1}")
                output.writeInt(FavoriteContainerType.VerticalList.ordinal)
                output.writeInt(FavoriteListSize.Medium.ordinal)
                output.writeInt(1)
                output.writeLong(identity.profileSerialNumber)
                output.writeUTF(identity.componentName.flattenToString())
            }
        }

        val store = OrderedFavoriteModuleStore(file, legacyFile)
        store.load()

        assertEquals(OrderedFavoriteReadState.ReadFailure, store.state.value)
        assertFalse(file.exists())
        legacyFile.delete()
    }

    @Test
    fun orderedModulesRoundTripInModuleAndIdentityOrder() = runBlocking {
        val file = temporaryFile()
        val first = LaunchableIdentity(1, ComponentName("com.example.first", "Main"))
        val second = LaunchableIdentity(2, ComponentName("com.example.second", "Main"))
        val aggregate = OrderedFavoriteAggregate(
            modules = listOf(
                OrderedFavoriteModule(
                    id = "vertical-1",
                    type = OrderedFavoriteModuleType.Vertical,
                    identities = listOf(first),
                    applicationSize = FavoriteListSize.Large,
                    namePlacement = FavoriteNamePlacement.Below,
                    itemsPerRow = 4,
                ),
                OrderedFavoriteModule(
                    id = "ribbon-1",
                    type = OrderedFavoriteModuleType.Ribbon,
                    identities = listOf(second),
                ),
            ),
        )
        val store = OrderedFavoriteModuleStore(file)
        store.load()

        assertTrue(store.replaceAggregate(aggregate))
        val reloadedStore = OrderedFavoriteModuleStore(file)
        reloadedStore.load()

        assertEquals(
            OrderedFavoriteReadState.Readable(aggregate),
            reloadedStore.state.value,
        )
        assertEquals(listOf(first, second), aggregate.identities)
        file.delete()
    }

    @Test
    fun versionOneOrderedModuleLoadsWithCurrentVerticalDefaults() = runBlocking {
        val file = temporaryFile()
        val identity = LaunchableIdentity(1, ComponentName("com.example", "Main"))
        DataOutputStream(file.outputStream()).use { output ->
            output.writeInt(0x41464D31)
            output.writeInt(1)
            output.writeInt(1)
            output.writeUTF("vertical-1")
            output.writeInt(OrderedFavoriteModuleType.Vertical.ordinal)
            output.writeInt(1)
            output.writeLong(identity.profileSerialNumber)
            output.writeUTF(identity.componentName.flattenToString())
        }

        val store = OrderedFavoriteModuleStore(file)
        store.load()

        assertEquals(
            OrderedFavoriteReadState.Readable(
                OrderedFavoriteAggregate(
                    listOf(
                        OrderedFavoriteModule(
                            id = "vertical-1",
                            type = OrderedFavoriteModuleType.Vertical,
                            identities = listOf(identity),
                        ),
                    ),
                ),
            ),
            store.state.value,
        )
        file.delete()
    }

    @Test
    fun recoverableOrderedBackupIsReadInsteadOfTriggeringLegacyAdoption() = runBlocking {
        val file = temporaryFile()
        val identity = LaunchableIdentity(1, ComponentName("com.example", "Main"))
        val aggregate = OrderedFavoriteAggregate(
            modules = listOf(
                OrderedFavoriteModule(
                    id = "vertical-1",
                    type = OrderedFavoriteModuleType.Vertical,
                    identities = listOf(identity),
                ),
            ),
        )
        val initialStore = OrderedFavoriteModuleStore(file)
        initialStore.load()
        assertTrue(initialStore.replaceAggregate(aggregate))
        val backup = File(file.path + ".bak")
        assertTrue(file.renameTo(backup))

        val reloadedStore = OrderedFavoriteModuleStore(file)
        reloadedStore.load()

        assertEquals(
            OrderedFavoriteReadState.Readable(aggregate),
            reloadedStore.state.value,
        )
        assertTrue(file.isFile)
        assertFalse(backup.exists())
        file.delete()
    }

    @Test
    fun invalidOrderedAggregateRejectsDuplicateIdentities() {
        val identity = LaunchableIdentity(1, ComponentName("com.example", "Main"))
        assertTrue(
            !isValidOrderedFavoriteAggregate(
                OrderedFavoriteAggregate(
                    modules = listOf(
                        OrderedFavoriteModule(
                            id = "one",
                            type = OrderedFavoriteModuleType.Vertical,
                            identities = listOf(identity),
                        ),
                        OrderedFavoriteModule(
                            id = "two",
                            type = OrderedFavoriteModuleType.Ribbon,
                            identities = listOf(identity),
                        ),
                    ),
                ),
            ),
        )
    }

    @Test
    fun aggregateUpdateCanDeleteTheLastModule() = runBlocking {
        val file = temporaryFile()
        val identity = LaunchableIdentity(1, ComponentName("com.example", "Main"))
        val adapter = OrderedFavoriteStoreAdapter(file)
        adapter.load()
        assertTrue(
            adapter.replaceAggregate(
                FavoriteAggregate(
                    verticalLists = listOf(
                        FavoriteContainer(
                            id = "vertical-list-1",
                            type = FavoriteContainerType.VerticalList,
                            identities = listOf(identity),
                        ),
                    ),
                ),
            ),
        )

        val updated = adapter.updateAggregate { it.removeIdentity(identity) }

        assertEquals(FavoriteAggregate(), updated)
        val readable = adapter.state.value as FavoriteReadState.Readable
        assertTrue(readable.aggregate.identities.isEmpty())
        assertTrue(readable.orderedModules?.isEmpty() == true)
        file.delete()
    }

    @Test
    fun genericAddPreservesAnExistingModule() = runBlocking {
        val file = temporaryFile()
        val ribbonIdentity = LaunchableIdentity(1, ComponentName("com.ribbon", "Main"))
        val addedIdentity = LaunchableIdentity(1, ComponentName("com.vertical", "Main"))
        val adapter = OrderedFavoriteStoreAdapter(file)
        adapter.load()
        assertTrue(
            adapter.replaceAggregate(
                FavoriteAggregate(
                    favoriteBars = listOf(
                        FavoriteContainer(
                            id = "favorite-bar-1",
                            type = FavoriteContainerType.FavoriteBar,
                            identities = listOf(ribbonIdentity),
                        ),
                    ),
                ),
            ),
        )

        assertTrue(adapter.add(addedIdentity))

        val readable = adapter.state.value as FavoriteReadState.Readable
        assertEquals(listOf(ribbonIdentity, addedIdentity), readable.identities)
        assertEquals(2, readable.orderedModules?.size)
        file.delete()
    }

    @Test
    fun styleUpdatePreservesUnchangedModuleInstances() = runBlocking {
        val file = temporaryFile()
        val firstIdentity = LaunchableIdentity(1, ComponentName("com.first", "Main"))
        val secondIdentity = LaunchableIdentity(2, ComponentName("com.second", "Main"))
        val adapter = OrderedFavoriteStoreAdapter(file)
        adapter.load()
        assertTrue(
            adapter.replaceAggregate(
                FavoriteAggregate(
                    verticalLists = listOf(
                        FavoriteContainer(
                            id = "vertical-1",
                            type = FavoriteContainerType.VerticalList,
                            identities = listOf(firstIdentity),
                        ),
                        FavoriteContainer(
                            id = "vertical-2",
                            type = FavoriteContainerType.VerticalList,
                            identities = listOf(secondIdentity),
                        ),
                    ),
                ),
            ),
        )
        val unchangedBefore = (adapter.state.value as FavoriteReadState.Readable)
            .orderedModules
            ?.single { it.id == "vertical-2" }

        adapter.updateAggregate { aggregate ->
            aggregate.updateVerticalList("vertical-1") {
                it.copy(listSize = FavoriteListSize.Large)
            }
        }

        val unchangedAfter = (adapter.state.value as FavoriteReadState.Readable)
            .orderedModules
            ?.single { it.id == "vertical-2" }
        assertSame(unchangedBefore, unchangedAfter)
        file.delete()
    }

    @Test
    fun concurrentAddsAreSerializedWithoutLosingIdentities() = runBlocking {
        val file = temporaryFile()
        val identities = (1L..12L).map { serial ->
            LaunchableIdentity(serial, ComponentName("com.example.$serial", "Main"))
        }
        val adapter = OrderedFavoriteStoreAdapter(file)
        adapter.load()

        val results = identities.map { identity ->
            async { adapter.add(identity) }
        }.awaitAll()

        assertTrue(results.all { it })
        val readable = adapter.state.value as FavoriteReadState.Readable
        assertEquals(identities.toSet(), readable.identities.toSet())
        assertEquals(identities.size, readable.identities.size)
        file.delete()
    }

    private fun temporaryFile(): File =
        ApplicationProvider.getApplicationContext<android.content.Context>()
            .cacheDir
            .resolve("ordered-favorites-${UUID.randomUUID()}.bin")
}
