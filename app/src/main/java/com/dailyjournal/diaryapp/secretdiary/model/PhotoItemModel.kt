package com.dailyjournal.diaryapp.secretdiary.model
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID
@Parcelize
data class PhotoItemModel(
    val id: String = UUID.randomUUID().toString(),
    val uri: String,
    val timeInMillis: Long,
    var isRotated: Boolean = false
): Parcelable