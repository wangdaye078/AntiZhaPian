package com.demo.antizha.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AppOpsManager
import android.app.usage.StorageStatsManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.provider.Settings
import androidx.core.graphics.drawable.toBitmap
import com.demo.antizha.ui.HiCore
import java.io.ByteArrayOutputStream


object AppUtil {
    internal object BytesBitmap {
        fun getBitmap(data: ByteArray): Bitmap {
            return BitmapFactory.decodeByteArray(data, 0, data.size)
        }

        fun getBytes(bitmap: Bitmap): ByteArray {
            val baops = ByteArrayOutputStream()
            bitmap.compress(CompressFormat.PNG, 0, baops)
            return baops.toByteArray()
        }
    }

    var gid: Int = 0

    class AppInfoBean : Parcelable {
        var id = 0
        var selected: Boolean = false
        var apkName: String = ""
        var appName: String = ""
        var version: String = ""
        var appIcon: Drawable? = null
        var checkState: Int = 0
        var size: Long = 0L

        constructor(source: Parcel) {
            id = source.readInt()
            apkName = source.readString().toString()
            appName = source.readString().toString()
            version = source.readString().toString()
            appIcon = BitmapDrawable(HiCore.app.resources,
                BytesBitmap.getBitmap(source.createByteArray()!!))
            checkState = source.readInt()
            size = source.readLong()
        }

        constructor(apkName: String, appName: String, version: String, appIcon: Drawable) {
            this.apkName = apkName
            this.appName = appName
            this.version = version
            this.appIcon = appIcon
            id = ++gid
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(id)
            parcel.writeString(apkName)
            parcel.writeString(appName)
            parcel.writeString(version)
            parcel.writeByteArray(BytesBitmap.getBytes(appIcon!!.toBitmap()))
            parcel.writeInt(checkState)
            parcel.writeLong(size)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<AppInfoBean> {
            override fun createFromParcel(parcel: Parcel): AppInfoBean {
                return AppInfoBean(parcel)
            }

            override fun newArray(size: Int): Array<AppInfoBean?> {
                return arrayOfNulls(size)
            }
        }
    }


    private var list: ArrayList<AppInfoBean> = ArrayList()
    private val apkNames: ArrayList<String> = ArrayList()

    @SuppressLint("ServiceCast")
    fun getAppinfos(): ArrayList<AppInfoBean> {
        if (list.size == 0) {
            val pm = HiCore.context.packageManager
            val sm =
                HiCore.context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
            val packageInfos: List<PackageInfo> = pm.getInstalledPackages(0)

            val intent = Intent("android.intent.action.MAIN", null as Uri?)
            intent.addCategory("android.intent.category.LAUNCHER")
            val queryIntentActivities = pm.queryIntentActivities(intent, 0)
            val apkNameLogs: java.util.ArrayList<String> = java.util.ArrayList<String>()
            for (activity in queryIntentActivities) {
                if (!apkNameLogs.contains(activity.activityInfo.packageName)) {
                    apkNameLogs.add(activity.activityInfo.packageName)
                }
            }

            for (packageInfo in packageInfos) {
                if ((packageInfo.applicationInfo.flags and 1) != 0)
                    continue
                if (!apkNameLogs.contains(packageInfo.packageName))
                    continue
                val appBean = AppInfoBean(packageInfo.packageName,
                    packageInfo.applicationInfo.loadLabel(pm).toString(),
                    packageInfo.versionName,
                    packageInfo.applicationInfo.loadIcon(pm))
                list.add(appBean)
                apkNames.add(packageInfo.packageName)

                val ai = pm.getApplicationInfo(packageInfo.packageName, 0)
                val storageStats = sm.queryStatsForUid(ai.storageUuid, ai.uid)
                appBean.size = storageStats.appBytes
            }
        }
        return list
    }

    fun contains(name: String): Boolean {
        return apkNames.contains(name)
    }

    fun getAppinfo(name: String): AppInfoBean {
        return list[apkNames.indexOf(name)]
    }

    @Suppress("DEPRECATION")
    fun checkPermission(activity: Activity, ischeckOpen: Boolean): Boolean {
        val pm = HiCore.context.packageManager
        val appInfo = pm.getApplicationInfo(HiCore.context.packageName, 0)
        val appOpsManager =
            HiCore.context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val op = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            appOpsManager.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                appInfo.uid,
                appInfo.packageName)
        else
            appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                appInfo.uid,
                appInfo.packageName)
        val opened = (op == AppOpsManager.MODE_ALLOWED)
        if (ischeckOpen == opened)
            return true
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        intent.data = Uri.fromParts("package", activity.packageName, null)
        try {
            activity.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
        return false
    }
}