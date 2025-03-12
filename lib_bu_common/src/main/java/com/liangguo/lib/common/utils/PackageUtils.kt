package com.liangguo.lib.common.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.view.inputmethod.InputMethodInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import java.io.File

/**
 * @author hesleyliang
 * 时间: 2024/8/28 17:24
 * 邮箱: liang.dh@outlook.com
 */
/** 一个应用是否是debug应用 */
fun PackageInfo.isDebug(): Boolean {
    return (applicationInfo?.flags ?: 0) and ApplicationInfo.FLAG_DEBUGGABLE != 0
}

/** 获取输入法列表 */
fun Context.getInputMethods(): List<InputMethodInfo> {
    val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    return inputMethodManager.inputMethodList
}

/** 获取已启用输入法列表 */
fun Context.getEnabledInputMethods(): List<InputMethodInfo> {
    val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    return inputMethodManager.enabledInputMethodList
}

/**
 * 复制文字到剪切板
 */
fun Context.copyToClipboard(text: String?) {
    if (text.isNullOrEmpty()) {
        return
    }
    val clipboardManager = ContextCompat.getSystemService(this, ClipboardManager::class.java)
    val clipData = ClipData.newPlainText("label", text)
    clipboardManager?.setPrimaryClip(clipData)
}

/** 获取应用的VersionCode */
fun PackageInfo.getVersionCode(): Long {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        longVersionCode
    } else {
        versionCode.toLong()
    }
}

/** 获取应用图标 */
fun PackageManager.getAppIcon(packageName: String): Bitmap? {
    return try {
        getApplicationIcon(packageName).toBitmap()
    } catch (e: Exception) {
        null
    }
}

/** 获取应用的包体积 */
fun PackageInfo.getAppSize(packageManager: PackageManager): Long {
    return try {
        applicationInfo?.let {
            File(it.sourceDir).length()
        } ?: 0L
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        0L
    }
}


