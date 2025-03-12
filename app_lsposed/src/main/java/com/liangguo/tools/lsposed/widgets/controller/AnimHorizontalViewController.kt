package com.liangguo.tools.lsposed.widgets.controller

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.view.isVisible


/**
 * @author hesleyliang
 * 时间: 2024/9/29 10:23
 * 邮箱: liang.dh@outlook.com
 *
 * 控制View在显示或隐藏的时候带上动画，类似Compose中的 expandHorizontally() + fadeIn()
 */
class AnimHorizontalViewController(val view: View, duration: Long = 300L) {

    private val showAnimatorSet = AnimatorSet()

    private val hideAnimatorSet = AnimatorSet()

    /** 设置View的是否显示，切换的时候会带上动画 */
    var viewVisibility: Boolean
        get() = view.isVisible
        set(value) {
            if (value && !view.isVisible) {
                view.isVisible = true
                showAnimatorSet.start()
            }
            if (!value && view.isVisible) {
                hideAnimatorSet.start()
            }
        }

    init {
        val fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
        fadeIn.interpolator = DecelerateInterpolator()

        val expandHorizontal = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f)
        expandHorizontal.interpolator = DecelerateInterpolator()

        showAnimatorSet.playTogether(fadeIn, expandHorizontal)
        showAnimatorSet.setDuration(duration)

        val fadeOut = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f)
        fadeOut.interpolator = AccelerateDecelerateInterpolator()

        val shrinkHorizontal = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f)
        shrinkHorizontal.interpolator = AccelerateDecelerateInterpolator()

        hideAnimatorSet.playTogether(fadeOut, shrinkHorizontal)
        hideAnimatorSet.setDuration(duration)
        hideAnimatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                view.visibility = View.GONE
            }
        })
    }

}