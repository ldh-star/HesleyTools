package com.liangguo.tools.app.ui.tooldialog.components

import android.provider.Settings
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.liangguo.lib.common.ContextHolder
import com.liangguo.lib.common.utils.showInputMethodPicker
import com.liangguo.lib.common.utils.startActivityByAction
import com.liangguo.tools.app.ui.tooldialog.base.AppInfoListItem
import com.liangguo.tools.app.ui.tooldialog.base.BaseTitleItem
import com.liangguo.tools.app.ui.tooldialog.logic.ToolDialogViewModel
import com.liangguo.tools.app.ui.tooldialog.logic.ToolDialogViewModel.Companion.TAG_SUB_PAGE
import com.liangguo.tools.app.ui.tooldialog.model.ToolItemTag

/**
 * @author hesleyliang
 * 时间: 2024/8/28 15:26
 * 邮箱: liang.dh@outlook.com
 */
/** 输入法选择 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun IMESelectComponent(
    viewModel: ToolDialogViewModel,
    navHostController: NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope
) {
    with(sharedTransitionScope) {
        BaseTitleItem(
            title = "输入法选择",
            subTitle = "查看输入法列表以及切换",
            modifier = Modifier
                .clickable {
                    navHostController.navigate("$TAG_SUB_PAGE/${ToolItemTag.IME_SELECT.name}")
                    viewModel.refreshIMEList(200)
                }
                .sharedElement(
                    sharedTransitionScope.rememberSharedContentState(key = ToolItemTag.IME_SELECT.name),
                    animatedVisibilityScope = animatedContentScope
                ),
            icon = Icons.Default.Keyboard,
            component = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(25.dp)
                )
            }
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class)
@Composable
fun IMESelectPage(
    viewModel: ToolDialogViewModel,
    navHostController: NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope
) {
    with(sharedTransitionScope) {
        Column(modifier = Modifier.fillMaxWidth()) {
            BaseTitleItem(
                title = "输入法选择",
                subTitle = "查看输入法列表以及切换",
                modifier = Modifier
                    .sharedElement(
                        sharedTransitionScope.rememberSharedContentState(key = ToolItemTag.IME_SELECT.name),
                        animatedVisibilityScope = animatedContentScope
                    ),
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                onIconClick = { navHostController.popBackStack() },
                component = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier
                            .size(25.dp)
                            .rotate(90f)
                            .clickable {
                                navHostController.popBackStack()
                            }
                    )
                }
            )

            val imeList by viewModel.imeList.collectAsState()
            val curIME by viewModel.currentIMEPkgName.collectAsState()
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp, bottom = 5.dp)
            ) {
                item(key = -1) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.animateItem()
                    ) {
                        TextButton(
                            modifier = Modifier.weight(1f),
                            onClick = { ContextHolder.application.showInputMethodPicker() }) {
                            Icon(
                                imageVector = Icons.Default.Keyboard,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(text = "系统切换")
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        TextButton(
                            modifier = Modifier.weight(1f),
                            onClick = { ContextHolder.application.startActivityByAction(Settings.ACTION_INPUT_METHOD_SETTINGS) }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(text = "管理界面")
                        }
                    }
                }

                items(imeList, key = { it.imeInfo.id }) { ime ->
                    val isEnabled = viewModel.isIMEEnabled(ime)
                    AppInfoListItem(modifier = Modifier
                        .fillMaxWidth()
                        .animateItem()
                        .alpha(if (isEnabled) 1f else 0.5f)
                        .height(70.dp)
                        .combinedClickable(
                            onClick = { viewModel.clickIME(ime) },
                            onDoubleClick = { viewModel.clickIME(ime, isDoubleTap = true) },
                            onLongClick = { viewModel.clickIME(ime, isLongClick = true) }
                        ), appInfo = ime) {
                        if (ime.packageName == curIME) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null)
                        }
                    }
                }
            }
        }
    }

}