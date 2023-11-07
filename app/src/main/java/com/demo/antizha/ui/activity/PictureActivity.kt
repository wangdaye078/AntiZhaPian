package com.demo.antizha.ui.activity

import android.content.Intent
import android.text.TextUtils
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.demo.antizha.R
import com.demo.antizha.adapter.PictureSelectAdapter
import com.demo.antizha.databinding.ActivityPictureBinding
import com.demo.antizha.util.PictureUtil
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener

class PictureActivity : BaseUploadActivity() {
    private lateinit var infoBinding: ActivityPictureBinding

    private lateinit var mAdapter: PictureSelectAdapter
    private val mLocalMedia: ArrayList<String> = ArrayList()
    private var previewPosition = -1
    private val mMaxSelectNum = 6

    override fun initPage() {
        infoBinding = ActivityPictureBinding.inflate(layoutInflater)
        setContentView(infoBinding.root)
        infoBinding.piTitle.tvTitle.text = "添加图片"
        infoBinding.lyComplete.tvCommitTip.text = "最多可选择${mMaxSelectNum}张图片"
        mLocalMedia.add("")
        getIntentData()
        infoBinding.recyclerview.layoutManager = GridLayoutManager(this, 3)
        mUploadStateList.add(UploadStateInfo())
        mAdapter = PictureSelectAdapter(R.layout.recyclerview_picture,
            mLocalMedia,
            PictureSelectAdapter.stylePicture,
            mMaxSelectNum,
            mUploadStateList)
        infoBinding.recyclerview.adapter = mAdapter
        infoBinding.recyclerview.itemAnimator = null
        mAdapter.setOnItemChildClickListener(OnItemChildClickListener { adapter, view, position ->
            val id: Int = view.id
            if (id == R.id.iv_clear) {
                removeMedia(position)
                adapter.notifyItemRemoved(position)
                if (mLocalMedia.size == 1)
                    infoBinding.lyComplete.btnCommit.text = "确定"
            } else if (id == R.id.picture_foot) {
                if (PictureUtil.checkPermission(this, true)) {
                    selectImage()
                }
            } else if (id == R.id.picture_select) {
                val intent = Intent(this, PreviewPictureActivity::class.java)
                val medias = ArrayList<String>()
                medias.addAll(mLocalMedia)
                if (TextUtils.isEmpty(mLocalMedia.last()))
                    medias.removeAt(medias.size - 1)
                intent.putStringArrayListExtra(PreviewPictureActivity.EXTRA_PIC, medias)
                intent.putExtra(PreviewPictureActivity.EXTRA_POSITION, position)
                startActivity(intent)
            }
        })
        infoBinding.piTitle.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        setResult(RESULT_OK, intent)
        val medias = ArrayList<String>()
        medias.addAll(mLocalMedia)
        if (TextUtils.isEmpty(mLocalMedia.last()))
            medias.removeAt(medias.size - 1)
        intent.putStringArrayListExtra("pics", medias)
        super.onBackPressed()
    }

    private fun getIntentData() {
        val list = intent.getStringArrayListExtra("pics")
        if (list != null) {
            for (media in list)
                addMedia(media)
        }
        if (mLocalMedia.size > 1)
            infoBinding.lyComplete.btnCommit.text = "文件上传"
    }

    fun addMedia(path: String) {
        for (media in mLocalMedia)
            if (media == path)
                return
        //最后是个空的，填进去
        if (TextUtils.isEmpty(mLocalMedia.last()))
            mLocalMedia[mLocalMedia.size - 1] = path
        //已经满了，就不加空了
        if (mLocalMedia.size >= mMaxSelectNum)
            return
        mLocalMedia.add("")
        mUploadStateList.add(UploadStateInfo())
    }

    private fun removeMedia(pos: Int) {
        mLocalMedia.removeAt(pos)
        mUploadStateList.removeAt(pos)
        if (!TextUtils.isEmpty(mLocalMedia.last())) {
            mLocalMedia.add("")
            mUploadStateList.add(UploadStateInfo())
        }
    }

    private fun selectImage() {
        var mediaSize = mLocalMedia.size
        if (TextUtils.isEmpty(mLocalMedia.last()))
            mediaSize -= 1
        val model = PictureUtil.getImageSelectModel(this, false, 200.0F, mMaxSelectNum - mediaSize)
        model.forResult(object : OnResultCallbackListener<LocalMedia?> {
            override fun onCancel() {
            }

            override fun onResult(result: List<LocalMedia?>?) {
                if (result == null)
                    return
                for (media in result) {
                    if (media != null) {
                        addMedia(media.path)
                    }
                }
                mAdapter.notifyDataSetChanged()
                if (mLocalMedia.size > 1)
                    infoBinding.lyComplete.btnCommit.text = "文件上传"
            }
        })
    }

}
