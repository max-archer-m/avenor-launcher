package com.avenor.launcher

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.pm.LauncherApps
import android.graphics.Rect
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.content.getSystemService
import androidx.core.graphics.drawable.toBitmap

internal fun interface ApplicationInformationLauncher {
    fun open(entry: LaunchableEntry): Boolean
}

internal class AndroidApplicationInformationLauncher(context: Context) :
    ApplicationInformationLauncher {
    private val launcherApps = checkNotNull(context.applicationContext.getSystemService<LauncherApps>())

    override fun open(entry: LaunchableEntry): Boolean = try {
        launcherApps.startAppDetailsActivity(
            entry.identity.componentName,
            entry.user,
            Rect(),
            null,
        )
        true
    } catch (_: SecurityException) {
        false
    } catch (_: IllegalStateException) {
        false
    } catch (_: ActivityNotFoundException) {
        false
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun ApplicationActionSheet(
    entry: LaunchableEntry,
    favoriteState: FavoriteReadState,
    onDismiss: () -> Unit,
    onAddFavorite: () -> Unit,
    onRemoveFavorite: () -> Unit,
    onEditFavorites: () -> Unit = {},
    canEditFavorites: Boolean = false,
    informationLauncher: ApplicationInformationLauncher,
    shortcuts: List<ApplicationShortcut> = emptyList(),
    onShortcut: (ApplicationShortcut) -> Unit = {},
) {
    val context = LocalContext.current
    val disabledAlpha = integerResource(R.integer.disabled_content_alpha_percent) / 100f
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val showsEdit = canEditFavorites &&
        favoriteState is FavoriteReadState.Readable &&
        entry.identity in favoriteState.identities &&
        favoriteState.identities.size >= 2
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        scrimColor = MaterialTheme.colorScheme.scrim,
        dragHandle = {
            Box(
                Modifier
                    .padding(vertical = dimensionResource(R.dimen.action_sheet_handle_padding))
                    .size(
                        width = dimensionResource(R.dimen.action_sheet_handle_width),
                        height = dimensionResource(R.dimen.action_sheet_handle_height),
                    )
                    .background(
                        color = MaterialTheme.colorScheme.onSurface,
                        shape = RoundedCornerShape(
                            dimensionResource(R.dimen.action_sheet_handle_height),
                        ),
                    )
                    .testTag("application_action_sheet_handle"),
            )
        },
        modifier = Modifier.testTag("application_action_sheet"),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clipToBounds(),
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimensionResource(R.dimen.action_sheet_horizontal_padding)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = entry.label,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("application_action_sheet_name"),
                        style = MaterialTheme.typography.titleLarge,
                    )
                    IconButton(
                        onClick = {
                            val opened = informationLauncher.open(entry)
                            onDismiss()
                            if (!opened) {
                                Toast.makeText(
                                    context,
                                    R.string.application_information_unavailable,
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        },
                        modifier = Modifier.testTag("application_information_action"),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_application_information),
                            contentDescription = stringResource(R.string.application_information),
                        )
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.padding(
                        horizontal = dimensionResource(R.dimen.action_sheet_divider_inset),
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (shortcuts.isNotEmpty()) {
                    ApplicationShortcutRegion(
                        modifier = Modifier.weight(1f, fill = false),
                        entry = entry,
                        shortcuts = shortcuts,
                        onShortcut = onShortcut,
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = dimensionResource(R.dimen.action_sheet_actions_vertical_padding)),
                    horizontalArrangement = Arrangement.Start,
                ) {
                    when (favoriteState) {
                        FavoriteReadState.Loading,
                        FavoriteReadState.ReadFailure,
                        -> FavoriteActionSlot(
                            label = stringResource(R.string.favorites_unavailable),
                            enabled = false,
                            disabledAlpha = disabledAlpha,
                            onClick = {},
                        )

                        is FavoriteReadState.Readable -> if (
                            entry.identity !in favoriteState.identities
                        ) {
                            FavoriteActionSlot(
                                label = stringResource(R.string.add_favorite),
                                enabled = true,
                                disabledAlpha = disabledAlpha,
                                onClick = onAddFavorite,
                            )
                        } else {
                            FavoriteActionSlot(
                                label = stringResource(R.string.remove_favorite),
                                icon = R.drawable.ic_cancel_favorite,
                                enabled = true,
                                disabledAlpha = disabledAlpha,
                                onClick = onRemoveFavorite,
                            )
                            if (showsEdit) {
                                FavoriteActionSlot(
                                    label = stringResource(R.string.edit_favorites),
                                    icon = R.drawable.ic_edit_favorites,
                                    enabled = true,
                                    disabledAlpha = disabledAlpha,
                                    onClick = onEditFavorites,
                                    testTag = "edit_favorites_action",
                                )
                            }
                        }
                    }
                    repeat(if (showsEdit) 3 else 4) { Spacer(Modifier.weight(1f)) }
                }
            }

            entry.profileBadge?.let { badge ->
                val badgeSize = dimensionResource(R.dimen.action_sheet_badge_size)
                val pixels = with(LocalDensity.current) { badgeSize.roundToPx() }
                val bitmap = badge.toBitmap(pixels, pixels).asImageBitmap()
                androidx.compose.foundation.Image(
                    bitmap = bitmap,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(badgeSize)
                        .testTag("application_action_sheet_profile_badge"),
                )
            }
        }
    }
}

