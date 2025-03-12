package com.liangguo.tools.lsposed.base

import android.app.Application

/**
 * @author hesleyliang
 * 时间: 2024/9/18 21:59
 * 邮箱: liang.dh@outlook.com
 *
 * 模块接入这个接口之后，在宿主进程进行初始化的时候会进行hook到Application的创建方法
 */
interface IApplicationHolder {

    /** 当宿主进程创建的时候会调用一次该方法 */
    fun onCreate(application: Application)

}