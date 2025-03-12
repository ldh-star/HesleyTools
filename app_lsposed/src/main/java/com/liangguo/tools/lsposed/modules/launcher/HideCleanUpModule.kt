package com.liangguo.tools.lsposed.modules.launcher

import android.os.Build
import android.view.View
import com.highcapable.yukihookapi.hook.factory.toClass
import com.liangguo.tools.lsposed.base.IModule
import com.liangguo.tools.lsposed.configs.Constants.PACKAGE_MIUI_HOME
import com.liangguo.tools.lsposed.configs.XpPrefs
import com.liangguo.tools.lsposed.configs.getBoolean
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge.hookAllMethods
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * @author hesleyliang
 * 时间: 2024/9/14 19:20
 * 邮箱: liang.dh@outlook.com
 *
 * 隐藏桌面最近任务中清理的按钮，但是不是直接把View设置为GONE，而是把alpha设置为0，这样View是可以点击的，但是看不见
 */
internal object HideCleanUpModule : IModule {

    override val hookPackageName = PACKAGE_MIUI_HOME

    override val isEnabled: Boolean
        get() = XpPrefs.prefsMap.getBoolean("prefs_key_home_hide_clean_button")

    override fun onStartHook(lpparam: XC_LoadPackage.LoadPackageParam) {
        val clearButtonName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            // 澎湃 2 把清理按钮改了，暂时用 Android 15 作为区分条件
            "mMemoryAndClearContainer"
        } else {
            "mClearAnimView"
        }
        hookAllMethods(
            "com.miui.home.recents.views.RecentsContainer".toClass(lpparam.classLoader),
            "onFinishInflate",
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    val view =
                        XposedHelpers.getObjectField(
                            param?.thisObject,
                            clearButtonName
                        ) as? View
                    view?.alpha = 0f
                }
            }
        )
    }
}