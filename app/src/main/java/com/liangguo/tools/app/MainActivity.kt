package com.liangguo.tools.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.ProvideWindowInsets
import com.liangguo.lib.common.ContextHolder
import com.liangguo.lib.common.utils.startNewActivity
import com.liangguo.libs.shell.api.Su
import com.liangguo.tools.BuildConfig
import com.liangguo.tools.R
import com.liangguo.tools.app.about.AboutActivity
import com.liangguo.tools.app.ui.theme.HesleyToolsTheme
import com.liangguo.tools.app.ui.tooldialog.DialogToolScreen
import com.liangguo.tools.app.ui.tooldialog.logic.ToolDialogViewModel
import com.liangguo.tools.test.MainTestScreen

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<ToolDialogViewModel>()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ContextHolder.curActivity = this
        Su.refreshSuEnabled()
        setContent {
            HesleyToolsTheme {
                ProvideWindowInsets {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            TopAppBar(
                                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                title = {
                                    Text(text = stringResource(id = R.string.app_name))
                                },
                                actions = {
                                    IconButton(onClick = {
                                        AboutActivity::class.startNewActivity()
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = "About"
                                        )
                                    }
                                }
                            )
                        },
                        content = { contentPadding ->
                            MainAppContent(
                                modifier = Modifier
                                    .padding(contentPadding)
                                    .fillMaxSize()
                            )
                        }
                    )
                }
            }
        }
    }

    @Composable
    private fun MainAppContent(modifier: Modifier = Modifier) {
        Box(
            modifier = modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(horizontal = 10.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(15.dp),
                    )
                    .padding(top = 15.dp),
                contentAlignment = Alignment.Center
            ) {
                DialogToolScreen(viewModel)
            }
            if (BuildConfig.DEBUG) {
                MainTestScreen()
            }
        }
    }
}