package com.whidy.whidyandroid.data.scrap

import com.whidy.whidyandroid.data.place.PlaceAddRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ScrapService {

    @GET("api/scrap")
    suspend fun getScrapList(
        @Query("keyword") keyword: String = "",
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 10
    ): List<ScrapResponse>

    @POST("api/scrap")
    suspend fun setScrap(
        @Body request: PlaceScrapRequest
    ): Unit

    @DELETE("api/scrap/{scrapId}")
    suspend fun deleteScrap(
        @Path("scrapId") scrapId: Int
    ): Unit
}