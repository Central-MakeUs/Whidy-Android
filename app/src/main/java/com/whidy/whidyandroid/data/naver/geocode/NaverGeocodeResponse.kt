package com.whidy.whidyandroid.data.naver.geocode

data class NaverGeocodeResponse(
    val status: String,
    val meta: Meta,
    val addresses: List<Address>,
    val errorMessage: String?
)