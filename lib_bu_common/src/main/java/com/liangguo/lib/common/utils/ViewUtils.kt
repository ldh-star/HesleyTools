package com.liangguo.lib.common.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Resources
import android.graphics.Outline
import android.graphics.drawable.InsetDrawable
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.annotation.MenuRes
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.PopupMenu

/**
 * @author hesleyliang
 * 时间: 2024/9/19 10:20
 * 邮箱: liang.dh@outlook.com
 */

/**
 * 在任意View处弹出一个popup菜单
 *
 * @param excludeIds 为了尽量使得menu.xml复用，可以在这里设置不需要展示的menu项
 */
@SuppressLint("RestrictedApi")
fun View.showPopupMenu(
    @MenuRes menuRes: Int,
    excludeIds: List<Int>? = null,
    onDismissListener: ((PopupMenu) -> Unit)? = null,
    onItemClick: (MenuItem) -> Boolean
) {
    val popup = PopupMenu(context, this)
    popup.menuInflater.inflate(menuRes, popup.menu)
    popup.setOnMenuItemClickListener(onItemClick)
    excludeIds?.forEach {
        popup.menu.removeItem(it)
    }
    onDismissListener?.let {
        popup.setOnDismissListener(it)
    }
    if (popup.menu is MenuBuilder) {
        val menuBuilder = popup.menu as MenuBuilder
        menuBuilder.setOptionalIconsVisible(true)
        for (item in menuBuilder.visibleItems) {
            val iconMarginPx =
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 5f, resources.displayMetrics
                )
                    .toInt()
            if (item.icon != null) {
                item.icon = InsetDrawable(item.icon, iconMarginPx, 0, iconMarginPx, 0)
            }
        }
    }
    popup.show()
}

/**
 * 调用Activity中的setContentView，并对设置的这个View进行配置。
 * @param view 要添加进ContentView的View
 * @param viewConfig 在这个函数块中可以给View做配置
 */
fun <V : View> Activity.setContentView(view: V, viewConfig: V.() -> Unit = {}) {
    setContentView(view)
    view.viewConfig()
}

/** 通过 id 的名字来查找子View */
fun <T : View> View.findViewById(id: String): T? {
    val findId = context.resources.getIdentifier(id, "id", context.packageName)
    return findViewById(findId)
}

/**
 * 扩展方法：在视图层次结构中查找具有指定标签的所有视图。
 *
 * @param tag 要查找的标签。
 * @return 一个包含所有具有指定标签的视图的列表。
 */
inline fun <reified T : View> View.findViewByTag(tag: Any): List<T> {
    val result = mutableListOf<View>()
    findViewByTagRecursive(this, tag, result)
    return result.mapNotNull { it as? T }
}

/**
 * 递归方法：在视图层次结构中查找具有指定标签的所有视图。
 *
 * @param view 当前视图。
 * @param tag 要查找的标签。
 * @param result 用于存储结果的列表。
 */
fun findViewByTagRecursive(view: View, tag: Any, result: MutableList<View>) {
    if (view.tag == tag) {
        result.add(view)
    }
    if (view is ViewGroup) {
        for (i in 0 until view.childCount) {
            findViewByTagRecursive(view.getChildAt(i), tag, result)
        }
    }
}

/**
 * dp转px
 */
inline val Float.dp2px: Float
    get() = this * (Resources.getSystem().displayMetrics.density) + 0.5f


/**
 * dp转px
 */
inline val Int.dp2px
    get() = toFloat().dp2px

/**
 * 切割View圆角
 *
 * @param pixel 输入进来的是像素点，调用时应使用[dp2px]。
 */
fun <V : View> V.clipRoundCorner(pixel: Float) = this.apply {
    clipToOutline = true
    outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            outline.setRoundRect(0, 0, view.width, view.height, pixel)
        }
    }
}

/** 获取View的宽度dp */
val View.widthDp: Float
    get() {
        val widthInPx = width.toFloat()
        val density = resources.displayMetrics.density
        return widthInPx / density
    }
