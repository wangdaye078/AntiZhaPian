package com.demo.antizha.adapter

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.demo.antizha.R
import com.demo.antizha.ui.HiCore
import com.demo.antizha.ui.activity.BaseUploadActivity

class PictureSelectAdapter(resId: Int, private var medias: ArrayList<String>) :
    BaseQuickAdapter<String, BaseViewHolder>(resId, medias) {
    companion object {
        const val styleVideo = "style_item_video"
        const val stylePicture = "style_item_picture"
    }

    private var style: String? = null
    private var maxCount = 0
    private var uploadStateInfos = ArrayList<BaseUploadActivity.UploadStateInfo>()

    init {
        style = ""
        maxCount = 9
        addChildClickViewIds(R.id.iv_clear)
        addChildClickViewIds(R.id.picture_foot)
        addChildClickViewIds(R.id.picture_select)
    }

    constructor(resId: Int,
                medias: ArrayList<String>,
                style: String?,
                maxCount: Int,
                upStates: ArrayList<BaseUploadActivity.UploadStateInfo>) : this(resId, medias) {
        this@PictureSelectAdapter.style = style
        this@PictureSelectAdapter.maxCount = maxCount
        this@PictureSelectAdapter.uploadStateInfos = upStates
    }

    @Suppress("UNUSED_PARAMETER")
    private fun convertMedia2Video(baseViewHolder: BaseViewHolder, localMedia: String) {
    }

    private fun convertMedia2Picture(baseViewHolder: BaseViewHolder, localMedia: String) {
        //baseViewHolder.addOnClickListener(R.id.iv_clear)

        val ivSelect = baseViewHolder.getView<ImageView>(R.id.picture_select)
        val ivFoot = baseViewHolder.getView<ImageView>(R.id.picture_foot)
        val ivClear = baseViewHolder.getView<ImageView>(R.id.iv_clear)
        val tvUploadState = baseViewHolder.getView<TextView>(R.id.tv_upload_state)
        if (TextUtils.isEmpty(localMedia)) {
            ivClear.visibility = View.GONE
            ivSelect.visibility = View.GONE
            tvUploadState.visibility = View.GONE
            ivFoot.visibility = View.VISIBLE
            return
        }
        ivFoot.visibility = View.GONE
        try {
            val indexOf = medias.indexOf(localMedia)
            val size = uploadStateInfos.size
            if (size > 0 && indexOf < size) {
                BaseUploadActivity.showUpState(tvUploadState, uploadStateInfos[indexOf])
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        ivClear.visibility = View.VISIBLE
        ivSelect.visibility = View.VISIBLE
        tvUploadState.visibility = View.VISIBLE
        Glide.with(HiCore.context).load(localMedia).into(ivSelect)
    }
    override fun convert(holder: BaseViewHolder, item: String) {
        if (styleVideo == style) {
            convertMedia2Video(holder, item)
        } else {
            convertMedia2Picture(holder, item)
        }
    }

}
