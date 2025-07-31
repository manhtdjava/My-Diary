package com.dailyjournal.diaryapp.secretdiary.ui.password

import android.annotation.SuppressLint
import android.content.Intent
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
import com.dailyjournal.diaryapp.secretdiary.base.BaseActivity
import com.dailyjournal.diaryapp.secretdiary.databinding.ActivityChangePasswordBinding
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefRemote
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefUtils
import com.dailyjournal.diaryapp.secretdiary.ui.home.MainActivity
import com.dailyjournal.diaryapp.secretdiary.utils.helper.Contants
import com.dailyjournal.diaryapp.secretdiary.widget.invisible
import com.dailyjournal.diaryapp.secretdiary.widget.tap
import com.dailyjournal.diaryapp.secretdiary.widget.tapPage


@SuppressLint("CustomSplashScreen")
class ChangePasswordActivity : BaseActivity<ActivityChangePasswordBinding>() {
    private var type = 0
    private var passCode = ""
    private var passCodeConfirm = ""
    private lateinit var number_list: ArrayList<String>
    private var num1 = ""
    private var num2 = ""
    private var num3 = ""
    private var num4 = ""
    override fun setViewBinding(): ActivityChangePasswordBinding {
        return ActivityChangePasswordBinding.inflate(LayoutInflater.from(this))
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

    override fun initView() {
        loadCollapse()
        if (intent != null) {
            type = intent.getIntExtra("TYPE", 0)
            passCodeConfirm = intent.getStringExtra("PASSWORD") as String
        }
        if (type == 0) {
            binding.tvTitle.text = getString(R.string.change_your_password)
            binding.tvHeader.text = getString(R.string.password)
        } else {
            binding.tvTitle.text = getString(R.string.Confirm_Your_Password)
            binding.tvSkin.invisible()
            binding.tvHeader.text = getString(R.string.confirm)
        }
        number_list = ArrayList()
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
            if (type == 0) {
                val intent = Intent(this, ChangePasswordActivity::class.java)
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
                    showActivity(MainActivity::class.java)
                    finishAffinity()
                    SharePrefUtils.setPassword(this@ChangePasswordActivity, passCode)
                }
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
}