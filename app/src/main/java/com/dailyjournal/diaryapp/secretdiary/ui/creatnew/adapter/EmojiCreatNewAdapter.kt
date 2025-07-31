package com.dailyjournal.diaryapp.secretdiary.ui.creatnew.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.base.BaseAdapter
import com.dailyjournal.diaryapp.secretdiary.databinding.ItemEmojiCreatnewBinding
import com.dailyjournal.diaryapp.secretdiary.model.EmojiModel

class EmojiCreatNewAdapter(val context: Context,
                           private val items: List<EmojiModel>,
                           private val onItemClick: (EmojiModel) -> Unit) : BaseAdapter<ItemEmojiCreatnewBinding , EmojiModel>() {
    private var selectedPosition: Int = -1
    fun setSelectedPosition(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()
    }
    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemEmojiCreatnewBinding = ItemEmojiCreatnewBinding.inflate(inflater, parent, false)

    override fun creatVH(binding: ItemEmojiCreatnewBinding): RecyclerView.ViewHolder = EmojiVH(binding)

    inner class EmojiVH(binding: ItemEmojiCreatnewBinding) : BaseVH<EmojiModel>(binding) {
        override fun bind(data: EmojiModel) {
            super.bind(data)
            try {
                binding.ivIamge.setImageResource(data.image)

            } catch (_: Exception) {

            }
            if (adapterPosition == selectedPosition) {
                binding.ll.setBackgroundResource(R.drawable.bg_selected_emoji)
            } else {
                binding.ll.setBackgroundResource(R.drawable.bg_un_selected_emoji)
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
    fun updateList(newList: List<EmojiModel>) {
        listData.clear()
        listData.addAll(newList)
        notifyDataSetChanged()
    }
}