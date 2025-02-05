package com.whidy.whidyandroid.presentation.my

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

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
}
