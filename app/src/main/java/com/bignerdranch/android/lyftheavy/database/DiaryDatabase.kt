package com.bignerdranch.android.lyftheavy.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bignerdranch.android.lyftheavy.Diary
import com.bignerdranch.android.lyftheavy.Food


@Database(entities = [Diary::class], version=1)
@TypeConverters(DiaryTypeConverters::class)
abstract class DiaryDatabase : RoomDatabase() {

    abstract fun diaryDao(): DiaryDao

}