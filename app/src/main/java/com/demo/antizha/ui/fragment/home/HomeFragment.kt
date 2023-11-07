package com.demo.antizha.ui.fragment.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.demo.antizha.BuildConfig
import com.demo.antizha.R
import com.demo.antizha.UserInfoBean
import com.demo.antizha.adapter.HomeNewCaseAdapter
import com.demo.antizha.adapter.NewCaseBean
import com.demo.antizha.adapter.NewCaseBeanPackage
import com.demo.antizha.interfaces.IApiResult
import com.demo.antizha.interfaces.IClickListener
import com.demo.antizha.md.JniHandStamp
import com.demo.antizha.newwork.DictionaryUtils
import com.demo.antizha.newwork.UnsafeOkHttpClient
import com.demo.antizha.newwork.saveBuff2File
import com.demo.antizha.ui.HiCore
import com.demo.antizha.ui.RefreshUIEvent
import com.demo.antizha.ui.activity.*
import com.demo.antizha.util.AppUtil
import com.demo.antizha.util.DialogUtils
import com.demo.antizha.util.LogUtils
import com.demo.antizha.util.Utils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import com.youth.banner.Banner
import com.youth.banner.adapter.BannerAdapter
import com.youth.banner.indicator.RoundLinesIndicator
import com.youth.banner.listener.OnBannerListener
import com.youth.banner.util.BannerUtils
import org.greenrobot.eventbus.EventBus
import java.io.InputStreamReader


@GlideModule
class GlideApp : AppGlideModule() {
    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
}

class ToolBean(val id: Int, val name: String, val imageId: Int)

class ToolViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var name: TextView = view.findViewById(R.id.tv_name) as TextView
    var image: ImageView = view.findViewById(R.id.iv_icon) as ImageView
}

