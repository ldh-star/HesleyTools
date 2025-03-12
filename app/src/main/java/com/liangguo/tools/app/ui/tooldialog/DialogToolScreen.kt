package com.liangguo.tools.app.ui.tooldialog

import android.provider.Settings
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.AppSettingsAlt
import androidx.compose.material.icons.filled.Screenshot
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.liangguo.lib.common.utils.startActivityByAction
import com.liangguo.lib.common.utils.startAppActivity
import com.liangguo.tools.app.ui.tooldialog.components.IMESelectComponent
import com.liangguo.tools.app.ui.tooldialog.components.IMESelectPage
import com.liangguo.tools.app.ui.tooldialog.components.MiStepComponent
import com.liangguo.tools.app.ui.tooldialog.components.MiStepPage
import com.liangguo.tools.app.ui.tooldialog.components.MoreItemsComponent
import com.liangguo.tools.app.ui.tooldialog.components.XposedSettingComponent
import com.liangguo.tools.app.ui.tooldialog.logic.ToolDialogViewModel
import com.liangguo.tools.app.ui.tooldialog.logic.ToolDialogViewModel.Companion.TAG_SUB_PAGE
import com.liangguo.tools.app.ui.tooldialog.model.ToolItemTag


/**
 * @author hesleyliang
 * 时间: 2024/8/28 12:33
 * 邮箱: liang.dh@outlook.com
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DialogToolScreen(viewModel: ToolDialogViewModel) {
    SharedTransitionLayout(modifier = Modifier.animateContentSize()) {
        val navHostController = rememberNavController()
        NavHost(
            navController = navHostController,
            startDestination = "home"
        ) {
            composable("home") {
                DialogToolMainPage(
                    viewModel = viewModel,
                    navHostController = navHostController,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this@composable
                )
            }
            composable(
                "$TAG_SUB_PAGE/{item}",
                arguments = listOf(navArgument("item") { type = NavType.StringType })
            ) { backStackEntry ->
                backStackEntry.arguments?.getString("item")?.let { item ->
                    when (item) {
                        ToolItemTag.IME_SELECT.name -> IMESelectPage(
                            viewModel = viewModel,
                            navHostController = navHostController,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedContentScope = this@composable
                        )

                        ToolItemTag.MI_STEP.name -> MiStepPage(
                            viewModel = viewModel,
                            navHostController = navHostController,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedContentScope = this@composable
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DialogToolMainPage(
    viewModel: ToolDialogViewModel,
    navHostController: NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope
) {
    LaunchedEffect(viewModel) {
        // 在一级页面初始化的时候就去刷新输入法列表了，这样下次打开会快些
        viewModel.refreshIMEList(200)
    }
    LazyColumn(verticalArrangement = Arrangement.Bottom) {
        item { MiniToolIconPanel() }
        item { XposedSettingComponent() }
        item {
            IMESelectComponent(
                viewModel = viewModel,
                navHostController = navHostController,
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope
            )
        }
        item {
            MiStepComponent(
                navHostController = navHostController,
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope
            )
        }
        item { MoreItemsComponent(viewModel) }
    }
}

/**
 * 小图标工具大全
 */
@Composable
private fun MiniToolIconPanel() {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MiniToolIcon("设置", Icons.Default.Settings, modifier = Modifier.weight(1f)) {
            context.startActivityByAction(Settings.ACTION_SETTINGS)
        }
        MiniToolIcon("开发者", Icons.Default.Android, modifier = Modifier.weight(1f)) {
            context.startActivityByAction(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
        }
        MiniToolIcon("刷新率", Icons.Default.Screenshot, modifier = Modifier.weight(1f)) {
            context.startAppActivity(
                "com.xiaomi.misettings",
                "com.xiaomi.misettings.display.RefreshRate.RefreshRateActivity"
            )
        }
        MiniToolIcon("应用管理", Icons.Default.AppSettingsAlt, modifier = Modifier.weight(1f)) {
            context.startAppActivity(
                "com.miui.securitycenter",
                "com.miui.appmanager.AppManagerMainActivity"
            )
        }
    }
}


/** 小号工具图标按钮 */
@Composable
fun MiniToolIcon(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isHighLight: Boolean = enabled,
    onClick: () -> Unit
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.alpha(if (isHighLight) 1f else 0.4f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(modifier = Modifier.size(26.dp), onClick = onClick, enabled = enabled) {
                Icon(imageVector = icon, contentDescription = null)
            }
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}