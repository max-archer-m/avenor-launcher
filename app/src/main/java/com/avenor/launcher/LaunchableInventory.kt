package com.avenor.launcher

import android.content.ComponentName
import android.content.Context
import android.content.pm.LauncherApps
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.os.Process
import android.os.UserHandle
import android.os.UserManager
import androidx.core.content.getSystemService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal data class LaunchableIdentity(
    val profileSerialNumber: Long,
    val componentName: ComponentName,
)

internal data class LaunchableEntry(
    val identity: LaunchableIdentity,
    val user: UserHandle,
    val label: String,
    val icon: Drawable,
)

internal fun interface LaunchableInventoryLoader {
    suspend fun load(): List<LaunchableEntry>
}

internal fun interface LaunchableInventoryObservation {
    fun stop()
}

internal fun interface LaunchableInventoryMonitor {
    fun observe(onInventoryChanged: () -> Unit): LaunchableInventoryObservation
}

internal class AndroidLaunchableInventoryLoader(
    context: Context,
    private val iconRenderer: LauncherIconRenderer = SystemLauncherIconRenderer(context),
    private val iconAppearance: LauncherIconAppearance = LauncherIconAppearance(),
) : LaunchableInventoryLoader, LaunchableInventoryMonitor {
    private val applicationContext = context.applicationContext
    private val launcherApps = checkNotNull(applicationContext.getSystemService<LauncherApps>())
    private val userManager = checkNotNull(applicationContext.getSystemService<UserManager>())

    override suspend fun load(): List<LaunchableEntry> = withContext(Dispatchers.IO) {
        val density = applicationContext.resources.displayMetrics.densityDpi
        val locale = applicationContext.resources.configuration.locales[0]
        val entryComparator = LaunchableEntryComparator(locale)

        val currentUser = Process.myUserHandle()

        launcherApps.profiles
            .flatMap { user ->
                val serialNumber = userManager.getSerialNumberForUser(user)
                val activities = try {
                    launcherApps.getActivityList(null, user)
                } catch (failure: IllegalStateException) {
                    if (user == currentUser) throw failure else emptyList()
                } catch (failure: SecurityException) {
                    if (user == currentUser) throw failure else emptyList()
                }

                activities.map { activity ->
                    val fallbackIcon = applicationContext.packageManager.defaultActivityIcon
                    val sourceIcon = runCatching {
                        activity.getIcon(density)
                    }.getOrDefault(fallbackIcon)
                    val icon = runCatching {
                        iconRenderer.render(sourceIcon, user, iconAppearance)
                    }.getOrElse {
                        iconRenderer.render(fallbackIcon, user, iconAppearance)
                    }

                    LaunchableEntry(
                        identity = LaunchableIdentity(
                            profileSerialNumber = serialNumber,
                            componentName = activity.componentName,
                        ),
                        user = user,
                        label = activity.label.toString(),
                        icon = icon,
                    )
                }
            }
            .distinctBy(LaunchableEntry::identity)
            .sortedWith(entryComparator)
    }

    override fun observe(
        onInventoryChanged: () -> Unit,
    ): LaunchableInventoryObservation {
        val callback = object : LauncherApps.Callback() {
            override fun onPackageAdded(packageName: String, user: UserHandle) {
                onInventoryChanged()
            }

            override fun onPackageChanged(packageName: String, user: UserHandle) {
                onInventoryChanged()
            }

            override fun onPackageRemoved(packageName: String, user: UserHandle) {
                onInventoryChanged()
            }

            override fun onPackagesAvailable(
                packageNames: Array<out String>,
                user: UserHandle,
                replacing: Boolean,
            ) {
                onInventoryChanged()
            }

            override fun onPackagesUnavailable(
                packageNames: Array<out String>,
                user: UserHandle,
                replacing: Boolean,
            ) {
                onInventoryChanged()
            }

            override fun onPackagesSuspended(packageNames: Array<out String>, user: UserHandle) {
                onInventoryChanged()
            }

            override fun onPackagesUnsuspended(packageNames: Array<out String>, user: UserHandle) {
                onInventoryChanged()
            }
        }

        launcherApps.registerCallback(callback, Handler(Looper.getMainLooper()))
        return LaunchableInventoryObservation {
            launcherApps.unregisterCallback(callback)
        }
    }
}
