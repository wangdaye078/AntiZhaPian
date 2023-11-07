package com.demo.antizha.ui.fragment.mine

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.demo.antizha.BuildConfig
import com.demo.antizha.R
import com.demo.antizha.UserInfoBean
import com.demo.antizha.ui.activity.*
import com.demo.antizha.util.LogUtils

class MineFragment : Fragment() {
    private lateinit var mActivity: Activity
    private lateinit var mineViewModel: MineViewModel
    private lateinit var root: View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mineViewModel =
            ViewModelProvider(this)[MineViewModel::class.java]
        root = inflater.inflate(R.layout.fragment_mine, container, false)
        val head: ConstraintLayout = root.findViewById(R.id.cl_head)
        head.setOnClickListener {
            val intentInfo = Intent(activity, MinePersonalActivity::class.java)
            startActivity(intentInfo)
        }
        val phone: TextView = root.findViewById(R.id.tv_phone)
        phone.setOnClickListener {
            val intentInfo = Intent(activity, MinePersonalActivity::class.java)
            startActivity(intentInfo)
        }
        val setting: RelativeLayout = root.findViewById(R.id.ll_setting)
        setting.setOnClickListener {
            val intentInfo = Intent(activity, SettingActivity::class.java)
            startActivity(intentInfo)
        }
        val about: RelativeLayout = root.findViewById(R.id.ll_ablout_app)
        about.setOnClickListener {
            val intentInfo = Intent(activity, AboutUsActivity::class.java)
            startActivity(intentInfo)
        }
        val userNote: RelativeLayout = root.findViewById(R.id.ll_user_note)
        userNote.setOnClickListener {
            val intentInfo = Intent(activity, WebActivity::class.java)
            intentInfo.putExtra(WebActivity.EXTRA_WEB_TITLE, "用户手册")
            intentInfo.putExtra(WebActivity.EXTRA_WEB_URL,
                BuildConfig.RELEASE_H5_URL + "/UserManual/?time=" + System.currentTimeMillis() / 3000)
            startActivity(intentInfo)
        }
        val feedback: RelativeLayout = root.findViewById(R.id.ll_feedback)
        feedback.setOnClickListener {
            val intentInfo = Intent(activity, FeedbackHelpActivity::class.java)
            startActivity(intentInfo)
        }
        return root
    }

    override fun onResume() {
        super.onResume()
        val phoneNumber: TextView = root.findViewById(R.id.tv_phone)
        if (!TextUtils.isEmpty(UserInfoBean.mobileNumber)) {
            val str = UserInfoBean.mobileNumber
            phoneNumber.text = "您好, ${str.substring(0, 3)}******${str.substring(str.length - 2)}"
        } else {
            phoneNumber.text = generatePhoneNumber()
        }
        val imei: TextView = root.findViewById(R.id.tv_imei)
        imei.text = UserInfoBean.imei
        val accountId: TextView = root.findViewById(R.id.tv_accountid)
        accountId.text = UserInfoBean.accountId
        val ver: LinearLayout = root.findViewById(R.id.ll_version)
        ver.visibility = if (TextUtils.isEmpty(UserInfoBean.name)) View.VISIBLE else View.GONE
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as Activity
    }

    private fun generatePhoneNumber(): String {   //手机号生成
        val head = getString(R.string.title_mine)
        val a = listOf(
            131,
            132,
            133,
            134,
            135,
            136,
            137,
            138,
            139,
            177,
            151,
            152,
            153,
            155,
            156
        ).random().toString()
        val b = "******"
        val c = (10..99).random().toString()
        return "$head  $a$b$c"
    }
}