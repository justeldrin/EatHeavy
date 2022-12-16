package com.bignerdranch.android.lyftheavy

import androidx.lifecycle.ViewModel

class DiaryListViewModel : ViewModel() {

    private val diaryRepository = DiaryRepository.get()

    val diaryListLiveData = diaryRepository.getDiaries()

    fun addDiary(diary: Diary) {
        diaryRepository.addDiary(diary)
    }

    fun deleteDiary(diary: Diary) {
        diaryRepository.deleteDiary(diary)
    }
}