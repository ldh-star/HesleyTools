package com.liangguo.tools.app.ui.tooldialog.logic

import android.content.Context
import android.icu.util.Calendar
import android.net.Uri
import android.util.Log
import com.liangguo.lib.common.ContextHolder
import com.liangguo.lib.common.utils.ToastUtils
import com.liangguo.libs.shell.api.Su
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.random.Random
import kotlin.random.nextInt

/**
 * @author hesleyliang
 * 时间: 2024/11/6 13:14
 * 邮箱: liang.dh@outlook.com
 */
class MiStepController(private val scope: CoroutineScope) {

    /** 编辑框输入的文字。初始化的时候应该是一个随机数，每次不用自己手填，刚好鹅民公社能刷就行 */
    val inputText = MutableStateFlow(Random.nextInt(6700..9999).toString())

    /** 系统当前步数 */
    val currentStep = MutableStateFlow<Int?>(null)

    /** 目标的步数 */
    val targetStep = inputText.map { text ->
        val num = text.toIntOrNull() ?: -1
        if (num >= 0) num else null
    }

    val loading = MutableStateFlow(false)

    /** 刷新当前步数 */
    suspend fun updateStep() = withContext(Dispatchers.IO) {
        currentStep.value = getTodaySteps(ContextHolder.application)
    }

    /** 设置步数 */
    fun setStep(step: Int?) {
        step ?: return
        scope.launch(Dispatchers.IO) {
            loading.value = true
            kotlin.runCatching {
                val addStep = step - (currentStep.value ?: 0)
                val time = System.currentTimeMillis()
                val cmd = String.format(Locale.CHINA, ROOT_ADD_STEPS, time - 1000L, time, addStep)
                Su.shell(cmd).also {
                    updateStep()
                    withContext(Dispatchers.Main) {
                        ToastUtils.toast("修改成功")
                    }
                }
            }
            loading.value = false
        }
    }

    companion object {
        private const val ROOT_ADD_STEPS =
            "content insert --uri content://com.miui.providers.steps/item --bind _begin_time:l:%d --bind _end_time:l:%d --bind _mode:i:2 --bind _steps:i:%d"
        private val STEP_URI = Uri.parse("content://com.miui.providers.steps/item")
        private val QUERY_FILED = arrayOf("_id", "_begin_time", "_end_time", "_mode", "_steps")
    }

    /** 获取今日步数 */
    private fun getTodaySteps(context: Context): Int {
        try {
            val cursor = context.contentResolver.query(
                STEP_URI,
                QUERY_FILED,
                null,
                null,
                null
            )
            if (cursor != null) {
                val todayBeginTime = getTodayTime(true)
                val todayEndTime = getTodayTime(false)
                var todayStepCount = 0
                while (cursor.moveToNext()) {
                    val beginTime = cursor.getLong(1)
                    val endTime = cursor.getLong(2)
                    val mode = cursor.getInt(3)
                    val steps = cursor.getInt(4)
                    if (beginTime >= todayBeginTime && endTime <= todayEndTime && mode == 2) {
                        todayStepCount += steps
                    }
                }
                cursor.close()
                return todayStepCount
            }
        } catch (e: Exception) {
            Log.e("21251521", e.stackTraceToString())
            e.printStackTrace()
        }
        return 0
    }

    /**
     * 获取今日时间
     * @param isStartOfDay true表示获取当天的开始时间（00:00:00），false表示获取当天的结束时间（23:59:59）
     */
    private fun getTodayTime(isStartOfDay: Boolean): Long {
        val calendar = Calendar.getInstance()
        if (isStartOfDay) {
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            calendar.set(Calendar.MILLISECOND, 999)
        }
        return calendar.timeInMillis
    }
}