package com.avenor.launcher

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import com.avenor.launcher.ui.drawer.DrawerApplicationSize
import com.avenor.launcher.ui.drawer.DrawerDisplaySettings
import com.avenor.launcher.ui.drawer.DrawerNamePlacement
import com.avenor.launcher.ui.drawer.DrawerSectionAnchorPresentation

/** Read and restore access to the favorite state for the backup and restore journey. */
internal interface BackupFavoritesAccess {
    fun currentOrderedAggregate(): OrderedFavoriteAggregate?

    suspend fun restoreAggregate(aggregate: OrderedFavoriteAggregate): Boolean
}

/** Read and restore access to the Drawer display settings for the backup journey. */
internal interface BackupSettingsAccess {
    fun currentSettings(): DrawerDisplaySettings?

    suspend fun restoreSettings(settings: DrawerDisplaySettings): Boolean
}

/** One parsed backup document: the favorite state and the display settings it carries. */
internal data class SettingsBackupState(
    val aggregate: OrderedFavoriteAggregate,
    val settings: DrawerDisplaySettings,
)

/**
 * Builds the suggested backup display name from the current application version name
 * and the device's local date and time. The timestamp pattern is evaluated under a
 * fixed locale so the `yyyyMMddHHmm` form never changes with locale or 12/24-hour
 * settings.
 */
internal fun backupFileName(
    versionName: String,
    timestamp: LocalDateTime,
): String {
    val stamp = timestamp.format(
        DateTimeFormatter.ofPattern(BACKUP_TIMESTAMP_PATTERN).withLocale(Locale.US),
    )
    return "avenor-backup-$versionName-$stamp.json"
}

private const val BACKUP_TIMESTAMP_PATTERN = "yyyyMMddHHmm"

/**
 * Serializes and parses the schema-versioned backup document. A backup whose schema
 * version is higher than the current application schema is rejected; a backup whose
 * schema version is equal to or lower is interpreted under the current schema. A
 * missing section restores that section's default state, and a missing field inside a
 * present section restores that field's default value.
 */
internal object SettingsBackupJson {
    const val BACKUP_SCHEMA_VERSION = 1

    fun serialize(
        aggregate: OrderedFavoriteAggregate,
        settings: DrawerDisplaySettings,
    ): String {
        val favorites = JSONObject().put(
            "modules",
            JSONArray().apply {
                aggregate.modules.forEach { module ->
                    put(
                        JSONObject()
                            .put("id", module.id)
                            .put("type", module.type.storageValue)
                            .put(
                                "applicationSize",
                                module.applicationSize.storageValue,
                            )
                            .put("namePlacement", module.namePlacement.storageValue)
                            .put("itemsPerRow", module.itemsPerRow)
                            .put(
                                "identities",
                                JSONArray().apply {
                                    module.identities.forEach { identity ->
                                        put(
                                            JSONObject()
                                                .put(
                                                    "serial",
                                                    identity.profileSerialNumber,
                                                )
                                                .put(
                                                    "component",
                                                    identity.componentName
                                                        .flattenToString(),
                                                ),
                                        )
                                    }
                                },
                            ),
                    )
                }
            },
        )
        val displaySettings = JSONObject()
            .put("applicationSize", settings.applicationSize.storageValue)
            .put("namePlacement", settings.namePlacement.storageValue)
            .put("itemsPerRow", settings.itemsPerRow)
            .put(
                "sectionAnchorPresentation",
                settings.sectionAnchorPresentation.storageValue,
            )
            .put("backgroundOpacity", settings.backgroundOpacity)
        return JSONObject()
            .put("schemaVersion", BACKUP_SCHEMA_VERSION)
            .put("favorites", favorites)
            .put("displaySettings", displaySettings)
            .toString(2)
    }

    fun parse(text: String): SettingsBackupState? = try {
        val root = JSONObject(text)
        val schemaVersion = root.getInt("schemaVersion")
        if (schemaVersion > BACKUP_SCHEMA_VERSION ||
            (root.has("favorites") && root.optJSONObject("favorites") == null) ||
            (root.has("displaySettings") && root.optJSONObject("displaySettings") == null)
        ) {
            null
        } else {
            val aggregate = parseFavorites(root.optJSONObject("favorites"))
            val settings = parseDisplaySettings(root.optJSONObject("displaySettings"))
            if (aggregate == null || settings == null ||
                !isValidOrderedFavoriteAggregate(aggregate)
            ) {
                null
            } else {
                SettingsBackupState(aggregate = aggregate, settings = settings)
            }
        }
    } catch (_: Exception) {
        null
    }

