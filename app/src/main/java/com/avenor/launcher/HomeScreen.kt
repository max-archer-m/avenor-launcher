package com.avenor.launcher

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.graphics.drawable.toBitmap
import java.time.ZonedDateTime
import kotlinx.coroutines.delay

@Composable
internal fun HomeScreen(
    clock: () -> ZonedDateTime = { ZonedDateTime.now() },
    favoriteState: FavoriteReadState = FavoriteReadState.Readable(emptyList()),
    favoriteAvailability: Map<LaunchableIdentity, FavoriteAvailability> = emptyMap(),
    marqueePaused: Boolean = false,
    onRetryFavorites: () -> Unit = {},
    onLaunchFavorite: (FavoriteAvailability) -> Unit = {},
    onLongPressFavorite: (LaunchableEntry) -> Unit = {},
) {
    val context = LocalContext.current
    var now by remember { mutableStateOf(clock()) }

    LaunchedEffect(clock) {
        while (true) {
            val remainingMillis = 60_000L - (System.currentTimeMillis() % 60_000L)
            delay(remainingMillis)
            now = clock()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(
                horizontal = dimensionResource(R.dimen.home_horizontal_padding),
                vertical = dimensionResource(R.dimen.home_vertical_padding),
            ),
    ) {
        Text(
            text = HomeDateTimeFormatter.time(context, now),
            modifier = Modifier
                .heightIn(min = dimensionResource(R.dimen.home_time_min_height))
                .testTag("home_time")
                .clickable(role = Role.Button) {
                    context.launchClockDestination()
                },
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = dimensionResource(R.dimen.home_time_text_size).value.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = dimensionResource(R.dimen.home_time_line_height).value.sp,
            textAlign = TextAlign.Start,
        )
        Text(
            text = HomeDateTimeFormatter.dateAndWeekday(context, now),
            modifier = Modifier
                .heightIn(min = dimensionResource(R.dimen.home_date_height))
                .testTag("home_date")
                .clickable(role = Role.Button) {
                    val calendarUri = CalendarContract.CONTENT_URI
                        .buildUpon()
                        .appendPath("time")
                        .appendPath(now.toInstant().toEpochMilli().toString())
                        .build()
                    context.launchPlatformDestination(
                        intent = Intent(Intent.ACTION_VIEW, calendarUri),
                        failureMessage = R.string.calendar_unavailable,
                    )
                }
                .wrapContentHeight(align = Alignment.CenterVertically),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = dimensionResource(R.dimen.home_date_text_size).value.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = dimensionResource(R.dimen.home_date_line_height).value.sp,
            textAlign = TextAlign.Start,
        )
        when (favoriteState) {
            FavoriteReadState.Loading -> HomeFavoriteMessage(
                message = stringResource(R.string.loading_favorites),
                showProgress = true,
                onRetry = null,
            )

            FavoriteReadState.ReadFailure -> HomeFavoriteMessage(
                message = stringResource(R.string.unable_to_load_favorites),
                showProgress = false,
                onRetry = onRetryFavorites,
            )

            is FavoriteReadState.Readable -> {
                if (favoriteState.identities.isEmpty()) {
                    Text(
                        text = stringResource(R.string.home_empty_favorites),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.testTag("home_favorites_empty"),
                    )
                } else {
                    HomeFavoriteList(
                        identities = favoriteState.identities,
                        availabilityByIdentity = favoriteAvailability,
                        marqueePaused = marqueePaused,
                        onLaunchFavorite = onLaunchFavorite,
                        onLongPressFavorite = onLongPressFavorite,
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeFavoriteList(
    identities: List<LaunchableIdentity>,
    availabilityByIdentity: Map<LaunchableIdentity, FavoriteAvailability>,
    marqueePaused: Boolean,
    onLaunchFavorite: (FavoriteAvailability) -> Unit,
    onLongPressFavorite: (LaunchableEntry) -> Unit,
) {
    val listState = rememberLazyListState()
    val overflowingEntries = remember { mutableStateMapOf<String, Boolean>() }
    var pressedEntryKey by remember { mutableStateOf<String?>(null) }
    var focusedEntryKey by remember { mutableStateOf<String?>(null) }
    val centeredEntryKey by remember(listState, identities) {
        derivedStateOf {
            if (listState.isScrollInProgress) null else {
                val layoutInfo = listState.layoutInfo
                val viewportCenter =
                    (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2f
                layoutInfo.visibleItemsInfo
                    .mapNotNull { item ->
                        val identity = identities.getOrNull(item.index) ?: return@mapNotNull null
                        val key = identity.stableKey()
                        if (overflowingEntries[key] != true) return@mapNotNull null
                        val itemCenter = item.offset + item.size / 2f
                        key to kotlin.math.abs(itemCenter - viewportCenter)
                    }
                    .minByOrNull { it.second }
                    ?.first
            }
        }
    }
    val activeMarqueeKey = selectActiveMarqueeKey(
        paused = marqueePaused,
        scrolling = listState.isScrollInProgress,
        pressedKey = pressedEntryKey,
        focusedKey = focusedEntryKey,
        centeredKey = centeredEntryKey,
        overflowingKeys = overflowingEntries.keys,
    )

    LazyColumn(
        modifier = Modifier.testTag("home_favorites"),
        state = listState,
    ) {
        items(
            items = identities,
            key = { it.stableKey() },
        ) { identity ->
            val availability = availabilityByIdentity[identity]
                ?: FavoriteAvailability.Unknown(null)
            val entry = availability.presentationEntry
            val entryKey = identity.stableKey()
            HomeFavoriteRow(
                availability = availability,
                marqueeEligible = activeMarqueeKey == entryKey,
                onOverflowChanged = { overflow ->
                    if (overflow) overflowingEntries[entryKey] = true
                    else overflowingEntries.remove(entryKey)
                },
                onPressedChanged = { pressed ->
                    pressedEntryKey = if (pressed) entryKey else {
                        pressedEntryKey.takeUnless { it == entryKey }
                    }
                },
                onFocusedChanged = { focused ->
                    focusedEntryKey = if (focused) entryKey else {
                        focusedEntryKey.takeUnless { it == entryKey }
                    }
                },
                onClick = { onLaunchFavorite(availability) },
                onLongClick = {
                    if (entry != null) onLongPressFavorite(entry)
                },
            )
        }
    }
}

@Composable
private fun HomeFavoriteMessage(
    message: String,
    showProgress: Boolean,
    onRetry: (() -> Unit)?,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (showProgress) CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
        if (!showProgress) {
            Icon(
                painter = painterResource(R.drawable.ic_inventory_error),
                contentDescription = null,
                modifier = Modifier.size(dimensionResource(R.dimen.home_favorite_error_icon_size)),
            )
        }
        Text(text = message, color = MaterialTheme.colorScheme.onBackground)
        onRetry?.let { retry ->
            TextButton(onClick = retry) { Text(stringResource(R.string.retry)) }
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun HomeFavoriteRow(
    availability: FavoriteAvailability,
    marqueeEligible: Boolean,
    onOverflowChanged: (Boolean) -> Unit,
    onPressedChanged: (Boolean) -> Unit,
    onFocusedChanged: (Boolean) -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    val entry = availability.presentationEntry
    val iconSize = dimensionResource(R.dimen.home_favorite_icon_size)
    val disabledAlpha = integerResource(R.integer.disabled_content_alpha_percent) / 100f
    val iconPixels = with(androidx.compose.ui.platform.LocalDensity.current) { iconSize.roundToPx() }
    val interactionSource = remember(entry?.identity) { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    LaunchedEffect(isPressed) { onPressedChanged(isPressed) }
    val hapticFeedback = LocalHapticFeedback.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = dimensionResource(R.dimen.home_favorite_row_min_height))
            .combinedClickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                role = Role.Button,
                onClick = onClick,
                onLongClick = {
                    if (entry != null) {
                        hapticFeedback.performHapticFeedback(
                            androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress,
                        )
                        onLongClick()
                    }
                },
            )
            .onFocusChanged { onFocusedChanged(it.isFocused) }
            .alpha(if (availability is FavoriteAvailability.Available) 1f else disabledAlpha)
            .testTag("home_favorite_row"),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (entry == null) {
            Icon(
                painter = painterResource(R.drawable.ic_inventory_error),
                contentDescription = null,
                modifier = Modifier.size(iconSize),
                tint = MaterialTheme.colorScheme.onBackground,
            )
        } else {
            val bitmap = entry.iconBitmap?.asImageBitmap() ?: remember(entry.icon, iconPixels) {
                entry.icon.toBitmap(iconPixels, iconPixels).asImageBitmap()
            }
            Image(bitmap = bitmap, contentDescription = null, modifier = Modifier.size(iconSize))
        }
        Spacer(Modifier.width(dimensionResource(R.dimen.home_favorite_icon_label_gap)))
        val displayText = when (availability) {
            is FavoriteAvailability.Available -> availability.entry.label
            is FavoriteAvailability.Disabled -> entry?.let {
                stringResource(R.string.favorite_disabled_format, it.label)
            } ?: stringResource(R.string.favorite_application_disabled)
            is FavoriteAvailability.TemporarilyUnavailable,
            is FavoriteAvailability.Unknown,
            -> entry?.let {
                stringResource(R.string.favorite_unavailable_format, it.label)
            } ?: stringResource(R.string.favorite_application_unavailable)
            FavoriteAvailability.ConfirmedRemoved ->
                stringResource(R.string.favorite_application_unavailable)
        }
        SharedMarqueeText(
            text = displayText,
            eligible = marqueeEligible,
            onOverflowChanged = onOverflowChanged,
            modifier = Modifier.weight(1f),
        )
    }
}

private fun LaunchableIdentity.stableKey(): String =
    "$profileSerialNumber:${componentName.flattenToString()}"

private fun Context.launchClockDestination() {
    val alarmIntent = Intent(AlarmClock.ACTION_SHOW_ALARMS)
    val clockMainIntent = try {
        packageManager
            .resolveDefaultActivity(alarmIntent)
            ?.activityInfo
            ?.packageName
            ?.let { packageManager.getLaunchIntentForPackage(it) }
    } catch (_: SecurityException) {
        null
    }

    launchPlatformDestination(
        intent = clockMainIntent ?: alarmIntent,
        failureMessage = R.string.clock_unavailable,
    )
}

private fun PackageManager.resolveDefaultActivity(intent: Intent) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        resolveActivity(
            intent,
            PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong()),
        )
    } else {
        @Suppress("DEPRECATION")
        resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
    }

private fun Context.launchPlatformDestination(
    intent: Intent,
    @StringRes failureMessage: Int,
) {
    try {
        startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(this, failureMessage, Toast.LENGTH_SHORT).show()
    } catch (_: SecurityException) {
        Toast.makeText(this, failureMessage, Toast.LENGTH_SHORT).show()
    }
}
