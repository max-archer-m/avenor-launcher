package com.avenor.launcher

import android.app.role.RoleManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

internal interface SettingsPlatform {
    fun isDefaultHome(): Boolean
    fun openDefaultHomeSettings(): Boolean
    fun openProjectRepository(): Boolean
    fun versionText(): String
}

internal object EmptySettingsPlatform : SettingsPlatform {
    override fun isDefaultHome() = false
    override fun openDefaultHomeSettings() = false
    override fun openProjectRepository() = false
    override fun versionText() = ""
}

internal class AndroidSettingsPlatform(context: Context) : SettingsPlatform {
    private val applicationContext = context.applicationContext
    private val roleManager = applicationContext.getSystemService(RoleManager::class.java)

    override fun isDefaultHome(): Boolean =
        roleManager?.let { manager ->
            manager.isRoleAvailable(RoleManager.ROLE_HOME) &&
                manager.isRoleHeld(RoleManager.ROLE_HOME)
        } == true

    override fun openDefaultHomeSettings(): Boolean = open(
        Intent(Settings.ACTION_HOME_SETTINGS),
    )

    override fun openProjectRepository(): Boolean = open(
        Intent(Intent.ACTION_VIEW, Uri.parse(PROJECT_REPOSITORY_URL)),
    )

    override fun versionText(): String {
        val packageInfo = applicationContext.packageManager.getPackageInfo(
            applicationContext.packageName,
            0,
        )
        return "v${packageInfo.versionName}(${packageInfo.longVersionCode})"
    }

    private fun open(intent: Intent): Boolean = try {
        applicationContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        true
    } catch (_: ActivityNotFoundException) {
        false
    } catch (_: SecurityException) {
        false
    }

    private companion object {
        const val PROJECT_REPOSITORY_URL = "https://github.com/max-archer-m/avenor-launcher"
    }
}

internal fun readAvenorLicense(context: Context): String =
    context.resources.openRawResource(R.raw.avenor_license)
        .bufferedReader(Charsets.UTF_8)
        .use { reader -> reader.readText() }
