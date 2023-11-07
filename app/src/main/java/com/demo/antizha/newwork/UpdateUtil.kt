package com.demo.antizha.newwork

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.demo.antizha.BuildConfig
import com.demo.antizha.R
import com.demo.antizha.UserInfoBean
import com.demo.antizha.interfaces.IApiResult
import com.demo.antizha.md.JniHandStamp
import com.demo.antizha.newwork.UnsafeOkHttpClient.getDataByPost
import com.demo.antizha.ui.BaseDialog
import com.demo.antizha.util.LogUtils
import com.demo.antizha.util.RegisterBody
import com.google.gson.Gson
import okhttp3.Headers


object UpdateUtil : View.OnClickListener {
    var mActivity: Context? = null
    var uppVerDlg: BaseDialog? = null

    class DownloadInfo {
        var url: String? = null
        var isUpdatable = false
        var fileMD5: String? = null
        var version: String? = null
        var innerVersion = 0
        var content: String? = null
        var isForcedVersion = false
    }

    class OnGetVersion : IApiResult {
        var saveFile: String

        constructor(saveFile: String) {
            this.saveFile = saveFile
        }

        override fun onError() {
            LogUtils.debug("OnGetVersion Error", "")
        }

        override fun onSuccess(data: String) {
            LogUtils.debug("OnGetVersion Success", data)
            onGetVersion(data, saveFile)
        }
    }

    class DownloadInfoPackage(val data: DownloadInfo, val code: Int)

    fun getVerInfo() {
        val registerBody = RegisterBody()
        registerBody.imei = UserInfoBean.imei
        registerBody.innerversion = UserInfoBean.innerVersion.toString()
        getDataByPost(
            BuildConfig.RELEASE_API_URL + "/api/AppVersion/checkv2",
            bodyMap = JniHandStamp.princiHttp(registerBody),
            addHead = true,
            OnGetVersion("AppVersion.txt")
        )
    }

    /*新颁布返回类似信息，无法获得最新版本号*/
    /*{"code":2,"msg":"当前版本过低，请前往应用市场下载最新版本"}*/
    @Suppress("UNUSED_PARAMETER")
    fun onGetVersion(data: String, saveFile: String) {
        val text = JniHandStamp.getSData(data)
        val ver = Gson().fromJson(text, DownloadInfoPackage::class.java)
        val oldVer = UserInfoBean.version
        val currentVer = ver.data.version.toString()

        if (ver.code == 0)
            UserInfoBean.setVer(currentVer, ver.data.innerVersion)
        if (!TextUtils.equals(currentVer, oldVer))
            showDialogUpdate(ver.data)
        saveBuff2File(text, saveFile)
    }

    fun showDialogUpdate(downloadInfo: DownloadInfo) {
        uppVerDlg = BaseDialog(mActivity!!, R.style.base_dialog_style)
        uppVerDlg!!.setContentView(R.layout.dialog_update)
        uppVerDlg!!.setGravityLayout(2)
        uppVerDlg!!.setWidthDialog(-2.0)
        uppVerDlg!!.setHeightDialog(-2.0)
        uppVerDlg!!.setCancelable(false)
        uppVerDlg!!.setCanceledOnTouchOutside(false)
        uppVerDlg!!.initOnCreate()
        uppVerDlg!!.show()
        val tv_title = uppVerDlg!!.findViewById(R.id.update_title) as TextView
        val tv_content = uppVerDlg!!.findViewById(R.id.update_content) as TextView
        val tv_update = uppVerDlg!!.findViewById(R.id.update) as TextView
        val iv_close = uppVerDlg!!.findViewById(R.id.update_close) as ImageView
        if (downloadInfo.isForcedVersion) {
            iv_close.setVisibility(View.INVISIBLE)
        } else {
            iv_close.setVisibility(View.VISIBLE)
        }
        tv_title.setText("v" + downloadInfo.version)
        tv_content.setText(downloadInfo.content?.replace("\\n", "\n")?.replace(" ", ""))

        tv_update.setOnClickListener(this)
        iv_close.setOnClickListener(this)
        /*
        Handler(Looper.getMainLooper()).postDelayed({
            if (uppVerDlg != null) {
                uppVerDlg!!.dismiss()
                uppVerDlg = null
                DictionaryUtils.getDictionary()
            }
        }, 4000)
        */
    }

    override fun onClick(view: View) {
        if (uppVerDlg != null) {
            uppVerDlg!!.dismiss()
            uppVerDlg = null
            DictionaryUtils.getDictionary()
        }
    }
}