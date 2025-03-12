package com.liangguo.tools.lsposed.widgets.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

/**
 * @author hesleyliang
 * 时间: 2024/9/20 18:46
 * 邮箱: liang.dh@outlook.com
 *
 * 中空圆形的ProgressBar
 */
class CircularProgressBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    /** 绘制区域 */
    private val rectF = RectF()

    private var _progress: Float = 0f
    private var _trackThickness: Int = 20
    private var _trackColor: Int = Color.BLACK
    private var _indicatorColor: Int = Color.TRANSPARENT
    private var sweepAngle = 0f

    /**
     * 是否让Visibility和进度绑在一起，进度为0时则Visibility为Gone，否则为Visible
     */
    var visibleInProgress = false

    /** 动画执行器*/
    private val progressAnimator = ValueAnimator().apply {
        interpolator = AccelerateDecelerateInterpolator()
        addUpdateListener { animation ->
            progress = animation.animatedValue as Float
        }
    }

    /** 动画时间 */
    var animationTime = 500

    /** 当前进度，取值 0～1 的小数 */
    var progress: Float
        get() = _progress
        private set(value) {
            if (_progress == value) return
            if (visibleInProgress) {
                if (value == 0f && visibility != GONE) {
                    // 隐藏
                    visibility = GONE
                } else if (value != 0f && visibility != VISIBLE) {
                    visibility = VISIBLE
                }
            }
            _progress = value
            sweepAngle = value * 360
            invalidate()
        }

    /** 此进度指示器的轨道厚度（以像素为单位） */
    var trackThickness: Int
        get() = _trackThickness
        set(value) {
            if (_trackThickness == value) return
            _trackThickness = value
            paint.strokeWidth = value.toFloat()
            onUpdateRect()
            invalidate()
        }

    /** 此进度指示器轨道的颜色 */
    var trackColor: Int
        get() = _trackColor
        set(value) {
            if (_trackColor == value) return
            _trackColor = value
            invalidate()
        }

    /** 进度指示器的指示器中使用的颜色 */
    var indicatorColor: Int
        get() = _indicatorColor
        set(value) {
            if (_indicatorColor == value) return
            _indicatorColor = value
            invalidate()
        }

    /**
     * 更新进度，范围0-1的小数
     *
     * @param progress 进度，取值 0～1 的小数
     * @param animate 在更新进度的时候是否要带上变化的动画
     */
    fun setProgress(progress: Float, animate: Boolean = true) {
        if (animate) {
            progressAnimator.apply {
                setFloatValues(this@CircularProgressBarView.progress, progress)
                duration = animationTime.toLong()
                start()
            }
        } else {
            this.progress = progress
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        onUpdateRect()
    }

    /** 计算更新绘制区域 */
    private fun onUpdateRect() {
        val halfWidth = width / 2f
        val halfHeight = height / 2f
        val radius = halfWidth.coerceAtMost(halfHeight) - trackThickness / 2f
        rectF.set(halfWidth - radius, halfHeight - radius, halfWidth + radius, halfHeight + radius)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = trackColor
        canvas.drawArc(rectF, 0f, 360f, false, paint)
        paint.color = indicatorColor
        canvas.drawArc(rectF, -90f, sweepAngle, false, paint)
    }
}