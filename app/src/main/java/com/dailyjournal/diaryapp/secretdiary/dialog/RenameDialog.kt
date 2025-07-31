package com.dailyjournal.diaryapp.secretdiary.dialog

import android.app.Activity
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
import com.dailyjournal.diaryapp.secretdiary.databinding.DialogRenameBinding
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefRemote
import com.dailyjournal.diaryapp.secretdiary.utils.helper.Contants
import com.dailyjournal.diaryapp.secretdiary.widget.tap


class RenameDialog(
    activity1: Activity, private var life: LifecycleOwner, private var action: (s: String) -> Unit
) : BaseDialog<DialogRenameBinding>(activity1, true) {
    override fun getContentView(): DialogRenameBinding {
        return DialogRenameBinding.inflate(LayoutInflater.from(activity))
    }

    override fun initView() {
        loadNative()
    }

    override fun bindView() {
        binding.apply {
            tvConfirm.tap {
                if (binding.edt.text.toString().trim().isNotEmpty()) {
                    action.invoke(binding.edt.text.toString().trim())
                    dismiss()
                } else {
                    Toast.makeText(
                        activity,
                        activity.getString(R.string.please_enter_your_name),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
            tvCancel.tap {
                dismiss()
            }
        }
    }

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