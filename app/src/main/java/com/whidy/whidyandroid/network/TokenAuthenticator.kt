package com.whidy.whidyandroid.network

import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(private val tokenManager: TokenManager) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        // 너무 많이 재시도하면 null 반환
        if (responseCount(response) >= 3) return null

        // 리프레시 토큰이 없으면 null 반환
        val refreshToken = tokenManager.getRefreshToken() ?: return null

        // 동기적으로 토큰 재발급 API 호출
        val newTokenResponse = runBlocking {
            try {
                RetrofitClient.authService.refreshToken("true")
            } catch (e: Exception) {
                null
            }
        }

        // 토큰 갱신에 성공했다면 tokenManager 업데이트 및 재요청 빌드
        if (newTokenResponse != null && newTokenResponse.isSuccessful) {
            val newAccessToken = newTokenResponse.body()?.accessToken
            val newRefreshToken = newTokenResponse.body()?.refreshToken
            if (!newAccessToken.isNullOrEmpty()) {
                if (newRefreshToken != null) {
                    tokenManager.saveTokens(newAccessToken, newRefreshToken)
                }
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $newRefreshToken")
                    .build()
            }
        }
        return null
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var priorResponse = response.priorResponse
        while (priorResponse != null) {
            count++
            priorResponse = priorResponse.priorResponse
        }
        return count
    }
}