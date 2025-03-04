package com.whidy.whidyandroid.data.review

data class ReviewRequest(
    val reviewId: Int = 0,
    val placeId: Int,
    val score: Double,
    val keywords: List<String>,
    val isReserved: Boolean,
    val waitTime: String,
    val visitPurposes: List<String>,
    val companionType: String,
    val comment: String
)
