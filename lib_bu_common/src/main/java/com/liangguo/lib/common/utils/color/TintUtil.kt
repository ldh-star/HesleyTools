package com.liangguo.lib.common.utils.color

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.DrawableCompat


/**
 * @author ldh
 * 时间: 2022/2/5 13:09
 * 邮箱: 2637614077@qq.com
 */

object TintUtil {

    /**
     * 为drawable设置tint
     */
    fun createTintedDrawable(drawable: Drawable, @ColorInt color: Int): Drawable {
        val returnDrawable = DrawableCompat.wrap(drawable.mutate())
        DrawableCompat.setTintMode(returnDrawable, PorterDuff.Mode.SRC_IN)
        DrawableCompat.setTint(returnDrawable, color)
        return returnDrawable
    }
}