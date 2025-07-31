package com.dailyjournal.diaryapp.secretdiary.ui.home.photos

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.base.BaseAdapter
import com.dailyjournal.diaryapp.secretdiary.databinding.ItemPhotoFullscreenBinding
import com.dailyjournal.diaryapp.secretdiary.model.BackGroundModel
import com.dailyjournal.diaryapp.secretdiary.model.PhotoItemModel
import com.dailyjournal.diaryapp.secretdiary.utils.SystemUtil
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PhotoPagerAdapter(
    private val context: Context,
    private val items: List<PhotoItemModel>,
    private val onTimeInMillis : (String, String,String) -> Unit
): BaseAdapter<ItemPhotoFullscreenBinding, PhotoItemModel>() {
    init {
        listData = items.toMutableList()
    }
    private var selectedPosition: Int = -1
    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemPhotoFullscreenBinding = ItemPhotoFullscreenBinding.inflate(inflater, parent, false)

    override fun creatVH(binding: ItemPhotoFullscreenBinding): RecyclerView.ViewHolder = BackGroudVH(binding)

    inner class BackGroudVH(binding: ItemPhotoFullscreenBinding) : BaseVH<PhotoItemModel>(binding) {
        override fun bind(data: PhotoItemModel) {
            super.bind(data)
            try {
                binding.ivPhoto.scaleX = if (data.isRotated) -1f else 1f
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = data.timeInMillis
                    Log.d("timeInMillis", data.timeInMillis.toString())
                }
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                val dayWeekString = when(dayOfWeek){
                    Calendar.SUNDAY -> context.getString(R.string.day_sun)
                    Calendar.MONDAY -> context.getString(R.string.day_mon)
                    Calendar.TUESDAY -> context.getString(R.string.day_tue)
                    Calendar.WEDNESDAY -> context.getString(R.string.day_wed)
                    Calendar.THURSDAY -> context.getString(R.string.day_Thu)
                    Calendar.FRIDAY -> context.getString(R.string.day_fri)
                    Calendar.SATURDAY -> context.getString(R.string.day_sat)
                    else -> ""
                }
                val locale = Locale(SystemUtil.getPreLanguage(context))
                val monthName = SimpleDateFormat("MMM ", locale).format(calendar.time)
                onTimeInMillis(day.toString(), dayWeekString, monthName)
                Glide.with(context)
                    .load(data.uri)
                    .into(binding.ivPhoto)
                Log.d("binding.ivPhoto", "${data.uri}")
            } catch (_: Exception) {

            }

            binding.root.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition
//                notifyItemChanged(previousPosition)
//                notifyItemChanged(selectedPosition)
                onItemClickListener(data)
            }
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<PhotoItemModel>) {
        listData.clear()
        listData.addAll(newList)
        notifyDataSetChanged()
    }
}