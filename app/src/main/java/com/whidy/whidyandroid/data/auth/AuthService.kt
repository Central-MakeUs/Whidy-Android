package com.whidy.whidyandroid.data.auth

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/sign-up")
    suspend fun signup(
        @Body request: SignUpRequest
    ): Response<SignUpResponse>
}