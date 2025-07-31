package com.dailyjournal.diaryapp.secretdiary.ui.welcome

import android.view.LayoutInflater
import android.view.View
import com.amazic.ads.service.AdmobApi
import com.amazic.ads.util.AdsConsentManager
import com.amazic.ads.util.manager.native_ad.NativeBuilder
import com.amazic.ads.util.manager.native_ad.NativeManager
import com.amazic.ads.util.manager.open_app.OpenAppBuilder
import com.amazic.ads.util.manager.open_app.OpenAppCallback
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.base.BaseActivity
import com.dailyjournal.diaryapp.secretdiary.databinding.ActivityWelcomeBackBinding
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefRemote
import com.dailyjournal.diaryapp.secretdiary.utils.helper.Contants
import com.dailyjournal.diaryapp.secretdiary.widget.tap

class WelcomeBackActivity : BaseActivity<ActivityWelcomeBackBinding>() {
    override fun setViewBinding(): ActivityWelcomeBackBinding {
        return ActivityWelcomeBackBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        loadNative()
    }

    override fun onBackPressed() {

        //  super.onBackPressed()
    }

    override fun viewListener() {
        val builder =
            OpenAppBuilder(this).setId(AdmobApi.getInstance().getListIDByName("appopen_resume"))
                .setCallback(object : OpenAppCallback() {
                    override fun onNextAction() {
                        super.onNextAction()
                        finish()
                    }

                    override fun onAdLoaded() {
                        super.onAdLoaded()
                    }
                })

//        val adOpenAppManager = AdOpenAppManager()
//        adOpenAppManager.setBuilder(builder)
//        adOpenAppManager.loadAd()
        binding.tvContinueWelcome.tap {
            //adOpenAppManager.showAd(this);
            finish();
        }
    }

    override fun dataObservable() {

    }


    private fun loadNative() {
        try {
            if (SharePrefRemote.get_config(
                    this, SharePrefRemote.native_resume
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
                nativeBuilder.setListIdAd(AdmobApi.getInstance().getListIDByName("native_resume"))
                val nativeManager = NativeManager(
                    this, this, nativeBuilder
                ).setIntervalReloadNative(Contants.interval_reload_native)
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