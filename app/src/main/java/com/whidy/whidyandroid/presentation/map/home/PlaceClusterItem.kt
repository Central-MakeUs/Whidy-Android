package com.whidy.whidyandroid.presentation.map.home

import com.naver.maps.geometry.LatLng
import com.naver.maps.map.clustering.ClusteringKey
import com.whidy.whidyandroid.data.place.GetPlaceResponse

data class PlaceClusterItem(
    val place: GetPlaceResponse
) : ClusteringKey {
    override fun getPosition(): LatLng = LatLng(place.latitude, place.longitude)

    override fun hashCode(): Int {
        return place.id.hashCode()  // id는 Int 타입이므로 안전하게 사용 가능
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PlaceClusterItem) return false
        return place.id == other.place.id
    }
}
