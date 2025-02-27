package com.whidy.whidyandroid.data.auth

data class SignUpRequest(
    val signUpCode: String,
    val email: String,
    val name: String
)
