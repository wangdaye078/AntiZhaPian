package com.demo.antizha.newwork

import android.text.TextUtils
import com.demo.antizha.BuildConfig
import com.demo.antizha.interfaces.IApiResult
import com.demo.antizha.md.JniHandStamp
import com.demo.antizha.newwork.UnsafeOkHttpClient.getDataByPost
import com.demo.antizha.util.LogUtils
import com.hjq.toast.ToastUtils
import okhttp3.Headers

object FileUtil {
    class NormalSave : IApiResult {
        var saveFile: String

        constructor(saveFile: String) {
            this.saveFile = saveFile
        }

        override fun onError() {
            LogUtils.debug("NormalSave Error", "")
        }

        override fun onSuccess(data: String) {
            LogUtils.debug("NormalSave Success", data)
            val text = JniHandStamp.getSData(data)
            if (TextUtils.isEmpty(text))
                return
            if (!TextUtils.isEmpty(saveFile)) {
                ToastUtils.cancel()
                ToastUtils.show("savefile:$saveFile")
                saveBuff2File(text, saveFile)
            }
        }
    }

    fun update() {
        //轮播
        val bannerUrl = BuildConfig.RELEASE_API_URL + "/api/Banner/getbanners"
        getDataByPost(bannerUrl, null, true, NormalSave("bander.txt"))
        //行业分类
        //val positionsUrl = BuildConfig.RELEASE_API_URL + "/api/Position/positionsv2"
        //getDataByPost(positionsUrl, null, true, "positions.txt", ::onNormalSave)
        //主界面右上角的消息
        val noticeListUrl = BuildConfig.RELEASE_API_URL + "/api/Notice/getnoticelistforuserv2"
        getDataByPost(noticeListUrl, null, true, NormalSave("noticelist.txt"))
        val lastnoticeListUrl =
            BuildConfig.RELEASE_API_URL + "/api/Notice/getlastestnoticeforuserv2"
        getDataByPost(lastnoticeListUrl, null, true, NormalSave("lastnoticelist.txt"))
        //社交账号类型
        val saccTypesUrl = BuildConfig.RELEASE_API_URL + "/api/EvidenceType/getsocialaccounttypesv2"
        getDataByPost(saccTypesUrl, null, true, NormalSave("socialaccounttypes.txt"))
        //支付账号类型
        val paymenttypesUrl = BuildConfig.RELEASE_API_URL + "/api/EvidenceType/getpaymenttypesv2"
        getDataByPost(paymenttypesUrl, null, true, NormalSave("paymenttypes.txt"))
        //帮助与反馈
        val qaListUrl = BuildConfig.RELEASE_API_URL + "/api/QA/getqalistv2"
        getDataByPost(qaListUrl, null, true, NormalSave("qalist.txt"))
        //进入警察界面好像是实名认证的时候会自动根据数据库记录判断你是不是警察，是的话，在首页上会显示一个警察专用图标，
        //点击以后再进入警察专用登陆界面，再次登陆后，才可以打开警察界面，可以进行报案登记什么的.
        //CaseActivity里用到，CaseActivity由PoliceInfoActivity调用，可能是警察报案登记界面用的？
        val xccasecategorysUrl = BuildConfig.RELEASE_API_URL + "/api/xc/getxccasecategorysv2"
        getDataByPost(xccasecategorysUrl, null, true, NormalSave("xccasecategorys.txt"))

        //默认的诈骗类型数据
        val evidenceTypeUrl = BuildConfig.RELEASE_API_URL + "/api/EvidenceType/getevidencetype"
        getDataByPost(evidenceTypeUrl, null, true, NormalSave("EvidenceType.txt"))

        val readpointUrl = BuildConfig.RELEASE_API_URL + "/api/home/getreadpointv2"
        getDataByPost(readpointUrl, null, true, NormalSave("readpoint.txt"))
        val osaUrl = BuildConfig.RELEASE_API_URL + "/api/OpenScreenAdvertising/get"
        getDataByPost(osaUrl, null, true, NormalSave("osa.txt"))

        /*
        //SnapCardReportActivity里用的，可能是信用卡诈骗的类型
        val casecategorysUrl = BuildConfig.RELEASE_API_URL + "/api/DK/getcasecategorys"
        getDataByGet(casecategorysUrl, true, NormalSave("casecategorys.txt"))*/
    }
}