package com.demo.antizha.ui

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.hjq.toast.ToastUtils
import java.util.*


@GlideModule
class GlideApp : AppGlideModule() {
    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
}

class HiCore : Application() {
    companion object {
        lateinit var app: HiCore
        lateinit var context: Application
        var mLastClickTime: Long = 0
        fun getContext(): Context {
            return context
        }
    }

    class MyToastStyle : com.hjq.toast.style.BlackToastStyle() {
        override fun getHorizontalPadding(context: Context): Int {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                30f,
                context.resources.displayMetrics)
                .toInt()
        }

        override fun getVerticalPadding(context: Context): Int {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                14f,
                context.resources.displayMetrics)
                .toInt()
        }

        override fun getBackgroundDrawable(context: Context): Drawable {
            val drawable = GradientDrawable()
            // 设置颜色
            drawable.setColor(-0x78000000)
            // 设置圆角
            drawable.cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                30f,
                context.resources.displayMetrics)
            return drawable
        }
    }


    override fun onCreate() {
        super.onCreate()
        context = this
        app = this
        ToastUtils.init(this)
        ToastUtils.setGravity(Gravity.CENTER)
        ToastUtils.setStyle(MyToastStyle())
    }

    fun isDouble(): Boolean {
        val timeInMillis: Long = Calendar.getInstance().timeInMillis
        val j2: Long = timeInMillis - mLastClickTime
        if (j2 in 0..800) {
            return true
        }
        mLastClickTime = timeInMillis
        return false
    }

    fun getChannel(): String {
        return try {
            app.packageManager.getApplicationInfo(packageName,
                PackageManager.GET_META_DATA).metaData.getString("CHANNEL").toString()
        } catch (e2: Exception) {
            e2.printStackTrace()
            "huawei"
        }
    }
}