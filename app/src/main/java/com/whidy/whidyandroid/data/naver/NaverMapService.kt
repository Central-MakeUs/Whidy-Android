package com.whidy.whidyandroid.data.naver

import com.whidy.whidyandroid.data.naver.geocode.NaverGeocodeResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NaverMapService {
    @GET("/map-geocode/v2/geocode")
    suspend fun geocode(
        @Header("x-ncp-apigw-api-key-id") clientId: String,
        @Header("x-ncp-apigw-api-key") clientSecret: String,
        @Query("query") query: String
    ): NaverGeocodeResponse
}