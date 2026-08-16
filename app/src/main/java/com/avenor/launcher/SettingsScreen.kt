package com.avenor.launcher

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
internal fun SettingsScreen(
    platform: SettingsPlatform,
    licenseText: String,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var isDefaultHome by remember(platform) { mutableStateOf(platform.isDefaultHome()) }
    var showLicense by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner, platform) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isDefaultHome = platform.isDefaultHome()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("settings_surface"),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
        ) {
            SettingsTopBar(onBack = onBack)
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant)
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("settings_list"),
            ) {
                item(key = "default-home") {
                    PrimarySettingsItem(
                        title = stringResource(R.string.default_home_application),
                        supportingText = stringResource(
                            if (isDefaultHome) {
                                R.string.avenor_is_default_launcher
                            } else {
                                R.string.avenor_is_not_default_launcher
                            },
                        ),
                        onClick = { platform.openDefaultHomeSettings() },
                        testTag = "settings_default_home",
                    )
                }
                item(key = "license") {
                    SecondarySettingsItem(
                        text = stringResource(R.string.avenor_license),
                        onClick = { showLicense = true },
                        testTag = "settings_license",
                    )
                }
                item(key = "repository") {
                    SecondarySettingsItem(
                        text = stringResource(R.string.project_repository),
                        onClick = {
                            if (!platform.openProjectRepository()) {
                                Toast.makeText(
                                    context,
                                    R.string.unable_to_open_project_link,
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        },
                        testTag = "settings_project_repository",
                    )
                }
                item(key = "version") {
                    SecondarySettingsItem(
                        text = platform.versionText(),
                        onClick = null,
                        testTag = "settings_version",
                    )
                }
            }
        }
    }

    if (showLicense) {
        LicenseBottomSheet(
            licenseText = licenseText,
            onDismiss = { showLicense = false },
        )
    }
}

@Composable
private fun SettingsTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = dimensionResource(R.dimen.settings_top_bar_min_height))
            .padding(horizontal = dimensionResource(R.dimen.settings_horizontal_padding)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.testTag("settings_back"),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_back),
                contentDescription = stringResource(R.string.back),
            )
        }
        Text(
            text = stringResource(R.string.settings),
            modifier = Modifier.testTag("settings_title"),
            style = MaterialTheme.typography.titleLarge,
        )
    }
}

@Composable
private fun PrimarySettingsItem(
    title: String,
    supportingText: String?,
    onClick: () -> Unit,
    testTag: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(
                min = dimensionResource(
                    if (supportingText == null) {
                        R.dimen.settings_primary_one_line_min_height
                    } else {
                        R.dimen.settings_primary_two_line_min_height
                    },
                ),
            )
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = dimensionResource(R.dimen.settings_horizontal_padding))
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
            )
            supportingText?.let { text ->
                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        Icon(
            painter = painterResource(R.drawable.ic_chevron_right),
            contentDescription = null,
            modifier = Modifier.size(dimensionResource(R.dimen.settings_trailing_icon_size)),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SecondarySettingsItem(
    text: String,
    onClick: (() -> Unit)?,
    testTag: String,
) {
    val interactionModifier = if (onClick == null) {
        Modifier
    } else {
        Modifier.clickable(role = Role.Button, onClick = onClick)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = dimensionResource(R.dimen.settings_secondary_item_min_height))
            .then(interactionModifier)
            .padding(horizontal = dimensionResource(R.dimen.settings_horizontal_padding))
            .testTag(testTag),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleSmall,
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun LicenseBottomSheet(
    licenseText: String,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        scrimColor = MaterialTheme.colorScheme.scrim,
        dragHandle = {
            androidx.compose.foundation.layout.Box(
                Modifier
                    .padding(vertical = dimensionResource(R.dimen.action_sheet_handle_padding))
                    .size(
                        width = dimensionResource(R.dimen.action_sheet_handle_width),
                        height = dimensionResource(R.dimen.action_sheet_handle_height),
                    )
                    .background(
                        color = MaterialTheme.colorScheme.onSurface,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(
                            dimensionResource(R.dimen.action_sheet_handle_height),
                        ),
                    ),
            )
        },
        modifier = Modifier.testTag("avenor_license_sheet"),
    ) {
        Text(
            text = stringResource(R.string.avenor_license_title),
            modifier = Modifier.padding(
                horizontal = dimensionResource(R.dimen.settings_horizontal_padding),
                vertical = dimensionResource(R.dimen.settings_license_title_vertical_padding),
            ),
            style = MaterialTheme.typography.titleLarge,
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = licenseText,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState())
                .padding(dimensionResource(R.dimen.settings_horizontal_padding))
                .testTag("avenor_license_text"),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}
