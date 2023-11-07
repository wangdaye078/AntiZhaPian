package com.demo.antizha.ui.view

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.demo.antizha.R

class BottomBarTab(context: Context, i: Int) : FrameLayout(context) {
    private lateinit var mIcon: ImageView
    private lateinit var mTitle: TextView
    var mTabPosition: Int private set
    private val unselImages: IntArray
    private val selImages: IntArray

    private fun initTab(context: Context, i: Int) {
        val inflate: View = LayoutInflater.from(context)
            .inflate(R.layout.tab_bottom_bar, this as ViewGroup, true)
        mIcon = inflate.findViewById<View>(R.id.iv_tab_bar) as ImageView
        mTitle = inflate.findViewById<View>(R.id.tv_tab_bar) as TextView
        mIcon.setImageResource(unselImages[i])
        mTitle.setText(titles[i])
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        if (selected) {
            mIcon.setImageResource(selImages[mTabPosition])
            mTitle.setTextColor(Color.parseColor("#2946E6"))
            return
        }
        mIcon.setImageResource(unselImages[mTabPosition])
        mTitle.setTextColor(Color.parseColor("#666666"))
    }

    fun setTabPosition(position: Int, currentPosition: Int) {
        mTabPosition = position
        if (position == currentPosition) {
            setSelected(true)
        }
    }

    companion object {
        val titles = arrayOf("首页", "骗局曝光", "我的")
    }

    init {
        mTabPosition = -1
        unselImages = intArrayOf(R.mipmap.tab_home_unseled,
            R.mipmap.tab_xc_unseled,
            R.mipmap.tab_mine_unseled)
        selImages = intArrayOf(R.mipmap.tab_home_seled,
            R.mipmap.tab_xc_seled,
            R.mipmap.tab_mine_seled)
        initTab(context, i)
    }
}