    private fun parseFavorites(favorites: JSONObject?): OrderedFavoriteAggregate? {
        if (favorites == null) return OrderedFavoriteAggregate()
        val modules = favorites.optJSONArray("modules") ?: return OrderedFavoriteAggregate()
        val parsed = buildList {
            repeat(modules.length()) { index ->
                val module = parseModule(modules.optJSONObject(index) ?: return null)
                    ?: return null
                add(module)
            }
        }
        return try {
            OrderedFavoriteAggregate(modules = parsed)
        } catch (_: IllegalArgumentException) {
            null
        }
    }

    private fun parseModule(module: JSONObject): OrderedFavoriteModule? = try {
        OrderedFavoriteModule(
            id = module.getString("id"),
            type = module.enumField(
                name = "type",
                default = OrderedFavoriteModuleType.Vertical,
            ) { value -> orderedFavoriteModuleTypeFromStorageValue(value) } ?: return null,
            applicationSize = module.enumField(
                name = "applicationSize",
                default = FavoriteListSize.Medium,
            ) { value -> favoriteListSizeFromStorageValue(value) } ?: return null,
            namePlacement = module.enumField(
                name = "namePlacement",
                default = FavoriteNamePlacement.Right,
            ) { value -> favoriteNamePlacementFromStorageValue(value) } ?: return null,
            itemsPerRow = module.optInt("itemsPerRow", 1),
            identities = parseIdentities(
                module.optJSONArray("identities") ?: return null,
            ),
        )
    } catch (_: Exception) {
        null
    }

    private fun parseIdentities(identities: JSONArray): List<LaunchableIdentity> = buildList {
        repeat(identities.length()) { index ->
            val identity = identities.optJSONObject(index) ?: throw IllegalArgumentException()
            val component = ComponentName.unflattenFromString(
                identity.getString("component"),
            ) ?: throw IllegalArgumentException()
            add(
                LaunchableIdentity(
                    profileSerialNumber = identity.getLong("serial"),
                    componentName = component,
                ),
            )
        }
    }

    private fun parseDisplaySettings(displaySettings: JSONObject?): DrawerDisplaySettings? {
        val defaults = DrawerDisplaySettings()
        if (displaySettings == null) return defaults
        return try {
            DrawerDisplaySettings(
                applicationSize = displaySettings.enumField(
                    name = "applicationSize",
                    default = defaults.applicationSize,
                ) { value -> drawerApplicationSizeFromStorageValue(value) } ?: return null,
                namePlacement = displaySettings.enumField(
                    name = "namePlacement",
                    default = defaults.namePlacement,
                ) { value -> drawerNamePlacementFromStorageValue(value) } ?: return null,
                itemsPerRow = displaySettings.optInt(
                    "itemsPerRow",
                    defaults.itemsPerRow,
                ),
                sectionAnchorPresentation = displaySettings.enumField(
                    name = "sectionAnchorPresentation",
                    default = defaults.sectionAnchorPresentation,
                ) { value ->
                    drawerSectionAnchorPresentationFromStorageValue(value)
                } ?: return null,
                // Backups written before the opacity contract carried a transparent /
                // frosted-glass mode choice under "backgroundMode"; that choice is
                // intentionally not mapped and a missing opacity resolves to the
                // contracted default.
                backgroundOpacity = displaySettings.optInt(
                    "backgroundOpacity",
                    defaults.backgroundOpacity,
                ),
            )
        } catch (_: IllegalArgumentException) {
            null
        }
    }

    /**
     * Reads one enum-like field. A missing field keeps its default value; a present
     * but unrecognized value fails the whole backup interpretation.
     */
    private inline fun <T> JSONObject.enumField(
        name: String,
        default: T,
        fromStorageValue: (String) -> T?,
    ): T? = if (has(name)) fromStorageValue(getString(name)) else default

    private fun orderedFavoriteModuleTypeFromStorageValue(
        value: String,
    ): OrderedFavoriteModuleType? = when (value) {
        "vertical" -> OrderedFavoriteModuleType.Vertical
        "ribbon" -> OrderedFavoriteModuleType.Ribbon
        else -> null
    }

    private fun favoriteListSizeFromStorageValue(value: String): FavoriteListSize? =
        when (value) {
            "large" -> FavoriteListSize.Large
            "medium" -> FavoriteListSize.Medium
            "small" -> FavoriteListSize.Small
            else -> null
        }

    private fun favoriteNamePlacementFromStorageValue(value: String): FavoriteNamePlacement? =
        when (value) {
            "right" -> FavoriteNamePlacement.Right
            "below" -> FavoriteNamePlacement.Below
            else -> null
        }

    private val DrawerApplicationSize.storageValue: String
        get() = when (this) {
            DrawerApplicationSize.Large -> "large"
            DrawerApplicationSize.Medium -> "medium"
            DrawerApplicationSize.Small -> "small"
        }

    private val FavoriteListSize.storageValue: String
        get() = when (this) {
            FavoriteListSize.Large -> "large"
            FavoriteListSize.Medium -> "medium"
            FavoriteListSize.Small -> "small"
        }

