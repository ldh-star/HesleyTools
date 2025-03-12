package com.liangguo.tools.lsposed

import android.app.Application
import com.github.kyuubiran.ezxhelper.EzXHelper
import com.highcapable.yukihookapi.hook.factory.toClass
import com.liangguo.tools.lsposed.base.IApplicationHolder
import com.liangguo.tools.lsposed.base.IModule
import com.liangguo.tools.lsposed.configs.Constants
import com.liangguo.tools.lsposed.configs.XpPrefs
import com.liangguo.tools.lsposed.modules.TestModule
import com.liangguo.tools.lsposed.modules.launcher.HideCleanUpModule
import com.liangguo.tools.lsposed.modules.moonlight.MoonlightExitModule
import com.liangguo.tools.lsposed.modules.systemui.NotificationCenterStepModule
import com.liangguo.tools.lsposed.modules.systemui.statusbar.StatusBarInfoModule
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge.hookAllMethods
import de.robv.android.xposed.callbacks.XC_LoadPackage

const val XP_TAG = "HesleyTools"

/**
 * @author Hesley
 * 时间: 2024/3/5 22:07
 * 邮箱: liang.dh@outlook.com
 */
class XposedInit : IXposedHookLoadPackage, IXposedHookZygoteInit {

    companion object {
        private val ALL_MODULES = listOf(
            TestModule,
            HideCleanUpModule,
            NotificationCenterStepModule,
            StatusBarInfoModule,
            MoonlightExitModule,
        )
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        lpparam ?: return
        // 目标进程是否被hook了
        var hooked = false
        // 确定了可以进行hook的模块
        val enabledModules = mutableListOf<IModule>()

        ALL_MODULES.forEach { module ->
            if (lpparam.packageName == module.hookPackageName) {
                if (module.hookProcessName != null && module.hookProcessName != lpparam.processName) {
                    // 进程名字不对的就不要hook了
                    return@forEach
                }
                if (!module.isEnabled) {
                    // 开关没打开的模块也不要hook了
                    return@forEach
                }
                if (!hooked) {
                    // 首次hook的时候进行init，后续再有模块需要hook则直接把代码执行到对应模块上
                    EzXHelper.initHandleLoadPackage(lpparam)
                    hooked = true
                }
                enabledModules.add(module)
            }
        }
        // 现在已经确定好了那些模块要接入hook，也确定好了当前进程是否需要hook
        if (!hooked) {
            // 当前进程没有任何模块可以对它进行hook的
            return
        }
        enabledModules.forEach { module ->
            // 所有需要hook的模块集体启动
            module.onStartHook(lpparam)
        }
        val applicationName =
            Constants.APPLICATION_CLASS_MAP[lpparam.packageName] ?: Application::class.java.name
        // 下面过滤出的是需要对Application初始化进行hook的模块
        val applicationHookModules = enabledModules.mapNotNull { it as? IApplicationHolder }
        if (applicationHookModules.isNotEmpty()) {
            // 有模块需要hook Application的话才会去hook，否则不要随便乱动
            hookAllMethods(
                applicationName.toClass(lpparam.classLoader),
                "onCreate",
                object : XC_MethodHook() {
                    /** 目标应用的Application是否已经被hook了 */
                    private var isHooked = false

                    override fun afterHookedMethod(param: MethodHookParam?) {
                        if (isHooked) {
                            // 只初始化一次，第二次以后就不要重复通知到模块了
                            return
                        }
                        val application = param?.thisObject as? Application ?: return
                        // 通知到每一个applicationHook的模块
                        applicationHookModules.forEach { module ->
                            module.onCreate(application)
                        }
                    }
                }
            )
        }
    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam?) {
        startupParam ?: return
        EzXHelper.initZygote(startupParam)
        EzXHelper.setLogTag(XP_TAG)
        EzXHelper.setToastTag(XP_TAG)
        XpPrefs.init()
    }

}