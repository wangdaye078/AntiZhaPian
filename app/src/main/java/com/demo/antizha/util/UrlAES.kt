package com.demo.antizha.util

import android.text.TextUtils
import com.demo.antizha.util.AESUtil.cipherDecrypt

object UrlAES {
    private const val charSet = "UTF-8"
    private const val str_AES = "AES"
    private const val DEFAULT_CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding"


    fun urlDecrypt(str: String): String {
        return try {
            if (TextUtils.isEmpty(str)) {
                ""
            } else
                cipherDecrypt(str.replace(" ".toRegex(), "+"),
                    "hicore2020051518",
                    "hicore2020051518")
        } catch (unused: Exception) {
            ""
        }
    }
}