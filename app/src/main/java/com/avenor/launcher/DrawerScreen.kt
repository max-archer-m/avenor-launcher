package com.avenor.launcher

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.avenor.launcher.ui.drawer.components.DrawerAlphabetIndex
import com.avenor.launcher.ui.drawer.components.DrawerIndexBubble
import com.avenor.launcher.ui.drawer.components.DrawerNavigationTopBar
import com.avenor.launcher.ui.drawer.components.DrawerSearchTopBar
import java.util.Locale

private enum class DrawerLoadTrigger {
    Initial,
    ManualRetry,
    LiveUpdate,
    LaunchFailureRefresh,
}

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
    onNavigateBack: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    favoriteSelectionTarget: String? = null,
    favoriteSelection: List<LaunchableIdentity> = emptyList(),
    favoriteMembership: Set<LaunchableIdentity> = emptySet(),
    favoriteSelectionSaving: Boolean = false,
    onToggleFavoriteSelection: (LaunchableIdentity) -> Unit = {},
    onCancelFavoriteSelection: () -> Unit = {},
    onConfirmFavoriteSelection: () -> Unit = {},
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
    var searchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var ordinaryPosition by remember { mutableStateOf<DrawerListPosition?>(null) }
    val searchFocusRequester = remember { FocusRequester() }
    val searchScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(key1 = active) {
        if (!active) {
            searchActive = false
            searchQuery = ""
            ordinaryPosition = null
        }
    }

    LaunchedEffect(inventoryLoader, loadRequest) {
        if (loadRequest == 0 &&
            (initialLoadHandledExternally || state is LaunchableInventoryState.Content)
        ) {
            return@LaunchedEffect
        }
        val positionBeforeRefresh = if (
            loadTrigger == DrawerLoadTrigger.LiveUpdate ||
            loadTrigger == DrawerLoadTrigger.LaunchFailureRefresh
        ) {
            (state as? LaunchableInventoryState.Content)?.let { content ->
                captureDrawerListPosition(
                    sections = content.snapshot.drawerSectionsForCurrentMode(
                        locale = locale,
                        searchActive = searchActive,
                        searchQuery = searchQuery,
                    ),
                    firstVisibleItemIndex = listState.firstVisibleItemIndex,
                    firstVisibleItemScrollOffset = listState.firstVisibleItemScrollOffset,
                )
            }
        } else {
            null
        }
        inventoryCoordinator.load(
            showLoading = loadTrigger != DrawerLoadTrigger.LiveUpdate &&
                loadTrigger != DrawerLoadTrigger.LaunchFailureRefresh,
            preserveContentOnFailure = loadTrigger == DrawerLoadTrigger.LaunchFailureRefresh,
        )
        val updatedState = inventoryCoordinator.state.value

        if (positionBeforeRefresh != null && updatedState is LaunchableInventoryState.Content) {
            val restorationTarget = resolveDrawerRestorationTarget(
                position = positionBeforeRefresh,
                sections = updatedState.snapshot.drawerSectionsForCurrentMode(
                    locale = locale,
                    searchActive = searchActive,
                    searchQuery = searchQuery,
                ),
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
            sections = oldContent.drawerSectionsForCurrentMode(
                locale = locale,
                searchActive = searchActive,
                searchQuery = searchQuery,
            ),
            firstVisibleItemIndex = listState.firstVisibleItemIndex,
            firstVisibleItemScrollOffset = listState.firstVisibleItemScrollOffset,
        ) ?: return@LaunchedEffect
        val restorationTarget = resolveDrawerRestorationTarget(
            position = position,
            sections = content.snapshot.drawerSectionsForCurrentMode(
                locale = locale,
                searchActive = searchActive,
                searchQuery = searchQuery,
            ),
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
        LaunchableInventoryState.Loading -> if (favoriteSelectionTarget == null) {
            DrawerOrdinaryMessage(
                modifier = modifier,
                message = stringResource(R.string.drawer_loading_applications),
                showProgress = true,
                action = null,
                testTag = "drawer_loading",
                onNavigateBack = onNavigateBack,
            )
        } else {
            DrawerSelectionMessage(
                modifier = modifier,
                target = favoriteSelectionTarget,
                message = stringResource(R.string.drawer_loading_applications),
                showProgress = true,
                retry = null,
                selection = favoriteSelection,
                saving = favoriteSelectionSaving,
                onCancel = onCancelFavoriteSelection,
                onConfirm = onConfirmFavoriteSelection,
            )
        }

        is LaunchableInventoryState.Error -> if (favoriteSelectionTarget == null) {
            DrawerOrdinaryMessage(
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
                onNavigateBack = onNavigateBack,
            )
        } else {
            DrawerSelectionMessage(
                modifier = modifier,
                target = favoriteSelectionTarget,
                message = stringResource(R.string.drawer_unable_to_load_applications),
                showProgress = false,
                retry = {
                    loadTrigger = DrawerLoadTrigger.ManualRetry
                    loadRequest += 1
                },
                selection = favoriteSelection,
                saving = favoriteSelectionSaving,
                onCancel = onCancelFavoriteSelection,
                onConfirm = onConfirmFavoriteSelection,
            )
        }

        is LaunchableInventoryState.Content -> if (favoriteSelectionTarget == null) {
            val completeSections = currentState.snapshot.drawerSectionsFor(locale)
            val visibleSections = remember(
                key1 = completeSections,
                key2 = searchActive,
                key3 = searchQuery,
                calculation = {
                    if (searchActive) {
                        filterDrawerSections(sections = completeSections, query = searchQuery)
                    } else {
                        completeSections
                    }
                },
            )
            val exitSearch: () -> Unit = {
                val restorationPosition = ordinaryPosition
                searchActive = false
                searchQuery = ""
                ordinaryPosition = null
                keyboardController?.hide()
                if (restorationPosition != null) {
                    searchScope.launch {
                        withFrameNanos { }
                        resolveDrawerOrdinaryRestorationTarget(
                            position = restorationPosition,
                            sections = completeSections,
                        )?.let { target ->
                            listState.scrollToItem(
                                index = target.itemIndex,
                                scrollOffset = target.scrollOffset,
                            )
                        }
                    }
                }
            }
            BackHandler(enabled = searchActive, onBack = exitSearch)
            LaunchedEffect(key1 = searchActive, key2 = searchQuery) {
                if (searchActive) {
                    listState.scrollToItem(index = 0)
                }
            }
            DrawerApplicationList(
                modifier = modifier,
                listState = listState,
                sections = visibleSections,
                searchActive = searchActive,
                searchQuery = searchQuery,
                searchFocusRequester = searchFocusRequester,
                onLaunch = { entry ->
                    if (activationGuard.tryAcquire()) {
                        if (entryLauncher.launch(entry)) {
                            searchActive = false
                            searchQuery = ""
                            ordinaryPosition = null
                            keyboardController?.hide()
                            onExternalLaunch()
                        } else {
                            Toast.makeText(context, launchFailureMessage, Toast.LENGTH_SHORT).show()
                            loadTrigger = DrawerLoadTrigger.LaunchFailureRefresh
                            loadRequest += 1
                        }
                    }
                },
                onLongPress = onLongPress,
                onNavigateBack = onNavigateBack,
                onEnterSearch = {
                    ordinaryPosition = captureDrawerOrdinaryListPosition(
                        sections = completeSections,
                        firstVisibleItemIndex = listState.firstVisibleItemIndex,
                        firstVisibleItemScrollOffset = listState.firstVisibleItemScrollOffset,
                    )
                    searchActive = true
                },
                onQueryChange = { query -> searchQuery = query },
                onClearSearch = { searchQuery = "" },
                onCancelSearch = exitSearch,
                onOpenSettings = onOpenSettings,
            )
        } else {
            DrawerFavoriteSelectionList(
                modifier = modifier,
                listState = listState,
                sections = currentState.snapshot.drawerSectionsFor(locale),
                target = favoriteSelectionTarget,
                selection = favoriteSelection,
                favoriteMembership = favoriteMembership,
                saving = favoriteSelectionSaving,
                onToggle = onToggleFavoriteSelection,
                onCancel = onCancelFavoriteSelection,
                onConfirm = onConfirmFavoriteSelection,
            )
        }
    }
}

private fun LaunchableInventorySnapshot.drawerSectionsForCurrentMode(
    locale: Locale,
    searchActive: Boolean,
    searchQuery: String,
): List<DrawerSection> {
    val completeSections = drawerSectionsFor(locale = locale)
    return if (searchActive) {
        filterDrawerSections(sections = completeSections, query = searchQuery)
    } else {
        completeSections
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
private fun DrawerOrdinaryMessage(
    modifier: Modifier,
    message: String,
    showProgress: Boolean,
    showErrorIcon: Boolean = false,
    action: (@Composable () -> Unit)?,
    testTag: String,
    onNavigateBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(insets = WindowInsets.safeDrawing),
    ) {
        DrawerNavigationTopBar(onNavigateBack = onNavigateBack)
        DrawerMessage(
            modifier = modifier.weight(weight = 1f),
            message = message,
            showProgress = showProgress,
            showErrorIcon = showErrorIcon,
            action = action,
            testTag = testTag,
        )
    }
}

@Composable
private fun DrawerSelectionMessage(
    modifier: Modifier,
    target: String,
    message: String,
    showProgress: Boolean,
    retry: (() -> Unit)?,
    selection: List<LaunchableIdentity>,
    saving: Boolean,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        DrawerSelectionHeader(
            target = target,
            selectionSize = selection.size,
            saving = saving,
            onCancel = onCancel,
            onConfirm = onConfirm,
        )
        DrawerMessage(
            modifier = Modifier.weight(1f),
            message = message,
            showProgress = showProgress,
            showErrorIcon = !showProgress,
            action = retry?.let { retryAction ->
                {
                    TextButton(onClick = retryAction, enabled = !saving) {
                        Text(stringResource(R.string.retry))
                    }
                }
            },
            testTag = "drawer_selection_message",
        )
    }
}

@Composable
private fun DrawerFavoriteSelectionList(
    modifier: Modifier,
    listState: LazyListState,
    sections: List<DrawerSection>,
    target: String,
    selection: List<LaunchableIdentity>,
    favoriteMembership: Set<LaunchableIdentity>,
    saving: Boolean,
    onToggle: (LaunchableIdentity) -> Unit,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
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
    val coroutineScope = rememberCoroutineScope()
    var activeIndexLabel by remember { mutableStateOf<String?>(null) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        DrawerSelectionHeader(
            target = target,
            selectionSize = selection.size,
            saving = saving,
            onCancel = onCancel,
            onConfirm = onConfirm,
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("drawer_favorite_selection_list"),
                contentPadding = PaddingValues(
                    start = dimensionResource(R.dimen.drawer_horizontal_padding),
                    end = dimensionResource(R.dimen.drawer_horizontal_padding) +
                        dimensionResource(R.dimen.drawer_index_width),
                ),
                state = listState,
            ) {
                sections.forEach { section ->
                    item(key = "selection_section:${section.label}") {
                        DrawerSectionHeader(section.label)
                    }
                    items(
                        items = section.entries,
                        key = { entry ->
                            "selection:${entry.identity.profileSerialNumber}:" +
                                entry.identity.componentName.flattenToString()
                        },
                    ) { entry ->
                        val alreadyFavorite = entry.identity in favoriteMembership
                        val order = selection.indexOf(entry.identity)
                        DrawerFavoriteSelectionRow(
                            entry = entry,
                            order = order.takeIf { it >= 0 }?.plus(1),
                            alreadyFavorite = alreadyFavorite,
                            enabled = !saving && !alreadyFavorite,
                            onClick = { onToggle(entry.identity) },
                        )
                    }
                }
            }
            DrawerAlphabetIndex(
                labels = sections.map(DrawerSection::label),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .windowInsetsPadding(WindowInsets.safeDrawing),
                onSelect = { label, immediate ->
                    if (!saving) {
                        val anchor = sectionAnchors.getValue(label)
                        coroutineScope.launch {
                            if (immediate) {
                                listState.scrollToItem(anchor)
                            } else {
                                listState.animateScrollToItem(anchor)
                            }
                        }
                    }
                },
                onActiveLabelChange = { label ->
                    activeIndexLabel = if (saving) null else label
                },
                onSelectSettings = {},
                includeSettings = false,
            )
            activeIndexLabel?.let { label ->
                DrawerIndexBubble(
                    label = label,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(
                            end = dimensionResource(R.dimen.drawer_index_width) +
                                dimensionResource(R.dimen.drawer_index_bubble_index_gap),
                        ),
                )
            }
        }
    }
}

