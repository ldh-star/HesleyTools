package com.liangguo.tools.lsposed.modules

import android.app.Application
import android.widget.Toast
import com.liangguo.lib.common.ContextHolder
import com.liangguo.lib.common.model.app.TestApp
import com.liangguo.tools.lsposed.base.IApplicationHolder
import com.liangguo.tools.lsposed.base.IModule
import com.liangguo.tools.lsposed.configs.XpPrefs
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * @author hesleyliang
 * 时间: 2024/9/6 15:49
 * 邮箱: liang.dh@outlook.com
 */
object TestModule : IModule, IApplicationHolder {

    override val hookPackageName = TestApp.IMETest.packageName

    override val isEnabled = true

    override fun onCreate(application: Application) {
        ContextHolder.application = application
        val str = XpPrefs.prefsMap.toList().toString()
        Toast.makeText(application, str, Toast.LENGTH_SHORT).show()
    }

    override fun onStartHook(lpparam: XC_LoadPackage.LoadPackageParam) {
    }
}