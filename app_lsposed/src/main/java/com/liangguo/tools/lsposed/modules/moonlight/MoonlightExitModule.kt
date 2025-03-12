package com.liangguo.tools.lsposed.modules.moonlight

import android.app.Activity
import android.view.MotionEvent
import com.liangguo.tools.lsposed.base.IModule
import com.liangguo.tools.lsposed.configs.Constants.PACKAGE_MOONLIGHT
import com.liangguo.tools.lsposed.configs.XpPrefs
import com.liangguo.tools.lsposed.configs.getBoolean
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * @author hesleyliang
 * 时间: 2024/12/31 14:42
 * 邮箱: liang.dh@outlook.com
 *
 * Moonlight 串流的辅助修改，鼠标控制退出界面
 */
internal object MoonlightExitModule : IModule {

    override val hookPackageName = PACKAGE_MOONLIGHT

    override val isEnabled: Boolean
        get() = XpPrefs.prefsMap.getBoolean("prefs_key_moonlight_exit")

    override fun onStartHook(lpparam: XC_LoadPackage.LoadPackageParam) {
        // 查找并拦截指定 Activity 的 onGenericMotionEvent 方法
        XposedHelpers.findAndHookMethod(
            "com.limelight.preferences.StreamSettings",
            lpparam.classLoader,
            "onGenericMotionEvent",
            MotionEvent::class.java,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    val activity = param?.thisObject as? Activity
                    val event = param?.args?.getOrNull(0) as? MotionEvent
                    event?.let {
                        if (onGenericEvent(event)) {
                            // 拦截鼠标中键点击事件
                            param.result = true
                            // 调用 Activity 的 finish 方法
                            activity?.finish()
                        }
                    }
                }
            }
        )
    }

    /**
     * 拦截处理 onGenericMotionEvent 事件
     *
     * @return true 表示拦截处理该事件，false 表示不处理该事件
     */
    private fun onGenericEvent(event: MotionEvent): Boolean {
        return event.action == MotionEvent.ACTION_BUTTON_PRESS &&
                event.buttonState and MotionEvent.BUTTON_TERTIARY != 0
    }
}