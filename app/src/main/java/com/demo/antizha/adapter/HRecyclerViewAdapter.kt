package com.demo.antizha.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.demo.antizha.R
import com.demo.antizha.util.NotchUtils.dp2px


class HRecyclerViewAdapter(private var context: Context, i: Int) :
    RecyclerView.Adapter<HRecyclerViewAdapter.HImageHolder>() {
    private var shareStrings: Array<String>
    private var shareImages: IntArray
    private var onItemClickListener: ((Int, String) -> Unit)? = null
    private var shareType = i
    private var widthPixels = 0

    init {
        this.widthPixels = context.resources.displayMetrics.widthPixels
        if (shareType == 5) {
            shareStrings = arrayOf("微信好友", "QQ好友", "钉钉", "复制链接")
            shareImages = intArrayOf(R.drawable.iv_share_wx,
                R.drawable.iv_login_qq,
                R.drawable.iv_share_dding,
                R.drawable.iv_share_copy)
        }
        else {
            shareStrings = arrayOf("微信好友", "朋友圈", "QQ好友", "QQ空间", "微博", "钉钉", "复制链接")
            shareImages = intArrayOf(R.drawable.iv_share_wx,
                R.drawable.iv_share_wxcicle,
                R.drawable.iv_login_qq,
                R.drawable.iv_share_qzone,
                R.drawable.iv_share_sina,
                R.drawable.iv_share_dding,
                R.drawable.iv_share_copy)
        }
    }

    inner class HImageHolder(view: View) : RecyclerView.ViewHolder(view) {
        var flShareItem: FrameLayout = view.findViewById(R.id.fl_share_item)
        var ivImage: ImageView = view.findViewById(R.id.image)
        var tvName: TextView = view.findViewById(R.id.tv_name)

        init {
            if (this@HRecyclerViewAdapter.shareType == 5) {
                this@HRecyclerViewAdapter.setShareItemWidth(flShareItem)
            }
        }
    }

    override fun getItemCount(): Int {
        return shareStrings.size
    }

    fun setOnItemClickListener(onItemClickListener: (Int, String) -> Unit) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): HImageHolder {
        return HImageHolder(LayoutInflater.from(context)
            .inflate(R.layout.item_hrecyclerview, viewGroup, false))
    }

    override fun onBindViewHolder(hImageHolder: HImageHolder, i: Int) {
        hImageHolder.tvName.text = shareStrings[i]
        hImageHolder.ivImage.setImageResource(shareImages[i])
        if (shareType == 1 && i == shareStrings.size - 1) {
            hImageHolder.tvName.text = "生成海报"
            hImageHolder.ivImage.setImageResource(R.drawable.iv_share_download)
        }
        hImageHolder.itemView.setOnClickListener { view ->
            if (onItemClickListener != null)
                onItemClickListener!!(hImageHolder.layoutPosition, shareStrings[i])
        }
    }

    fun setShareItemWidth(frameLayout: FrameLayout) {
        val layoutParams = frameLayout.layoutParams
        layoutParams.width = (widthPixels - dp2px(16.0f)) / shareStrings.size
        frameLayout.layoutParams = layoutParams
    }

}