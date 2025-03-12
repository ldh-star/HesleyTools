package com.liangguo.libs.shell.api

import com.liangguo.libs.shell.core.SuPermissionController
import com.liangguo.libs.shell.model.ScreenRotation
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.flow.MutableStateFlow

private fun Boolean.toSymbol() = if (this) "1" else "0"
private fun String.parseSymbol() = trim().startsWith("1")

/**
 * @author ldh
 * 时间: 2024/8/24 20:58
 * 邮箱: liang.dh@outlook.com
 */
sealed interface Su {

    /** 系统相关 */
    data object System : Su {

        /**
         * 设置SELinux宽容/强制模式
         *
         * @param isEnforce true表示强制模式，false表示宽容模式
         */
        fun setSELinuxEnforce(isEnforce: Boolean) =
            rootShell("setenforce ${isEnforce.toSymbol()}")

        /**
         * 获取SELinux宽容/强制模式
         *
         * @return true表示强制模式，false表示宽容模式
         */
        fun isSELinuxEnforce() =
            rootShell("getenforce").startsWith("Enforcing")

    }

    /**
     * 安卓设备相关
     */
    data object Device : Su {
        /** 强制旋转屏幕 */
        fun rotateScreen(rotation: ScreenRotation) =
            shell("settings put system user_rotation ${rotation.id}")

        /** 获取屏幕当前方向 */
        fun getScreenRotationStatus(): ScreenRotation {
            val result = shell("settings get system user_rotation").trim().toIntOrNull() ?: 0
            return ScreenRotation.get(result)
        }

        /** 切换输入法 */
        fun switchIME(imeID: String) = shell("settings put secure default_input_method $imeID")

        /**
         * 启用输入法。
         *
         * ⚠️该方法会只设置列表里的输入法可用，不在列表里的输入法就算原本启用了现在也会被关闭
         */
        fun enableIME(imeIDList: List<String>) =
            shell("settings put secure enabled_input_methods \"${imeIDList.joinToString(":")}\"")
    }

    /**
     * 安卓开发者选项相关
     */
    data object Developer : Su {

        /** 开发者选项里的显示指针位置的选项 */
        fun setPointerLocation(open: Boolean) =
            shell("settings put system pointer_location ${open.toSymbol()}")

        /** 开发者选项里的显示指针位置的选项是否开启 */
        fun isPointerLocationOpen() = shell("settings get system pointer_location").parseSymbol()
    }


    companion object {

        /** su权限是否被授予 */
        val enabled: Boolean
            get() = SuPermissionController.isRoot || SuPermissionController.isShizuku

        /** su权限是否被授予 */
        val enabledState = MutableStateFlow(false)

        /** root权限是否被授予 */
        val isRootState = MutableStateFlow(false)

        /** 刷新是否Developer已授权shell */
        fun refreshSuEnabled() {
            SuPermissionController.refreshSuEnabled()
            enabledState.tryEmit(enabled)
            isRootState.tryEmit(SuPermissionController.isRoot)
        }

        init {
            SuPermissionController.onShizukuBindCallback = {
                enabledState.tryEmit(enabled)
            }
        }

        /** 通过root的方式执行shell命令 */
        fun rootShell(command: String) = Shell.cmd(command).exec().out.joinToString("\n")

        /** 执行shell命令 */
        fun shell(command: String) =
            shizukuShell(command) ?: rootShell(command)

        /** 通过Shizuku的方式执行shell命令 */
        fun shizukuShell(command: String): String? =
            SuPermissionController.shellService?.exec(command)
    }
}