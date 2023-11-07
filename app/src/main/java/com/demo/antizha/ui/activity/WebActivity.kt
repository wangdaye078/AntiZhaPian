package com.demo.antizha.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Message
import android.text.TextUtils
import android.view.View
import android.webkit.*
import com.demo.antizha.R
import com.demo.antizha.databinding.ActivityWebBinding
import com.demo.antizha.interfaces.IClickListener
import com.demo.antizha.interfaces.IHandler
import com.demo.antizha.ui.HiCore
import com.demo.antizha.ui.HiWebView
import com.demo.antizha.ui.SwipBackLayout
import com.demo.antizha.util.DialogUtils
import com.demo.antizha.util.Parameters
import com.demo.antizha.util.UrlAES
import com.demo.antizha.util.UrlUtils.separateParam
import qiu.niorgai.StatusBarCompat
import java.net.URLDecoder
import java.util.concurrent.atomic.AtomicReference


class WebActivity : BaseActivity(), IHandler.HandleWebActListener {
    companion object {
        const val EXTRA_WEB_PERSONALIZE = "extra_web_personalize"
        const val EXTRA_WEB_PERSONALIZE_UNSEALING = "extra_web_personalize_unsealing"
        const val EXTRA_WEB_TITLE = "extra_web_title"
        const val EXTRA_WEB_URL = "extra_web_url"
    }

    private lateinit var infoBinding: ActivityWebBinding
    private lateinit var mTitle: String
    private lateinit var mOrginUrl: String
    private lateinit var swipBackLayout: SwipBackLayout
    private var caragyCode = 0
    private var mPersonalize: String? = null
    private var toPage = 0
    private var articId: String? = null
    private var shareFullScreen = false

    override fun initPage() {
        infoBinding = ActivityWebBinding.inflate(layoutInflater)
        setContentView(infoBinding.root)
        StatusBarCompat.translucentStatusBar(this, true, true)
        swipBackLayout = SwipBackLayout.create(mActivity)
        swipBackLayout.init()
        IHandler.setHandleMsgListener(this)
        mTitle = intent.getStringExtra(EXTRA_WEB_TITLE).toString()
        mOrginUrl = intent.getStringExtra(EXTRA_WEB_URL).toString()
        caragyCode = intent.getIntExtra("extra_web_theme_caragy", -9)
        mPersonalize = intent.getStringExtra(EXTRA_WEB_PERSONALIZE)
        infoBinding.piTitle.tvTitle.text = mTitle
        initWebView(infoBinding.webview)
        infoBinding.webview.loadUrl(mOrginUrl)
        infoBinding.llProgress.visibility = View.VISIBLE
        fromUnsealedUrl()
        infoBinding.piTitle.ivBack.setOnClickListener {
            if (!HiCore.app.isDouble())
                onBackPressed()
        }
    }

    // androidx.activity.ComponentActivity, android.app.Activity
    override fun onBackPressed() {
        if (infoBinding.webview.canGoBack()) {
            when (toPage) {
                3 -> {
                    DialogUtils.showInterlinkingDialog(mActivity,
                        "确定要退出答题?",
                        "退出后已作答题目将不会保存",
                        "退出",
                        "继续",
                        OnDialogClick())
                }
                2 -> {
                    infoBinding.webview.loadUrl(mOrginUrl)
                }
                else -> {
                    super.onBackPressed()
                }
            }
        } else {
            super.onBackPressed()
        }
    }

    private fun initWebView(myWebView: HiWebView) {
        myWebView.webChromeClient = WebWebChromeClient()
        myWebView.webViewClient = WebWebViewClient()
    }

    private fun fromUnsealedUrl() {
        if (TextUtils.equals(mPersonalize, EXTRA_WEB_PERSONALIZE_UNSEALING)) {
            StatusBarCompat.translucentStatusBar((this as Activity), true, false)
            infoBinding.piTitle.rlTitle.visibility = View.GONE
            infoBinding.flMask.visibility = View.GONE
        }
    }

    private fun initView() {
        shareFullScreen = false
        infoBinding.piTitle.rlTitle.setBackgroundResource(R.drawable.status_bar_bg)
        infoBinding.piTitle.tvTitle.text = mTitle
        infoBinding.piTitle.ivBack.visibility = View.VISIBLE
        infoBinding.piTitle.ivRight.setBackgroundResource(0)
    }

