package com.avenor.launcher

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource

@Composable
internal fun AvenorTheme(content: @Composable () -> Unit) {
    val transparent = colorResource(R.color.avenor_transparent)
    val foreground = colorResource(R.color.avenor_foreground)
    val secondaryForeground = colorResource(R.color.avenor_secondary_foreground)
    val sheetSurface = colorResource(R.color.avenor_sheet_surface)
    val sheetScrim = colorResource(R.color.avenor_sheet_scrim)
    val error = colorResource(R.color.avenor_error)
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = transparent,
            error = error,
            surface = sheetSurface,
            onBackground = foreground,
            onSurface = foreground,
            onSurfaceVariant = secondaryForeground,
            scrim = sheetScrim,
        ),
        content = content,
    )
}
