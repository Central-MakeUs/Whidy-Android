package com.whidy.whidyandroid.data.auth

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {
    @POST("auth/sign-up")
    suspend fun signup(@Body request: SignUpRequest): Response<SignUpResponse>

    @POST("auth/refresh-token")
    suspend fun refreshToken(@Header("Use-Refresh") useRefresh: String = "true"): Response<TokenResponse>

    @POST("auth/sign-out")
    suspend fun logout(
        @Header("No-Auth") noAuth: String = "true",
        @Body logoutRequest: TokenResponse
    ): Unit
}