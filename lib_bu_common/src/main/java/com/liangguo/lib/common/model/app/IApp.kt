package com.liangguo.lib.common.model.app

/**
 * @author Hesley
 * 时间: 2024/7/29 上午10:58
 * 邮箱: liang.dh@outlook.com
 */
interface IApp {
    /** App名（区分各家OEM分支线） */
    val name: String

    /** 包名 */
    val packageName: String

    /** 启动入口Activity */
    val launcherActivity: String?
        get() = null

}