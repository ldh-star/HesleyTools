package com.liangguo.tools.app.ui.tooldialog.components

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.liangguo.lib.common.ContextHolder
import com.liangguo.tools.app.ui.tooldialog.base.BaseTitleItem
import com.liangguo.tools.app.ui.tooldialog.logic.MiStepController
import com.liangguo.tools.app.ui.tooldialog.logic.ToolDialogViewModel
import com.liangguo.tools.app.ui.tooldialog.logic.ToolDialogViewModel.Companion.TAG_SUB_PAGE
import com.liangguo.tools.app.ui.tooldialog.model.ToolItemTag


/**
 * @author hesleyliang
 * 时间: 2024/11/6 12:53
 * 邮箱: liang.dh@outlook.com
 */
/** 修改小米步数 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun MiStepComponent(
    navHostController: NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val context = LocalContext.current
        LaunchedEffect(Unit) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val activity = (context as? Activity) ?: ContextHolder.curActivity
                activity?.let {
                    ActivityCompat.requestPermissions(
                        it,
                        arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                        0
                    )
                }
            }
        }
    }
    with(sharedTransitionScope) {
        BaseTitleItem(
            title = "小米步数修改",
            subTitle = "任意修改系统步数",
            modifier = Modifier
                .clickable {
                    navHostController.navigate("$TAG_SUB_PAGE/${ToolItemTag.MI_STEP.name}")
                }
                .sharedElement(
                    sharedTransitionScope.rememberSharedContentState(key = ToolItemTag.MI_STEP.name),
                    animatedVisibilityScope = animatedContentScope
                ),
            icon = Icons.AutoMirrored.Filled.DirectionsWalk,
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


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MiStepPage(
    viewModel: ToolDialogViewModel,
    navHostController: NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope
) {
    LaunchedEffect(Unit) {
        viewModel.miStepController.updateStep()
    }
    with(sharedTransitionScope) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BaseTitleItem(
                title = "小米步数修改",
                subTitle = "任意修改系统步数",
                modifier = Modifier
                    .sharedElement(
                        sharedTransitionScope.rememberSharedContentState(key = ToolItemTag.MI_STEP.name),
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
            MiStepContent(controller = viewModel.miStepController)
        }
    }
}

@Composable
private fun MiStepContent(controller: MiStepController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val step by controller.targetStep.collectAsState(null)
        val currentStep by controller.currentStep.collectAsState()
        val loading by controller.loading.collectAsState()
        val isValid = step != null && currentStep != step && !loading
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            modifier = Modifier.align(Alignment.Start),
            text = "默认生成6700-9999的随机值"
        )
        Row(
            modifier = Modifier.padding(vertical = 5.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            val text by controller.inputText.collectAsState()
            OutlinedTextField(
                value = text,
                label = { Text(text = "目标步数") },
                onValueChange = { controller.inputText.tryEmit(it) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                isError = step == null,
                maxLines = 1,
                placeholder = { Text(text = "要变成的步数") },
                trailingIcon = {
                    AnimatedVisibility(
                        visible = text.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        IconButton(onClick = { controller.inputText.tryEmit("") }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = null)
                        }
                    }
                },
            )
            Box(modifier = Modifier.size(55.dp), contentAlignment = Alignment.Center) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = loading,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(35.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        val stepText = if (currentStep == step) {
            "今日步数：$step，编辑输入框以修改"
        } else {
            val modifyText = if ((currentStep ?: 0) < (step ?: 0)) "升为" else "降为"
            "步数将由  $currentStep  $modifyText->  $step"
        }
        Text(
            modifier = Modifier.align(Alignment.Start),
            text = stepText
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = { controller.setStep(step) },
            enabled = isValid,
        ) {
            Spacer(modifier = Modifier.width(15.dp))
            Text(text = "进行修改")
            Spacer(modifier = Modifier.width(15.dp))
        }
    }
}