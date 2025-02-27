package com.whidy.whidyandroid.data.auth

data class SignUpResponse(
    val authToken: AuthToken,
    val userId: Int
)
