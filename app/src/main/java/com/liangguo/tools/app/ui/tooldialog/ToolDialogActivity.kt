package com.liangguo.tools.app.ui.tooldialog

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import com.liangguo.lib.common.ContextHolder
import com.liangguo.libs.shell.api.Su
import com.liangguo.tools.app.ui.theme.HesleyToolsTheme
import com.liangguo.tools.app.ui.tooldialog.logic.ToolDialogViewModel
import com.liangguo.tools.widget.dialog.PositionDialog

/**
 * @author hesleyliang
 * 时间: 2024/8/28 11:41
 * 邮箱: liang.dh@outlook.com
 */
class ToolDialogActivity : FragmentActivity() {

    private val viewModel by viewModels<ToolDialogViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ContextHolder.curActivity = this
        Su.refreshSuEnabled()
        viewModel.bindActivity(this)
        setContent {
            HesleyToolsTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    PositionDialog(onDismissRequest = { finish() }) {
                        DialogToolScreen(viewModel)
                    }
                }
            }
        }
    }
}