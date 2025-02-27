package com.whidy.whidyandroid.data.naver.geocode

data class Address(
    val roadAddress: String?,
    val jibunAddress: String?,
    val englishAddress: String?,
    val addressElements: List<AddressElement>?,
    val x: String?, // 경도 (longitude)
    val y: String?, // 위도 (latitude)
    val distance: Double?
)
