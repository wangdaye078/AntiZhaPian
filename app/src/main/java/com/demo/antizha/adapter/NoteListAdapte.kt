package com.demo.antizha.adapter

import android.app.Activity
import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.demo.antizha.R
import com.demo.antizha.ui.activity.NoteDetailActivity

class NoteListBean(source: Parcel) : Parcelable {
    var content: String? = null
    var id: String? = null
    var isRead = false
    var title: String = ""
    var url: String? = null
    var vaildEndTime: String? = null
    var vaildStartTime: String? = null

    init {
        content = source.readString()
        id = source.readString()
        isRead = source.readInt() > 0
        title = source.readString().toString()
        url = source.readString()
        vaildEndTime = source.readString()
        vaildStartTime = source.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(content)
        dest.writeString(id)
        dest.writeInt(if (isRead) 1 else 0)
        dest.writeString(title)
        dest.writeString(url)
        dest.writeString(vaildEndTime)
        dest.writeString(vaildStartTime)
    }

    companion object CREATOR : Parcelable.Creator<NoteListBean> {
        override fun createFromParcel(parcel: Parcel): NoteListBean {
            return NoteListBean(parcel)
        }

        override fun newArray(size: Int): Array<NoteListBean?> {
            return arrayOfNulls(size)
        }
    }

}

class NoteHolder(view: View) : RecyclerView.ViewHolder(view) {
    var tvNoteThem: TextView = view.findViewById(R.id.tv_note_them)
    var tvTime: TextView = view.findViewById(R.id.tv_time)
    var tvNumRed: TextView = view.findViewById(R.id.tv_num_red)
    var tvDesc: TextView = view.findViewById(R.id.desc)
}

class NoteListAdapte(val mActivity: Activity, var list: ArrayList<NoteListBean>) :
    RecyclerView.Adapter<NoteHolder>() {

    private fun subTitle(str: String): String {
        return if (str.length <= 18) {
            str
        } else (str.subSequence(0, 18) as Any).toString() + "..."
    }

    override fun onBindViewHolder(holder: NoteHolder, i: Int) {
        if (list.size > 0) {
            val noteListBean = list[i]
            holder.tvNumRed.visibility = View.INVISIBLE

            holder.tvNoteThem.text = subTitle(noteListBean.title)
            holder.tvTime.text = noteListBean.vaildStartTime
            holder.itemView.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View?) {
                    openNoteDetail(noteListBean, view)
                }
            })
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): NoteHolder {
        return NoteHolder(LayoutInflater.from(mActivity)
            .inflate(R.layout.item_note_list, viewGroup, false))
    }

    fun openNoteDetail(noteListBean: NoteListBean, view: View?) {
        val intent = Intent(mActivity, NoteDetailActivity::class.java)
        intent.putExtra("from_page_bean", noteListBean)
        mActivity.startActivity(intent)
        noteListBean.isRead = true
        //this@NoteListPresenter.getNoteItem(noteListBean.id)
    }
}
