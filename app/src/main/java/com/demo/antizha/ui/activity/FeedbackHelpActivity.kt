package com.demo.antizha.ui.activity

import android.content.Intent
import android.nfc.NfcAdapter.EXTRA_ID
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.demo.antizha.R
import com.demo.antizha.databinding.ActivityFeedbackHelpBinding
import com.demo.antizha.util.AnimUtils
import com.demo.antizha.util.Utils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class QABean {
    var answer: String? = null
    var createTime: String? = null
    var description: String? = null
    var id: String? = null
    var isShow = 0
    var qaClassifyCode = 0
    var question: String? = null
    var sort = 0
    var updateTime: String? = null
}

class QATypeBean {
    var code = 0
    var createTime: String? = null
    var description: String? = null
    var id: String? = null
    var isShow = 0
    var qaList: List<QABean>? = null
    var sort = 0
    var text: String? = null
    var updateTime: String? = null
}

class QATypeBeanData(val data: ArrayList<QATypeBean>, val code: Int)

class FeedbackHelpActivity : BaseActivity() {
    private lateinit var infoBinding: ActivityFeedbackHelpBinding
    private lateinit var qaTypeBeanData: QATypeBeanData
    override fun initPage() {
        infoBinding = ActivityFeedbackHelpBinding.inflate(layoutInflater)
        setContentView(infoBinding.root)
        infoBinding.piTitle.tvTitle.text = "反馈与帮助"
        infoBinding.piTitle.tvRightRed.visibility = View.GONE
        initQaList()
        infoBinding.piTitle.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun initQaList() {
        val inputStream = Utils.openfile("qalist.txt")
        qaTypeBeanData = Gson().fromJson(InputStreamReader(inputStream, "UTF-8"),
            object : TypeToken<QATypeBeanData>() {}.type)
        for ((i, qaType) in qaTypeBeanData.data.withIndex()) {
            addView(qaType, i == qaTypeBeanData.data.size - 1)
        }
        inputStream.close()
    }

    @Throws(Exception::class)
    private fun addView(qATypeBean: QATypeBean, isLast: Boolean) {
        var viewGroup: ViewGroup? = null
        val inflate = View.inflate(this, R.layout.item_qa, null)
        val linearLayout = inflate.findViewById<LinearLayout>(R.id.ll_type)
        val tvName = inflate.findViewById<TextView>(R.id.type_name)
        val ivTypeArrow = inflate.findViewById<ImageView>(R.id.iv_type_arrow)
        val tvContent1 = inflate.findViewById<TextView>(R.id.tv_content_1)
        val lyQa = inflate.findViewById<View>(R.id.ly_qa)
        val tvContent = lyQa.findViewById<TextView>(R.id.tv_content)
        val lyContentMore = inflate.findViewById<LinearLayout>(R.id.ly_content_more)
        val line = inflate.findViewById<View>(R.id.botton_line)
        if (qATypeBean.qaList != null && qATypeBean.qaList!!.isNotEmpty()) {
            for ((i, qABean) in qATypeBean.qaList!!.withIndex()) {
                if (i == 0) {
                    tvContent1.text = qABean.question
                    tvContent1.setOnClickListener(OnQaClickListener(qABean))
                } else if (i == 1) {
                    val question = qABean.question
                    if (TextUtils.isEmpty(question)) {
                        lyQa.visibility = View.GONE
                    } else {
                        tvContent.text = question
                        tvContent.setOnClickListener(OnQaClickListener(qABean))
                    }
                } else {
                    val lyQaSub = View.inflate(this, R.layout.layout_qa, viewGroup)
                    val tvContentSub = lyQaSub.findViewById<View>(R.id.tv_content) as TextView
                    tvContentSub.text = qABean.question
                    tvContentSub.setOnClickListener(OnQaClickListener(qABean))
                    lyContentMore.addView(lyQaSub)
                }
                viewGroup = null
            }
        }
        tvName.text = qATypeBean.text
        linearLayout.setOnClickListener(OnQaTypeClickListener(lyContentMore, ivTypeArrow))
        if (isLast) {
            line.visibility = View.INVISIBLE
        }
        lyContentMore.visibility = View.VISIBLE
        infoBinding.llQaContent.addView(inflate)
    }

    fun intentDetail(qABean: QABean) {
        if (!isDouble()) {
            val intent = Intent(this, FeedbackDetailActivity::class.java)
            intent.putExtra(EXTRA_ID, qABean.id)
            intent.putExtra("extra_question", qABean.question)
            intent.putExtra("extra_answer", qABean.answer)
            startActivity(intent)
        }
    }

    inner class OnQaClickListener internal constructor(private val qa: QABean) :
        View.OnClickListener {
        override fun onClick(view: View) {
            intentDetail(qa)
        }
    }

    class OnQaTypeClickListener internal constructor(
        private val lyMore: LinearLayout,
        private val ivArrow: ImageView) :
        View.OnClickListener {
        override fun onClick(view: View) {
            if (lyMore.visibility == View.GONE) {
                lyMore.visibility = View.VISIBLE
                AnimUtils.rotateArrow(ivArrow, false)
                return
            }
            lyMore.visibility = View.GONE
            AnimUtils.rotateArrow(ivArrow, true)
        }
    }

}