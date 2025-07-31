package com.dailyjournal.diaryapp.secretdiary.ui.language

import android.content.Intent
import android.view.View
import com.amazic.ads.service.AdmobApi
import com.amazic.ads.util.AdsConsentManager
import com.amazic.ads.util.manager.native_ad.NativeBuilder
import com.amazic.ads.util.manager.native_ad.NativeManager
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.base.BaseActivity
import com.dailyjournal.diaryapp.secretdiary.databinding.ActivityLangugeBinding
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefRemote
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefUtils
import com.dailyjournal.diaryapp.secretdiary.ui.home.MainActivity
import com.dailyjournal.diaryapp.secretdiary.utils.SystemUtil
import com.dailyjournal.diaryapp.secretdiary.utils.helper.Contants
import com.dailyjournal.diaryapp.secretdiary.widget.tap
import com.dailyjournal.diaryapp.secretdiary.widget.visible


class LanguageActivity : BaseActivity<ActivityLangugeBinding>() {
    private lateinit var adapter: LanguageAdapter
    private lateinit var iCode: String

    override fun setViewBinding(): ActivityLangugeBinding {
        return ActivityLangugeBinding.inflate(layoutInflater)
    }

    override fun initView() {
        loadNative()
        if (SharePrefUtils.getTheme(this) == 2 || SharePrefUtils.getTheme(
                this
            ) == 5
        ) {
            binding.tvLanguage.setTextColor(this.getColor(R.color.color_F4F5F5))
            binding.ivDone.setImageResource(R.drawable.ic_check_language_start_white)
            binding.ivBack.setImageResource(R.drawable.ic_back_white)
        } else {
            binding.tvLanguage.setTextColor(this.getColor(R.color.color_292B2B))
            binding.ivDone.setImageResource(R.drawable.ic_check_language_start)
            binding.ivBack.setImageResource(R.drawable.ic_back)
        }
        binding.root.setBackgroundResource(getBackground(SharePrefUtils.getTheme(this)))
        iCode = SystemUtil.getPreLanguage(this)
        adapter = LanguageAdapter(this, onClick = {
            adapter.setCheck(it.code)
            iCode = it.code
        })
        binding.recyclerView.adapter = adapter
    }

    override fun viewListener() {
        binding.ivBack.tap { finish() }
        binding.ivDone.tap {
            SystemUtil.saveLocal(baseContext, iCode)
            back()
        }
    }

    override fun dataObservable() {
        var pos = 0
        val list = SystemUtil.listLanguage()
        list.forEachIndexed { index, languageModel ->
            if (languageModel.code == iCode) {
                pos = index
                binding.ivDone.visible()
                return@forEachIndexed
            }
        }
        val temp = list[pos]
        temp.active = true
        list.removeAt(pos)
        list.add(0, temp)
        adapter.addList(list)
    }

    private fun back() {
        finishAffinity()
        val intent = Intent(this@LanguageActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
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
                nativeBuilder.setListIdAd(AdmobApi.getInstance().getListIDByName("native_mine"))
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