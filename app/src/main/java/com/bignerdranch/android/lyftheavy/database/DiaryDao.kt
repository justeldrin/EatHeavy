package com.bignerdranch.android.lyftheavy.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bignerdranch.android.lyftheavy.Diary
import com.bignerdranch.android.lyftheavy.Food
import java.util.*

@Dao
interface DiaryDao {
    @Query("SELECT * FROM diary")
    fun getDiaries(): LiveData<List<Diary>>

    @Query("SELECT * FROM diary WHERE id=(:id)")
    fun getDiary(id: UUID): LiveData<Diary?>

    @Update
    fun updateDiary(diary: Diary)

    @Insert
    fun addDiary(diary: Diary)

    @Delete
    fun deleteDiary(diary: Diary)
}