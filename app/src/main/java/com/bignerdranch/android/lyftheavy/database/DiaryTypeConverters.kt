package com.bignerdranch.android.lyftheavy.database

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.bignerdranch.android.lyftheavy.Food
import java.util.*
import kotlin.collections.ArrayList

import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import com.google.gson.Gson


class DiaryTypeConverters {

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(millisSinceEpoch: Long?): Date? {
        return millisSinceEpoch?.let{
            Date(it)
        }
    }

    @TypeConverter
    fun stringToFoodList(data: String?): ArrayList<Food?>? {
        val listType: Type = object :
            TypeToken<ArrayList<Food?>?>() {}.type
        return Gson().fromJson<ArrayList<Food?>>(data, listType)
    }

    @TypeConverter
    fun FoodListtoString(someObjects: ArrayList<Food?>?): String? {
        return Gson().toJson(someObjects)
    }




    @TypeConverter
    fun toUUID(uuid: String?): UUID? {
        return UUID.fromString(uuid)
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }

}

