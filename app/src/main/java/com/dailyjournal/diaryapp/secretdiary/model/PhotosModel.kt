package com.dailyjournal.diaryapp.secretdiary.model



data class PhotosModel(
    val listUri: MutableList<PhotoItemModel> = mutableListOf(),
    var description: String = "",
    )