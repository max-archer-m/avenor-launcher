package com.avenor.launcher

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.widget.Toast
import android.view.HapticFeedbackConstants
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.colorResource
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
import kotlinx.coroutines.launch

@Composable
internal fun HomeScreen(
    clock: () -> ZonedDateTime = { ZonedDateTime.now() },
    favoriteState: FavoriteReadState = FavoriteReadState.Readable(emptyList()),
    favoriteAvailability: Map<LaunchableIdentity, FavoriteAvailability> = emptyMap(),
    marqueePaused: Boolean = false,
    editMode: Boolean = false,
    onRetryFavorites: () -> Unit = {},
    onLaunchFavorite: (FavoriteAvailability) -> Unit = {},
    onLongPressFavorite: (LaunchableEntry) -> Unit = {},
    onReorderFavorites: (List<LaunchableIdentity>) -> Unit = {},
    accessibilityLockController: AccessibilityLockController = EmptyAccessibilityLockController,
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .editSurface(editMode),
        ) {
            Column {
                Text(
                    text = HomeDateTimeFormatter.time(context, now),
                    modifier = Modifier
                        .heightIn(min = dimensionResource(R.dimen.home_time_min_height))
                        .testTag("home_time")
                        .clickable(enabled = !editMode, role = Role.Button) {
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
                        .clickable(enabled = !editMode, role = Role.Button) {
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
            }
            Spacer(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .then(
                        if (accessibilityLockController.availableForValidation && !editMode) {
                            Modifier.pointerInput(accessibilityLockController) {
                                detectTapGestures(
                                    onDoubleTap = {
                                        if (!accessibilityLockController.isSystemEnabled()) {
                                            return@detectTapGestures
                                        }
                                        if (accessibilityLockController.requestLock() !=
                                            LockRequestResult.Requested
                                        ) {
                                            Toast.makeText(
                                                context,
                                                R.string.unable_to_lock_screen,
                                                Toast.LENGTH_SHORT,
                                            ).show()
                                        }
                                    },
                                )
                            }
                        } else {
                            Modifier
                        },
                    )
                    .testTag("home_double_tap_lock_region"),
            )
        }
        Spacer(Modifier.height(dimensionResource(R.dimen.home_module_spacing)))
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
                        editMode = editMode,
                        onLaunchFavorite = onLaunchFavorite,
                        onLongPressFavorite = onLongPressFavorite,
                        onReorderFavorites = onReorderFavorites,
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
    editMode: Boolean,
    onLaunchFavorite: (FavoriteAvailability) -> Unit,
    onLongPressFavorite: (LaunchableEntry) -> Unit,
    onReorderFavorites: (List<LaunchableIdentity>) -> Unit,
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val view = LocalView.current
    var displayedIdentities by remember { mutableStateOf(identities) }
    var draggedIdentity by remember { mutableStateOf<LaunchableIdentity?>(null) }
    var dragOffset by remember { mutableFloatStateOf(0f) }
    val edgeScrollStep = with(androidx.compose.ui.platform.LocalDensity.current) {
        dimensionResource(R.dimen.home_reorder_edge_scroll_step).toPx()
    }
    LaunchedEffect(identities, draggedIdentity) {
        if (draggedIdentity == null) displayedIdentities = identities
    }
    val overflowingEntries = remember { mutableStateMapOf<String, Boolean>() }
    var pressedEntryKey by remember { mutableStateOf<String?>(null) }
    var focusedEntryKey by remember { mutableStateOf<String?>(null) }
    val centeredEntryKey by remember(listState, displayedIdentities) {
        derivedStateOf {
            if (listState.isScrollInProgress) null else {
                val layoutInfo = listState.layoutInfo
                val viewportCenter =
                    (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2f
                layoutInfo.visibleItemsInfo
                    .mapNotNull { item ->
                        val identity = displayedIdentities.getOrNull(item.index)
                            ?: return@mapNotNull null
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
        paused = marqueePaused || editMode,
        scrolling = listState.isScrollInProgress,
        pressedKey = pressedEntryKey,
        focusedKey = focusedEntryKey,
        centeredKey = centeredEntryKey,
        overflowingKeys = overflowingEntries.keys,
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .editSurface(editMode)
            .testTag("home_favorites"),
        state = listState,
    ) {
        items(
            items = displayedIdentities,
            key = { it.stableKey() },
        ) { identity ->
            val availability = availabilityByIdentity[identity]
                ?: FavoriteAvailability.Unknown(null)
            val entry = availability.presentationEntry
            val entryKey = identity.stableKey()
            HomeFavoriteRow(
                modifier = Modifier.animateItem(),
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
                editMode = editMode,
                dragging = draggedIdentity == identity,
                dragOffset = if (draggedIdentity == identity) dragOffset else 0f,
                onDragStart = {
                    draggedIdentity = identity
                    dragOffset = 0f
                },
                onDrag = { delta ->
                    val currentIndex = displayedIdentities.indexOf(identity)
                    val itemSize = listState.layoutInfo.visibleItemsInfo
                        .firstOrNull { it.index == currentIndex }
                        ?.size
                        ?.toFloat()
                    if (itemSize != null) {
                        dragOffset += delta
                        val direction = when {
                            dragOffset > itemSize / 2f -> 1
                            dragOffset < -itemSize / 2f -> -1
                            else -> 0
                        }
                        if (direction != 0) {
                            val targetIndex = currentIndex + direction
                            if (targetIndex in displayedIdentities.indices) {
                                displayedIdentities =
                                    displayedIdentities.moved(currentIndex, targetIndex)
                                dragOffset -= itemSize * direction
                                view.performHapticFeedback(
                                    if (Build.VERSION.SDK_INT >= 34) {
                                        HapticFeedbackConstants.SEGMENT_FREQUENT_TICK
                                    } else {
                                        HapticFeedbackConstants.CLOCK_TICK
                                    },
                                )
                                val visible = listState.layoutInfo.visibleItemsInfo
                                if (targetIndex == visible.firstOrNull()?.index && listState.canScrollBackward) {
                                    scope.launch { listState.scrollBy(-edgeScrollStep) }
                                } else if (targetIndex == visible.lastOrNull()?.index && listState.canScrollForward) {
                                    scope.launch { listState.scrollBy(edgeScrollStep) }
                                }
                            }
                        }
                    }
                },
                onDragEnd = {
                    draggedIdentity = null
                    dragOffset = 0f
                    onReorderFavorites(displayedIdentities)
                },
                onDragCancel = {
                    draggedIdentity = null
                    dragOffset = 0f
                    displayedIdentities = identities
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
    modifier: Modifier,
    availability: FavoriteAvailability,
    marqueeEligible: Boolean,
    onOverflowChanged: (Boolean) -> Unit,
    onPressedChanged: (Boolean) -> Unit,
    onFocusedChanged: (Boolean) -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    editMode: Boolean,
    dragging: Boolean,
    dragOffset: Float,
    onDragStart: () -> Unit,
    onDrag: (Float) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
) {
    val entry = availability.presentationEntry
    val iconSize = dimensionResource(R.dimen.home_favorite_icon_size)
    val disabledAlpha = integerResource(R.integer.disabled_content_alpha_percent) / 100f
    val iconPixels = with(androidx.compose.ui.platform.LocalDensity.current) { iconSize.roundToPx() }
    val interactionSource = remember(entry?.identity) { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    LaunchedEffect(isPressed) { onPressedChanged(isPressed) }
    val hapticFeedback = LocalHapticFeedback.current
    val handleTargetSize = dimensionResource(R.dimen.home_reorder_handle_target_size)
    val dragElevation = with(androidx.compose.ui.platform.LocalDensity.current) {
        dimensionResource(R.dimen.home_reorder_drag_elevation).toPx()
    }
    val currentOnDragStart by rememberUpdatedState(onDragStart)
    val currentOnDrag by rememberUpdatedState(onDrag)
    val currentOnDragEnd by rememberUpdatedState(onDragEnd)
    val currentOnDragCancel by rememberUpdatedState(onDragCancel)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = dimensionResource(R.dimen.home_favorite_row_min_height))
            .combinedClickable(
                enabled = !editMode,
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
            .graphicsLayer {
                translationY = dragOffset
                shadowElevation = if (dragging) dragElevation else 0f
            }
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
        if (editMode) {
            Icon(
                painter = painterResource(R.drawable.ic_drag_handle),
                contentDescription = stringResource(R.string.favorite_reorder_handle),
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .size(handleTargetSize)
                    .padding(
                        (handleTargetSize - dimensionResource(R.dimen.home_reorder_handle_size)) / 2,
                    )
                    .pointerInput(entry?.identity) {
                        detectDragGestures(
                            onDragStart = {
                                hapticFeedback.performHapticFeedback(
                                    androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress,
                                )
                                currentOnDragStart()
                            },
                            onDragEnd = currentOnDragEnd,
                            onDragCancel = currentOnDragCancel,
                            onDrag = { change, amount ->
                                change.consume()
                                currentOnDrag(amount.y)
                            },
                        )
                    }
                    .testTag("favorite_reorder_handle"),
            )
        }
    }
}

@Composable
private fun Modifier.editSurface(enabled: Boolean): Modifier = if (!enabled) this else {
    clip(RoundedCornerShape(dimensionResource(R.dimen.home_edit_surface_radius)))
        .background(colorResource(R.color.home_edit_surface))
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
