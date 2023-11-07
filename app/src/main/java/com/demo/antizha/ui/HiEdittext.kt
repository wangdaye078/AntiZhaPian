package com.demo.antizha.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.EditText

@SuppressLint("AppCompatCustomView")
class HiEdittext : EditText {
    override fun dispatchTouchEvent(motionEvent: MotionEvent): Boolean {
        parent.requestDisallowInterceptTouchEvent(true)
        return super.dispatchTouchEvent(motionEvent)
    }

    constructor(context: Context?, attributeSet: AttributeSet?) : super(context, attributeSet)
    constructor(context: Context?, attributeSet: AttributeSet?, i: Int) :
            super(context, attributeSet, i)
}