package com.demo.antizha.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.demo.antizha.R
import com.demo.antizha.UserInfoBean
import com.demo.antizha.optimizationTimeStr
import com.demo.antizha.ui.HiCore
import com.demo.antizha.ui.activity.PromosWebDetActivity

class NewCaseBean(
    val id: String,
    val updateTime: String,
    val createTime: String,
    val title: String,
    val author: String,
    var cdnCover: String,
    var localFilePath: String,
    val mterialType: Int
)

class NewCaseBeanData(var rows: ArrayList<NewCaseBean>)
class NewCaseBeanPackage(val data: NewCaseBeanData?, val code: Int)

class HomeNewCaseAdapter : HeadFootAdapter {
    private val context: Context
    private var list: MutableList<NewCaseBean>

    fun getHeaderView(): View? {
        return _headerView
    }

    fun setHeaderView(view: View?) {
        _headerView = view
        notifyItemInserted(0)
    }

    fun getFooterView(): View? {
        return _footerView
    }

    fun setFooterView(view: View?) {
        _footerView = view
        notifyItemInserted(itemCount - 1)
    }

    constructor(context: Context, list: MutableList<NewCaseBean>) {
        this.context = context
        this.list = list
    }

    inner class NewCaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var rlTypeTopic: View
        lateinit var ivTopicPic: ImageView
        lateinit var ivTopicTit: TextView
        lateinit var ivTopicTime: TextView
        lateinit var viewLine: View

        init {
            if (view !== _headerView && view !== _footerView) {
                rlTypeTopic = view.findViewById(R.id.rl_type_topic)
                ivTopicPic = view.findViewById(R.id.iv_topic_pic)
                ivTopicTit = view.findViewById(R.id.iv_topic_tit)
                ivTopicTime = view.findViewById(R.id.iv_topic_time)
                viewLine = view.findViewById(R.id.view_line)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    val data: MutableList<NewCaseBean>
        get() = list

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        if (getItemViewType(i) == 2 && holder is NewCaseViewHolder) {
            val rowsBean: NewCaseBean = list[i - 1]
            holder.ivTopicTit.text = rowsBean.title
            if (!TextUtils.isEmpty(rowsBean.author)) {
                holder.ivTopicTime.text =
                    "${rowsBean.author}  ${optimizationTimeStr(rowsBean.updateTime)}"
            } else {
                holder.ivTopicTime.text = "国家反诈中心  ${optimizationTimeStr(rowsBean.updateTime)}"
            }
            holder.ivTopicPic.visibility = View.VISIBLE
            if (4 != rowsBean.mterialType)
                Glide.with(_headerView!!).load(rowsBean.cdnCover).into(holder.ivTopicPic)
            else
                holder.ivTopicPic.visibility = View.INVISIBLE

            if (TextUtils.isEmpty(rowsBean.cdnCover)) {
                holder.ivTopicPic.visibility = View.INVISIBLE
            }
            if (i == itemCount - 2) {
                holder.viewLine.visibility = View.GONE
            } else {
                holder.viewLine.visibility = View.VISIBLE
            }
            holder.itemView.setOnClickListener(View.OnClickListener { view ->
                if (!HiCore.app.isDouble()) {
                    val intent = Intent(context, PromosWebDetActivity::class.java)
                    intent.putExtra("extra_web_title", "国家反诈中心")
                    val adcode =
                        if (TextUtils.isEmpty(UserInfoBean.adcode)) "110105" else UserInfoBean.adcode
                    intent.putExtra("extra_web_url", rowsBean.localFilePath + "&nodeId=$adcode")
                    intent.putExtra("extra_web_id", rowsBean.id)
                    intent.putExtra("extra_web_enter", 2)
                    context.startActivity(intent)
                }
            })
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        if (_headerView != null && i == 0) {
            return NewCaseViewHolder(_headerView!!)
        }
        return if (_footerView == null || i != 1) {
            NewCaseViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_home_new_case, viewGroup, false))
        } else NewCaseViewHolder(_footerView!!)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun resetNewCase(list: List<NewCaseBean>?) {
        this.list.clear()
        this.list.addAll((list)!!)
        notifyDataSetChanged()
    }

}