package com.whidy.whidyandroid.presentation.my

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whidy.whidyandroid.data.my.SetMyNameRequest
import com.whidy.whidyandroid.network.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class MyViewModel : ViewModel() {

    private val _nickname = MutableLiveData<String>()
    val nickname: LiveData<String> get() = _nickname

    private val _profileImage = MutableLiveData<Int>()
    val profileImage: LiveData<Int> get() = _profileImage

    private val _profileImageUrl = MutableLiveData<String>()
    val profileImageUrl: LiveData<String> get() = _profileImageUrl

    fun setNickname(newNickname: String) {
        _nickname.value = newNickname
    }

    fun fetchMyPage() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.myService.getMyPage()
                if (response.isSuccessful) {
                    response.body()?.let { myPage ->
                        _nickname.postValue(myPage.name)
                        _profileImageUrl.postValue(myPage.profileImageUrl)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun checkTokenExpired(autoLogin: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.myService.getMyPage()
                if (response.isSuccessful) {
                    response.body()?.let { myPage ->
                        _nickname.postValue(myPage.name)
                        _profileImageUrl.postValue(myPage.profileImageUrl)
                    }
                    autoLogin()
                } else {
                    onFailure()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onFailure()
            }
        }
    }

    fun setMyName(name: String) {
        viewModelScope.launch {
            try {
                // API 호출: SetMyNameRequest를 생성하여 전달
                RetrofitClient.myService.setMyName(SetMyNameRequest(name))
                // API 호출이 성공하면 LiveData 업데이트
                _nickname.postValue(name)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setMyProfileImage(imageFile: File) {
        viewModelScope.launch {
            try {
                val requestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                val multipartBody = MultipartBody.Part.createFormData("file", imageFile.name, requestBody)

                // API 호출
                RetrofitClient.myService.setMyProfileImage(multipartBody)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
