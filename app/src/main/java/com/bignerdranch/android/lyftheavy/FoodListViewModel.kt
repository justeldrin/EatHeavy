package com.bignerdranch.android.lyftheavy

import androidx.lifecycle.ViewModel

class FoodListViewModel : ViewModel() {

    private val foodRepository = FoodRepository.get()

    val foodListLiveData = foodRepository.getFoods()

    fun addFood(food: Food) {
        foodRepository.addFood(food)
    }

    fun deleteFood(food: Food) {
        foodRepository.deleteFood(food)
    }


}