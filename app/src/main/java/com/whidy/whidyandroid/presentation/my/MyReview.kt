package com.whidy.whidyandroid.presentation.my

import com.whidy.whidyandroid.presentation.map.ItemType

data class MyReview(
    val placeName: String,
    val score: Float,
    val reviewTime: String,
    val reviewComment: String,
    val tags: List<ItemType>
)
