package com.avenor.launcher

import android.app.role.RoleManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        installHomeBackBehavior()
        setContent {
            AvenorTheme {
                AvenorApp()
            }
        }
    }

    private fun installHomeBackBehavior() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val roleManager = getSystemService(RoleManager::class.java)
                    if (roleManager?.isRoleHeld(RoleManager.ROLE_HOME) == true) return

                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                }
            },
        )
    }
}
