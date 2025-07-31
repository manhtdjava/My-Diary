package com.dailyjournal.diaryapp.secretdiary.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dailyjournal.diaryapp.secretdiary.data.DiaryDatabase
import com.dailyjournal.diaryapp.secretdiary.data.DiaryTable
import com.dailyjournal.diaryapp.secretdiary.model.DiaryItem
import com.dailyjournal.diaryapp.secretdiary.repository.DiaryRepository
import kotlinx.coroutines.launch
import java.util.Calendar

class DiaryViewModel (application: Application) : AndroidViewModel(application) {
    private val repository: DiaryRepository

    val allCVs: LiveData<List<DiaryTable>>

    init {
        val diaryDao = DiaryDatabase.getDatabase(application).diaryDao()
        repository = DiaryRepository(diaryDao)

        allCVs = repository.getAllCVs()
    }

    fun insertPF(diaryTable: DiaryTable) = viewModelScope.launch {
        repository.insertPF(diaryTable)
    }

    fun deletePF(diaryTable: DiaryTable) = viewModelScope.launch {
        repository.deletePF(diaryTable)
    }

    suspend fun getPFById(id: Int): DiaryTable? {
        return repository.getPFById(id)
    }
    fun updateDiary(diaryTable: DiaryTable) = viewModelScope.launch {
        repository.updateDiary(diaryTable)
    }

//    suspend fun getDiariesByDate(dateInMillis: Long): List<DiaryTable> {
//        return repository.getDiariesByDate(dateInMillis)
//    }

}