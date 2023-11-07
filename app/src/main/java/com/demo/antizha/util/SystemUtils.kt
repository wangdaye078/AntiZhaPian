package com.demo.antizha.util

import android.app.Activity
import android.content.res.Configuration
import android.os.Build


object SystemUtils {

    /* renamed from: a */
    fun adjustFontScale(activity: Activity) {
        val configuration: Configuration = activity.resources.configuration
        if (configuration.fontScale > 1.0f) {
            configuration.fontScale = 0.9f
            activity.baseContext.createConfigurationContext(configuration)
        }
    }

    private fun getAndroidVer(): String {
        return Build.VERSION.RELEASE
    }

    fun getOsVer(): String {
        return "Android " + getAndroidVer()
    }

    fun getBrand(): String {
        return Build.BRAND
    }

    fun getModel(): String {
        return Build.MODEL
    }
}