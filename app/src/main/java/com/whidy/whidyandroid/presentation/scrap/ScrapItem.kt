package com.whidy.whidyandroid.presentation.scrap

import com.whidy.whidyandroid.model.PlaceType

data class ScrapItem(
    val scrapId: Int,
    val name: String,
    val address: String,
    val placeType: PlaceType
)
