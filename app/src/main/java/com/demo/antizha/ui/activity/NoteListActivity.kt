package com.demo.antizha.ui.activity

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demo.antizha.R
import com.demo.antizha.adapter.NoteListAdapte
import com.demo.antizha.adapter.NoteListBean
import com.demo.antizha.databinding.ActivityNoteListBinding
import com.demo.antizha.ui.SwipBackLayout
import com.demo.antizha.util.Utils
import com.google.gson.Gson
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import qiu.niorgai.StatusBarCompat
import java.io.InputStreamReader


class NoteListBeanData(val data: ArrayList<NoteListBean>, val code: Int)

class NoteListActivity : BaseActivity() {
    private lateinit var infoBinding: ActivityNoteListBinding
    private lateinit var swipBackLayout: SwipBackLayout
    private var noteList: ArrayList<NoteListBean> = ArrayList()
    private lateinit var mAdapter: NoteListAdapte

    override fun initPage() {
        infoBinding = ActivityNoteListBinding.inflate(layoutInflater)
        setContentView(infoBinding.root)
        StatusBarCompat.translucentStatusBar(this, true, true)
        swipBackLayout = SwipBackLayout(this)
        swipBackLayout.init()
        infoBinding.piTitle.tvTitle.text = "公告"
        infoBinding.lyNodata.ivImg.setImageResource(R.drawable.iv_no_feed_list)
        infoBinding.lyNodata.tvTip.text = "暂无数据"

        mAdapter = NoteListAdapte(this, noteList)
        val lyManager = LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false)
        infoBinding.recyclerview.layoutManager = lyManager
        infoBinding.recyclerview.adapter = mAdapter

        infoBinding.swipeRefresh.setRefreshHeader(ClassicsHeader(this))
        infoBinding.swipeRefresh.setEnableLoadMore(false)
        infoBinding.swipeRefresh.setOnRefreshListener(OnRefresh())
        infoBinding.swipeRefresh.autoRefresh()

        infoBinding.piTitle.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    inner class OnRefresh internal constructor() : OnRefreshListener {
        @SuppressLint("NotifyDataSetChanged")
        override fun onRefresh(refreshLayout: RefreshLayout) {
            Handler(Looper.getMainLooper()).postDelayed({
                val inputStream = Utils.openfile("noticelist.txt")
                val noteListData = Gson().fromJson(InputStreamReader(inputStream, "UTF-8"),
                    NoteListBeanData::class.java)
                infoBinding.swipeRefresh.finishRefresh()
                if (noteListData.code != 0)
                    return@postDelayed
                noteList.clear()
                noteList.addAll(noteListData.data)
                mAdapter.notifyDataSetChanged()
                inputStream.close()
            }, 2000)
        }
    }
}