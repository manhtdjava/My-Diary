package com.dailyjournal.diaryapp.secretdiary.model

import com.dailyjournal.diaryapp.secretdiary.data.DiaryTable

sealed class DiaryItem {
    data class Header(val year: Int) : DiaryItem()
    data class Diary(val diary: DiaryTable) : DiaryItem()
    data class ads (val ads: Int): DiaryItem()
}
