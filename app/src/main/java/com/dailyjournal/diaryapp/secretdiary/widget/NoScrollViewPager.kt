package com.dailyjournal.diaryapp.secretdiary.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class NoScrollViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        return false
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return false
    }
}
