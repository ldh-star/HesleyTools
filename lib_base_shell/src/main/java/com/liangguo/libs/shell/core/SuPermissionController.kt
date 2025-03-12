package com.liangguo.libs.shell.core

import android.content.ComponentName
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import com.liangguo.lib.common.AppBuildConfig
import com.liangguo.libs.shell.api.IShellService
import com.topjohnwu.superuser.Shell
import rikka.shizuku.Shizuku
import rikka.shizuku.Shizuku.UserServiceArgs
import java.io.DataOutputStream

/**
 * @author hesleyliang
 * 时间: 2024/8/29 13:10
 * 邮箱: liang.dh@outlook.com
 */
internal object SuPermissionController {

    /** 用来执行shell命令的跨进程通信接口 */
    var shellService: IShellService? = null


    /** 是否已经Root授权 */
    val isRoot: Boolean
        get() = Shell.getShell().isRoot

    /** 是否已经Shizuku授权 */
    val isShizuku
        get() = shellService != null

    /** Shizuku启动后的回调 */
    var onShizukuBindCallback: (() -> Unit)? = null

    private val listener = Shizuku.OnRequestPermissionResultListener { _, grantResult ->
        if (grantResult == PackageManager.PERMISSION_GRANTED) {
            // 授予成功，去绑定服务
            bindShizuku()
        }
    }

    private val userServiceArgs: UserServiceArgs = UserServiceArgs(
        ComponentName(
            AppBuildConfig.APPLICATION_ID,
            ShellService::class.java.getName()
        )
    )
        .daemon(false)
        .processNameSuffix("service")
        .debuggable(AppBuildConfig.DEBUG)
        .version(AppBuildConfig.VERSION_CODE)

    private val userServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName?, binder: IBinder?) {
            if (binder != null && binder.pingBinder()) {
                shellService = IShellService.Stub.asInterface(binder)
                onShizukuBindCallback?.invoke()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            shellService = null
            onShizukuBindCallback?.invoke()
        }
    }

    init {
        Shizuku.addRequestPermissionResultListener(listener)
        Shizuku.addBinderReceivedListener {
            refreshSuEnabled()
        }
    }

    private fun requestRoot() {
        try {
            val process = Runtime.getRuntime().exec("su")
            val outputStream = DataOutputStream(process.outputStream)
            outputStream.writeBytes("id\n")
            outputStream.writeBytes("exit\n")
            outputStream.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** 刷新是否已授权root */
    fun refreshSuEnabled() {
        // 优先授权root，如果不行再申请Shizuku
        requestRoot()
        if (isRoot) {
            // 有root就直接运行了
            return
        }
        try {
            bindShizuku()
            return
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            // Shizuku未授权，尝试去申请
            if (!Shizuku.isPreV11()) {
                val requestCode = System.currentTimeMillis().toInt()
                Shizuku.requestPermission(requestCode)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun bindShizuku() {
        kotlin.runCatching {
            if (Shizuku.getVersion() >= 10) {
                Shizuku.bindUserService(userServiceArgs, userServiceConnection)
                return
            }
        }
        Shizuku.peekUserService(userServiceArgs, userServiceConnection)
    }


}