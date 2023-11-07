package com.demo.antizha.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.text.Html.FROM_HTML_MODE_LEGACY
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import cn.qqtheme.framework.entity.City
import cn.qqtheme.framework.entity.County
import cn.qqtheme.framework.entity.Province
import com.bumptech.glide.Glide
import com.demo.antizha.R
import com.demo.antizha.adapter.SmsBean
import com.demo.antizha.adapter.SocialAccBean
import com.demo.antizha.databinding.ActivityReportNewBinding
import com.demo.antizha.interfaces.IClickListener
import com.demo.antizha.ui.HiCore
import com.demo.antizha.util.AddressBean
import com.demo.antizha.util.AppUtil
import com.demo.antizha.util.DialogUtils


class ReportNewActivity : BaseActivity() {
    private lateinit var infoBinding: ActivityReportNewBinding
    private var provinces: ArrayList<Province> = ArrayList()
    private var duperyType: Int = 0
    private var apps: ArrayList<AppUtil.AppInfoBean> = ArrayList()
    private var urls: ArrayList<String> = ArrayList()
    private var calls: ArrayList<CallBean> = ArrayList()
    private var smss: ArrayList<SmsBean> = ArrayList()
    private var socialAccounts: ArrayList<SocialAccBean> = ArrayList()
    private var dealAccounts: ArrayList<SocialAccBean> = ArrayList()
    private var pics: ArrayList<String> = ArrayList()
    private lateinit var startDuperyType: ActivityResultLauncher<Intent>
    private lateinit var startCaseDescribe: ActivityResultLauncher<Intent>
    private lateinit var startApp: ActivityResultLauncher<Intent>
    private lateinit var startPic: ActivityResultLauncher<Intent>
    private lateinit var startCall: ActivityResultLauncher<Intent>
    private lateinit var startSms: ActivityResultLauncher<Intent>
    private lateinit var startUrl: ActivityResultLauncher<Intent>
    private lateinit var startContact: ActivityResultLauncher<Intent>
    private lateinit var startDeal: ActivityResultLauncher<Intent>

    override fun initPage() {
        DialogUtils.showOneTimeDialog(this,
            3,
            "110",
            getString(R.string.report_time_tips),
            "",
            "我知道了",
            null)
        infoBinding = ActivityReportNewBinding.inflate(layoutInflater)
        setContentView(infoBinding.root)
        infoBinding.piTitle.tvTitle.text = "我要举报"
        infoBinding.tvNumTip.text =
            Html.fromHtml("今日剩余可举报次数<font color=#2B4CFF>2</font>次", FROM_HTML_MODE_LEGACY)
        //直接调用好像会让界面卡一下，所以先压入事件循环，让界面先显示出来
        Handler(Looper.getMainLooper()).postDelayed({
            provinces = AddressBean.getProvince()
        }, 10)
        infoBinding.piTitle.ivBack.setOnClickListener {
            onBackPressed()
        }
        initActivityResultLauncher()
        infoBinding.tvDuperyType.setOnClickListener {
            val intent = Intent(this, TagFlowLayoutActivity::class.java)
            intent.putExtra("int_tag_name", duperyType)
            startDuperyType.launch(intent)
        }
        infoBinding.region.setOnClickListener {
            val picker = AddressBean.createAddressPicker(this, "", true, AddressListener())
            picker.show()
        }
        infoBinding.etCaseDescribe.setOnClickListener {
            val intent = Intent(mActivity, EvidenceDiscActivity::class.java)
            intent.putExtra("disc", infoBinding.etCaseDescribe.text.toString())
            intent.putExtra("title", "举报描述")
            startCaseDescribe.launch(intent)
        }
        infoBinding.lyCall.tvUploadCall.setOnClickListener {
            val intent = Intent(this, CallActivity::class.java)
            intent.putParcelableArrayListExtra("call", calls)
            startCall.launch(intent)
        }
        infoBinding.lySms.tvUploadSms.setOnClickListener {
            val intent = Intent(this, SmsActivity::class.java)
            intent.putParcelableArrayListExtra("sms", smss)
            startSms.launch(intent)
        }
        infoBinding.lyApp.tvUploadApp.setOnClickListener {
            val intent = Intent(this, AppActivity::class.java)
            intent.putParcelableArrayListExtra("apps", apps)
            startApp.launch(intent)
        }
        infoBinding.lyPicture.tvUploadPicture.setOnClickListener {
            val intent = Intent(this, PictureActivity::class.java)
            intent.putStringArrayListExtra("pics", pics)
            startPic.launch(intent)
        }
        infoBinding.lyAudio.tvUploadAudio.setOnClickListener {
            val intent = Intent(this, AudioActivity::class.java)
            startActivity(intent)
        }
        infoBinding.lyUrl.tvUploadUrl.setOnClickListener {
            val intent = Intent(this, WebsiteActivity::class.java)
            intent.putStringArrayListExtra("url", urls)
            startUrl.launch(intent)
        }
        infoBinding.lyContact.tvSocial.setOnClickListener {
            val intent = Intent(this, SocialAccountActivity::class.java)
            intent.putParcelableArrayListExtra("accounts", socialAccounts)
            startContact.launch(intent)
        }
        infoBinding.lyDeal.tvTrad.setOnClickListener {
            val intent = Intent(this, TradAccountActivity::class.java)
            intent.putParcelableArrayListExtra("accounts", dealAccounts)
            startDeal.launch(intent)
        }
    }

