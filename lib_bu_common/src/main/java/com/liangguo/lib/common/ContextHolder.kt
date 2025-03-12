package com.liangguo.lib.common

import android.app.Activity
import android.app.Application
import java.lang.ref.WeakReference

/**
 * @author hesleyliang
 * 时间: 2024/9/6 15:56
 * 邮箱: liang.dh@outlook.com
 */
object ContextHolder {

    /** 应用的进程全局Application，在Xposed环境下可能是宿主进程的Application或者null */
    lateinit var application: Application

    /** 当前显示的 Activity */
    private var curActivityRef: WeakReference<Activity>? = null

    /** 当前显示的 Activity */
    var curActivity: Activity?
        set(value) {
            curActivityRef = WeakReference(value)
        }
        get() = curActivityRef?.get()
}

/** 应用的App级别的BuildConfig */
typealias AppBuildConfig = BuildConfig