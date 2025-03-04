package com.whidy.whidyandroid.data.my

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface MyPageService {
    @GET("api/my/profile")
    suspend fun getMyPage(): Response<MyPageResponse>

    @PUT("api/my/profile")
    suspend fun setMyName(
        @Body request: SetMyNameRequest
    ): Unit

    @Multipart
    @PATCH("api/my/profile-image")
    suspend fun setMyProfileImage(
        @Part file: MultipartBody.Part
    ): Unit

    @GET("api/my/review")
    suspend fun getMyPlaceReviews(
        @Query("placeId") placeId: Int? = null,
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 100
    ): Response<List<MyReviewResponse>>

    @GET("api/my/place-request")
    suspend fun getMyPlaceRequest(
        @Query("name") name: String = "",
        @Query("address") address: String = "",
        @Query("processed") limit: Boolean = false
    ): Response<List<MyPlaceRequestResponse>>
}