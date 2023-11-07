package com.demo.antizha.ui.activity

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import com.demo.antizha.BuildConfig
import com.demo.antizha.UserInfoBean
import com.demo.antizha.databinding.ActivityAboutUsBinding
import com.demo.antizha.newwork.FileUtil
import com.hjq.toast.ToastUtils


class AboutUsActivity : BaseActivity() {
    private lateinit var infoBinding: ActivityAboutUsBinding
    override fun initPage() {
        infoBinding = ActivityAboutUsBinding.inflate(layoutInflater)
        setContentView(infoBinding.root)
        infoBinding.piTitle.tvTitle.text = "关于我们"
        infoBinding.tvAppVersion.text = "v${UserInfoBean.version}"
        infoBinding.piTitle.ivBack.setOnClickListener {
            finish()
        }
        infoBinding.checkUpadte.setOnClickListener {
            showProgressDialog("检测中...", true)
            if (!TextUtils.isEmpty(UserInfoBean.acctoken))
                FileUtil.update()
            Handler(Looper.getMainLooper()).postDelayed({
                hideProgressDialog()
                ToastUtils.show("已是最新版本")
            }, 500)
        }
        infoBinding.llAgreement.setOnClickListener {
            val intentInfo = Intent(this, WebActivity::class.java)
            intentInfo.putExtra(WebActivity.EXTRA_WEB_TITLE, "服务协议")
            intentInfo.putExtra(
                WebActivity.EXTRA_WEB_URL,
                BuildConfig.RELEASE_H5_URL + "/Agreements/clause.html?time=" + System.currentTimeMillis() / 3000
            )
            startActivity(intentInfo)
        }
        infoBinding.llPolicy.setOnClickListener {
            val intentInfo = Intent(this, WebActivity::class.java)
            intentInfo.putExtra(WebActivity.EXTRA_WEB_TITLE, "隐私政策")
            intentInfo.putExtra(
                WebActivity.EXTRA_WEB_URL,
                BuildConfig.RELEASE_H5_URL + "/Agreements/policy.html?time=" + System.currentTimeMillis() / 3000
            )
            startActivity(intentInfo)
        }
    }

}