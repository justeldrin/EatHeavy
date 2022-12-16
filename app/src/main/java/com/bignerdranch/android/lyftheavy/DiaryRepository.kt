package com.bignerdranch.android.lyftheavy

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.bignerdranch.android.lyftheavy.database.DiaryDatabase
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "diary-database"
class DiaryRepository private constructor(context: Context){

    private val database: DiaryDatabase = Room.databaseBuilder(
        context.applicationContext,
        DiaryDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val diaryDao = database.diaryDao()
    private val executor = Executors.newSingleThreadExecutor()

    fun getDiaries(): LiveData<List<Diary>> = diaryDao.getDiaries()

    fun getDiary(id: UUID): LiveData<Diary?> = diaryDao.getDiary(id)

    fun updateDiary(diary: Diary) {
        executor.execute{
            diaryDao.updateDiary(diary)
        }
    }

    fun addDiary(diary: Diary) {
        executor.execute{
            diaryDao.addDiary(diary)
        }
    }

    fun deleteDiary(diary: Diary) {
        executor.execute {
            diaryDao.deleteDiary(diary)
        }
    }

    companion object {
        private var INSTANCE: DiaryRepository? = null

        fun initialize(context: Context) {
            if(INSTANCE == null) {
                INSTANCE = DiaryRepository(context)
            }
        }

        fun get(): DiaryRepository {
            return INSTANCE ?:
            throw IllegalStateException("diary not initialized")
        }
    }
}