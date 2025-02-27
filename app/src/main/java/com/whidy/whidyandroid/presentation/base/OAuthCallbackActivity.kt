package com.whidy.whidyandroid.presentation.base

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.whidy.whidyandroid.network.TokenManager
import timber.log.Timber

class OAuthCallbackActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 인텐트로 전달된 URI를 처리
        val uri: Uri? = intent?.data
        if (uri != null && uri.scheme == "whidy" && uri.host == "home") {
            // 토큰 정보 추출
            val accessToken = uri.getQueryParameter("accessToken")
            val refreshToken = uri.getQueryParameter("refreshToken")
            Timber.d("OAuthCallbackActivity accessToken: $accessToken")
            Timber.d("OAuthCallbackActivity refreshToken: $refreshToken")

            // TokenManager를 사용해 토큰 저장 (예시: Application이나 Singleton에서 관리)
            val sharedPrefs = getSharedPreferences("prefs", MODE_PRIVATE)
            val tokenManager = TokenManager(sharedPrefs)
            tokenManager.saveTokens(accessToken ?: "", refreshToken ?: "")

            // 로그인 성공 후 원하는 액티비티로 이동 (예: MainActivity 또는 온보딩 화면)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        // 콜백 처리가 끝난 후 종료
        finish()
    }
}