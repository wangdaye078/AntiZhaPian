package com.demo.antizha.ui.activity

import android.content.Intent
import android.text.TextUtils
import com.demo.antizha.databinding.ActivityEvidenceBinding


class EvidenceDiscActivity : BaseActivity() {
    private lateinit var infoBinding: ActivityEvidenceBinding
    override fun initPage() {
        infoBinding = ActivityEvidenceBinding.inflate(layoutInflater)
        setContentView(infoBinding.root)
        val szTitle = intent.getStringExtra("title")
        infoBinding.piTitle.tvTitle.text = szTitle
        infoBinding.edDescribe.setText(intent.getStringExtra("disc"))
        if (TextUtils.equals("案情描述", szTitle)) {
            infoBinding.edDescribe.hint = "请详细描述案件经过（时间、地点、人物、原因、经过、结果）... "
        }
        infoBinding.piTitle.ivBack.setOnClickListener {
            val intent = Intent()
            setResult(RESULT_CANCELED, intent)
            finish()
        }
        infoBinding.btnCommit.setOnClickListener {
            val intent = Intent()
            intent.putExtra("disc", infoBinding.edDescribe.text.toString())
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}