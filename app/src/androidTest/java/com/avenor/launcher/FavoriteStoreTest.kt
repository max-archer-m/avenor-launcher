package com.avenor.launcher

import android.content.ComponentName
import androidx.test.core.app.ApplicationProvider
import java.io.DataOutputStream
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FavoriteStoreTest {
    @Test
    fun contextStoreWritesFavoritesInsideTheExcludedFilesDirectory() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val file = context.filesDir.resolve("favorites.bin")
        val newFile = context.filesDir.resolve("favorites.bin.new")
        val legacyBackupFile = context.filesDir.resolve("favorites.bin.bak")
        file.delete()
        newFile.delete()
        legacyBackupFile.delete()
        val store = AtomicFileFavoriteStore(context)

        try {
            store.load()
            assertTrue(store.add(identity(1, "com.example", "Main")))

            assertTrue(file.isFile)
            assertEquals(context.filesDir.canonicalFile, file.parentFile?.canonicalFile)
        } finally {
            file.delete()
            newFile.delete()
            legacyBackupFile.delete()
        }
    }

    @Test
    fun addDeduplicateRemoveAndBatchRemovePreserveOrder(): Unit = runBlocking {
        val file = temporaryFavoriteFile()
        val store = AtomicFileFavoriteStore(file)
        val first = identity(1, "com.example.first", "Main")
        val second = identity(1, "com.example.second", "Main")
        val clone = identity(2, "com.example.first", "Main")

        store.load()
        assertTrue(store.add(first))
        assertTrue(store.add(second))
        assertTrue(store.add(first))
        assertTrue(store.add(clone))
        assertEquals(listOf(first, second, clone), store.readableIdentities())
        val reloadedStore = AtomicFileFavoriteStore(file)
        reloadedStore.load()
        assertEquals(listOf(first, second, clone), reloadedStore.readableIdentities())

        assertTrue(store.remove(second))
        assertTrue(store.removeAll(setOf(first)))
        assertEquals(listOf(clone), store.readableIdentities())

        file.delete()
    }

    @Test
    fun damagedDocumentPublishesFailureAndMutationsStayDisabled(): Unit = runBlocking {
        val file = temporaryFavoriteFile()
        DataOutputStream(file.outputStream()).use { output ->
            output.writeInt(0)
            output.writeInt(1)
            output.writeInt(0)
        }
        val original = file.readBytes()
        val store = AtomicFileFavoriteStore(file)

        store.load()

        assertEquals(FavoriteReadState.ReadFailure, store.state.value)
        assertFalse(store.add(identity(1, "com.example", "Main")))
        assertFalse(store.remove(identity(1, "com.example", "Main")))
        assertEquals(original.toList(), file.readBytes().toList())

        file.delete()
    }

    @Test
    fun failedWriteKeepsLastReadableState(): Unit = runBlocking {
        val file = temporaryFavoriteFile()
        val store = AtomicFileFavoriteStore(file)
        val identity = identity(1, "com.example", "Main")
        store.load()
        assertTrue(file.mkdir())

        assertFalse(store.add(identity))
        assertEquals(emptyList<LaunchableIdentity>(), store.readableIdentities())

        file.delete()
    }

    @Test
    fun replaceOrderPersistsOnlyACompletePermutation(): Unit = runBlocking {
        val file = temporaryFavoriteFile()
        val store = AtomicFileFavoriteStore(file)
        val first = identity(1, "com.example.first", "Main")
        val second = identity(1, "com.example.second", "Main")
        val third = identity(2, "com.example.third", "Main")
        store.load()
        store.add(first)
        store.add(second)
        store.add(third)

        assertFalse(store.replaceOrder(listOf(first, first, third)))
        assertFalse(store.replaceOrder(listOf(first, second)))
        assertEquals(listOf(first, second, third), store.readableIdentities())
        assertTrue(store.replaceOrder(listOf(third, first, second)))

        val reloadedStore = AtomicFileFavoriteStore(file)
        reloadedStore.load()
        assertEquals(listOf(third, first, second), reloadedStore.readableIdentities())
        file.delete()
    }

    @Test
    fun legacySchemaMigratesFavoritesToPrimaryInOriginalOrder(): Unit = runBlocking {
        val file = temporaryFavoriteFile()
        val first = identity(1, "com.example.first", "Main")
        val second = identity(2, "com.example.second", "Main")
        DataOutputStream(file.outputStream()).use { output ->
            output.writeInt(0x4156454E)
            output.writeInt(1)
            output.writeInt(2)
            listOf(first, second).forEach {
                output.writeLong(it.profileSerialNumber)
                output.writeUTF(it.componentName.flattenToString())
            }
        }

        val store = AtomicFileFavoriteStore(file)
        store.load()

        val readable = store.state.value as FavoriteReadState.Readable
        assertEquals(listOf(first, second), readable.primaryIdentities)
        assertEquals(emptyList<LaunchableIdentity>(), readable.companionIdentities)
        file.delete()
    }

    @Test
    fun replaceCompositionPersistsBothGroups(): Unit = runBlocking {
        val file = temporaryFavoriteFile()
        val first = identity(1, "com.example.first", "Main")
        val second = identity(1, "com.example.second", "Main")
        val third = identity(2, "com.example.third", "Main")
        val store = AtomicFileFavoriteStore(file)
        store.load()
        store.add(first)
        store.add(second)
        store.add(third)

        assertTrue(store.replaceComposition(listOf(second), listOf(third, first)))

        val reloadedStore = AtomicFileFavoriteStore(file)
        reloadedStore.load()
        val readable = reloadedStore.state.value as FavoriteReadState.Readable
        assertEquals(listOf(second), readable.primaryIdentities)
        assertEquals(listOf(third, first), readable.companionIdentities)
        file.delete()
    }

    private fun temporaryFavoriteFile() = ApplicationProvider.getApplicationContext<android.content.Context>()
        .cacheDir
        .resolve("favorites-${UUID.randomUUID()}.bin")

    private fun identity(serial: Long, packageName: String, className: String) =
        LaunchableIdentity(serial, ComponentName(packageName, className))

    private fun FavoriteStore.readableIdentities(): List<LaunchableIdentity> =
        (state.value as FavoriteReadState.Readable).identities
}
