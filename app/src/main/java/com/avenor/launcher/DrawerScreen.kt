package com.avenor.launcher

import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.CancellationException

private sealed interface DrawerUiState {
    data object Loading : DrawerUiState

    data class Content(val entries: List<LaunchableEntry>) : DrawerUiState

    data object Error : DrawerUiState
}

@Composable
internal fun DrawerScreen(
    inventoryLoader: LaunchableInventoryLoader,
    entryLauncher: LaunchableEntryLauncher = LaunchableEntryLauncher { false },
) {
    var reloadRequest by remember { mutableIntStateOf(0) }
    var state by remember(inventoryLoader) {
        mutableStateOf<DrawerUiState>(DrawerUiState.Loading)
    }
    val activationGuard = remember { RapidActivationGuard() }
    val context = LocalContext.current
    val launchFailureMessage = stringResource(R.string.application_unable_to_open)

    LaunchedEffect(inventoryLoader, reloadRequest) {
        state = DrawerUiState.Loading
        state = try {
            val entries = inventoryLoader.load()
            if (entries.isEmpty()) DrawerUiState.Error else DrawerUiState.Content(entries)
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: Exception) {
            DrawerUiState.Error
        }
    }

    when (val currentState = state) {
        DrawerUiState.Loading -> DrawerMessage(
            message = stringResource(R.string.drawer_loading_applications),
            showProgress = true,
            action = null,
            testTag = "drawer_loading",
        )

        DrawerUiState.Error -> DrawerMessage(
            message = stringResource(R.string.drawer_unable_to_load_applications),
            showProgress = false,
            showErrorIcon = true,
            action = {
                TextButton(
                    onClick = {
                        reloadRequest += 1
                    },
                ) {
                    Text(stringResource(R.string.retry))
                }
            },
            testTag = "drawer_error",
        )

        is DrawerUiState.Content -> DrawerApplicationList(
            entries = currentState.entries,
            onLaunch = { entry ->
                if (activationGuard.tryAcquire() && !entryLauncher.launch(entry)) {
                    Toast.makeText(context, launchFailureMessage, Toast.LENGTH_SHORT).show()
                }
            },
        )
    }
}

@Composable
private fun DrawerMessage(
    message: String,
    showProgress: Boolean,
    showErrorIcon: Boolean = false,
    action: (@Composable () -> Unit)?,
    testTag: String,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if (showProgress) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        if (showErrorIcon) {
            Icon(
                painter = painterResource(R.drawable.ic_inventory_error),
                contentDescription = null,
                modifier = Modifier
                    .size(dimensionResource(R.dimen.drawer_error_icon_size))
                    .testTag("drawer_error_icon"),
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge,
        )
        action?.invoke()
    }
}

@Composable
private fun DrawerApplicationList(
    entries: List<LaunchableEntry>,
    onLaunch: (LaunchableEntry) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .testTag("drawer_application_list"),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = dimensionResource(R.dimen.drawer_horizontal_padding),
        ),
    ) {
        items(
            items = entries,
            key = { entry ->
                "${entry.identity.profileSerialNumber}:${entry.identity.componentName.flattenToString()}"
            },
        ) { entry ->
            DrawerApplicationRow(entry, onLaunch)
        }
    }
}

@Composable
private fun DrawerApplicationRow(
    entry: LaunchableEntry,
    onLaunch: (LaunchableEntry) -> Unit,
) {
    val iconSize = dimensionResource(R.dimen.drawer_application_icon_size)
    val iconSizePixels = with(LocalDensity.current) { iconSize.roundToPx() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = dimensionResource(R.dimen.drawer_application_row_min_height))
            .clickable(
                role = Role.Button,
                onClick = { onLaunch(entry) },
            )
            .testTag("drawer_application_row"),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DrawerApplicationIcon(entry.icon, iconSizePixels)
        Spacer(Modifier.width(dimensionResource(R.dimen.drawer_application_icon_label_gap)))
        Text(
            text = entry.label,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun DrawerApplicationIcon(icon: Drawable, iconSizePixels: Int) {
    val bitmap = remember(icon, iconSizePixels) {
        icon.toBitmap(
            width = iconSizePixels,
            height = iconSizePixels,
        ).asImageBitmap()
    }

    Image(
        bitmap = bitmap,
        contentDescription = null,
        modifier = Modifier.size(dimensionResource(R.dimen.drawer_application_icon_size)),
    )
}
