package com.dailyjournal.diaryapp.secretdiary.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.LifecycleOwner
import com.amazic.ads.service.AdmobApi
import com.amazic.ads.util.AdsConsentManager
import com.amazic.ads.util.manager.native_ad.NativeBuilder
import com.amazic.ads.util.manager.native_ad.NativeManager
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.ads.InterAdHelper
import com.dailyjournal.diaryapp.secretdiary.base.BaseDialog
import com.dailyjournal.diaryapp.secretdiary.databinding.DialogDeleteSaveBinding
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefRemote
import com.dailyjournal.diaryapp.secretdiary.utils.helper.Contants
import com.dailyjournal.diaryapp.secretdiary.widget.tap


class DialogSave(
    activity1: Activity,
    private var life: LifecycleOwner,
    private var action: () -> Unit,
    private var onCancelAction: () -> Unit
) : BaseDialog<DialogDeleteSaveBinding>(activity1, true) {
    override fun getContentView(): DialogDeleteSaveBinding {
        return DialogDeleteSaveBinding.inflate(LayoutInflater.from(activity))
    }

    override fun initView() {
    //    loadNative()
        binding.txtTitle.text = activity.getString(R.string.save)
        binding.txtContent.text = activity.getString(R.string.do_want_to_save)
    }

    override fun bindView() {
        binding.apply {
            txtGo.tap {
                showLoading()
                if (SharePrefRemote.get_config(
                        activity, SharePrefRemote.inter_save
                    ) && InterAdHelper.canShowNextAd(activity) && AdsConsentManager.getConsentResult(
                        activity
                    )
                ) {
                    InterAdHelper.showListInterAd(
                        activity, SharePrefRemote.get_config(
                            activity, SharePrefRemote.inter_save
                        ), AdmobApi.getInstance().getListIDByName("inter_create")
                    ) {
                        action.invoke()
                        dismiss()
                        hideLoading()
                    }

                } else {
                    action.invoke()
                    dismiss()
                    hideLoading()
                }

            }
            txtCancel.tap {
                onCancelAction.invoke()
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

    private var backgroundView: FrameLayout? = null
    private var loadingLayout: View? = null

    @SuppressLint("InflateParams")
    protected open fun showLoading() {
        if (loadingLayout == null) {
            val li = LayoutInflater.from(activity)
            loadingLayout = li.inflate(R.layout.layout_loading_progress, null, false)
            backgroundView = loadingLayout!!.findViewById(R.id.root)
            val rootView = activity.findViewById<ViewGroup>(android.R.id.content)
            rootView.addView(
                loadingLayout, ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
            backgroundView!!.isClickable = true
        }
    }

    protected open fun hideLoading() {
        if (loadingLayout != null) {
            val rootView = activity.findViewById<ViewGroup>(android.R.id.content)
            rootView.removeView(loadingLayout)
            if (backgroundView != null) backgroundView!!.isClickable = false
            loadingLayout = null
        }
    }
}