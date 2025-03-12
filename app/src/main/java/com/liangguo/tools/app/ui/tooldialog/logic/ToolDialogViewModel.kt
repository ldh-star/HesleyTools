package com.liangguo.tools.app.ui.tooldialog.logic

import android.Manifest
import android.app.Activity
import android.os.Build
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liangguo.claritypermission.core.PermissionResult
import com.liangguo.claritypermission.requestPermissionsWithCoroutine
import com.liangguo.lib.common.ContextHolder
import com.liangguo.lib.common.ime.IMEPackageInfo
import com.liangguo.lib.common.ime.getIMEInfoList
import com.liangguo.lib.common.model.app.BaiduApp
import com.liangguo.lib.common.model.app.SogouApp
import com.liangguo.lib.common.utils.getCurrentIMEPackageName
import com.liangguo.lib.common.utils.getEnabledInputMethods
import com.liangguo.lib.common.utils.openAppDetails
import com.liangguo.lib.common.utils.showInputMethodPicker
import com.liangguo.lib.common.utils.tryShell
import com.liangguo.libs.shell.api.Su
import com.liangguo.libs.shell.model.ScreenRotation
import com.liangguo.tools.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

/**
 * @author hesleyliang
 * 时间: 2024/8/28 14:20
 * 邮箱: liang.dh@outlook.com
 */
class ToolDialogViewModel : ViewModel() {

    companion object {
        const val TAG_SUB_PAGE = "subpage"
    }

    val pointerStatus = mutableStateOf(tryShell { Su.Developer.isPointerLocationOpen() } ?: false)

    val isSELinuxEnforce = mutableStateOf(tryShell { Su.System.isSELinuxEnforce() } ?: false)

    val screenRotation = mutableStateOf(ScreenRotation.DEFAULT)

    private var curActivity: WeakReference<Activity>? = null

    val imeList = MutableStateFlow<List<IMEPackageInfo>>(listOf())

    val miStepController by lazy { MiStepController(viewModelScope) }

    /** 已启用的输入法列表 */
    private val enabledIMEList = mutableListOf<String>()

    /** 当前选中的输入法的包名 */
    val currentIMEPkgName = MutableStateFlow<String?>(null)

