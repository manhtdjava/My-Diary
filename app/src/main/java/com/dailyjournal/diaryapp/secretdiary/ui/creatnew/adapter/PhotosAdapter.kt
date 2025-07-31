package com.dailyjournal.diaryapp.secretdiary.ui.creatnew.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Typeface
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.base.BaseAdapter
import com.dailyjournal.diaryapp.secretdiary.databinding.ItemImageBinding
import com.dailyjournal.diaryapp.secretdiary.databinding.ItemTextPhotoCreatnewBinding
import com.dailyjournal.diaryapp.secretdiary.model.FontTextModel
import com.dailyjournal.diaryapp.secretdiary.model.PhotoItemModel
import com.dailyjournal.diaryapp.secretdiary.model.PhotosModel
import com.dailyjournal.diaryapp.secretdiary.ui.home.photos.ItemPhotoAdapter
import com.dailyjournal.diaryapp.secretdiary.widget.tap

class PhotosAdapter(
    val context: Context,
    private var selectedFont: String? = null,
    private var selectColor: String? = null,
    private var selectDoct: Int? = null,
    private var selectAlignment: Int? = null,
    private var selectSize: Int? = null,
    private var selectStyle: Int? = null,
    private val onUri: (PhotoItemModel) -> Unit,
) : BaseAdapter<ItemTextPhotoCreatnewBinding, PhotosModel>() {

    private var selectedPosition: Int = -1
    private var mode: String? = null
    fun setMode(activityMode: String) {
        mode = activityMode
        notifyDataSetChanged()
    }

    fun addItem(
        newItem: PhotosModel,
        newTextColor: String,
        newFont: String,
        newDots: Int,
        newAligment: Int,
        newSize: Int,
        newStyle: Int
    ) {
        listData.add(newItem)
        selectColor = newTextColor
        selectedFont = newFont
        selectDoct = newDots
        selectSize = newSize
        selectAlignment = newAligment
        selectStyle = newStyle
        notifyItemInserted(listData.size - 1)
    }

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemTextPhotoCreatnewBinding = ItemTextPhotoCreatnewBinding.inflate(inflater, parent, false)

    override fun creatVH(binding: ItemTextPhotoCreatnewBinding): RecyclerView.ViewHolder =
        HastagVH(binding)

    inner class HastagVH(binding: ItemTextPhotoCreatnewBinding) : BaseVH<PhotosModel>(binding) {
        override fun bind(data: PhotosModel) {
            super.bind(data)
            try {
                val photoAdapter = ItemPhotoAdapterCreate(
                    context,
                    data.listUri,
                    { photo, position ->

                    },
                    { uri ->
                        val index = data.listUri.indexOf(uri)
                        if (index != -1) {
                            data.listUri.removeAt(index)
                            notifyItemChanged(adapterPosition)
                        }
                        if (data.listUri.isEmpty()) {
                            val positionInList = listData.indexOf(data)
                            if (positionInList != -1) {
                                listData.removeAt(positionInList)
                                notifyItemRemoved(positionInList)
                            }
                        }
                    },{

                    },{uri ->
                        onUri(uri)
                    },
                    mode
                )
                binding.rcv.apply {
                    layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    adapter = photoAdapter
                }


                Log.d("ted", "newPhotos $data  $mode")
                //Log.d("PhotosAdapter_getTable", "Mode: $mode, Description: ${data.description}")

                if (mode == "getTable") {
                    binding.etDescription.isFocusable = false
                    binding.etDescription.isCursorVisible = false
                    if (data.description.isNotEmpty()) {
                        binding.etDescription.setText(data.description)
                    } else {
                        binding.etDescription.visibility = View.GONE
                    }
                } else {
                    if (data.description.isNotEmpty()) {
                        binding.etDescription.setText(data.description)
                    } else {
                        binding.etDescription.visibility = View.VISIBLE
                        binding.etDescription.setHint(R.string.start_typing_here)
                    }

                    binding.etDescription.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                        }

                        override fun afterTextChanged(s: Editable?) {
                            data.description = s.toString()
                        }
                    })
                }
                when (selectSize) {
                    1 -> binding.etDescription.textSize = 16F
                    2 -> binding.etDescription.textSize = 19F
                    3 -> binding.etDescription.textSize = 22F
                    else -> binding.etDescription.textSize = 16F
                }
                selectedFont?.let { fontName ->
                    val fontId =
                        context.resources.getIdentifier(fontName, "font", context.packageName)
                    val customFont = ResourcesCompat.getFont(context, fontId)
                    val currentStyle = binding.etDescription.typeface?.style ?: Typeface.NORMAL
                    if (fontId != 0) {
                        if (customFont != null) {
                            binding.etDescription.typeface =
                                Typeface.create(customFont, currentStyle)
                        }
                    } else {
                        binding.etDescription.typeface =
                            Typeface.create(customFont, Typeface.NORMAL)
                    }
                }
                val currentTypeface = binding.etDescription.typeface ?: Typeface.DEFAULT
                when (selectStyle) {
                    1 -> binding.etDescription.setTypeface(currentTypeface, Typeface.BOLD)
                    2 -> binding.etDescription.setTypeface(currentTypeface, Typeface.ITALIC)
                    3 -> binding.etDescription.setTypeface(currentTypeface, Typeface.NORMAL)
                    else -> binding.etDescription.setTypeface(currentTypeface, Typeface.NORMAL)
                }

                selectColor?.let { color ->
                    try {
                        if (color != null) {
                            val parsedColor = Color.parseColor(color)
                            binding.etDescription.setHintTextColor(parsedColor)
                            binding.etDescription.setTextColor(parsedColor)
                        } else {
                            val parsedColor = Color.parseColor("#4A514F")
                            binding.etDescription.setHintTextColor(parsedColor)
                            binding.etDescription.setTextColor(parsedColor)
                        }
                    } catch (e: IllegalArgumentException) {
                        Log.e("PhotoAdapter", "Invalid color format: $color")
                    }
                }
                //Log.d("selectAlignmentPhoto", selectAlignment.toString())
                when (selectAlignment) {
                    1 -> binding.etDescription.gravity = Gravity.START
                    2 -> binding.etDescription.gravity = Gravity.CENTER
                    3 -> binding.etDescription.gravity = Gravity.END
                    else -> binding.etDescription.gravity = Gravity.START
                }
                if (selectDoct == 1) {
                    val drawable = ContextCompat.getDrawable(context, R.drawable.ic_dot_image)
                    drawable?.let {
                        binding.etDescription.setCompoundDrawablesWithIntrinsicBounds(
                            it,
                            null,
                            null,
                            null
                        )
                        binding.etDescription.compoundDrawablePadding = 16
                    }
                    notifyDataSetChanged()
                } else {
                    binding.etDescription.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    binding.etDescription.compoundDrawablePadding = 0
                    notifyDataSetChanged()
                }


            } catch (_: Exception) {

            }

            binding.root.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
                onItemClickListener(data)
            }
        }
    }

    fun getAllPhotos(): List<PhotosModel> {
        return listData
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<PhotosModel>) {
        listData.clear()
        listData.addAll(newList)
        notifyDataSetChanged()
    }

}
