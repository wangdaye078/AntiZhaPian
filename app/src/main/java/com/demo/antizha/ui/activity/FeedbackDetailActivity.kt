package com.demo.antizha.ui.activity

import android.text.Html
import com.demo.antizha.R
import com.demo.antizha.databinding.ActivityFeedbackQuestionBinding
import com.demo.antizha.util.DialogUtils


class FeedbackDetailActivity : BaseActivity() {
    private lateinit var infoBinding: ActivityFeedbackQuestionBinding
    private var isClickSolve = false


    override fun initPage() {
        infoBinding = ActivityFeedbackQuestionBinding.inflate(layoutInflater)
        setContentView(infoBinding.root)
        infoBinding.piTitle.tvTitle.text = "问题详情"
        infoBinding.tvQuestion.text = intent.getStringExtra("extra_question")
        infoBinding.tvAnswer.text = intent.getStringExtra("extra_answer")
        infoBinding.tvGoFeedback.text = Html.fromHtml("还没有解决您的问题？前往<font color=#2B4CFF>意见反馈</font>",
            Html.FROM_HTML_MODE_LEGACY)
        infoBinding.piTitle.ivBack.setOnClickListener {
            finish()
        }
        infoBinding.llNotSolve.setOnClickListener {
            clickSolve(false)
        }
        infoBinding.llSolve.setOnClickListener {
            clickSolve(true)
        }
    }

    private fun clickSolve(solve: Boolean) {
        if (!isClickSolve) {
            isClickSolve = true
            if (solve) {
                infoBinding.ivSolve.setImageResource(R.mipmap.ic_solve_select)
                infoBinding.tvSolveTip.setTextColor(resources.getColor(R.color.red_1, null))
            } else {
                infoBinding.ivNotSolve.setImageResource(R.mipmap.ic_not_solve_select)
                infoBinding.tvNotSolveTip.setTextColor(resources.getColor(R.color.red_1, null))
            }
            DialogUtils.showDialogAutoClose(this, false, 1, "谢谢您的反馈", R.mipmap.ic_white_ok)
        }
    }

}