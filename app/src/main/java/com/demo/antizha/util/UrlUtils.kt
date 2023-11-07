package com.demo.antizha.util

import android.text.TextUtils
import java.io.IOException
import java.net.URL
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

object UrlUtils {
    fun separateParam(str: String): Parameters {
        val aVar: Parameters? = if (TextUtils.isEmpty(str) || str.indexOf('?') <= -1) {
            null
        } else {
            val substring = str.substring(str.indexOf('?') + 1)
            val indexOf = substring.indexOf('#')
            if (indexOf > -1)
                string2Param(substring.substring(0, indexOf))
            else
                string2Param(substring)
        }
        return aVar ?: Parameters()
    }

    fun string2Param(paramString: String): Parameters {
        val params = Parameters()
        try {
            val paramList: List<String> = paramString.split("&")
            if (paramList.isNotEmpty()) {
                for (param in paramList) {
                    val paramSplit: List<String> = param.split("=", ignoreCase = false, limit = 2)
                    if (paramSplit.size == 2) {
                        params.insert(
                            URLDecoder.decode(paramSplit[0], StandardCharsets.UTF_8.toString()),
                            URLDecoder.decode(paramSplit[1], StandardCharsets.UTF_8.toString())
                        )
                    }
                }
            }
        } catch (e2: Exception) {
            e2.message
        }
        return params
    }

    @Throws(IOException::class)
    fun removeParam(urlString: String, param: String): String {
        if (param.isEmpty()) {
            return urlString
        }
        val url = URL(urlString)
        if (url.query.isEmpty()) {
            return urlString
        }
        if (!urlString.contains("&$param")) {
            if (!urlString.contains("?$param")) {
                return urlString
            }
        }
        return urlString.replace(param, "")
    }
}