    private val FavoriteNamePlacement.storageValue: String
        get() = when (this) {
            FavoriteNamePlacement.Right -> "right"
            FavoriteNamePlacement.Below -> "below"
        }

    private val OrderedFavoriteModuleType.storageValue: String
        get() = when (this) {
            OrderedFavoriteModuleType.Vertical -> "vertical"
            OrderedFavoriteModuleType.Ribbon -> "ribbon"
        }

    private val DrawerNamePlacement.storageValue: String
        get() = when (this) {
            DrawerNamePlacement.Right -> "right"
            DrawerNamePlacement.Below -> "below"
        }

    private val DrawerSectionAnchorPresentation.storageValue: String
        get() = when (this) {
            DrawerSectionAnchorPresentation.Inline -> "inline"
            DrawerSectionAnchorPresentation.LeftSide -> "left_side"
        }

    private fun drawerApplicationSizeFromStorageValue(
        value: String,
    ): DrawerApplicationSize? = when (value) {
        "large" -> DrawerApplicationSize.Large
        "medium" -> DrawerApplicationSize.Medium
        "small" -> DrawerApplicationSize.Small
        else -> null
    }

    private fun drawerNamePlacementFromStorageValue(value: String): DrawerNamePlacement? =
        when (value) {
            "right" -> DrawerNamePlacement.Right
            "below" -> DrawerNamePlacement.Below
            else -> null
        }

    private fun drawerSectionAnchorPresentationFromStorageValue(
        value: String,
    ): DrawerSectionAnchorPresentation? = when (value) {
        "inline" -> DrawerSectionAnchorPresentation.Inline
        "left_side" -> DrawerSectionAnchorPresentation.LeftSide
        else -> null
    }
}

/**
 * Replaces the complete current state with a parsed backup as one coordinated
 * operation. The favorites store is replaced first; if the display-settings
 * replacement fails, the previous favorites state is written back so no mixed state
 * remains.
 */
internal class SettingsBackupRestoreCoordinator(
    private val favorites: BackupFavoritesAccess,
    private val settings: BackupSettingsAccess,
) {
    suspend fun restore(backup: SettingsBackupState): Boolean {
        val previousFavorites = favorites.currentOrderedAggregate() ?: return false
        val previousSettings = settings.currentSettings() ?: return false
        if (backup.settings == previousSettings) {
            return favorites.restoreAggregate(backup.aggregate)
        }
        if (!favorites.restoreAggregate(backup.aggregate)) return false
        if (!settings.restoreSettings(backup.settings)) {
            favorites.restoreAggregate(previousFavorites)
            return false
        }
        return true
    }
}

/** UI-facing operations for the Settings Data backup and restore entries. */
internal interface SettingsBackupControl {
    fun suggestedFileName(): String

    suspend fun writeBackup(uri: Uri): Boolean

    /** Returns the parsed backup, or null when the file is invalid or unreadable. */
    suspend fun readBackup(uri: Uri): SettingsBackupState?

    suspend fun restore(backup: SettingsBackupState): Boolean
}

internal class SettingsBackupController(
    private val context: Context,
    private val favorites: BackupFavoritesAccess,
    private val settings: BackupSettingsAccess,
) : SettingsBackupControl {
    private val coordinator = SettingsBackupRestoreCoordinator(
        favorites = favorites,
        settings = settings,
    )

    override fun suggestedFileName(): String {
        val versionName = try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: ""
        } catch (_: Exception) {
            ""
        }
        return backupFileName(
            versionName = versionName,
            timestamp = LocalDateTime.now(),
        )
    }

    override suspend fun writeBackup(uri: Uri): Boolean = withContext(context = Dispatchers.IO) {
        try {
            val aggregate = favorites.currentOrderedAggregate() ?: return@withContext false
            val currentSettings = settings.currentSettings() ?: return@withContext false
            val document = SettingsBackupJson.serialize(
                aggregate = aggregate,
                settings = currentSettings,
            )
            context.contentResolver.openOutputStream(uri, WRITE_MODE)?.use { output ->
                output.write(document.toByteArray())
                output.flush()
            } ?: return@withContext false
            true
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: Exception) {
            false
        }
    }

    override suspend fun readBackup(uri: Uri): SettingsBackupState? =
        withContext(context = Dispatchers.IO) {
            try {
                val content = context.contentResolver.openInputStream(uri)?.use { input ->
                    input.readBytes().toString(Charsets.UTF_8)
                } ?: return@withContext null
                SettingsBackupJson.parse(text = content)
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                null
            }
        }

    override suspend fun restore(backup: SettingsBackupState): Boolean =
        coordinator.restore(backup = backup)

    private companion object {
        const val WRITE_MODE = "wt"
    }
}
