package com.liangguo.tools.app.ui.tooldialog.base

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.liangguo.lib.common.model.app.AppPackageInfo
import com.liangguo.tools.R

/**
 * @author hesleyliang
 * 时间: 2024/8/28 12:51
 * 邮箱: liang.dh@outlook.com
 */

/** 默认的每一个子项的item规范容器 */
@Composable
fun BaseItemContainer(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .padding(vertical = 5.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        content()
    }
}

/** 上面两行标题下面是内容并且带一个右边组件的Item */
@Composable
fun BaseTitleItem(
    title: String,
    modifier: Modifier = Modifier,
    icon: Any? = null,
    onIconClick: (() -> Unit)? = null,
    tagIcon: Any? = null,
    subTitle: String? = null,
    component: (@Composable () -> Unit)? = null,
    content: (@Composable () -> Unit)? = null
) {
    BaseItemContainer {
        Column {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .heightIn(min = 70.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(15.dp)
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(10.dp))
                val iconScope: (@Composable () -> Unit)? = when (icon) {
                    is ImageVector -> {
                        {
                            Icon(
                                imageVector = icon,
                                contentDescription = "",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    is Painter -> {
                        {
                            Icon(
                                painter = icon,
                                contentDescription = "",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    else -> null
                }
                iconScope?.let {
                    Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                        onIconClick?.let {
                            IconButton(
                                onClick = onIconClick,
                            ) {
                                iconScope()
                            }
                        } ?: iconScope()
                    }

                }
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = title, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.width(3.5.dp))
                        (tagIcon as? ImageVector)?.let {
                            Icon(
                                imageVector = tagIcon,
                                contentDescription = null,
                                modifier = Modifier.size(14.5.dp)
                            )
                        }
                        (tagIcon as? Painter)?.let {
                            Icon(
                                painter = tagIcon,
                                contentDescription = null,
                                modifier = Modifier.size(14.5.dp)
                            )
                        }
                    }
                    subTitle?.let {
                        Text(
                            text = subTitle,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.alpha(0.7f)
                        )
                    }
                }
                component?.let {
                    Spacer(modifier = Modifier.width(5.dp))
                    component()
                }
                Spacer(modifier = Modifier.width(15.dp))
            }
            content?.invoke()
        }
    }
}

/** 点击后可以显示和隐藏的item */
@Composable
fun BaseTitleAnimVisibleItem(
    title: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: Any? = null,
    tagIcon: Any? = null,
    subTitle: String? = null,
    visibleState: MutableState<Boolean> = remember { mutableStateOf(false) },
    onVisibleChanged: (Boolean) -> Unit = { visibleState.value = it },
    content: (@Composable () -> Unit)? = null
) {
    val rotation by animateFloatAsState(
        targetValue = if (visibleState.value) 90f else 0f,
        label = ""
    )
    BaseTitleItem(
        title = title,
        modifier = modifier
            .alpha(if (enabled) 1f else 0.5f)
            .clickable(enabled = enabled) { onVisibleChanged(!visibleState.value) },
        icon = icon,
        tagIcon = tagIcon,
        subTitle = subTitle,
        component = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier
                    .clickable(enabled = enabled) { onVisibleChanged(!visibleState.value) }
                    .size(25.dp)
                    .rotate(rotation)
            )
        }
    ) {
        content?.let {
            AnimatedVisibility(
                visible = visibleState.value,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                content()
            }
        }
    }
}

@Composable
fun SwitchComponentItem(
    title: String,
    checked: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: Any? = null,
    tagIcon: Any? = null,
    subTitle: String? = null,
    onCheckedChange: ((Boolean) -> Unit)? = null,
) = BaseTitleItem(
    title = title,
    modifier = modifier
        .alpha(if (enabled) 1f else 0.5f)
        .clickable(enabled = enabled) { onCheckedChange?.invoke(!checked) },
    icon = icon,
    tagIcon = tagIcon,
    subTitle = subTitle,
    component = {
        Switch(
            checked = checked,
            enabled = enabled,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.scale(0.9f)
        )
    }
)

/**
 * 显示应用简介的一个横着的单项
 *
 * @param appInfo 应用数据
 * @param component item右边的控件
 */
@Composable
fun AppInfoListItem(
    modifier: Modifier = Modifier,
    appInfo: AppPackageInfo,
    component: @Composable (() -> Unit)? = null
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Spacer(modifier = Modifier.width(5.dp))
        Box(modifier = Modifier.size(40.dp)) {
            appInfo.icon?.let {
                AsyncImage(
                    model = appInfo.icon ?: R.drawable.ic_launcher_foreground,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(2.dp)
                )
            }
            appInfo.oemIcon?.let { id ->
                AsyncImage(
                    model = id,
                    contentDescription = null,
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(4.dp))
                        .align(Alignment.BottomEnd)
                        .size(14.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = appInfo.name,
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = appInfo.versionName,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.alpha(0.7f)
            )
            Row(modifier = Modifier.alpha(0.7f)) {
                Icon(
                    painter = painterResource(id = R.drawable.apk_document_24px),
                    modifier = Modifier.size(14.dp),
                    contentDescription = null
                )
                Text(
                    text = appInfo.fileSizeStr,
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(modifier = Modifier.width(7.dp))
                if (appInfo.isDebug) {
                    Icon(
                        painter = painterResource(id = R.drawable.adb_24px),
                        modifier = Modifier.size(13.dp),
                        contentDescription = null
                    )
                }
                Text(
                    text = if (appInfo.isDebug) "debug" else "release",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        component?.invoke()
        Spacer(modifier = Modifier.width(5.dp))
    }
}