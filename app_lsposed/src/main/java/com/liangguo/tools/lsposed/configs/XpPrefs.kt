package com.liangguo.tools.lsposed.configs

import android.os.Process
import com.liangguo.lib.common.AppBuildConfig
import com.liangguo.tools.lsposed.utils.log.XposedLogUtils.logE
import com.liangguo.tools.lsposed.utils.prefs.PrefsUtils
import de.robv.android.xposed.XSharedPreferences

/**
 * @author hesleyliang
 * 时间: 2024/9/17 09:31
 * 邮箱: liang.dh@outlook.com
 *
 * Xposed环境下的Preference
 */
internal object XpPrefs {

    val prefsMap = mutableMapOf<String, Any?>()

    fun init() {
        if (prefsMap.isEmpty()) {
            var mXSharedPreferences: XSharedPreferences?
            try {
                mXSharedPreferences =
                    XSharedPreferences(AppBuildConfig.APPLICATION_ID, PrefsUtils.PREFS_NAME)
                mXSharedPreferences.makeWorldReadable()

                var allPrefs = mXSharedPreferences.all
                if (allPrefs == null || allPrefs.isEmpty()) {
                    mXSharedPreferences = XSharedPreferences(PrefsUtils.getSharedPrefsFile())
                    mXSharedPreferences.makeWorldReadable()
                    allPrefs = mXSharedPreferences.all
                    if (allPrefs == null || allPrefs.isEmpty()) {
                        logE(
                            "[UID" + Process.myUid() + "]",
                            "Cannot read module's SharedPreferences, some mods might not work!"
                        )
                    } else {
                        putData(allPrefs)
                    }
                } else {
                    putData(allPrefs)
                }
            } catch (t: Throwable) {
                logE("setXSharedPrefs", t)
            }
        }
    }

    private fun putData(map: Map<String, *>) {
        prefsMap.clear()
        map.forEach { (k, v) ->
            prefsMap[k] = v as Any
        }
    }
}

/** 从Xposed的Preference里取值 */
fun Map<String, Any?>.getBoolean(key: String, defaultValue: Boolean = false) =
    (get(key) as? Boolean) ?: defaultValue


/** 从Xposed的Preference里取值 */
fun Map<String, Any?>.getInt(key: String, defaultValue: Int = 0) =
    (get(key) as? Int) ?: defaultValue
