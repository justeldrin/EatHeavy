package com.bignerdranch.android.lyftheavy

import android.app.Application

class CalorieCounterApp : Application() {

    override fun onCreate() {
        super.onCreate()
        FoodRepository.initialize(this)
        DiaryRepository.initialize(this)
    }
}