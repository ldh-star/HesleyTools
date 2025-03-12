package com.liangguo.tools.lsposed.base

/**
 * @author hesleyliang
 * 时间: 2024/9/26 13:06
 * 邮箱: liang.dh@outlook.com
 *
 * 亮色暗色切换时候的接口
 */
interface IDarkChangedReceiver {

    /**
     * 当亮色暗色切换时调用
     *
     * @param tintAreas 着色范围
     * @param darkIntensity 暗强度
     * @param tintColor 需要新显示的颜色
     */
    fun onDarkChanged(
        tintAreas: List<Any>,
        darkIntensity: Float,
        tintColor: Int,
    ) {
    }

    /**
     * 当亮色暗色切换时调用
     *
     * @param tintAreas 着色范围
     * @param darkIntensity 暗强度
     * @param tintColor 需要新显示的颜色
     * @param lightColor 亮色的颜色
     * @param useTint 暗色的颜色
     */
    fun onDarkChanged(
        tintAreas: List<Any>,
        darkIntensity: Float,
        tintColor: Int,
        lightColor: Int,
        darkColor: Int,
        useTint: Boolean
    ) {
    }
}