package com.whidy.whidyandroid.data.place

data class PlaceResponse(
    val id: Int,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val beveragePrice: Int,
    val reviewNum: Int,
    val reviewScore: Double?,
    val placeType: String,
    val additionalInfo: CafeAdditionalInfo,
    val businessHours: List<BusinessHour>,
    val images: List<String>
)
