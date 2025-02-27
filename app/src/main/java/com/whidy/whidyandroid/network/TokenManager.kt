package com.whidy.whidyandroid.network

import android.content.SharedPreferences

class TokenManager(private val sharedPreferences: SharedPreferences) {
    companion object {
        private const val ACCESS_TOKEN = "ACCESS_TOKEN"
        private const val REFRESH_TOKEN = "REFRESH_TOKEN"
    }

    fun saveTokens(accessToken: String, refreshToken: String) {
        sharedPreferences.edit().apply {
            putString(ACCESS_TOKEN, accessToken)
            putString(REFRESH_TOKEN, refreshToken)
            apply()
        }
    }

    fun getAccessToken(): String? = sharedPreferences.getString(ACCESS_TOKEN, null)
    fun getRefreshToken(): String? = sharedPreferences.getString(REFRESH_TOKEN, null)
    fun clearTokens() {
        sharedPreferences.edit().clear().apply()
    }
}
