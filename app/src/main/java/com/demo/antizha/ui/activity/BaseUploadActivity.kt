package com.demo.antizha.ui.activity

import android.widget.TextView
import com.demo.antizha.R
import com.demo.antizha.ui.HiCore

abstract class BaseUploadActivity : BaseActivity() {
    companion object {
        private const val UPLOAD_STATE_LOADING = 0
        private const val UPLOAD_STATE_UPLOAD = 1
        private const val UPLOAD_STATE_SUCCESS = 2
        private const val UPLOAD_STATE_FAIL = 3

        fun showUpState(textView: TextView, uploadStateInfo: UploadStateInfo) {
            when (uploadStateInfo.uploadState){
                UPLOAD_STATE_LOADING->{
                    textView.text = "等待上传"
                    textView.setTextColor(HiCore.app.resources.getColor(R.color.colorGray, null))
                }
                UPLOAD_STATE_UPLOAD->{
                    textView.text = "上传中"
                    textView.setTextColor(HiCore.app.resources.getColor(R.color.black_dark, null))
                }
                UPLOAD_STATE_SUCCESS->{
                    textView.text = "上传完成"
                    textView.setTextColor(HiCore.app.resources.getColor(R.color.blue, null))
                }
                UPLOAD_STATE_FAIL->{
                    textView.text = "上传失败"
                    textView.setTextColor(HiCore.app.resources.getColor(R.color.colorRed, null))
                }
            }
        }
    }

    class UploadStateInfo {
        var fileId: String? = null
        var fileName: String? = null
        var filePath: String? = null
        var fileSize: Long = 0
        var isPlayState = false
        var progress: Long = 0
        var total: Long = 0
        var uploadState = 0
    }

    var mUploadStateList: ArrayList<UploadStateInfo> = ArrayList()

}