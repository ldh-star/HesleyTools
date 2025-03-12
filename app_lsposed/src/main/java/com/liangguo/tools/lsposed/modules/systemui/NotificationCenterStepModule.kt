package com.liangguo.tools.lsposed.modules.systemui

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.net.Uri
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createAfterHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.liangguo.lib.common.utils.startAppActivity
import com.liangguo.tools.lsposed.base.IApplicationHolder
import com.liangguo.tools.lsposed.base.IModule
import com.liangguo.tools.lsposed.configs.Constants
import com.liangguo.tools.lsposed.configs.XpPrefs
import com.liangguo.tools.lsposed.configs.getBoolean
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.lang.ref.WeakReference

/**
 * @author hesleyliang
 * 时间: 2024/9/18 21:52
 * 邮箱: liang.dh@outlook.com
 *
 * 通知中心页面显示步数
 */
object NotificationCenterStepModule : IModule, IApplicationHolder {

    override val hookPackageName = Constants.PACKAGE_SYSTEM_UI

    override val isEnabled: Boolean
        get() = XpPrefs.prefsMap.getBoolean("prefs_key_notification_center_show_step")

    private val textViewsRef = mutableListOf<WeakReference<TextView>>()

    private var contextRef: WeakReference<Context>? = null

    /** Application处拿到的Context */
    private val context: Context?
        get() = contextRef?.get()

    /** 显示步数的View */
    private val textViews: List<TextView>
        get() = textViewsRef.mapNotNull { it.get() }

    /** 显示的步数文字 */
    private var stepText = ""

    override fun onCreate(application: Application) {
        contextRef = WeakReference(application)
        val timeTickReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                updateSteps()
            }
        }
        application.registerReceiver(
            timeTickReceiver,
            IntentFilter("android.intent.action.TIME_TICK")
        )
    }

    override fun onStartHook(lpparam: XC_LoadPackage.LoadPackageParam) {
        hookNotificationView()
        hookNotificationViewUpdate()
    }

    private fun hookNotificationView() {
        loadClass("com.android.systemui.qs.MiuiNotificationHeaderView")
            .methodFinder()
            .filterByName("onFinishInflate")
            .single()
            .createAfterHook { param ->
                val headerView = param.thisObject as? ViewGroup ?: return@createAfterHook
                headerView.post { onHookHeaderView(headerView) }
            }
    }

    private fun onHookHeaderView(headerView: ViewGroup) {
        val context = headerView.context
        val lp = ConstraintLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        lp.marginStart = context.resources.getDimensionPixelSize(
            context.resources.getIdentifier(
                "notification_panel_time_date_space",
                "dimen",
                context.packageName
            )
        )

        val textView = TextView(context).apply {
            setTextAppearance(
                context.resources.getIdentifier(
                    "TextAppearance.QSControl.Date",
                    "style",
                    context.packageName
                )
            )
            setOnClickListener {
                runCatching {
                    // 点击后启动小米运动健康
                    context.startAppActivity(
                        "com.mi.health",
                        "com.xiaomi.fitness.main.MainActivity"
                    )
                }
            }
            text = stepText
        }
        headerView.addView(textView, lp)
        textViewsRef.add(WeakReference(textView))
    }

    private fun hookNotificationViewUpdate() {
        loadClass("com.android.systemui.qs.MiuiNotificationHeaderView").methodFinder()
            .filterByName("updateLayout")
            .single()
            .createAfterHook {
                val list = textViews
                if (list.isEmpty()) return@createAfterHook
                val viewGroup = it.thisObject as ViewGroup
                val orientation =
                    XposedHelpers.getObjectField(viewGroup, "mOrientation") as Int
                list.forEach { tv ->
                    tv.isVisible = orientation != Configuration.ORIENTATION_LANDSCAPE
                }
            }
    }

    /** 更新步数显示 */
    private fun updateSteps() {
        if (textViews.isEmpty()) return
        val uri = Uri.parse("content://com.mi.health.provider.main/activity/steps/brief")
        val thisContext = context ?: return
        try {
            val cursor =
                thisContext.contentResolver.query(uri, arrayOf("steps", "goal"), null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val stepCount = cursor.getString(0)
                cursor.close()
                val newText = "${stepCount}步"
                if (newText == stepText) {
                    return
                }
                stepText = newText
                textViews.forEach {
                    it.text = stepText
                }
            }
        } catch (t: Throwable) {
            XposedBridge.log(t)
        }
    }
}