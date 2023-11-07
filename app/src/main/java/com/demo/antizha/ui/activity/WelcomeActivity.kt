package com.demo.antizha.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.demo.antizha.UserInfoBean
import com.demo.antizha.interfaces.IOneClickListener
import com.demo.antizha.md.JniHandStamp
import com.demo.antizha.newwork.DictionaryUtils
import com.demo.antizha.newwork.UpdateUtil


class WelcomeActivity : Activity() {
    private var beginSec: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserInfoBean.init()
        UpdateUtil.mActivity = this
        beginSec = System.currentTimeMillis()
        //初始化网络
        JniHandStamp.handshareKeyAsyn(object : IOneClickListener {
            override fun clickOKBtn() {
                DictionaryUtils.getDictionary()
                UpdateUtil.getVerInfo()
            }
        })
        closeSplash()
    }

    fun finishPage() {
        if (!isFinishing) {
            finish()
        }
    }

    private fun closeSplash() {
        val currentSec = System.currentTimeMillis()
        if (DictionaryUtils.step < 2 || currentSec - beginSec < MainActivity.SPLASH_TIME || UpdateUtil.uppVerDlg != null) {
            Handler(Looper.getMainLooper()).postDelayed({
                closeSplash()
            }, 500)
            return
        }
        val intent = Intent()
        intent.setClass(this, MainActivity::class.java)
        startActivity(intent);
        finishPage();
    }
}