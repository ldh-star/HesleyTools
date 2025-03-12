package com.liangguo.lib.common.ime

import android.content.Context
import android.graphics.Bitmap
import android.view.inputmethod.InputMethodInfo
import androidx.core.graphics.drawable.toBitmapOrNull
import com.liangguo.lib.common.model.app.AppPackageInfo
import com.liangguo.lib.common.model.app.toAppPackageInfo
import com.liangguo.lib.common.utils.getInputMethods

/**
 * 输入法的应用信息
 *
 * @param imsClassName IMS的名称
 * @param imeInfo 输入法信息
 */
data class IMEPackageInfo(
    override val name: String,
    val imeInfo: InputMethodInfo,
    val imsClassName: String,
    override val isDebug: Boolean,
    override val versionName: String,
    override val versionCode: Long,
    override val packageName: String,
    override val fileSizeStr: String,
    override var icon: Bitmap? = null,
    override var oemIcon: Int? = null,
) : AppPackageInfo(
    name = name,
    isDebug = isDebug,
    versionName = versionName,
    versionCode = versionCode,
    packageName = packageName,
    fileSizeStr = fileSizeStr,
    icon = icon,
    oemIcon = oemIcon,
)

/**
 * 获取当前输入法应用列表
 */
fun Context.getIMEInfoList(): List<IMEPackageInfo> {
    return getInputMethods().mapNotNull map@{ inputMethod ->
        try {
            val packageInfo = packageManager.getPackageInfo(inputMethod.packageName, 0)
            val appPackageInfo = packageInfo.toAppPackageInfo(packageManager) ?: return@map null
            IMEPackageInfo(
                name = appPackageInfo.name,
                packageName = appPackageInfo.packageName,
                imeInfo = inputMethod,
                imsClassName = inputMethod.serviceInfo.name,
                versionName = appPackageInfo.versionName,
                versionCode = appPackageInfo.versionCode,
                isDebug = appPackageInfo.isDebug,
                fileSizeStr = appPackageInfo.fileSizeStr,
                icon = inputMethod.loadIcon(packageManager).toBitmapOrNull()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}