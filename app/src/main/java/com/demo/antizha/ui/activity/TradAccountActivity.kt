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
import com.demo.antizha.adapter.SocialAccAdapter
import com.demo.antizha.adapter.SocialAccBean
import com.demo.antizha.databinding.ActivitySocialAccountBinding

class TradAccountActivity : BaseActivity() {
    private lateinit var infoBinding: ActivitySocialAccountBinding
    private var dealAccounts: ArrayList<SocialAccBean> = ArrayList()
    private lateinit var socialAccAdapter: SocialAccAdapter
    private lateinit var startEdit: ActivityResultLauncher<Intent>

    override fun initPage() {
        infoBinding = ActivitySocialAccountBinding.inflate(layoutInflater)
        setContentView(infoBinding.root)
        infoBinding.piTitle.tvTitle.text = "添加诈骗交易账户"
        infoBinding.tvSelectTip.text = "添加"
        infoBinding.lyComplete.tvCommitTip.text = "提示：最多可上传20条交易账户"
        infoBinding.lyComplete.btnCommit.text = "确定"
        getIntentData()
        infoBinding.recyclerview.layoutManager = LinearLayoutManager(this,
            RecyclerView.VERTICAL, false)
        socialAccAdapter = SocialAccAdapter(R.layout.item_social_acc, dealAccounts)
        infoBinding.recyclerview.adapter = socialAccAdapter
        infoBinding.recyclerview.itemAnimator = null
        socialAccAdapter.setOnItemChildClickListener(OnItemChildClickListener { adapter, view, position ->
            val id: Int = view.id
            if (id == R.id.iv_delete) {
                dealAccounts.removeAt(position)
                adapter.notifyItemRemoved(position)
            } else if (id == R.id.iv_edit) {
                editAcc(position)
            }
        })
        infoBinding.piTitle.ivBack.setOnClickListener {
            setResult(RESULT_CANCELED, intent)
            finish()
        }
        infoBinding.llSelect.setOnClickListener {
            editAcc(-1)
        }
        infoBinding.lyComplete.btnCommit.setOnClickListener {
            val intent = Intent()
            intent.putParcelableArrayListExtra("accounts", dealAccounts)
            setResult(RESULT_OK, intent)
            finish()
        }
        startEdit =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode != Activity.RESULT_OK)
                    return@registerForActivityResult
                if (result.data == null || result.data!!.extras == null)
                    return@registerForActivityResult
                val tag = result.data!!.extras!!.getInt("tag")
                val socialAccount = result.data!!.getParcelableExtra<SocialAccBean>("account")!!
                if (tag == -1) {
                    dealAccounts.add(socialAccount)
                    socialAccAdapter.notifyItemInserted(dealAccounts.size - 1)
                }
                else {
                    dealAccounts[tag] = socialAccount
                    socialAccAdapter.notifyItemChanged(tag)
                }
            }
    }

    private fun getIntentData() {
        val list = intent.getParcelableArrayListExtra<SocialAccBean>("accounts")
        if (list != null) {
            dealAccounts.addAll(list)
        }
    }

    private fun editAcc(i: Int) {
        val intent = Intent(mActivity, TradAccountEditActivity::class.java)
        intent.putExtra("pos", i)
        if (i != -1) {
            intent.putExtra("acc", dealAccounts[i])
        }
        startEdit.launch(intent)
    }
}