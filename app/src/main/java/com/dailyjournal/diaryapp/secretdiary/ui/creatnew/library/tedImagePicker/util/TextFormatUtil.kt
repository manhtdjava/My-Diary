package com.dailyjournal.diaryapp.secretdiary.ui.creatnew.library.tedImagePicker.util

import java.text.DecimalFormat

class TextFormatUtil {
    companion object {
        @JvmStatic
        fun getMediaCountText(imageCountFormat: String, count: Int): String {
            val decimalCount = DecimalFormat("#,###").format(count)
            return String.format(imageCountFormat, decimalCount)
        }
    }
}