package com.avenor.launcher

import androidx.test.core.app.ApplicationProvider
import java.io.DataOutputStream
import java.io.File
import java.util.UUID
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DrawerDisplaySettingsStoreTest {
    @Test
    fun freshStoreLoadsConfirmedDefaults(): Unit = runBlocking {
        val file = temporarySettingsFile()
        val store = DrawerDisplaySettingsStore(file = file)

        store.load()

        assertEquals(
            DrawerDisplaySettingsReadState.Readable(
                settings = DrawerDisplaySettings(),
            ),
            store.state.value,
        )
        assertFalse(file.exists())
        deleteSettingsFiles(file = file)
    }

    @Test
    fun completeSettingsRoundTrip(): Unit = runBlocking {
        val file = temporarySettingsFile()
        val settings = DrawerDisplaySettings(
            applicationSize = DrawerApplicationSize.Large,
            namePlacement = DrawerNamePlacement.Below,
            itemsPerRow = 4,
            sectionAnchorPresentation = DrawerSectionAnchorPresentation.LeftSide,
            backgroundMode = DrawerBackgroundMode.Transparent,
        )
        val store = DrawerDisplaySettingsStore(file = file)
        store.load()

        assertTrue(store.replace(settings = settings))
        val reloadedStore = DrawerDisplaySettingsStore(file = file)
        reloadedStore.load()

        assertEquals(
            DrawerDisplaySettingsReadState.Readable(settings = settings),
            reloadedStore.state.value,
        )
        deleteSettingsFiles(file = file)
    }

    @Test
    fun missingFieldsAdoptCurrentDefaultsWithoutDiscardingReadableFields(): Unit = runBlocking {
        val file = temporarySettingsFile()
        writeRawDocument(
            file = file,
            fields = listOf(
                "application_size" to "large",
                "name_placement" to "below",
            ),
        )
        val store = DrawerDisplaySettingsStore(file = file)

        store.load()

        val expected = DrawerDisplaySettings(
            applicationSize = DrawerApplicationSize.Large,
            namePlacement = DrawerNamePlacement.Below,
            itemsPerRow = 1,
            sectionAnchorPresentation = DrawerSectionAnchorPresentation.Inline,
            backgroundMode = DrawerBackgroundMode.FrostedGlass,
        )
        assertEquals(
            DrawerDisplaySettingsReadState.Readable(settings = expected),
            store.state.value,
        )

        val reloadedStore = DrawerDisplaySettingsStore(file = file)
        reloadedStore.load()
        assertEquals(
            DrawerDisplaySettingsReadState.Readable(settings = expected),
            reloadedStore.state.value,
        )
        deleteSettingsFiles(file = file)
    }

    @Test
    fun unknownFieldsDoNotChangeKnownSettings(): Unit = runBlocking {
        val file = temporarySettingsFile()
        writeRawDocument(
            file = file,
            fields = completeRawFields() + ("future_field" to "future_value"),
        )
        val store = DrawerDisplaySettingsStore(file = file)

        store.load()

        assertEquals(
            DrawerDisplaySettingsReadState.Readable(
                settings = DrawerDisplaySettings(),
            ),
            store.state.value,
        )
        deleteSettingsFiles(file = file)
    }

    @Test
    fun unreadableDocumentPublishesFailureWithoutReplacingSource(): Unit = runBlocking {
        val file = temporarySettingsFile()
        val unreadableBytes = byteArrayOf(0x01, 0x02, 0x03)
        file.writeBytes(unreadableBytes)
        val store = DrawerDisplaySettingsStore(file = file)

        store.load()

        assertEquals(DrawerDisplaySettingsReadState.ReadFailure, store.state.value)
        assertEquals(unreadableBytes.toList(), file.readBytes().toList())
        assertFalse(store.replace(settings = DrawerDisplaySettings()))
        deleteSettingsFiles(file = file)
    }

    @Test
    fun failedWriteKeepsLastSuccessfullyReadState(): Unit = runBlocking {
        val blockedParent = temporarySettingsFile().apply {
            writeText("not a directory")
        }
        val file = File(blockedParent, "drawer-display-settings.bin")
        val store = DrawerDisplaySettingsStore(file = file)
        store.load()

        val candidate = DrawerDisplaySettings(
            applicationSize = DrawerApplicationSize.Small,
        )
        assertFalse(store.replace(settings = candidate))
        assertEquals(
            DrawerDisplaySettingsReadState.Readable(
                settings = DrawerDisplaySettings(),
            ),
            store.state.value,
        )
        blockedParent.delete()
    }

    @Test
    fun concurrentCompleteWritesRemainSerializedAndReloadable(): Unit = runBlocking {
        val file = temporarySettingsFile()
        val store = DrawerDisplaySettingsStore(file = file)
        store.load()
        val candidates = listOf(
            DrawerDisplaySettings(applicationSize = DrawerApplicationSize.Large),
            DrawerDisplaySettings(
                namePlacement = DrawerNamePlacement.Below,
                itemsPerRow = 3,
            ),
            DrawerDisplaySettings(
                sectionAnchorPresentation = DrawerSectionAnchorPresentation.LeftSide,
            ),
        )

        val results = candidates.map { settings ->
            async { store.replace(settings = settings) }
        }.awaitAll()

        assertTrue(results.all { result -> result })
        val finalState = store.state.value as DrawerDisplaySettingsReadState.Readable
        val reloadedStore = DrawerDisplaySettingsStore(file = file)
        reloadedStore.load()
        assertEquals(finalState, reloadedStore.state.value)
        deleteSettingsFiles(file = file)
    }

    @Test
    fun contextStoreUsesBackupExcludedFilesDirectory(): Unit = runBlocking {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val file = context.filesDir.resolve("drawer-display-settings.bin")
        deleteSettingsFiles(file = file)
        val store = DrawerDisplaySettingsStore(context = context)

        try {
            store.load()
            assertTrue(
                store.replace(
                    settings = DrawerDisplaySettings(
                        backgroundMode = DrawerBackgroundMode.Transparent,
                    ),
                ),
            )
            assertTrue(file.isFile)
            assertEquals(context.filesDir.canonicalFile, file.parentFile?.canonicalFile)
        } finally {
            deleteSettingsFiles(file = file)
        }
    }

    private fun temporarySettingsFile(): File = File(
        ApplicationProvider.getApplicationContext<android.content.Context>().cacheDir,
        "drawer-display-settings-${UUID.randomUUID()}.bin",
    ).also { file -> deleteSettingsFiles(file = file) }

    private fun deleteSettingsFiles(file: File) {
        file.delete()
        File(file.path + ".new").delete()
        File(file.path + ".bak").delete()
    }

    private fun writeRawDocument(
        file: File,
        fields: List<Pair<String, String>>,
    ) {
        DataOutputStream(file.outputStream()).use { output ->
            output.writeInt(0x44525331)
            output.writeInt(1)
            output.writeInt(fields.size)
            fields.forEach { (key, value) ->
                output.writeUTF(key)
                output.writeUTF(value)
            }
        }
    }

    private fun completeRawFields(): List<Pair<String, String>> = listOf(
        "application_size" to "medium",
        "name_placement" to "right",
        "items_per_row" to "1",
        "section_anchor" to "inline",
        "background" to "frosted_glass",
    )
}
