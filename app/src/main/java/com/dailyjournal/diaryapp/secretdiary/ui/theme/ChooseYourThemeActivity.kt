package com.dailyjournal.diaryapp.secretdiary.ui.theme

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.amazic.ads.service.AdmobApi
import com.amazic.ads.util.AdsConsentManager
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.base.BaseActivity
import com.dailyjournal.diaryapp.secretdiary.databinding.ActivityChooseYourThemeBinding
import com.dailyjournal.diaryapp.secretdiary.logenven.LogEven
import com.dailyjournal.diaryapp.secretdiary.model.ThemeModel
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefRemote
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefUtils
import com.dailyjournal.diaryapp.secretdiary.ui.password.SetPasswordActivity
import com.dailyjournal.diaryapp.secretdiary.utils.helper.Contants
import com.dailyjournal.diaryapp.secretdiary.widget.tap


class ChooseYourThemeActivity : BaseActivity<ActivityChooseYourThemeBinding>() {
    private lateinit var listTheme: ArrayList<ThemeModel>
    private lateinit var adapter: ThemeImageAdapter
    private var type = 0


    override fun setViewBinding(): ActivityChooseYourThemeBinding {
        return ActivityChooseYourThemeBinding.inflate(layoutInflater)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (handler != null && ::runnable.isInitialized) handler.removeCallbacks(runnable)
    }

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    override fun initView() {
        if (SharePrefRemote.get_config(
                this, SharePrefRemote.banner_all
            ) && AdsConsentManager.getConsentResult(this)
        ) {

            runnable = Runnable {
                AdmobApi.getInstance().loadBanner(this)
                binding.include.visibility = View.VISIBLE
                handler.postDelayed(runnable, Contants.collap_reload_interval)
            }
            handler.post(runnable)

        } else {
            binding.include.visibility = View.GONE
        }
        binding.root.setBackgroundResource(getBackground(SharePrefUtils.getTheme(this)))
        listTheme = ArrayList()
        listTheme.add(ThemeModel(R.drawable.img_choose_theme_0, 0))
        listTheme.add(ThemeModel(R.drawable.img_choose_theme_1, 1))
        listTheme.add(ThemeModel(R.drawable.img_choose_theme_2, 2))
        listTheme.add(ThemeModel(R.drawable.img_choose_theme_3, 3))
        listTheme.add(ThemeModel(R.drawable.img_choose_theme_4, 4))
        listTheme.add(ThemeModel(R.drawable.img_choose_theme_5, 5))

        adapter = ThemeImageAdapter(listTheme, true, binding.viewPager.currentItem)
        binding.viewPager.adapter = adapter
        binding.viewPager.offscreenPageLimit = 3
        binding.viewPager.clipChildren = false
        binding.viewPager.clipToPadding = false

        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                adapter.setPosSelect(position - 1)
                type = position - 1
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

    }

    override fun viewListener() {
        binding.apply {
            tvLater.tap {
                LogEven.logEvent(this@ChooseYourThemeActivity, "later_click", Bundle())
                val intent = Intent(this@ChooseYourThemeActivity, SetPasswordActivity::class.java)
                intent.putExtra("TYPE", 0)
                intent.putExtra("PASSWORD", " ")
                startActivity(intent)
                finishAffinity()

            }
            tvSelect.tap {
                val bundle = Bundle()
                bundle.putInt("theme_name:", type)
                LogEven.logEvent(this@ChooseYourThemeActivity, "select_click", bundle)
                val intent = Intent(this@ChooseYourThemeActivity, SetPasswordActivity::class.java)
                intent.putExtra("TYPE", 0)
                intent.putExtra("PASSWORD", "")
                startActivity(intent)
                finishAffinity()
                SharePrefUtils.setTheme(this@ChooseYourThemeActivity, type)
            }
        }

    }

    override fun dataObservable() {
    }

    override fun onPermissionGranted() {

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }


}