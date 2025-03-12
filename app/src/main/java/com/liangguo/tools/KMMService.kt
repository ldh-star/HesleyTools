package com.liangguo.tools

/**
 * @author hesleyliang
 * 时间: 2024/8/30 08:49
 * 邮箱: liang.dh@outlook.com
 */
interface KMMService {

    companion object {

        private var _instance: KMMService? = null

        fun init(instance: KMMService) {
            _instance = instance
        }

        val INSTANCE: KMMService
            get() = _instance ?: throw IllegalArgumentException("请先调用init初始化设置实例")

    }

    /** 执行shell命令 */
    fun shell(command: String): String?

    /** 执行 ADB shell命令，在 Android 平台上是直接使用shell或者su来实现 */
    fun adbShell(command: String): String?

    /** 打印日志输出，在java平台是print，在安卓平台是Log，需要主动重写一下 */
    fun println(msg: Any?) {
        kotlin.io.println(msg)
    }

}