package com.liangguo.tools.app.about

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.google.accompanist.insets.ProvideWindowInsets
import com.liangguo.tools.app.ui.theme.HesleyToolsTheme

/**
 * @author hesleyliang
 * 时间: 2024/3/14 15:11
 * 邮箱: liang.dh@outlook.com
 */
class AboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HesleyToolsTheme {
                ProvideWindowInsets {
                    Surface(
                        color = MaterialTheme.colorScheme.background,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        AboutScreen()
                    }
                }
            }
        }
    }
}