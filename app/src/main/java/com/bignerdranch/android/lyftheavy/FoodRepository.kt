package com.bignerdranch.android.lyftheavy

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.bignerdranch.android.lyftheavy.database.FoodDatabase
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "food-database"

class FoodRepository private constructor(context: Context) {

    private val database: FoodDatabase = Room.databaseBuilder(
        context.applicationContext,
        FoodDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val foodDao = database.foodDao()
    private val executor = Executors.newSingleThreadExecutor()

    fun getFoods(): LiveData<List<Food>> = foodDao.getFoods()

    fun getFood(id: UUID): LiveData<Food?> = foodDao.getFood(id)

    fun updateFood(food: Food) {
        executor.execute {
            foodDao.updateFood(food)
        }
    }

    fun addFood(food: Food) {
        executor.execute {
            foodDao.addFood(food)
        }
    }

    fun deleteFood(food: Food) {
        executor.execute{
            foodDao.deleteFood(food)
        }
    }

    companion object {
        private var INSTANCE: FoodRepository? = null

        fun initialize(context: Context) {
            if(INSTANCE == null) {
                INSTANCE = FoodRepository(context)
            }
        }

        fun get(): FoodRepository {
            return INSTANCE ?:
            throw IllegalStateException("repo not initialized")
        }
    }

}