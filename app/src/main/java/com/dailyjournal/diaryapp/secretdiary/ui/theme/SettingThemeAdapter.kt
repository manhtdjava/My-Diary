package com.dailyjournal.diaryapp.secretdiary.ui.theme

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dailyjournal.diaryapp.secretdiary.base.BaseAdapter
import com.dailyjournal.diaryapp.secretdiary.databinding.ItemYourThemeV2Binding
import com.dailyjournal.diaryapp.secretdiary.model.ThemeModel
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefUtils
import com.dailyjournal.diaryapp.secretdiary.widget.gone
import com.dailyjournal.diaryapp.secretdiary.widget.visible


class SettingThemeAdapter(
    val context: Context,
    val onClick: (lang: ThemeModel) -> Unit
) : BaseAdapter<ItemYourThemeV2Binding, ThemeModel>() {

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemYourThemeV2Binding {
        return ItemYourThemeV2Binding.inflate(inflater, parent, false)
    }

    override fun creatVH(binding: ItemYourThemeV2Binding): RecyclerView.ViewHolder =
        ThemeVH(binding)

    inner class ThemeVH(binding: ItemYourThemeV2Binding) : BaseVH<ThemeModel>(binding) {
        override fun onItemClickListener(data: ThemeModel) {
            super.onItemClickListener(data)
            onClick.invoke(data)
        }

        override fun bind(data: ThemeModel) {
            super.bind(data)
            if (SharePrefUtils.getTheme(context) == data.type) {
                binding.cardView.visible()
            } else {
                binding.cardView.gone()
            }
            binding.img.setImageResource(data.image)

        }
    }
}