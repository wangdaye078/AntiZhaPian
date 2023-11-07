package com.demo.antizha.ui.activity

import com.demo.antizha.databinding.ActivityAudioBinding

class AudioActivity : BaseActivity() {
    private lateinit var infoBinding: ActivityAudioBinding

    override fun initPage() {
        infoBinding = ActivityAudioBinding.inflate(layoutInflater)
        setContentView(infoBinding.root)
        infoBinding.piTitle.tvTitle.text = "添加录音"
        infoBinding.lySelect.tvSelectTip.text = "添加"
        infoBinding.lyComplete.tvCommitTip.text = "最多可选择9条录音"
        infoBinding.piTitle.ivBack.setOnClickListener {
            finish()
        }
    }
}