package com.avenor.launcher

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource

internal enum class AvenorSurface {
    Home,
    Drawer,
}

@Composable
internal fun AvenorApp() {
    val context = LocalContext.current
    val inventoryLoader = remember(context) {
        AndroidLaunchableInventoryLoader(context)
    }
    val entryLauncher = remember(context) {
        AndroidLaunchableEntryLauncher(context)
    }
    AvenorApp(inventoryLoader, entryLauncher)
}

@Composable
internal fun AvenorApp(
    inventoryLoader: LaunchableInventoryLoader,
    entryLauncher: LaunchableEntryLauncher = LaunchableEntryLauncher { false },
) {
    var surface by remember { mutableStateOf(AvenorSurface.Home) }

    BackHandler(enabled = surface == AvenorSurface.Drawer) {
        surface = AvenorSurface.Home
    }

    when (surface) {
        AvenorSurface.Home -> HomeWithDrawerEntry(
            onOpenDrawer = { surface = AvenorSurface.Drawer },
        )

        AvenorSurface.Drawer -> DrawerScreen(
            inventoryLoader = inventoryLoader,
            entryLauncher = entryLauncher,
        )
    }
}

@Composable
private fun HomeWithDrawerEntry(onOpenDrawer: () -> Unit) {
    val threshold = with(LocalDensity.current) {
        dimensionResource(R.dimen.drawer_entry_drag_threshold).toPx()
    }
    var upwardDrag by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_surface")
            .pointerInput(threshold) {
                detectVerticalDragGestures(
                    onDragStart = { upwardDrag = 0f },
                    onVerticalDrag = { _, dragAmount ->
                        upwardDrag = (upwardDrag - dragAmount).coerceAtLeast(0f)
                    },
                    onDragCancel = { upwardDrag = 0f },
                    onDragEnd = {
                        if (upwardDrag >= threshold) onOpenDrawer()
                        upwardDrag = 0f
                    },
                )
            },
    ) {
        HomeScreen()
    }
}
