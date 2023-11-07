package com.demo.antizha.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.text.TextUtils
import android.util.AttributeSet
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import com.demo.antizha.OnWebListener
import com.demo.antizha.UserInfoBean
import com.demo.antizha.ui.activity.BaseActivity
import com.demo.antizha.ui.activity.PromosWebDetActivity
import com.demo.antizha.util.UrlUtils
import com.google.gson.Gson


fun getFixedContext(context: Context): Context {
    return context.createConfigurationContext(Configuration())
}

class HiWebView : WebView {
    private var mActivity: Activity? = null
    private lateinit var mJsObject: Any
    private var mOnWebListener: OnWebListener? = null
    private var mSwipBackLayout: SwipBackLayout? = null

    constructor(context: Context) : super(getFixedContext(context)) {
        initWebView(this)
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(
        getFixedContext(context),
        attributeSet
    ) {
        initWebView(this)
    }

    constructor(
        context: Context,
        attributeSet: AttributeSet,
        defStyle: Int
    ) : super(getFixedContext(context), attributeSet, defStyle) {
        initWebView(this)
    }

    fun setListener(activity: Activity?, onWebListener: OnWebListener?) {
        mActivity = activity!!
        mOnWebListener = onWebListener!!
    }

    fun setSwipLayout(activity: Activity?, swipBackLayout: SwipBackLayout?) {
        mActivity = activity
        mSwipBackLayout = swipBackLayout
    }

    @SuppressLint("JavascriptInterface")
    private fun initWebView(webView: WebView) {
        isClickable = true
        val settings = webView.settings
        settings.javaScriptEnabled = true
        webView.addJavascriptInterface(JsObject(), "appjs")
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.allowFileAccess = false
        settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
        settings.setSupportZoom(false)
        settings.displayZoomControls = false
        settings.builtInZoomControls = false
        settings.useWideViewPort = true
        settings.setSupportMultipleWindows(true)
        settings.setGeolocationEnabled(true)
        settings.loadWithOverviewMode = true
        settings.domStorageEnabled = true
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.databaseEnabled = true
        settings.blockNetworkImage = false
        settings.mixedContentMode = 0
        settings.textZoom = 100
        settings.mediaPlaybackRequiresUserGesture = false

    }

    inner class JsObject {
        @JavascriptInterface
        fun getHCData(): String {
            return getH5Data()
        }

        @JavascriptInterface
        fun getPageParams(str: String?) {
            if (mActivity != null && TextUtils.equals("pageFinish=1", str) &&
                mOnWebListener != null) {
                mOnWebListener!!.webJsFinish()
            }
        }

        @JavascriptInterface
        fun getSwpieEvent(str: String?) {
            if (mActivity != null && mSwipBackLayout != null && mActivity is PromosWebDetActivity) {
                if (TextUtils.equals(str, "1")) {
                    mSwipBackLayout!!.setInterEvent(true)
                } else {
                    mSwipBackLayout!!.setInterEvent(false)
                }
            }
        }

        @JavascriptInterface
        fun sendWebMsg(str: String?) {
            if (mActivity != null && mOnWebListener != null &&
                str != null && !TextUtils.isEmpty(str)
            ) {
                mOnWebListener!!.shouldIntercept(UrlUtils.string2Param(str))
            }
        }
    }

    companion object {
        fun getH5Data(): String {
            val hashMap = HashMap<Any, Any>()
            hashMap["deviceid"] = UserInfoBean.imei
            hashMap["os-version"] = "0"
            hashMap["market"] = HiCore.app.getChannel()
            hashMap["app-version"] = UserInfoBean.version
            hashMap["app-version-code"] = UserInfoBean.innerVersion.toString()
            hashMap["haveLiuhai"] = BaseActivity.liuhaiHeight.toString()
            hashMap["userid"] = UserInfoBean.accountId
            hashMap["pcode"] = UserInfoBean.adcode
            hashMap["nodeId"] = UserInfoBean.adcode
            hashMap["nodeCode"] = UserInfoBean.adcode
            val regions = TextUtils.split(UserInfoBean.region, "\\.")
            if (regions.size == 3) {
                hashMap["pname"] = regions[2]
                hashMap["nodeName"] = regions[2]
                hashMap["nodeProvince"] = regions[0]
            }
            return Gson().toJson(hashMap)
        }
    }

}