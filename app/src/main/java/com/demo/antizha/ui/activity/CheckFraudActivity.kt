package com.demo.antizha.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.Parcel
import android.os.Parcelable
import android.text.Html
import android.text.TextUtils
import android.view.View
import android.widget.RadioButton
import com.demo.antizha.R
import com.demo.antizha.databinding.ActivityCheckFraudBinding
import com.demo.antizha.interfaces.IEditAfterListener
import com.demo.antizha.ui.HiCore
import com.demo.antizha.ui.dialog.HiShareDialog
import com.demo.antizha.ui.dialog.ShareConfigBean
import com.demo.antizha.util.DialogUtils
import com.demo.antizha.util.EditUtil
import com.hjq.toast.ToastUtils
import qiu.niorgai.StatusBarCompat

class CheckFraudBean : Parcelable {
    var bankName: String = ""
    var content: String = ""
    var text: String = ""
    var isCheat = 0
    var type = 0

    constructor()
    constructor(source: Parcel) {
        bankName = source.readString().toString()
        content = source.readString().toString()
        text = source.readString().toString()
        isCheat = source.readInt()
        type = source.readInt()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(bankName)
        dest.writeString(content)
        dest.writeString(text)
        dest.writeInt(isCheat)
        dest.writeInt(type)
    }

    companion object CREATOR : Parcelable.Creator<CheckFraudBean> {
        override fun createFromParcel(parcel: Parcel): CheckFraudBean {
            return CheckFraudBean(parcel)
        }

        override fun newArray(size: Int): Array<CheckFraudBean?> {
            return arrayOfNulls(size)
        }
    }

}

class CheckFraudActivity : BaseActivity() {
    companion object {
        var dayCheckCount: IntArray = intArrayOf(1, 1, 1)
        var weekCheckCount: IntArray = intArrayOf(3, 3, 3)
    }

    private lateinit var infoBinding: ActivityCheckFraudBinding
    private lateinit var onViewClickListener: OnClickListener
    private val shareBean = ShareConfigBean()
    private val checkFraudBean = CheckFraudBean()
    private var model = 0

    override fun initPage() {
        infoBinding = ActivityCheckFraudBinding.inflate(layoutInflater)
        setContentView(infoBinding.root)
        StatusBarCompat.translucentStatusBar(this, true, false)
        infoBinding.piTitle.tvTitle.text = "风险查询"
        infoBinding.piTitle.ivRight.setImageResource(R.drawable.iv_share_white)
        onViewClickListener = OnClickListener()
        initView()
        infoBinding.piTitle.ivBack.setOnClickListener {
            finish()
        }
        infoBinding.ivClear.setOnClickListener {
            infoBinding.etContent.setText("")
        }
        infoBinding.piTitle.ivRight.setOnClickListener {
            if (!TextUtils.isEmpty(shareBean.downloadUrl)) {
                val shareConfigBean2: ShareConfigBean = this.shareBean
                val str = shareConfigBean2.downloadUrl
                shareConfigBean2.downloadUrl =
                    str.replace("{0}", (System.currentTimeMillis() / 3000).toString())
            }
            val mShareDialog = HiShareDialog(this, shareBean, 2)
            mShareDialog.show()
        }
        infoBinding.confirm.setOnClickListener {
            val obj: String = infoBinding.etContent.text.toString()
            if (TextUtils.isEmpty(obj)) {
                ToastUtils.show("查询内容不能为空")
                return@setOnClickListener
            }
            checkFraudBean.content = obj

            showProgressDialog("查询中...", true)
            Handler(Looper.getMainLooper()).postDelayed({
                hideProgressDialog()
                if (dayCheckCount[model] == 0) {
                    DialogUtils.showOneClickDialog(this,
                        "单日核验上限为1次，请明天再试",
                        "",
                        "确定",
                        null)
                } else {
                    dayCheckCount[model] -= 1
                    weekCheckCount[model] -= 1
                    refCountTip(model)
                    val intent = Intent(this, CheckFraudResultActivity::class.java)
                    intent.putExtra("checkBean", checkFraudBean)
                    intent.putExtra("shareBean", shareBean)
                    intent.putExtra("checkType", model)
                    startActivity(intent)
                }
            }, 1000)
        }
    }

