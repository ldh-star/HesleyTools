package com.liangguo.tools.app.services

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.liangguo.libs.shell.api.Su
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.system.exitProcess

/**
 * @author hesleyliang
 * 时间: 2024/8/29 21:47
 * 邮箱: liang.dh@outlook.com
 */
class ScriptTileService : TileService() {

    // Shizuku脚本：adb shell sh /storage/emulated/0/Android/data/moe.shizuku.privileged.api/start.sh
    // 拉取日志脚本
    // adb pull /sdcard/Android/data/com.liangguo.tools/files/. ./

    companion object {
        /** 为了避免多个脚本同时运行相互搅局，这里只允许一个脚本运行 */
        private var job: Job? = null

        private val isRunning: Boolean
            get() = job != null
    }


    override fun onClick() {
        super.onClick()
        if (isRunning) {
            job?.cancel()
            job = null
            refreshStatus()
            exitProcess(0)
        } else {
            job = CoroutineScope(Dispatchers.Default).launch {
                // 在这里配置脚本运行
                // 运行完了之后释放job并刷新状态
                job = null
                withContext(Dispatchers.Main) {
                    refreshStatus(false)
                    return@withContext
                }
            }
        }
        refreshStatus()
    }

    override fun onStartListening() {
        super.onStartListening()
        Su.refreshSuEnabled()
        refreshStatus()
    }

    private fun refreshStatus(running: Boolean = isRunning) {
        qsTile?.apply {
            state = if (running) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            updateTile()
        }
    }
}