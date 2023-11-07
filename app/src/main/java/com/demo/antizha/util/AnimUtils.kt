package com.demo.antizha.util

import android.animation.ObjectAnimator
import android.view.animation.LinearInterpolator
import android.widget.ImageView

object AnimUtils {
    private const val DURATION = 250

    /**
     * 箭头旋转动画
     *
     * @param arrow
     * @param isFlag
     */
    fun rotateArrow(arrow: ImageView?, isFlag: Boolean) {
        val srcValue: Float
        val targetValue: Float
        if (isFlag) {
            srcValue = 0f
            targetValue = 180f
        } else {
            srcValue = 180f
            targetValue = 360f
        }
        val objectAnimator = ObjectAnimator.ofFloat(arrow, "rotation", srcValue, targetValue)
        objectAnimator.duration = DURATION.toLong()
        objectAnimator.interpolator = LinearInterpolator()
        objectAnimator.start()
    }
}
