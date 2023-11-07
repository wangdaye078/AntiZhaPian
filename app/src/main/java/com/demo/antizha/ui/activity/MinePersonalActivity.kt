package com.demo.antizha.ui.activity
//个人信息页窗口
import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.view.View
import com.demo.antizha.R
import com.demo.antizha.UserInfoBean
import com.demo.antizha.databinding.ActivityMinePersonalBinding
import qiu.niorgai.StatusBarCompat

//AddressBean
class MinePersonalActivity : BaseActivity(), View.OnClickListener {
    private lateinit var infoBinding: ActivityMinePersonalBinding

    override fun initPage() {
        infoBinding = ActivityMinePersonalBinding.inflate(layoutInflater)
        setContentView(infoBinding.root)
        StatusBarCompat.translucentStatusBar(this as Activity, true, true)
        infoBinding.piTitle.tvTitle.text = "个人信息"
        infoBinding.tvIdfineVar.setOnClickListener(this)
        infoBinding.llIdfineVar.setOnClickListener(this)
        infoBinding.userName.setOnClickListener(this)
        infoBinding.userID.setOnClickListener(this)
        infoBinding.area.setOnClickListener(this)
        infoBinding.areaDetail.setOnClickListener(this)
        infoBinding.piTitle.ivBack.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        onSuccRequest()
    }

    private fun onSuccRequest() {
        infoBinding.tvProgress.text = "已完善${UserInfoBean.perfectProgress}%"
        infoBinding.pbProgress.progress = UserInfoBean.perfectProgress

        if (!TextUtils.isEmpty(UserInfoBean.name) && !TextUtils.isEmpty(UserInfoBean.id)) {
            infoBinding.tvIdfineVar.visibility = View.GONE
            infoBinding.llIdfineVar.visibility = View.VISIBLE
            infoBinding.userName.visibility = View.VISIBLE
        } else {
            infoBinding.tvIdfineVar.visibility = View.VISIBLE
            infoBinding.llIdfineVar.visibility = View.GONE
            infoBinding.userName.visibility = View.GONE
        }
        if (!TextUtils.isEmpty(UserInfoBean.name)) {
            infoBinding.userName.text = UserInfoBean.name
        } else {
            infoBinding.userName.text = ""
        }
        if (!TextUtils.isEmpty(UserInfoBean.id)) {
            infoBinding.userID.text = "${UserInfoBean.id[0]}****************${UserInfoBean.id[1]}"
        } else {
            infoBinding.userID.text = ""
        }
        infoBinding.area.text = UserInfoBean.region.replace(".", "")
        /* 2023 10 01 说地址不再隐藏中间部分
        var areaDetail = ""
        if (UserInfoBean.addr.length > 2)
            areaDetail = "${UserInfoBean.addr[0]}" + "*".repeat(UserInfoBean.addr.length - 2) +
                    "${UserInfoBean.addr[UserInfoBean.addr.length - 1]}"
        infoBinding.areaDetail.text = areaDetail
        */
        infoBinding.areaDetail.text = UserInfoBean.addr
        infoBinding.regArea.text = infoBinding.area.text

        //professionName行业信息已经不填了，但是这边还在判断是否填写，应该是程序员弄错了，所以导致一直提示再完善
        if (!TextUtils.isEmpty(UserInfoBean.name) && !TextUtils.isEmpty(UserInfoBean.id) &&
            !TextUtils.isEmpty(UserInfoBean.region) && !TextUtils.isEmpty(UserInfoBean.addr) &&
            !TextUtils.isEmpty(UserInfoBean.professionName)
        ) {
            infoBinding.llTopCointips.setVisibility(View.GONE);
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.tv_idfine_var, R.id.user_name, R.id.ll_idfine_var, R.id.user_ID -> {
                val intent = Intent(this, PersonalInfoAddActivity::class.java)
                intent.putExtra("from_page_type", PersonalInfoAddActivity.pageBase)
                startActivity(intent)
            }

            R.id.area -> {
                val intent = Intent(this, PersonalInfoAddActivity::class.java)
                intent.putExtra("from_page_type", PersonalInfoAddActivity.pageArea)
                startActivity(intent)
            }
            R.id.area_detail -> {
                val intent = Intent(this, PersonalInfoAddActivity::class.java)
                intent.putExtra("from_page_type", PersonalInfoAddActivity.pageAreaDetail)
                startActivity(intent)
            }
        }
    }
}