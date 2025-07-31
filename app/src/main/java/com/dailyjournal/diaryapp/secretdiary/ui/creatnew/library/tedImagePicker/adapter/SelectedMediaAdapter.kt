package com.dailyjournal.diaryapp.secretdiary.ui.creatnew.library.tedImagePicker.adapter

import android.app.Activity
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.bumptech.glide.Glide
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.databinding.ItemSelectedMediaBinding
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.library.tedImagePicker.base.BaseRecyclerViewAdapter
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.library.tedImagePicker.base.BaseViewHolder

internal class SelectedMediaAdapter :
    BaseRecyclerViewAdapter<Uri, SelectedMediaAdapter.MediaViewHolder>() {

    var onClearClickListener: ((Uri) -> Unit)? = null
    private var layoutManager: RecyclerView.LayoutManager? = null

    override fun getViewHolder(parent: ViewGroup, viewType: ViewType) = MediaViewHolder(parent)

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        layoutManager = recyclerView.layoutManager
    }


    inner class MediaViewHolder(parent: ViewGroup) :
        BaseViewHolder<ItemSelectedMediaBinding, Uri>(parent, R.layout.item_selected_media) {

        init {
            binding.ivClear.setOnClickListener {
                val item = getItem(adapterPosition.takeIf { it != NO_POSITION }
                    ?: return@setOnClickListener)
                onClearClickListener?.invoke(item)
            }
        }

        override fun bind(data: Uri) {
            binding.uri = data
        }

        override fun recycled() {
            if ((itemView.context as? Activity)?.isDestroyed == true) {
                return
            }
            Glide.with(itemView).clear(binding.ivImage)
        }
    }

}