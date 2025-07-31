package com.dailyjournal.diaryapp.secretdiary.data

import android.net.Uri
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.dailyjournal.diaryapp.secretdiary.model.HastagModel
import com.dailyjournal.diaryapp.secretdiary.model.PhotosModel
import com.dailyjournal.diaryapp.secretdiary.model.SoundsModel
import java.io.Serializable

@Entity(tableName = "diary_table")
@TypeConverters(ConvertersDiary::class)
data class DiaryTable(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timeInMillis: Long,
    val statusEmoji: Int,
    val title:String,
    val content : String,
    val hastagList: List<HastagModel>,
    val listSounds: List<SoundsModel>,
    val listPhoto: List<PhotosModel>,
    val backGround: Int,
    val backGroundUri: String,
    val alignment: Int,
    val textStyle:Int,
    val textSize: Int,
    val textDots: Int,
    val textColor: String,
    val fontTypeface:String,
    val selectedPhotos: MutableList<String> = mutableListOf()
)



