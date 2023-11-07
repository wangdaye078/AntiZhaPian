package com.demo.antizha.ui.activity

import android.content.Intent
import android.text.TextUtils
import com.demo.antizha.adapter.SmsBean
import com.demo.antizha.databinding.ActivityCallAddBinding
import com.hjq.toast.ToastUtils
import java.text.SimpleDateFormat
import java.util.*

class SmsAddActivity : BaseActivity() {
    private lateinit var infoBinding: ActivityCallAddBinding
    private lateinit var sms: SmsBean
    override fun initPage() {
        infoBinding = ActivityCallAddBinding.inflate(layoutInflater)
        setContentView(infoBinding.root)
        infoBinding.piTitle.tvTitle.text = "添加短信"
        getIntentData()
        infoBinding.piTitle.ivBack.setOnClickListener {
            onBackPressed()
        }
        infoBinding.confirm.setOnClickListener {
            editConfirm()
        }
    }

    private fun getIntentData() {
        val tmp = intent.getParcelableExtra<SmsBean>("sms")
        if (tmp != null) {
            sms = tmp
        }
        if (!TextUtils.isEmpty(sms.smsNum)) {
            infoBinding.etPhone.setText(sms.smsNum)
        }
        if (!TextUtils.isEmpty(sms.smsContent)) {
            infoBinding.etDescribe.setText(sms.smsContent)
        }
    }

    private fun editConfirm() {
        sms.smsNum = infoBinding.etPhone.text.toString()
        sms.smsContent = infoBinding.etDescribe.text.toString()
        if (TextUtils.isEmpty(sms.smsNum)) {
            ToastUtils.show("请输入短信号码")
            return
        }
        if (TextUtils.isEmpty(sms.smsContent)) {
            ToastUtils.show("请输入短信内容")
            return
        }
        sms.isInput = true
        sms.stringDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            .format(Date(System.currentTimeMillis()))
        val intent = Intent()
        setResult(RESULT_OK, intent)
        intent.putExtra("sms", sms)
        finish()
    }
}