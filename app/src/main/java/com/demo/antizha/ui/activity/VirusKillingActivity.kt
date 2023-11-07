package com.demo.antizha.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.demo.antizha.R
import com.demo.antizha.databinding.ActivityVirusKillingBinding
import com.demo.antizha.util.AppUtil
import com.demo.antizha.util.Utils
import qiu.niorgai.StatusBarCompat
import java.util.*


class ScanAppInfoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var image: ImageView = view.findViewById(R.id.app_icon) as ImageView
    var appName: TextView = view.findViewById(R.id.tv_app_name) as TextView
    var status: ImageView = view.findViewById(R.id.scan_state) as ImageView
}

class ScanAppInfoHolderAdapter(
    private var context: Context,
    private var list: ArrayList<AppUtil.AppInfoBean>
) : RecyclerView.Adapter<ScanAppInfoViewHolder>() {

    override fun onBindViewHolder(holder: ScanAppInfoViewHolder, i: Int) {
        holder.appName.text = list[i].appName
        holder.image.setImageDrawable(list[i].appIcon)
        holder.status.visibility = if (list[i].checkState == 0) View.GONE else View.VISIBLE
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ScanAppInfoViewHolder {
        return ScanAppInfoViewHolder(
            LayoutInflater.from(context).inflate(R.layout.recyclerview_app_scan, viewGroup, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

class VirusKillingActivity : BaseActivity() {
    private lateinit var infoBinding: ActivityVirusKillingBinding
    private var currentState = -1
    private var appScanTimer: Timer? = null
    private var apkScanTimer: Timer? = null
    private var appScanTask: TimerTask? = null
    private var apkScanTask: TimerTask? = null
    private var isPause: Boolean = false
    private var appCount = 0
    private var apkCount = 0
    private lateinit var appInfos: ArrayList<AppUtil.AppInfoBean>

    override fun initPage() {
        infoBinding = ActivityVirusKillingBinding.inflate(layoutInflater)
        setContentView(infoBinding.root)
        infoBinding.piTitle.tvTitle.text = "APP自检"
        infoBinding.piTitle.tvTitle.typeface = typ_ME
        StatusBarCompat.translucentStatusBar(this, true, false)
        infoBinding.piTitle.ivBack.setOnClickListener {
            stopScan()
            finish()
        }
        infoBinding.stopScan.setOnClickListener {
            when (currentState) {
                1 -> {    //正在检测
                    changeScanState(2)
                    isPause = true
                }
                2 -> {    //暂停中
                    changeScanState(1)
                    isPause = false
                }
                3 -> {    //已经完成
                    stopScan()
                    finish()
                }
                4 -> {

                }
            }
        }
        changeScanState(0)
        try {
            infoBinding.scanApp.text = "等待检测应用"
            infoBinding.scanPackage.text = "等待检测安装包"
            showGif2Image(infoBinding.ivAppStates, Integer.valueOf(R.mipmap.ic_scan_wait))
            showGif2Image(infoBinding.ivApkStates, Integer.valueOf(R.mipmap.ic_scan_wait))
        } catch (e2: Exception) {
            e2.printStackTrace()
        }
        infoBinding.rvApp.visibility = View.GONE
        beginScanApp()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun beginScanApp() {
        infoBinding.rvApp.visibility = View.VISIBLE
        appInfos = AppUtil.getAppinfos()
        appCount = appInfos.size
        infoBinding.rvApp.layoutManager = LinearLayoutManager(this)
        val appAdapter = ScanAppInfoHolderAdapter(this, appInfos)
        infoBinding.rvApp.adapter = appAdapter
        appAdapter.notifyDataSetChanged()
        changeScanState(1)

        if (appScanTimer == null) {
            appScanTimer = Timer()
        }
        var sizeSum = 0L
        for (app in appInfos)
            sizeSum += app.size
        val sizeaverage = sizeSum / appInfos.size
        appScanTask = object : TimerTask() {
            var idx: Int = 0
            var scaning = false
            override fun run() {
                if (isPause)
                    return
                if (scaning)
                    return
                scaning = true
                var delay = 0L
                if (idx < appInfos.size)
                    delay = appInfos[idx].size * 200L / sizeaverage
                Handler(Looper.getMainLooper()).postDelayed({
                    if (idx < appInfos.size) {
                        appInfos[idx].checkState = 1
                        appAdapter.notifyItemChanged(idx)
                        infoBinding.rvApp.smoothScrollToPosition(idx)
                        idx++
                        infoBinding.scanAppCount.text = idx.toString()
                    } else {
                        stopScan()
                        infoBinding.rvApp.visibility = View.GONE
                        infoBinding.scanApp.text = "检测应用完成"
                        showGif2Image(infoBinding.ivAppStates, Integer.valueOf(R.mipmap.ic_scan_ok))
                        beginScanPackage()
                    }
                    scaning = false
                }, delay)
            }
        }
        appScanTimer!!.schedule(appScanTask, 0, 30)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun beginScanPackage() {
        val apkInfos: ArrayList<AppUtil.AppInfoBean> = ArrayList<AppUtil.AppInfoBean>()
        /*
        val pm: PackageManager = Hicore.app.getPackageManager()
        val intent = Intent("android.intent.action.MAIN", null as Uri?)
        intent.addCategory("android.intent.category.LAUNCHER")
        val queryIntentActivities = pm.queryIntentActivities(intent, 0)
        var apkNameLogs: ArrayList<String> = ArrayList<String>()
        for (activity in queryIntentActivities) {
            if (AppUtil.contains(activity.activityInfo.packageName) && !apkNameLogs.contains(
                    activity.activityInfo.packageName)) {
                apkInfos.add(AppUtil.getAppinfo(activity.activityInfo.packageName))
                apkNameLogs.add(activity.activityInfo.packageName)
            }
        }*/
        apkCount = apkInfos.size
        infoBinding.rvApk.layoutManager = LinearLayoutManager(this)
        val appAdapter = ScanAppInfoHolderAdapter(this, apkInfos)
        infoBinding.rvApk.adapter = appAdapter
        appAdapter.notifyDataSetChanged()
        if (apkScanTimer == null) {
            apkScanTimer = Timer()
        }
        apkScanTask = object : TimerTask() {
            var idx: Int = 0

            override fun run() {
                if (isPause)
                    return
                Handler(Looper.getMainLooper()).postDelayed({
                    if (idx < apkInfos.size) {
                        apkInfos[idx].checkState = 1
                        appAdapter.notifyItemChanged(idx)
                        infoBinding.rvApk.smoothScrollToPosition(idx)
                        idx++
                        infoBinding.scanPackageCount.text = idx.toString()
                    } else {
                        stopScan()
                        infoBinding.rvApk.visibility = View.GONE
                        infoBinding.scanApp.text = "检测安装包完成"
                        showGif2Image(infoBinding.ivApkStates, Integer.valueOf(R.mipmap.ic_scan_ok))
                        changeScanState(3)
                    }
                }, 0)

            }
        }
        apkScanTimer!!.schedule(apkScanTask, 200, 200)
    }

    fun stopScan() {
        if (appScanTimer != null) {
            appScanTimer!!.cancel()
            appScanTimer = null
        }
        if (apkScanTimer != null) {
            apkScanTimer!!.cancel()
            apkScanTimer = null
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun changeScanState(state: Int) {
        infoBinding.stopScan.setTextColor(resources.getColor(R.color.colorWhite, null))
        infoBinding.ivCircleCenter.visibility = View.GONE
        infoBinding.scanResultTip.visibility = View.GONE
        infoBinding.scanState.visibility = View.GONE
        infoBinding.stopScan.visibility = View.GONE
        infoBinding.lyRbl.layoutRiskBtn.visibility = View.GONE
        when (state) {
            0 -> {
                infoBinding.scanState.text = "等待检测"
                infoBinding.scanState.visibility = View.VISIBLE
                infoBinding.ivCircleCenter.visibility = View.VISIBLE
                stopGif2Img(R.mipmap.ic_scan_rotate)
            }
            1 -> {
                infoBinding.viewBg.background = resources.getDrawable(R.mipmap.ic_blue_bg_big, null)
                infoBinding.ivCircleCenter.visibility = View.VISIBLE
                infoBinding.ivCircle.setImageResource(R.mipmap.ic_scan_rotate)
                Utils.showAnimation(mActivity, R.anim.scan_app_anim, infoBinding.ivCircle)
                infoBinding.stopScan.text = "停止检测"
                infoBinding.stopScan.setTextColor(resources.getColor(R.color.black_dark, null))
                infoBinding.stopScan.background = resources.getDrawable(R.drawable.button_bg_gray,
                    null)
                infoBinding.stopScan.visibility = View.VISIBLE
                currentState = 1
            }
            2 -> {
                infoBinding.viewBg.background = resources.getDrawable(R.mipmap.ic_blue_bg_big, null)
                stopGif2Img(R.mipmap.ic_scan_unfinish)
                infoBinding.scanResultTip.text = "暂停检测应用/安装包"
                infoBinding.scanResultTip.visibility = View.VISIBLE
                infoBinding.stopScan.text = "继续检测"
                infoBinding.stopScan.background =
                    resources.getDrawable(R.drawable.button_bg_them, null)
                infoBinding.stopScan.visibility = View.VISIBLE
                currentState = 2
            }
            3 -> {
                infoBinding.viewBg.background = resources.getDrawable(R.mipmap.ic_blue_bg_big, null)
                infoBinding.viewBg.backgroundTintList = ColorStateList.valueOf(-13479169)
                stopGif2Img(R.mipmap.ic_scan_finish)
                infoBinding.scanResultTip.text = "安全"
                infoBinding.scanResultTip.visibility = View.VISIBLE
                infoBinding.scanState.text = "建议定期检测清理，国家反诈中心实时守护您的网络安全"
                infoBinding.scanState.background =
                    resources.getDrawable(R.drawable.button_bg_blue_dark, null)
                infoBinding.scanState.visibility = View.VISIBLE
                infoBinding.stopScan.text = "安全返回"
                infoBinding.stopScan.background =
                    resources.getDrawable(R.drawable.button_bg_them, null)
                infoBinding.stopScan.visibility = View.VISIBLE
                infoBinding.scanLayout.visibility = View.GONE
                infoBinding.rlRiskResult.visibility = View.GONE
                infoBinding.rlSafeResult.visibility = View.VISIBLE
                infoBinding.safeSpace.visibility = View.VISIBLE
                infoBinding.tvAppCount.text = appCount.toString()
                infoBinding.tvPackageCount.text = apkCount.toString()
                currentState = 3
            }
            4 -> {
                stopGif2Img(R.mipmap.ic_scan_finish_virus)
                infoBinding.viewBg.backgroundTintList = ColorStateList.valueOf(-1289424)
                infoBinding.scanState.text = "建议定期检测清理，国家反诈中心实时守护您的网络安全"
                infoBinding.scanState.background =
                    resources.getDrawable(R.drawable.button_bg_red_dark,
                        null)
                infoBinding.scanState.visibility = View.VISIBLE
                infoBinding.scanLayout.visibility = View.GONE
                infoBinding.rlRiskResult.visibility = View.VISIBLE
                infoBinding.lyRbl.layoutRiskBtn.visibility = View.VISIBLE
                infoBinding.lyRbl.tvDelete.text = "一键清除"
                //riskLayoutDeal()
                //refreshRiskCount()
                infoBinding.scanResultTip.visibility = View.VISIBLE
                currentState = 4
            }
        }
    }

    private fun showGif2Image(imageView: ImageView, num: Int?) {
        try {
            Glide.with(imageView.context).asGif().load(num).into(imageView)
        } catch (unused: Exception) {
        }
    }

    private fun stopGif2Img(resid: Int) {
        try {
            if (infoBinding.ivCircle.drawable != null) {
                infoBinding.ivCircle.clearAnimation()
            }
            infoBinding.ivCircle.setImageResource(resid)
        } catch (e2: Exception) {
            e2.printStackTrace()
            infoBinding.ivCircle.setImageResource(resid)
        }
    }

}