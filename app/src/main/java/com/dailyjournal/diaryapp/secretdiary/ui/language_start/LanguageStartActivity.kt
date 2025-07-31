package com.dailyjournal.diaryapp.secretdiary.ui.language_start

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.amazic.ads.service.AdmobApi
import com.amazic.ads.util.AdsConsentManager
import com.amazic.ads.util.manager.native_ad.NativeBuilder
import com.amazic.ads.util.manager.native_ad.NativeManager
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.base.BaseActivity
import com.dailyjournal.diaryapp.secretdiary.databinding.ActivityLangugeStartBinding
import com.dailyjournal.diaryapp.secretdiary.logenven.LogEven
import com.dailyjournal.diaryapp.secretdiary.model.LanguageModel
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefRemote
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefUtils
import com.dailyjournal.diaryapp.secretdiary.ui.intro.IntroActivity
import com.dailyjournal.diaryapp.secretdiary.utils.SystemUtil
import com.dailyjournal.diaryapp.secretdiary.utils.helper.Contants
import com.dailyjournal.diaryapp.secretdiary.widget.tap
import com.dailyjournal.diaryapp.secretdiary.widget.visible
import java.util.Locale


class LanguageStartActivity : BaseActivity<ActivityLangugeStartBinding>() {
    private lateinit var adapter: LanguageStartAdapter
    private var listLanguage: ArrayList<LanguageModel> = ArrayList()
    private var codeLang = ""

    override fun setViewBinding(): ActivityLangugeStartBinding {
        return ActivityLangugeStartBinding.inflate(layoutInflater)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    override fun initView() {
        loadNative()
        LogEven.logEvent(this, "language_fo_open", Bundle())
        val count = SharePrefUtils.getSplashOpenCount(this)
        if (count <= 10) {
            LogEven.logEvent(this, "language_fo_open_$count", Bundle())
        }

        if (SharePrefUtils.getTheme(this) == 2 || SharePrefUtils.getTheme(
                this
            ) == 5
        ) {
            binding.tvLanguage.setTextColor(this.getColor(R.color.color_F4F5F5))
            binding.ivDone.setImageResource(R.drawable.ic_check_language_start_white)
        } else {
            binding.tvLanguage.setTextColor(this.getColor(R.color.color_292B2B))
            binding.ivDone.setImageResource(R.drawable.ic_check_language_start)
        }
        binding.root.setBackgroundResource(getBackground(SharePrefUtils.getTheme(this)))
        adapter = LanguageStartAdapter(this, onClick = {
            LogEven.logEvent(this, "language_fo_choose", Bundle())
            val count = SharePrefUtils.getSplashOpenCount(this)
            if (count <= 10) {
                LogEven.logEvent(this, "language_fo_choose_$count", Bundle())
            }
            codeLang = it.code
            adapter.setCheck(it.code)
        })

        binding.recyclerView.adapter = adapter
    }

    override fun viewListener() {
        binding.ivDone.tap {
            SystemUtil.saveLocal(baseContext, codeLang)
            val bundle = Bundle()
            bundle.putString("language_name", codeLang)
            LogEven.logEvent(this, "language_fo_save_click", bundle)
            val count = SharePrefUtils.getSplashOpenCount(this)
            if (count <= 10) {
                LogEven.logEvent(this, "language_fo_save_click_$count", Bundle())
            }
            startActivity(Intent(this@LanguageStartActivity, IntroActivity::class.java))
            finish()
        }
    }

    override fun dataObservable() {
        setCodeLanguage()
        initData()
    }

    private fun setCodeLanguage() {
        val codelangDefault = Locale.getDefault().language
        val langDefault = arrayOf("fr", "pt", "es", "de", "in", "en", "hi", "vi", "ja")

        codeLang = if (SystemUtil.getPreLanguage(this).equals("")) {
            if (!mutableListOf(*langDefault).contains(codelangDefault)) {
                "en"
            } else {
                codelangDefault
            }
        } else {
            SystemUtil.getPreLanguage(this)
        }
    }

    private fun initData() {
        var pos = 0
        listLanguage.clear()
        listLanguage.addAll(SystemUtil.listLanguage())
        listLanguage.forEachIndexed { index, languageModel ->
            if (languageModel.code == codeLang) {
                pos = index
                binding.ivDone.visible()
                return@forEachIndexed
            }
        }
        val temp = listLanguage[pos]
        temp.active = true
        listLanguage.removeAt(pos)
        listLanguage.add(0, temp)

        adapter.addList(listLanguage)
    }

    private fun loadNative() {
        try {
            if (SharePrefRemote.get_config(
                    this,
                    SharePrefRemote.native_language
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
                nativeBuilder.setListIdAd(AdmobApi.getInstance().listIDNativeLanguage)
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