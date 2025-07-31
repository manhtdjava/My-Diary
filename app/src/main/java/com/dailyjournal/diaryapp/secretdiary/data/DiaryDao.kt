package com.dailyjournal.diaryapp.secretdiary.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface DiaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(diaryTable: DiaryTable)

    @Query("SELECT * FROM diary_table WHERE id = :id")
    suspend fun getPFById(id: Int): DiaryTable?

    @Query("SELECT * FROM diary_table")
    fun getAllCVs(): LiveData<List<DiaryTable>>

    @Delete
    suspend fun delete(diaryTable: DiaryTable)

    @Query("SELECT COUNT(*) FROM diary_table")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGetID(diaryTable: DiaryTable): Long

    @Query("SELECT * FROM diary_table")
    fun getAllDiary(): MutableList<DiaryTable>

    @Query("SELECT * FROM diary_table WHERE timeInMillis = :timeInMillis")
    suspend fun getListByTimeInMillis(timeInMillis: Long): List<DiaryTable>

    @Query("SELECT * FROM diary_table WHERE DATE(timeInMillis / 1000, 'unixepoch') = DATE(:dateInMillis / 1000, 'unixepoch')")
    suspend fun getDiariesByDate(dateInMillis: Long): List<DiaryTable>
    @Update
    suspend fun updateDiary(diaryTable: DiaryTable)

}
