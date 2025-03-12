package com.liangguo.tools.lsposed.modules.systemui.statusbar

import android.graphics.Typeface
import android.view.ViewGroup
import android.widget.TextView
import com.liangguo.lib.common.utils.dp2px
import com.liangguo.tools.lsposed.configs.XpPrefs
import com.liangguo.tools.lsposed.configs.getBoolean
import java.io.FileInputStream
import java.lang.ref.WeakReference
import java.util.Properties
import kotlin.math.absoluteValue

/**
 * @author hesleyliang
 * 时间: 2024/9/27 16:44
 * 邮箱: liang.dh@outlook.com
 */
object BatteryInfoModule : StatusBarSubModule() {

    private var textRef: WeakReference<TextView>? = null

    private val textView: TextView?
        get() = textRef?.get()

    private val formater = "%.2f"

    override val isEnabled: Boolean
        get() = XpPrefs.prefsMap.getBoolean("prefs_key_status_bar_show_battery")

    override fun onCreateView(parent: ViewGroup) {
        val context = parent.context
        val text = TextView(context)
        text.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val marginHor = 1.4f.dp2px.toInt()
        text.setPadding(marginHor, 0, marginHor, 0)
        val styleId = context.resources.getIdentifier(
            "TextAppearance.StatusBar.Clock",
            "style",
            hookPackageName
        )
        text.setTextAppearance(styleId)
        text.setTypeface(text.typeface, Typeface.BOLD)
        parent.addView(text)
        text.textSize = 8f
        textRef = WeakReference(text)
    }

    override fun onDarkChanged(
        tintAreas: List<Any>,
        darkIntensity: Float,
        tintColor: Int,
        lightColor: Int,
        darkColor: Int,
        useTint: Boolean
    ) {
        textView?.setTextColor(tintColor)
    }

    override fun onRefresh() {
        if (textView == null) {
            stopLoop()
            return
        }
        val (power, current) = getBatteryPowerAndCurrent()
        // 功耗，例如：2.45W
        val powerStr = "${formater.format(power)} W"
        // 电流，例如：489mA、2.12A
        val currentStr =
            if (current.absoluteValue >= 1000) {
                // 如果超过 1000mA，则把单位变为A
                formater.format(current / 1000) + " A"
            } else {
                // 小于 1000mA，直接转int显示mA
                "${current.toInt()} mA"
            }
        val str = "$powerStr\n$currentStr"
        textView?.post {
            textView?.let { view ->
                view.text = str
            }
        }
    }

    /**
     * 获取当前功率和电流
     */
    private fun getBatteryPowerAndCurrent(): Pair<Double, Double> {
        val properties = Properties()
        FileInputStream("/sys/class/power_supply/battery/uevent").use {
            properties.load(it)
        }
        // 电压（V）
        val voltage =
            (properties.getProperty("POWER_SUPPLY_VOLTAGE_NOW").toDoubleOrNull() ?: 0.0) / 1e6f
        // 以微安为单位的瞬时电池电流，整数。正值表示从充电源进入电池的净电流，负值表示从电池放电的净电流
        // 电流（mA）
        val currentNow =
            (properties.getProperty("POWER_SUPPLY_CURRENT_NOW").toDoubleOrNull() ?: 0.0) / -1e3

        // 功率（W）=电压（V）*电流（A）
        // 计算前将微伏和微安转换为伏和安
        val power = voltage * currentNow / 1000

        return Pair(power, currentNow)
    }
}