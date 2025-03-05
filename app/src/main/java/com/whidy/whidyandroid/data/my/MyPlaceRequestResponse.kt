package com.whidy.whidyandroid.data.my

import com.google.gson.annotations.SerializedName

data class MyPlaceRequestResponse(
    @SerializedName("createdDateTime")
    val createdDate: String?,
    @SerializedName("lastModifiedDateTime")
    val lastModifiedDate: String?,
    val createUser: Int,
    val lastModifyingUser: Int,
    val id: Int,
    val address: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val processed: Boolean
)
