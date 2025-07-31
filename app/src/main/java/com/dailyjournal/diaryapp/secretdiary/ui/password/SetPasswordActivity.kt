package com.dailyjournal.diaryapp.secretdiary.ui.password

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.amazic.ads.service.AdmobApi
import com.amazic.ads.util.Admob
import com.amazic.ads.util.AdsConsentManager
import com.amazic.ads.util.BannerGravity
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.ads.InterAdHelper
import com.dailyjournal.diaryapp.secretdiary.base.BaseActivity
import com.dailyjournal.diaryapp.secretdiary.databinding.ActivitySetPasswordBinding
import com.dailyjournal.diaryapp.secretdiary.logenven.LogEven
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefRemote
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefUtils
import com.dailyjournal.diaryapp.secretdiary.ui.home.MainActivity
import com.dailyjournal.diaryapp.secretdiary.ui.mine.MineActivity
import com.dailyjournal.diaryapp.secretdiary.utils.helper.Contants
import com.dailyjournal.diaryapp.secretdiary.widget.invisible
import com.dailyjournal.diaryapp.secretdiary.widget.tap
import com.dailyjournal.diaryapp.secretdiary.widget.tapPage
import com.dailyjournal.diaryapp.secretdiary.widget.visible


@SuppressLint("CustomSplashScreen")
class SetPasswordActivity : BaseActivity<ActivitySetPasswordBinding>() {
    private var type = 0
    private var passCode = ""
    private var passCodeConfirm = ""
    private lateinit var number_list: ArrayList<String>
    private var num1 = ""
    private var num2 = ""
    private var num3 = ""
    private var num4 = ""
    override fun setViewBinding(): ActivitySetPasswordBinding {
        return ActivitySetPasswordBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        loadCollapse()
        if (intent != null) {
            type = intent.getIntExtra("TYPE", 0)
            passCodeConfirm = intent.getStringExtra("PASSWORD") as String
        }
        if (type == 0) {
            binding.tvTitle.text = getString(R.string.please_enter_your_password)
            binding.tvSkin.visible()
        } else {
            binding.tvTitle.text = getString(R.string.confirm_your_password)
            binding.tvSkin.invisible()
        }
        number_list = ArrayList()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun viewListener() {
        binding.tv1.tapPage {
            number_list.add("1")
            passNumber(number_list)
        }
        binding.tv2.tapPage {
            number_list.add("2")
            passNumber(number_list)
        }
        binding.tv3.tapPage {
            number_list.add("3")
            passNumber(number_list)
        }
        binding.tv4.tapPage {
            number_list.add("4")
            passNumber(number_list)
        }
        binding.tv5.tapPage {
            number_list.add("5")
            passNumber(number_list)
        }
        binding.tv6.tapPage {
            number_list.add("6")
            passNumber(number_list)
        }
        binding.tv7.tapPage {
            number_list.add("7")
            passNumber(number_list)
        }
        binding.tv8.tapPage {
            number_list.add("8")
            passNumber(number_list)
        }
        binding.tv9.tapPage {
            number_list.add("9")
            passNumber(number_list)
        }
        binding.tv0.tapPage {
            number_list.add("0")
            passNumber(number_list)
        }
        binding.tvX.tap {
            number_list.clear()
            passNumber(number_list)
        }
        binding.tvNext.tap {
            LogEven.logEvent(this, "next_pass_click", Bundle())
            if (type == 0) {
                val intent = Intent(this, SetPasswordActivity::class.java)
                intent.putExtra("TYPE", 1)
                intent.putExtra("PASSWORD", passCode)
                startActivity(intent)
            } else {
                if (passCodeConfirm != passCode) {
                    Toast.makeText(this, getString(R.string.title_re_enter), Toast.LENGTH_SHORT)
                        .show()
                    num1 = ""
                    num2 = ""
                    num3 = ""
                    num4 = ""
                    passCode = ""
                    number_list.clear()
                    passNumber(number_list)
                } else {
                    showActivity(SecurityQuestionsActivity::class.java)
                    finish()
                    SharePrefUtils.setPassword(this@SetPasswordActivity, passCode)
                }
            }
        }
        binding.tvSkin.tap {
            LogEven.logEvent(this, "skip_pass_click", Bundle())

            showLoading()
            if (SharePrefRemote.get_config(
                    this@SetPasswordActivity, SharePrefRemote.inter_password
                ) && InterAdHelper.canShowNextAd(this) && AdsConsentManager.getConsentResult(this)
            ) {
                InterAdHelper.showListInterAd(
                    this@SetPasswordActivity, SharePrefRemote.get_config(
                        this@SetPasswordActivity, SharePrefRemote.inter_password
                    ), AdmobApi.getInstance().getListIDByName("inter_permission")
                ) {
                    if (SharePrefUtils.isGoToMain(this@SetPasswordActivity)) {
                        finish()
                    } else {
                        SharePrefUtils.forceGoToMain(this@SetPasswordActivity)
                        showActivity(MainActivity::class.java)
                        finishAffinity()
                    }
                    hideLoading()
                }
            } else {
                if (SharePrefUtils.isGoToMain(this@SetPasswordActivity)) {
                    finish()
                } else {
                    SharePrefUtils.forceGoToMain(this@SetPasswordActivity)
                    showActivity(MainActivity::class.java)
                    finishAffinity()
                }
                hideLoading()
            }

        }
    }

    private fun passNumber(numberList: ArrayList<String>) {
        if (numberList.isEmpty()) {
            binding.view1.setBackgroundResource(R.drawable.bg_border_edt_password_no)
            binding.view2.setBackgroundResource(R.drawable.bg_border_edt_password_no)
            binding.view3.setBackgroundResource(R.drawable.bg_border_edt_password_no)
            binding.view4.setBackgroundResource(R.drawable.bg_border_edt_password_no)
        } else {
            when (number_list.size) {
                1 -> {
                    num1 = numberList[0]
                    binding.view1.setBackgroundResource(R.drawable.bg_border_edt_password)
                }

                2 -> {
                    num2 = numberList[1]
                    binding.view2.setBackgroundResource(R.drawable.bg_border_edt_password)
                }

                3 -> {
                    num3 = numberList[2]
                    binding.view3.setBackgroundResource(R.drawable.bg_border_edt_password)
                }

                4 -> {
                    num4 = numberList[3]
                    binding.view4.setBackgroundResource(R.drawable.bg_border_edt_password)
                    passCode = num1 + num2 + num3 + num4
                }
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
        if (handler != null && ::runnable.isInitialized) handler.removeCallbacks(runnable)
    }

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private fun loadCollapse() {
        if (SharePrefRemote.get_config(
                this, SharePrefRemote.collapse_banner_password
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