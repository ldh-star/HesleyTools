package com.liangguo.lib.common.utils

import android.app.ActivityManager
import android.content.Context
import android.os.Process
import android.widget.Toast
import com.liangguo.lib.common.ContextHolder

/**
 * @author ldh
 * 时间: 2024/8/24 21:19
 * 邮箱: liang.dh@outlook.com
 */
object AppUtils {


}

/**
 * 尝试执行shell命令
 */
inline fun <T> tryShell(block: () -> T): T? {
    try {
        return block()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

/** 快捷发Toast的方法 */
fun toast(msg: Any, isLong: Boolean = false) {
    Toast.makeText(
        ContextHolder.application,
        msg.toString(),
        if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
    ).show()
}

/** 获取当前的进程名字 */
fun Context.getProcessName(): String? {
    val pid = Process.myPid()
    val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    for (processInfo in manager.runningAppProcesses) {
        if (processInfo.pid == pid) {
            return processInfo.processName
        }
    }
    return null
}

/** 获取当前进程是否是应用的主进程 */
fun Context.isMainProcess(): Boolean {
    val processName = getProcessName()
    return processName != null && processName == packageName
}