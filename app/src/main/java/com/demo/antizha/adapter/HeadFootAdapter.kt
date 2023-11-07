package com.demo.antizha.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class HeadFootAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    var _footerView: View? = null
    var _headerView: View? = null

    override fun getItemCount(): Int {
        if (_headerView == null && _footerView == null) {
            return getItemCount()
        }
        if (_headerView == null && _footerView != null) {
            return getItemCount() + 1
        }
        return if (_headerView == null || _footerView != null) {
            getItemCount() + 2
        } else getItemCount() + 1
    }

    override fun getItemViewType(i: Int): Int {
        return if (_headerView == null || i != 0) {
            if (_footerView == null || i != getItemCount() - 1) 2 else 1
        } else 0
    }
}