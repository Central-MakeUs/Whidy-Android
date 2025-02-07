package com.whidy.whidyandroid.presentation.map.info

import com.whidy.whidyandroid.presentation.map.ItemType

data class PlaceReviewComment(
    val userProfileImageUrl: String,
    val userName: String,
    val reviewScore: Int,
    val reviewTime: String,
    val reviewComment: String,
    val tags: List<ItemType>
)
