package com.avenor.launcher.ui.home.components

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventTimeoutCancellationException
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.positionChangeIgnoreConsumed
import kotlinx.coroutines.withTimeout

/**
 * Waits for the platform long-press timeout without consuming the initial press. This keeps
 * scrolling available until the gesture has actually become a reorder drag.
 */
internal suspend fun AwaitPointerEventScope.awaitHomeHandleLongPress(
    down: PointerInputChange,
): PointerInputChange? {
    var current = down
    return try {
        withTimeout(
            timeMillis = viewConfiguration.longPressTimeoutMillis,
            block = {
                while (true) {
                    val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                    if (event.changes.any(predicate = { it.id != down.id && it.pressed })) {
                        return@withTimeout null
                    }
                    val change = event.changes.firstOrNull(predicate = { it.id == down.id })
                        ?: return@withTimeout null
                    if (change.changedToUpIgnoreConsumed()) return@withTimeout null
                    if ((change.position - down.position).getDistance() >=
                        viewConfiguration.touchSlop
                    ) {
                        return@withTimeout null
                    }
                    current = change
                }
                @Suppress("UNREACHABLE_CODE")
                null
            },
        )
    } catch (_: PointerEventTimeoutCancellationException) {
        current
    }
}

internal suspend fun PointerInputScope.detectHomeReorderDrag(
    onPressChanged: (Boolean) -> Unit,
    onLongPress: () -> Unit,
    onDragStart: (Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
) {
    awaitEachGesture(
        block = {
            val down = awaitFirstDown(
                requireUnconsumed = false,
                pass = PointerEventPass.Initial,
            )
            val longPress = awaitHomeHandleLongPress(down = down) ?: return@awaitEachGesture
            var dragging = false
            var cancelled = false
            try {
                longPress.consume()
                onLongPress()
                while (true) {
                    val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                    if (event.changes.any(predicate = { it.id != down.id && it.pressed })) {
                        cancelled = true
                        break
                    }
                    val change = event.changes.firstOrNull(predicate = { it.id == down.id })
                    if (change == null) {
                        cancelled = true
                        break
                    }
                    val movement = change.positionChangeIgnoreConsumed()
                    if (change.changedToUpIgnoreConsumed()) {
                        if (dragging && movement != Offset.Zero) {
                            change.consume()
                            onDrag(movement)
                        }
                        break
                    }
                    change.consume()
                    if (!dragging) {
                        if (movement == Offset.Zero) continue
                        dragging = true
                        onPressChanged(true)
                        onDragStart(longPress.position)
                    }
                    onDrag(movement)
                }
                if (dragging) {
                    dragging = false
                    if (cancelled) onDragCancel() else onDragEnd()
                }
            } finally {
                onPressChanged(false)
                if (dragging) onDragCancel()
            }
        },
    )
}
