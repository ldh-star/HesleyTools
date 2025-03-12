package com.liangguo.tools.lsposed.base

import android.view.ViewGroup

/**
 * @author hesleyliang
 * 时间: 2024/9/20 13:24
 * 邮箱: liang.dh@outlook.com
 *
 * 这个接口标识了该模块会添加子View
 */
interface ISubViewModule {

    /** 当父容器被创建的时候会回调该方法 */
    fun onCreate(parent: ViewGroup)

}