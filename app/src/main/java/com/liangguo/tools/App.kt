package com.liangguo.tools

import android.app.Application
import android.content.Context
import android.util.Log
import com.liangguo.lib.common.ContextHolder
import com.liangguo.lib.common.utils.isMainProcess
import com.liangguo.libs.shell.api.Su
import rikka.shizuku.ShizukuProvider
import rikka.sui.Sui

/**
 * @author ldh
 * 时间: 2024/8/24 21:16
 * 邮箱: liang.dh@outlook.com
 */
class App : Application() {
    companion object {
        var isSui = Sui.init(BuildConfig.APPLICATION_ID)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        ContextHolder.application = this
        if (!isSui) {
            ShizukuProvider.enableMultiProcessSupport(isMainProcess())
        }
        KMMService.init(object : KMMService {
            override fun shell(command: String): String {
                return Su.shell(command)
            }

            override fun adbShell(command: String): String {
                // 安卓平台上直接去执行shell就行，不需要加上adb shell字段
                return shell(command)
            }

            override fun println(msg: Any?) {
                Log.e("$packageName.${javaClass.simpleName}", msg.toString())
            }
        })
    }
}