@Composable
private fun DrawerSelectionHeader(
    target: String,
    selectionSize: Int,
    saving: Boolean,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.drawer_top_app_bar_height))
            .padding(horizontal = dimensionResource(R.dimen.drawer_horizontal_padding))
            .testTag("drawer_favorite_selection_header"),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart,
        ) {
            TextButton(
                onClick = onCancel,
                enabled = !saving,
                modifier = Modifier.testTag("drawer_favorite_selection_cancel"),
            ) {
                Text(stringResource(R.string.drawer_selection_cancel))
            }
        }
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = target,
                modifier = Modifier.testTag("drawer_favorite_selection_title"),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterEnd,
        ) {
            TextButton(
                onClick = onConfirm,
                enabled = !saving && selectionSize > 0,
                modifier = Modifier.testTag("drawer_favorite_selection_confirm"),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = colorResource(R.color.avenor_foreground),
                ),
            ) {
                Text(stringResource(R.string.drawer_selection_confirm))
            }
        }
    }
}

@Composable
private fun DrawerFavoriteSelectionRow(
    entry: LaunchableEntry,
    order: Int?,
    alreadyFavorite: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val selected = order != null
    val disabledAlpha = integerResource(R.integer.disabled_content_alpha_percent) / 100f
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = dimensionResource(R.dimen.drawer_application_row_min_height))
            .then(
                if (selected) {
                    Modifier.background(colorResource(R.color.drawer_selection_background))
                } else {
                    Modifier
                },
            )
            .alpha(if (alreadyFavorite) disabledAlpha else 1f)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .testTag("drawer_favorite_selection_row"),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(dimensionResource(R.dimen.drawer_selection_indicator_region_width))
                .height(dimensionResource(R.dimen.drawer_application_row_min_height)),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(dimensionResource(R.dimen.drawer_selection_indicator_size))
                    .testTag("drawer_favorite_selection_indicator")
                    .then(
                        if (selected) {
                            Modifier.background(
                                color = MaterialTheme.colorScheme.onBackground,
                                shape = CircleShape,
                            )
                        } else {
                            Modifier.border(
                                width = dimensionResource(
                                    R.dimen.drawer_selection_indicator_border_width,
                                ),
                                color = MaterialTheme.colorScheme.onBackground,
                                shape = CircleShape,
                            )
                        },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (order != null) {
                    Text(
                        text = order.toString(),
                        color = colorResource(
                            R.color.drawer_selection_indicator_content,
                        ),
                        modifier = Modifier.testTag(
                            "drawer_favorite_selection_number",
                        ),
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        }
        DrawerApplicationIcon(
            preparedBitmap = entry.iconBitmap,
            icon = entry.icon,
            iconSize = dimensionResource(R.dimen.drawer_application_icon_size),
            iconSizePixels = with(LocalDensity.current) {
                dimensionResource(R.dimen.drawer_application_icon_size).roundToPx()
            },
        )
        Spacer(Modifier.width(dimensionResource(R.dimen.drawer_application_icon_label_gap)))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.label,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun DrawerApplicationList(
    modifier: Modifier,
    listState: LazyListState,
    sections: List<DrawerSection>,
    searchActive: Boolean,
    searchQuery: String,
    searchFocusRequester: FocusRequester,
    onLaunch: (LaunchableEntry) -> Unit,
    onLongPress: (LaunchableEntry) -> Unit,
    onNavigateBack: () -> Unit,
    onEnterSearch: () -> Unit,
    onQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onCancelSearch: () -> Unit,
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
    LaunchedEffect(key1 = searchActive) {
        if (searchActive) {
            indexJumpJob?.cancel()
            activeIndexLabel = null
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(insets = WindowInsets.safeDrawing),
    ) {
        DrawerSearchTopBar(
            searchActive = searchActive,
            query = searchQuery,
            focusRequester = searchFocusRequester,
            onNavigateBack = onNavigateBack,
            onEnterSearch = onEnterSearch,
            onQueryChange = onQueryChange,
            onClearSearch = onClearSearch,
            onCancelSearch = onCancelSearch,
        )
        val horizontalPadding = dimensionResource(R.dimen.drawer_horizontal_padding)
        val indexWidth = dimensionResource(R.dimen.drawer_index_width)
        Box(
            modifier = modifier
                .weight(weight = 1f)
                .fillMaxWidth(),
        ) {
            if (searchActive && searchQuery.isNotBlank() && sections.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag(tag = "drawer_search_empty"),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(id = R.string.drawer_no_matching_apps),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag(tag = "drawer_application_list"),
                    contentPadding = PaddingValues(
                        start = horizontalPadding,
                        end = horizontalPadding + indexWidth,
                    ),
                    state = listState,
                ) {
                    sections.forEach { section ->
                        item(key = "section:${section.label}") {
                            DrawerSectionHeader(label = section.label)
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
                                searchQuery = searchQuery.takeIf { searchActive },
                                onLaunch = onLaunch,
                                onLongPress = onLongPress,
                            )
                        }
                    }
                    if (!searchActive) {
                        item(key = "section:settings") {
                            DrawerSectionHeader(
                                label = stringResource(id = R.string.settings),
                                modifier = Modifier.testTag(tag = "drawer_settings_anchor"),
                            )
                        }
                        item(key = "settings") {
                            DrawerSettingsRow(onClick = onOpenSettings)
                        }
                    }
                }
                if (!searchActive || sections.isNotEmpty()) {
                    DrawerAlphabetIndex(
                        labels = sections.map(transform = DrawerSection::label),
                        modifier = Modifier.align(alignment = Alignment.CenterEnd),
                        onSelect = { label, immediate ->
                            val anchor = sectionAnchors.getValue(label)
                            hapticFeedback.performHapticFeedback(
                                hapticFeedbackType = HapticFeedbackType.SegmentTick,
                            )
                            indexJumpJob?.cancel()
                            indexJumpJob = coroutineScope.launch {
                                if (immediate) {
                                    listState.scrollToItem(index = anchor)
                                } else {
                                    listState.animateScrollToItem(index = anchor)
                                }
                            }
                        },
                        onActiveLabelChange = { label -> activeIndexLabel = label },
                        onSelectSettings = { immediate ->
                            if (!searchActive) {
                                hapticFeedback.performHapticFeedback(
                                    hapticFeedbackType = HapticFeedbackType.SegmentTick,
                                )
                                indexJumpJob?.cancel()
                                indexJumpJob = coroutineScope.launch {
                                    if (immediate) {
                                        listState.scrollToItem(index = settingsAnchor)
                                    } else {
                                        listState.animateScrollToItem(index = settingsAnchor)
                                    }
                                }
                            }
                        },
                        includeSettings = !searchActive,
                    )
                }
            }

            activeIndexLabel?.let { label ->
                DrawerIndexBubble(
                    label = label,
                    modifier = Modifier
                        .align(alignment = Alignment.CenterEnd)
                        .padding(
                            end = indexWidth + dimensionResource(
                                id = R.dimen.drawer_index_bubble_index_gap,
                            ),
                        ),
                )
            }
        }
    }
}

@Composable
private fun DrawerSectionHeader(
    label: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height = dimensionResource(id = R.dimen.drawer_section_header_height))
            .testTag(tag = "drawer_section_$label"),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = dimensionResource(
                    id = R.dimen.shared_large_app_name_text_size,
                ).value.sp,
                lineHeight = dimensionResource(
                    id = R.dimen.shared_large_app_name_line_height,
                ).value.sp,
                fontWeight = FontWeight.Bold,
            ),
        )
    }
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
    searchQuery: String?,
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
        DrawerApplicationName(
            label = entry.label,
            searchQuery = searchQuery,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun DrawerApplicationName(
    label: String,
    searchQuery: String?,
    modifier: Modifier = Modifier,
) {
    val matchRanges = remember(key1 = label, key2 = searchQuery) {
        searchQuery?.let { query ->
            drawerSearchMatchRanges(label = label, query = query)
        }.orEmpty()
    }
    val emphasizedLabel = remember(key1 = label, key2 = matchRanges) {
        buildAnnotatedString {
            append(text = label)
            matchRanges.forEach { range ->
                addStyle(
                    style = SpanStyle(fontWeight = FontWeight.Medium),
                    start = range.first,
                    end = range.last + 1,
                )
            }
        }
    }
    Text(
        text = emphasizedLabel,
        modifier = modifier,
        color = MaterialTheme.colorScheme.onBackground,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.bodyLarge,
    )
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
