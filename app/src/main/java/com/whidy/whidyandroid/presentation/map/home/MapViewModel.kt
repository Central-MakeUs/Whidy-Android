package com.whidy.whidyandroid.presentation.map.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.whidy.whidyandroid.data.place.PlaceResponse
import com.whidy.whidyandroid.network.RetrofitClient
import kotlinx.coroutines.launch
import timber.log.Timber

class MapViewModel : ViewModel() {
    private val _naverMapLiveData = MutableLiveData<NaverMap>()
    val naverMapLiveData: LiveData<NaverMap> = _naverMapLiveData

    fun setNaverMap(naverMap: NaverMap) {
        _naverMapLiveData.value = naverMap
    }

    private val _selectedLocation = MutableLiveData<LatLng?>()
    val selectedLocation: LiveData<LatLng?> = _selectedLocation

    fun setSelectedLocation(latLng: LatLng) {
        _selectedLocation.value = latLng
    }

    fun clearLocation() {
        _selectedLocation.value = null
    }

    private val _placeDetail = MutableLiveData<PlaceResponse>()
    val placeDetail: LiveData<PlaceResponse> get() = _placeDetail

    fun fetchPlaceGeneralCafe(id: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.placeService.getPlaceGeneralCafe(id)
                if (response.isSuccessful) {
                    response.body()?.let { placeResponse ->
                        Timber.d("placeResponse: $placeResponse")
                        val latitude = placeResponse.longitude
                        val longitude = placeResponse.latitude
                        setSelectedLocation(LatLng(latitude, longitude))
                        _placeDetail.value = placeResponse
                    }
                } else {
                    // 오류 처리 (ex. 로그 출력)
                    Log.e("MapViewModel", "API 호출 실패: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("MapViewModel", "API 호출 중 예외 발생", e)
            }
        }
    }
}
