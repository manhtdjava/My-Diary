package com.dailyjournal.diaryapp.secretdiary.ui.home.photos

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dailyjournal.diaryapp.secretdiary.databinding.ItemImage2Binding
import com.dailyjournal.diaryapp.secretdiary.model.PhotoItemModel

class ItemPhotoAdapter(
    private val context: Context,
    private val uriList: List<PhotoItemModel>,
    private val selectedPhotos: MutableList<String>,
    private val onPhotoClick: (PhotoItemModel,Int) -> Unit,
    private val onPhotoLongClick: (PhotoItemModel) -> Unit,

    ) : RecyclerView.Adapter<ItemPhotoAdapter.PhotoViewHolder>() {

    inner class PhotoViewHolder(val binding: ItemImage2Binding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(uri: PhotoItemModel, position: Int) {
            binding.ivMedia.scaleX = if (uri.isRotated) -1f else 1f

            Glide.with(context)
                .load(uri.uri)
                .centerCrop()
                .into(binding.ivMedia)

                if (selectedPhotos.contains(uri.id)) {
                binding.ivMedia.setBorderWidth(4F)
                binding.ivCheck.visibility= View.VISIBLE
            } else {
                binding.ivMedia.setBorderWidth(0F)
                binding.ivCheck.visibility= View.GONE
            }
            binding.root.setOnClickListener {
//                if (selectedPhotos.isEmpty()) {
                   onPhotoClick(uri, position)
//                } else {
                    //toggleSelection(uri)
              //  }
            }
            binding.root.setOnLongClickListener {
                toggleSelection(uri)
                Log.d("ItemPhotoAdapter", "Long clicked: $uri")
                onPhotoLongClick(uri)
                true
            }

        }
        private fun toggleSelection(uri: PhotoItemModel) {
            if (selectedPhotos.contains(uri.id)) {
                selectedPhotos.remove(uri.id)
            } else {
                selectedPhotos.add(uri.id)
            }
            notifyItemChanged(adapterPosition)
            onPhotoLongClick(uri)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemImage2Binding.inflate(LayoutInflater.from(context), parent, false)
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val uri = uriList[position]
        holder.bind(uri, position)
    }

    override fun getItemCount(): Int = uriList.size

}

