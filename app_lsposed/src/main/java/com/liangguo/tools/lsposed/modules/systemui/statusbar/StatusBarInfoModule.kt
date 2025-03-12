package com.liangguo.tools.lsposed.modules.systemui.statusbar

import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createAfterHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.liangguo.lib.common.utils.findViewById
import com.liangguo.lib.common.utils.findViewByTag
import com.liangguo.tools.lsposed.base.IDarkChangedReceiver
import com.liangguo.tools.lsposed.base.IModule
import com.liangguo.tools.lsposed.base.ISubViewModule
import com.liangguo.tools.lsposed.configs.Constants
import com.liangguo.tools.lsposed.configs.XpPrefs
import com.liangguo.tools.lsposed.configs.getBoolean
import com.liangguo.tools.lsposed.modules.systemui.statusbar.StatusBarInfoModule.SUB_MODULES
import com.liangguo.tools.lsposed.utils.getObjectFieldOrNull
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.lang.ref.WeakReference

/**
 * @author hesleyliang
 * 时间: 2024/9/19 18:54
 * 邮箱: liang.dh@outlook.com
 *
 * 状态栏显示设备信息的模块，其下面可以加载很多子模块进来，通过[SUB_MODULES]来配置
 */
object StatusBarInfoModule : IModule {

    override val hookPackageName = Constants.PACKAGE_SYSTEM_UI

    override val isEnabled: Boolean
        get() = XpPrefs.prefsMap.getBoolean("prefs_key_status_bar_show_info")

    private const val TAG_STATUS_BAR_INFO_MODULE = "StatusBarInfoModule"

    /** 用来装子信息View的容器的tag */
    private const val TAG_STATUS_BAR_CONTAINER_VIEW = "tag_status_bar_container_view"

    /** 这个模块下面的子模块 */
    private val SUB_MODULES: List<IModule> = listOf(CpuInfoModule, BatteryInfoModule)

    /** 用来装状态栏信息View的父容器 */
    private var infoViewContainerRef: WeakReference<LinearLayout>? = null

    /** 用来装状态栏信息View的父容器 */
    val infoViewContainer: LinearLayout?
        get() = infoViewContainerRef?.get()

    override fun onStartHook(lpparam: XC_LoadPackage.LoadPackageParam) {
        val enabledModules = SUB_MODULES.filter { it.isEnabled }
        enabledModules.forEach { module ->
            module.onStartHook(lpparam)
        }
        onHookStatusBarView(enabledModules.mapNotNull { it as? ISubViewModule })
        onHookDarkChanged(enabledModules.mapNotNull { it as? IDarkChangedReceiver })
    }

    /** 针对状态栏的View进行hook */
    private fun onHookStatusBarView(modules: List<ISubViewModule>) {
        if (modules.isEmpty()) return
        try {
            // Hook小米状态栏的左边区域View，注入一个横滑的Layout
            loadClass("com.android.systemui.statusbar.phone.MiuiPhoneStatusBarView")
                .methodFinder()
                .filterByName("onFinishInflate")
                .single()
                .createAfterHook { param ->
                    val statusBar = param.thisObject as? ViewGroup ?: return@createAfterHook
                    val statusBarLeftContainer =
                        statusBar.getObjectFieldOrNull("mStatusBarLeftContainer") as? ViewGroup
                            ?: return@createAfterHook
                    // 保险起见，还是检查一下状态栏是否已经添加过这个View了，不要重复添加
                    statusBarLeftContainer
                        .findViewByTag<LinearLayout>(TAG_STATUS_BAR_CONTAINER_VIEW)
                        .firstOrNull()?.let { view ->
                            infoViewContainerRef = WeakReference(view)
                            return@createAfterHook
                        }
                    // 下面是开始注入View了
                    val context = statusBar.context
                    val linearLayout = LinearLayout(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        tag = TAG_STATUS_BAR_CONTAINER_VIEW
                        orientation = LinearLayout.HORIZONTAL
                        gravity = Gravity.CENTER_VERTICAL
                    }
                    // 把父容器创建的信息通知到子模块里去，让子模块自行管理他们的View
                    modules.forEach { it.onCreate(linearLayout) }
                    infoViewContainerRef = WeakReference(linearLayout)
                    statusBarLeftContainer.addView(
                        linearLayout,
                        getContainerIndex(statusBarLeftContainer)
                    )
                }
        } catch (e: Exception) {
            printLog("hook状态栏并注入View时报错", e)
        }
    }

    /** 针对状态栏的亮暗色切换进行hook */
    private fun onHookDarkChanged(modules: List<IDarkChangedReceiver>) {
        if (modules.isEmpty()) return
        try {
            // Hook小米状态栏的电池View，电池View被执行onDark时会把这个事件传到这来
            loadClass("com.android.systemui.statusbar.views.MiuiBatteryMeterView")
                .methodFinder()
                .filterByName("onDarkChanged")
                .forEach { method ->
                    method.createAfterHook onHook@{ param ->
                        // 方法里带的参数列表
                        val args = param.args?.toList() ?: emptyList()
                        if (args.size == 3 || args.size == 6) {
                            val tintAreas = args[0] as? ArrayList<*> ?: return@onHook
                            val darkIntensity = args[1] as? Float ?: return@onHook
                            val tintColor = args[2] as? Int ?: return@onHook
                            if (args.size == 6) {
                                val lightColor = args[3] as? Int ?: return@onHook
                                val darkColor = args[4] as? Int ?: return@onHook
                                val useTint = args[5] as? Boolean ?: return@onHook
                                modules.forEach {
                                    it.onDarkChanged(
                                        tintAreas,
                                        darkIntensity,
                                        tintColor,
                                        lightColor,
                                        darkColor,
                                        useTint
                                    )
                                }
                            } else {
                                modules.forEach {
                                    it.onDarkChanged(tintAreas, darkIntensity, tintColor)
                                }
                            }
                        }
                    }
                }
        } catch (e: Exception) {
            printLog("hook状态栏亮暗色切换时报错", e)
        }
    }

    /** 从状态栏的父容器中定位到我的信息View需要添加到第几个位置 */
    private fun getContainerIndex(parent: ViewGroup): Int {
        parent.findViewById<View>("drip_notification_icon_area")?.let { iconArea ->
            // 放在通知的左边
            return parent.indexOfChild(iconArea)
        }
        parent.findViewById<View>("pad_clock")?.let { iconArea ->
            // 放在pad的时间View的右边
            return parent.indexOfChild(iconArea) + 1
        }
        parent.findViewById<View>("clock")?.let { iconArea ->
            // 放在手机的时间View的右边
            return parent.indexOfChild(iconArea) + 1
        }
        return 0
    }

    fun printLog(msg: String, e: Exception? = null) {
        XposedBridge.log("$TAG_STATUS_BAR_INFO_MODULE $msg \n${e?.stackTraceToString()}")
        e?.let {
            Log.e(TAG_STATUS_BAR_INFO_MODULE, msg, e)
        } ?: run {
            Log.e(TAG_STATUS_BAR_INFO_MODULE, msg)
        }
    }
}