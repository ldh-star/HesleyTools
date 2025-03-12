package com.liangguo.lib.common.utils.color

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt


/**
 * @author ldh
 * 时间: 2022/2/5 12:58
 * 邮箱: 2637614077@qq.com
 */
object ColorUtil {

    fun desaturateColor(color: Int, ratio: Float): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)

        hsv[1] = hsv[1] / 1 * ratio + 0.2f * (1.0f - ratio)

        return Color.HSVToColor(hsv)
    }

    fun stripAlpha(@ColorInt color: Int): Int {
        return -0x1000000 or color
    }

    @ColorInt
    fun shiftColor(@ColorInt color: Int, @FloatRange(from = 0.0, to = 2.0) by: Float): Int {
        if (by == 1f) return color
        val alpha = Color.alpha(color)
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[2] *= by // value component
        return (alpha shl 24) + (0x00ffffff and Color.HSVToColor(hsv))
    }

    @ColorInt
    fun darkenColor(@ColorInt color: Int): Int {
        return shiftColor(color, 0.9f)
    }

    @ColorInt
    fun darkenColorTheme(@ColorInt color: Int): Int {
        return shiftColor(color, 0.8f)
    }

    @ColorInt
    fun lightenColor(@ColorInt color: Int): Int {
        return shiftColor(color, 1.1f)
    }

    fun isColorLight(@ColorInt color: Int): Boolean {
        val darkness =
            1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return darkness < 0.4
    }

    @ColorInt
    fun invertColor(@ColorInt color: Int): Int {
        val r = 255 - Color.red(color)
        val g = 255 - Color.green(color)
        val b = 255 - Color.blue(color)
        return Color.argb(Color.alpha(color), r, g, b)
    }

    @ColorInt
    fun adjustAlpha(@ColorInt color: Int, @FloatRange(from = 0.0, to = 1.0) factor: Float): Int {
        val alpha = (Color.alpha(color) * factor).roundToInt()
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.argb(alpha, red, green, blue)
    }

    @ColorInt
    fun withAlpha(@ColorInt baseColor: Int, @FloatRange(from = 0.0, to = 1.0) alpha: Float): Int {
        val a = min(255, max(0, (alpha * 255).toInt())) shl 24
        val rgb = 0x00ffffff and baseColor
        return a + rgb
    }

}

@ColorInt
fun Int.desaturateColor(ratio: Float) = ColorUtil.desaturateColor(this, ratio)


@ColorInt
fun Int.withAlpha(@FloatRange(from = 0.0, to = 1.0) alpha: Float) = ColorUtil.withAlpha(this, alpha)

