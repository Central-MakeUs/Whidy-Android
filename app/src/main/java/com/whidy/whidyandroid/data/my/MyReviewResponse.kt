package com.whidy.whidyandroid.data.my

data class MyReviewResponse(
    val id: Int,
    val placeId: Int,
    val placeName: String,
    val placeThumbnail: String,
    val score: Int,
    val keywords: List<String>,
    val isReserved: Boolean,
    val waitTime: String,
    val visitPurposes: List<String>,
    val companionType: String,
    val userId: Int,
    val userName: String,
    val userProfileImage: String,
    val createdDateTime: String,
    val lastModifiedDateTime: String
)
