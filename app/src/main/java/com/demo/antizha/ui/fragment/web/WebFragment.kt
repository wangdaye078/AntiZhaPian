package com.demo.antizha.ui.fragment.web

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.demo.antizha.*
import com.demo.antizha.ui.HiCore
import com.demo.antizha.ui.activity.PromosWebDetActivity
import com.demo.antizha.util.LogUtils
import com.demo.antizha.util.Parameters
import com.demo.antizha.util.UrlAES
import com.just.agentweb.AbsAgentWebSettings
import com.just.agentweb.AgentWeb
import com.just.agentweb.WebChromeClient
import com.just.agentweb.WebViewClient
import java.net.URLDecoder


class WebFragment : Fragment() {
    private lateinit var mActivity: Activity
    private lateinit var dashboardViewModel: WebViewModel
    private lateinit var agentWeb: AgentWeb
    private lateinit var webView: WebView
    private lateinit var webViewFrag: WebViewFrag
    private lateinit var root: View
    private lateinit var mVirtualWeb: LinearLayout
    private lateinit var mllWebContainer: LinearLayout
    private lateinit var mIvRight: ImageView
    private lateinit var mLinearLayout: LinearLayout
    private lateinit var mLlNetworkNo: View
    private lateinit var mNetTips: TextView
    private lateinit var mProgressBar: ProgressBar
    private lateinit var mRlTitle: RelativeLayout
    private lateinit var mIvBack: ImageView
    private lateinit var mTvTitle: TextView
    private lateinit var mWebChromeClient: WebChromeClient
    private lateinit var mWebViewClient: WebViewClient
    private lateinit var mOnWebListener: OnWebListener
    private lateinit var mHandler: Handler
    private var isInitWeb: Boolean = false
    private var id: String = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        dashboardViewModel = ViewModelProvider(this)[WebViewModel::class.java]
        root = inflater.inflate(R.layout.fragment_web, container, false)
        mVirtualWeb = root.findViewById(R.id.ll_virtual_web)
        mllWebContainer = root.findViewById(R.id.ll_web_container)
        mIvRight = root.findViewById(R.id.iv_right)
        mLinearLayout = root.findViewById(R.id.web_container)
        mLlNetworkNo = root.findViewById(R.id.ll_network_no)
        mNetTips = root.findViewById(R.id.net_tips)
        mProgressBar = root.findViewById(R.id.pro_bar)
        mRlTitle = root.findViewById(R.id.rl_title)
        mIvBack = root.findViewById(R.id.iv_back)
        mTvTitle = root.findViewById(R.id.tv_title)

        mWebChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(webView: WebView?, progress: Int) {
                if (progress == 100) {
                    mProgressBar.visibility = View.GONE
                } else {
                    mProgressBar.visibility = View.VISIBLE
                    mProgressBar.progress = progress
                }
            }
        }
        mWebViewClient = object : WebViewClient() {
            override fun doUpdateVisitedHistory(webView: WebView, url: String?, isReload: Boolean) {
                super.doUpdateVisitedHistory(webView, url, isReload)
                webView.clearHistory()
            }

            override fun onPageStarted(webView: WebView?, str: String?, bitmap: Bitmap?) {
                super.onPageStarted(webView, str, bitmap)
                if (!networkConnected()) {
                    switchLoadingPage(true)
                }
            }

            override fun shouldOverrideUrlLoading(
                webView: WebView,
                request: WebResourceRequest
            ): Boolean {

                if (!networkConnected()) {
                    switchLoadingPage(true)
                    return false
                }
                return super.shouldOverrideUrlLoading(webView, request)
            }
        }
        mOnWebListener = object : OnWebListener {
            override fun shouldIntercept(aVar: Parameters?) {
                if (aVar != null)
                    analysisParam(aVar)
            }

            override fun webJsFinish() {
                mActivity.runOnUiThread { switchLoadingPage(false) }
            }
        }
        mHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(message: Message) {
                super.handleMessage(message)
                if (!mActivity.isFinishing) {
                    val what: Int = message.what
                    if (what == 0) {
                        mRlTitle.visibility = View.VISIBLE
                    } else if (what == 1) {
                        mRlTitle.visibility = View.GONE
                    }
                }
            }
        }
        return root
    }

    private fun networkConnected(): Boolean {
        val cm: ConnectivityManager = HiCore.getContext()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network: Network? = cm.activeNetwork
        if (network != null) {
            val nc: NetworkCapabilities? = cm.getNetworkCapabilities(network)
            if (nc != null) {
                if (nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {//WIFI
                    return true
                } else if (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {//移动数据
                    return true
                }
            }
        }
        return false
    }

    private fun initPage() {
        mIvRight.setBackgroundResource(R.drawable.iv_share_white)
        mIvBack.visibility = View.GONE
        mRlTitle.visibility = View.GONE
        mRlTitle.setBackgroundColor(0)
        webViewFrag = WebViewFrag()
        initWebViewFrag()
        viewWeb()
    }

    private fun viewWeb() {
        val url =
            BuildConfig.RELEASE_H5_URL + "/?userid=" + UserInfoBean.accountId + "&imei=" + UserInfoBean.imei + "&" + (System.currentTimeMillis() / 3000)
        agentWeb =
            AgentWeb.with(this).setAgentWebParent(mLinearLayout, LinearLayout.LayoutParams(-1, -1))
                .closeIndicator().setWebViewClient(mWebViewClient)
                .addJavascriptInterface("appjs", WebViewFrag.JsObject())
                .setAgentWebWebSettings(AbsAgentWebSettings.getInstance())
                .setMainFrameErrorView(R.layout.web_page_error, -1).createAgentWeb().ready().go(url)
        webView = agentWeb.webCreator.webView
        agentWeb.webCreator.webView.isHorizontalScrollBarEnabled = false
        webView.webChromeClient = mWebChromeClient
        webView.settings.textZoom = 100
    }

    private fun initWebViewFrag() {
        switchLoadingPage(true)
        webViewFrag.init(mActivity, mOnWebListener)
    }

    fun switchLoadingPage(isLoading: Boolean) {
        mLinearLayout.visibility = View.VISIBLE
        if (isLoading) {
            mNetTips.text = "正在努力加载中..."
            mLlNetworkNo.visibility = View.VISIBLE
        } else {
            mLlNetworkNo.visibility = View.GONE
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as Activity
    }

    override fun onResume() {
        super.onResume()
        if (TextUtils.isEmpty(UserInfoBean.accountId))
            return
        if (!isInitWeb) {
            mVirtualWeb.visibility = View.GONE
            mllWebContainer.visibility = View.VISIBLE
            mProgressBar.visibility = View.VISIBLE
            isInitWeb = true
            initPage()
        }
    }

    fun goBack(): Boolean {
        if (!webView.canGoBack()) {
            return false
        }
        webView.goBack()
        return true
    }

    fun analysisParam(param: Parameters) {
        if (!param.isEmpty) {
            try {
                val tid: String = param.value("id")
                if (!TextUtils.isEmpty(tid)) {
                    id = tid
                }
                val url: String = UrlAES.urlDecrypt(param.value("url"))
                if (!TextUtils.isEmpty(url)) {
                    val decode: String = URLDecoder.decode(param.value("title"), "UTF-8")
                    val intent = Intent(mActivity, PromosWebDetActivity::class.java)
                    intent.putExtra("extra_web_title", decode)
                    intent.putExtra("extra_web_url", url)
                    intent.putExtra("extra_web_id", id)
                    intent.putExtra("extra_web_enter", 2)
                    startActivity(intent)

                }
                val isOnlyFullScreen: String = param.value("isOnlyFullScreen")
                val isFullScreen: String = param.value("isfullScreen")
                val stylecolor: String = param.value("stylecolor")
                when {
                    TextUtils.equals("yes", isFullScreen) -> {
                        //EventBus.getDefault().postSticky(a(100, null))
                        mHandler.sendEmptyMessage(0)
                    }
                    TextUtils.equals("no", isFullScreen) -> {
                        //EventBus.getDefault().postSticky(a(101, null))
                        mHandler.sendEmptyMessage(1)
                    }
                    TextUtils.equals("yes", isOnlyFullScreen) -> {
                        //EventBus.getDefault().postSticky(a(100, null))
                        mHandler.sendEmptyMessage(1)
                    }
                    TextUtils.equals("no", isOnlyFullScreen) -> {
                        //EventBus.getDefault().postSticky(a(101, null))
                        mHandler.sendEmptyMessage(1)
                    }/*
                    TextUtils.equals("black", stylecolor) -> {
                        EventBus.getDefault().postSticky(a(102, null))
                    }
                    TextUtils.equals("white", stylecolor) -> {
                        EventBus.getDefault().postSticky(a(103, null))
                    }*/
                }
            } catch (unused: Exception) {
            }
        }
    }


}