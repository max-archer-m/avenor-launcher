package com.avenor.launcher

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource

@Composable
internal fun AvenorTheme(content: @Composable () -> Unit) {
    val transparent = colorResource(R.color.avenor_transparent)
    val foreground = colorResource(R.color.avenor_foreground)
    val sheetSurface = colorResource(R.color.avenor_sheet_surface)
    val sheetScrim = colorResource(R.color.avenor_sheet_scrim)
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = transparent,
            surface = sheetSurface,
            onBackground = foreground,
            onSurface = foreground,
            scrim = sheetScrim,
        ),
        content = content,
    )
}