    private fun initView() {
        infoBinding.rbPay.typeface = typ_ME
        infoBinding.rbUrl.typeface = typ_ME
        infoBinding.rbChat.typeface = typ_ME
        infoBinding.etContent.typeface = typ_ME
        infoBinding.ivClear.visibility = View.GONE
        infoBinding.etContent.addTextChangedListener(EditUtil.TextWatcher(EditAfterListener()))
        infoBinding.rbPay.tag = 0
        infoBinding.rbUrl.tag = 1
        infoBinding.rbChat.tag = 2
        infoBinding.llScan.tag = 1
        infoBinding.rbPay.setOnClickListener(onViewClickListener)
        infoBinding.rbUrl.setOnClickListener(onViewClickListener)
        infoBinding.rbChat.setOnClickListener(onViewClickListener)
        infoBinding.llScan.setOnClickListener(onViewClickListener)
        onViewClickListener.onClick(infoBinding.rbPay)
    }

    fun refCountTip(model: Int) {
        val str = "今日剩余可查询次数" + getColorStr(dayCheckCount[model]) +
                "次，本周剩余可查询次数" + getColorStr(weekCheckCount[model]) + "次"
        infoBinding.tvCountTip.text = Html.fromHtml(str, Html.FROM_HTML_MODE_LEGACY)
    }

    private fun changeState(model: Int) {
        this.model = model
        refCountTip(model)
        infoBinding.etContent.setText("")
        when (model) {
            1 -> {
                radioState(infoBinding.rbUrl, infoBinding.rbPay,
                    infoBinding.rbChat, R.mipmap.ic_fraud_radio_center)
                infoBinding.etContent.hint = "请输入或粘贴要查询的IP或URL网址"
                infoBinding.llScan.visibility = View.VISIBLE
            }
            2 -> {
                radioState(infoBinding.rbChat, infoBinding.rbPay,
                    infoBinding.rbUrl, R.mipmap.ic_fraud_radio_right)
                infoBinding.etContent.hint = "请输入或粘贴要查询的QQ或微信账号"
                infoBinding.llScan.visibility = View.GONE
            }
            else -> {
                radioState(infoBinding.rbPay, infoBinding.rbUrl,
                    infoBinding.rbChat, R.mipmap.ic_fraud_radio_left)
                infoBinding.etContent.hint = "请输入或粘贴要查询的银行卡号或支付账户"
                infoBinding.llScan.visibility = View.GONE
            }
        }
    }

    private fun getColorStr(i: Int): String {
        val sb = StringBuilder()
        sb.append("<font color=")
        sb.append(if (i > 0) "#1A57F3" else "#FF0000")
        sb.append(">")
        sb.append(i)
        sb.append("</font>")
        return sb.toString()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun radioState(rb1: RadioButton,
                           rb2: RadioButton,
                           rb3: RadioButton,
                           resID: Int) {
        rb1.background = resources.getDrawable(resID, null)
        rb1.setTextColor(resources.getColor(R.color.black_dark, null))
        rb2.background = null
        rb2.setTextColor(resources.getColor(R.color.colorWhite, null))
        rb3.background = null
        rb3.setTextColor(resources.getColor(R.color.colorWhite, null))
    }

    inner class EditAfterListener internal constructor() : IEditAfterListener {
        override fun editLength(length: Int) {
            if (length > 0) {
                infoBinding.ivClear.visibility = View.VISIBLE
            } else {
                infoBinding.ivClear.visibility = View.GONE
            }
        }
    }

    inner class OnClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            if (HiCore.app.isDouble())
                return
            when (v?.id) {
                R.id.ll_scan, R.id.rb_pay, R.id.rb_url, R.id.rb_chat -> {
                    val tag = v.tag!! as Int
                    changeState(tag)
                }
            }
        }
    }
}