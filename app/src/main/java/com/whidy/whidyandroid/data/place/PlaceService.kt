package com.whidy.whidyandroid.data.place

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PlaceService {

    @POST("/api/place-request")
    suspend fun postPlaceRequest(
        @Body request: PlaceAddRequest
    ): Unit

    @GET("/api/place")
    suspend fun getPlace(
        @Query("reviewScoreFrom") reviewScoreFrom: Int? = null,
        @Query("reviewScoreTo") reviewScoreTo: Int? = null,
        @Query("beverageFrom") beverageFrom: Int? = null,
        @Query("beverageTo") beverageTo: Int? = null,
        @Query("placeType") placeType: List<String>? = null,
        @Query("businessDayOfWeek") businessDayOfWeek: List<String>? = null,
        @Query("visitTimeFrom") visitTimeFromHour: Int? = null,
        @Query("visitTimeTo") visitTimeToHour: Int? = null,
        @Query("centerLatitude") centerLatitude: Double? = null,
        @Query("centerLongitude") centerLongitude: Double? = null,
        @Query("radius") radius: Int? = null,
        @Query("keyword") keyword: String? = null
    ): Response<List<GetPlaceResponse>>

    @GET("/api/place/study-cafe/{id}")
    suspend fun getPlaceStudyCafe(
        @Path("id") id: Int
    ): Response<PlaceResponse>

    @GET("/api/place/general-cafe/{id}")
    suspend fun getPlaceGeneralCafe(
        @Path("id") id: Int
    ): Response<PlaceResponse>

    @GET("/api/place/free-study/{id}")
    suspend fun getPlaceFreeStudy(
        @Path("id") id: Int
    ): Response<PlaceResponse>

    @GET("/api/place/free-clothes/{id}")
    suspend fun getPlaceFreeClothes(
        @Path("id") id: Int
    ): Response<PlaceResponse>

    @GET("/api/place/free-picture/{id}")
    suspend fun getPlaceFreePicture(
        @Path("id") id: Int
    ): Response<PlaceResponse>

    @GET("/api/place/franchise-cafe/{id}")
    suspend fun getPlaceFranchiseCafe(
        @Path("id") id: Int
    ): Response<PlaceResponse>
}