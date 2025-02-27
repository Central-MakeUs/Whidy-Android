package com.whidy.whidyandroid.presentation.map.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whidy.whidyandroid.data.naver.geocode.NaverGeocodeResponse
import com.whidy.whidyandroid.data.place.PlaceAddRequest
import com.whidy.whidyandroid.network.RetrofitClient
import kotlinx.coroutines.launch
import timber.log.Timber

class PlaceAddViewModel : ViewModel() {

    private val _address = MutableLiveData<String>()
    val address: LiveData<String> get() = _address

    private val _location = MutableLiveData<Pair<Double, Double>>()
    val location: LiveData<Pair<Double, Double>> get() = _location

    fun setAddress(address: String) {
        _address.value = address
        viewModelScope.launch {
            try {
                val clientId = com.whidy.whidyandroid.BuildConfig.NAVER_MAP_CLIENT_ID
                val clientSecret = com.whidy.whidyandroid.BuildConfig.NAVER_MAP_CLIENT_SECRET

                val response: NaverGeocodeResponse =
                    RetrofitClient.geocodeService.geocode(clientId, clientSecret, address)
                if (response.addresses.isNotEmpty()) {
                    Timber.d("Geocoding response: $response")
                    val result = response.addresses[0]
                    val latitude = result.y?.toDoubleOrNull() ?: 0.0
                    val longitude = result.x?.toDoubleOrNull() ?: 0.0
                    _location.value = Pair(latitude, longitude)
                    Timber.d("Geocoded: $address -> Latitude: $latitude, Longitude: $longitude")
                } else {
                    Timber.e("No geocoding result for address: $address")
                }
            } catch (e: Exception) {
                Timber.e(e, "Geocoding error for address: $address")
            }
        }
    }

    fun postPlaceRequest(
        placeName: String,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        val currentAddress = _address.value
        val currentLocation = _location.value
        if (currentAddress.isNullOrBlank() || currentLocation == null || placeName.isBlank()) {
            return
        }
        viewModelScope.launch {
            try {
                val request = PlaceAddRequest(
                    address = currentAddress,
                    name = placeName,
                    latitude = currentLocation.first,
                    longitude = currentLocation.second
                )
                RetrofitClient.placeService.postPlaceRequest(request)
                onSuccess()
            } catch (e: Exception) {
                Timber.e(e, "Failed to add place")
                onError(e)
            }
        }
    }
}