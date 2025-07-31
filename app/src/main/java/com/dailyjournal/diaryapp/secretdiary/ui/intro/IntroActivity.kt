package com.dailyjournal.diaryapp.secretdiary.ui.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager.widget.ViewPager
import com.amazic.ads.service.AdmobApi
import com.amazic.ads.util.AdsConsentManager
import com.amazic.ads.util.manager.native_ad.NativeBuilder
import com.amazic.ads.util.manager.native_ad.NativeManager
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.ads.InterAdHelper
import com.dailyjournal.diaryapp.secretdiary.base.BaseActivity
import com.dailyjournal.diaryapp.secretdiary.databinding.ActivityIntroBinding
import com.dailyjournal.diaryapp.secretdiary.dialog.RatingIntroDialog
import com.dailyjournal.diaryapp.secretdiary.logenven.LogEven
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefRemote
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefUtils
import com.dailyjournal.diaryapp.secretdiary.ui.home.MainActivity
import com.dailyjournal.diaryapp.secretdiary.ui.password.EnterPasswordActivity
import com.dailyjournal.diaryapp.secretdiary.ui.permission.PermissionActivity
import com.dailyjournal.diaryapp.secretdiary.utils.helper.Contants
import com.dailyjournal.diaryapp.secretdiary.widget.gone
import com.dailyjournal.diaryapp.secretdiary.widget.isNetworkAvailable
import com.dailyjournal.diaryapp.secretdiary.widget.tapPage
import com.dailyjournal.diaryapp.secretdiary.widget.visible


class IntroActivity : BaseActivity<ActivityIntroBinding>() {
    private lateinit var dots: Array<ImageView?>
    private var countPageIntro = 6

