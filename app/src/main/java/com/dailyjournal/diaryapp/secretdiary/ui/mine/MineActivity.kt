package com.dailyjournal.diaryapp.secretdiary.ui.mine

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.amazic.ads.service.AdmobApi
import com.amazic.ads.util.AdsConsentManager
import com.amazic.ads.util.AppOpenManager
import com.amazic.ads.util.manager.native_ad.NativeBuilder
import com.amazic.ads.util.manager.native_ad.NativeManager
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.ads.InterAdHelper
import com.dailyjournal.diaryapp.secretdiary.base.BaseActivity
import com.dailyjournal.diaryapp.secretdiary.databinding.ActivitySettingBinding
import com.dailyjournal.diaryapp.secretdiary.dialog.RenameDialog
import com.dailyjournal.diaryapp.secretdiary.logenven.LogEven
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefRemote
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefUtils
import com.dailyjournal.diaryapp.secretdiary.ui.language.LanguageActivity
import com.dailyjournal.diaryapp.secretdiary.ui.notify.NotificationActivity
import com.dailyjournal.diaryapp.secretdiary.ui.password.ChangePasswordSettingActivity
import com.dailyjournal.diaryapp.secretdiary.ui.password.SetPasswordActivity
import com.dailyjournal.diaryapp.secretdiary.ui.theme.SettingThemeActivity
import com.dailyjournal.diaryapp.secretdiary.utils.HelperMenu
import com.dailyjournal.diaryapp.secretdiary.utils.helper.Contants
import com.dailyjournal.diaryapp.secretdiary.widget.gone
import com.dailyjournal.diaryapp.secretdiary.widget.tap


