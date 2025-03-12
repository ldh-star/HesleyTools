package com.liangguo.tools.lsposed.modules.systemui.statusbar

import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.liangguo.lib.common.utils.color.withAlpha
import com.liangguo.lib.common.utils.dp2px
import com.liangguo.tools.lsposed.configs.XpPrefs
import com.liangguo.tools.lsposed.configs.getBoolean
import com.liangguo.tools.lsposed.configs.getInt
import com.liangguo.tools.lsposed.widgets.views.CircularProgressBarView
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.lang.ref.WeakReference
import kotlin.random.Random

/**
 * @author hesleyliang
 * 时间: 2024/9/20 12:44
 * 邮箱: liang.dh@outlook.com
 *
 * 状态栏显示Cpu信息的模块
 */
object CpuInfoModule : StatusBarSubModule() {

    /** 用来显示CPU占用的View的tag */
    private const val TAG_PROGRESS_BAR_CPU = "tag_progress_bar_cpu"

    override val isEnabled: Boolean
        get() = XpPrefs.prefsMap.getBoolean("prefs_key_status_bar_show_cpu")

    private var progressBarRef: WeakReference<CircularProgressBarView>? = null

    private var previousCpuTimes: List<LongArray> = emptyList()

    /** CPU占用，取值 0 - 1 */
    private var cpuProgress = 0f

    /** cpu占用的显示阈值 */
    private val threshold by lazy { XpPrefs.prefsMap.getInt("prefs_key_status_bar_show_cpu_threshold") }

    /** ProgressBar变化时执行的动画时长 */
    private val animDuration by lazy { XpPrefs.prefsMap.getInt("prefs_key_status_bar_show_cpu_animation_duration") }

    override fun onCreateView(parent: ViewGroup) {
        val context = parent.context
        val progressBar = CircularProgressBarView(context).apply {
            animationTime = animDuration
            setProgress(Random.nextFloat())
            val size = 18.dp2px.toInt()
            trackThickness = 3f.dp2px.toInt()
            alpha = 0.7f
            layoutParams = FrameLayout.LayoutParams(size, size).apply {
                gravity = Gravity.CENTER
            }
            tag = TAG_PROGRESS_BAR_CPU
        }
        progressBarRef = WeakReference(progressBar)

        val cpuInfoContainer = FrameLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val marginHor = 2.dp2px.toInt()
            setPadding(marginHor, 0, marginHor, 0)
        }

        cpuInfoContainer.addView(progressBar)
        parent.addView(cpuInfoContainer)
    }

    override fun onDarkChanged(
        tintAreas: List<Any>,
        darkIntensity: Float,
        tintColor: Int,
        lightColor: Int,
        darkColor: Int,
        useTint: Boolean
    ) {
        progressBarRef?.get()?.apply {
            indicatorColor = tintColor
            trackColor = tintColor.withAlpha(0.1f)
        }
    }

    /**
     * 更新最新的CPU占用
     *
     * @param avgCpuUsage cpu占用百分比，范围 0 - 100
     */
    private fun updateProgress(avgCpuUsage: Int) {
        cpuProgress = (avgCpuUsage / 100f).coerceAtMost(1f)
        progressBarRef?.get()?.post {
            progressBarRef?.get()?.let { view ->
                // 占用超过了阈值，则显示，否则隐藏
                setProgressVisible(view, avgCpuUsage > threshold)
            }
        }
    }

    /**
     * 修改CPU占用的进度圈是否可见
     *
     * @param isVisible CPU占用的进度圈是否可见，在超过阈值的时候这个值为true
     * @param animVisible 在显示和隐藏的时候是否带上动画专场，如果为true，则View的progress为0时才会隐藏，否则显示
     */
    private fun setProgressVisible(
        view: CircularProgressBarView,
        isVisible: Boolean,
        animVisible: Boolean = true
    ) {
        val animEnable = animDuration > 0
        view.visibleInProgress = animVisible
        if (animVisible) {
            if (isVisible) {
                view.setProgress(cpuProgress, animEnable)
            } else {
                view.setProgress(0f, animEnable)
            }
        } else {
            if (view.isVisible != isVisible) {
                view.isVisible = isVisible
            }
            if (!isVisible) {
                // 隐藏之后把进度置0，下次显示的时候可以从0开始拉进度
                view.setProgress(0f, false)
            } else {
                // 显示的时候要把进度更新为当前的最新进度
                view.setProgress(cpuProgress, animEnable)
            }
        }
    }

    override fun onRefresh() {
        if (progressBarRef?.get() == null) {
            stopLoop()
            return
        }
        val currentCpuTimes = getCpuTimes()

        if (previousCpuTimes.isNotEmpty() && currentCpuTimes.isNotEmpty()) {
            var totalUsage = 0
            for (i in currentCpuTimes.indices) {
                val prevTimes = previousCpuTimes[i]
                val currTimes = currentCpuTimes[i]

                val prevTotal = prevTimes.sum()
                val currTotal = currTimes.sum()

                val totalDiff = currTotal - prevTotal
                val idleDiff = currTimes[3] - prevTimes[3]

                if (totalDiff > 0) {
                    val usage = ((totalDiff - idleDiff) * 100 / totalDiff).toInt()
                    totalUsage += usage
                }
            }
            updateProgress(totalUsage / currentCpuTimes.size)
        }
        previousCpuTimes = currentCpuTimes
    }

    // 获取当前设备的每个CPU核的时间
    private fun getCpuTimes(): List<LongArray> {
        val cpuTimes = mutableListOf<LongArray>()
        try {
            val reader = BufferedReader(FileReader("/proc/stat"))
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                if (line!!.startsWith("cpu")) {
                    val tokens = line!!.split(" ").filter { it.isNotEmpty() }
                    // 跳过总的CPU时间
                    if (tokens[0] == "cpu") continue

                    // 提取前 7 个字段
                    val times = LongArray(10)
                    for (i in 1..10) {
                        times[i - 1] = tokens[i].toLong()
                    }
                    cpuTimes.add(times)
                }
            }
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return cpuTimes
    }
}