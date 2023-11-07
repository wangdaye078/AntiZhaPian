package com.demo.antizha.util

import android.content.Context
import android.os.Environment
import java.io.File
import java.math.BigDecimal


object DataCleanManager {

    private fun fileSize(file: File?): Long {
        if (file == null)
            return 0
        var sizeSum: Long = 0
        try {
            val listFiles = file.listFiles() ?: return 0
            for (i in listFiles.indices) {
                val fileSize = if (listFiles[i].isDirectory) {
                    fileSize(listFiles[i])
                } else {
                    listFiles[i].length()
                }
                sizeSum += fileSize
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return sizeSum
    }

    private fun fileSize2String(size: Long): String {
        val sizeKB = size / 1024
        if (sizeKB.toDouble() < 1.0) {
            return sizeKB.toString() + "kB"
        }
        val sizeMB = sizeKB.toDouble() / 1024.0
        if (sizeMB < 1.0) {
            val bigDecimal = BigDecimal(sizeKB.toString())
            return bigDecimal.setScale(1, 4).toPlainString().toString() + "KB"
        }
        val sizeGB = sizeMB / 1024.0
        if (sizeGB < 1.0) {
            val bigDecimal = BigDecimal(sizeMB.toString())
            return bigDecimal.setScale(1, 4).toPlainString().toString() + "MB"
        }
        val sizeTB = sizeGB / 1024.0
        if (sizeTB < 1.0) {
            val bigDecimal = BigDecimal(sizeGB.toString())
            return bigDecimal.setScale(2, 4).toPlainString().toString() + "GB"
        }
        val bigDecimal = BigDecimal(sizeTB)
        return bigDecimal.setScale(2, 4).toPlainString().toString() + "TB"
    }

    fun getCheckSize(context: Context): String {
        var size = fileSize(context.cacheDir)
        if (Environment.getExternalStorageState() == "mounted") {
            size += fileSize(context.externalCacheDir)
        }
        return fileSize2String(size)
    }

}