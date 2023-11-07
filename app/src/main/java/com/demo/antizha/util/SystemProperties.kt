package com.demo.antizha.util

object SystemProperties {
    fun getInt(key: String?): Int {
        return try {
            val getIntMethod = Class.forName("android.os.SystemProperties")
                .getMethod("getInt", String::class.java, Int::class.javaPrimitiveType)
            (getIntMethod.invoke(null, key, -1) as Int).toInt()
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    fun getString(key: String?): String? {
        var value: String? = null
        try {
            val cls = Class.forName("android.os.SystemProperties")
            val hideMethod = cls.getMethod("get", String::class.java)
            val tobject = cls.newInstance()
            value = hideMethod.invoke(tobject, key) as String
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return value
    }
}