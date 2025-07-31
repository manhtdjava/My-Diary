package com.dailyjournal.diaryapp.secretdiary.ui.splash

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.amazic.ads.callback.AdCallback
import com.amazic.ads.callback.ApiCallBack
import com.amazic.ads.callback.InterCallback
import com.amazic.ads.service.AdmobApi
import com.amazic.ads.util.Admob
import com.amazic.ads.util.AdsConsentManager
import com.amazic.ads.util.AdsSplash
import com.amazic.ads.util.AppOpenManager
import com.applovin.sdk.AppLovinPrivacySettings
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.ads.InterAdHelper
import com.dailyjournal.diaryapp.secretdiary.base.BaseActivity
import com.dailyjournal.diaryapp.secretdiary.databinding.ActivitySplashBinding
import com.dailyjournal.diaryapp.secretdiary.logenven.LogEven
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.Common
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefRemote
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefUtils
import com.dailyjournal.diaryapp.secretdiary.ui.language_start.LanguageStartActivity
import com.dailyjournal.diaryapp.secretdiary.ui.welcome.WelcomeBackActivity
import com.dailyjournal.diaryapp.secretdiary.utils.helper.Contants
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.ironsource.mediationsdk.IronSource
import com.mbridge.msdk.MBridgeConstans
import com.mbridge.msdk.out.MBridgeSDKFactory
import com.unity3d.ads.metadata.MetaData
import com.vungle.ads.VunglePrivacySettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    private val croutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var adsSplash: AdsSplash? = null
    private var adCallback: AdCallback? = null
    private var interCallback: InterCallback? = null

    override fun setViewBinding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(LayoutInflater.from(this))
    }

    private val back: Boolean = true
    override fun initView() {
        SharePrefUtils.setCheckAdsBG(this, false)
        SharePrefUtils.setCheckAdsIMG(this, false)
        SharePrefUtils.setCheckAdsTAG(this, false)
        SharePrefUtils.setCheckAdsTEXT(this, false)
        SharePrefUtils.setCheckAdsMusic(this, false)
        SharePrefUtils.setCheckAdsRecord(this, false)
        SharePrefUtils.setCheckAdsEMOIJ(this, false)
        InterAdHelper.openAppTime = System.currentTimeMillis()
        SharePrefUtils.setFirstT(this)
        LogEven.logEvent(this, "splash_open", Bundle())
        if (!isNetworkAvailable()) {
            LogEven.logEvent(this, "splash_have_internet", Bundle())
        }
        val splashCount = SharePrefUtils.getSplashOpenCount(this)
        if (splashCount <= 10) {
            val eventName = "splash_open_${splashCount + 1}"
            LogEven.logEvent(this, eventName, Bundle())
            SharePrefUtils.incrementSplashOpenCount(this)
        }
        Admob.getInstance().setOpenShowAllAds(false)
        Common.initRemoteConfig { task ->
            if (task.isSuccessful) {
                val updated = task.result as Boolean
                if (updated) {
                    setupConfigRemote()
                }
                checkUMP()
            } else {
                checkUMP()
            }
        }
    }

    override fun viewListener() {

    }

    override fun dataObservable() {

    }


    override fun onBackPressed() {
        if (back) {
            return
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        croutineScope.cancel()
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    private fun checkUMP() {
        val adsConsentManager = AdsConsentManager(this)
        adsConsentManager.requestUMP { b ->
            if (b) {
                AppOpenManager.getInstance().initApi(application);
                AppOpenManager.getInstance()
                    .disableAppResumeWithActivity(SplashActivity::class.java)
            }
            handleOpenApp()
            initMediation(b)
        }

    }

    private fun handleOpenApp() {
        Admob.getInstance().setOpenShowAllAds(
            SharePrefRemote.get_config(
                this@SplashActivity, SharePrefRemote.show_ads
            )
        )
        Admob.getInstance().setTimeInterval(
            SharePrefRemote.get_config_long(
                this@SplashActivity, SharePrefRemote.interval_between_interstitial
            ) * 1000
        )

        AdmobApi.getInstance().init(this, null, getString(R.string.app_id), object : ApiCallBack() {
            override fun onReady() {
                super.onReady()
                if (AdsConsentManager.getConsentResult(this@SplashActivity)) {
                    if (SharePrefRemote.get_config(
                            this@SplashActivity, SharePrefRemote.banner_splash
                        ) && AdsConsentManager.getConsentResult(this@SplashActivity)
                    ) {
                        Admob.getInstance().loadBannerFloor(
                            this@SplashActivity, AdmobApi.getInstance().getListIDByName("banner_splash")
                        )
                        binding.include.visibility = View.VISIBLE
                    } else {
                        binding.include.visibility = View.GONE
                    }
                    if (SharePrefRemote.get_config(
                            this@SplashActivity, SharePrefRemote.appopen_resume
                        )
                    ) {
                        val adIds = AdmobApi.getInstance().getListIDByName("appopen_resume")
                        if (adIds.isNotEmpty()) {
                            AppOpenManager.getInstance()
                                .init(application, adIds.first())
                            AppOpenManager.getInstance().initWelcomeBackActivity(
                                application, WelcomeBackActivity::class.java, true
                            )
                        }
                    } else {
                        AppOpenManager.getInstance().initWelcomeBackActivity(
                            application, WelcomeBackActivity::class.java, false
                        )
                    }
                }
                api()
            }
        })

    }

    private fun api() {
        adCallback = object : AdCallback() {
            override fun onNextAction() {
                super.onNextAction()
                startIntro()
            }

            override fun onAdFailedToLoad(i: LoadAdError?) {
                super.onAdFailedToLoad(i)
                startIntro()
            }

            override fun onAdFailedToShow(adError: AdError?) {
                super.onAdFailedToShow(adError)
                startIntro()
            }
        }

        interCallback = object : InterCallback() {
            override fun onNextAction() {
                super.onNextAction()
                startIntro()
            }
        }
        if (AdsConsentManager.getConsentResult(this@SplashActivity)) {
            adsSplash = AdsSplash.init(
                SharePrefRemote.get_config(
                    this@SplashActivity, SharePrefRemote.inter_splash
                ), SharePrefRemote.get_config(
                    this@SplashActivity, SharePrefRemote.open_splash
                ), SharePrefRemote.get_config_string(
                    this@SplashActivity, SharePrefRemote.rate_aoa_inter_splash
                )
            )
            adsSplash?.showAdsSplashApi(
                this@SplashActivity, adCallback, interCallback
            )
        } else {
            croutineScope.launch {
                delay(3000)
                startIntro()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity::class.java)
        if (adsSplash != null && AdsConsentManager.getConsentResult(this@SplashActivity)) {
            adsSplash?.onCheckShowSplashWhenFail(this, adCallback, interCallback)
        }
    }

    private fun setupConfigRemote() {
        SharePrefRemote.set_config(
            this, SharePrefRemote.show_ads, Common.getRemoteConfigBoolean("show_ads")
        )
        SharePrefRemote.set_config(
            this, SharePrefRemote.banner_splash, Common.getRemoteConfigBoolean("banner_splash")
        )
        SharePrefRemote.set_config(
            this, SharePrefRemote.open_splash, Common.getRemoteConfigBoolean("open_splash")
        )
        SharePrefRemote.set_config(
            this, SharePrefRemote.inter_splash, Common.getRemoteConfigBoolean("inter_splash")
        )
        SharePrefRemote.set_config(
            this, SharePrefRemote.native_language, Common.getRemoteConfigBoolean("native_language")
        )
        SharePrefRemote.set_config(
            this, SharePrefRemote.native_intro, Common.getRemoteConfigBoolean("native_intro")
        )
        SharePrefRemote.set_config(
            this,
            SharePrefRemote.native_intro_full,
            Common.getRemoteConfigBoolean("native_intro_full")
        )
        SharePrefRemote.set_config(
            this,
            SharePrefRemote.native_intro_full2,
            Common.getRemoteConfigBoolean("native_intro_full2")
        )
        SharePrefRemote.set_config(
            this, SharePrefRemote.inter_intro, Common.getRemoteConfigBoolean("inter_intro")
        )
        SharePrefRemote.set_config(
            this,
            SharePrefRemote.native_permission,
            Common.getRemoteConfigBoolean("native_permission")
        )
        SharePrefRemote.set_config(
            this,
            SharePrefRemote.inter_permission,
            Common.getRemoteConfigBoolean("inter_permission")
        )
        SharePrefRemote.set_config(
            this, SharePrefRemote.native_welcome, Common.getRemoteConfigBoolean("native_welcome")
        )
        SharePrefRemote.set_config(
            this, SharePrefRemote.inter_welcome, Common.getRemoteConfigBoolean("inter_welcome")
        )
        SharePrefRemote.set_config(
            this, SharePrefRemote.native_password, Common.getRemoteConfigBoolean("native_password")
        )
        SharePrefRemote.set_config(
            this,
            SharePrefRemote.collapse_banner_password,
            Common.getRemoteConfigBoolean("collapse_banner_password")
        )
        SharePrefRemote.set_config(
            this, SharePrefRemote.inter_password, Common.getRemoteConfigBoolean("inter_password")
        )
        SharePrefRemote.set_config(
            this, SharePrefRemote.appopen_resume, Common.getRemoteConfigBoolean("appopen_resume")
        )
        SharePrefRemote.set_config(
            this, SharePrefRemote.native_resume, Common.getRemoteConfigBoolean("native_resume")
        )
        SharePrefRemote.set_config(
            this, SharePrefRemote.banner_all, Common.getRemoteConfigBoolean("banner_all")
        )
        SharePrefRemote.set_config(
            this, SharePrefRemote.native_popup, Common.getRemoteConfigBoolean("native_popup")
        )
        SharePrefRemote.set_config(
            this, SharePrefRemote.native_mine, Common.getRemoteConfigBoolean("native_mine")
        )
        SharePrefRemote.set_config(
            this, SharePrefRemote.inter_mine, Common.getRemoteConfigBoolean("inter_mine")
        )
        SharePrefRemote.set_config(
            this,
            SharePrefRemote.collapse_banner_home,
            Common.getRemoteConfigBoolean("collapse_banner_home")
        )
        SharePrefRemote.set_config(
            this,
            SharePrefRemote.collapse_banner_edit,
            Common.getRemoteConfigBoolean("collapse_banner_edit")
        )
        SharePrefRemote.set_config(
            this, SharePrefRemote.native_home, Common.getRemoteConfigBoolean("native_home")
        )
        SharePrefRemote.set_config(
            this, SharePrefRemote.inter_create, Common.getRemoteConfigBoolean("inter_create")
        )
        SharePrefRemote.set_config(
            this, SharePrefRemote.inter_preview, Common.getRemoteConfigBoolean("inter_preview")
        )
        SharePrefRemote.set_config(
            this,
            SharePrefRemote.native_view_edit,
            Common.getRemoteConfigBoolean("native_view_edit")
        )
        SharePrefRemote.set_config(
            this, SharePrefRemote.rewarded_edit, Common.getRemoteConfigBoolean("rewarded_edit")
        )
        SharePrefRemote.set_config(
            this,
            SharePrefRemote.rewarded_sound_photo,
            Common.getRemoteConfigBoolean("rewarded_sound_photo")
        )
        SharePrefRemote.set_config(
            this, SharePrefRemote.inter_save, Common.getRemoteConfigBoolean("inter_save")
        )
        SharePrefRemote.set_config(
            this, SharePrefRemote.inter_libruary, Common.getRemoteConfigBoolean("inter_libruary")
        )
        SharePrefRemote.set_config(
            this, SharePrefRemote.native_libruary, Common.getRemoteConfigBoolean("native_libruary")
        )
        SharePrefRemote.set_config_string(
            this,
            SharePrefRemote.rate_aoa_inter_splash,
            Common.getRemoteConfigString("rate_aoa_inter_splash")
        )
        SharePrefRemote.set_config_long(
            this,
            SharePrefRemote.interval_reload_native,
            Common.getRemoteConfigLong("interval_reload_native")
        )
        SharePrefRemote.set_config_long(
            this,
            SharePrefRemote.collap_reload_interval,
            Common.getRemoteConfigLong("collap_reload_interval")
        )
        SharePrefRemote.set_config_long(
            this,
            SharePrefRemote.interval_interstitial_from_start,
            Common.getRemoteConfigLong("interval_interstitial_from_start")
        )
        SharePrefRemote.set_config_long(
            this,
            SharePrefRemote.interval_between_interstitial,
            Common.getRemoteConfigLong("interval_between_interstitial")
        )
        Log.d(
            "duyhung99",
            "setupConfigRemote: " + SharePrefRemote.get_config_long(
                this,
                SharePrefRemote.interval_between_interstitial
            ) * 1000
        )
        Contants.collap_reload_interval =
            if (Common.getRemoteConfigLong("collap_reload_interval") * 1000L != 0L) {
                Common.getRemoteConfigLong(
                    "collap_reload_interval"
                ) * 1000L
            } else 100000 * 1000L
        Contants.interval_reload_native =
            if (Common.getRemoteConfigLong("interval_reload_native") * 1000L != 0L) {
                Common.getRemoteConfigLong(
                    "interval_reload_native"
                ) * 1000L
            } else
                100000 * 1000
    }

    private fun startIntro() {
        showActivity(LanguageStartActivity::class.java)
        finish()
    }

    private fun initMediation(canRequestAds: Boolean) {
        VunglePrivacySettings.setGDPRStatus(canRequestAds, null)
        AppLovinPrivacySettings.setHasUserConsent(canRequestAds, this)
        IronSource.setConsent(canRequestAds)
        MBridgeSDKFactory.getMBridgeSDK().setConsentStatus(
            this, if (canRequestAds) MBridgeConstans.IS_SWITCH_ON else MBridgeConstans.IS_SWITCH_OFF
        )
        val gdprMetaData = MetaData(this)
        gdprMetaData["gdpr.consent"] = canRequestAds
        gdprMetaData.commit()
    }
}