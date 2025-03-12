package com.liangguo.lib.common.model.app

/**
 * @author Hesley
 * 时间: 2024/7/29 上午10:55
 * 邮箱: liang.dh@outlook.com
 */
sealed interface TestApp : IApp {

    data object IMETest : TestApp {
        override val name = "IMETestApp"
        override val packageName = "com.justwen.ime.test"
        override val launcherActivity = "$packageName.activity.TestMainActivity"
    }

    data object InputTest : TestApp {
        override val name = "输入测试"
        override val packageName = "com.sogou.inputtest"
        override val launcherActivity = "$packageName.MainActivity"
    }

    data object GameTest : TestApp {
        override val name = "牛叉游戏"
        override val packageName = "com.ddianle.lovedance.huawei"
    }

    data object WangZhe : TestApp {
        override val name = "王者荣耀"
        override val packageName = "com.tencent.tmgp.sgame"
    }
}