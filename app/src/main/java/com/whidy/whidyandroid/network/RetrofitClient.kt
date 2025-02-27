package com.whidy.whidyandroid.network

import com.whidy.whidyandroid.data.scrap.ScrapService
import com.whidy.whidyandroid.data.auth.AuthService
import com.whidy.whidyandroid.data.my.MyPageService
import com.whidy.whidyandroid.data.naver.NaverMapService
import com.whidy.whidyandroid.data.place.PlaceService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private lateinit var tokenManager: TokenManager

    fun init(tokenManager: TokenManager) {
        this.tokenManager = tokenManager
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val builder = originalRequest.newBuilder()
                tokenManager.getAccessToken()?.let { token ->
                    builder.addHeader("Authorization", "Bearer $token")
                }
                chain.proceed(builder.build())
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

    val scrapService: ScrapService by lazy {
        retrofit.create(ScrapService::class.java)
    }
}