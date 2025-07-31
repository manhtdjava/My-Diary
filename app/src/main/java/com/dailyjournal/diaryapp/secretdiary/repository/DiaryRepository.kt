package com.dailyjournal.diaryapp.secretdiary.repository

import androidx.lifecycle.LiveData
import com.dailyjournal.diaryapp.secretdiary.data.DiaryDao
import com.dailyjournal.diaryapp.secretdiary.data.DiaryTable

class DiaryRepository(private val diaryDao: DiaryDao) {

    fun getAllCVs(): LiveData<List<DiaryTable>> {
        return diaryDao.getAllCVs()
    }

    suspend fun insertPF(diaryTable: DiaryTable) {
        diaryDao.insert(diaryTable)
    }

    suspend fun getPFById(id: Int): DiaryTable? {
        return diaryDao.getPFById(id)
    }

    suspend fun deletePF(diaryTable: DiaryTable) {
        diaryDao.delete(diaryTable)
    }
    suspend fun updateDiary(diaryTable: DiaryTable) {
        diaryDao.updateDiary(diaryTable)
    }

//    suspend fun getDiariesByDate(dateInMillis: Long): List<DiaryTable> {
//        return diaryDao.getDiariesByDate(dateInMillis)
//    }
//

}