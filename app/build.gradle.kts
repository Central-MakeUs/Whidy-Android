import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
}

val localProperties = Properties()
localProperties.load(project.rootProject.file("local.properties").inputStream())
val naverMapClientId = localProperties.getProperty("NAVER_MAP_CLIENT_ID")?:""
val naverMapClientSECRET = localProperties.getProperty("NAVER_MAP_CLIENT_SECRET")?:""

android {
    namespace = "com.whidy.whidyandroid"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.whidy.whidyandroid"
        minSdk = 30
        targetSdk = 34
        versionCode = 2
        versionName = "1.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "NAVER_MAP_CLIENT_ID", "\"$naverMapClientId\"")
        buildConfigField("String", "NAVER_MAP_CLIENT_SECRET", "\"$naverMapClientSECRET\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.navermap)
    implementation(libs.androidx.browser)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.timber)
    implementation(libs.play.services.location)
    implementation(libs.glide)
    implementation(libs.splashscreen)

    implementation(libs.dotsindicator)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
}