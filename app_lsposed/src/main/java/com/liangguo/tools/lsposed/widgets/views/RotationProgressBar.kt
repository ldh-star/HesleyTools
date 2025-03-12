package com.liangguo.tools.lsposed.widgets.views

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import android.widget.ProgressBar

/**
 * @author hesleyliang
 * 时间: 2024/9/26 20:58
 * 邮箱: liang.dh@outlook.com
 */
class RotationProgressBar : ProgressBar {

    /** 当前速度，取值 0 - 1，数字越大转的越快，0.5表示原生速度 */
    var speed: Float = 0f
        set(value) {
            if (field == value) return
            field = value
            duration = 5 + (1000 * (1f - value)).toLong()
        }

    /** 加速动画时长，为0的时候表示不做加速动画 */
    private var duration: Long = 0L
        set(value) {
            if (field == value) return
            field = value
            resumeAnim()
        }

    /** 给ProgressBar加速的动画器 */
    private val rotationAnimator by lazy { createAnimator() }

    private fun createAnimator(): ObjectAnimator {
        return ObjectAnimator.ofFloat(this, "rotation", 0f, 360f).apply {
            this.duration = 1000L
            repeatCount = ObjectAnimator.INFINITE
            interpolator = LinearInterpolator()
        }
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnim()
    }

    override fun onDetachedFromWindow() {
        rotationAnimator.cancel()
        super.onDetachedFromWindow()
    }

    /**
     * 开始进行动画
     */
    fun startAnim() {
        rotationAnimator.cancel()
        if (duration > 0) {
            rotationAnimator.start()
        }
    }

    /**
     * 继续执行动画，可以作为更新当前执行的动画，如果本身没有动画运行，则会启动运行
     */
    fun resumeAnim() {
        if (duration == 0L) {
            rotationAnimator.cancel()
            return
        }
        if (rotationAnimator.isRunning) {
            rotationAnimator.pause()
            rotationAnimator.resume()
        } else {
            rotationAnimator.start()
        }
    }
}