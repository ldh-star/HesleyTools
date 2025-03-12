package com.liangguo.tools.app.about

import android.app.Activity
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.liangguo.lib.common.AppBuildConfig
import com.liangguo.lib.common.utils.startNewActivity
import com.liangguo.tools.R

private const val GITHUB_URL = "https://github.com/ldh-star"
private const val CHILD_CARD_FRACTION = 0.85f

/**
 * @author Hesley
 * 时间: 2024/3/16 18:16
 * 邮箱: liang.dh@outlook.com
 */
@Preview("Drawer contents")
@Preview("Drawer contents (dark)", uiMode = UI_MODE_NIGHT_YES)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                title = {
                    Text(text = stringResource(id = R.string.about))
                },
                navigationIcon = {
                    val context = LocalContext.current
                    IconButton(onClick = {
                        (context as? Activity)?.onBackPressed()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                })
        },
        content = { innerPadding ->
            SelectionContainer {
                LazyColumn(
                    contentPadding = innerPadding,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AboutContent()
                }
            }
        }
    )
}

fun LazyListScope.AboutContent() {
    item {
        Column(
            modifier = Modifier.fillMaxWidth(CHILD_CARD_FRACTION),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            AsyncImage(
                model = R.mipmap.ic_launcher,
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.scale(0.9f)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = AppBuildConfig.VERSION_NAME,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.alpha(0.7f)
            )
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = "您隐私的最佳保护伞，让您的照\n片安全到连外星人都打不开！",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    item { Spacer(modifier = Modifier.height(40.dp)) }

    item {
        Card(
            modifier = Modifier.fillMaxSize(CHILD_CARD_FRACTION),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 10.dp)
            ) {
                val uriHandler = LocalUriHandler.current
                ClickableSettingItem(
                    title = "Hesley",
                    imageModel = R.drawable.ic_github,
                    subtitle = GITHUB_URL
                ) {
                    uriHandler.openUri(GITHUB_URL)
                }

                ClickableSettingItem(
                    title = "开源许可证",
                    subtitle = "查看开源项目使用详情",
                    imageModel = R.drawable.ic_license
                ) {
                    OSLActivity::class.startNewActivity()
                }
            }
        }
    }

    item { Spacer(modifier = Modifier.height(20.dp)) }

    item {
        var open by remember { mutableStateOf(false) }
        val rotationAngle by animateFloatAsState(
            targetValue = if (open) 0f else 180f,
            label = "toggleArrow",
        )
        val alpha = if (open) 1f else 0.5f
        ElevatedCard(
            modifier = Modifier.fillMaxSize(CHILD_CARD_FRACTION),
            onClick = { open = !open },
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 20.dp, end = 10.dp, bottom = 20.dp, top = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "编译信息",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { open = !open }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = null,
                            modifier = Modifier
                                .rotate(rotationAngle)
                                .alpha(alpha)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(5.dp))

                InfoRow(
                    title = "版本信息",
                    message = "${AppBuildConfig.VERSION_NAME} (${AppBuildConfig.VERSION_CODE})"
                )
                Spacer(modifier = Modifier.height(5.dp))
//                InfoRow(title = "最后编译", message = AppBuildConfig.LAST_BUILD_TIME)

                AnimatedVisibility(
                    visible = open,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        val context = LocalContext.current
                        Spacer(modifier = Modifier.height(5.dp))
                        InfoRow(title = "包名", message = context.packageName)
                        Spacer(modifier = Modifier.height(5.dp))
//                        InfoRow(title = "提交分支", message = AppBuildConfig.BRANCH)
                        Spacer(modifier = Modifier.height(5.dp))
//                        var commitIdShow by remember { mutableStateOf(false) }
//                        InfoRow(
//                            title = "提交id",
//                            message = if (commitIdShow)
//                                AppBuildConfig.COMMIT_ID else
//                                "${AppBuildConfig.COMMIT_ID.substring(0, 8)}...",
//                            modifier = Modifier
//                                .animateContentSize()
//                                .clickable {
//                                    commitIdShow = !commitIdShow
//                                })
//                        Spacer(modifier = Modifier.height(5.dp))
//                        InfoRow(title = "提交信息", message = "")
//                        Spacer(modifier = Modifier.height(3.dp))
//                        InfoRow(message = AppBuildConfig.COMMIT_MESSAGE)
                    }
                }

            }
        }
    }

    item { Spacer(modifier = Modifier.height(100.dp)) }
}

@Composable
private fun InfoRow(title: String? = null, message: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        title?.let {
            Text(text = "$it：")
        }
        Text(text = message, modifier = Modifier.alpha(0.7f))
    }
}

@Composable
fun ClickableSettingItem(
    title: String,
    imageModel: Any? = null,
    subtitle: String? = null,
    onClick: (() -> Unit) = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp)
            .clip(RoundedCornerShape(15.dp))
            .clickable(onClick = onClick)
    ) {
        SettingItem(
            title = title,
            imageModel = imageModel,
            subtitle = subtitle,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp)
        )
    }
}

@Composable
private fun SettingItem(
    title: String,
    modifier: Modifier = Modifier,
    imageModel: Any? = null,
    subtitle: String? = null
) {
    MaterialTheme.colorScheme.primary
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        imageModel?.let { model ->
            AsyncImage(
                model = model,
                contentDescription = null,
                modifier = Modifier
                    .size(30.dp)
                    .alpha(0.8f)
                    .clip(CircleShape),
                colorFilter = ColorFilter.tint(
                    MaterialTheme.colorScheme.onPrimaryContainer,
                    blendMode = BlendMode.SrcIn
                ),
            )
        }
        Spacer(modifier = Modifier.width(15.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )
            subtitle?.let {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.alpha(0.8f)
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.alpha(0.5f)
        )
        Spacer(modifier = Modifier.width(5.dp))
    }
}