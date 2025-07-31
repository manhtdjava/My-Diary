package com.dailyjournal.diaryapp.secretdiary.model

data class SoundsModel(
    val fileName: String,
    val filePath: String,
    var duration: Long ,
    var progress: Int,
    var isPlay: Boolean = false,
    var isPaused: Boolean = false
)
