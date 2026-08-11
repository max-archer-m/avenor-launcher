package com.avenor.launcher

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private sealed interface DrawerUiState {
    data object Loading : DrawerUiState

    data class Content(val entries: List<LaunchableEntry>) : DrawerUiState

    data object Error : DrawerUiState
}

private enum class DrawerLoadTrigger {
    Initial,
    ManualRetry,
    LiveUpdate,
}

@Composable
internal fun DrawerScreen(
    inventoryLoader: LaunchableInventoryLoader,
    entryLauncher: LaunchableEntryLauncher = LaunchableEntryLauncher { false },
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    active: Boolean = true,
    marqueePaused: Boolean = false,
) {
    var loadRequest by remember { mutableIntStateOf(0) }
    var loadTrigger by remember { mutableStateOf(DrawerLoadTrigger.Initial) }
    var state by remember(inventoryLoader) {
        mutableStateOf<DrawerUiState>(DrawerUiState.Loading)
    }
    val loadMutex = remember(inventoryLoader) { Mutex() }
    val activationGuard = remember { RapidActivationGuard() }
    val context = LocalContext.current
    val locale = LocalConfiguration.current.locales[0]
    val launchFailureMessage = stringResource(R.string.application_unable_to_open)
    val currentState by rememberUpdatedState(state)

    LaunchedEffect(inventoryLoader, loadRequest) {
        val positionBeforeRefresh = if (loadTrigger == DrawerLoadTrigger.LiveUpdate) {
            (state as? DrawerUiState.Content)?.let { content ->
                captureDrawerListPosition(
                    sections = buildDrawerSections(content.entries, locale),
                    firstVisibleItemIndex = listState.firstVisibleItemIndex,
                    firstVisibleItemScrollOffset = listState.firstVisibleItemScrollOffset,
                )
            }
        } else {
            null
        }
        if (loadTrigger != DrawerLoadTrigger.LiveUpdate) {
            state = DrawerUiState.Loading
        }
        val updatedState = try {
            val entries = loadMutex.withLock {
                inventoryLoader.load()
            }
            if (entries.isEmpty()) DrawerUiState.Error else DrawerUiState.Content(entries)
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: Exception) {
            DrawerUiState.Error
        }
        state = updatedState

        if (positionBeforeRefresh != null && updatedState is DrawerUiState.Content) {
            val restorationTarget = resolveDrawerRestorationTarget(
                position = positionBeforeRefresh,
                sections = buildDrawerSections(updatedState.entries, locale),
            )
            if (restorationTarget != null) {
                withFrameNanos { }
                listState.scrollToItem(
                    index = restorationTarget.itemIndex,
                    scrollOffset = restorationTarget.scrollOffset,
                )
            }
        }
    }

    LaunchedEffect(inventoryLoader, active) {
        if (active && currentState is DrawerUiState.Content) {
            loadTrigger = DrawerLoadTrigger.LiveUpdate
            loadRequest += 1
        }
    }

    DisposableEffect(inventoryLoader, active) {
        val monitor = inventoryLoader as? LaunchableInventoryMonitor
        val observation = if (active && monitor != null) {
            monitor.observe {
                if (currentState is DrawerUiState.Content) {
                    loadTrigger = DrawerLoadTrigger.LiveUpdate
                    loadRequest += 1
                }
            }
        } else {
            null
        }
        onDispose {
            observation?.stop()
        }
    }

    when (val currentState = state) {
        DrawerUiState.Loading -> DrawerMessage(
            modifier = modifier,
            message = stringResource(R.string.drawer_loading_applications),
            showProgress = true,
            action = null,
            testTag = "drawer_loading",
        )

        DrawerUiState.Error -> DrawerMessage(
            modifier = modifier,
            message = stringResource(R.string.drawer_unable_to_load_applications),
            showProgress = false,
            showErrorIcon = true,
            action = {
                TextButton(
                    onClick = {
                        loadTrigger = DrawerLoadTrigger.ManualRetry
                        loadRequest += 1
                    },
                ) {
                    Text(stringResource(R.string.retry))
                }
            },
            testTag = "drawer_error",
        )

        is DrawerUiState.Content -> DrawerApplicationList(
            modifier = modifier,
            listState = listState,
            entries = currentState.entries,
            marqueePaused = marqueePaused || !active,
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
    modifier: Modifier,
    message: String,
    showProgress: Boolean,
    showErrorIcon: Boolean = false,
    action: (@Composable () -> Unit)?,
    testTag: String,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .scrollable(
                state = rememberScrollableState { 0f },
                orientation = Orientation.Vertical,
            )
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
@OptIn(ExperimentalFoundationApi::class)
private fun DrawerApplicationList(
    modifier: Modifier,
    listState: LazyListState,
    entries: List<LaunchableEntry>,
    marqueePaused: Boolean,
    onLaunch: (LaunchableEntry) -> Unit,
) {
    val locale = LocalConfiguration.current.locales[0]
    val sections = remember(entries, locale) {
        buildDrawerSections(entries, locale)
    }
    val sectionAnchors = remember(sections) {
        buildMap {
            var itemIndex = 0
            sections.forEach { section ->
                put(section.label, itemIndex)
                itemIndex += 1 + section.entries.size
            }
        }
    }
    val coroutineScope = rememberCoroutineScope()
    val hapticFeedback = LocalHapticFeedback.current
    var activeIndexLabel by remember { mutableStateOf<String?>(null) }
    var indexJumpJob by remember { mutableStateOf<Job?>(null) }
    val overflowingEntries = remember { mutableStateMapOf<String, Boolean>() }
    var pressedEntryKey by remember { mutableStateOf<String?>(null) }
    var focusedEntryKey by remember { mutableStateOf<String?>(null) }
    val entryKeyByItemIndex = remember(sections) {
        buildMap {
            var itemIndex = 0
            sections.forEach { section ->
                itemIndex += 1
                section.entries.forEach { entry ->
                    put(itemIndex, entry.drawerEntryKey())
                    itemIndex += 1
                }
            }
        }
    }
    val centeredEntryKey by remember(listState, entryKeyByItemIndex) {
        derivedStateOf {
            if (listState.isScrollInProgress) {
                null
            } else {
                val layoutInfo = listState.layoutInfo
                val viewportCenter =
                    (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2f
                layoutInfo.visibleItemsInfo
                    .mapNotNull { item ->
                        val entryKey = entryKeyByItemIndex[item.index] ?: return@mapNotNull null
                        if (overflowingEntries[entryKey] != true) return@mapNotNull null
                        val itemCenter = item.offset + (item.size / 2f)
                        entryKey to kotlin.math.abs(itemCenter - viewportCenter)
                    }
                    .minByOrNull { (_, distance) -> distance }
                    ?.first
            }
        }
    }
    val activeMarqueeKey = if (marqueePaused || listState.isScrollInProgress) {
        null
    } else {
        listOf(pressedEntryKey, focusedEntryKey, centeredEntryKey)
            .firstOrNull { entryKey ->
                entryKey != null && overflowingEntries[entryKey] == true
            }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        val horizontalPadding = dimensionResource(R.dimen.drawer_horizontal_padding)
        val indexWidth = dimensionResource(R.dimen.drawer_index_width)
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .testTag("drawer_application_list"),
            contentPadding = PaddingValues(
                start = horizontalPadding,
                end = horizontalPadding + indexWidth,
            ),
            state = listState,
        ) {
            sections.forEach { section ->
                stickyHeader(key = "section:${section.label}") {
                    DrawerSectionHeader(section.label)
                }
                items(
                    items = section.entries,
                    key = { entry ->
                        "${entry.identity.profileSerialNumber}:" +
                            entry.identity.componentName.flattenToString()
                    },
                ) { entry ->
                    val entryKey = entry.drawerEntryKey()
                    DrawerApplicationRow(
                        entry = entry,
                        marqueeEligible = activeMarqueeKey == entryKey,
                        onOverflowChanged = { overflow ->
                            if (overflow) {
                                overflowingEntries[entryKey] = true
                            } else {
                                overflowingEntries.remove(entryKey)
                            }
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
                        onLaunch = onLaunch,
                    )
                }
            }
        }

        DrawerAlphabetIndex(
            labels = sections.map(DrawerSection::label),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .windowInsetsPadding(WindowInsets.safeDrawing),
            onSelect = { label ->
                val anchor = sectionAnchors.getValue(label)
                hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
                indexJumpJob?.cancel()
                indexJumpJob = coroutineScope.launch {
                    listState.scrollToItem(anchor)
                }
            },
            onActiveLabelChange = { label -> activeIndexLabel = label },
        )

        activeIndexLabel?.let { label ->
            DrawerIndexBubble(
                label = label,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(
                        end = indexWidth +
                            dimensionResource(R.dimen.drawer_index_bubble_index_gap),
                    ),
            )
        }
    }
}

@Composable
private fun DrawerAlphabetIndex(
    labels: List<String>,
    modifier: Modifier = Modifier,
    onSelect: (String) -> Unit,
    onActiveLabelChange: (String?) -> Unit,
) {
    val slotHeight = dimensionResource(R.dimen.drawer_index_slot_height)
    val density = LocalDensity.current
    val indexState = rememberLazyListState()
    LazyColumn(
        modifier = modifier
            .heightIn(max = dimensionResource(R.dimen.drawer_index_complete_height))
            .height(slotHeight * labels.size)
            .width(dimensionResource(R.dimen.drawer_index_width))
            .pointerInput(labels, indexState) {
                val edgeThresholdPx = with(density) { slotHeight.toPx() }

                fun labelAt(y: Float): String? = indexState.layoutInfo.visibleItemsInfo
                    .firstOrNull { item -> y >= item.offset && y < item.offset + item.size }
                    ?.let { item -> labels.getOrNull(item.index) }

                try {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        var selectedLabel: String? = null
                        var multiplePointersDetected = false

                        fun select(label: String?) {
                            if (label == null || label == selectedLabel) return
                            selectedLabel = label
                            onActiveLabelChange(label)
                            onSelect(label)
                        }

                        down.consume()
                        select(labelAt(down.position.y))
                        var pointersRemainPressed: Boolean
                        do {
                            val event = awaitPointerEvent()
                            if (event.changes.count { it.pressed } > 1) {
                                multiplePointersDetected = true
                                selectedLabel = null
                                onActiveLabelChange(null)
                            }
                            event.changes.forEach { change -> change.consume() }
                            val activeChange = event.changes.firstOrNull { it.id == down.id }
                            if (!multiplePointersDetected && activeChange?.pressed == true) {
                                val viewportEnd = indexState.layoutInfo.viewportEndOffset.toFloat()
                                when {
                                    activeChange.position.y < edgeThresholdPx -> {
                                        indexState.dispatchRawDelta(-edgeThresholdPx)
                                    }

                                    activeChange.position.y > viewportEnd - edgeThresholdPx -> {
                                        indexState.dispatchRawDelta(edgeThresholdPx)
                                    }
                                }
                                select(labelAt(activeChange.position.y))
                            }
                            pointersRemainPressed = event.changes.any { it.pressed }
                        } while (pointersRemainPressed)
                        onActiveLabelChange(null)
                    }
                } finally {
                    onActiveLabelChange(null)
                }
            }
            .testTag("drawer_alphabet_index"),
        state = indexState,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(
            items = labels,
            key = { label -> "index:$label" },
        ) { label ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(slotHeight)
                    .semantics {
                        role = Role.Button
                        onClick {
                            onSelect(label)
                            true
                        }
                    }
                    .testTag("drawer_index_$label"),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = dimensionResource(R.dimen.drawer_index_text_size).value.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun DrawerIndexBubble(
    label: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(dimensionResource(R.dimen.drawer_index_bubble_size))
            .testTag("drawer_index_bubble"),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = dimensionResource(R.dimen.drawer_index_bubble_text_size).value.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun DrawerSectionHeader(label: String) {
    Text(
        text = label,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = dimensionResource(R.dimen.drawer_section_header_min_height))
            .padding(vertical = dimensionResource(R.dimen.drawer_section_header_vertical_padding))
            .testTag("drawer_section_$label"),
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.labelLarge,
    )
}

@Composable
private fun DrawerApplicationRow(
    entry: LaunchableEntry,
    marqueeEligible: Boolean,
    onOverflowChanged: (Boolean) -> Unit,
    onPressedChanged: (Boolean) -> Unit,
    onFocusedChanged: (Boolean) -> Unit,
    onLaunch: (LaunchableEntry) -> Unit,
) {
    val iconSize = dimensionResource(R.dimen.drawer_application_icon_size)
    val iconSizePixels = with(LocalDensity.current) { iconSize.roundToPx() }
    val interactionSource = remember(entry.identity) { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    LaunchedEffect(pressed) {
        onPressedChanged(pressed)
    }
    DisposableEffect(entry.identity) {
        onDispose {
            onOverflowChanged(false)
            onPressedChanged(false)
            onFocusedChanged(false)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = dimensionResource(R.dimen.drawer_application_row_min_height))
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                role = Role.Button,
                onClick = { onLaunch(entry) },
            )
            .onFocusChanged { focusState -> onFocusedChanged(focusState.isFocused) }
            .testTag("drawer_application_row"),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DrawerApplicationIcon(entry.icon, iconSizePixels)
        Spacer(Modifier.width(dimensionResource(R.dimen.drawer_application_icon_label_gap)))
        SharedMarqueeText(
            text = entry.label,
            eligible = marqueeEligible,
            onOverflowChanged = onOverflowChanged,
            modifier = Modifier.weight(1f),
        )
    }
}

private fun LaunchableEntry.drawerEntryKey(): String =
    "${identity.profileSerialNumber}:${identity.componentName.flattenToString()}"

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
