package com.whidy.whidyandroid.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.whidy.whidyandroid.data.auth.AuthService
import com.whidy.whidyandroid.network.TokenManager
import com.whidy.whidyandroid.presentation.onboarding.SignUpViewModel

class AuthViewModelFactory(
    private val authService: AuthService,
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SignUpViewModel(authService, tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}