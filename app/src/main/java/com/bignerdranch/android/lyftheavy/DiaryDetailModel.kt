package com.bignerdranch.android.lyftheavy

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.*

class DiaryDetailModel() : ViewModel() {

    private val diaryRepository = DiaryRepository.get()
    private val diaryIdLiveData = MutableLiveData<UUID>()

    var diaryLiveData: LiveData<Diary?> =
        Transformations.switchMap(diaryIdLiveData) { diaryId ->
            diaryRepository.getDiary(diaryId)
        }

    fun loadDiary(diaryId: UUID) {
        diaryIdLiveData.value = diaryId
    }

    fun saveDiary(diary: Diary) {
        diaryRepository.updateDiary(diary)
    }
}