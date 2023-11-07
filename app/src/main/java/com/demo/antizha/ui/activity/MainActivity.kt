package com.demo.antizha.ui.activity


import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.demo.antizha.Dp2Px
import com.demo.antizha.R
import com.demo.antizha.databinding.ActivityMainBinding
import com.demo.antizha.dp2px
import com.demo.antizha.ui.HiCore
import com.demo.antizha.ui.RefreshUIEvent
import com.demo.antizha.ui.fragment.home.HomeFragment
import com.demo.antizha.ui.fragment.mine.MineFragment
import com.demo.antizha.ui.fragment.web.WebFragment
import com.demo.antizha.ui.view.BottomBar
import com.demo.antizha.util.LogUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import qiu.niorgai.StatusBarCompat


class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private var mTabs = ArrayList<Fragment>()
    private val mHomeFragment = HomeFragment()
    private val mWebFragment = WebFragment()
    private val mMineFragment = MineFragment()

    companion object {
        const val SPLASH_TIME: Long = 4000
    }

    private fun initTabIndicators() {
        binding.bottomBar.initTabs()
        binding.bottomBar.setCurrentItem(0)
    }

    private fun initViewPager() {
        mTabs.add(mHomeFragment)
        mTabs.add(mWebFragment)
        mTabs.add(mMineFragment)
        binding.viewpager.isUserInputEnabled = false
        binding.viewpager.offscreenPageLimit = 3
        binding.viewpager.setAdapter(object : FragmentStateAdapter(this@MainActivity) {
            override fun getItemCount(): Int = mTabs.size
            override fun createFragment(position: Int): Fragment = mTabs[position]
        })
        StatusBarCompat.translucentStatusBar(this, true, true)
        binding.bottomBar.setOnClickItemMenu(object : BottomBar.OnClickItemMenu {
            override fun onClickItem(nowPosition: Int, position: Int) {
                onItemMenuClick(nowPosition, position)
            }
        })
    }

    fun onItemMenuClick(nowPosition: Int, position: Int) {
        if (nowPosition == 0) {
            StatusBarCompat.translucentStatusBar(this, true, true)
            binding.viewpager.setCurrentItem(0, false)
        } else if (nowPosition == 1) {
            binding.viewpager.setCurrentItem(1, false)
            StatusBarCompat.translucentStatusBar(this, true, true)
        } else if (nowPosition == 2) {
            binding.viewpager.setCurrentItem(2, false)
            StatusBarCompat.translucentStatusBar(this, true, false)
        }
    }

    override fun initPage() {
        dp2px = Dp2Px(HiCore.context)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViewPager()
        initTabIndicators()
        EventBus.getDefault().register(this)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    private fun intentHome() {
        val intent = Intent("android.intent.action.MAIN")
        intent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND)
        intent.addCategory("android.intent.category.HOME")
        startActivity(intent)
    }

    override fun onBackPressed() {
        if (binding.viewpager.currentItem != 1) {
            intentHome()
        } else if (!this.mWebFragment.goBack()) {
            intentHome()
        }
    }

    fun setCurrentPage(i: Int) {
        binding.bottomBar.setCurrentItem(i)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: RefreshUIEvent) {
        when (event.msgCode) {
            RefreshUIEvent.SELECT_WEB_FRAGMENT -> {
                setCurrentPage(1)
            }
        }
    }
}
