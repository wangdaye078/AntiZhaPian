package com.demo.antizha.ui

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.*


open class BaseDialog : Dialog {
    companion object {
        const val BOTTOM = 0
        const val CENTER = 2
        const val TOP = 1
        const val WARPHEIGHT = -2.0f
        const val WARPWIDTH = -2.0f
    }

    private var mContext: Context
    private lateinit var display: Display
    private lateinit var dm: DisplayMetrics
    private lateinit var lp: WindowManager.LayoutParams
    private lateinit var mWindow: Window
    private lateinit var metrics: WindowMetrics
    var widthDialog = 0.0F
    var heightDialog = 0.0F
    var widthDialogdp = 0.0F
    var heightDialogdp = 0.0F
    private var mGravityLayout: Int = 0

    constructor(context: Context) : super(context) {
        mContext = context
        initWindowState()
    }

    constructor(context: Context, resId: Int) : super(context, resId) {
        mContext = context
        initWindowState()
    }

    @Suppress("DEPRECATION")
    private fun initWindowState() {
        mWindow = window!!
        lp = mWindow.attributes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display = context.display!!
            dm = mContext.resources.displayMetrics
            metrics = (mContext as Activity).windowManager.currentWindowMetrics
        } else {
            val windowManager = (mContext as Activity).windowManager
            display = windowManager.defaultDisplay
            dm = DisplayMetrics()
            display.getMetrics(dm)
        }
    }

    fun dp2px(f2: Float): Int {
        return (f2 * mContext.resources.displayMetrics.density + 0.5f).toInt()
    }

    private val statusBarHeight: Int
        get() = try {
            val cls = Class.forName("com.android.internal.R\$dimen")
            val tfield = cls.getField("status_bar_height")
            context.resources.getDimensionPixelSize(tfield[cls.newInstance()]!!.toString().toInt())
        } catch (e2: Exception) {
            e2.printStackTrace()
            0
        }

    fun <T : View?> getViewById(i2: Int): T {
        return findViewById<T>(i2)
    }

    @Suppress("DEPRECATION")
    fun initOnCreate() {
        val layoutParams = lp
        layoutParams.gravity = mGravityLayout
        if (widthDialog > 0.0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                layoutParams.width = (metrics.bounds.width().toDouble() * widthDialog).toInt()
            else
                layoutParams.width = (display.width * widthDialog).toInt()
        } else {
            val f2 = widthDialogdp
            if (f2 > 0.0f) {
                layoutParams.width = dp2px(f2)
            } else if (f2 == -2.0f) {
                layoutParams.width = -2
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                    layoutParams.width = metrics.bounds.width()
                else
                    layoutParams.width = display.width
            }
        }
        if (heightDialog > 0.0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                lp.height = (metrics.bounds.height().toDouble() * heightDialog).toInt()
            else
                lp.height = (display.height * heightDialog).toInt()
        } else {
            val f3 = heightDialogdp
            if (f3 > 0.0f) {
                lp.height = dp2px(f3)
            } else if (f3 == -2.0f) {
                lp.height = -2
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                    lp.height = metrics.bounds.height() - statusBarHeight
                else
                    lp.height = display.height - statusBarHeight
            }
        }
        mWindow.attributes = lp
    }

    fun setGravityLayout(i2: Int) {
        if (i2 == 0) {
            mGravityLayout = 80
        }
        if (1 == i2) {
            mGravityLayout = 48
        }
        if (2 == i2) {
            mGravityLayout = 17
        }
    }

    fun setHeightDialog(d: Double) {
        heightDialog = d.toFloat()
    }

    fun setHeightDialogdp(d: Double) {
        heightDialogdp = d.toFloat()
    }

    fun setWidthDialog(d: Double) {
        widthDialog = d.toFloat()
    }

    fun setWidthDialogdp(d: Double) {
        widthDialogdp = d.toFloat()
    }

}