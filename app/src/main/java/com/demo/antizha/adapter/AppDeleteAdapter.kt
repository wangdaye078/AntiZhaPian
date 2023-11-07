package com.demo.antizha.adapter

import android.text.format.Formatter
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.demo.antizha.R
import com.demo.antizha.ui.HiCore
import com.demo.antizha.ui.activity.BaseUploadActivity
import com.demo.antizha.util.AppUtil.AppInfoBean


class AppDeleteAdapter(i: Int, list: ArrayList<AppInfoBean>?, /* renamed from: a */
                       private var upStates: List<BaseUploadActivity.UploadStateInfo>) :
    BaseQuickAdapter<AppInfoBean, BaseViewHolder>(i, list) {
    init {
        addChildClickViewIds(R.id.iv_clear)
    }
    public override fun convert(holder: BaseViewHolder, item: AppInfoBean) {
        if (item.appIcon != null)
            holder.setImageDrawable(R.id.app_icon, item.appIcon)

        val formatFileSize: String = Formatter.formatFileSize(HiCore.app, item.size)
        holder.setText(R.id.tv_app_name, item.appName)
        holder.setText(R.id.tv_app_version,
            "版本:" + item.version + "  |  " + formatFileSize)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val tvUpState = holder.getView<TextView>(R.id.tv_upload_state)
        if (position < upStates.size) {
            BaseUploadActivity.showUpState(tvUpState, upStates[position])
        }
    }

}