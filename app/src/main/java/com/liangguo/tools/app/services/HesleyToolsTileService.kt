package com.liangguo.tools.app.services

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.service.quicksettings.TileService
import com.liangguo.tools.app.ui.tooldialog.ToolDialogActivity

/**
 * @author hesleyliang
 * 时间: 2024/8/28 09:34
 * 邮箱: liang.dh@outlook.com
 */
class HesleyToolsTileService : TileService() {

    override fun onClick() {
        super.onClick()
        val intent = Intent(this, ToolDialogActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startActivityAndCollapse(getPendingIntent(this, intent))
        } else {
            startActivity(Intent().apply {
                setAction(Intent.ACTION_MAIN)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addCategory(Intent.CATEGORY_HOME)
            })
            startActivity(intent)
        }
    }

    private fun getPendingIntent(context: Context, intent: Intent): PendingIntent {
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}