class ToolHolderAdapter(private var homeFragment: HomeFragment,
                        private var context: Context,
                        private var list: ArrayList<ToolBean>) :
    RecyclerView.Adapter<ToolViewHolder>() {

    override fun onBindViewHolder(holder: ToolViewHolder, i: Int) {
        holder.name.text = list[i].name
        holder.image.setImageResource(list[i].imageId)
        holder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (!HiCore.app.isDouble()) {
                    if (list.size <= 0)
                        return
                    val adapterPosition = holder.absoluteAdapterPosition
                    if (adapterPosition < 0)
                        return
                    val toolBean = list[adapterPosition]
                    when (toolBean.id) {
                        0 -> {
                            if (!UserInfoBean.isVerified())
                                DialogUtils.showNormalDialog(context,
                                    "请先进行实名认证",
                                    "",
                                    "取消",
                                    "身份验证",
                                    homeFragment as IClickListener)
                            else
                                homeFragment.startActivity(Intent(homeFragment.activity,
                                    ReportNewActivity::class.java))
                        }
                        1 -> {
                            if (!UserInfoBean.isVerified())
                                DialogUtils.showNormalDialog(context,
                                    "请先进行实名认证",
                                    "",
                                    "取消",
                                    "身份验证",
                                    homeFragment as IClickListener)
                            else
                                homeFragment.startActivity(Intent(homeFragment.activity,
                                    ReporterAidActivity::class.java))
                        }
                        2 -> {
                            val intentInfo =
                                Intent(homeFragment.activity, WarnSettingActivity::class.java)
                            homeFragment.startActivity(intentInfo)
                        }
                        3 -> {
                            if (!UserInfoBean.isVerified())
                                DialogUtils.showNormalDialog(context,
                                    "请先进行实名认证",
                                    "",
                                    "取消",
                                    "身份验证",
                                    homeFragment as IClickListener)
                            else
                                homeFragment.startActivity(Intent(homeFragment.activity,
                                    CheckIDActivity::class.java))
                        }
                    }
                }
            }
        })

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ToolViewHolder {
        return ToolViewHolder(
            LayoutInflater.from(context).inflate(R.layout.tool_item, viewGroup, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

enum class BanderType {
    TYPE_RES,
    TYPE_URL
}

class BanderBean(
    val imageRes: Int,
    val imagePath: String,
    val url: String,
    val title: String,
    val imageType: BanderType
)

class BanderHolder(view: View) : RecyclerView.ViewHolder(view) {
    var imageView: ImageView = view as ImageView

}

class BanderAdapter(
    imageUrls: ArrayList<BanderBean>
) : BannerAdapter<BanderBean, BanderHolder>(imageUrls) {
    override fun onCreateHolder(parent: ViewGroup?, viewType: Int): BanderHolder {
        val imageView = ImageView(parent!!.context)
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        imageView.layoutParams = params
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        //通过裁剪实现圆角
        BannerUtils.setBannerRound(imageView, 20f)
        return BanderHolder(imageView)
    }

    override fun onBindView(holder: BanderHolder, data: BanderBean, position: Int, size: Int) {
        when (data.imageType) {
            BanderType.TYPE_RES -> holder.imageView.setImageResource(data.imageRes)
            BanderType.TYPE_URL -> Glide.with(holder.itemView).load(data.imagePath)
                .into(holder.imageView)
        }
    }

}

class HomeFragment : Fragment(), IClickListener {
    private lateinit var mActivity: Activity
    private lateinit var typeME: Typeface
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var newCaseAdapter: HomeNewCaseAdapter

    //fragment_home.xml里面包含的
    private lateinit var root: View
    private lateinit var mFlNoteView: View
    private lateinit var mRefreshLayout: SmartRefreshLayout
    private lateinit var mRecyclerview: RecyclerView
    private lateinit var mHomeTopName: TextView
    private lateinit var mTvNoteNum: TextView

    private lateinit var headerView: View
    private lateinit var mBanner: Banner<BanderBean, BanderAdapter>
    private lateinit var mToolRecycle: RecyclerView
    private lateinit var mTvCheckFrad: TextView
    private lateinit var mVirusCheck: View
    private lateinit var mFruadCheck: View
    private lateinit var mLlHead: View
    private lateinit var mLlFoot: View
    private lateinit var mTvNewCase: TextView
    private lateinit var mTvMoreCase: View
    private lateinit var banderAdapter: BanderAdapter

    private var tools: ArrayList<ToolBean> = ArrayList()
    private val mNewCaseList: MutableList<NewCaseBean> = ArrayList()
    private var isLoadmore = false
    private var pageIndex = 1
    private var pageSize = 10

    class OnGetLatestCase : IApiResult {
        private var saveFile: String
        private var homeFragment: HomeFragment

        constructor(saveFile: String, homeFragment: HomeFragment) {
            this.saveFile = saveFile
            this.homeFragment = homeFragment
        }

        override fun onError() {
            LogUtils.debug("OnGetLatestCase Error", "")
            homeFragment.getNewCaseApiV2(homeFragment.pageIndex)
        }

        override fun onSuccess(data: String) {
            if (TextUtils.isEmpty(data)) {
                onError()
                return
            }
            LogUtils.debug("OnGetLatestCase Success", data)
            homeFragment.addLatestCase(data, saveFile)
        }
    }

    class OnGetLatestCaseV2 : IApiResult {
        private var saveFile: String
        private var homeFragment: HomeFragment

        constructor(saveFile: String, homeFragment: HomeFragment) {
            this.saveFile = saveFile
            this.homeFragment = homeFragment
        }

        override fun onError() {
            LogUtils.debug("OnGetLatestCaseV2 Erroe", "")

        }

        override fun onSuccess(data: String) {
            LogUtils.debug("OnGetLatestCaseV2 Success", data)
            homeFragment.addLatestCaseV2(data, saveFile)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ViewModelProvider(this)[HomeViewModel::class.java].also { homeViewModel = it }
        typeME = Typeface.createFromAsset(mActivity.assets, "DIN-Medium.otf")
        root = inflater.inflate(R.layout.fragment_home, container, false)
        mFlNoteView = root.findViewById(R.id.fl_note_view)
        mRefreshLayout = root.findViewById(R.id.sm_refresh)
        mRecyclerview = root.findViewById(R.id.recyclerview)
        mHomeTopName = root.findViewById(R.id.home_top_name)
        mTvNoteNum = root.findViewById(R.id.tv_num_tip)
        initViewNewCaseList()
        initBanner()
        initRecycleTool(mToolRecycle)
        initListener()
        return root
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as Activity
    }

    private fun initViewNewCaseList() {
        mRecyclerview.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        headerView = LayoutInflater.from(mActivity)
            .inflate(R.layout.home_case_head, mRecyclerview as ViewGroup, false)
        mBanner = headerView.findViewById(R.id.banner) as Banner<BanderBean, BanderAdapter>
        mToolRecycle = headerView.findViewById(R.id.rcy_tool)
        mTvCheckFrad = headerView.findViewById(R.id.tv_check_frad)
        mVirusCheck = headerView.findViewById(R.id.fl_virus_check)
        mFruadCheck = headerView.findViewById(R.id.fl_fruad_check)
        mLlHead = headerView.findViewById(R.id.ll_head)
        mTvNewCase = headerView.findViewById(R.id.tv_new_case)
        mHomeTopName.typeface = typeME
        mTvCheckFrad.typeface = typeME
        mTvNewCase.typeface = typeME
        val inflate: View = LayoutInflater.from(mActivity)
            .inflate(R.layout.item_more_case, mRecyclerview as ViewGroup, false)
        mLlFoot = inflate.findViewById(R.id.ll_foot)
        mTvMoreCase = inflate.findViewById(R.id.tv_more_case)
        mTvMoreCase.setOnClickListener(View.OnClickListener {
            EventBus.getDefault().postSticky(RefreshUIEvent(RefreshUIEvent.SELECT_WEB_FRAGMENT))
        })

        newCaseAdapter = HomeNewCaseAdapter(mActivity, mNewCaseList)
        newCaseAdapter.setHeaderView(headerView)
        newCaseAdapter.setFooterView(inflate)
        mRecyclerview.setAdapter(newCaseAdapter)

        getNewCaseApi(pageIndex)
        mFlNoteView.setOnClickListener {
            startActivity(Intent(activity, NoteListActivity::class.java))
        }
        mVirusCheck.setOnClickListener(View.OnClickListener {
            if (AppUtil.checkPermission(mActivity, true))
                startActivity(Intent(activity, VirusKillingActivity::class.java))
        })
        mFruadCheck.setOnClickListener(View.OnClickListener {
            startActivity(Intent(activity, CheckFraudActivity::class.java))
        })
    }

    private fun initBanner() {
        val imageList = ArrayList<BanderBean>()
        imageList.add(BanderBean(Integer.valueOf(R.mipmap.banner1),
            "", "", "", BanderType.TYPE_RES))
        mBanner.addBannerLifecycleObserver(this)
        mBanner.setBannerRound(20f)
        mBanner.setLoopTime(5000)
        mBanner.indicator = RoundLinesIndicator(HiCore.getContext())
        banderAdapter = BanderAdapter(imageList)
        mBanner.setAdapter(banderAdapter)
        val mOnWebListener = object : OnBannerListener<BanderBean> {
            override fun OnBannerClick(data: BanderBean, position: Int) {
                if (TextUtils.isEmpty(data.url))
                    return
                val intent = Intent(context, PromosWebDetActivity::class.java)
                intent.putExtra("extra_web_title", data.title)
                val adcode =
                    if (TextUtils.isEmpty(UserInfoBean.adcode)) "110105" else UserInfoBean.adcode
                intent.putExtra("extra_web_url", data.url + "&nodeId=" + adcode)
                startActivity(intent)
            }
        }
        mBanner.setOnBannerListener(mOnWebListener)
        loadBander()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun initRecycleTool(recyclerView: RecyclerView) {
        initToolBean()
        recyclerView.layoutManager = GridLayoutManager(mActivity, 4)
        val toolAdapter = ToolHolderAdapter(this, root.context, tools)
        recyclerView.adapter = toolAdapter
        toolAdapter.notifyDataSetChanged()
    }

    private fun initToolBean() {
        val toolText = arrayOf("我要举报", "举报助手", "来电预警", "身份核实")
        val toolImage = arrayOf(
            R.drawable.iv_home_report,
            R.drawable.iv_home_case,
            R.drawable.iv_home_warn,
            R.drawable.iv_home_id_check
        )
        tools.clear()
        for ((i, text) in toolText.withIndex()) {
            val toolBean = ToolBean(i, text, toolImage[i])
            tools.add(toolBean)
        }
    }

    private fun initListener() {
        mRefreshLayout.setRefreshHeader(ClassicsHeader(root.context))
        mRefreshLayout.setEnableLoadMore(false)
        mRefreshLayout.setEnableRefresh(true)
        mRefreshLayout.setOnRefreshListener { //下拉刷新
            pullToRefresh()
        }
        mRefreshLayout.setOnLoadMoreListener { //上拉加载更多
            pullToLoadMore()
        }
    }

    override fun cancelBtn() {

    }

    override fun clickOKBtn() {
        val intent = Intent(activity, PersonalInfoAddActivity::class.java)
        intent.putExtra("from_page_type", PersonalInfoAddActivity.pageBase)
        startActivity(intent)
    }

    private fun pullToRefresh() {
        loadBander()
        pageIndex = 1
        getNewCaseApi(pageIndex)
    }

    private fun pullToLoadMore() {
        isLoadmore = true
        val size: Int = newCaseAdapter.itemCount
        val nextPage = size / pageSize + 1
        if (pageIndex < nextPage) {
            pageIndex = nextPage
            getNewCaseApi(pageIndex)
            return
        }
        mRefreshLayout.finishLoadMore()
    }

    private fun getNewCaseApi(page: Int) {
        if (DictionaryUtils.step < 2) {
            Handler(Looper.getMainLooper()).postDelayed({
                getNewCaseApi(page)
            }, 500)
            return
        }
        //https://fzapp.gjfzpt.cn/hicore/api/Information/querylatestcases?Page=1&Rows=2&Sort=releasetime&Order=desc
        if (page > 2) {
            onNewCaseRequest(ArrayList())
            return
        }
        UnsafeOkHttpClient.getDataByGet(
            BuildConfig.RELEASE_OSS_DOWNLOAD + "h5/news/index/index-${page}.json",
            addHead = true, OnGetLatestCase("newcase${page}.txt", this))
    }

    private fun getNewCaseApiV2(page: Int) {
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["Sort"] = "releasetime"
        hashMap["Rows"] = "$pageSize"
        hashMap["Order"] = "desc"
        hashMap["Page"] = "$page"
        UnsafeOkHttpClient.getDataByPost(
            BuildConfig.RELEASE_API_URL + "/api/Information/querylatestcasesv2",
            bodyMap = JniHandStamp.princiHttp(hashMap),
            addHead = true,
            OnGetLatestCaseV2("latestcase${page}.txt", this)
        )
    }

    private fun showMoreBtn(noMore: Boolean) {
        if (noMore) {
            mRefreshLayout.setEnableLoadMore(false)
            mTvMoreCase.visibility = View.VISIBLE
            return
        }
        mTvMoreCase.visibility = View.GONE
    }

    private fun onNewCaseRequest(cases: ArrayList<NewCaseBean>) {
        LogUtils.debug("onNewCaseRequest count ", cases.size.toString())
        mRefreshLayout.finishRefresh()
        mRefreshLayout.finishLoadMore()
        var noMore = true
        if (cases.size == 0) {
            if (isLoadmore) {
                mRefreshLayout.setEnableLoadMore(false)
            }
            noMore = false
        } else {
            val oldCount = newCaseAdapter.itemCount
            newCaseAdapter.data.addAll(cases)
            newCaseAdapter.notifyItemRangeInserted(oldCount, cases.size)
            isLoadmore = true
            if (cases.size != pageSize) {
                mRefreshLayout.setEnableLoadMore(false)
            } else {
                mRefreshLayout.setEnableLoadMore(true)
                noMore = false
            }
        }
        isLoadmore = false
        showMoreBtn(noMore)
    }

    fun addLatestCaseV2(data: String, saveFile: String) {
        val text = JniHandStamp.getSData(data)
        if (TextUtils.isEmpty(text))
            return
        if (text[0] != '{')
            return
        val json = Gson().fromJson(text, NewCaseBeanPackage::class.java)
        if (json != null && json.code == 0 && json.data != null) {
            if (!TextUtils.isEmpty(saveFile))
                saveBuff2File(text, saveFile)
            onNewCaseRequest(json.data.rows)
        }
    }

    private fun getNewCaseList(data: String): ArrayList<NewCaseBean> {
        if (TextUtils.isEmpty(data) || data[0] != '[')
            return ArrayList()
        return Gson().fromJson(data, object : TypeToken<ArrayList<NewCaseBean>>() {}.type)
    }

    private fun addLatestCase(data: String, saveFile: String) {
        //服务器发送的数据明明没有压缩，却故意设置一个错误的zip标记，
        //让客户端无法正确处理，然后去调用新的接口，这是个什么处理方式？
        //以上是根据2.0.1代码猜的，他并没有处理age字段，
        //而是直接在onError或解析出的数值为空里处理
        val newCaseList = getNewCaseList(data)
        for (NewCase in newCaseList) {
            if (NewCase.cdnCover.substring(0, 4) != "http")
                NewCase.cdnCover = BuildConfig.RELEASE_OSS_DOWNLOAD + "h5/" + NewCase.cdnCover
            if (NewCase.localFilePath.substring(0, 4) != "http")
                NewCase.localFilePath =
                    BuildConfig.RELEASE_OSS_DOWNLOAD + "h5/" + NewCase.localFilePath
        }

        if (!TextUtils.isEmpty(saveFile))
            saveBuff2File(data, saveFile)
        onNewCaseRequest(newCaseList)
    }

    class NewBanderData(val data: ArrayList<NewBander>?, val code: Int)
    class NewBander(
        val createTime: String?,
        val extraID: Long?,
        val id: Long,
        var imgPath: String,
        val isShow: Int?,
        val openType: Int?,
        val sort: Int?,
        val title: String?,
        val updateTime: String?,
        val url: String?
    )

    private fun loadBander() {
        if (DictionaryUtils.step < 2) {
            Handler(Looper.getMainLooper()).postDelayed({
                loadBander()
            }, 500)
            return
        }
        val inStream = Utils.openfile("bander.txt")
        val inputReader = InputStreamReader(inStream, charset("UTF_8"))
        val buff = inputReader.readText()
        inStream.close()
        addNewBander(buff)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addNewBander(data: String) {
        if (data[0] != '{')
            return
        val json = Gson().fromJson(data, NewBanderData::class.java)
        if (json != null && json.code == 0 && json.data != null && json.data.size > 0) {
            val imageList = ArrayList<BanderBean>()
            for (row in json.data) {
                if (row.imgPath.substring(0, 4) != "http")
                    row.imgPath = BuildConfig.RELEASE_OSS_DOWNLOAD + "h5/" + row.imgPath
                imageList.add(
                    BanderBean(
                        0,
                        row.imgPath,
                        row.url ?: "",
                        row.title ?: "",
                        BanderType.TYPE_URL
                    )
                )
            }
            banderAdapter.setDatas(imageList)
            banderAdapter.notifyDataSetChanged()
        }
    }
}