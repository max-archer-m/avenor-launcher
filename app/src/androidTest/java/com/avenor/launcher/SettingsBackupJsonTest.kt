package com.avenor.launcher

import android.content.ComponentName
import java.time.LocalDateTime
import java.util.Locale
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class SettingsBackupJsonTest {
    @Test
    fun roundTripPreservesCompleteFavoriteAndDisplaySettingsState() {
        val aggregate = testAggregate()
        val settings = testSettings()

        val parsed = SettingsBackupJson.parse(
            SettingsBackupJson.serialize(
                aggregate = aggregate,
                settings = settings,
            ),
        )

        assertNotNull(parsed)
        assertEquals(aggregate, parsed?.aggregate)
        assertEquals(settings, parsed?.settings)
    }

    @Test
    fun higherSchemaVersionIsRejected() {
        val document = validBackupDocument()
            .put("schemaVersion", SettingsBackupJson.BACKUP_SCHEMA_VERSION + 1)

        assertNull(SettingsBackupJson.parse(document.toString()))
    }

    @Test
    fun lowerSchemaVersionIsAcceptedUnderCurrentSchema() {
        val document = validBackupDocument()
            .put("schemaVersion", SettingsBackupJson.BACKUP_SCHEMA_VERSION - 1)

        val parsed = SettingsBackupJson.parse(document.toString())

        assertNotNull(parsed)
    }

    @Test
    fun missingFavoritesSectionRestoresAnEmptyFavoriteState() {
        val document = validBackupDocument()
            .remove("favorites")

        val parsed = SettingsBackupJson.parse(document.toString())

        assertNotNull(parsed)
        assertEquals(OrderedFavoriteAggregate(), parsed?.aggregate)
    }

    @Test
    fun missingDisplaySettingsSectionRestoresDefaultSettings() {
        val document = validBackupDocument()
            .remove("displaySettings")

        val parsed = SettingsBackupJson.parse(document.toString())

        assertNotNull(parsed)
        assertEquals(DrawerDisplaySettings(), parsed?.settings)
    }

    @Test
    fun missingFieldInsidePresentSectionRestoresThatFieldDefault() {
        val displaySettings = validBackupDocument()
            .getJSONObject("displaySettings")
            .remove("backgroundOpacity")
        val document = validBackupDocument()
            .put("displaySettings", displaySettings)

        val parsed = SettingsBackupJson.parse(document.toString())

        assertNotNull(parsed)
        assertEquals(
            DrawerDisplaySettings.DEFAULT_BACKGROUND_OPACITY,
            parsed?.settings?.backgroundOpacity,
        )
    }

    @Test
    fun emptyBackupWithNoModulesIsValid() {
        val document = validBackupDocument()
            .put("favorites", JSONObject().put("modules", org.json.JSONArray()))

        val parsed = SettingsBackupJson.parse(document.toString())

        assertNotNull(parsed)
        assertEquals(0, parsed?.aggregate?.modules?.size)
    }

    @Test
    fun malformedJsonFails() {
        assertNull(SettingsBackupJson.parse("not a json document"))
    }

    @Test
    fun truncatedJsonFails() {
        val document = SettingsBackupJson.serialize(
            aggregate = testAggregate(),
            settings = testSettings(),
        )

        assertNull(SettingsBackupJson.parse(document.dropLast(8)))
    }

    @Test
    fun unrecognizedFieldValueFails() {
        val modules = validBackupDocument()
            .getJSONObject("favorites")
            .getJSONArray("modules")
        modules.getJSONObject(0).put("type", "spiral")

        assertNull(parseWithModules(modules))
    }

    @Test
    fun moduleWithEmptyIdentitiesFails() {
        val modules = validBackupDocument()
            .getJSONObject("favorites")
            .getJSONArray("modules")
        modules.getJSONObject(0).put("identities", org.json.JSONArray())

        assertNull(parseWithModules(modules))
    }

    @Test
    fun itemsPerRowOutsideValidRangeFails() {
        val modules = validBackupDocument()
            .getJSONObject("favorites")
            .getJSONArray("modules")
        modules.getJSONObject(0).put("itemsPerRow", 5)

        assertNull(parseWithModules(modules))
    }

    @Test
    fun fileNameUsesContractedPrefixVersionAndTimestamp() {
        val name = backupFileName(
            versionName = "1.5.0",
            timestamp = LocalDateTime.of(2026, 9, 8, 9, 7),
        )

        assertEquals("avenor-backup-1.5.0-202609080907.json", name)
    }

    @Test
    fun fileNameTimestampIsStableAcrossLocalesAndHourFormats() {
        val defaultLocale = Locale.getDefault()
        try {
            Locale.setDefault(Locale.forLanguageTag("th-TH-u-ca-buddhist-nu-thai"))

            val name = backupFileName(
                versionName = "1.5.0",
                timestamp = LocalDateTime.of(2026, 9, 8, 9, 7),
            )

            assertEquals("avenor-backup-1.5.0-202609080907.json", name)
        } finally {
            Locale.setDefault(defaultLocale)
        }
    }

    private fun validBackupDocument(): JSONObject =
        JSONObject(
            SettingsBackupJson.serialize(
                aggregate = testAggregate(),
                settings = testSettings(),
            ),
        )

    private fun parseWithModules(modules: org.json.JSONArray): SettingsBackupState? =
        SettingsBackupJson.parse(
            validBackupDocument()
                .put("favorites", JSONObject().put("modules", modules))
                .toString(),
        )

    private fun testAggregate() = OrderedFavoriteAggregate(
        modules = listOf(
            OrderedFavoriteModule(
                id = "vertical-list-1",
                type = OrderedFavoriteModuleType.Vertical,
                identities = listOf(identity(1), identity(2)),
                applicationSize = FavoriteListSize.Large,
                namePlacement = FavoriteNamePlacement.Below,
                itemsPerRow = 3,
            ),
            OrderedFavoriteModule(
                id = "favorite-bar-1",
                type = OrderedFavoriteModuleType.Ribbon,
                identities = listOf(identity(3)),
            ),
        ),
    )

    private fun testSettings() = DrawerDisplaySettings(
        applicationSize = DrawerApplicationSize.Small,
        namePlacement = DrawerNamePlacement.Below,
        itemsPerRow = 2,
        sectionAnchorPresentation = DrawerSectionAnchorPresentation.LeftSide,
        backgroundOpacity = 100,
    )

    private fun identity(serial: Long) = LaunchableIdentity(
        serial,
        ComponentName("com.example", "Main"),
    )
}
