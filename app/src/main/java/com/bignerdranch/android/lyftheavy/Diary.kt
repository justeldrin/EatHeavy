package com.bignerdranch.android.lyftheavy

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*
import kotlin.collections.ArrayList

@Entity
data class Diary(@PrimaryKey val id: UUID = UUID.randomUUID(),
                 var date: Date = Date(),
                 var totalCalories: Int = 0,
                 var foodList: ArrayList<Food> = ArrayList())
