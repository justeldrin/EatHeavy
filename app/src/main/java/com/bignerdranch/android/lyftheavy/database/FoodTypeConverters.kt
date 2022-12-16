package com.bignerdranch.android.lyftheavy.database

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.*

class FoodTypeConverters {

    @TypeConverter
    fun toUUID(uuid: String?): UUID? {
        return UUID.fromString(uuid)
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }
}