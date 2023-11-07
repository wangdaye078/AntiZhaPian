package com.demo.antizha.util

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import com.demo.antizha.BuildConfig
import com.demo.antizha.R
import com.demo.antizha.ui.HiCore
import com.demo.antizha.ui.activity.WebActivity
import com.hjq.toast.ToastUtils
import java.io.File
import java.io.FileInputStream
import java.io.InputStream


object Utils {
    fun openfile(name: String): InputStream {
        val extPath = HiCore.context.getExternalFilesDir(null)?.path
        val file = File(extPath, name)
        if (file.canRead())
            return FileInputStream(file)
        val assetManager = HiCore.app.resources.assets
        return assetManager.open(name)
    }

    fun showAnimation(context: Context?, resId: Int, imageView: ImageView) {
        val loadAnimation: Animation = AnimationUtils.loadAnimation(context, resId)
        loadAnimation.interpolator = LinearInterpolator()
        imageView.startAnimation(loadAnimation)
    }

    fun copyToClipboard(str: String?) {
        (HiCore.app.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager)
            .setPrimaryClip(ClipData.newPlainText("data", str))
        ToastUtils.show("复制成功")
    }

    internal class ClauseClickableSpan(val activity: Activity) : ClickableSpan() {
        override fun onClick(view: View) {
            if (view is TextView) {
                view.highlightColor = 0
            }
            val str =
                BuildConfig.RELEASE_H5_URL + "/Agreements/clause.html?time=" + System.currentTimeMillis() / 3000
            activity.startActivity(Intent(activity,
                WebActivity::class.java).putExtra(WebActivity.EXTRA_WEB_TITLE, "服务协议")
                .putExtra(WebActivity.EXTRA_WEB_URL, str))
        }

        override fun updateDrawState(textPaint: TextPaint) {
            textPaint.color = textPaint.linkColor
            textPaint.isUnderlineText = false
        }
    }

    internal class PolicyClickableSpan(val activity: Activity) : ClickableSpan() {
        override fun onClick(view: View) {
            if (view is TextView) {
                view.highlightColor = 0
            }
            val str =
                BuildConfig.RELEASE_H5_URL + "/Agreements/policy.html?time=" + System.currentTimeMillis() / 3000
            activity.startActivity(Intent(activity,
                WebActivity::class.java).putExtra(WebActivity.EXTRA_WEB_TITLE, "隐私政策")
                .putExtra(WebActivity.EXTRA_WEB_URL, str))
        }

        override fun updateDrawState(textPaint: TextPaint) {
            textPaint.color = textPaint.linkColor
            textPaint.isUnderlineText = false
        }
    }

    fun createSpannableString(activity: Activity,
                              str: String,
                              str2: String,
                              str3: String,
                              str4: String,
                              str5: String): CharSequence {
        val app: HiCore = HiCore.app
        val spannableStringBuilder = SpannableStringBuilder()
        spannableStringBuilder.append((str + str4 + str2 + str5 + str3) as CharSequence)
        spannableStringBuilder.setSpan(ClauseClickableSpan(activity),
            str.length, str.length + str4.length, 33)
        spannableStringBuilder.setSpan(
            ForegroundColorSpan(app.resources.getColor(R.color.blue, null)),
            str.length, str.length + str4.length, 33)
        val bVar = PolicyClickableSpan(activity)
        val length = (str + str4 + str2).length
        spannableStringBuilder.setSpan(bVar, length, (str + str4 + str2 + str5).length, 33)
        val foregroundColorSpan = ForegroundColorSpan(app.resources.getColor(R.color.blue, null))
        val length2 = (str + str4 + str2).length
        spannableStringBuilder.setSpan(foregroundColorSpan,
            length2, (str + str4 + str2 + str5).length, 33)
        return spannableStringBuilder
    }

}