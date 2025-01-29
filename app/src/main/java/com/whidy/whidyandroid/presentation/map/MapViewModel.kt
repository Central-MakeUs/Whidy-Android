package com.whidy.whidyandroid.presentation.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap

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
}