class MineActivity : BaseActivity<ActivitySettingBinding>(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private var helperMenu: HelperMenu? = null

    override fun setViewBinding(): ActivitySettingBinding {
        return ActivitySettingBinding.inflate(layoutInflater)
    }

    private var launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        loadNative()
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        loadNative()
        LogEven.logEvent(this, "setting_view", Bundle())
        if (SharePrefUtils.getTheme(this) == 2 || SharePrefUtils.getTheme(
                this
            ) == 5
        ) {
            binding.tv.setTextColor(this.getColor(R.color.color_F4F5F5))
            binding.ivBack.setImageResource(R.drawable.ic_back_white)
        } else {
            binding.tv.setTextColor(this.getColor(R.color.color_292B2B))
            binding.ivBack.setImageResource(R.drawable.ic_back)
        }
        binding.root.setBackgroundResource(getBackground(SharePrefUtils.getTheme(this)))
        if (SharePrefUtils.isRated(this)) binding.tvRate.gone()
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        val versionName = packageInfo.versionName
        binding.tvVer.text = "${getString(R.string.version_1_0_0)} $versionName"
    }

    override fun viewListener() {
        binding.apply {
            tvRate.tap { helperMenu?.showDialogRate(false, this@MineActivity) }
            tvShare.tap { helperMenu?.showShareApp() }
            tvPolicy.tap {
                LogEven.logEvent(this@MineActivity, "setting_policy_click", Bundle())
                AppOpenManager.getInstance().disableAppResumeWithActivity(MineActivity::class.java)
                helperMenu?.showPolicy()
            }
            tvLanguage.tap {
                LogEven.logEvent(this@MineActivity, "setting_lang_click", Bundle())
                launcher.launch(Intent(this@MineActivity, LanguageActivity::class.java))

            }
            ivBack.tap { finish() }
            tvChangeName.tap {
                LogEven.logEvent(this@MineActivity, "setting_change_name_click", Bundle())
                val dialog = RenameDialog(this@MineActivity, this@MineActivity) {
                    SharePrefUtils.setUser(this@MineActivity, it)
                }
                dialog.show()
            }
            tvTheme.tap {
                // LogEven.logEvent(this@MineActivity, "setting_theme_click", Bundle())
                showLoading()
                if (SharePrefRemote.get_config(
                        this@MineActivity, SharePrefRemote.inter_mine
                    ) && InterAdHelper.canShowNextAd(this@MineActivity) && AdsConsentManager.getConsentResult(
                        this@MineActivity
                    )
                ) {
                    InterAdHelper.showListInterAd(
                        this@MineActivity, SharePrefRemote.get_config(
                            this@MineActivity, SharePrefRemote.inter_mine
                        ), AdmobApi.getInstance().getListIDByName("inter_permission")
                    ) {
                        launcher.launch(Intent(this@MineActivity, SettingThemeActivity::class.java))
                        hideLoading()
                    }
                } else {
                    launcher.launch(Intent(this@MineActivity, SettingThemeActivity::class.java))
                    hideLoading()
                }
            }
            tvNofity.tap {
                LogEven.logEvent(this@MineActivity, "setting_noti_click", Bundle())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    if (checkPermission(Manifest.permission.POST_NOTIFICATIONS) && isExactAlarmPermissionGranted(
                            this@MineActivity
                        )
                    ) {
                        showLoading()
                        if (SharePrefRemote.get_config(
                                this@MineActivity, SharePrefRemote.inter_mine
                            ) && InterAdHelper.canShowNextAd(this@MineActivity) && AdsConsentManager.getConsentResult(
                                this@MineActivity
                            )
                        ) {
                            InterAdHelper.showListInterAd(
                                this@MineActivity, SharePrefRemote.get_config(
                                    this@MineActivity, SharePrefRemote.inter_mine
                                ), AdmobApi.getInstance().getListIDByName("inter_permission")
                            ) {
                                launcher.launch(
                                    Intent(
                                        this@MineActivity, NotificationActivity::class.java
                                    )
                                )
                                hideLoading()
                            }
                        } else {
                            launcher.launch(
                                Intent(
                                    this@MineActivity, NotificationActivity::class.java
                                )
                            )
                            hideLoading()
                        }
                    } else if (!checkPermission(Manifest.permission.POST_NOTIFICATIONS)) {
                        showDialogPermission(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
                    } else if (!isExactAlarmPermissionGranted(this@MineActivity)) {
                        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                            data = Uri.parse("package:${applicationContext.packageName}")
                        }
                        startActivity(intent)
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (!checkPermission(Manifest.permission.POST_NOTIFICATIONS)) {
                        showDialogPermission(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
                    } else {
                        showLoading()
                        if (SharePrefRemote.get_config(
                                this@MineActivity, SharePrefRemote.inter_mine
                            ) && InterAdHelper.canShowNextAd(this@MineActivity) && AdsConsentManager.getConsentResult(
                                this@MineActivity
                            )
                        ) {
                            InterAdHelper.showListInterAd(
                                this@MineActivity, SharePrefRemote.get_config(
                                    this@MineActivity, SharePrefRemote.inter_mine
                                ), AdmobApi.getInstance().getListIDByName("inter_permission")
                            ) {
                                launcher.launch(
                                    Intent(
                                        this@MineActivity, NotificationActivity::class.java
                                    )
                                )
                                hideLoading()
                            }
                        } else {
                            launcher.launch(
                                Intent(
                                    this@MineActivity, NotificationActivity::class.java
                                )
                            )
                            hideLoading()
                        }
                    }
                } else {
                    showLoading()
                    if (SharePrefRemote.get_config(
                            this@MineActivity, SharePrefRemote.inter_mine
                        ) && InterAdHelper.canShowNextAd(this@MineActivity) && AdsConsentManager.getConsentResult(
                            this@MineActivity
                        )
                    ) {
                        InterAdHelper.showListInterAd(
                            this@MineActivity, SharePrefRemote.get_config(
                                this@MineActivity, SharePrefRemote.inter_mine
                            ), AdmobApi.getInstance().getListIDByName("inter_permission")
                        ) {
                            launcher.launch(
                                Intent(
                                    this@MineActivity, NotificationActivity::class.java
                                )
                            )
                            hideLoading()
                        }
                    } else {
                        launcher.launch(Intent(this@MineActivity, NotificationActivity::class.java))
                        hideLoading()
                    }
                }
            }
            tvPassword.tap {
                LogEven.logEvent(this@MineActivity, "setting_password_click", Bundle())
                showLoading()
                if (SharePrefRemote.get_config(
                        this@MineActivity, SharePrefRemote.inter_mine
                    ) && InterAdHelper.canShowNextAd(this@MineActivity) && AdsConsentManager.getConsentResult(
                        this@MineActivity
                    )
                ) {
                    InterAdHelper.showListInterAd(
                        this@MineActivity, SharePrefRemote.get_config(
                            this@MineActivity, SharePrefRemote.inter_mine
                        ), AdmobApi.getInstance().getListIDByName("inter_permission")
                    ) {
                        if (SharePrefUtils.getPassword(this@MineActivity) == "") {
                            val intent = Intent(this@MineActivity, SetPasswordActivity::class.java)
                            intent.putExtra("TYPE", 0)
                            intent.putExtra("PASSWORD", " ")
                            launcher.launch(intent)
                        } else {
                            launcher.launch(
                                Intent(
                                    this@MineActivity, ChangePasswordSettingActivity::class.java
                                )
                            )
                        }
                        hideLoading()
                    }
                } else {
                    if (SharePrefUtils.getPassword(this@MineActivity) == "") {
                        val intent = Intent(this@MineActivity, SetPasswordActivity::class.java)
                        intent.putExtra("TYPE", 0)
                        intent.putExtra("PASSWORD", " ")
                        launcher.launch(intent)
                    } else {
                        launcher.launch(
                            Intent(
                                this@MineActivity, ChangePasswordSettingActivity::class.java
                            )
                        )
                    }
                    hideLoading()
                }

            }
        }
    }

    override fun onResume() {
        AppOpenManager.getInstance().enableAppResumeWithActivity(this::class.java)
        super.onResume()
    }

    override fun dataObservable() {
        helperMenu = HelperMenu(this)

        val prefs = getSharedPreferences("data", Context.MODE_PRIVATE)
        prefs.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences?, key: String?
    ) {
        if (key == null) return

        if (SharePrefUtils.isRated(this)) binding.tvRate.gone()
    }

    private fun isExactAlarmPermissionGranted(context: Context): Boolean {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager?.canScheduleExactAlarms() ?: false
        } else {
            return false
        }
    }

    private fun loadNative() {
        try {
            if (SharePrefRemote.get_config(
                    this, SharePrefRemote.native_mine
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
                nativeBuilder.setListIdAd(
                    AdmobApi.getInstance().getListIDByName("native_permission")
                )
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