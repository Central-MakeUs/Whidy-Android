package com.whidy.whidyandroid.presentation.map.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whidy.whidyandroid.data.place.GetPlaceResponse
import com.whidy.whidyandroid.network.RetrofitClient
import kotlinx.coroutines.launch
import timber.log.Timber

class PlaceFilterViewModel : ViewModel() {
    private val _priceRange = MutableLiveData<String>()
    val priceRange: LiveData<String> get() = _priceRange

    private val _visitWeek = MutableLiveData<String>()
    val visitWeek: LiveData<String> get() = _visitWeek

    private val _visitPeople = MutableLiveData<String>()
    val visitPeople: LiveData<String> get() = _visitPeople

    private val _reservation = MutableLiveData<String>()
    val reservation: LiveData<String> get() = _reservation

    private val _reviewScore = MutableLiveData<String>()
    val reviewScore: LiveData<String> get() = _reviewScore

    fun setPriceRange(range: String) {
        _priceRange.value = range
    }
    fun setVisitWeek(week: String) {
        _visitWeek.value = week
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

    private val _searchResults = MutableLiveData<List<GetPlaceResponse>?>()
    val searchResults: LiveData<List<GetPlaceResponse>?> get() = _searchResults

    fun clearSearchResults() {
        _searchResults.value = null
    }

    fun fetchPlaceList(
        keyword: String?,
        reviewScoreFrom: Int?,
        reviewScoreTo: Int?,
        beverageFrom: Int?,
        beverageTo: Int?,
        businessDayOfWeek: String?
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.placeService.getPlace(
                    reviewScoreFrom = reviewScoreFrom,
                    reviewScoreTo = reviewScoreTo,
                    beverageFrom = beverageFrom,
                    beverageTo = beverageTo,
                    placeType = null,
                    businessDayOfWeek = businessDayOfWeek,
                    visitTimeFromHour = null,
                    visitTimeToHour = null,
                    centerLatitude = 37.545532,
                    centerLongitude = 126.952514,
                    radius = 100000000,
                    keyword = keyword
                )
                if (response.isSuccessful) {
                    response.body()?.let { placeList ->
                        _searchResults.postValue(placeList)
                        Timber.d("fetchPlaceList 성공")
                    }
                } else {
                    Timber.e("fetchPlaceList API 호출 실패: ${response.code()}")
                }
            } catch (e: Exception) {
                Timber.e(e, "fetchPlaceList API 호출 중 예외 발생")
            }
        }
    }
}