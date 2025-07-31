package com.dailyjournal.diaryapp.secretdiary.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.amazic.ads.service.AdmobApi
import com.amazic.ads.util.Admob
import com.amazic.ads.util.AdsConsentManager
import com.amazic.ads.util.BannerGravity
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.ads.InterAdHelper
import com.dailyjournal.diaryapp.secretdiary.base.BaseActivity
import com.dailyjournal.diaryapp.secretdiary.databinding.ActivityMainBinding
import com.dailyjournal.diaryapp.secretdiary.logenven.LogEven
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefRemote
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefUtils
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.CreateNewActivity
import com.dailyjournal.diaryapp.secretdiary.ui.mine.MineActivity
import com.dailyjournal.diaryapp.secretdiary.utils.helper.Contants
import com.dailyjournal.diaryapp.secretdiary.widget.tap

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun setViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(LayoutInflater.from(this))
    }

    private var launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        loadCollapse()
    }

    override fun initView() {
        loadCollapse()
        binding.root.setBackgroundResource(getBackground(SharePrefUtils.getTheme(this)))
        if (SharePrefUtils.getTheme(this) == 2 || SharePrefUtils.getTheme(this) == 3) {
            binding.linearLayout2.setBackgroundResource(R.drawable.navigation_dark)
        } else {
            binding.linearLayout2.setBackgroundResource(R.drawable.navigation)
        }
        setColorTab(0)
        val pagerAdapter = HomeAdapter(supportFragmentManager)
        binding.viewPager.adapter = pagerAdapter
    }

    override fun viewListener() {
        binding.llHome.tap {
            LogEven.logEvent(this, "home_click", Bundle())
            binding.viewPager.currentItem = 0
            setColorTab(0)
            Admob.getInstance().loadCollapsibleBannerFloor(
                this, AdmobApi.getInstance().getListIDByName("collapse_banner_home"), BannerGravity.bottom
            )
        }
        binding.llCalender.tap {
            LogEven.logEvent(this, "calender_click", Bundle())
            binding.viewPager.currentItem = 1
            setColorTab(1)
            Admob.getInstance().loadCollapsibleBannerFloor(
                this, AdmobApi.getInstance().getListIDByName("collapse_banner_home"), BannerGravity.bottom
            )

        }
        binding.llPhoto.tap {
            LogEven.logEvent(this, "photo_click", Bundle())
            binding.viewPager.currentItem = 2
            setColorTab(2)
            Admob.getInstance().loadCollapsibleBannerFloor(
                this, AdmobApi.getInstance().getListIDByName("collapse_banner_home"), BannerGravity.bottom
            )

        }
        binding.llMine.tap {
            LogEven.logEvent(this, "setting_click", Bundle())
            launcher.launch(Intent(this@MainActivity, MineActivity::class.java))
            //scheduleNotification()
        }
        binding.ivAdd.tap {
            LogEven.logEvent(this, "add_click", Bundle())
            showLoading()
            if (SharePrefRemote.get_config(
                    this@MainActivity, SharePrefRemote.inter_create
                ) && InterAdHelper.canShowNextAd(this) && AdsConsentManager.getConsentResult(this)
            ) {
                InterAdHelper.showListInterAd(
                    this@MainActivity, SharePrefRemote.get_config(
                        this@MainActivity, SharePrefRemote.inter_create
                    ), AdmobApi.getInstance().getListIDByName("inter_create")
                ) {
                    launcher.launch(Intent(this@MainActivity, CreateNewActivity::class.java))
                    hideLoading()
                }
            } else {
                launcher.launch(Intent(this@MainActivity, CreateNewActivity::class.java))
                hideLoading()
            }
        }
    }

    override fun dataObservable() {

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun setColorTab(i: Int) {
        when (i) {
            0 -> {
                binding.ivHome.setImageDrawable(getDrawable(R.drawable.ic_home_v1))
                binding.ivCalender.setImageDrawable(getDrawable(R.drawable.ic_calender_v2))
                binding.ivPhoto.setImageDrawable(getDrawable(R.drawable.ic_photo_2))
                binding.ivMine.setImageDrawable(getDrawable(R.drawable.ic_mine_2))

                binding.tvHome.setTextColor(getColor(R.color.color_app))
                binding.tvCalender.setTextColor(getColor(R.color.color_B8BBBB))
                binding.tvPhoto.setTextColor(getColor(R.color.color_B8BBBB))
                binding.tvMine.setTextColor(getColor(R.color.color_B8BBBB))
            }

            1 -> {
                binding.ivHome.setImageDrawable(getDrawable(R.drawable.ic_home_v2))
                binding.ivCalender.setImageDrawable(getDrawable(R.drawable.ic_calender_v1))
                binding.ivPhoto.setImageDrawable(getDrawable(R.drawable.ic_photo_2))
                binding.ivMine.setImageDrawable(getDrawable(R.drawable.ic_mine_2))

                binding.tvHome.setTextColor(getColor(R.color.color_B8BBBB))
                binding.tvCalender.setTextColor(getColor(R.color.color_app))
                binding.tvPhoto.setTextColor(getColor(R.color.color_B8BBBB))
                binding.tvMine.setTextColor(getColor(R.color.color_B8BBBB))
            }

            2 -> {
                binding.ivHome.setImageDrawable(getDrawable(R.drawable.ic_home_v2))
                binding.ivCalender.setImageDrawable(getDrawable(R.drawable.ic_calender_v2))
                binding.ivPhoto.setImageDrawable(getDrawable(R.drawable.ic_photo_1))
                binding.ivMine.setImageDrawable(getDrawable(R.drawable.ic_mine_2))

                binding.tvHome.setTextColor(getColor(R.color.color_B8BBBB))
                binding.tvCalender.setTextColor(getColor(R.color.color_B8BBBB))
                binding.tvPhoto.setTextColor(getColor(R.color.color_app))
                binding.tvMine.setTextColor(getColor(R.color.color_B8BBBB))
            }

            3 -> {
                binding.ivHome.setImageDrawable(getDrawable(R.drawable.ic_home_v2))
                binding.ivCalender.setImageDrawable(getDrawable(R.drawable.ic_calender_v2))
                binding.ivPhoto.setImageDrawable(getDrawable(R.drawable.ic_photo_2))
                binding.ivMine.setImageDrawable(getDrawable(R.drawable.ic_mine_1))

                binding.tvHome.setTextColor(getColor(R.color.color_B8BBBB))
                binding.tvCalender.setTextColor(getColor(R.color.color_B8BBBB))
                binding.tvPhoto.setTextColor(getColor(R.color.color_B8BBBB))
                binding.tvMine.setTextColor(getColor(R.color.color_app))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (handler != null && ::runnable.isInitialized) handler.removeCallbacks(runnable)
    }

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private fun loadCollapse() {
        if (SharePrefRemote.get_config(
                this, SharePrefRemote.collapse_banner_home
            ) && AdsConsentManager.getConsentResult(this)
        ) {

            runnable = Runnable {
                Admob.getInstance().loadCollapsibleBannerFloor(
                    this, AdmobApi.getInstance().getListIDByName("collapse_banner_home"), BannerGravity.bottom
                )
                binding.include.visibility = View.VISIBLE
                handler.postDelayed(runnable, Contants.collap_reload_interval)
            }
            handler.post(runnable)

        } else {
            binding.include.visibility = View.GONE
        }
    }

}