package com.whidy.whidyandroid

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.naver.maps.map.NaverMapSdk

class WhidyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NaverCloudPlatformClient(BuildConfig.NAVER_MAP_CLIENT_ID)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}