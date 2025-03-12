package com.liangguo.libs.shell.model

/**
 * @author ldh
 * 时间: 2024/8/24 21:04
 * 邮箱: liang.dh@outlook.com
 *
 * 屏幕方向
 */
sealed interface ScreenRotation {

    val id: Int

    val name: String

    val description: String

    val isPort: Boolean

    val isLand: Boolean

    companion object {
        val LIST = listOf(Port, Landscape, ReversePort, ReverseLandscape)

        val DEFAULT: ScreenRotation = Port

        fun get(i: Int) = LIST[i % LIST.size]
    }

    /**
     * 竖屏（正常方向）
     */
    data object Port : ScreenRotation {
        override val id = 0
        override val name = "竖屏"
        override val description = "正常方向"
        override val isPort = true
        override val isLand = !isPort
    }

    /**
     * 横屏（顺时针90度）
     */
    data object Landscape : ScreenRotation {
        override val id = 1
        override val name = "横屏"
        override val description = "顺时针90度"
        override val isPort = false
        override val isLand = !isPort
    }

    /**
     * 反向竖屏（180度旋转）
     */
    data object ReversePort : ScreenRotation {
        override val id = 2
        override val name = "反向竖屏"
        override val description = "180度旋转"
        override val isPort = true
        override val isLand = !isPort
    }

    /**
     * 反向横屏（顺时针270度）
     */
    data object ReverseLandscape : ScreenRotation {
        override val id = 3
        override val name = "反向横屏"
        override val description = "顺时针270度"
        override val isPort = false
        override val isLand = !isPort
    }
}

fun ScreenRotation.next() = ScreenRotation.get((ScreenRotation.LIST.indexOf(this) + 1))