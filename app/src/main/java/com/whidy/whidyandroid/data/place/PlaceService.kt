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
        @Query("reviewScoreFrom") reviewScoreFrom: Int = 0,
        @Query("reviewScoreTo") reviewScoreTo: Int = 0,
        @Query("beverageFrom") beverageFrom: Int = 0,
        @Query("beverageTo") beverageTo: Int = 0,
        @Query("placeType") placeType: List<String> = listOf("STUDY_CAFE"),
        @Query("businessDayOfWeek") businessDayOfWeek: List<String> = listOf("MONDAY"),
        @Query("visitTimeFrom.hour") visitTimeFromHour: Int = 0,
        @Query("visitTimeFrom.minute") visitTimeFromMinute: Int = 0,
        @Query("visitTimeFrom.second") visitTimeFromSecond: Int = 0,
        @Query("visitTimeFrom.nano") visitTimeFromNano: Int = 0,
        @Query("visitTimeTo.hour") visitTimeToHour: Int = 0,
        @Query("visitTimeTo.minute") visitTimeToMinute: Int = 0,
        @Query("visitTimeTo.second") visitTimeToSecond: Int = 0,
        @Query("visitTimeTo.nano") visitTimeToNano: Int = 0,
        @Query("centerLatitude") centerLatitude: Double = 0.0,
        @Query("centerLongitude") centerLongitude: Double = 0.0,
        @Query("radius") radius: Int = 0,
        @Query("keyword") keyword: String = "string"
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