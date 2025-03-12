package com.liangguo.lib.common.model.app

/**
 * @author Hesley
 * 时间: 2024/7/29 上午10:47
 * 邮箱: liang.dh@outlook.com
 */
sealed interface SogouApp : IMEApp {

    companion object {
        const val BASE_PACKAGE = "com.sohu.inputmethod.sogou"
        val LIST = listOf(Default, Vivo, Xiaomi, Samsung, Zte, Oppo, Honor)
    }

    override val inputMethodService: String
        get() = ".SogouIME"

    data object Default : SogouApp {
        override val name = "公版"
        override val packageName = BASE_PACKAGE
    }

    data object Vivo : SogouApp {
        override val name = "Vivo"
        override val packageName = "$BASE_PACKAGE.vivo"
    }

    data object Xiaomi : SogouApp {
        override val name = "小米"
        override val packageName = "$BASE_PACKAGE.xiaomi"
    }

    data object Samsung : SogouApp {
        override val name = "三星"
        override val packageName = "$BASE_PACKAGE.samsung"
    }

    data object Zte : SogouApp {
        override val name = "中兴"
        override val packageName = "$BASE_PACKAGE.zte"
    }

    data object Oppo : SogouApp {
        override val name = "Oppo"
        override val packageName = "${BASE_PACKAGE}oem"
    }

    data object Honor : SogouApp {
        override val name = "荣耀"
        override val packageName = "$BASE_PACKAGE.honor"
    }
}