package com.liangguo.lib.common.utils

import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.liangguo.lib.common.ContextHolder
import kotlin.reflect.KClass

/**
 * @author hesleyliang
 * 时间: 2024/5/11 16:08
 * 邮箱: liang.dh@outlook.com
 */
/** 启动指定包名的应用，应用不存在就返回false */
fun Context.launchApp(packageName: String, className: String? = null): Boolean {
    val start: (Intent) -> Unit = start@{ intent ->
        if (this !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }
    // 先尝试用packageName启动
    packageManager.getLaunchIntentForPackage(packageName)?.let { intent ->
        start(intent)
        return true
    }
    className ?: return false
    // 不行的话直接对着Activity启动
    startAppActivity(packageName, className)
    return false
}

/** 启动某个app的某个Activity */
fun Context.startAppActivity(packageName: String?, className: String?): Boolean {
    packageName ?: return false
    className ?: return false
    val start: (Intent) -> Unit = start@{ intent ->
        if (this !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }
    try {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setClassName(packageName, className)
        start(intent)
        return true
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
}

/** 利用Intent的Action来启动界面 */
fun Context.startActivityByAction(action: String): Boolean {
    try {
        val intent = Intent(action)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        return true
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
}

/** 获取当前输入法的包名 */
fun Context.getCurrentIMEPackageName(): String? {
    val inputMethodId =
        Settings.Secure.getString(contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD)
            ?: return null
    val index = inputMethodId.indexOf("/")
    val packageName = inputMethodId.substring(0, index)
    return packageName
}

/**
 * 打开系统输入法选择弹窗
 */
fun Context.showInputMethodPicker() {
    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showInputMethodPicker()
}

/** 打开应用详情界面 */
fun Context.openAppDetails(packageName: String) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.parse("package:$packageName")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    }
}


/**
 * 切割边界的转场动画。
 * 加了个动画而已，还是调用的是[startNewActivity]。
 */
fun KClass<out Activity>.startActivityWithClipRevealAnimation(
    context: Context,
    intent: Intent = Intent(context, java).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) },
    sourceView: View,
    configIntent: Intent.() -> Unit = {}
) {
    val options = ActivityOptionsCompat.makeClipRevealAnimation(
        sourceView,
        0,
        0,
        sourceView.width,
        sourceView.height
    )
    startNewActivity(
        context = context,
        intent = intent,
        bundle = options.toBundle(),
        configIntent = configIntent
    )
}

/**
 * 通过context和Activity的Class启动一个新Activity。
 *
 * - 注意：在这里Intent会默认加入[Intent.FLAG_ACTIVITY_NEW_TASK]的属性，如果不要此属性，可以自定义Intent传进来，也可以在函数块中手动移除。
 *
 * @param intent 默认有一个，也可以自己设。
 * @param bundle 默认为空。
 * @param configIntent 在这个函数块里可以进行对Intent配置。
 */
fun Class<out Activity>.startNewActivity(
    context: Context,
    intent: Intent = Intent(context, this).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) },
    bundle: Bundle? = null,
    configIntent: Intent.() -> Unit = {}
) {
    configIntent(intent)
    context.startActivity(intent, bundle)
}

/**
 * 通过context和Activity的Class启动一个新Activity。
 *
 * - 注意：在这里Intent会默认加入[Intent.FLAG_ACTIVITY_NEW_TASK]的属性，如果不要此属性，可以自定义Intent传进来，也可以在函数块中手动移除。
 *
 * @param intent 默认有一个，也可以自己设。
 * @param bundle 默认为空。
 * @param configIntent 在这个函数块里可以进行对Intent配置。
 */
fun KClass<out Activity>.startNewActivity(
    context: Context = ContextHolder.application,
    intent: Intent = Intent(context, this.java).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) },
    bundle: Bundle? = null,
    configIntent: Intent.() -> Unit = {}
) = java.startNewActivity(context, intent, bundle, configIntent)

/**
 * 为Lifecycle添加监听。
 *
 * @return LifecycleEventObserver的实体。
 */
fun Lifecycle.observe(block: (Lifecycle.Event) -> Unit) =
    LifecycleEventObserver { _, event -> block(event) }.also { addObserver(it) }
