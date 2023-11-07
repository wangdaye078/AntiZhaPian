package com.demo.antizha.util

import kotlin.collections.set

class Parameters {
    private val parameters: MutableMap<String?, MutableMap<Int, Any>?> =
        LinkedHashMap()

    private var valueMaxCount = -1

    private var valueCount = 0

    private var uniqueKey = 0

    @Throws(IllegalStateException::class)
    fun insert(key: String?, value: Any?) {
        if (key != null) {
            if (objIsEffective(value)) {
                valueCount++
                val i2 = valueMaxCount
                if (i2 <= -1 || valueCount <= i2) {
                    var map = parameters[key]
                    if (map == null) {
                        map = LinkedHashMap(1)
                        parameters[key] = map
                    }
                    val valueOf = Integer.valueOf(uniqueKey++)
                    map[valueOf] = value ?: ""
                    return
                }
                throw IllegalStateException("parameters.maxCountFail: $valueMaxCount")
            }
            throw IllegalArgumentException("Please use value which is primitive type like: String,Integer,Long and so on. But not Collection !")
        }
    }

    fun value(str: String): String {
        val map: Map<Int, Any>? = parameters[str]
        if (map == null || map.isEmpty()) {
            return ""
        }
        val obj = map.values.iterator().next().toString()
        return if ("null" == obj) "" else obj
    }

    val isEmpty: Boolean
        get() = parameters.isEmpty()

    fun keys(): Set<String?> {
        return parameters.keys
    }

    private fun objIsEffective(obj: Any?): Boolean {
        return obj == null || obj is String || obj is Int || obj is Long || obj is Boolean || obj is Float || obj is Double || obj is Char || obj is Byte || obj is Short
    }

    private fun toStringArray(values: MutableCollection<Any>): ArrayList<String> {
        val strArray = ArrayList<String>()
        for (obj in values)
            strArray.add(obj.toString())
        return strArray
    }

    fun values(key: String): ArrayList<String> {
        val map = parameters[key]
        return if (map == null)
            ArrayList()
        else
            toStringArray(map.values)
    }
}