    private fun testPage(i: Int) {
        toPage = i
        StatusBarCompat.translucentStatusBar((this as Activity), true, false)
        infoBinding.piTitle.rlTitle.setBackgroundResource(R.drawable.transparent)
        infoBinding.piTitle.tvTitle.text = mTitle + "人群防诈骗指数测试"
        swipBackLayout.setInterEvent(true)
        infoBinding.flMask.visibility = View.GONE
        infoBinding.piTitle.ivBack.setImageResource(R.mipmap.iv_white_back)
        infoBinding.piTitle.tvTitle.setTextColor(Color.parseColor("#ffffff"))
        infoBinding.piTitle.ivRight.setBackgroundResource(R.drawable.iv_share_white)
    }

    fun sendWebMsg(param: Parameters) {
        if (!param.isEmpty) {
            try {
                val id: String = param.value("id")
                if (!TextUtils.isEmpty(id)) {
                    articId = id
                }
                val a: String = UrlAES.urlDecrypt(param.value("url"))
                if (!TextUtils.isEmpty(a)) {
                    val decode: String = URLDecoder.decode(param.value("title"), "UTF-8")
                    val intent = Intent(mActivity, PromosWebDetActivity::class.java)
                    intent.putExtra("extra_web_title", decode)
                    intent.putExtra("extra_web_url", a)
                    intent.putExtra("extra_web_id", articId)
                    startActivity(intent)
                }
                val isfullScreen: String = param.value("isfullScreen")
                val stylecolor: String = param.value("stylecolor")
                val toPage: String = param.value("toPage")
                when {
                    TextUtils.equals("yes", isfullScreen) -> {
                        IHandler.HandleListener.mHandler.sendEmptyMessage(2)
                    }
                    TextUtils.equals("no", isfullScreen) -> {
                        IHandler.HandleListener.mHandler.sendEmptyMessage(3)
                    }
                    TextUtils.equals("black", stylecolor) -> {
                        IHandler.HandleListener.mHandler.sendEmptyMessage(4)
                    }
                    TextUtils.equals("white", stylecolor) -> {
                        IHandler.HandleListener.mHandler.sendEmptyMessage(5)
                    }
                    TextUtils.equals("1", toPage) -> {
                        IHandler.HandleListener.mHandler.sendEmptyMessage(8)
                    }
                    TextUtils.equals("2", toPage) -> {
                        IHandler.HandleListener.mHandler.sendEmptyMessage(9)
                    }
                    TextUtils.equals("3", toPage) -> {
                        IHandler.HandleListener.mHandler.sendEmptyMessage(10)
                    }
                }
                if (TextUtils.equals(param.value("appBack"), "1")) {
                    IHandler.HandleListener.mHandler.sendEmptyMessage(11)
                }
            } catch (unused: java.lang.Exception) {
            }
        }
    }

