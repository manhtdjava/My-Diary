package com.dailyjournal.diaryapp.secretdiary.ui.creatnew.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.base.BaseAdapter
import com.dailyjournal.diaryapp.secretdiary.databinding.ItemBackgroundCreatnewBinding
import com.dailyjournal.diaryapp.secretdiary.databinding.ItemEmojiCreatnewBinding
import com.dailyjournal.diaryapp.secretdiary.model.BackGroundModel
import com.dailyjournal.diaryapp.secretdiary.model.EmojiModel
import com.dailyjournal.diaryapp.secretdiary.widget.gone
import com.dailyjournal.diaryapp.secretdiary.widget.visible

class BackGroundCreatNewAdapter(val context: Context,
                                private val items: List<BackGroundModel>,
                                private val onItemClick: (BackGroundModel, Int) -> Unit,
    ) : BaseAdapter<ItemBackgroundCreatnewBinding , BackGroundModel>() {
    private var selectedPosition: Int = -1

    fun setSelectedPosition(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()
    }


    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemBackgroundCreatnewBinding = ItemBackgroundCreatnewBinding.inflate(inflater, parent, false)

    override fun creatVH(binding: ItemBackgroundCreatnewBinding): RecyclerView.ViewHolder = BackGroudVH(binding)

    inner class BackGroudVH(binding: ItemBackgroundCreatnewBinding) : BaseVH<BackGroundModel>(binding) {
        override fun bind(data: BackGroundModel) {
            super.bind(data)
            try {
                if (data.imageUri != null && data.id ==0) {
                    Log.d("ic_load_spinner", "${data.imageUri}")
                    Glide.with(context)
                        .load(data.imageUri)
                        .placeholder(R.drawable.ic_load_spinner)
                        .into(binding.imgBackGround)
                    binding.icAdd.visible()
                } else if (data.image != null) {
                    Log.d("ic_load_spinner", "${data.image}")
                     Glide.with(context)
                        .load(data.image)
                        .placeholder(R.drawable.ic_load_spinner)
                        .into(binding.imgBackGround)
                    binding.icAdd.gone()
                }
            } catch (_: Exception) {

            }

            if (adapterPosition == selectedPosition) {
                binding.imgBackGround.setBorderWidth(2F)
            } else {
                binding.imgBackGround.setBorderWidth(0F)
            }

            binding.root.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
                onItemClick(data, adapterPosition)
                onItemClickListener(data)
            }
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<BackGroundModel>) {
        listData.clear()
        listData.addAll(newList)
        notifyDataSetChanged()
    }
}