package com.demo.antizha.ui.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demo.antizha.BuildConfig
import com.demo.antizha.R
import com.demo.antizha.UserInfoBean
import com.demo.antizha.adapter.HRecyclerViewAdapter
import com.demo.antizha.ui.BaseDialog
import com.demo.antizha.util.Utils


class ShareConfigBean : Parcelable {
    var apiAddress: String = ""
    var description: String = ""
    var downloadUrl: String = ""
    var iconUrl: String = ""
    var id: String = ""
    var initiatorName: String = ""
    var saveImgUrl: String = ""
    var title: String = ""

    constructor()

    constructor(source: Parcel) {
        apiAddress = source.readString().toString()
        description = source.readString().toString()
        downloadUrl = source.readString().toString()
        iconUrl = source.readString().toString()
        id = source.readString().toString()
        initiatorName = source.readString().toString()
        saveImgUrl = source.readString().toString()
        title = source.readString().toString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(apiAddress)
        dest.writeString(description)
        dest.writeString(downloadUrl)
        dest.writeString(iconUrl)
        dest.writeString(id)
        dest.writeString(initiatorName)
        dest.writeString(saveImgUrl)
        dest.writeString(title)
    }

    companion object CREATOR : Parcelable.Creator<ShareConfigBean> {
        override fun createFromParcel(parcel: Parcel): ShareConfigBean {
            return ShareConfigBean(parcel)
        }

        override fun newArray(size: Int): Array<ShareConfigBean?> {
            return arrayOfNulls(size)
        }
    }
}

class HiShareDialog(
    private var activity: Activity,
    private var shareBean: ShareConfigBean,
    private var shareType: Int
) : BaseDialog(activity, R.style.base_dialog_style) {
    private var shareUrl: String
    private var title: String
    private var description: String

    init {
        shareUrl = BuildConfig.RELEASE_H5_URL + "/QRCode/?appkey=a28ft4&pcode=10000"
        title = "下载国家反诈中心APP,公安部打击防范电信网络诈骗官方应用"
        description = "看更多反诈文章，上国家反诈中心APP"
        createDialog()
    }

    @SuppressLint("ResourceType")
    private fun createDialog() {
        setContentView(R.layout.share_dlg)
        setGravityLayout(0)
        widthDialog = (-2.0).toFloat()
        heightDialogdp = -2.0f
        setCancelable(true)
        setCanceledOnTouchOutside(true)
        initOnCreate()
        window!!.setWindowAnimations(R.anim.anim_bottom_in)
        findViewById<View>(R.id.cancel_btn).setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                dismiss()
            }
        })
        val recyclerView = findViewById<View>(R.id.h_recyclerview) as RecyclerView
        val hRecyclerViewAdapter = HRecyclerViewAdapter(activity, shareType)
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerView.layoutManager = linearLayoutManager
        //recyclerView.setHasFixedSize(true)
        recyclerView.adapter = hRecyclerViewAdapter
        hRecyclerViewAdapter.setOnItemClickListener { i, str ->
            this@HiShareDialog.onItemClick(i, str)
        }
        if (!TextUtils.isEmpty(shareBean.downloadUrl)) {
            shareUrl = shareBean.downloadUrl
        }
        if (!TextUtils.isEmpty(shareBean.title)) {
            title = shareBean.title
        }
        if (!TextUtils.isEmpty(shareBean.description)) {
            description = shareBean.description
        }
        shareUrl += "&nodeId=" + UserInfoBean.adcode
    }

    fun onItemClick(i: Int, str: String) {
        when (i) {
            6 -> {
                if (shareType == 2)
                    Utils.copyToClipboard(shareUrl)
            }
        }
        dismiss()
    }

}