package com.liangguo.lib.common.model.app

/**
 * @author ldh
 * 时间: 2024/8/9 20:00
 * 邮箱: liang.dh@outlook.com
 */
interface IMEApp : IApp {
    /** IMS的class */
    val inputMethodService: String

    /** IMS的id */
    val imsId: String
        get() = "${packageName}/$inputMethodService"
}