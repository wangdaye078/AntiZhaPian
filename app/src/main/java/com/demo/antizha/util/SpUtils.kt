package com.demo.antizha.util

import android.content.SharedPreferences
import com.demo.antizha.ui.HiCore


class SpUtils {
    companion object {
        private var sharedPreferences: SharedPreferences? = null
        const val warnCall = "warn_call_open"
        const val warnSms = "warn_sms_open"
        const val primissAuto = "primiss_auto"
        const val primissPower = "primiss_power"
        const val primissLock = "primiss_lock"
        private fun getSharedPreferences(): SharedPreferences? {
            if (sharedPreferences == null) {
                sharedPreferences = HiCore.app.getSharedPreferences("note_national", 0)
            }
            return sharedPreferences
        }
        fun getValue(str: String?, bool: Boolean): Boolean {
            return getSharedPreferences()!!.getBoolean(str, bool)
        }
        fun setValue(str: String?, bool: Boolean) {
            getSharedPreferences()!!.edit().putBoolean(str, bool).apply()
        }

    }

}