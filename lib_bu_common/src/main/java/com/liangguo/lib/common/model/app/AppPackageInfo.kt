package com.liangguo.lib.common.model.app

import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmapOrNull
import com.liangguo.lib.common.utils.formatFileSize
import com.liangguo.lib.common.utils.getAppSize
import com.liangguo.lib.common.utils.getVersionCode
import com.liangguo.lib.common.utils.isDebug

/**
 * @author hesleyliang
 * 时间: 2024/8/28 18:47
 * 邮箱: liang.dh@outlook.com
 *
 * 应用包信息
 *
 * @param name 应用名
 * @param isDebug 是否是Debug包
 * @param oemIcon 各家定制版厂商的Icon
 */
open class AppPackageInfo(
    open val name: String,
    open val isDebug: Boolean,
    open val versionName: String,
    open val versionCode: Long,
    open val packageName: String,
    open val fileSizeStr: String,
    open val icon: Bitmap? = null,
    open var oemIcon: Int? = null,
)

/** 读取为[AppPackageInfo] */
fun android.content.pm.PackageInfo.toAppPackageInfo(packageManager: PackageManager): AppPackageInfo? {
    return applicationInfo?.let { info ->
        AppPackageInfo(
            name = packageManager.getApplicationLabel(info).toString(),
            packageName = packageName,
            versionName = versionName.orEmpty(),
            versionCode = getVersionCode(),
            isDebug = isDebug(),
            fileSizeStr = getAppSize(packageManager).formatFileSize(),
            icon = packageManager.getApplicationIcon(info).toBitmapOrNull()
        )
    }
}