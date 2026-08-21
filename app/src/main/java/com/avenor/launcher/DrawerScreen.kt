package com.avenor.launcher

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private enum class DrawerLoadTrigger {
    Initial,
    ManualRetry,
    LiveUpdate,
}

private const val SETTINGS_INDEX_ENTRY = "settings"

@Composable
internal fun DrawerScreen(
    inventoryLoader: LaunchableInventoryLoader,
    inventoryCoordinator: LaunchableInventoryCoordinator = remember(inventoryLoader) {
        LaunchableInventoryCoordinator(inventoryLoader)
    },
    initialLoadHandledExternally: Boolean = false,
    entryLauncher: LaunchableEntryLauncher = LaunchableEntryLauncher { false },
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    active: Boolean = true,
    onLongPress: (LaunchableEntry) -> Unit = {},
    onExternalLaunch: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
) {
    var loadRequest by remember { mutableIntStateOf(0) }
    var loadTrigger by remember { mutableStateOf(DrawerLoadTrigger.Initial) }
    var hasBeenActive by remember { mutableStateOf(false) }
    val state by inventoryCoordinator.state.collectAsState()
    val activationGuard = remember { RapidActivationGuard() }
    val context = LocalContext.current
    val locale = LocalConfiguration.current.locales[0]
    val launchFailureMessage = stringResource(R.string.application_unable_to_open)
    val currentState by rememberUpdatedState(state)
    var previousContent by remember { mutableStateOf<LaunchableInventorySnapshot?>(null) }

    LaunchedEffect(inventoryLoader, loadRequest) {
        if (loadRequest == 0 &&
            (initialLoadHandledExternally || state is LaunchableInventoryState.Content)
        ) {
            return@LaunchedEffect
        }
        val positionBeforeRefresh = if (loadTrigger == DrawerLoadTrigger.LiveUpdate) {
            (state as? LaunchableInventoryState.Content)?.let { content ->
                captureDrawerListPosition(
                    sections = content.snapshot.drawerSectionsFor(locale),
                    firstVisibleItemIndex = listState.firstVisibleItemIndex,
                    firstVisibleItemScrollOffset = listState.firstVisibleItemScrollOffset,
                )
            }
        } else {
            null
        }
        inventoryCoordinator.load(showLoading = loadTrigger != DrawerLoadTrigger.LiveUpdate)
        val updatedState = inventoryCoordinator.state.value

        if (positionBeforeRefresh != null && updatedState is LaunchableInventoryState.Content) {
            val restorationTarget = resolveDrawerRestorationTarget(
                position = positionBeforeRefresh,
                sections = updatedState.snapshot.drawerSectionsFor(locale),
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

    LaunchedEffect(inventoryLoader, active, initialLoadHandledExternally) {
        if (active && !initialLoadHandledExternally) {
            if (!hasBeenActive) {
                hasBeenActive = true
                return@LaunchedEffect
            }
            when (currentState) {
                is LaunchableInventoryState.Content -> {
                    loadTrigger = DrawerLoadTrigger.LiveUpdate
                    loadRequest += 1
                }

                is LaunchableInventoryState.Error -> {
                    loadTrigger = DrawerLoadTrigger.Initial
                    loadRequest += 1
                }

                LaunchableInventoryState.Loading -> Unit
            }
        }
    }

    LaunchedEffect(state, initialLoadHandledExternally) {
        if (!initialLoadHandledExternally) return@LaunchedEffect
        val content = state as? LaunchableInventoryState.Content ?: return@LaunchedEffect
        val oldContent = previousContent
        previousContent = content.snapshot
        if (oldContent == null) return@LaunchedEffect
        val position = captureDrawerListPosition(
            sections = oldContent.drawerSectionsFor(locale),
            firstVisibleItemIndex = listState.firstVisibleItemIndex,
            firstVisibleItemScrollOffset = listState.firstVisibleItemScrollOffset,
        ) ?: return@LaunchedEffect
        val restorationTarget = resolveDrawerRestorationTarget(
            position = position,
            sections = content.snapshot.drawerSectionsFor(locale),
        ) ?: return@LaunchedEffect
        withFrameNanos { }
        listState.scrollToItem(
            index = restorationTarget.itemIndex,
            scrollOffset = restorationTarget.scrollOffset,
        )
    }

    DisposableEffect(inventoryLoader, active, initialLoadHandledExternally) {
        val observation = if (active && !initialLoadHandledExternally) {
            inventoryCoordinator.observe {
                if (currentState is LaunchableInventoryState.Content) {
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
        LaunchableInventoryState.Loading -> DrawerMessage(
            modifier = modifier,
            message = stringResource(R.string.drawer_loading_applications),
            showProgress = true,
            action = null,
            testTag = "drawer_loading",
        )

        is LaunchableInventoryState.Error -> DrawerMessage(
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

        is LaunchableInventoryState.Content -> DrawerApplicationList(
            modifier = modifier,
            listState = listState,
            sections = currentState.snapshot.drawerSectionsFor(locale),
            onLaunch = { entry ->
                if (activationGuard.tryAcquire()) {
                    if (entryLauncher.launch(entry)) {
                        onExternalLaunch()
                    } else {
                        Toast.makeText(context, launchFailureMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onLongPress = onLongPress,
            onOpenSettings = onOpenSettings,
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
    sections: List<DrawerSection>,
    onLaunch: (LaunchableEntry) -> Unit,
    onLongPress: (LaunchableEntry) -> Unit,
    onOpenSettings: () -> Unit,
) {
    val sectionAnchors = remember(sections) {
        buildMap {
            var itemIndex = 0
            sections.forEach { section ->
                put(section.label, itemIndex)
                itemIndex += 1 + section.entries.size
            }
        }
    }
    val settingsAnchor = remember(sections) {
        sections.sumOf { section -> 1 + section.entries.size }
    }
    val coroutineScope = rememberCoroutineScope()
    val hapticFeedback = LocalHapticFeedback.current
    var activeIndexLabel by remember { mutableStateOf<String?>(null) }
    var indexJumpJob by remember { mutableStateOf<Job?>(null) }
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
                item(key = "section:${section.label}") {
                    DrawerSectionHeader(section.label)
                }
                items(
                    items = section.entries,
                    key = { entry ->
                        "${entry.identity.profileSerialNumber}:" +
                            entry.identity.componentName.flattenToString()
                    },
                ) { entry ->
                    DrawerApplicationRow(
                        entry = entry,
                        onLaunch = onLaunch,
                        onLongPress = onLongPress,
                    )
                }
            }
            item(key = "section:settings") {
                DrawerSectionHeader(
                    label = stringResource(R.string.settings),
                    modifier = Modifier.testTag("drawer_settings_anchor"),
                )
            }
            item(key = "settings") {
                DrawerSettingsRow(onClick = onOpenSettings)
            }
        }

        DrawerAlphabetIndex(
            labels = sections.map(DrawerSection::label),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .windowInsetsPadding(WindowInsets.safeDrawing),
            onSelect = { label, immediate ->
                val anchor = sectionAnchors.getValue(label)
                hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
                indexJumpJob?.cancel()
                indexJumpJob = coroutineScope.launch {
                    if (immediate) {
                        listState.scrollToItem(anchor)
                    } else {
                        listState.animateScrollToItem(anchor)
                    }
                }
            },
            onActiveLabelChange = { label -> activeIndexLabel = label },
            onSelectSettings = { immediate ->
                hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
                indexJumpJob?.cancel()
                indexJumpJob = coroutineScope.launch {
                    if (immediate) {
                        listState.scrollToItem(settingsAnchor)
                    } else {
                        listState.animateScrollToItem(settingsAnchor)
                    }
                }
            },
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
    onSelect: (String, Boolean) -> Unit,
    onActiveLabelChange: (String?) -> Unit,
    onSelectSettings: (Boolean) -> Unit,
) {
    val slotHeight = dimensionResource(R.dimen.drawer_index_slot_height)
    val density = LocalDensity.current
    val indexState = rememberLazyListState()
    val indexEntries = remember(labels) { labels + SETTINGS_INDEX_ENTRY }
    LazyColumn(
        modifier = modifier
            .heightIn(max = dimensionResource(R.dimen.drawer_index_complete_height))
            .height(slotHeight * indexEntries.size)
            .width(dimensionResource(R.dimen.drawer_index_width))
            .pointerInput(indexEntries, indexState) {
                val edgeThresholdPx = with(density) { slotHeight.toPx() }

                fun entryAt(y: Float): String? = indexState.layoutInfo.visibleItemsInfo
                    .firstOrNull { item -> y >= item.offset && y < item.offset + item.size }
                    ?.let { item -> indexEntries.getOrNull(item.index) }

                try {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        var selectedLabel: String? = null
                        var multiplePointersDetected = false

                        fun select(entry: String?, immediate: Boolean) {
                            if (entry == null || entry == selectedLabel) return
                            selectedLabel = entry
                            if (entry == SETTINGS_INDEX_ENTRY) {
                                onActiveLabelChange(SETTINGS_INDEX_ENTRY)
                                onSelectSettings(immediate)
                            } else {
                                onActiveLabelChange(entry)
                                onSelect(entry, immediate)
                            }
                        }

                        down.consume()
                        select(entryAt(down.position.y), immediate = true)
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
                                select(entryAt(activeChange.position.y), immediate = false)
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
                            onSelect(label, true)
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
        item(key = "index:settings") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(slotHeight)
                    .semantics {
                        role = Role.Button
                        onClick {
                            onSelectSettings(true)
                            true
                        }
                    }
                    .testTag("drawer_index_settings"),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_settings),
                    contentDescription = stringResource(R.string.settings),
                    modifier = Modifier.size(
                        dimensionResource(R.dimen.drawer_index_settings_icon_size),
                    ),
                    tint = MaterialTheme.colorScheme.onBackground,
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
        if (label == SETTINGS_INDEX_ENTRY) {
            Icon(
                painter = painterResource(R.drawable.ic_settings),
                contentDescription = stringResource(R.string.settings),
                modifier = Modifier.size(
                    dimensionResource(R.dimen.drawer_index_bubble_settings_icon_size),
                ),
                tint = MaterialTheme.colorScheme.onBackground,
            )
        } else {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = dimensionResource(R.dimen.drawer_index_bubble_text_size).value.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun DrawerSectionHeader(
    label: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = label,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = dimensionResource(R.dimen.drawer_section_header_min_height))
            .padding(vertical = dimensionResource(R.dimen.drawer_section_header_vertical_padding))
            .testTag("drawer_section_$label"),
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.labelLarge,
    )
}

@Composable
private fun DrawerSettingsRow(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = dimensionResource(R.dimen.drawer_application_row_min_height))
            .clickable(role = Role.Button, onClick = onClick)
            .testTag("drawer_settings_entry"),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_settings),
            contentDescription = null,
            modifier = Modifier.size(dimensionResource(R.dimen.drawer_application_icon_size)),
            tint = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.width(dimensionResource(R.dimen.drawer_application_icon_label_gap)))
        Text(
            text = stringResource(R.string.settings),
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun DrawerApplicationRow(
    entry: LaunchableEntry,
    onLaunch: (LaunchableEntry) -> Unit,
    onLongPress: (LaunchableEntry) -> Unit,
) {
    val iconSize = dimensionResource(R.dimen.drawer_application_icon_size)
    val iconSizePixels = with(LocalDensity.current) { iconSize.roundToPx() }
    val hapticFeedback = LocalHapticFeedback.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = dimensionResource(R.dimen.drawer_application_row_min_height))
            .combinedClickable(
                role = Role.Button,
                onClick = { onLaunch(entry) },
                onLongClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    onLongPress(entry)
                },
            )
            .testTag("drawer_application_row"),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DrawerApplicationIcon(entry.iconBitmap, entry.icon, iconSize, iconSizePixels)
        Spacer(Modifier.width(dimensionResource(R.dimen.drawer_application_icon_label_gap)))
        Text(
            text = entry.label,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun DrawerApplicationIcon(
    preparedBitmap: Bitmap?,
    icon: Drawable,
    iconSize: Dp,
    iconSizePixels: Int,
) {
    // Keep the ImageBitmap wrapper across recompositions so scrolling a long
    // distance does not reallocate one per row it passes.
    val bitmap = remember(preparedBitmap, icon, iconSizePixels) {
        preparedBitmap?.asImageBitmap()
            ?: icon.toBitmap(width = iconSizePixels, height = iconSizePixels).asImageBitmap()
    }

    Image(
        bitmap = bitmap,
        contentDescription = null,
        modifier = Modifier.size(iconSize),
    )
}
