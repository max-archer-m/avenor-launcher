package com.avenor.launcher

import android.content.ComponentName
import android.content.Context
import android.graphics.drawable.Drawable
import android.icu.text.Transliterator
import android.os.Process
import android.os.UserHandle
import android.os.UserManager
import androidx.core.content.getSystemService
import java.text.Collator
import java.util.Locale
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

internal class AndroidLaunchableInventoryLoader(
    context: Context,
    private val iconRenderer: LauncherIconRenderer = SystemLauncherIconRenderer(context),
    private val iconAppearance: LauncherIconAppearance = LauncherIconAppearance(),
) : LaunchableInventoryLoader {
    private val applicationContext = context.applicationContext

    override suspend fun load(): List<LaunchableEntry> = withContext(Dispatchers.IO) {
        val launcherApps = checkNotNull(
            applicationContext.getSystemService<android.content.pm.LauncherApps>(),
        )
        val userManager = checkNotNull(applicationContext.getSystemService<UserManager>())
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
}

internal class LaunchableEntryComparator(locale: Locale) : Comparator<LaunchableEntry> {
    private val transliterator = Transliterator.getInstance("Han-Latin; Latin-ASCII; Lower")
    private val collator = Collator.getInstance(locale).apply {
        strength = Collator.PRIMARY
    }

    override fun compare(left: LaunchableEntry, right: LaunchableEntry): Int {
        val labelOrder = collator.compare(
            transliterator.transliterate(left.label),
            transliterator.transliterate(right.label),
        )
        if (labelOrder != 0) return labelOrder

        return compareValuesBy(
            left,
            right,
            { it.identity.profileSerialNumber },
            { it.identity.componentName.flattenToString() },
        )
    }
}
