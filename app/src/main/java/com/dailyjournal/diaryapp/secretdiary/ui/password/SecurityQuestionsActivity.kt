package com.dailyjournal.diaryapp.secretdiary.ui.password

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import com.amazic.ads.service.AdmobApi
import com.amazic.ads.util.AdsConsentManager
import com.amazic.ads.util.manager.native_ad.NativeBuilder
import com.amazic.ads.util.manager.native_ad.NativeManager
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.ads.InterAdHelper
import com.dailyjournal.diaryapp.secretdiary.base.BaseActivity
import com.dailyjournal.diaryapp.secretdiary.databinding.ActivitySecurityQuestionsBinding
import com.dailyjournal.diaryapp.secretdiary.logenven.LogEven
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefRemote
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefUtils
import com.dailyjournal.diaryapp.secretdiary.ui.home.MainActivity
import com.dailyjournal.diaryapp.secretdiary.utils.helper.Contants
import com.dailyjournal.diaryapp.secretdiary.widget.gone
import com.dailyjournal.diaryapp.secretdiary.widget.tap
import com.dailyjournal.diaryapp.secretdiary.widget.visible


@SuppressLint("CustomSplashScreen")
class SecurityQuestionsActivity : BaseActivity<ActivitySecurityQuestionsBinding>() {
    private var type = 1
    override fun setViewBinding(): ActivitySecurityQuestionsBinding {
        return ActivitySecurityQuestionsBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        loadNative()
        LogEven.logEvent(this, "security_view", Bundle())
    }

    override fun viewListener() {
        binding.tvQuestion1.tap {
            type = 1
            binding.ctlListQuestion.gone()
            binding.tvQues.text = getString(R.string.your_question_1)
        }
        binding.tvQuestion2.tap {
            type = 2
            binding.ctlListQuestion.gone()
            binding.tvQues.text = getString(R.string.your_question_2)
        }
        binding.tvQuestion3.tap {
            type = 3
            binding.ctlListQuestion.gone()
            binding.tvQues.text = getString(R.string.your_question_3)
        }
        binding.ctlQuestion.tap {
            if (binding.ctlListQuestion.isVisible) binding.ctlListQuestion.gone()
            else binding.ctlListQuestion.visible()
        }
        binding.ivBack.tap { onBackPressed() }

        val password = SharePrefUtils.getPassword(this@SecurityQuestionsActivity)
        SharePrefUtils.setPassword(this@SecurityQuestionsActivity, "")
        binding.tvConfirm.tap {
            LogEven.logEvent(this, "confirm_answer_click", Bundle())
            if (binding.edt.text.toString().trim().isNotEmpty()) {
                SharePrefUtils.setQuestion(this, type)
                SharePrefUtils.setAnswer(this, binding.edt.text.toString().trim())
                SharePrefUtils.setPassword(this@SecurityQuestionsActivity, password)

                showLoading()
                if (SharePrefRemote.get_config(
                        this@SecurityQuestionsActivity, SharePrefRemote.inter_password
                    ) && InterAdHelper.canShowNextAd(this) && AdsConsentManager.getConsentResult(this)
                ) {

                    InterAdHelper.showListInterAd(
                        this@SecurityQuestionsActivity, SharePrefRemote.get_config(
                            this@SecurityQuestionsActivity, SharePrefRemote.inter_password
                        ), AdmobApi.getInstance().getListIDByName("inter_permission")
                    ) {
                        if (SharePrefUtils.isGoToMain(this@SecurityQuestionsActivity)) {
                            showActivity(MainActivity::class.java)
                            finishAffinity()
                        } else {
                            SharePrefUtils.forceGoToMain(this@SecurityQuestionsActivity)
                            showActivity(MainActivity::class.java)
                            finishAffinity()
                        }
                        hideLoading()
                    }
                } else {
                    if (SharePrefUtils.isGoToMain(this@SecurityQuestionsActivity)) {
                        showActivity(MainActivity::class.java)
                        finishAffinity()
                    } else {
                        SharePrefUtils.forceGoToMain(this@SecurityQuestionsActivity)
                        showActivity(MainActivity::class.java)
                        finishAffinity()
                    }
                    hideLoading()
                }

            } else {
                Toast.makeText(
                    this,
                    getString(R.string.please_enter_your_answer),
                    Toast.LENGTH_SHORT
                ).show()
            }




        }
    }

    override fun dataObservable() {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun loadNative() {
        try {
            if (SharePrefRemote.get_config(
                    this, SharePrefRemote.native_password
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
                    this,
                    this,
                    nativeBuilder
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