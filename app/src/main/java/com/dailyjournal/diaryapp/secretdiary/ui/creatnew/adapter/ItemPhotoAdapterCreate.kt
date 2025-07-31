package com.dailyjournal.diaryapp.secretdiary.ui.creatnew.adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.databinding.ItemImage2Binding
import com.dailyjournal.diaryapp.secretdiary.databinding.ItemImageBinding
import com.dailyjournal.diaryapp.secretdiary.model.PhotoItemModel
import com.bumptech.glide.request.target.Target
import com.dailyjournal.diaryapp.secretdiary.widget.tap

class ItemPhotoAdapterCreate(
    private val context: Context,
    private val uriList: List<PhotoItemModel>,
    private val onPhotoClick: (PhotoItemModel,Int) -> Unit,
    private val onPhotoDeleteClick: (PhotoItemModel) -> Unit,
    private val onPhotoRotateClick: (PhotoItemModel) -> Unit,
    private val onPhotoRomClick: (PhotoItemModel) -> Unit,
    private var mode: String? = null
    ) : RecyclerView.Adapter<ItemPhotoAdapterCreate.PhotoViewHolder>() {

    inner class PhotoViewHolder(val binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(uri: PhotoItemModel, position: Int) {
            Log.d("binding.ivMedia",uri.uri)
            if (mode == "getTable"){
                binding.ll1.visibility = View.GONE
                binding.ivMedia.setBorderWidth(0F)
            }
            binding.ivMedia.scaleX = if (uri.isRotated) -1f else 1f
            Glide.with(context)
                .load(uri.uri)
                .override(Target.SIZE_ORIGINAL)
                .into(binding.ivMedia)

            binding.ivDelete.tap {
                (uriList as? MutableList)?.let {
                    it.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, it.size)
                    onPhotoDeleteClick(uri)
                }
            }
            binding.ivRotate.tap {
                uri.isRotated = !uri.isRotated
                binding.ivMedia.scaleX = if (uri.isRotated) -1f else 1f
                onPhotoRotateClick(uri)
            }
            binding.ivZoom.tap {
                onPhotoRomClick(uri)
            }
            binding.root.setOnClickListener {
                   onPhotoClick(uri, position)
            }
            binding.root.setOnLongClickListener {
                Log.d("ItemPhotoAdapter", "Long clicked: $uri")
                true
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(context), parent, false)
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val uri = uriList[position]
        holder.bind(uri, position)
    }

    override fun getItemCount(): Int = uriList.size

}

