package com.dailyjournal.diaryapp.secretdiary.ui.permission

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.amazic.ads.service.AdmobApi
import com.amazic.ads.util.AdsConsentManager
import com.amazic.ads.util.AppOpenManager
import com.amazic.ads.util.manager.native_ad.NativeBuilder
import com.amazic.ads.util.manager.native_ad.NativeManager
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.ads.InterAdHelper
import com.dailyjournal.diaryapp.secretdiary.base.BaseActivity
import com.dailyjournal.diaryapp.secretdiary.databinding.ActivityPermissionBinding
import com.dailyjournal.diaryapp.secretdiary.logenven.LogEven
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefRemote
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefUtils
import com.dailyjournal.diaryapp.secretdiary.ui.welcome.WelcomeActivity
import com.dailyjournal.diaryapp.secretdiary.utils.helper.Contants
import com.dailyjournal.diaryapp.secretdiary.utils.helper.Default.CAMERA_PERMISSION
import com.dailyjournal.diaryapp.secretdiary.utils.helper.Default.RECORD_PERMISSION
import com.dailyjournal.diaryapp.secretdiary.utils.helper.Default.STORAGE_PERMISSION
import com.dailyjournal.diaryapp.secretdiary.widget.tap


class PermissionActivity : BaseActivity<ActivityPermissionBinding>() {


    override fun setViewBinding(): ActivityPermissionBinding {
        return ActivityPermissionBinding.inflate(layoutInflater)
    }

    override fun initView() {
        loadNative()
        LogEven.logEvent(this, "permission_open", Bundle())
        binding.root.setBackgroundResource(getBackground(SharePrefUtils.getTheme(this)))
        if (checkPermission(STORAGE_PERMISSION)) allowStoragePermission()
        if (checkPermission(CAMERA_PERMISSION)) allowCameraPermission()
        if (checkPermission(RECORD_PERMISSION)) allowRecordPermission()
    }

    override fun viewListener() {
        binding.apply {
            ivSetStoragePermission.tap {
                val bundle = Bundle()
                bundle.putString("per_name", "STORAGE_PERMISSION")
                LogEven.logEvent(this@PermissionActivity, "permission_allow_click", bundle)
                showDialogPermission(STORAGE_PERMISSION)
            }

            ivSetCameraPermission.tap {
                val bundle = Bundle()
                bundle.putString("per_name", "CAMERA_PERMISSION")
                LogEven.logEvent(this@PermissionActivity, "permission_allow_click", bundle)
                showDialogPermission(arrayOf(CAMERA_PERMISSION))
            }

            ivSetRecordPermission.tap {
                val bundle = Bundle()
                bundle.putString("per_name", "RECORD_PERMISSION")
                LogEven.logEvent(this@PermissionActivity, "permission_allow_click", bundle)
                showDialogPermission(arrayOf(RECORD_PERMISSION))
            }

            tvContinue.tap {
                LogEven.logEvent(this@PermissionActivity, "permission_continue_click", Bundle())
//                SharePrefUtils.forceGoToMain(this@PermissionActivity)
                showLoading()
                if (SharePrefRemote.get_config(
                        this@PermissionActivity, SharePrefRemote.inter_permission
                    ) && InterAdHelper.canShowNextAd(this@PermissionActivity) && AdsConsentManager.getConsentResult(
                        this@PermissionActivity
                    )
                ) {
                    InterAdHelper.showListInterAd(
                        this@PermissionActivity, SharePrefRemote.get_config(
                            this@PermissionActivity, SharePrefRemote.inter_permission
                        ), AdmobApi.getInstance().getListIDByName("inter_permission")
                    ) {
                        showActivity(WelcomeActivity::class.java)
                        finishAffinity()
                        hideLoading()
                    }
                } else {
                    showActivity(WelcomeActivity::class.java)
                    finishAffinity()
                    hideLoading()
                }
            }
        }

    }

    override fun dataObservable() {
    }

    override fun onPermissionGranted() {
        if (checkPermission(STORAGE_PERMISSION)) allowStoragePermission()
        if (checkPermission(CAMERA_PERMISSION)) allowCameraPermission()
        if (checkPermission(RECORD_PERMISSION)) allowRecordPermission()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    override fun onResume() {
        AppOpenManager.getInstance().enableAppResumeWithActivity(this::class.java)
        if (checkPermission(STORAGE_PERMISSION)) allowStoragePermission()
        if (checkPermission(CAMERA_PERMISSION)) allowCameraPermission()
        if (checkPermission(RECORD_PERMISSION)) allowRecordPermission()
        super.onResume()
    }

    private fun allowStoragePermission() {
        binding.ivSetStoragePermission.visibility = View.GONE
        binding.ivSelectStoragePermission.visibility = View.VISIBLE
    }

    private fun allowCameraPermission() {
        binding.ivSetCameraPermission.visibility = View.GONE
        binding.ivSelectCameraPermission.visibility = View.VISIBLE
    }

    private fun allowRecordPermission() {
        binding.ivSetRecordPermission.visibility = View.GONE
        binding.ivSelectRecordPermission.visibility = View.VISIBLE
    }
    private fun loadNative() {
        try {
            if (SharePrefRemote.get_config(
                    this, SharePrefRemote.native_permission
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
                nativeBuilder.setListIdAd(AdmobApi.getInstance().listIDNativePermission)
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