@Composable
private fun RowScope.FavoriteActionSlot(
    label: String,
    @DrawableRes
    icon: Int = R.drawable.ic_add_favorite,
    enabled: Boolean,
    disabledAlpha: Float,
    onClick: () -> Unit,
    testTag: String = "favorite_action",
) {
    androidx.compose.material3.TextButton(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = disabledAlpha),
        ),
        modifier = Modifier
            .weight(1f)
            .testTag(testTag),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(dimensionResource(R.dimen.action_sheet_action_icon_size)),
            )
            Spacer(Modifier.height(dimensionResource(R.dimen.action_sheet_icon_label_gap)))
            Text(text = label, maxLines = 1)
        }
    }
}

@Composable
private fun ApplicationShortcutRegion(
    modifier: Modifier = Modifier,
    entry: LaunchableEntry,
    shortcuts: List<ApplicationShortcut>,
    onShortcut: (ApplicationShortcut) -> Unit,
) {
    val iconSize = dimensionResource(R.dimen.action_sheet_shortcut_icon_size)
    val iconPixels = with(LocalDensity.current) { iconSize.roundToPx() }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .testTag("application_shortcut_region"),
    ) {
        shortcuts.forEach { shortcut ->
            val iconDrawable = shortcut.icon ?: entry.icon
            val bitmap = androidx.compose.runtime.remember(
                shortcut.packageName,
                shortcut.shortcutId,
                iconDrawable,
                iconPixels,
            ) {
                iconDrawable.toBitmap(iconPixels, iconPixels).asImageBitmap()
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(
                        min = dimensionResource(R.dimen.action_sheet_shortcut_row_min_height),
                    )
                    .clickable(role = Role.Button) { onShortcut(shortcut) }
                    .padding(
                        horizontal = dimensionResource(R.dimen.action_sheet_horizontal_padding),
                    )
                    .testTag("application_shortcut_${shortcut.shortcutId}"),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                androidx.compose.foundation.Image(
                    bitmap = bitmap,
                    contentDescription = null,
                    modifier = Modifier.size(iconSize),
                )
                Spacer(
                    Modifier.size(
                        dimensionResource(R.dimen.action_sheet_shortcut_icon_label_gap),
                    ),
                )
                Text(
                    text = shortcut.label,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
    HorizontalDivider(
        modifier = Modifier.padding(
            horizontal = dimensionResource(R.dimen.action_sheet_divider_inset),
        ),
        color = MaterialTheme.colorScheme.onSurface,
    )
}
