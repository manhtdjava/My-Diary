package com.dailyjournal.diaryapp.secretdiary.ui.welcome

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.amazic.ads.service.AdmobApi
import com.amazic.ads.util.AdsConsentManager
import com.amazic.ads.util.manager.native_ad.NativeBuilder
import com.amazic.ads.util.manager.native_ad.NativeManager
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.ads.InterAdHelper
import com.dailyjournal.diaryapp.secretdiary.base.BaseActivity
import com.dailyjournal.diaryapp.secretdiary.databinding.ActivityWelcomeBinding
import com.dailyjournal.diaryapp.secretdiary.logenven.LogEven
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefRemote
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefUtils
import com.dailyjournal.diaryapp.secretdiary.ui.theme.ChooseYourThemeActivity
import com.dailyjournal.diaryapp.secretdiary.utils.helper.Contants
import com.dailyjournal.diaryapp.secretdiary.widget.tap


class WelcomeActivity : BaseActivity<ActivityWelcomeBinding>() {


    override fun setViewBinding(): ActivityWelcomeBinding {
        return ActivityWelcomeBinding.inflate(layoutInflater)
    }

    override fun initView() {
        loadNative()
    }

    override fun viewListener() {
        binding.apply {
            tvConfirm.tap {
                LogEven.logEvent(this@WelcomeActivity, "confirm_welcome_click", Bundle())
                if (binding.edt.text.toString().trim().isNotEmpty()) {
                    showLoading()
                    if (SharePrefRemote.get_config(
                            this@WelcomeActivity, SharePrefRemote.inter_welcome
                        ) && InterAdHelper.canShowNextAd(this@WelcomeActivity) && AdsConsentManager.getConsentResult(
                            this@WelcomeActivity
                        )
                    ) {
                            InterAdHelper.showListInterAd(
                                this@WelcomeActivity, SharePrefRemote.get_config(
                                    this@WelcomeActivity, SharePrefRemote.inter_welcome
                                ), AdmobApi.getInstance().getListIDByName("inter_permission")
                            ) {
                                SharePrefUtils.setUser(
                                    this@WelcomeActivity, binding.edt.text.toString().trim()
                                )
                                showActivity(ChooseYourThemeActivity::class.java)
                                finishAffinity()
                                hideLoading()
                            }
                    } else {
                        SharePrefUtils.setUser(
                            this@WelcomeActivity, binding.edt.text.toString().trim()
                        )
                        showActivity(ChooseYourThemeActivity::class.java)
                        finishAffinity()
                        hideLoading()
                    }
                } else {
                    Toast.makeText(
                        this@WelcomeActivity,
                        getString(R.string.please_enter_your_name),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
            tvSkip.tap {
                LogEven.logEvent(this@WelcomeActivity, "skip_welcome_click", Bundle())
                showLoading()
                if (SharePrefRemote.get_config(
                        this@WelcomeActivity, SharePrefRemote.inter_welcome
                    ) && InterAdHelper.canShowNextAd(this@WelcomeActivity) && AdsConsentManager.getConsentResult(
                        this@WelcomeActivity
                    )
                ) {
                    InterAdHelper.showListInterAd(
                        this@WelcomeActivity, SharePrefRemote.get_config(
                            this@WelcomeActivity, SharePrefRemote.inter_welcome
                        ), AdmobApi.getInstance().getListIDByName("inter_permission")
                    ) {
                        showActivity(ChooseYourThemeActivity::class.java)
                        finishAffinity()
                        hideLoading()
                    }
                } else {
                    showActivity(ChooseYourThemeActivity::class.java)
                    finishAffinity()
                    hideLoading()
                }
            }
        }

    }

    override fun dataObservable() {
    }

    override fun onPermissionGranted() {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    private fun loadNative() {
        try {
            if (SharePrefRemote.get_config(
                    this, SharePrefRemote.native_welcome
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
                nativeBuilder.setListIdAd(AdmobApi.getInstance().getListIDByName("native_permission"))
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