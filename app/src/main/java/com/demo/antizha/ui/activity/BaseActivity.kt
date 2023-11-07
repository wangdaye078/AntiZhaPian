package com.demo.antizha.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.demo.antizha.R
import com.demo.antizha.ui.HiCore
import com.demo.antizha.ui.ProgressDialogBar
import com.demo.antizha.util.NotchUtils
import com.demo.antizha.util.SystemUtils.adjustFontScale
import qiu.niorgai.StatusBarCompat


abstract class BaseActivity : AppCompatActivity() {
    companion object {
        var haveLiuhai = false
        var liuhaiHeight = 0
        val activityList: ArrayList<BaseActivity> = ArrayList()
    }

    var mActivity: Activity? = null
    var typ_ME: Typeface? = null
    private var mProgressDialogBar: ProgressDialogBar? = null

    abstract fun initPage()

    fun removeAllActivity(exception: String): Activity? {
        var exAct: Activity? = null
        for (act in activityList) {
            val actName = act.javaClass.name
            if (actName != exception) {
                if (!act.isFinishing)
                    act.finish()
            } else
                exAct = act
        }
        return exAct
    }

    fun findActivity(name: String): Activity? {
        for (act in activityList) {
            val actName = act.javaClass.name
            if (actName == name)
                return act
        }
        return null
    }

    open fun isDouble(): Boolean {
        return HiCore.app.isDouble()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //关闭权限后APP会重启，但直接跳到后面的界面，所以在这重新载入
        if (null != savedInstanceState) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        mActivity = this
        typ_ME = Typeface.createFromAsset(assets, "DIN-Medium.otf")
        adjustFontScale(this)
        supportActionBar?.hide()
        setStatusBar()
        initPage()
        haveLiuhai = NotchUtils.haveLiuhai(this)
        liuhaiHeight = NotchUtils.liuhaiHeight(this)
        activityList.add(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        activityList.remove(this)
    }

    private fun setBlackStatusBar() {
        StatusBarCompat.setStatusBarColor((this as Activity),
            resources.getColor(R.color.black, null))
    }

    private fun setWhiteStatusBar() {
        StatusBarCompat.setStatusBarColor((this as Activity),
            resources.getColor(R.color.white, null))
    }

    private fun setStatusBar() {
        setWhiteStatusBar()
        StatusBarCompat.translucentStatusBar(this, true, true)
    }

    open fun showProgressDialog(str: String?, cancelable: Boolean) {
        if (!isFinishing) {
            if (mProgressDialogBar == null) {
                mProgressDialogBar = ProgressDialogBar.create(this)
            }
            if (mProgressDialogBar!!.isShowing) {
                return
            }
            mProgressDialogBar!!.setProgress(str)
            mProgressDialogBar!!.setCanceledOnTouchOutside(false)
            mProgressDialogBar!!.setCancelable(cancelable)
            mProgressDialogBar!!.show()
        }
    }

    open fun hideProgressDialog() {
        if (mProgressDialogBar != null && mProgressDialogBar!!.isShowing) {
            mProgressDialogBar!!.dismiss()
        }
    }

}