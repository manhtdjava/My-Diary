package com.dailyjournal.diaryapp.secretdiary.data

import androidx.room.TypeConverter
import com.dailyjournal.diaryapp.secretdiary.model.HastagModel
import com.dailyjournal.diaryapp.secretdiary.model.PhotoItemModel
import com.dailyjournal.diaryapp.secretdiary.model.PhotosModel
import com.dailyjournal.diaryapp.secretdiary.model.SoundsModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class ConvertersDiary {
    private val gson = Gson()

    @TypeConverter
    fun fromPhotoItemModelList(value: List<PhotoItemModel>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toPhotoItemModelList(value: String): List<PhotoItemModel> {
        val type = object : TypeToken<List<PhotoItemModel>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromString(value: String): MutableList<String> {
        return value.split(",").map { it.trim() }.toMutableList()
    }

    @TypeConverter
    fun fromList(list: MutableList<String>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }

    @TypeConverter
    fun fromHastagList(list: List<HastagModel>?): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toHastagList(json: String?): List<HastagModel>? {
        val type = object : TypeToken<List<HastagModel>>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun fromSoundsList(list: List<SoundsModel>?): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toSoundsList(json: String?): List<SoundsModel>? {
        val type = object : TypeToken<List<SoundsModel>>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun fromPhotosList(list: List<PhotosModel>?): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toPhotosList(json: String?): List<PhotosModel>? {
        val type = object : TypeToken<List<PhotosModel>>() {}.type
        return gson.fromJson(json, type)
    }

}