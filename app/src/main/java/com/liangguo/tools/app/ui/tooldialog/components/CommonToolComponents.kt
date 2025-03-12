package com.liangguo.tools.app.ui.tooldialog.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.SettingsApplications
import androidx.compose.material.icons.filled.StayCurrentLandscape
import androidx.compose.material.icons.filled.StayPrimaryPortrait
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.liangguo.lib.common.utils.startNewActivity
import com.liangguo.lib.common.utils.tryShell
import com.liangguo.libs.shell.api.Su
import com.liangguo.libs.shell.model.ScreenRotation
import com.liangguo.tools.R
import com.liangguo.tools.app.ui.tooldialog.MiniToolIcon
import com.liangguo.tools.app.ui.tooldialog.logic.ToolDialogViewModel
import com.liangguo.tools.app.ui.tooldialog.base.BaseTitleAnimVisibleItem
import com.liangguo.tools.app.ui.tooldialog.base.BaseTitleItem
import com.liangguo.tools.app.ui.tooldialog.base.SwitchComponentItem
import com.liangguo.tools.lsposed.app.XpSettingsActivity

/**
 * @author hesleyliang
 * 时间: 2024/11/6 12:42
 * 邮箱: liang.dh@outlook.com
 */

/** 指针位置开关 */
@Composable
internal fun PointerLocationSwitchComponent(viewModel: ToolDialogViewModel) {
    val suEnabled by Su.enabledState.collectAsState()
    SwitchComponentItem(
        title = "指针位置",
        icon = painterResource(id = R.drawable.point_scan_24px),
        tagIcon = Icons.Default.Code,
        checked = viewModel.pointerStatus.value,
        enabled = suEnabled,
        subTitle = "开发者模式中的“显示指针”选项",
    ) {
        Su.Developer.setPointerLocation(!viewModel.pointerStatus.value)
        viewModel.pointerStatus.value = Su.Developer.isPointerLocationOpen()
    }
}


/** SELinux宽容/强制模式开关 */
@Composable
internal fun SELinuxForceComponent(viewModel: ToolDialogViewModel) {
    if (Su.isRootState.collectAsState().value) {
        SwitchComponentItem(
            title = "SELinux宽容模式",
            icon = Icons.Default.Android,
            tagIcon = Icons.Default.Build,
            checked = !viewModel.isSELinuxEnforce.value,
            subTitle = "关闭开关后可以回到默认的强制模式",
        ) {
            viewModel.switchSELinuxForce(!it)
        }
    }
}

/** 跳转到Xposed设置页 */
@Composable
internal fun XposedSettingComponent() {
    BaseTitleItem(
        title = "模块设置页",
        subTitle = "打开LSPosed模块的设置页面",
        modifier = Modifier.clickable {
            XpSettingsActivity::class.startNewActivity()
        },
        icon = Icons.Default.SettingsApplications,
        component = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(25.dp)
            )
        }
    )
}

/** 更多杂七杂八的item */
@Composable
internal fun MoreItemsComponent(viewModel: ToolDialogViewModel) {
    BaseTitleAnimVisibleItem(
        title = "更多选项",
        icon = Icons.Default.MoreHoriz,
    ) {
        Column {
            Spacer(modifier = Modifier.height(5.dp))
            ScreenRotationComponent(viewModel)
            PointerLocationSwitchComponent(viewModel = viewModel)
            SELinuxForceComponent(viewModel = viewModel)
        }
    }
}

/** 屏幕旋转配置 */
@Composable
internal fun ScreenRotationComponent(viewModel: ToolDialogViewModel) {
    val suEnabled by Su.enabledState.collectAsState()
    BaseTitleAnimVisibleItem(
        title = "屏幕方向",
        subTitle = "强制设置屏幕旋转方向",
        enabled = suEnabled,
        icon = Icons.Default.ScreenRotation,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        ) {
            val current = viewModel.screenRotation.value
            ScreenRotation.LIST.forEach { rotation ->
                MiniToolIcon(
                    title = rotation.name,
                    icon = if (rotation.isPort) Icons.Default.StayPrimaryPortrait else Icons.Default.StayCurrentLandscape,
                    isHighLight = current == rotation,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    tryShell {
                        Su.Device.rotateScreen(rotation)
                        viewModel.finish()
                    }
                }
            }
        }
    }
}