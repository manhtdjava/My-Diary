package com.dailyjournal.diaryapp.secretdiary.ui.creatnew.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.base.BaseAdapter
import com.dailyjournal.diaryapp.secretdiary.databinding.ItemFontTextCreatnewBinding
import com.dailyjournal.diaryapp.secretdiary.databinding.ItemTextHastagCreatnewBinding
import com.dailyjournal.diaryapp.secretdiary.model.FontTextModel
import com.dailyjournal.diaryapp.secretdiary.model.HastagModel
import com.dailyjournal.diaryapp.secretdiary.model.PhotosModel

class HastagAdapter(
    val context: Context,
    private val items: List<HastagModel>,
    private var selectedFont: String? = null,
    private var selectColor: String? = null,
    private var selectSize: Int? = null,
    private var selectStyle: Int? = null,
    private val onItemClick: (Int) -> Unit,
) : BaseAdapter<ItemTextHastagCreatnewBinding, HastagModel>() {
    private var selectedPosition: Int = -1
    private var mode: String? = null

    fun setMode(activityMode: String) {
        mode = activityMode
        notifyDataSetChanged()
    }

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemTextHastagCreatnewBinding =
        ItemTextHastagCreatnewBinding.inflate(inflater, parent, false)

    override fun creatVH(binding: ItemTextHastagCreatnewBinding): RecyclerView.ViewHolder =
        HastagVH(binding)

    inner class HastagVH(binding: ItemTextHastagCreatnewBinding) : BaseVH<HastagModel>(binding) {
        override fun bind(data: HastagModel) {
            super.bind(data)
            try {
                binding.txtHastag.includeFontPadding = false
                Log.d("HastagAdapter", "Font: $selectedFont, Color: $selectColor")
                if (mode == "getTable") {
                    binding.ivDelete.visibility = View.GONE
                } else {
                    binding.ivDelete.visibility = View.VISIBLE
                }
                binding.txtHastag.text = "# " + data.hastag
                selectedFont?.let { fontName ->
                    val fontId = context.resources.getIdentifier(fontName, "font", context.packageName)
                    val customFont = ResourcesCompat.getFont(context, fontId)
                    val currentStyle = binding.txtHastag.typeface?.style ?: Typeface.NORMAL
                    if (fontId != 0) {
                        if (customFont != null) {
                            binding.txtHastag.typeface = Typeface.create(customFont, currentStyle)
                        }
                    } else {
                        binding.txtHastag.typeface = Typeface.create(customFont, Typeface.NORMAL)
                    }
                }
                val currentTypeface = binding.txtHastag.typeface ?: Typeface.DEFAULT
                when (selectStyle) {
                    1 -> binding.txtHastag.setTypeface(currentTypeface, Typeface.BOLD)
                    2 -> binding.txtHastag.setTypeface(currentTypeface, Typeface.ITALIC)
                    3 -> binding.txtHastag.setTypeface(currentTypeface, Typeface.NORMAL)
                    else -> binding.txtHastag.setTypeface(currentTypeface, Typeface.NORMAL)
                }

                selectColor?.let { color ->
                    try {
                        if (color != null) {
                            val parsedColor = Color.parseColor(color)
                            binding.txtHastag.setTextColor(parsedColor)
                        } else {
                            val parsedColor = Color.parseColor("#FF7375")
                            binding.txtHastag.setTextColor(parsedColor)
                        }
                    } catch (e: IllegalArgumentException) {
                        Log.e("HastagAdapter", "Invalid color format: $color")
                    }
                }
                when (selectSize) {
                    1 -> binding.txtHastag.textSize = 16F
                    2 -> binding.txtHastag.textSize = 19F
                    3 -> binding.txtHastag.textSize = 22F
                    else -> binding.txtHastag.textSize = 16F
                }

                binding.ivDelete.setOnClickListener {
                    //deleteItem(adapterPosition)
                    onItemClick(adapterPosition)
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

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<HastagModel>, newTextColor: String, newFont:String, newSize:Int, newStyle:Int) {
        listData.clear()
        selectColor = newTextColor
        selectedFont = newFont
        selectSize = newSize
        selectStyle = newStyle
        listData.addAll(newList)
        notifyDataSetChanged()
    }

    fun getAllHastag(): List<HastagModel> {
        return listData
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteItem(position: Int) {
        if (position >= 0 && position < listData.size) {
            listData.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, listData.size)
        }
    }

}
