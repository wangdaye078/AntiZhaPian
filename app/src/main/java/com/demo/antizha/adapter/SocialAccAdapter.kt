package com.demo.antizha.adapter

import android.os.Parcel
import android.os.Parcelable
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.demo.antizha.R

class SocialAccAdapter(resId: Int, val list: ArrayList<SocialAccBean>) :
    BaseQuickAdapter<SocialAccBean, BaseViewHolder>(resId, list) {
    init {
        addChildClickViewIds(R.id.iv_delete)
        addChildClickViewIds(R.id.iv_edit)
    }
    override fun convert(holder: BaseViewHolder, item: SocialAccBean) {
        holder.setText(R.id.tv_name, item.accountName)
        holder.setText(R.id.tv_acc_num, item.accountNum)
    }
}

class SocialAccBean : Parcelable {
    var accountName: String = ""
    var accountNum: String = ""
    var id: String = ""
    var suspectInfoID: String = ""

    constructor()

    constructor(source: Parcel) {
        accountName = source.readString().toString()
        accountNum = source.readString().toString()
        id = source.readString().toString()
        suspectInfoID = source.readString().toString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(accountName)
        dest.writeString(accountNum)
        dest.writeString(id)
        dest.writeString(suspectInfoID)
    }

    companion object CREATOR : Parcelable.Creator<SocialAccBean> {
        override fun createFromParcel(parcel: Parcel): SocialAccBean {
            return SocialAccBean(parcel)
        }

        override fun newArray(size: Int): Array<SocialAccBean?> {
            return arrayOfNulls(size)
        }
    }
}
