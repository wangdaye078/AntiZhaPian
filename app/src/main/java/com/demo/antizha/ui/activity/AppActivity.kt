package com.demo.antizha.ui.activity

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.demo.antizha.R
import com.demo.antizha.adapter.AppDeleteAdapter
import com.demo.antizha.databinding.ActivityAudioBinding
import com.demo.antizha.util.AppUtil
import com.demo.antizha.util.AppUtil.AppInfoBean


class AppActivity : BaseUploadActivity() {
    private lateinit var infoBinding: ActivityAudioBinding
    private var mAppBeans: ArrayList<AppInfoBean> = ArrayList()
    private var mAppIds: ArrayList<Int> = ArrayList()
    private val mMaxSelectNum = 2
    private lateinit var startApp: ActivityResultLauncher<Intent>
    private lateinit var mAdapter: AppDeleteAdapter

    override fun initPage() {
        infoBinding = ActivityAudioBinding.inflate(layoutInflater)
        setContentView(infoBinding.root)
        infoBinding.piTitle.tvTitle.text = "添加APP应用程序"
        infoBinding.lySelect.tvSelectTip.text = "添加"
        infoBinding.lyComplete.tvCommitTip.text = "最多可选择${mMaxSelectNum}个APP应用程序"
        AppUtil.checkPermission(this, true)
        getIntentData()
        val lyManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        infoBinding.recyclerview.layoutManager = lyManager
        mAdapter =
            AppDeleteAdapter(R.layout.recyclerview_app_record_select, mAppBeans, mUploadStateList)
        infoBinding.recyclerview.adapter = mAdapter
        infoBinding.recyclerview.itemAnimator = null
        mAdapter.setOnItemChildClickListener(OnItemChildClickListener { adapter, view, position ->
            val id: Int = view.id
            if (id == R.id.iv_clear) {
                mAppBeans.removeAt(position)
                mAppIds.removeAt(position)
                mUploadStateList.removeAt(position)
                adapter.notifyItemRemoved(position)
                if (mAppBeans.size == 0)
                    infoBinding.lyComplete.btnCommit.text = "确定"
            }
        })
        initActivityResultLauncher()
        infoBinding.piTitle.ivBack.setOnClickListener {
            val intent = Intent()
            setResult(RESULT_OK, intent)
            intent.putParcelableArrayListExtra("apps", mAppBeans)
            finish()
        }
        infoBinding.lySelect.llSelect.setOnClickListener {
            if (!AppUtil.checkPermission(this, true))
                return@setOnClickListener
            val intent = Intent(this, AppSelectedActivity::class.java)
            intent.putExtra(AppSelectedActivity.SELECT_TYPE, 2)
            intent.putExtra(AppSelectedActivity.SELECT_CURRENT, mAppBeans.size)
            intent.putExtra(AppSelectedActivity.SELECT_MAX, mMaxSelectNum)
            startApp.launch(intent)
        }
    }

    private fun getIntentData() {
        val list = intent.getParcelableArrayListExtra<AppInfoBean>("apps")
        if (list != null) {
            mAppBeans.addAll(list)
            for (app in mAppBeans) {
                mAppIds.add(app.id)
                mUploadStateList.add(UploadStateInfo())
            }
        }
        if (mAppBeans.size > 0)
            infoBinding.lyComplete.btnCommit.text = "文件上传"
    }

    private fun initActivityResultLauncher() {
        startApp =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode != Activity.RESULT_OK)
                    return@registerForActivityResult
                if (result.data == null)
                    return@registerForActivityResult
                val array: ArrayList<AppInfoBean> =
                    result.data!!.getParcelableArrayListExtra("app") ?: return@registerForActivityResult
                val count = mAppBeans.size
                var insertCount = 0
                for (app in array) {
                    if (!mAppIds.contains(app.id)) {
                        mAppBeans.add(app)
                        mAppIds.add(app.id)
                        insertCount++
                        mUploadStateList.add(UploadStateInfo())
                    }
                }
                mAdapter.notifyItemRangeInserted(count, insertCount)
                if (mAppBeans.size > 0)
                    infoBinding.lyComplete.btnCommit.text = "文件上传"

            }
    }
}