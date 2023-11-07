package com.demo.antizha.ui.activity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.demo.antizha.R
import com.demo.antizha.databinding.DialogPreviewPictureBinding
import com.demo.antizha.util.GlideEngine
import com.luck.picture.lib.photoview.PhotoView
import qiu.niorgai.StatusBarCompat


class PreviewPictureActivity : BaseActivity() {
    companion object {
        const val EXTRA_PIC = "extra_pic"
        const val EXTRA_POSITION = "extra_position"
    }

    private lateinit var infoBinding: DialogPreviewPictureBinding
    private var preViews: ArrayList<View> = ArrayList()
    private var medias: ArrayList<String> = ArrayList()
    override fun initPage() {
        overridePendingTransition(R.anim.picture_anim_enter, R.anim.picture_anim_fade_in)
        infoBinding = DialogPreviewPictureBinding.inflate(layoutInflater)
        setContentView(infoBinding.root)
        infoBinding.piTitle.tvTitle.text = "预览"
        StatusBarCompat.translucentStatusBar(this, true, false)
        getIntentData()
        infoBinding.piTitle.ivBack.setOnClickListener {
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.picture_anim_fade_in, R.anim.picture_anim_exit)
    }

    private fun getIntentData() {
        val list: ArrayList<String>? = intent.getStringArrayListExtra(EXTRA_PIC)
        val pos = intent.getIntExtra(EXTRA_POSITION, 0)
        if (list == null)
            return
        for (path in list) {
            preViews.add(LayoutInflater.from(this)
                .inflate(R.layout.pic_preview, null as ViewGroup?))
        }
        medias.addAll(list)
        infoBinding.viewPager.adapter = PreviewPagerAdapter(preViews)
        infoBinding.viewPager.currentItem = pos
    }

    inner class PreviewPagerAdapter(private val list: List<View>) : PagerAdapter() {
        override fun destroyItem(viewGroup: ViewGroup, i: Int, obj: Any) {
            viewGroup.removeView(list[i])
        }

        override fun getCount(): Int {
            return list.size
        }

        override fun instantiateItem(viewGroup: ViewGroup, i: Int): Any {
            val view = list[i]
            val glideEngine = GlideEngine.createGlideEngine()
            val ppa = this@PreviewPictureActivity
            glideEngine.loadImage(ppa,
                ppa.medias[i],
                view.findViewById<PhotoView>(R.id.preview_image))
            viewGroup.addView(view)
            return view
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view === obj
        }
    }

}