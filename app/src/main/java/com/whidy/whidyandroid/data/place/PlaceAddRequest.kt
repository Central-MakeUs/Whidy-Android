package com.whidy.whidyandroid.data.place

data class PlaceAddRequest (
    val address: String,
    val name: String,
    val latitude: Double,
    val longitude: Double
)