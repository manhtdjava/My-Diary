package com.dailyjournal.diaryapp.secretdiary.ui.creatnew.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.base.BaseAdapter
import com.dailyjournal.diaryapp.secretdiary.databinding.ItemFontTextCreatnewBinding
import com.dailyjournal.diaryapp.secretdiary.databinding.ItemTextColorCreatnewBinding
import com.dailyjournal.diaryapp.secretdiary.model.FontTextModel

class FontTextCreatNewAdapter(val context: Context,
                              private val items: List<FontTextModel>,
                              private val onItemClick: (FontTextModel) -> Unit) : BaseAdapter<ItemFontTextCreatnewBinding , FontTextModel>()
    {
    private var selectedPosition: Int = -1
        private var selectedFont: String? = null

        fun setSelectedFont(fontCode: String) {
            val position = items.indexOfFirst { it.name == fontCode }
            if (position != -1) {
                setSelectedPosition(position)
            } else {
                Log.e("ColorAdapter", "Color code not found: $fontCode")
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
    ): ItemFontTextCreatnewBinding = ItemFontTextCreatnewBinding.inflate(inflater, parent, false)

    override fun creatVH(binding: ItemFontTextCreatnewBinding): RecyclerView.ViewHolder = EmojiVH(binding)

    inner class EmojiVH(binding: ItemFontTextCreatnewBinding) : BaseVH<FontTextModel>(binding) {
        override fun bind(data: FontTextModel) {
            super.bind(data)
            try {
                binding.ivIamge.setImageResource(data.image)
            } catch (_: Exception) {

            }
                if (adapterPosition == selectedPosition) {
                    binding.btnimg.setBackgroundResource(R.drawable.bg_selected_fonttext)
                } else {
                    binding.btnimg.setBackgroundResource(0)
                }

            binding.root.setOnClickListener {
                selectedFont = data.name
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
    fun updateList(newList: List<FontTextModel>) {
        listData.clear()
        listData.addAll(newList)
        notifyDataSetChanged()
    }
}