package com.liangguo.tools.app.services.developer

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.liangguo.lib.common.utils.tryShell
import com.liangguo.libs.shell.api.Su

/**
 * @author ldh
 * 时间: 2024/8/24 22:22
 * 邮箱: liang.dh@outlook.com
 */
class PointerLocationTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        refreshStatus()
    }

    override fun onTileAdded() {
        super.onTileAdded()
        refreshStatus()
    }

    override fun onClick() {
        super.onClick()
        tryShell {
            Su.Developer.setPointerLocation(!Su.Developer.isPointerLocationOpen())
            refreshStatus()
        }
    }

    private fun refreshStatus() {
        qsTile?.apply {
            tryShell {
                state =
                    if (Su.Developer.isPointerLocationOpen()) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
                updateTile()
            }
        }
    }
}