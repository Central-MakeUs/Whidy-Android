package com.whidy.whidyandroid.presentation.map.info

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlaceReviewWriteViewModel : ViewModel() {
    private val _keyword = MutableLiveData<String>()
    val keyword: LiveData<String> get() = _keyword

    private val _howToUse = MutableLiveData<String>()
    val howToUse: LiveData<String> get() = _howToUse

    // 방문 대기 시간 (예: "1분", "10분" 등)
    private val _howLongWait = MutableLiveData<String>()
    val howLongWait: LiveData<String> get() = _howLongWait

    // 방문 목적: 복수 선택을 위해 List<String> 사용
    private val _visitPurpose = MutableLiveData<List<String>>()
    val visitPurpose: LiveData<List<String>> get() = _visitPurpose

    private val _withWho = MutableLiveData<String>()
    val withWho: LiveData<String> get() = _withWho

    fun setKeyword(type: String) {
        _keyword.value = type
    }
    fun setHowToUse(range: String) {
        _howToUse.value = range
    }
    fun setHowLongWait(time: String) {
        _howLongWait.value = time
    }
    // 복수 선택된 방문 목적의 목록을 업데이트
    fun setVisitPurpose(purposes: List<String>) {
        _visitPurpose.value = purposes
    }
    fun setWithWho(people: String) {
        _withWho.value = people
    }
}