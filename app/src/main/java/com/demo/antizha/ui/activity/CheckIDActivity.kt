package com.demo.antizha.ui.activity

import android.app.Activity
import android.widget.Toast
import com.demo.antizha.databinding.ActivityIdCheckBinding
import com.demo.antizha.interfaces.IClickListener
import com.demo.antizha.ui.HiCore
import com.demo.antizha.util.DialogUtils
import qiu.niorgai.StatusBarCompat

class CheckIDActivity : BaseActivity(), IClickListener {
    private lateinit var infoBinding: ActivityIdCheckBinding
    override fun initPage() {
        infoBinding = ActivityIdCheckBinding.inflate(layoutInflater)
        setContentView(infoBinding.root)
        StatusBarCompat.translucentStatusBar(this as Activity, true, true)
        infoBinding.piTitle.tvTitle.text = "身份核实"
        infoBinding.piTitle.ivBack.setOnClickListener {
            finish()
        }
        infoBinding.btnIdvrfySend.setOnClickListener {
            val checkID: String = infoBinding.etCheckPhone.text.toString()
            if (checkID.trim { it <= ' ' }.length < 11) {
                val toast = Toast.makeText(HiCore.app, "请输入正确手机号~", Toast.LENGTH_SHORT)
                toast.setGravity(17, 0, 0)
                toast.show()
            } else {
                DialogUtils.showNormalDialog(mActivity,
                    "向该号码发送身份核实请求?",
                    checkID,
                    "取消",
                    "确认发送",
                    this)
            }
        }
    }

    override fun cancelBtn() {

    }

    override fun clickOKBtn() {
        val toast = Toast.makeText(HiCore.app, "发送成功", Toast.LENGTH_SHORT)
        toast.setGravity(17, 0, 0)
        toast.show()
    }
}