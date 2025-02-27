package com.whidy.whidyandroid.data.scrap

data class ScrapPlaceResponse(
    val id: Int,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val beveragePrice: Double?,
    val reviewScore: Double?,
    val placeType: String,
    val thumbnail: String?
)
