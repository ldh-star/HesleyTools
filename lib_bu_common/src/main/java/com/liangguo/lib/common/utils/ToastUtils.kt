package com.liangguo.lib.common.utils

import android.content.Context
import android.widget.Toast
import com.liangguo.lib.common.ContextHolder

/**
 * @author hesleyliang
 * 时间: 2024/11/6 14:33
 * 邮箱: liang.dh@outlook.com
 */
object ToastUtils {

    /** 直接调用系统的toast */
    fun toast(
        msg: Any,
        context: Context = ContextHolder.application,
        duration: Int = Toast.LENGTH_SHORT
    ) = Toast.makeText(context, msg.toString(), duration).show()
}