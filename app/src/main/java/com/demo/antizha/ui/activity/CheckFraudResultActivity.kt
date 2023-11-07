package com.demo.antizha.ui.activity

import android.text.TextUtils
import android.view.View
import com.demo.antizha.R
import com.demo.antizha.databinding.ActivityCheckFraudResultBinding
import com.demo.antizha.ui.SwipBackLayout
import com.demo.antizha.ui.dialog.HiShareDialog
import com.demo.antizha.ui.dialog.ShareConfigBean
import qiu.niorgai.StatusBarCompat


class CheckFraudResultActivity : BaseActivity() {
    private lateinit var infoBinding: ActivityCheckFraudResultBinding
    private var checkType = 0
    private var shareBean = ShareConfigBean()
    private var checkFraudBean = CheckFraudBean()

    override fun initPage() {
        infoBinding = ActivityCheckFraudResultBinding.inflate(layoutInflater)
        setContentView(infoBinding.root)
        SwipBackLayout(this).init()
        StatusBarCompat.translucentStatusBar(this, true, true)
        checkType = intent.getIntExtra("checkType", 0)
        checkFraudBean = intent.getParcelableExtra("checkBean")!!
        shareBean = intent.getParcelableExtra("shareBean")!!
        when (checkType) {
            1 -> infoBinding.piTitle.tvTitle.text = "IP网址查询"
            2 -> infoBinding.piTitle.tvTitle.text = "QQ/微信查询"
            else -> infoBinding.piTitle.tvTitle.text = "支付风险查询"
        }
        infoBinding.tvType.visibility = View.GONE
        infoBinding.tvCotent.text = "查询内容：${checkFraudBean.content}"
        infoBinding.ivPicture.setImageResource(R.mipmap.ic_fraud_safe)
        infoBinding.tvCheckResult.text = "未知"
        infoBinding.tvCheckResult.setTextColor(
            resources.getColor(R.color.fraud_result_color, null))
        infoBinding.warnTxt.visibility = View.VISIBLE
        infoBinding.piTitle.ivBack.setOnClickListener {
            onBackPressed()
        }
        infoBinding.tvShare.setOnClickListener {
            if (!TextUtils.isEmpty(shareBean.downloadUrl)) {
                val shareConfigBean2: ShareConfigBean = this.shareBean
                val str = shareConfigBean2.downloadUrl
                shareConfigBean2.downloadUrl =
                    str.replace("{0}", (System.currentTimeMillis() / 3000).toString())
            }
            val mShareDialog = HiShareDialog(this, shareBean, 2)
            mShareDialog.show()
        }
    }
}