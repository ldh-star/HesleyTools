package com.liangguo.tools.test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.liangguo.lib.common.utils.startNewActivity
import com.liangguo.tools.lsposed.app.XpTestActivity

/**
 * @author hesleyliang
 * 时间: 2024/12/31 15:38
 * 邮箱: liang.dh@outlook.com
 */
@Composable
fun BoxScope.MainTestScreen() {
    var showTest by remember { mutableStateOf(false) }
    if (showTest) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)) {
            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.height(60.dp))
                LazyRow {
                    item {
                        Button(onClick = {
                            showTest = false
                        }) {
                            Text("关闭测试页")
                        }
                    }
                    item {
                        Button(onClick = {
                            XpTestActivity::class.startNewActivity()
                        }) {
                            Text(text = "XP测试")
                        }
                    }
                }
            }
        }
    } else {
        Button(onClick = {
            showTest = true
        }, modifier = Modifier.align(Alignment.TopStart)) {
            Text("打开测试页")
        }
    }
}