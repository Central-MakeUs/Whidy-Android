package com.whidy.whidyandroid.network

import com.whidy.whidyandroid.data.scrap.ScrapService
import com.whidy.whidyandroid.data.auth.AuthService
import com.whidy.whidyandroid.data.my.MyPageService
import com.whidy.whidyandroid.data.naver.NaverMapService
import com.whidy.whidyandroid.data.place.PlaceService
import com.whidy.whidyandroid.data.review.ReviewService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    lateinit var tokenManager: TokenManager

    fun init(tokenManager: TokenManager) {
        this.tokenManager = tokenManager
    }

    fun clearTokens() {
        tokenManager.clearTokens()
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val requestBuilder = originalRequest.newBuilder()

                // 요청에 커스텀 헤더가 있으면 해당 플래그에 따라 토큰을 다르게 처리
                when {
                    originalRequest.header("Use-Refresh") != null -> {
                        // "Use-Refresh" 헤더가 있으면 리프레시 토큰 사용
                        requestBuilder.removeHeader("Use-Refresh")
                        tokenManager.getRefreshToken()?.let { token ->
                            requestBuilder.addHeader("Authorization", "Bearer $token")
                        }
                    }
                    originalRequest.header("No-Auth") != null -> {
                        // "No-Auth" 헤더가 있으면 토큰을 아예 추가하지 않음
                        requestBuilder.removeHeader("No-Auth")
                    }
                    else -> {
                        // 기본: 액세스 토큰 추가
                        tokenManager.getAccessToken()?.let { token ->
                            requestBuilder.addHeader("Authorization", "Bearer $token")
                        }
                    }
                }

                chain.proceed(requestBuilder.build())
            }
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.whidy.site/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val naverRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://naveropenapi.apigw.ntruss.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val geocodeService: NaverMapService by lazy {
        naverRetrofit.create(NaverMapService::class.java)
    }

    val authService: AuthService by lazy {
        retrofit.create(AuthService::class.java)
    }

    val myService: MyPageService by lazy {
        retrofit.create(MyPageService::class.java)
    }

    val placeService: PlaceService by lazy {
        retrofit.create(PlaceService::class.java)
    }

    val reviewService: ReviewService by lazy {
        retrofit.create(ReviewService::class.java)
    }

    val scrapService: ScrapService by lazy {
        retrofit.create(ScrapService::class.java)
    }
}