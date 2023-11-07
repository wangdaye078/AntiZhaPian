package com.demo.antizha.ui.activity

import android.text.Html
import android.text.Html.FROM_HTML_MODE_LEGACY
import android.text.method.ScrollingMovementMethod
import android.view.MotionEvent
import android.view.View
import com.demo.antizha.adapter.NoteListBean
import com.demo.antizha.databinding.ActivityNoteDetailBinding
import com.demo.antizha.ui.SwipBackLayout
import qiu.niorgai.StatusBarCompat


class NoteDetailActivity : BaseActivity() {
    private lateinit var infoBinding: ActivityNoteDetailBinding
    private lateinit var swipBackLayout: SwipBackLayout
    private lateinit var noteInfo: NoteListBean

    override fun initPage() {
        infoBinding = ActivityNoteDetailBinding.inflate(layoutInflater)
        setContentView(infoBinding.root)
        StatusBarCompat.translucentStatusBar(this, true, true)
        swipBackLayout = SwipBackLayout(this)
        swipBackLayout.init()
        infoBinding.piTitle.tvTitle.text = "公告详情"
        initView()
        infoBinding.piTitle.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initView() {
        noteInfo = intent.getParcelableExtra("from_page_bean")!!
        infoBinding.time.text = noteInfo.vaildStartTime
        infoBinding.title.text = Html.fromHtml(noteInfo.title, FROM_HTML_MODE_LEGACY)
        infoBinding.content.text = Html.fromHtml(noteInfo.content, FROM_HTML_MODE_LEGACY)
        infoBinding.content.movementMethod = ScrollingMovementMethod()
        infoBinding.content.setOnTouchListener(OnTouch.onTouch)
    }

    class OnTouch internal constructor() : View.OnTouchListener {
        override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
            return true
        }

        companion object {
            val onTouch = OnTouch()
        }
    }

}