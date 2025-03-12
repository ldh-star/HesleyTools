package com.liangguo.tools.lsposed.app

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.BatteryManager
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.lifecycle.lifecycleScope
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.liangguo.lib.common.utils.color.ColorUtil
import com.liangguo.lib.common.utils.dp2px
import com.liangguo.tools.lsposed.R
import com.liangguo.tools.lsposed.widgets.controller.AnimHorizontalViewController
import com.liangguo.tools.lsposed.widgets.views.CircularProgressBarView
import com.liangguo.tools.lsposed.widgets.views.RotationProgressBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * @author hesleyliang
 * 时间: 2024/9/20 19:06
 * 邮箱: liang.dh@outlook.com
 */
class XpTestActivity : AppCompatActivity() {

    private val container by lazy {
        findViewById<LinearLayout>(R.id.linearlayout)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_xp_test)
        testCircularProgressIndicator()
        testProgressBar()
        testAnim()
        testBattery()
    }

    private fun testCircularProgressIndicator() {
        val progressBar = CircularProgressIndicator(
            ContextThemeWrapper(
                applicationContext,
                androidx.appcompat.R.style.Theme_AppCompat_DayNight
            )
        ).apply {
            val progressSize = 25.dp2px.toInt()
            indicatorSize = progressSize
            // 轨道粗细
            trackThickness = 4.dp2px.toInt()
            indicatorTrackGapSize = 1
            max = 100
            trackColor = Color.LTGRAY
            setIndicatorColor(Color.BLUE)
        }
        val lp = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val button = Button(this).apply {
            text = "随机进度"
            setOnClickListener {
                progressBar.setProgress(Random.nextInt(100), true)
            }
        }
        val controller = AnimHorizontalViewController(progressBar)
        val button2 = Button(this).apply {
            text = "显示/隐藏"
            setOnClickListener {
                controller.viewVisibility = !controller.viewVisibility
            }
        }
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.HORIZONTAL
        layout.addView(button, lp)
        layout.addView(button2, lp)
        layout.addView(progressBar, lp)

        container.addView(
            layout, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
    }

    private fun testProgressBar() {
        val progressBar = ProgressBar(
            ContextThemeWrapper(
                applicationContext,
                androidx.appcompat.R.style.Theme_AppCompat_DayNight
            )
        ).apply {
            max = 100
            isIndeterminate = false
            indeterminateTintList = null
            indeterminateTintMode = null
        }
        val myProgress = CircularProgressBarView(this).apply {
            setProgress(Random.nextFloat())
            val size = 18.dp2px.toInt()
            trackThickness = 3f.dp2px.toInt()
            alpha = 0.7f
            trackColor = ColorUtil.withAlpha(Color.BLUE, 0.3f)
            indicatorColor = ColorUtil.withAlpha(Color.RED, 0.8f)
            layoutParams = FrameLayout.LayoutParams(size, size).apply {
                gravity = Gravity.CENTER
            }
            layoutParams = ViewGroup.LayoutParams(size, size)
        }
        val lp = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val button = Button(this).apply {
            text = "随机进度"
            setOnClickListener {
                progressBar.setProgress(Random.nextInt(100), true)
                myProgress.setProgress(Random.nextFloat())
            }
        }
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.HORIZONTAL
        layout.addView(button, lp)
        layout.addView(progressBar, lp)
        layout.addView(myProgress)
        container.addView(
            layout, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
    }

    private fun testAnim() {
        val progressBar1 = findViewById<CircularProgressBarView>(R.id.progress_circular_1)
        val progressBar2 = findViewById<RotationProgressBar>(R.id.progress_rotation)
        val seekbar = findViewById<AppCompatSeekBar>(R.id.seekbar_view)
        progressBar1.apply {
            setProgress(Random.nextFloat())
            trackThickness = 5f.dp2px.toInt()
            alpha = 0.7f
            trackColor = ColorUtil.withAlpha(Color.BLUE, 0.3f)
            indicatorColor = ColorUtil.withAlpha(Color.RED, 0.8f)
        }
        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let { view ->
                    val progress = view.progress / view.max.toFloat()
                    progressBar1.setProgress(progress, true)
                    progressBar2.speed = progress
                }
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun testBattery() {
        val batteryManager = getSystemService(Context.BATTERY_SERVICE) as? BatteryManager ?: return
        val textView = findViewById<TextView>(R.id.text_battery_test)
        lifecycleScope.launch {
            repeat(1000) {
                val (power, current) = getBatteryPowerAndCurrent(batteryManager)
                textView.text = "功率:$power  电流:$current}"
                delay(1000)
            }
        }
    }

    private fun getBatteryPowerAndCurrent(batteryManager: BatteryManager): Pair<Double, Double> {
        val voltage =
            batteryManager.getIntProperty(BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE) / 1000f / 1000f
        // 以微安为单位的瞬时电池电流，整数。正值表示从充电源进入电池的净电流，负值表示从电池放电的净电流
        val currentNow =
            batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW) / 1000f

        // 功率（W）=电压（V）*电流（A）
        // 计算前将微伏和微安转换为伏和安
        val power = voltage / 1e6 * currentNow / 1e6
        val current = currentNow / 1e6

        return Pair(power, current)
    }
}