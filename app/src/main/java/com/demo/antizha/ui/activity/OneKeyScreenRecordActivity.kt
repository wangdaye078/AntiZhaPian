package com.demo.antizha.ui.activity

import android.view.View
import android.widget.RadioGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demo.antizha.R
import com.demo.antizha.databinding.ActivityOneKeyScreenRecordBinding
import qiu.niorgai.StatusBarCompat


class OneKeyScreenRecordActivity : BaseActivity() {
    private lateinit var infoBinding: ActivityOneKeyScreenRecordBinding

    override fun initPage() {
        infoBinding = ActivityOneKeyScreenRecordBinding.inflate(layoutInflater)
        setContentView(infoBinding.root)
        StatusBarCompat.translucentStatusBar(this, true, false)
        infoBinding.piTitle.tvTitle.text = "音频录制"
        infoBinding.piTitle.tvTitle.typeface = typ_ME
        infoBinding.recyclerview.layoutManager = LinearLayoutManager(this,
            RecyclerView.HORIZONTAL,
            false)
        infoBinding.chronometer.visibility = View.INVISIBLE
        infoBinding.rgMediaType.setOnCheckedChangeListener(OnCheckedChangeListener())
        infoBinding.piTitle.ivBack.setOnClickListener {
            finish()
        }
    }

    fun onMediaIsVideo() {
        infoBinding.ivAudioRecord.visibility = View.GONE
        infoBinding.ivVideoRecord.visibility = View.VISIBLE
        infoBinding.video.isChecked = true
        infoBinding.audio.isChecked = false
    }

    fun onMediaIsAudio() {
        infoBinding.ivAudioRecord.visibility = View.VISIBLE
        infoBinding.ivVideoRecord.visibility = View.GONE
        infoBinding.audio.isChecked = true
        infoBinding.video.isChecked = false
    }

    inner class OnCheckedChangeListener internal constructor() :
        RadioGroup.OnCheckedChangeListener {
        override fun onCheckedChanged(radioGroup: RadioGroup, ResId: Int) {
            if (ResId == R.id.video) {
                onMediaIsVideo()
            } else if (ResId == R.id.audio) {
                onMediaIsAudio()
            }
        }
    }

}