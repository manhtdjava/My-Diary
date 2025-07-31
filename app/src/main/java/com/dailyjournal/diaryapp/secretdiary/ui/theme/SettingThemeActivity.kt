package com.dailyjournal.diaryapp.secretdiary.ui.theme

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.amazic.ads.service.AdmobApi
import com.amazic.ads.util.AdsConsentManager
import com.amazic.ads.util.manager.native_ad.NativeBuilder
import com.amazic.ads.util.manager.native_ad.NativeManager
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.base.BaseActivity
import com.dailyjournal.diaryapp.secretdiary.databinding.ActivitySettingThemeBinding
import com.dailyjournal.diaryapp.secretdiary.logenven.LogEven
import com.dailyjournal.diaryapp.secretdiary.model.ThemeModel
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefRemote
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefUtils
import com.dailyjournal.diaryapp.secretdiary.ui.home.MainActivity
import com.dailyjournal.diaryapp.secretdiary.utils.helper.Contants
import com.dailyjournal.diaryapp.secretdiary.widget.tap


class SettingThemeActivity : BaseActivity<ActivitySettingThemeBinding>() {
    private lateinit var listTheme: ArrayList<ThemeModel>
    private lateinit var adapter: SettingThemeAdapter


    override fun setViewBinding(): ActivitySettingThemeBinding {
        return ActivitySettingThemeBinding.inflate(layoutInflater)
    }

    override fun initView() {
        loadNative()
        listTheme = ArrayList()
        listTheme.add(ThemeModel(R.drawable.img_choose_theme_0, 0))
        listTheme.add(ThemeModel(R.drawable.img_choose_theme_1, 1))
        listTheme.add(ThemeModel(R.drawable.img_choose_theme_2, 2))
        listTheme.add(ThemeModel(R.drawable.img_choose_theme_3, 3))
        listTheme.add(ThemeModel(R.drawable.img_choose_theme_4, 4))
        listTheme.add(ThemeModel(R.drawable.img_choose_theme_5, 5))

        binding.rcv.layoutManager = GridLayoutManager(this, 2)
        adapter = SettingThemeAdapter(this) {
            SharePrefUtils.setTheme(this, it.type)
            showActivity(MainActivity::class.java)
            val bundle= Bundle()
            bundle.putInt("theme_name", it.type)
            LogEven.logEvent(this@SettingThemeActivity, "setting_theme_click", Bundle())
            finishAffinity()
        }
        binding.rcv.adapter = adapter
        adapter.addList(listTheme)


    }

    override fun viewListener() {
        binding.apply {
            ivBack.tap { finish() }
        }

    }

    override fun dataObservable() {
    }

    override fun onPermissionGranted() {

    }
    private fun loadNative() {
        try {
            if (SharePrefRemote.get_config(
                    this, SharePrefRemote.native_mine
                ) && AdsConsentManager.getConsentResult(this)
            ) {
                    binding.frAds.visibility = View.VISIBLE
                    val nativeBuilder = NativeBuilder(
                        this,
                        binding.frAds,
                        R.layout.ads_native_large_top_shimer,
                        R.layout.layout_native_large_top,
                        R.layout.layout_native_large_top
                    )
                nativeBuilder.setListIdAd(AdmobApi.getInstance().getListIDByName("native_mine"))
                    val nativeManager = NativeManager(this, this, nativeBuilder).setIntervalReloadNative(Contants.interval_reload_native)
            } else {
                binding.frAds.visibility = View.GONE
                binding.frAds.removeAllViews()
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
            binding.frAds.visibility = View.GONE
            binding.frAds.removeAllViews()
        }
    }
}