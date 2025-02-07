package com.whidy.whidyandroid.presentation.my.review

import com.whidy.whidyandroid.presentation.map.ItemType

data class MyReview(
    val placeName: String,
    val score: Float,
    val reviewTime: String,
    val reviewComment: String,
    val tags: List<ItemType>
)
