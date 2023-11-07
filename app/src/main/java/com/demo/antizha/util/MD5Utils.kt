package com.demo.antizha.util

import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


object MD5Utils {

    fun getMd5StringCharLowercase(str: String): String {
        return getMd5StringChar(str).lowercase(Locale.getDefault())
    }

    private fun getMd5StringChar(str: String): String {
        return try {
            val instance: MessageDigest = MessageDigest.getInstance("MD5")
            val bArr = str.toByteArray()
            val digest: ByteArray = instance.digest(bArr)
            val stringBuffer = StringBuffer()
            for (element in digest) {
                stringBuffer.append(String.format("%02X",
                    Integer.valueOf(element.toInt() and 255)))
            }
            stringBuffer.toString()
        } catch (e2: UnsupportedEncodingException) {
            e2.printStackTrace()
            ""
        } catch (e3: NoSuchAlgorithmException) {
            e3.printStackTrace()
            ""
        }
    }
    fun getMd5Half(str: String): String {
        return getMd5StringUtf8(str).substring(8, 24)
    }

    fun getMd5StringUtf8(str: String): String {
        return try {
            val instance: MessageDigest = MessageDigest.getInstance("MD5")
            val bArr = str.toByteArray(charset("UTF-8"))
            val digest: ByteArray = instance.digest(bArr)
            val stringBuffer = StringBuffer()
            for (element in digest) {
                stringBuffer.append(String.format("%02X",
                    Integer.valueOf(element.toInt() and 255)))
            }
            stringBuffer.toString()
        } catch (e2: UnsupportedEncodingException) {
            e2.printStackTrace()
            ""
        } catch (e3: NoSuchAlgorithmException) {
            e3.printStackTrace()
            ""
        }
    }
}