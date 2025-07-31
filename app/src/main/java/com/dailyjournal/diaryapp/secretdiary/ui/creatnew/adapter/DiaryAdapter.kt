package com.dailyjournal.diaryapp.secretdiary.ui.creatnew.adapter

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.amazic.ads.service.AdmobApi
import com.amazic.ads.util.manager.native_ad.NativeBuilder
import com.amazic.ads.util.manager.native_ad.NativeManager
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.data.DiaryTable
import com.dailyjournal.diaryapp.secretdiary.databinding.ItemAdsHomeBinding
import com.dailyjournal.diaryapp.secretdiary.databinding.ItemHeaderBinding
import com.dailyjournal.diaryapp.secretdiary.databinding.ItemHomeDiaryBinding
import com.dailyjournal.diaryapp.secretdiary.model.DiaryItem
import com.dailyjournal.diaryapp.secretdiary.utils.SystemUtil
import com.dailyjournal.diaryapp.secretdiary.utils.helper.Contants
import com.dailyjournal.diaryapp.secretdiary.widget.visible
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class DiaryAdapter(
    val context: Activity, val life: LifecycleOwner, private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val items = mutableListOf<DiaryItem>()
    private lateinit var locale: Locale
    private var listPosAds: ArrayList<Int> = ArrayList()
    fun updateList(newItems: List<DiaryItem>) {
        items.clear()
        items.addAll(newItems)
        locale = try {
            Locale(SystemUtil.getPreLanguage(context))
        } catch (e: Exception) {
            Locale.getDefault()
        }
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is DiaryItem.Header -> 0
            is DiaryItem.Diary -> {
                return 1
            }

            is DiaryItem.ads -> 2
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val binding = ItemHeaderBinding.inflate(LayoutInflater.from(context), parent, false)
            HeaderViewHolder(binding)
        } else {
            if (viewType == 2) {
                val binding =
                    ItemAdsHomeBinding.inflate(LayoutInflater.from(context), parent, false)
                AdsViewHolder(binding)
            } else {
                val binding =
                    ItemHomeDiaryBinding.inflate(LayoutInflater.from(context), parent, false)
                DiaryViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is DiaryItem.Header -> (holder as HeaderViewHolder).bind(item)
            is DiaryItem.Diary -> {
                (holder as DiaryViewHolder).bind(
                    item.diary, onClick, context, locale
                )
            }

            is DiaryItem.ads -> (holder as AdsViewHolder).bind(context, life)
        }
    }

    override fun getItemCount(): Int = items.size
    class AdsViewHolder(private val binding: ItemAdsHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(activity: Activity, life: LifecycleOwner) {
            binding.frAds.visible()
            val nativeBuilder = NativeBuilder(
                activity,
                binding.frAds,
                R.layout.ads_native_custom_shimer,
                R.layout.layout_native_custom,
                R.layout.layout_native_custom
            )
            Log.d("duyhung99", "bind: " + Contants.interval_reload_native)
            nativeBuilder.setListIdAd(AdmobApi.getInstance().getListIDByName("native_home"))
            val nativeManager = NativeManager(
                activity, life, nativeBuilder
            ).setIntervalReloadNative(Contants.interval_reload_native)
        }
    }

    class HeaderViewHolder(private val binding: ItemHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(header: DiaryItem.Header) {
            binding.txtHeader.text = header.year.toString()
        }
    }

    class DiaryViewHolder(private val binding: ItemHomeDiaryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: DiaryTable, onClick: (Int) -> Unit, context: Context, locale: Locale) {
            val calendar = Calendar.getInstance().apply { timeInMillis = data.timeInMillis }
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            val dayWeekString = when (dayOfWeek) {
                Calendar.SUNDAY -> context.getString(R.string.day_sun)
                Calendar.MONDAY -> context.getString(R.string.day_mon)
                Calendar.TUESDAY -> context.getString(R.string.day_tue)
                Calendar.WEDNESDAY -> context.getString(R.string.day_wed)
                Calendar.THURSDAY -> context.getString(R.string.day_Thu)
                Calendar.FRIDAY -> context.getString(R.string.day_fri)
                Calendar.SATURDAY -> context.getString(R.string.day_sat)
                else -> ""
            }
            val monthName = SimpleDateFormat("MMM", locale).format(calendar.time)
            binding.txtDay.text = day.toString()
            binding.txtRank.text = "${dayWeekString}, "
            binding.txtMonth.text = monthName
            binding.txtTitle.text = data.title
            binding.txtContent.text = data.content

            // Hiển thị emoji
            val emojiRes = when (data.statusEmoji) {
                0 -> R.drawable.emotion1
                1 -> R.drawable.emotion2
                2 -> R.drawable.emotion3
                3 -> R.drawable.emotion4
                4 -> R.drawable.emotion5
                5 -> R.drawable.emotion6
                6 -> R.drawable.emotion7
                7 -> R.drawable.emotion8
                8 -> R.drawable.emotion9
                else -> R.drawable.ic_emotion
            }
            binding.ivEmoji.setImageResource(emojiRes)

            binding.ivMusic.visibility =
                if (data.listSounds.isNotEmpty()) View.VISIBLE else View.GONE

            binding.root.setOnClickListener {
                onClick(data.id)
            }
        }
    }
}
