package com.avenor.launcher

import android.app.Application
import android.content.Context
import com.avenor.launcher.ui.drawer.DrawerDisplaySettingsStore
import com.avenor.launcher.ui.settings.AndroidSettingsPlatform
import com.avenor.launcher.ui.settings.readAvenorLicense

/**
 * Process-wide owner of the stores and platform adapters [AvenorApp] consumes. Holding
 * them outside the composition keeps their in-memory state readable across Activity
 * recreation, so returning to Home never re-reads the durable files or re-queries the
 * inventory while the process is alive.
 */
class AvenorApplication : Application() {
    internal val avenorGraph: AvenorGraph by lazy { AvenorGraph(context = this) }
}

internal class AvenorGraph(context: Context) {
    val inventoryLoader: AndroidLaunchableInventoryLoader =
        AndroidLaunchableInventoryLoader(context = context)
    val entryLauncher: AndroidLaunchableEntryLauncher =
        AndroidLaunchableEntryLauncher(context = context)
    val favoriteStore: OrderedFavoriteStoreAdapter =
        OrderedFavoriteStoreAdapter(context = context)
    val drawerDisplaySettingsStore: DrawerDisplaySettingsStore =
        DrawerDisplaySettingsStore(context = context)
    val informationLauncher: AndroidApplicationInformationLauncher =
        AndroidApplicationInformationLauncher(context = context)
    val uninstallLauncher: AndroidApplicationUninstallLauncher =
        AndroidApplicationUninstallLauncher(context = context)
    val shortcutController: AndroidApplicationShortcutController =
        AndroidApplicationShortcutController(context = context)
    val settingsPlatform: AndroidSettingsPlatform =
        AndroidSettingsPlatform(context = context)
    val licenseText: String = readAvenorLicense(context = context)
    val accessibilityLockController: AccessibilityLockController =
        if (BuildConfig.DEBUG) {
            AndroidAccessibilityLockController(
                context = context,
                serviceComponent = debugAccessibilityLockServiceComponent(context = context),
            )
        } else {
            EmptyAccessibilityLockController
        }

    init {
        // One synchronous read per process puts the durable favorites and display settings
        // in front of the first frame; later reloads stay async and mutex-guarded.
        favoriteStore.loadBlocking()
        drawerDisplaySettingsStore.loadBlocking()
    }
}
