package com.demo.antizha.util

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

object LogUtils {
    fun debug(str1: String, str2: String) {
        val stringBuilder = StringBuilder()
        stringBuilder.append(str1, str2)
        Log.d("hying out ", stringBuilder.toString())
    }
}