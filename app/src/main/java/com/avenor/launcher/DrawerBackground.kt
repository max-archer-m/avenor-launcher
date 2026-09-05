package com.avenor.launcher

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.Window
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import java.util.function.Consumer

internal enum class DrawerBackgroundTreatment { Clear, Glass, OpaqueFallback }

internal fun drawerBackgroundTreatment(
    mode: DrawerBackgroundMode,
    blurApplied: Boolean,
): DrawerBackgroundTreatment = when {
    mode == DrawerBackgroundMode.Transparent -> DrawerBackgroundTreatment.Clear
    blurApplied -> DrawerBackgroundTreatment.Glass
    else -> DrawerBackgroundTreatment.OpaqueFallback
}

/** Own only the blur flag/radius; preserve other window flags and restore previous values. */
internal class DrawerWindowBlur(private val window: Window) : AutoCloseable {
    private val originalEnabled = window.attributes.flags and WindowManager.LayoutParams.FLAG_BLUR_BEHIND != 0
    private val originalRadius = window.attributes.blurBehindRadius

    fun apply(radius: Int): Boolean = runCatching {
        update(radius > 0, radius)
        radius > 0
    }.getOrElse {
        runCatching { update(false, 0) }
        false
    }

    private fun update(enabled: Boolean, radius: Int) {
        val attributes = window.attributes
        attributes.flags = if (enabled) {
            attributes.flags or WindowManager.LayoutParams.FLAG_BLUR_BEHIND
        } else {
            attributes.flags and WindowManager.LayoutParams.FLAG_BLUR_BEHIND.inv()
        }
        attributes.setBlurBehindRadius(radius)
        window.attributes = attributes
    }

    override fun close() {
        runCatching { update(originalEnabled, originalRadius) }
    }
}

private fun Context.activity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.takeIf { it !== this }?.activity()
    else -> null
}

@Composable
private fun rememberDrawerBlurApplied(requested: Boolean): Boolean {
    val context = LocalContext.current
    val activity = remember(context) { context.activity() }
    val focused = LocalWindowInfo.current.isWindowFocused
    val radius = with(LocalDensity.current) {
        dimensionResource(R.dimen.drawer_background_blur_radius).roundToPx()
    }
    var applied by remember { mutableStateOf(false) }
    DisposableEffect(activity, requested, focused, radius) {
        applied = false
        if (activity == null || !requested || !focused) {
            onDispose { }
        } else {
            val manager = activity.getSystemService(WindowManager::class.java)
            val session = DrawerWindowBlur(activity.window)
            var disposed = false
            val listener = Consumer<Boolean> { available ->
                if (!disposed) applied = session.apply(if (available) radius else 0)
            }
            val registered = runCatching {
                listener.accept(manager.isCrossWindowBlurEnabled)
                manager.addCrossWindowBlurEnabledListener(activity.mainExecutor, listener)
            }.isSuccess
            if (!registered) {
                session.close()
                applied = false
            }
            onDispose {
                disposed = true
                try {
                    if (registered) manager.removeCrossWindowBlurEnabledListener(listener)
                } finally {
                    session.close()
                }
            }
        }
    }
    return requested && focused && applied
}

private val LocalDrawerForegroundShadow = staticCompositionLocalOf { false }

@Composable
internal fun DrawerBackgroundSurface(
    mode: DrawerBackgroundMode,
    active: Boolean,
    content: @Composable () -> Unit,
) {
    val applied = rememberDrawerBlurApplied(active && mode == DrawerBackgroundMode.FrostedGlass)
    val treatment = drawerBackgroundTreatment(mode, applied)
    val color = when (treatment) {
        DrawerBackgroundTreatment.Clear -> Color.Transparent
        DrawerBackgroundTreatment.Glass -> colorResource(R.color.avenor_sheet_surface).copy(
            alpha = integerResource(R.integer.drawer_glass_tint_alpha_percent) / 100f,
        )
        DrawerBackgroundTreatment.OpaqueFallback -> colorResource(R.color.avenor_sheet_surface).copy(
            alpha = integerResource(R.integer.drawer_glass_fallback_alpha_percent) / 100f,
        )
    }
    val shadow = if (mode == DrawerBackgroundMode.Transparent) drawerForegroundShadow() else null
    MaterialTheme(typography = MaterialTheme.typography.withShadow(shadow)) {
        CompositionLocalProvider(LocalTextStyle provides LocalTextStyle.current.copy(shadow = shadow)) {
            CompositionLocalProvider(LocalDrawerForegroundShadow provides (shadow != null)) {
                Box(Modifier.fillMaxSize().background(color).testTag("drawer_background_surface")) {
                    content()
                }
            }
        }
    }
}

/** Opaque panels retain their own contrast treatment; Home never enters the Drawer provider. */
@Composable
internal fun DrawerPanelAppearance(content: @Composable () -> Unit) {
    MaterialTheme(typography = MaterialTheme.typography.withShadow(null)) {
        CompositionLocalProvider(LocalTextStyle provides LocalTextStyle.current.copy(shadow = null)) {
            CompositionLocalProvider(LocalDrawerForegroundShadow provides false, content = content)
        }
    }
}

@Composable
private fun drawerForegroundShadow(): Shadow {
    val color = colorResource(R.color.drawer_foreground_shadow)
    val x = dimensionResource(R.dimen.drawer_foreground_shadow_offset_x)
    val y = dimensionResource(R.dimen.drawer_foreground_shadow_offset_y)
    val radius = dimensionResource(R.dimen.drawer_foreground_shadow_radius)
    return with(LocalDensity.current) { Shadow(color, Offset(x.toPx(), y.toPx()), radius.toPx()) }
}

@Composable
internal fun DrawerIcon(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    if (!LocalDrawerForegroundShadow.current) {
        Icon(painter, contentDescription, modifier, tint)
        return
    }
    Box(modifier, propagateMinConstraints = true) {
        Icon(
            painter = painter,
            contentDescription = null,
            tint = colorResource(R.color.drawer_foreground_shadow),
            modifier = Modifier.matchParentSize()
                .offset(
                    x = dimensionResource(R.dimen.drawer_foreground_shadow_offset_x),
                    y = dimensionResource(R.dimen.drawer_foreground_shadow_offset_y),
                )
                .blur(
                    radius = dimensionResource(R.dimen.drawer_foreground_shadow_radius),
                    edgeTreatment = BlurredEdgeTreatment.Unbounded,
                ),
        )
        Icon(painter = painter, contentDescription = contentDescription, tint = tint)
    }
}

private fun Typography.withShadow(shadow: Shadow?) = copy(
    displayLarge = displayLarge.copy(shadow = shadow),
    displayMedium = displayMedium.copy(shadow = shadow),
    displaySmall = displaySmall.copy(shadow = shadow),
    headlineLarge = headlineLarge.copy(shadow = shadow),
    headlineMedium = headlineMedium.copy(shadow = shadow),
    headlineSmall = headlineSmall.copy(shadow = shadow),
    titleLarge = titleLarge.copy(shadow = shadow),
    titleMedium = titleMedium.copy(shadow = shadow),
    titleSmall = titleSmall.copy(shadow = shadow),
    bodyLarge = bodyLarge.copy(shadow = shadow),
    bodyMedium = bodyMedium.copy(shadow = shadow),
    bodySmall = bodySmall.copy(shadow = shadow),
    labelLarge = labelLarge.copy(shadow = shadow),
    labelMedium = labelMedium.copy(shadow = shadow),
    labelSmall = labelSmall.copy(shadow = shadow),
)