    override fun handleMsg(message: Message?) {
        val activity = mActivity
        if (activity != null && !activity.isFinishing) {
            initView()
            when (message!!.what) {
                2 -> {
                    StatusBarCompat.translucentStatusBar(this, true, false)
                    infoBinding.piTitle.rlTitle.visibility = View.VISIBLE
                    infoBinding.flMask.visibility = View.GONE
                    infoBinding.piTitle.rlTitle.setBackgroundResource(R.drawable.transparent)
                    swipBackLayout.setInterEvent(true)
                    infoBinding.piTitle.tvTitle.text = ""
                    infoBinding.piTitle.ivBack.visibility = View.GONE
                    infoBinding.piTitle.ivRight.setBackgroundResource(R.drawable.iv_share_white)
                    shareFullScreen = true
                    return
                }
                3 -> {
                    StatusBarCompat.translucentStatusBar(this, true, true)
                    infoBinding.piTitle.rlTitle.visibility = View.VISIBLE
                    infoBinding.flMask.visibility = View.VISIBLE
                    swipBackLayout.setInterEvent(false)
                    return
                }
                4 -> {
                    StatusBarCompat.translucentStatusBar(this, true, true)
                    infoBinding.piTitle.rlTitle.visibility = View.GONE
                    infoBinding.flMask.visibility = View.GONE
                    swipBackLayout.setInterEvent(true)
                    return
                }
                5 -> {
                    StatusBarCompat.translucentStatusBar(this, true, false)
                    infoBinding.piTitle.rlTitle.visibility = View.GONE
                    infoBinding.flMask.visibility = View.GONE
                    swipBackLayout.setInterEvent(true)
                    return
                }
                6, 7 -> return
                8 -> {
                    toPage = 1
                    StatusBarCompat.translucentStatusBar(this, true, true)
                    infoBinding.piTitle.rlTitle.setBackgroundResource(R.drawable.status_bar_bg)
                    infoBinding.piTitle.tvTitle.text = mTitle
                    swipBackLayout.setInterEvent(false)
                    infoBinding.flMask.visibility = View.VISIBLE
                    infoBinding.piTitle.ivBack.setImageResource(R.drawable.back_bla_arrow)
                    infoBinding.piTitle.tvTitle.setTextColor(Color.parseColor("#1D1A33"))
                    infoBinding.piTitle.ivRight.setBackgroundResource(0)
                    return
                }
                9 -> {
                    testPage(2)
                    return
                }
                10 -> {
                    testPage(3)
                    return
                }
                11 -> {
                    onBackPressed()
                    return
                }
                else -> return
            }
        }

    }

    inner class OnDialogClick internal constructor() : IClickListener {
        override fun cancelBtn() {
            val webActivity: WebActivity = this@WebActivity
            infoBinding.webview.loadUrl(webActivity.mOrginUrl)
        }

        override fun clickOKBtn() {}
    }

    inner class WebWebChromeClient internal constructor() : WebChromeClient() {
        // android.webkit.WebChromeClient
        override fun onProgressChanged(webView: WebView?, i: Int) {
            if (i == 100) {
                infoBinding.progressBar.visibility = View.GONE
                infoBinding.llProgress.visibility = View.GONE
                return
            }
            infoBinding.progressBar.visibility = View.VISIBLE
            infoBinding.progressBar.progress = i
        }

        // android.webkit.WebChromeClient
        override fun onReceivedTitle(webView: WebView?, str: String?) {
            super.onReceivedTitle(webView, str)
            if (TextUtils.isEmpty(mTitle) && !TextUtils.isEmpty(str)) {
                infoBinding.piTitle.tvTitle.text = mTitle
            }
        }
    }

    inner class WebWebViewClient internal constructor() : WebViewClient() {

        override fun onReceivedError(webView: WebView,
                                     request: WebResourceRequest,
                                     error: WebResourceError) {
            super.onReceivedError(webView, request, error)
            if (request.isForMainFrame()) {
                infoBinding.piNetworkNo.llNetworkNo.visibility = View.VISIBLE
                infoBinding.webview.visibility = View.GONE
            }
        }

        // android.webkit.WebViewClient
        override fun shouldInterceptRequest(webView: WebView?,
                                            webResourceRequest: WebResourceRequest): WebResourceResponse? {
            val uri = webResourceRequest.url.toString()
            if (TextUtils.isEmpty(uri)) {
                return super.shouldInterceptRequest(webView, webResourceRequest)
            }
            sendWebMsg(separateParam(uri))
            return super.shouldInterceptRequest(webView, webResourceRequest)
        }

        override fun shouldOverrideUrlLoading(webView: WebView?,
                                              webResourceRequest: WebResourceRequest): Boolean {
            val atomicReference = AtomicReference<String>()
            atomicReference.set(webResourceRequest.url.path)
            return super.shouldOverrideUrlLoading(webView, webResourceRequest)
        }

        // android.webkit.WebViewClient
        override fun shouldOverrideUrlLoading(webView: WebView, str: String?): Boolean {
            return if (str == null) {
                false
            } else try {
                if (!str.startsWith("http:") && !str.startsWith("https:")) {
                    startActivity(Intent("android.intent.action.VIEW", Uri.parse(str)))
                    return true
                }
                webView.loadUrl(str)
                true
            } catch (unused: Exception) {
                false
            }
        }
    }

}