package com.bignerdranch.android.lyftheavy

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.*

class FoodDetailViewModel() : ViewModel() {

    private val foodRepository = FoodRepository.get()
    private val foodIdLiveData = MutableLiveData<UUID>()

    var foodLivedata: LiveData<Food?> =
        Transformations.switchMap(foodIdLiveData) { foodId ->
            foodRepository.getFood(foodId)
        }

    fun loadFood(foodId: UUID) {
        foodIdLiveData.value = foodId
    }

    fun saveFood(food: Food) {
        foodRepository.updateFood(food)
    }
}