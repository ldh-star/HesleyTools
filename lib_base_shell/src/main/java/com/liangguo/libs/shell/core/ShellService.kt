package com.liangguo.libs.shell.core

import com.liangguo.libs.shell.api.IShellService
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import kotlin.system.exitProcess


/**
 * @author hesleyliang
 * 时间: 2024/8/29 13:25
 * 邮箱: liang.dh@outlook.com
 */
internal class ShellService : IShellService.Stub() {
    override fun destroy() {
        exitProcess(0)
    }

    override fun exit() {
        destroy()
    }

    override fun exec(command: String?): String {
        val result = StringBuilder()
        val doProcess: (Process) -> Unit = { process ->
            // 获取命令的标准输出
            val output = BufferedReader(InputStreamReader(process.inputStream))
            val errorOutput = BufferedReader(InputStreamReader(process.errorStream))

            // 读取标准输出
            var line: String?
            while (output.readLine().also { line = it } != null) {
                result.append(line).append("\n")
            }

            // 读取错误输出
            while (errorOutput.readLine().also { line = it } != null) {
                result.append(line).append("\n")
            }
            process.waitFor() // 等待命令执行完毕
        }
        try {
            // 首先直接执行shell级别命令
            doProcess(Runtime.getRuntime().exec(arrayOf("sh", "-c", command)))
        } catch (e: IOException) {
            try {
                // 如果不能直接执行shell命令，再使用 `su -c` 来执行root级别命令
                doProcess(Runtime.getRuntime().exec(arrayOf("su", "-c", command)))
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
        return result.toString()
    }
}