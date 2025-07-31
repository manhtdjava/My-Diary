package com.dailyjournal.diaryapp.secretdiary.ui.password

import android.annotation.SuppressLint
import android.content.Intent
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.amazic.ads.service.AdmobApi
import com.amazic.ads.util.AdsConsentManager
import com.amazic.ads.util.manager.native_ad.NativeBuilder
import com.amazic.ads.util.manager.native_ad.NativeManager
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.ads.InterAdHelper
import com.dailyjournal.diaryapp.secretdiary.base.BaseActivity
import com.dailyjournal.diaryapp.secretdiary.databinding.ActivityForgotPasswordBinding
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefRemote
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefUtils
import com.dailyjournal.diaryapp.secretdiary.utils.helper.Contants
import com.dailyjournal.diaryapp.secretdiary.widget.gone
import com.dailyjournal.diaryapp.secretdiary.widget.tap
import com.dailyjournal.diaryapp.secretdiary.widget.visible


@SuppressLint("CustomSplashScreen")
class ForgotPasswordActivity : BaseActivity<ActivityForgotPasswordBinding>() {
    private var countdownTimer: CountDownTimer? = null
    override fun setViewBinding(): ActivityForgotPasswordBinding {
        return ActivityForgotPasswordBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        loadNative()
        binding.tvQuestion.text = when (SharePrefUtils.getQuestion(this)) {
            1 -> {
                getString(R.string.your_question_1)
            }

            2 -> {
                getString(R.string.your_question_2)
            }

            3 -> {
                getString(R.string.your_question_3)
            }

            else -> {
                getString(R.string.your_question_1)
            }
        }

    }

    override fun viewListener() {
        binding.ivBack.tap { finish() }
        binding.tvConfirm.tap {
            showLoading()
            if (SharePrefRemote.get_config(
                    this@ForgotPasswordActivity, SharePrefRemote.inter_password
                ) && InterAdHelper.canShowNextAd(this) && AdsConsentManager.getConsentResult(this)
            ) {
                InterAdHelper.showListInterAd(
                    this@ForgotPasswordActivity, SharePrefRemote.get_config(
                        this@ForgotPasswordActivity, SharePrefRemote.inter_password
                    ), AdmobApi.getInstance().getListIDByName("inter_permission")
                ) {
                    if (binding.edt.text.toString().trim()
                            .isNotEmpty() && binding.edt.text.toString()
                            .trim() == SharePrefUtils.getAnswer(this)
                    ) {
                        val intent =
                            Intent(this@ForgotPasswordActivity, ChangePasswordActivity::class.java)
                        intent.putExtra("TYPE", 0)
                        intent.putExtra("PASSWORD", " ")
                        startActivity(intent)
                        finish()
                    } else if (binding.edt.text.toString().trim().isNotEmpty()) {
                        countdownTimer = object : CountDownTimer(2000, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                                binding.ctlAnswer.setBackgroundResource(R.drawable.bg_border_answer_false)
                                binding.tvFalse.visible()
                            }

                            override fun onFinish() {
                                binding.ctlAnswer.setBackgroundResource(R.drawable.bg_border_answer)
                                binding.tvFalse.gone()
                            }
                        }
                        countdownTimer!!.start()
                    } else {
                        Toast.makeText(
                            this, getString(R.string.please_enter_your_answer), Toast.LENGTH_SHORT
                        ).show()
                    }
                    hideLoading()
                }
            } else {
                if (binding.edt.text.toString().trim().isNotEmpty() && binding.edt.text.toString()
                        .trim() == SharePrefUtils.getAnswer(this)
                ) {
                    val intent =
                        Intent(this@ForgotPasswordActivity, ChangePasswordActivity::class.java)
                    intent.putExtra("TYPE", 0)
                    intent.putExtra("PASSWORD", " ")
                    startActivity(intent)
                    finish()
                } else if (binding.edt.text.toString().trim().isNotEmpty()) {
                    countdownTimer = object : CountDownTimer(2000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            binding.ctlAnswer.setBackgroundResource(R.drawable.bg_border_answer_false)
                            binding.tvFalse.visible()
                        }

                        override fun onFinish() {
                            binding.ctlAnswer.setBackgroundResource(R.drawable.bg_border_answer)
                            binding.tvFalse.gone()
                        }
                    }
                    countdownTimer!!.start()
                } else {
                    Toast.makeText(
                        this, getString(R.string.please_enter_your_answer), Toast.LENGTH_SHORT
                    ).show()
                }
                hideLoading()
            }


        }
    }

    override fun dataObservable() {

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (countdownTimer != null) countdownTimer!!.cancel()
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