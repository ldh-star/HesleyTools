package com.liangguo.tools.lsposed.configs

/**
 * @author hesleyliang
 * 时间: 2024/9/14 19:21
 * 邮箱: liang.dh@outlook.com
 */
object Constants {

    /** 小米系统桌面 */
    const val PACKAGE_MIUI_HOME = "com.miui.home"

    /** 系统界面 */
    const val PACKAGE_SYSTEM_UI = "com.android.systemui"

    /** Moonlight 串流客户端 */
    const val PACKAGE_MOONLIGHT = "com.limelight"

    /** 目标应用进程的Application的className映射表 */
    val APPLICATION_CLASS_MAP = mapOf(
        PACKAGE_SYSTEM_UI to "com.android.systemui.SystemUIApplication",
    )
}