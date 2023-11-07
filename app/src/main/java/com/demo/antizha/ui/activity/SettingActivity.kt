package com.demo.antizha.ui.activity

import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.demo.antizha.databinding.ActivitySettingBinding
import com.demo.antizha.interfaces.IClickListener
import com.demo.antizha.util.DataCleanManager
import com.demo.antizha.util.DialogUtils

class SettingActivity : BaseActivity() {
    private lateinit var infoBinding: ActivitySettingBinding

    override fun initPage() {
        infoBinding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(infoBinding.root)
        infoBinding.piTitle.tvTitle.text = "设置"
        infoBinding.switchShowCheck.isChecked = false
        infoBinding.switchShowPush.isChecked = true
        infoBinding.cacheNum.text = DataCleanManager.getCheckSize(this)
        infoBinding.piTitle.ivBack.setOnClickListener {
            finish()
        }
        infoBinding.logoutBtn.setOnClickListener {
            DialogUtils.showBtTitleDialog(mActivity,
                "您确认要退出登录吗？",
                "",
                "确定",
                "取消",
                -1,
                -1,
                true,
                LogoutListener())
        }
        infoBinding.rlCacheCalean.setOnClickListener {
            infoBinding.cacheNum.text = "0KB"
        }
    }

    inner class LogoutListener : IClickListener {
        override fun cancelBtn() {
            showProgressDialog("退出中...", false)
            Handler(Looper.getMainLooper()).postDelayed({
                hideProgressDialog()
                startActivity(Intent(this@SettingActivity, LoginActivity::class.java))
            }, 100)
        }

        override fun clickOKBtn() {
        }
    }
}