    override fun setViewBinding(): ActivityIntroBinding {
        return ActivityIntroBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        countPageIntro = if (SharePrefRemote.get_config(
                this, SharePrefRemote.native_intro_full
            ) && SharePrefRemote.get_config(
                this, SharePrefRemote.native_intro_full2
            ) && AdsConsentManager.getConsentResult(
                this
            ) && SharePrefRemote.get_config(
                this, SharePrefRemote.show_ads
            ) && isNetworkAvailable()
        ) {
            6

        } else if (!SharePrefRemote.get_config(
                this, SharePrefRemote.native_intro_full
            ) && !SharePrefRemote.get_config(
                this, SharePrefRemote.native_intro_full2
            ) && AdsConsentManager.getConsentResult(
                this
            ) && SharePrefRemote.get_config(
                this, SharePrefRemote.show_ads
            ) && isNetworkAvailable()
        ) {
            4
        } else if ((SharePrefRemote.get_config(
                this, SharePrefRemote.native_intro_full
            ) || SharePrefRemote.get_config(
                this, SharePrefRemote.native_intro_full2
            )) && AdsConsentManager.getConsentResult(
                this
            ) && SharePrefRemote.get_config(
                this, SharePrefRemote.show_ads
            ) && isNetworkAvailable()
        ) {
            5
        } else {
            4
        }
        loadNative()

        val pagerAdapter = IntroAdapter(supportFragmentManager, this)
        binding.viewPager2.adapter = pagerAdapter
        binding.viewPager2.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                addBottomDots(position)
                if (SharePrefRemote.get_config(
                        this@IntroActivity, SharePrefRemote.native_intro_full
                    ) && SharePrefRemote.get_config(
                        this@IntroActivity, SharePrefRemote.native_intro_full2
                    ) && AdsConsentManager.getConsentResult(this@IntroActivity) && SharePrefRemote.get_config(
                        this@IntroActivity, SharePrefRemote.show_ads
                    )
                ) {
                    if (position == 1 || position == 3) {
                        binding.frAds.gone()
                        binding.linearDots.gone()
                        binding.btnNextTutorial.gone()
                    } else {
                        if (SharePrefRemote.get_config(
                                this@IntroActivity, SharePrefRemote.native_intro
                            ) && AdsConsentManager.getConsentResult(this@IntroActivity)
                        ) binding.frAds.visible() else {
                            binding.frAds.gone()
                        }
                        binding.linearDots.visible()
                        binding.btnNextTutorial.visible()
                    }
                } else if (!SharePrefRemote.get_config(
                        this@IntroActivity, SharePrefRemote.native_intro_full
                    ) && !SharePrefRemote.get_config(
                        this@IntroActivity, SharePrefRemote.native_intro_full2
                    ) && AdsConsentManager.getConsentResult(
                        this@IntroActivity
                    ) && SharePrefRemote.get_config(
                        this@IntroActivity, SharePrefRemote.show_ads
                    ) && isNetworkAvailable()
                ) {
                    if (SharePrefRemote.get_config(
                            this@IntroActivity, SharePrefRemote.native_intro
                        ) && AdsConsentManager.getConsentResult(this@IntroActivity)
                    ) binding.frAds.visible()
                    else {
                        binding.frAds.gone()
                    }
                    binding.linearDots.visible()
                    binding.btnNextTutorial.visible()

                } else if ((SharePrefRemote.get_config(
                        this@IntroActivity, SharePrefRemote.native_intro_full
                    ) && AdsConsentManager.getConsentResult(
                        this@IntroActivity
                    ) && SharePrefRemote.get_config(
                        this@IntroActivity, SharePrefRemote.show_ads
                    ) && isNetworkAvailable())
                ) {
                    if (position == 1) {
                        binding.frAds.gone()
                        binding.linearDots.gone()
                        binding.btnNextTutorial.gone()
                    } else {
                        if (SharePrefRemote.get_config(
                                this@IntroActivity, SharePrefRemote.native_intro
                            ) && AdsConsentManager.getConsentResult(this@IntroActivity)
                        ) binding.frAds.visible() else {
                            binding.frAds.gone()
                        }
                        binding.linearDots.visible()
                        binding.btnNextTutorial.visible()
                    }
                } else if ((SharePrefRemote.get_config(
                        this@IntroActivity, SharePrefRemote.native_intro_full2
                    ) && AdsConsentManager.getConsentResult(
                        this@IntroActivity
                    ) && SharePrefRemote.get_config(
                        this@IntroActivity, SharePrefRemote.show_ads
                    ) && isNetworkAvailable())
                ) {
                    if (position == 2) {
                        binding.frAds.gone()
                        binding.linearDots.gone()
                        binding.btnNextTutorial.gone()
                    } else {
                        if (SharePrefRemote.get_config(
                                this@IntroActivity, SharePrefRemote.native_intro
                            ) && AdsConsentManager.getConsentResult(this@IntroActivity)
                        ) binding.frAds.visible() else {
                            binding.frAds.gone()
                        }
                        binding.linearDots.visible()
                        binding.btnNextTutorial.visible()
                    }
                } else {

                }
                if (position == countPageIntro - 1) {
                    binding.btnNextTutorial.setText(R.string.start)
                } else {
                    LogEven.logEvent(this@IntroActivity, "onboarding_next_click", Bundle())
                    binding.btnNextTutorial.setText(R.string.next)
                }


                when (position) {
                    0 -> LogEven.logEvent(this@IntroActivity, "onboarding1_view", Bundle())
                    1 -> LogEven.logEvent(this@IntroActivity, "onboarding2_view", Bundle())
                    2 -> LogEven.logEvent(this@IntroActivity, "onboarding3_view", Bundle())
                    else -> LogEven.logEvent(this@IntroActivity, "onboarding4_view", Bundle())
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
        addBottomDots(0)
    }

    override fun viewListener() {
        binding.btnNextTutorial.tapPage {
            if (binding.viewPager2.currentItem == countPageIntro - 1) {
                if ((SharePrefUtils.isFirstTimeLogin(this) == 2 || SharePrefUtils.isFirstTimeLogin(
                        this
                    ) == 5 || SharePrefUtils.isFirstTimeLogin(this) == 9) && !SharePrefUtils.isRated(
                        this
                    )
                ) {
                    showDialogRate(false)
                } else {
                    it?.isEnabled = false
                    startNextScreen()
                }
            } else binding.viewPager2.currentItem += 1
        }

    }

    private fun showDialogRate(isFinishActivity: Boolean) {
        RatingIntroDialog(this, this, isFinishActivity) {
            startNextScreen()
        }.show()
    }

    private fun startNextScreen() {
        showLoading()
        if (SharePrefRemote.get_config(
                this@IntroActivity, SharePrefRemote.inter_intro
            ) && InterAdHelper.canShowNextAd(this) && AdsConsentManager.getConsentResult(this)
        ) {
            InterAdHelper.showListInterAd(
                this@IntroActivity, SharePrefRemote.get_config(
                    this@IntroActivity, SharePrefRemote.inter_intro
                ), AdmobApi.getInstance().getListIDByName("inter_intro")
            ) {
                if (SharePrefUtils.isGoToMain(this)) {
                    if (SharePrefUtils.getPassword(this).trim().isNotEmpty()) {
                        showActivity(EnterPasswordActivity::class.java)
                    } else {
                        showActivity(MainActivity::class.java)
                    }
                } else {
                    showActivity(PermissionActivity::class.java)
                }
                finishAffinity()
                hideLoading()
            }
        } else {
            if (SharePrefUtils.isGoToMain(this)) {
                if (SharePrefUtils.getPassword(this).trim().isNotEmpty()) {
                    showActivity(EnterPasswordActivity::class.java)
                } else {
                    showActivity(MainActivity::class.java)
                }
            } else {
                showActivity(PermissionActivity::class.java)
            }
            finishAffinity()
            hideLoading()
        }
    }


    override fun dataObservable() {
        addBottomDots(0)
    }


    private fun addBottomDots(currentPage: Int) {
        binding.linearDots.removeAllViews()
        dots = arrayOfNulls(countPageIntro)
        for (i in 0 until countPageIntro) {
            dots[i] = ImageView(this)
            if (i == currentPage) dots[i]!!.setImageResource(R.drawable.ic_intro_selected)
            else dots[i]!!.setImageResource(R.drawable.ic_intro_not_select)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)
            binding.linearDots.addView(dots[i], params)
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    private fun loadNative() {
        try {
            if (SharePrefRemote.get_config(
                    this, SharePrefRemote.native_intro
                ) && AdsConsentManager.getConsentResult(this)
            ) {
                if (SharePrefRemote.get_config(
                        this, SharePrefRemote.show_ads
                    )
                ) {
                    binding.frAds.visibility = View.VISIBLE
                    val nativeBuilder = NativeBuilder(
                        this,
                        binding.frAds,
                        R.layout.ads_native_large_top_shimer,
                        R.layout.layout_native_large_top,
                        R.layout.layout_native_large_top
                    )
                    nativeBuilder.setListIdAd(AdmobApi.getInstance().listIDNativeIntro)
                    val nativeManager = NativeManager(
                        this, this, nativeBuilder
                    ).setIntervalReloadNative(Contants.interval_reload_native)
                }

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


    private fun checkPos(): Boolean {
        if (SharePrefRemote.get_config(
                this@IntroActivity, SharePrefRemote.native_intro_full
            ) && SharePrefRemote.get_config(
                this@IntroActivity, SharePrefRemote.native_intro_full2
            ) && (binding.viewPager2.currentItem == 1 || binding.viewPager2.currentItem == 3)
        ) {
            return false
        } else if (SharePrefRemote.get_config(
                this@IntroActivity, SharePrefRemote.native_intro_full
            ) && !SharePrefRemote.get_config(
                this@IntroActivity, SharePrefRemote.native_intro_full2
            ) && binding.viewPager2.currentItem == 1
        ) {
            return false
        } else if (!SharePrefRemote.get_config(
                this@IntroActivity, SharePrefRemote.native_intro_full
            ) && SharePrefRemote.get_config(
                this@IntroActivity, SharePrefRemote.native_intro_full2
            ) && binding.viewPager2.currentItem == 2
        ) {
            return false
        } else return true
    }
}