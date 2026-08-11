package com.avenor.launcher

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.roundToInt

@Composable
internal fun SharedMarqueeText(
    text: String,
    eligible: Boolean,
    onOverflowChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val initialDelayMillis = integerResource(R.integer.marquee_endpoint_delay_millis)
    val velocityDpPerSecond = integerResource(R.integer.marquee_velocity_dp_per_second)
    var containerWidth by remember { mutableIntStateOf(0) }
    var textWidth by remember(text) { mutableIntStateOf(0) }
    val offset = remember(text) { Animatable(0f) }
    val overflow = (textWidth - containerWidth).coerceAtLeast(0)

    LaunchedEffect(overflow) {
        onOverflowChanged(overflow > 0)
    }

    LaunchedEffect(eligible, overflow, velocityDpPerSecond) {
        offset.snapTo(0f)
        if (!eligible || overflow <= 0) return@LaunchedEffect

        val overflowDp = with(density) { overflow.toDp().value }
        val travelDurationMillis =
            ((overflowDp / velocityDpPerSecond) * 1_000f).roundToInt().coerceAtLeast(1)
        while (isActive) {
            delay(initialDelayMillis.toLong())
            offset.animateTo(
                targetValue = -overflow.toFloat(),
                animationSpec = tween(travelDurationMillis, easing = LinearEasing),
            )
            delay(initialDelayMillis.toLong())
            offset.animateTo(
                targetValue = 0f,
                animationSpec = tween(travelDurationMillis, easing = LinearEasing),
            )
        }
    }

    Box(
        modifier = modifier
            .clipToBounds()
            .onSizeChanged { size -> containerWidth = size.width },
    ) {
        Text(
            text = text,
            modifier = Modifier
                .wrapContentWidth(unbounded = true)
                .offset { IntOffset(offset.value.roundToInt(), 0) }
                .onSizeChanged { size -> textWidth = size.width },
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            softWrap = false,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
