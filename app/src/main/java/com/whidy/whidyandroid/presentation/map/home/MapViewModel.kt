package com.whidy.whidyandroid.presentation.map.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.data.place.GetPlaceResponse
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

    private val _searchResults = MutableLiveData<List<GetPlaceResponse>>()
    val searchResults: LiveData<List<GetPlaceResponse>> get() = _searchResults

    fun fetchPlaceList(keyword: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.placeService.getPlace(
                    reviewScoreFrom = null,
                    reviewScoreTo = null,
                    beverageFrom = null,
                    beverageTo = null,
                    placeType = null,
                    businessDayOfWeek = null,
                    visitTimeFromHour = null,
                    visitTimeToHour = null,
                    centerLatitude = 37.541113416270406,
                    centerLongitude = 127.05062406417724,
                    radius = 10000,
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

    fun fetchPlaceGeneralCafe(id: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.placeService.getPlaceGeneralCafe(id)
                if (response.isSuccessful) {
                    response.body()?.let { placeResponse ->
                        Timber.d("placeResponse: $placeResponse")
                        val latitude = placeResponse.latitude
                        val longitude = placeResponse.longitude
                        setSelectedLocation(LatLng(latitude, longitude))
                        _placeDetail.value = placeResponse
                    }
                } else {
                    Timber.tag("MapViewModel").e("API 호출 실패: %s", response.code())
                }
            } catch (e: Exception) {
                Timber.tag("MapViewModel").e(e, "API 호출 중 예외 발생")
            }
        }
    }

    fun fetchPlaceStudyCafe(id: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.placeService.getPlaceStudyCafe(id)
                if (response.isSuccessful) {
                    response.body()?.let { placeResponse ->
                        Timber.d("placeResponse: $placeResponse")
                        val latitude = placeResponse.latitude
                        val longitude = placeResponse.longitude
                        setSelectedLocation(LatLng(latitude, longitude))
                        _placeDetail.value = placeResponse
                    }
                } else {
                    Timber.tag("MapViewModel").e("API 호출 실패: %s", response.code())
                }
            } catch (e: Exception) {
                Timber.tag("MapViewModel").e(e, "API 호출 중 예외 발생")
            }
        }
    }

    fun fetchPlaceFreeStudy(id: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.placeService.getPlaceFreeStudy(id)
                if (response.isSuccessful) {
                    response.body()?.let { placeResponse ->
                        Timber.d("placeResponse: $placeResponse")
                        val latitude = placeResponse.latitude
                        val longitude = placeResponse.longitude
                        setSelectedLocation(LatLng(latitude, longitude))
                        _placeDetail.value = placeResponse
                    }
                } else {
                    Timber.tag("MapViewModel").e("API 호출 실패: %s", response.code())
                }
            } catch (e: Exception) {
                Timber.tag("MapViewModel").e(e, "API 호출 중 예외 발생")
            }
        }
    }

    fun fetchPlaceFreeClothes(id: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.placeService.getPlaceFreeClothes(id)
                if (response.isSuccessful) {
                    response.body()?.let { placeResponse ->
                        Timber.d("placeResponse: $placeResponse")
                        val latitude = placeResponse.latitude
                        val longitude = placeResponse.longitude
                        setSelectedLocation(LatLng(latitude, longitude))
                        _placeDetail.value = placeResponse
                    }
                } else {
                    Timber.tag("MapViewModel").e("API 호출 실패: %s", response.code())
                }
            } catch (e: Exception) {
                Timber.tag("MapViewModel").e(e, "API 호출 중 예외 발생")
            }
        }
    }

    fun fetchPlaceFreePicture(id: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.placeService.getPlaceFreePicture(id)
                if (response.isSuccessful) {
                    response.body()?.let { placeResponse ->
                        Timber.d("placeResponse: $placeResponse")
                        val latitude = placeResponse.latitude
                        val longitude = placeResponse.longitude
                        setSelectedLocation(LatLng(latitude, longitude))
                        _placeDetail.value = placeResponse
                    }
                } else {
                    Timber.tag("MapViewModel").e("API 호출 실패: %s", response.code())
                }
            } catch (e: Exception) {
                Timber.tag("MapViewModel").e(e, "API 호출 중 예외 발생")
            }
        }
    }

    fun fetchPlaceFranchiseCafe(id: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.placeService.getPlaceFranchiseCafe(id)
                if (response.isSuccessful) {
                    response.body()?.let { placeResponse ->
                        Timber.d("placeResponse: $placeResponse")
                        val latitude = placeResponse.latitude
                        val longitude = placeResponse.longitude
                        setSelectedLocation(LatLng(latitude, longitude))
                        _placeDetail.value = placeResponse
                    }
                } else {
                    Timber.tag("MapViewModel").e("API 호출 실패: %s", response.code())
                }
            } catch (e: Exception) {
                Timber.tag("MapViewModel").e(e, "API 호출 중 예외 발생")
            }
        }
    }

    fun getMarkerIcon(placeType: String): Int {
        return when (placeType) {
            "GENERAL_CAFE" -> R.drawable.ic_marker_general_cafe
            "STUDY_CAFE" -> R.drawable.ic_marker_study_cafe
            "FREE_STUDY_SPACE" -> R.drawable.ic_marker_free_study_space
            "FREE_PICTURE" -> R.drawable.ic_marker_free_picture
            "FREE_CLOTHES_RENTAL" -> R.drawable.ic_marker_free_clothes_rental
            "FRANCHISE_CAFE" -> R.drawable.ic_marker_franchise_cafe
            else -> R.drawable.ic_marker_general_cafe
        }
    }
}
