package com.avenor.launcher

import android.content.res.Configuration
import androidx.test.core.app.ApplicationProvider
import java.util.Locale
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsBackupStringParityTest {
    @Test
    fun dataStringsResolveInEnglishAndSimplifiedChinese() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val english = context.createConfigurationContext(
            Configuration().apply { setLocale(Locale.US) },
        )
        val simplifiedChinese = context.createConfigurationContext(
            Configuration().apply { setLocale(Locale.SIMPLIFIED_CHINESE) },
        )
        val identifiers = listOf(
            R.string.settings_data_backup,
            R.string.settings_data_restore,
            R.string.settings_data_backup_success,
            R.string.settings_data_backup_failure,
            R.string.settings_data_restore_success,
            R.string.settings_data_restore_failure,
            R.string.settings_data_restore_dialog_title,
            R.string.settings_data_restore_dialog_body,
            R.string.settings_data_restore_confirm,
            R.string.privacy_statement,
        )

        identifiers.forEach { identifier ->
            assertTrue(english.getString(identifier).isNotBlank())
            assertTrue(simplifiedChinese.getString(identifier).isNotBlank())
        }
    }

    @Test
    fun privacyStatementDisclosesBackupAndRestoreInBothLanguages() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val english = context.createConfigurationContext(
            Configuration().apply { setLocale(Locale.US) },
        )
        val simplifiedChinese = context.createConfigurationContext(
            Configuration().apply { setLocale(Locale.SIMPLIFIED_CHINESE) },
        )

        val englishStatement = english.getString(R.string.privacy_statement)
        val chineseStatement = simplifiedChinese.getString(R.string.privacy_statement)

        assertTrue(englishStatement.contains("manual local backup"))
        assertTrue(englishStatement.contains("never uploaded"))
        assertTrue(chineseStatement.contains("手动本地备份"))
        assertTrue(chineseStatement.contains("绝不会被上传"))
    }
}
