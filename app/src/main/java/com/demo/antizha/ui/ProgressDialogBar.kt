package com.demo.antizha.ui

import android.app.Dialog
import android.content.Context

import android.widget.TextView
import com.demo.antizha.R


class ProgressDialogBar : Dialog {
    constructor(context: Context?) : super(context!!)

    constructor(context: Context?, i: Int) : super(context!!, i)

    companion object {
        var progressDialogBar: ProgressDialogBar? = null
        fun create(context: Context?): ProgressDialogBar? {
            progressDialogBar = ProgressDialogBar(context, R.style.CustomProgressDialog)
            progressDialogBar!!.setContentView(R.layout.progress_layout)
            return progressDialogBar
        }
    }

    fun getDialog(): ProgressDialogBar? {
        return progressDialogBar
    }

    fun setProgress(str: String?) {
        val textView = progressDialogBar!!.findViewById(R.id.text_progress) as TextView?
        if (textView != null) {
            textView.text = str
        }
    }

}