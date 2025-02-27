package com.whidy.whidyandroid.data.place

data class GetPlaceResponse(
    val id: Int,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val beveragePrice: Int,
    val reviewScore: Int,
    val placeType: String,
    val thumbnail: String
)