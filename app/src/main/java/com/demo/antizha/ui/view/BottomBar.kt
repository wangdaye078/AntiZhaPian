package com.demo.antizha.ui.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout

class BottomBar : LinearLayout {
    private val bottomBarTabs: MutableList<BottomBarTab> = ArrayList<BottomBarTab>()
    private var mTabLayout: LinearLayout
    private var mTabParams: LayoutParams
    private var currentItemPosition: Int = 0
        private set
    private lateinit var onClickItemMenu: OnClickItemMenu

    interface OnClickItemMenu {
        fun onClickItem(nowPosition: Int, position: Int)
    }

    constructor(context: Context) : this(context, null) {
    }

    constructor(context: Context, attributeSet: AttributeSet) : this(context, attributeSet, 0) {
    }

    constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0) :
            super(context, attributeSet, defStyleAttr) {
        orientation = VERTICAL
        mTabLayout = LinearLayout(context)
        mTabLayout.setBackgroundColor(Color.WHITE)
        mTabLayout.orientation = HORIZONTAL
        addView(mTabLayout, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        mTabParams = LayoutParams(0, LayoutParams.MATCH_PARENT)
        mTabParams.weight = 1.0f
    }

    fun setCurrentItem(position: Int) {
        mTabLayout.post(Runnable {
            mTabLayout.getChildAt(position).performClick()
        })
    }

    fun setCurrentPosition(position: Int) {
        currentItemPosition = position
    }

    fun setOnClickItemMenu(onClickItemMenu: OnClickItemMenu) {
        this.onClickItemMenu = onClickItemMenu
    }

    fun initTabs() {
        for (i in 0 until BottomBarTab.titles.size) {
            addItem(BottomBarTab(context, i))
        }
    }

    fun addItem(bottomBarTab: BottomBarTab) {
        bottomBarTab.setOnClickListener(object : OnClickListener {
            override fun onClick(view: View) {
                onItemSelect(bottomBarTab, view)
            }
        })
        bottomBarTab.setTabPosition(mTabLayout.childCount, currentItemPosition)
        bottomBarTab.layoutParams = mTabParams
        mTabLayout.addView(bottomBarTab)
        bottomBarTabs.add(bottomBarTab)
    }

    fun onItemSelect(bottomBarTab: BottomBarTab, view: View?) {
        if (bottomBarTab.mTabPosition == currentItemPosition)
            return
        onClickItemMenu.onClickItem(bottomBarTab.mTabPosition, currentItemPosition)
        bottomBarTab.isSelected = true
        bottomBarTabs.get(currentItemPosition).isSelected = false
        currentItemPosition = bottomBarTab.mTabPosition
    }

    fun getItem(position: Int): BottomBarTab? {
        return if (bottomBarTabs.size < position) {
            null
        } else bottomBarTabs.get(position)
    }
}