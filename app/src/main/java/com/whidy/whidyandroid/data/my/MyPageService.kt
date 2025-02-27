package com.whidy.whidyandroid.data.my

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Query

interface MyPageService {
    @GET("api/my/profile")
    suspend fun getMyPage(): Response<MyPageResponse>

    @PUT("api/my/profile")
    suspend fun setMyName(
        @Body request: SetMyNameRequest
    ): Unit

    @PATCH("api/my/profile-image")
    suspend fun setMyProfileImage(
        @Body request: SetMyProfileImageRequest
    ): Unit

    @GET("api/my/review")
    suspend fun getMyPlaceRequest(
        @Query("name") name: String = "",
        @Query("address") address: String = "",
        @Query("processed") limit: Boolean = false
    ): Response<List<MyPlaceRequestResponse>>
}