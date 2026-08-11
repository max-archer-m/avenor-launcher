package com.avenor.launcher

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.BitmapShader
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Drawable
import android.os.UserHandle
import androidx.core.graphics.createBitmap
import androidx.core.graphics.get
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable

internal sealed interface LauncherIconShape {
    data object SystemAdaptive : LauncherIconShape
}

internal data class LauncherIconAppearance(
    val shape: LauncherIconShape = LauncherIconShape.SystemAdaptive,
)

internal fun interface LauncherIconRenderer {
    fun render(
        source: Drawable,
        user: UserHandle,
        appearance: LauncherIconAppearance,
    ): Drawable
}

internal class SystemLauncherIconRenderer(
    context: Context,
) : LauncherIconRenderer {
    private val applicationContext = context.applicationContext
    private val packageManager = applicationContext.packageManager
    private val resources = applicationContext.resources
    private val iconSizePixels = resources.getDimensionPixelSize(
        R.dimen.drawer_application_icon_size,
    )

    override fun render(
        source: Drawable,
        user: UserHandle,
        appearance: LauncherIconAppearance,
    ): Drawable {
        val normalizedBitmap = when (appearance.shape) {
            LauncherIconShape.SystemAdaptive -> renderSystemAdaptiveIcon(source)
        }
        val normalizedDrawable = normalizedBitmap.toDrawable(resources)

        return packageManager.getUserBadgedIcon(normalizedDrawable, user)
    }

    private fun renderSystemAdaptiveIcon(source: Drawable): Bitmap {
        if (source is AdaptiveIconDrawable) return source.toIconBitmap()

        val analysisSize = maxOf(iconSizePixels * ANALYSIS_SIZE_MULTIPLIER, MINIMUM_ANALYSIS_SIZE)
        val sourceBitmap = source.toBitmap(
            width = analysisSize,
            height = analysisSize,
            config = Bitmap.Config.ARGB_8888,
        )
        return renderLegacyIcon(sourceBitmap)
    }

    private fun Drawable.toIconBitmap(): Bitmap = toBitmap(
        width = iconSizePixels,
        height = iconSizePixels,
        config = Bitmap.Config.ARGB_8888,
    )

    private fun renderLegacyIcon(source: Bitmap): Bitmap {
        val content = createBitmap(
            iconSizePixels,
            iconSizePixels,
        )
        val contentCanvas = Canvas(content)
        contentCanvas.drawColor(source.inferLegacyBackgroundColor())

        val inset = iconSizePixels * LEGACY_CONTENT_INSET_FRACTION
        val destination = RectF(
            inset,
            inset,
            iconSizePixels - inset,
            iconSizePixels - inset,
        )
        contentCanvas.drawBitmap(
            source,
            Rect(0, 0, source.width, source.height),
            destination,
            ICON_BITMAP_PAINT,
        )

        val result = createBitmap(
            iconSizePixels,
            iconSizePixels,
        )
        val resultCanvas = Canvas(result)
        val maskSource = AdaptiveIconDrawable(Color.BLACK.toDrawable(), null).apply {
            bounds = Rect(0, 0, iconSizePixels, iconSizePixels)
        }
        val maskPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
            shader = BitmapShader(content, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        }
        resultCanvas.drawPath(maskSource.iconMask, maskPaint)
        return result
    }

    private companion object {
        const val ANALYSIS_SIZE_MULTIPLIER = 2
        const val MINIMUM_ANALYSIS_SIZE = 192
        const val LEGACY_CONTENT_INSET_FRACTION = 0.1f
        val ICON_BITMAP_PAINT = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    }
}

internal fun Bitmap.inferLegacyBackgroundColor(): Int {
    val edgeWidth = (minOf(width, height) * EDGE_SAMPLE_FRACTION).toInt().coerceAtLeast(1)
    val colorCounts = mutableMapOf<Int, Int>()
    var opaqueSamples = 0
    var totalSamples = 0

    for (y in 0 until height) {
        for (x in 0 until width) {
            if (x >= edgeWidth && x < width - edgeWidth &&
                y >= edgeWidth && y < height - edgeWidth
            ) {
                continue
            }

            totalSamples += 1
            val color = this[x, y]
            if (Color.alpha(color) < BACKGROUND_ALPHA_THRESHOLD) continue

            opaqueSamples += 1
            val quantizedColor = Color.rgb(
                quantizeChannel(Color.red(color)),
                quantizeChannel(Color.green(color)),
                quantizeChannel(Color.blue(color)),
            )
            colorCounts[quantizedColor] = colorCounts.getOrDefault(quantizedColor, 0) + 1
        }
    }

    if (opaqueSamples.toFloat() / totalSamples < MINIMUM_OPAQUE_EDGE_RATIO) return Color.WHITE
    return colorCounts.maxByOrNull(Map.Entry<Int, Int>::value)?.key ?: Color.WHITE
}

private fun quantizeChannel(channel: Int): Int = (channel / COLOR_BUCKET_SIZE) * COLOR_BUCKET_SIZE +
    COLOR_BUCKET_CENTER_OFFSET

private const val EDGE_SAMPLE_FRACTION = 0.08f
private const val BACKGROUND_ALPHA_THRESHOLD = 224
private const val MINIMUM_OPAQUE_EDGE_RATIO = 0.25f
private const val COLOR_BUCKET_SIZE = 16
private const val COLOR_BUCKET_CENTER_OFFSET = 8
