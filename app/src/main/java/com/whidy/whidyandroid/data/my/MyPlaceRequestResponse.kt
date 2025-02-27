package com.whidy.whidyandroid.data.my

data class MyPlaceRequestResponse(
    val createdDate: String,
    val lastModifiedDate: String,
    val createUser: Int,
    val lastModifyingUser: Int,
    val id: Int,
    val address: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val processed: Boolean
)
