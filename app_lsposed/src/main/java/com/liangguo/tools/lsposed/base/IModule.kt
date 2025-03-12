package com.liangguo.tools.lsposed.base

import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * @author hesleyliang
 * 时间: 2024/3/6 09:34
 * 邮箱: liang.dh@outlook.com
 */
interface IModule {

    /** 要hook的目标应用的包名 */
    val hookPackageName: String

    /** 要hook的目标进程名，如果为空则不过滤，如果不为空，则当进程名一致的时候才会进入hook逻辑 */
    val hookProcessName: String?
        get() = null

    /**
     * 该hook模块是否需要生效，子类自行改写get()方法
     */
    val isEnabled: Boolean

    /** 开始进行hook的逻辑 */
    fun onStartHook(lpparam: XC_LoadPackage.LoadPackageParam)
}
