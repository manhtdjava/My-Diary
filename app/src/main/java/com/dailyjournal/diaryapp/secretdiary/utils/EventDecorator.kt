package com.holybible.biblestudy.dailybible.view.ui.calendar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.style.ForegroundColorSpan
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan

class EventDecorator(
    private val context: Context,
    private val drawable: Int,
    private val dates: HashSet<CalendarDay>,
    private val textColor: String,
    private val type: Int? = null
) : DayViewDecorator {
    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return dates.contains(day)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun decorate(view: DayViewFacade?) {
        if (type == 1) {
            view?.addSpan(DotSpan(5F, Color.parseColor(textColor)))

        } else {
            view?.setSelectionDrawable(context.resources.getDrawable(drawable))
            // white text color
            view?.addSpan(ForegroundColorSpan(Color.parseColor(textColor)))
        }
    }

}