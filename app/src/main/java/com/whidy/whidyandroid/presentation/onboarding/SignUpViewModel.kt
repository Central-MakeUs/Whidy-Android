package com.whidy.whidyandroid.presentation.onboarding

import androidx.lifecycle.*
import com.whidy.whidyandroid.data.auth.AuthService
import com.whidy.whidyandroid.data.auth.SignUpRequest
import com.whidy.whidyandroid.data.auth.SignUpResponse
import com.whidy.whidyandroid.data.auth.TokenResponse
import com.whidy.whidyandroid.network.RetrofitClient
import com.whidy.whidyandroid.network.TokenManager
import kotlinx.coroutines.launch
import timber.log.Timber

class SignUpViewModel(
    private val authService: AuthService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    private val _nickname = MutableLiveData<String>()
    val nickname: LiveData<String> get() = _nickname

    private val _signUpCode = MutableLiveData<String>()
    val signUpCode: LiveData<String> get() = _signUpCode

    private val _signUpResult = MutableLiveData<Result<SignUpResponse>>()
    val signUpResult: LiveData<Result<SignUpResponse>> get() = _signUpResult

    fun setEmail(email: String) {
        _email.value = email
    }

    fun setNickname(nickname: String) {
        _nickname.value = nickname
    }

    fun setSignUpCode(code: String) {
        _signUpCode.value = code
    }

    fun signUp() {
        viewModelScope.launch {
            try {
                val emailValue = _email.value ?: throw Exception("Email is missing")
                val nicknameValue = _nickname.value ?: throw Exception("Nickname is missing")
                val codeValue = _signUpCode.value ?: throw Exception("SignUpCode is missing")

                val request = SignUpRequest(
                    signUpCode = codeValue,
                    email = emailValue,
                    name = nicknameValue
                )

                Timber.d("request: $request")

                val response = authService.signup(request)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        tokenManager.saveTokens(
                            body.authToken.accessToken,
                            body.authToken.refreshToken
                        )
                        _signUpResult.value = Result.success(body)
                    } else {
                        _signUpResult.value = Result.failure(Exception("Empty response"))
                    }
                } else {
                    _signUpResult.value = Result.failure(Exception("Signup API error: ${response.code()}"))
                }
            } catch (e: Exception) {
                _signUpResult.value = Result.failure(e)
            }
        }
    }

    fun logout(onLogoutSuccess: () -> Unit, onLogoutFailure: () -> Unit) {
        viewModelScope.launch {
            try {
                val accessToken = RetrofitClient.tokenManager.getAccessToken() ?: ""
                val refreshToken = RetrofitClient.tokenManager.getRefreshToken() ?: ""

                val logoutRequest = TokenResponse(accessToken, refreshToken)

                RetrofitClient.authService.logout(true.toString(), logoutRequest)

                RetrofitClient.clearTokens()
                onLogoutSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
                onLogoutFailure()
            }
        }
    }
}
