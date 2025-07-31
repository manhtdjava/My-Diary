package com.dailyjournal.diaryapp.secretdiary.ui.creatnew.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.base.BaseAdapter
import com.dailyjournal.diaryapp.secretdiary.data.DiaryTable
import com.dailyjournal.diaryapp.secretdiary.databinding.ItemCalenderDiaryBinding

class DiaryCalenderAdapter(
    val context: Context, private val onClick: (Int) -> Unit
) : BaseAdapter<ItemCalenderDiaryBinding, DiaryTable>() {
    override fun createBinding(
        inflater: LayoutInflater, parent: ViewGroup, viewType: Int
    ): ItemCalenderDiaryBinding {
        return ItemCalenderDiaryBinding.inflate(inflater, parent, false)
    }

    override fun creatVH(binding: ItemCalenderDiaryBinding): RecyclerView.ViewHolder =
        DiaryVH(binding)

    inner class DiaryVH(binding: ItemCalenderDiaryBinding) : BaseVH<DiaryTable>(binding) {
        override fun bind(data: DiaryTable) {
            super.bind(data)
            try {
                binding.txtTitle.text = data.title
                binding.txtContent.text = data.content
                when (data.statusEmoji) {
                    0 -> binding.ivEmoji.setImageResource(R.drawable.emotion1)
                    1 -> binding.ivEmoji.setImageResource(R.drawable.emotion2)
                    2 -> binding.ivEmoji.setImageResource(R.drawable.emotion3)
                    3 -> binding.ivEmoji.setImageResource(R.drawable.emotion4)
                    4 -> binding.ivEmoji.setImageResource(R.drawable.emotion5)
                    5 -> binding.ivEmoji.setImageResource(R.drawable.emotion6)
                    6 -> binding.ivEmoji.setImageResource(R.drawable.emotion7)
                    7 -> binding.ivEmoji.setImageResource(R.drawable.emotion8)
                    8 -> binding.ivEmoji.setImageResource(R.drawable.emotion9)
                    else -> binding.ivEmoji.setImageResource(R.drawable.ic_emotion)
                }

            } catch (_: Exception) {

            }

            binding.root.setOnClickListener {
                onClick.invoke(data.id)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<DiaryTable>) {
        listData.clear()
        listData.addAll(newList)
        notifyDataSetChanged()
    }
}