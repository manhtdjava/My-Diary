package com.dailyjournal.diaryapp.secretdiary.ui.home.photos

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amazic.ads.service.AdmobApi
import com.amazic.ads.util.manager.native_ad.NativeBuilder
import com.amazic.ads.util.manager.native_ad.NativeManager
import com.bumptech.glide.Glide
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.base.BaseAdapter
import com.dailyjournal.diaryapp.secretdiary.data.DiaryTable
import com.dailyjournal.diaryapp.secretdiary.databinding.ItemPhotoFragmentBinding
import com.dailyjournal.diaryapp.secretdiary.model.PhotoItemModel
import com.dailyjournal.diaryapp.secretdiary.utils.SystemUtil
import com.dailyjournal.diaryapp.secretdiary.utils.helper.Contants
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FragmentPhotoAdapter(
    val context: Activity,
    val life: LifecycleOwner,
    private val selectedPhotos: MutableList<String>,
    private val onClick: (Int) -> Unit,
    private val onClickPosition: (String) -> Unit,
    private val onPhotoClick: (PhotoItemModel, String) -> Unit,
) : BaseAdapter<ItemPhotoFragmentBinding, DiaryTable>() {
    private var selectedPosition: Int = -1
    private lateinit var locale: Locale
    override fun createBinding(
        inflater: LayoutInflater, parent: ViewGroup, viewType: Int
    ): ItemPhotoFragmentBinding {
        return ItemPhotoFragmentBinding.inflate(inflater, parent, false)
    }

    override fun creatVH(binding: ItemPhotoFragmentBinding): RecyclerView.ViewHolder =
        DiaryVH(binding)

    inner class DiaryVH(binding: ItemPhotoFragmentBinding) : BaseVH<DiaryTable>(binding) {
        override fun bind(data: DiaryTable) {
            super.bind(data)
            if (data.id == -1) {
                binding.frAds.visibility = View.VISIBLE
                binding.ctlView.visibility = View.GONE
                val nativeBuilder = NativeBuilder(
                    context,
                    binding.frAds,
                    R.layout.ads_native_small_top_shimer,
                    R.layout.layout_native_small_top,
                    R.layout.layout_native_small_top
                )
                nativeBuilder.setListIdAd(AdmobApi.getInstance().getListIDByName("native_home"))
                val nativeManager = NativeManager(
                    context, life, nativeBuilder
                ).setIntervalReloadNative(Contants.interval_reload_native)
            } else {
                binding.frAds.visibility = View.GONE
                binding.ctlView.visibility = View.VISIBLE
                try {
                    val calendar = Calendar.getInstance().apply {
                        timeInMillis = data.timeInMillis
                        Log.d("timeInMillis", data.timeInMillis.toString())
                    }
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

                    val photoAdapter = ItemPhotoAdapter(context,
                        data.listPhoto.flatMap { it.listUri }
                            ?.sortedByDescending { it.timeInMillis } ?: emptyList(),
                        selectedPhotos,
                        { photo, position ->
                            onPhotoClick(photo, photo.id)
                        },
                        { uri ->
                            if (selectedPhotos.contains(uri.id)) {
                                selectedPhotos.remove(uri.id)
                            } else {
                                selectedPhotos.add(uri.id)
                            }
                            onClickPosition(uri.id)
                            notifyDataSetChanged()
                        })

                    binding.rcv.apply {
                        layoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                        adapter = photoAdapter
                    }
                } catch (_: Exception) {

                }
                binding.root.setOnClickListener {
                    val previousPosition = selectedPosition
                    selectedPosition = adapterPosition
                    notifyItemChanged(previousPosition)
                    notifyItemChanged(selectedPosition)
                    onClick(data.id)
                    onItemClickListener(data)
                }
            }
        }
    }

    private fun showPhotoFullscreen(uri: String) {
        val dialog = Dialog(context)
        val imageView = ImageView(context).apply {
            Glide.with(context).load(uri).into(this)
            scaleType = ImageView.ScaleType.FIT_CENTER
        }
        dialog.setContentView(imageView)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<DiaryTable>) {
        listData.clear()
        listData.addAll(newList)
        locale = try {
            Locale(SystemUtil.getPreLanguage(context))
        } catch (e: Exception) {
            Locale.getDefault()
        }
        notifyDataSetChanged()
    }
}
