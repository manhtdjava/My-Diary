package com.dailyjournal.diaryapp.secretdiary.dialog

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import com.amazic.ads.service.AdmobApi
import com.amazic.ads.util.AdsConsentManager
import com.amazic.ads.util.manager.native_ad.NativeBuilder
import com.amazic.ads.util.manager.native_ad.NativeManager
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.base.BaseDialog
import com.dailyjournal.diaryapp.secretdiary.databinding.DialogSetTimeBinding
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefRemote
import com.dailyjournal.diaryapp.secretdiary.utils.helper.Contants
import com.dailyjournal.diaryapp.secretdiary.widget.tap


class SetTimeDialog(
    activity1: Activity,
    private var life: LifecycleOwner,
    var timeString: String,
    private var action: (s: String) -> Unit
) : BaseDialog<DialogSetTimeBinding>(activity1, true) {
    private var time = "AM"

    override fun getContentView(): DialogSetTimeBinding {
        return DialogSetTimeBinding.inflate(LayoutInflater.from(activity))
    }

    override fun initView() {
        loadNative()
        if (timeString.contains("AM")) {
            showAMorPM(true)
            time = "AM"
        } else if (timeString.contains("PM")) {
            showAMorPM(false)
            time = "PM"
        }
        val s = timeString.replace("AM", "").replace("PM", "").trim()
        binding.edtHour.setText(s.split(":")[0])
        binding.edtMin.setText(s.split(":")[1])
    }

    override fun bindView() {
        binding.apply {
            tvOke.tap {
                if (binding.edtHour.text.toString().trim()
                        .isNotEmpty() && binding.edtMin.text.toString().trim().isNotEmpty()
                ) {
                    if (binding.edtHour.text.toString().trim()
                            .toInt() <= 12 && binding.edtMin.text.toString().trim().toInt() <= 59
                    ) {
                        action.invoke(
                            "" + binding.edtHour.text.toString()
                                .trim() + ":" + binding.edtMin.text.toString().trim() + time
                        )
                        dismiss()
                    } else {
                        Toast.makeText(
                            context,
                            context.getString(R.string.please_fill_in_valid_time),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.please_fill_in_valid_time),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            tvCancel.tap {
                dismiss()
            }
            tvAm.tap {
                time = "AM"
                showAMorPM(true)
            }
            tvPm.tap {
                time = "PM"
                showAMorPM(false)
            }
        }

    }


    private fun showAMorPM(isCheckAM: Boolean) {
        if (isCheckAM) {
            binding.tvAm.setBackgroundResource(R.drawable.bg_border_btn_confirm)
            binding.tvPm.setBackgroundResource(R.drawable.bg_border_btn_cancel)
            binding.tvAm.setTextColor(context.getColor(R.color.color_white))
            binding.tvPm.setTextColor(context.getColor(R.color.color_7B8280))
        } else {
            binding.tvPm.setBackgroundResource(R.drawable.bg_border_btn_confirm)
            binding.tvAm.setBackgroundResource(R.drawable.bg_border_btn_cancel)
            binding.tvPm.setTextColor(context.getColor(R.color.color_white))
            binding.tvAm.setTextColor(context.getColor(R.color.color_7B8280))
        }
    }

    private val handler1 = Handler(Looper.getMainLooper())
    private lateinit var runnable1: Runnable
    private fun loadNative() {
        try {
            if (SharePrefRemote.get_config(
                    activity, SharePrefRemote.native_popup
                ) && AdsConsentManager.getConsentResult(activity)
            ) {
                binding.frAds.visibility = View.VISIBLE
                val nativeBuilder = NativeBuilder(
                    activity,
                    binding.frAds,
                    R.layout.ads_native_large_top_shimer,
                    R.layout.layout_native_large_top,
                    R.layout.layout_native_large_top
                )
                nativeBuilder.setListIdAd(AdmobApi.getInstance().getListIDByName("native_popup"))
                val nativeManager = NativeManager(
                    activity, life, nativeBuilder
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