package com.dailyjournal.diaryapp.secretdiary.ui.password

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
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
import com.dailyjournal.diaryapp.secretdiary.databinding.ActivityEnterPasswordBinding
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefRemote
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefUtils
import com.dailyjournal.diaryapp.secretdiary.ui.home.MainActivity
import com.dailyjournal.diaryapp.secretdiary.utils.helper.Contants
import com.dailyjournal.diaryapp.secretdiary.widget.tap
import com.dailyjournal.diaryapp.secretdiary.widget.tapPage


@SuppressLint("CustomSplashScreen")
class EnterPasswordActivity : BaseActivity<ActivityEnterPasswordBinding>() {
    private var passCode = ""
    private lateinit var list: ArrayList<String>
    private var num1 = ""
    private var num2 = ""
    private var num3 = ""
    private var num4 = ""
    override fun setViewBinding(): ActivityEnterPasswordBinding {
        return ActivityEnterPasswordBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        loadCollapse()
        list = ArrayList()
    }

    override fun viewListener() {
        binding.tv1.tapPage {
            list.add("1")
            passNumber(list)
        }
        binding.tv2.tapPage {
            list.add("2")
            passNumber(list)
        }
        binding.tv3.tapPage {
            list.add("3")
            passNumber(list)
        }
        binding.tv4.tapPage {
            list.add("4")
            passNumber(list)
        }
        binding.tv5.tapPage {
            list.add("5")
            passNumber(list)
        }
        binding.tv6.tapPage {
            list.add("6")
            passNumber(list)
        }
        binding.tv7.tapPage {
            list.add("7")
            passNumber(list)
        }
        binding.tv8.tapPage {
            list.add("8")
            passNumber(list)
        }
        binding.tv9.tapPage {
            list.add("9")
            passNumber(list)
        }
        binding.tv0.tapPage {
            list.add("0")
            passNumber(list)
        }
        binding.tvX.tap {
            list.clear()
            passNumber(list)
        }
        binding.tvNext.tap {
            if (SharePrefUtils.getPassword(this) != passCode) {
                Toast.makeText(this, getString(R.string.title_re_enter), Toast.LENGTH_SHORT)
                    .show()
                num1 = ""
                num2 = ""
                num3 = ""
                num4 = ""
                passCode = ""
                list.clear()
                passNumber(list)
            } else {
                showActivity(MainActivity::class.java)
                finishAffinity()
            }

        }
        binding.tvForgot.tap {
            showLoading()
            if (SharePrefRemote.get_config(
                    this@EnterPasswordActivity, SharePrefRemote.inter_password
                ) && InterAdHelper.canShowNextAd(this) && AdsConsentManager.getConsentResult(this)
            ) {

                InterAdHelper.showListInterAd(
                    this@EnterPasswordActivity, SharePrefRemote.get_config(
                        this@EnterPasswordActivity, SharePrefRemote.inter_password
                    ), AdmobApi.getInstance().getListIDByName("inter_permission")
                ) {
                    showActivity(ForgotPasswordActivity::class.java)
                    hideLoading()
                }
            } else {
                showActivity(ForgotPasswordActivity::class.java)
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
            when (list.size) {
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