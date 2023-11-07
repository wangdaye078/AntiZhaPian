package com.demo.antizha.ui.activity

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.demo.antizha.R
import com.demo.antizha.databinding.ActivityWarnPrimessBinding
import com.demo.antizha.util.SpUtils
import qiu.niorgai.StatusBarCompat


class WarnPrimessActivity : BaseActivity() {
    private lateinit var infoBinding: ActivityWarnPrimessBinding
    private var mOpenCount = ""
    private var mOldShowCount = -1

    override fun initPage() {
        infoBinding = ActivityWarnPrimessBinding.inflate(layoutInflater)
        setContentView(infoBinding.root)
        StatusBarCompat.translucentStatusBar(this as Activity, true, false)

        infoBinding.piTitle.rlTitle.setBackgroundColor(resources.getColor(R.color.blue, null))
        infoBinding.ivIc.visibility = View.GONE
        infoBinding.openFlow.tag = "0"
        infoBinding.openPhone.tag = "1"
        infoBinding.openPhoneRecord.tag = "2"
        infoBinding.openSms.tag = "3"
        infoBinding.piTitle.ivBack.setOnClickListener {
            finish()
        }
        infoBinding.confirm.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        mOpenCount = ""
        mOldShowCount = -1
        Handler(Looper.getMainLooper()).postDelayed({
            infoBinding.tvLabel.text = "预警守护中"
            infoBinding.tvLabelSmall.text = "准确识别电信网络诈骗"
            infoBinding.ivIc.visibility = View.VISIBLE
            infoBinding.confirm.isEnabled = true
            if (!mOpenCount.contains("0")) {
                SpUtils.setValue(SpUtils.warnCall, true)
                SpUtils.setValue(SpUtils.warnSms, true)
                openState(infoBinding.openFlow, isOpen = true, addOpenCount = true)
            }

        }, 500)
        openState(infoBinding.openFlow,
            SpUtils.getValue(SpUtils.warnCall, true) && SpUtils.getValue(SpUtils.warnSms, true),
            true)
        openState(infoBinding.openPhone, isOpen = true, addOpenCount = true)
        openState(infoBinding.openPhoneRecord, true, addOpenCount = true)
        openState(infoBinding.openSms, isOpen = true, addOpenCount = true)
        openState(infoBinding.openAuto, SpUtils.getValue(SpUtils.primissAuto, false), false)
        openState(infoBinding.openPower, SpUtils.getValue(SpUtils.primissPower, false), false)
        openState(infoBinding.openLock, SpUtils.getValue(SpUtils.primissLock, false), false)
        infoBinding.openAuto.setOnClickListener(OnClickListener(SpUtils.primissAuto))
        infoBinding.openPower.setOnClickListener(OnClickListener(SpUtils.primissPower))
        infoBinding.openLock.setOnClickListener(OnClickListener(SpUtils.primissLock))
    }

    private fun openState(textView: TextView?, isOpen: Boolean, addOpenCount: Boolean) {
        val openTag = try {
            textView!!.tag as String
        } catch (e2: Exception) {
            e2.printStackTrace()
            ""
        }

        if (textView != null) {
            if (isOpen) {
                textView.isEnabled = false
                textView.text = "已开启"
                textView.setTextColor(resources.getColor(R.color.colorGray, null))
                textView.setBackgroundResource(R.drawable.shape_gray_3)
                if (addOpenCount && !TextUtils.isEmpty(openTag) && !mOpenCount.contains(openTag)) {
                    mOpenCount += openTag
                }
                return
            }
            textView.isEnabled = true
            textView.text = "去开启"
            textView.setTextColor(resources.getColor(R.color.blue, null))
            textView.setBackgroundResource(R.drawable.shape_blue_3)
        }
    }

    inner class OnClickListener(private val key: String) : View.OnClickListener {
        override fun onClick(v: View?) {
            val isOpen = SpUtils.getValue(key, false)
            SpUtils.setValue(key, !isOpen)
            openState(v as TextView, !isOpen, false)
        }
    }
}