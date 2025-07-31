package com.dailyjournal.diaryapp.secretdiary.ui.creatnew.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.base.BaseAdapter
import com.dailyjournal.diaryapp.secretdiary.databinding.ItemTextColorCreatnewBinding
import com.dailyjournal.diaryapp.secretdiary.model.ColorTextModel

class ColorTextCreatNewAdapter(
    val context: Context,
    private val items: List<ColorTextModel>,
    private val onItemClick: (ColorTextModel) -> Unit
) : BaseAdapter<ItemTextColorCreatnewBinding, ColorTextModel>() {
    private var selectedPosition: Int = -1

    fun setSelectedColor(colorCode: String) {
        val position = items.indexOfFirst { it.name == colorCode }
        if (position != -1) {
            setSelectedPosition(position)
            notifyDataSetChanged()
        } else {
            setSelectedPosition(1)
            notifyDataSetChanged()
            Log.e("ColorAdapter", "Color code not found: $colorCode")
        }
    }
    fun getSelectedPosition(): Int {
        return selectedPosition
    }
    fun setSelectedPosition(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()
    }

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemTextColorCreatnewBinding = ItemTextColorCreatnewBinding.inflate(inflater, parent, false)

    override fun creatVH(binding: ItemTextColorCreatnewBinding): RecyclerView.ViewHolder =
        EmojiVH(binding)

    inner class EmojiVH(binding: ItemTextColorCreatnewBinding) : BaseVH<ColorTextModel>(binding) {
        override fun bind(data: ColorTextModel) {
            super.bind(data)
            try {
                binding.ivIamge.setImageResource(data.image)

            } catch (_: Exception) {

            }
            if (adapterPosition != 0) {
                if (adapterPosition == selectedPosition) {
                    binding.root.setBackgroundResource(R.drawable.bg_selected_emoji)
                } else {
                    binding.root.setBackgroundResource(R.drawable.bg_un_selected_emoji)
                }
            } else {
                binding.root.setBackgroundResource(0)
            }
            binding.root.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
                onItemClick(data)
                onItemClickListener(data)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<ColorTextModel>) {
        listData.clear()
        listData.addAll(newList)
        notifyDataSetChanged()
    }
}