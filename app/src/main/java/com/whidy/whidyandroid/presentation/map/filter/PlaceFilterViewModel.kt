package com.whidy.whidyandroid.presentation.map.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlaceFilterViewModel : ViewModel() {
    private val _spaceType = MutableLiveData<String>()
    val spaceType: LiveData<String> get() = _spaceType

    private val _priceRange = MutableLiveData<String>()
    val priceRange: LiveData<String> get() = _priceRange

    // 방문 요일 (예: "월", "화", …, "연중 무휴")
    private val _visitWeek = MutableLiveData<String>()
    val visitWeek: LiveData<String> get() = _visitWeek

    private val _visitTime = MutableLiveData<String>()
    val visitTime: LiveData<String> get() = _visitTime

    private val _visitPeople = MutableLiveData<String>()
    val visitPeople: LiveData<String> get() = _visitPeople

    private val _reservation = MutableLiveData<String>()
    val reservation: LiveData<String> get() = _reservation

    private val _reviewScore = MutableLiveData<String>()
    val reviewScore: LiveData<String> get() = _reviewScore

    fun setSpaceType(type: String) {
        _spaceType.value = type
    }
    fun setPriceRange(range: String) {
        _priceRange.value = range
    }
    fun setVisitWeek(week: String) {
        _visitWeek.value = week
    }
    fun setVisitTime(time: String) {
        _visitTime.value = time
    }
    fun setVisitPeople(people: String) {
        _visitPeople.value = people
    }
    fun setReservation(reserv: String) {
        _reservation.value = reserv
    }
    fun setReviewScore(score: String) {
        _reviewScore.value = score
    }
}