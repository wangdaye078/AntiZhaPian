package com.demo.antizha.util

import android.content.SharedPreferences
import android.text.TextUtils
import android.util.Base64
import com.demo.antizha.ui.HiCore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


object AESUtil {
    private var cipher: Cipher? = null
    private val charSet = charset("UTF-8")
    private const val str_MD5 = "MD5"
    private const val str_AES = "AES"
    private const val KEY_ALGORITHM = "SHA1PRNG"
    private const val DEFAULT_CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding"
    private const val PRIVATE_KEY_SIZE_BIT = 128
    private const val PRIVATE_KEY_SIZE_BYTE = 16
    private var cryptoSalt: ByteArray? = null
    private const val ACCEPT_TIME_SEPARATOR_SP = ","

    @Throws(Exception::class)
    private fun createSecretKey(str: String): ByteArray? {
        if (cryptoSalt == null || cryptoSalt!!.size != 32) {
            val sharedPreferences: SharedPreferences =
                HiCore.app.getSharedPreferences("crypto_info", 0)
            val string = sharedPreferences.getString("salt", "").toString()
            if (!TextUtils.isEmpty(string)) {
                cryptoSalt = splitString2ByteArray(string)
            }
            if (cryptoSalt == null || cryptoSalt!!.size != 32) {
                val bArr3 = ByteArray(32)
                SecureRandom().nextBytes(bArr3)
                sharedPreferences.edit().putString("salt", byteArray2SplitString(bArr3)).apply()
                cryptoSalt = bArr3
            }
        }
        return SecretKeySpec(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            .generateSecret(PBEKeySpec(str.toCharArray(), cryptoSalt, 1000, 256)).encoded,
            str_AES).encoded
    }

    private fun splitString2ByteArray(str: String): ByteArray? {
        return splitString2ByteArray(str, "", 0)
    }

    private fun splitString2ByteArray(str: String, separator: String, default: Byte): ByteArray? {
        var separate = separator
        if (TextUtils.isEmpty(str)) {
            return null
        }
        if (TextUtils.isEmpty(separator)) {
            separate = ACCEPT_TIME_SEPARATOR_SP
        }
        val split = str.split(separate.toRegex()).toTypedArray()
        val length = split.size
        val bArr = ByteArray(length)
        for (i2 in 0 until length) {
            try {
                bArr[i2] = split[i2].toByte()
            } catch (unused: Exception) {
                bArr[i2] = default
            }
        }
        return bArr
    }

    private fun byteArray2SplitString(bArr: ByteArray?): String? {
        return byteArray2SplitString(bArr, null as String?)
    }

    private fun byteArray2SplitString(bArr: ByteArray?, separator: String?): String? {
        var separate = separator
        if (bArr == null) {
            return null
        }
        val length = bArr.size
        if (length <= 0) {
            return ""
        }
        if (TextUtils.isEmpty(separate)) {
            separate = ACCEPT_TIME_SEPARATOR_SP
        }
        val sb = StringBuilder()
        for (i in 0 until length) {
            sb.append(bArr[i].toString())
            if (i != length - 1) {
                sb.append(separate)
            }
        }
        return sb.toString()
    }

    fun encrypt(seedkey: String, input: String): String {
        val seed = MD5Utils.getMd5Half(seedkey)
        return if (seed.length == PRIVATE_KEY_SIZE_BYTE) {
            try {
                cipherInit(seed, 1)
                byteArray2HexStr(cipher!!.doFinal(input.toByteArray(charSet)))
            } catch (err: Exception) {
                throw RuntimeException("AESUtil:encrypt fail!", err)
            }
        } else {
            throw RuntimeException("AESUtil:Invalid AES secretKey length (must be 16 bytes)")
        }
    }

