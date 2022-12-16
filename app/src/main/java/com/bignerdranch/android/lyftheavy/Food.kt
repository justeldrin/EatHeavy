package com.bignerdranch.android.lyftheavy

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity
data class Food(@PrimaryKey val id: UUID = UUID.randomUUID(),
                var name: String = "",
                var calories: Int=0,
                var protein: Int=0,
                var carbs: Int=0,
                var fats: Int=0,
                var quantity: Int = 0) : Serializable
