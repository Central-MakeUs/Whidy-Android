package com.whidy.whidyandroid.data.review

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ReviewService {
    @GET("api/review")
    suspend fun getReviews(
        @Query("placeId") placeId: Int,
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 100
    ): Response<List<ReviewResponse>>

    @PUT("api/review")
    suspend fun updateReview(
        @Body reviewRequest: ReviewRequest
    ): Unit

    @POST("api/review")
    suspend fun postReview(
        @Body reviewRequest: ReviewRequest
    ): Unit

    @GET("api/review/{reviewId}")
    suspend fun getReviewDate(
        @Path("reviewId") reviewId: Int
    ): Response<ResponseBody>

    @DELETE("api/review/{reviewId}")
    suspend fun deleteReview(
        @Path("reviewId") reviewId: Int
    ): Unit
}