    fun decrypt(seedkey: String, input: String): String {
        val seed = MD5Utils.getMd5Half(seedkey)
        return if (seed.length == PRIVATE_KEY_SIZE_BYTE) {
            try {
                cipherInit(seed, 2)
                String(cipher!!.doFinal(hexString2ByteArray(input)), charSet)
            } catch (e2: Exception) {
                throw RuntimeException("AESUtil:decrypt fail!", e2)
            }
        } else {
            throw RuntimeException("AESUtil:Invalid AES secretKey length (must be 16 bytes)")
        }
    }

    fun byteArray2HexStr(bArr: ByteArray): String {
        val sb = StringBuilder(bArr.size * 2)
        val length = bArr.size
        for (i in 0 until length) {
            sb.append(String.format("%02X", java.lang.Byte.valueOf(bArr[i])))
        }
        return sb.toString()
    }

    private fun hexString2ByteArray(str: String): ByteArray {
        val bArr = ByteArray(str.length / 2)
        var i2 = 0
        while (i2 < str.length) {
            val i3 = i2 + 2
            bArr[i2 / 2] = str.substring(i2, i3).toInt(16).toByte()
            i2 = i3
        }
        return bArr
    }

    private fun cipherInit(seed: String, opmode: Int) {
        try {
            val instance = SecureRandom.getInstance(KEY_ALGORITHM)
            instance.setSeed(seed.toByteArray())
            KeyGenerator.getInstance(str_AES).init(PRIVATE_KEY_SIZE_BIT, instance)
            val secretKeySpec = SecretKeySpec(seed.toByteArray(), str_AES)
            cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM)
            cipher!!.init(opmode,
                secretKeySpec,
                IvParameterSpec(MD5Utils.getMd5Half(seed).toByteArray()))
        } catch (err: Exception) {
            throw RuntimeException("AESUtil:initParam fail!", err)
        }
    }

    @Throws(java.lang.Exception::class)
    fun cipherEncrypt(str: String, keySalt: String, iv: String): String {
        val instance = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM)
        val secretKeySpec = SecretKeySpec(keySalt.toByteArray(charSet), str_AES)
        instance.init(Cipher.ENCRYPT_MODE, secretKeySpec, IvParameterSpec(iv.toByteArray(charSet)))
        return Base64.encodeToString(instance.doFinal(str.toByteArray(charSet)), 0)
    }

    @Throws(java.lang.Exception::class)
    fun cipherDecrypt(str: String?, keySalt: String, iv: String): String {
        val instance = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM)
        val secretKeySpec = SecretKeySpec(keySalt.toByteArray(charSet), str_AES)
        instance.init(Cipher.DECRYPT_MODE, secretKeySpec, IvParameterSpec(iv.toByteArray(charSet)))
        return String(instance.doFinal(Base64.decode(str, 0)), charSet)
    }

    @Throws(java.lang.Exception::class)
    fun cipherEncrypt_ZeroPadding(str: String, keySalt: ByteArray, iv: String): String {
        val instance = Cipher.getInstance("AES/CBC/NoPadding")

        val blockSize = instance!!.blockSize
        val dataBytes: ByteArray = str.toByteArray(charSet)
        var plaintextLength = dataBytes.size
        if (plaintextLength % blockSize != 0) {
            plaintextLength += (blockSize - plaintextLength % blockSize)
        }
        val plaintext = ByteArray(plaintextLength)
        System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.size)

        val secretKeySpec = SecretKeySpec(keySalt, str_AES)
        instance.init(Cipher.ENCRYPT_MODE, secretKeySpec, IvParameterSpec(iv.toByteArray(charSet)))
        return Base64.encodeToString(instance.doFinal(plaintext), 0).trim()
    }

    @Throws(java.lang.Exception::class)
    fun cipherDecrypt_ZeroPadding(str: String?, keySalt: ByteArray, iv: String): String {
        val instance = Cipher.getInstance("AES/CBC/NoPadding")
        val secretKeySpec = SecretKeySpec(keySalt, str_AES)
        instance.init(Cipher.DECRYPT_MODE, secretKeySpec, IvParameterSpec(iv.toByteArray(charSet)))
        return String(instance.doFinal(Base64.decode(str, 0)), charSet).trimEnd(0.toChar())
    }
}