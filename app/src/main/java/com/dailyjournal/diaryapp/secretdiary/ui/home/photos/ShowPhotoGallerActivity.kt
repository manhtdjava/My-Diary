package com.dailyjournal.diaryapp.secretdiary.ui.home.photos

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.amazic.ads.service.AdmobApi
import com.amazic.ads.util.Admob
import com.amazic.ads.util.AdsConsentManager
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.base.BaseActivity
import com.dailyjournal.diaryapp.secretdiary.databinding.DialogPhotoGalleryBinding
import com.dailyjournal.diaryapp.secretdiary.model.PhotoItemModel
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefRemote
import com.dailyjournal.diaryapp.secretdiary.utils.SystemUtil
import com.dailyjournal.diaryapp.secretdiary.utils.helper.Contants
import com.dailyjournal.diaryapp.secretdiary.widget.tap
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ShowPhotoGallerActivity : BaseActivity<DialogPhotoGalleryBinding>() {
    private lateinit var adapter: PhotoPagerAdapter
    private lateinit var locale: Locale
    override fun setViewBinding(): DialogPhotoGalleryBinding {
        return DialogPhotoGalleryBinding.inflate(layoutInflater)
    }

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    override fun onDestroy() {
        if (handler != null && ::runnable.isInitialized) handler.removeCallbacks(runnable)
        super.onDestroy()
    }

    override fun initView() {
        locale = Locale(SystemUtil.getPreLanguage(this))
        val photoList: List<PhotoItemModel> =
            intent.getParcelableArrayListExtra("photoList") ?: emptyList()
        val startPosition = intent.getIntExtra("startIndex", 0)

        adapter = PhotoPagerAdapter(this, photoList) { day, dayWeekString, monthName ->
            binding.txtDay.text = day
            binding.txtRank.text = "${dayWeekString}, "
            binding.txtMonth.text = monthName
        }
        binding.viewPager.adapter = adapter
        binding.viewPager.setCurrentItem(startPosition, false)
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                val photo = photoList[position]
                val calendar = Calendar.getInstance().apply { timeInMillis = photo.timeInMillis }
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                val dayWeekString = when(dayOfWeek){
                    Calendar.SUNDAY -> getString(R.string.day_sun)
                    Calendar.MONDAY -> getString(R.string.day_mon)
                    Calendar.TUESDAY -> getString(R.string.day_tue)
                    Calendar.WEDNESDAY -> getString(R.string.day_wed)
                    Calendar.THURSDAY -> getString(R.string.day_Thu)
                    Calendar.FRIDAY -> getString(R.string.day_fri)
                    Calendar.SATURDAY -> getString(R.string.day_sat)
                    else -> ""
                }
                val monthName = SimpleDateFormat("MMM ", locale).format(calendar.time)

                binding.txtDay.text = day.toString()
                binding.txtRank.text = "${dayWeekString}, "
                binding.txtMonth.text = monthName
            }
        })
        if (SharePrefRemote.get_config(
                this, SharePrefRemote.banner_all
            ) && AdsConsentManager.getConsentResult(this)
        ) {

            runnable = Runnable {
                Admob.getInstance().loadBannerFloor(
                    this, AdmobApi.getInstance().listIDBannerAll
                )
                binding.include.visibility = View.VISIBLE
                handler.postDelayed(runnable, Contants.collap_reload_interval)
            }
            handler.post(runnable)

        } else {
            binding.include.visibility = View.GONE
        }
    }

    override fun viewListener() {
        binding.ivBack.tap {
            finish()
        }
    }

    override fun dataObservable() {
    }
}