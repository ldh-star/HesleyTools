package com.liangguo.tools.app.services.rotation

import android.service.quicksettings.TileService
import com.liangguo.lib.common.utils.tryShell
import com.liangguo.libs.shell.api.Su
import com.liangguo.libs.shell.model.ScreenRotation

/**
 * @author ldh
 * 时间: 2024/8/24 22:22
 * 邮箱: liang.dh@outlook.com
 */
class LandRotationTileService : TileService() {
    override fun onClick() {
        super.onClick()
        tryShell { Su.Device.rotateScreen(ScreenRotation.Landscape) }
    }
}