    override fun onBackPressed() {
        var showSaveWarn = false
        if (duperyType != 0)
            showSaveWarn = true
        if (!TextUtils.isEmpty(infoBinding.etCaseDescribe.text.toString()))
            showSaveWarn = true
        if (!TextUtils.isEmpty(infoBinding.region.text))
            showSaveWarn = true
        if (apps.size > 0)
            showSaveWarn = true
        if (urls.size > 0)
            showSaveWarn = true
        if (smss.size > 0)
            showSaveWarn = true
        if (calls.size > 0)
            showSaveWarn = true
        if (socialAccounts.size > 0)
            showSaveWarn = true
        if (dealAccounts.size > 0)
            showSaveWarn = true
        if (pics.size > 0)
            showSaveWarn = true
        if (showSaveWarn)
            DialogUtils.showBtTitleDialog(
                this,
                "将此次编辑保留",
                "",
                "不保留",
                "保留",
                R.color._353536,
                -1,
                true,
                SaveRecordListener()
            )
        else
            finish()
    }

    private fun initActivityResultLauncher() {
        startDuperyType =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode != Activity.RESULT_OK)
                    return@registerForActivityResult
                if (result.data == null || result.data!!.extras == null)
                    return@registerForActivityResult
                val tagString = result.data!!.extras!!.getString("tagString")
                val tagId = result.data!!.extras!!.getInt("tagId")
                if (tagId != 0) {
                    duperyType = tagId
                    infoBinding.tvDuperyType.text = tagString
                }
            }
        startCaseDescribe =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode != Activity.RESULT_OK)
                    return@registerForActivityResult
                if (result.data == null || result.data!!.extras == null)
                    return@registerForActivityResult
                val disc = result.data!!.extras!!.getString("disc")
                if (disc != null)
                    infoBinding.etCaseDescribe.text = disc
            }
        startCall =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode != Activity.RESULT_OK)
                    return@registerForActivityResult
                if (result.data == null)
                    return@registerForActivityResult
                val array: java.util.ArrayList<CallBean> =
                    result.data!!.getParcelableArrayListExtra("call")
                        ?: return@registerForActivityResult
                calls = array
                if (calls.size == 0)
                    infoBinding.lyCall.tvUploadCall.text = ""
                else
                    infoBinding.lyCall.tvUploadCall.text = "${calls.size}个"
            }
        startSms =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode != Activity.RESULT_OK)
                    return@registerForActivityResult
                if (result.data == null)
                    return@registerForActivityResult
                val array: java.util.ArrayList<SmsBean> =
                    result.data!!.getParcelableArrayListExtra("sms")
                        ?: return@registerForActivityResult
                smss = array
                if (smss.size == 0)
                    infoBinding.lySms.tvUploadSms.text = ""
                else
                    infoBinding.lySms.tvUploadSms.text = "${smss.size}个"
            }

        startApp =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode != Activity.RESULT_OK)
                    return@registerForActivityResult
                if (result.data == null)
                    return@registerForActivityResult
                val array = result.data!!.getParcelableArrayListExtra<AppUtil.AppInfoBean>("apps")
                    ?: return@registerForActivityResult
                apps = array
                if (apps.size == 0) {
                    infoBinding.lyApp.tvUploadApp.text = ""
                    infoBinding.lyApp.flAppBg.setBackgroundResource(R.drawable.white_corner)
                } else {
                    infoBinding.lyApp.tvUploadApp.text = "${apps.size}个"
                    infoBinding.lyApp.flAppBg.setBackgroundResource(R.drawable.red_corner)
                }
                infoBinding.lyApp.llApp.removeAllViews()
                for ((i, app) in apps.withIndex()) {
                    val inflate: View = LayoutInflater.from(this)
                        .inflate(R.layout.pic_item_view, infoBinding.lyApp.llApp, false)
                    val ivIcon = inflate.findViewById<ImageView>(R.id.imageview)
                    val tvMore = inflate.findViewById<TextView>(R.id.tvDot)
                    if (i > 2) {
                        tvMore.visibility = View.VISIBLE
                        ivIcon.visibility = View.GONE
                        infoBinding.lyApp.llApp.addView(inflate)
                        break
                    }
                    if (app.appIcon != null) {
                        ivIcon.setImageDrawable(app.appIcon)
                    }
                    tvMore.visibility = View.GONE
                    ivIcon.visibility = View.VISIBLE
                    infoBinding.lyApp.llApp.addView(inflate)
                }
            }
        startPic =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode != Activity.RESULT_OK)
                    return@registerForActivityResult
                if (result.data == null)
                    return@registerForActivityResult
                val array = result.data!!.getStringArrayListExtra("pics")
                    ?: return@registerForActivityResult
                pics = array
                if (pics.size == 0) {
                    infoBinding.lyPicture.tvUploadPicture.text = ""
                } else {
                    infoBinding.lyPicture.tvUploadPicture.text = "${pics.size}个"
                }
                infoBinding.lyPicture.llPic.removeAllViews()
                for ((i, pic) in pics.withIndex()) {
                    val inflate: View = LayoutInflater.from(this)
                        .inflate(R.layout.pic_item_view, infoBinding.lyPicture.llPic, false)
                    val ivIcon = inflate.findViewById<ImageView>(R.id.imageview)
                    val tvMore = inflate.findViewById<TextView>(R.id.tvDot)
                    if (i > 2) {
                        tvMore.visibility = View.VISIBLE
                        ivIcon.visibility = View.GONE
                        infoBinding.lyPicture.llPic.addView(inflate)
                        break
                    }
                    Glide.with(HiCore.context).load(pic).into(ivIcon)
                    tvMore.visibility = View.GONE
                    ivIcon.visibility = View.VISIBLE
                    infoBinding.lyPicture.llPic.addView(inflate)
                }
            }
        startUrl =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode != Activity.RESULT_OK)
                    return@registerForActivityResult
                if (result.data == null)
                    return@registerForActivityResult
                val array =
                    result.data!!.getStringArrayListExtra("url") ?: return@registerForActivityResult
                urls = array
                if (urls.size == 0)
                    infoBinding.lyUrl.tvUploadUrl.text = ""
                else
                    infoBinding.lyUrl.tvUploadUrl.text = "${urls.size}个"
            }
        startContact =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode != Activity.RESULT_OK)
                    return@registerForActivityResult
                if (result.data == null)
                    return@registerForActivityResult
                val array = result.data!!.getParcelableArrayListExtra<SocialAccBean>("accounts")
                    ?: return@registerForActivityResult
                socialAccounts = array
                if (socialAccounts.size == 0)
                    infoBinding.lyContact.tvSocial.text = ""
                else
                    infoBinding.lyContact.tvSocial.text = "${socialAccounts.size}个"
            }
        startDeal =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->

                if (result.resultCode != Activity.RESULT_OK)
                    return@registerForActivityResult
                if (result.data == null)
                    return@registerForActivityResult
                val array = result.data!!.getParcelableArrayListExtra<SocialAccBean>("accounts")
                    ?: return@registerForActivityResult
                dealAccounts = array
                if (dealAccounts.size == 0)
                    infoBinding.lyDeal.tvTrad.text = ""
                else
                    infoBinding.lyDeal.tvTrad.text = "${dealAccounts.size}个"
            }
    }

    inner class AddressListener : AddressBean.HiAddressListener() {
        override fun onAddressPicked(province: Province?, city: City?, county: County?) {
            if (province == null || city == null || county == null)
                infoBinding.region.text = ""
            else
                infoBinding.region.text = "${province.name}.${city.name}.${county.name}"
        }

        override fun onClear() {
            infoBinding.region.text = ""
        }
    }

    inner class SaveRecordListener : IClickListener {
        override fun cancelBtn() {
            finish()
        }

        override fun clickOKBtn() {
            //往上层提交
            finish()
        }
    }

    //诈骗类型窗口TagFlowLaoutActivity
    //https://fzapp.gjfzpt.cn/hicore/api/EvidenceType通用诈骗类型
    //https://fzapp.gjfzpt.cn/hicore/api/DK/getcasecategorys断卡诈骗类型?
    //https://fzapp.gjfzpt.cn/hicore/api/xc/getxccasecategorys
}