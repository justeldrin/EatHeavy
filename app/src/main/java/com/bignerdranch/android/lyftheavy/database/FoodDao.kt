package com.bignerdranch.android.lyftheavy.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bignerdranch.android.lyftheavy.Food
import java.util.*

@Dao
interface FoodDao {

    @Query("SELECT * FROM food")
    fun getFoods(): LiveData<List<Food>>

    @Query("SELECT * FROM food WHERE id=(:id)")
    fun getFood(id: UUID): LiveData<Food?>

    @Update
    fun updateFood(ingredient: Food)

    @Insert
    fun addFood(ingredient: Food)

    @Delete
    fun deleteFood(ingredient: Food)
}