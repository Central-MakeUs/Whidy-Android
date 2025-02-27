package com.whidy.whidyandroid.data.place

import com.google.gson.annotations.SerializedName

data class CafeAdditionalInfo(
    @SerializedName("@type")
    val type: String,
    val menu: List<MenuItem>
)
