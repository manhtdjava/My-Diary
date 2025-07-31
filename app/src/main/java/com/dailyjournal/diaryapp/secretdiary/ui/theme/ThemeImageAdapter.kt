package com.dailyjournal.diaryapp.secretdiary.ui.theme

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import com.asksira.loopingviewpager.LoopingPagerAdapter
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.model.ThemeModel
import com.dailyjournal.diaryapp.secretdiary.widget.gone
import com.dailyjournal.diaryapp.secretdiary.widget.visible

class ThemeImageAdapter(
    itemList: ArrayList<ThemeModel>,
    isInfinite: Boolean,
    private var type: Int
) : LoopingPagerAdapter<ThemeModel>(itemList, isInfinite) {

    fun setPosSelect(type: Int) {
        this.type = type
        notifyDataSetChanged()
    }

    override fun inflateView(
        viewType: Int,
        container: ViewGroup,
        listPosition: Int
    ): View {
        return LayoutInflater.from(container.context)
            .inflate(R.layout.item_your_theme, container, false)
    }

    override fun bindView(
        convertView: View,
        listPosition: Int,
        viewType: Int
    ) {
        val img = convertView.findViewById<ImageView>(R.id.img)
        val v1 = convertView.findViewById<View>(R.id.view_1)
        val v2 = convertView.findViewById<View>(R.id.view_2)
        val cv = convertView.findViewById<CardView>(R.id.card_view)
        img.setImageResource(itemList!![listPosition].image)
        if (type == itemList!![listPosition].type) {
            v1.gone()
            v2.gone()
            cv.visible()
        } else {
            v1.visible()
            v2.visible()
            cv.gone()
        }
    }
}