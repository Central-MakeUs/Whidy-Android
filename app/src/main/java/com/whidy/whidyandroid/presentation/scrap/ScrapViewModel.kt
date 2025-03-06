package com.whidy.whidyandroid.presentation.scrap

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whidy.whidyandroid.data.scrap.PlaceScrapRequest
import com.whidy.whidyandroid.network.RetrofitClient
import kotlinx.coroutines.launch

class ScrapViewModel : ViewModel() {

    private val _scrapItems = MutableLiveData<List<ScrapItem>>()
    val scrapItems: LiveData<List<ScrapItem>> get() = _scrapItems

    init {
        fetchScrapItems()
    }

    fun fetchScrapItems() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.scrapService.getScrapList()
                val items = response.map { scrapResponse ->
                    ScrapItem(
                        scrapId = scrapResponse.scrapId,
                        placeId = scrapResponse.place.id,
                        name = scrapResponse.place.name,
                        address = scrapResponse.place.address,
                        placeType = scrapResponse.place.placeType
                    )
                }
                _scrapItems.postValue(items)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun isScrapped(placeId: Int): Boolean {
        return _scrapItems.value?.any { it.placeId == placeId } ?: false
    }

    fun setScrap(placeId: Int) {
        viewModelScope.launch {
            try {
                RetrofitClient.scrapService.setScrap(PlaceScrapRequest(placeId))
                fetchScrapItems()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteScrap(scrapId: Int) {
        viewModelScope.launch {
            try {
                RetrofitClient.scrapService.deleteScrap(scrapId)
                val currentItems = _scrapItems.value?.toMutableList() ?: mutableListOf()
                val newItems = currentItems.filter { it.scrapId != scrapId }
                _scrapItems.postValue(newItems)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}