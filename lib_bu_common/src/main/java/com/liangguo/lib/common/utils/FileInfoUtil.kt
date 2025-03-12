package com.liangguo.lib.common.utils

import android.icu.text.DecimalFormat


/**
 * @author ldh
 * 时间: 2022/1/2 15:30
 * 邮箱: 2637614077@qq.com
 */

/** 把Long格式化成文件的大小 */
fun Long.formatFileSize(): String {
    val length = this
    val df = DecimalFormat("#.00")
    val wrongSize = "0B"
    if (length == 0L) {
        return wrongSize
    }
    val fileSizeString: String = when {
        length < 1024 -> {
            df.format(length.toDouble()) + "B"
        }

        length < 1048576 -> {
            df.format(length.toDouble() / 1024) + "KB"
        }

        length < 1073741824 -> {
            df.format(length.toDouble() / 1048576) + "MB"
        }

        else -> {
            df.format(length.toDouble() / 1073741824) + "GB"
        }
    }
    return fileSizeString
}