package com.whidy.whidyandroid.presentation.my

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whidy.whidyandroid.data.my.SetMyNameRequest
import com.whidy.whidyandroid.data.my.SetMyProfileImageRequest
import com.whidy.whidyandroid.network.RetrofitClient
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.ByteArrayOutputStream

class MyViewModel : ViewModel() {

    private val _nickname = MutableLiveData<String>()
    val nickname: LiveData<String> get() = _nickname

    private val _profileImage = MutableLiveData<Int>()
    val profileImage: LiveData<Int> get() = _profileImage

    fun setNickname(newNickname: String) {
        _nickname.value = newNickname
    }

    fun setProfileImage(imageRes: Int) {
        _profileImage.value = imageRes
    }

    fun fetchMyPage() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.myService.getMyPage()
                if (response.isSuccessful) {
                    response.body()?.let { myPage ->
                        _nickname.postValue(myPage.name)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
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

    fun setMyProfileImage(imageRes: Int, context: Context) {
        viewModelScope.launch {
            try {
                val encodedImage = encodeImageResource(context, imageRes)
                Timber.d("encodedImage: $encodedImage")
                RetrofitClient.myService.setMyProfileImage(SetMyProfileImageRequest(file = encodedImage))
                // API 호출 성공 시 LiveData 업데이트
                _profileImage.postValue(imageRes)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun encodeImageResource(context: Context, imageRes: Int): String {
        val bitmap: Bitmap = BitmapFactory.decodeResource(context.resources, imageRes)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }
}