    private val observer by lazy {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> screenRotation.value =
                    tryShell { Su.Device.getScreenRotationStatus() } ?: ScreenRotation.DEFAULT

                Lifecycle.Event.ON_DESTROY -> onDestroy()

                else -> Unit
            }
        }
    }

    private fun onDestroy() {
        CoroutineScope(Dispatchers.Default).launch {
            runCatching {
                (curActivity?.get() as? LifecycleOwner)?.lifecycle?.removeObserver(observer)
            }
            curActivity = null
            imeList.emit(emptyList())
            enabledIMEList.clear()
            currentIMEPkgName.emit(null)
        }
    }

    fun bindActivity(activity: Activity) {
        curActivity = WeakReference(activity)
        (activity as? LifecycleOwner)?.lifecycle?.addObserver(observer)
    }

    fun refreshIMEList(delay: Long = 0) {
        viewModelScope.launch(Dispatchers.Default) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                (curActivity?.get() as? FragmentActivity)?.requestPermissionsWithCoroutine(Manifest.permission.QUERY_ALL_PACKAGES)
                    ?.let { result ->
                        if (result !is PermissionResult.Granted) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    ContextHolder.application,
                                    "无法获取应用列表，请到设置中手动开启权限",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            return@launch
                        }
                    }
            }
            delay(delay)
            enabledIMEList.clear()
            enabledIMEList.addAll(ContextHolder.application.getEnabledInputMethods().map { it.id })
            currentIMEPkgName.value = ContextHolder.application.getCurrentIMEPackageName()
            imeList.value = ContextHolder.application.getIMEInfoList()
                .map { imeInfo ->
                    // 搜狗输入法的OEM版本有些不改名所以要手动改一下名字
                    var name = imeInfo.name
                    if (imeInfo.packageName.contains("sogou")) {
                        SogouApp.LIST.forEach { ime ->
                            if (ime.packageName == imeInfo.packageName) {
                                // 搜狗输入法需要加上定制版名字
                                name += "（${ime.name}）"
                            }
                        }
                    }
                    imeInfo.copy(name = name, oemIcon = getOEMIconResId(imeInfo.packageName))
                }
                // 把搜狗输入法的优先级提到最高
                .sortedBy { it.packageName }
                .sortedByDescending { it.packageName.contains("sogou") }
                .sortedByDescending { isIMEEnabled(it) }
        }
    }

    fun finish() {
        curActivity?.get()?.finish()
    }

    /** 通过包名获取OEM厂商的Icon */
    private fun getOEMIconResId(packageName: String): Int? {
        val vivo = R.mipmap.logo_vivo
        val oppo = R.mipmap.logo_oppo
        val xiaomi = R.mipmap.logo_xiaomi
        if (packageName.endsWith("vivo")) {
            return vivo
        } else if (packageName.endsWith("oppo")) {
            return oppo
        } else if (packageName.endsWith("xiaomi")) {
            return xiaomi
        }
        if (packageName.startsWith(BaiduApp.BASE_PACKAGE)) {
            // 百度输入法的小米版是以mi结尾的
            if (packageName.endsWith("_mi")) {
                return xiaomi
            }
        }
        return null
    }

    /**
     * 切换输入法
     *
     * @param isDoubleTap 是否是双击
     * @param isLongClick 是否是长按事件
     */
    fun clickIME(ime: IMEPackageInfo, isDoubleTap: Boolean = false, isLongClick: Boolean = false) {
        val enabled = isIMEEnabled(ime)
        try {
            if (isLongClick) {
                // TODO 长按输入法，暂时先用系统查看详情信息
                ContextHolder.application.openAppDetails(ime.packageName)
                return
            }
            val switch = {
                Su.Device.switchIME(ime.imeInfo.id)
                currentIMEPkgName.value = ContextHolder.application.getCurrentIMEPackageName()
                Toast.makeText(
                    ContextHolder.application,
                    "切换为：\n${ime.name}",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
            if (enabled) {
                if (isDoubleTap) {
                    // 已经启用了该输入法，双击则隐藏
                    val enabledList = enabledIMEList.toMutableList()
                    enabledList.remove(ime.imeInfo.id)
                    Su.Device.enableIME(enabledList)
                    Toast.makeText(
                        ContextHolder.application,
                        "弃用输入法：\n${ime.name}",
                        Toast.LENGTH_SHORT
                    ).show()
                    refreshIMEList()
                } else {
                    switch()
                }
            } else {
                if (Su.enabled) {
                    if (isDoubleTap) {
                        // 有root权限可以强行启用输入法
                        val enabledList = enabledIMEList.toMutableList()
                        enabledList.add(ime.imeInfo.id)
                        Su.Device.enableIME(enabledList)
                        Toast.makeText(
                            ContextHolder.application,
                            "启用输入法：\n${ime.name}",
                            Toast.LENGTH_SHORT
                        ).show()
                        refreshIMEList()
                    } else {
                        Toast.makeText(
                            ContextHolder.application,
                            "该输入法还未启用，双击可强制启用",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    // 该输入法没有被启用且没有root权限
                    Toast.makeText(
                        ContextHolder.application,
                        "该输入法还未被启用，\n请先去输入法管理页面启用",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                ContextHolder.application,
                "切换失败，请确认root或Shizuku权限",
                Toast.LENGTH_SHORT
            ).show()
            ContextHolder.application.showInputMethodPicker()
        }
    }

    /** 输入法是否已经被启用 */
    fun isIMEEnabled(ime: IMEPackageInfo): Boolean {
        return ime.imeInfo.id in enabledIMEList
    }

    fun switchSELinuxForce(newState: Boolean) {
        Su.System.setSELinuxEnforce(newState)
        isSELinuxEnforce.value = Su.System.isSELinuxEnforce()
        val msg = "SELinux已切换为 ${if (isSELinuxEnforce.value) "✅强制模式" else "⚠️宽容模式"}"
        Toast.makeText(ContextHolder.application, msg, Toast.LENGTH_SHORT).show()
    }

}