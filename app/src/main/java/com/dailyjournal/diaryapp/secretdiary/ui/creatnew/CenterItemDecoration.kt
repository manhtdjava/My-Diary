package com.dailyjournal.diaryapp.secretdiary.ui.creatnew

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class CenterItemDecoration(private val spanCount: Int, private val totalItems: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position >= totalItems - 4) {
            val sidePadding = (parent.width - (view.layoutParams.width * 4)) / 2
            outRect.left = sidePadding / 4
            outRect.right = sidePadding / 4
        }
    }
}