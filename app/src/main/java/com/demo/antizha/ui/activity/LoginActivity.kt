package com.demo.antizha.ui.activity

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.EditText
import com.demo.antizha.R
import com.demo.antizha.UserInfoBean
import com.demo.antizha.databinding.ActivityLoginBinding
import com.demo.antizha.interfaces.IEditAfterListener
import com.demo.antizha.util.EditUtil.TextWatcher
import com.demo.antizha.util.Utils
import com.hjq.toast.ToastUtils


class LoginActivity : BaseActivity(), IEditAfterListener {
    private lateinit var infoBinding: ActivityLoginBinding
    private var pwdIsVisible = false
    override fun initPage() {
        infoBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(infoBinding.root)
        val number = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
        var phoneNum = UserInfoBean.mobileNumber.substring(0, 3)
        for (i in 0..5)
            phoneNum += number.random()
        phoneNum += UserInfoBean.mobileNumber.substring(UserInfoBean.mobileNumber.length - 2)
        infoBinding.account.setText(phoneNum)
        val regions = TextUtils.split(UserInfoBean.region, "\\.")
        infoBinding.tvRegion.text = regions[regions.size - 1]
        initListener()
        initClause(false)
    }

    private fun initListener() {
        infoBinding.pwdEye.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                chgPwdEye(view)
            }
        })
        infoBinding.confirm.setOnClickListener {
            if (!infoBinding.cbSelect.isChecked) {
                ToastUtils.show("请先勾选同意协议政策后再登录")
                return@setOnClickListener
            }
            showProgressDialog("登录中...", false)
            Handler(Looper.getMainLooper()).postDelayed({
                hideProgressDialog()
                val mainActivity = removeAllActivity(MainActivity::class.java.name)
                if (mainActivity != null)
                    (mainActivity as MainActivity).setCurrentPage(0)
            }, 500)

        }
        setTextChangedListener(infoBinding.account, this)
        setTextChangedListener(infoBinding.userPwd, this)
    }

    fun chgPwdEye(view: View?) {
        if (pwdIsVisible) {
            infoBinding.pwdEye.setImageResource(R.mipmap.eye_sel)
            infoBinding.userPwd.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            infoBinding.pwdEye.setImageResource(R.mipmap.eye_nor)
            infoBinding.userPwd.transformationMethod = PasswordTransformationMethod.getInstance()
        }
        pwdIsVisible = !pwdIsVisible
        if (!infoBinding.userPwd.isFocused) {
            infoBinding.userPwd.isFocusable = true
            infoBinding.userPwd.isFocusableInTouchMode = true
            infoBinding.userPwd.requestFocus()
            infoBinding.userPwd.requestFocusFromTouch()
        }
        infoBinding.userPwd.setSelection(infoBinding.userPwd.text.toString().length)
    }

    override fun editLength(length: Int) {
        buttonStatus()
    }

    private fun buttonStatus() {
        val acc: String = infoBinding.account.text.toString().trim()
        if (acc.isNotEmpty()) {
            infoBinding.ivClear.visibility = View.VISIBLE
        } else {
            infoBinding.ivClear.visibility = View.INVISIBLE
        }
        val pwd: String = infoBinding.userPwd.text.toString().trim()
        infoBinding.confirm.isEnabled =
            !(TextUtils.isEmpty(acc) || acc.length != 11 || TextUtils.isEmpty(pwd) || pwd.length < 6)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        removeAllActivity("")
    }

    private fun setTextChangedListener(editText: EditText,
                                       iEditAfterListener: IEditAfterListener?) {
        editText.addTextChangedListener(TextWatcher(iEditAfterListener!!))
    }

    private fun initClause(newUser: Boolean) {
        val a: CharSequence = Utils.createSpannableString(this,
            if (newUser) "新用户登录即完成注册，且表示您已同意\n" else "登录即同意",
            "和", "", "《服务协议》", "《隐私政策》")
        infoBinding.clause.movementMethod = LinkMovementMethod.getInstance()
        infoBinding.clause.text = a
    }
}