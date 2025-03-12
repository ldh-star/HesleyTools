package com.liangguo.lib.common.model.app

/**
 * @author ldh
 * 时间: 2024/8/6 15:04
 * 邮箱: liang.dh@outlook.com
 */
sealed interface BaiduApp : IApp {

    companion object {
        const val BASE_PACKAGE = "com.baidu.input"
    }

    data object Default : BaiduApp {
        override val name = "百度公版"
        override val packageName = BASE_PACKAGE
    }

    data object Vivo : BaiduApp {
        override val name = "百度Vivo版"
        override val packageName = "${BASE_PACKAGE}_vivo"
    }

    data object Xiaomi : BaiduApp {
        override val name = "百度小米版"
        override val packageName = "${BASE_PACKAGE}_mi"
    }
}