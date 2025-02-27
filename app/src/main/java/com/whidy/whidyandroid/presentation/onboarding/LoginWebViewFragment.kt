package com.whidy.whidyandroid.presentation.onboarding

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.databinding.FragmentLoginWebViewBinding
import com.whidy.whidyandroid.network.TokenManager
import timber.log.Timber

class LoginWebViewFragment : Fragment() {

    private var _binding: FragmentLoginWebViewBinding? = null
    private val binding get() = requireNotNull(_binding) { "FragmentLoginWebViewBinding is null" }

    // 전달받은 인자에 따라 로그인 방식을 결정 ("KAKAO"가 기본)
    private val loginMethod: String by lazy { arguments?.getString("loginMethod") ?: "KAKAO" }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginWebViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (loginMethod.equals("GOOGLE", ignoreCase = true)) {
            // Google 로그인: 내장 WebView 대신 Chrome Custom Tabs를 사용하여 외부 브라우저에서 로그인 처리
            openCustomTab("https://api.whidy.site/auth/GOOGLE")
        } else {
            // Kakao 로그인: WebView를 사용하여 로그인 페이지 로드
            binding.webView.settings.javaScriptEnabled = true
            binding.webView.settings.domStorageEnabled = true
            binding.webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?, request: WebResourceRequest?
                ): Boolean {
                    val urlLoaded = request?.url.toString()
                    if (urlLoaded.startsWith("whidy://")) {
                        val uri = Uri.parse(urlLoaded)
                        when {
                            urlLoaded.startsWith("whidy://home") -> {
                                // 로그인 성공: accessToken, refreshToken 추출 후 TokenManager에 저장
                                val accessToken = uri.getQueryParameter("accessToken")
                                val refreshToken = uri.getQueryParameter("refreshToken")
                                Timber.d("accessToken: $accessToken")
                                Timber.d("refreshToken: $refreshToken")
                                val tokenManager = TokenManager(requireContext().getSharedPreferences("prefs", 0))
                                tokenManager.saveTokens(accessToken ?: "", refreshToken ?: "")
                                // 로그인 성공 후 다음 화면(예: Map 화면)으로 이동
                                findNavController().navigate(R.id.action_navigation_login_web_view_to_map)
                            }
                            urlLoaded.startsWith("whidy://sign-up") -> {
                                // 로그인 실패 또는 회원가입 필요: signUpCode 추출 후 shared ViewModel에 저장
                                val signUpCode = uri.getQueryParameter("signUpCode")
                                Timber.d("signUpCode: $signUpCode")
                                // 예를 들어, shared ViewModel에 저장 후 회원가입 플로우 시작
                                // viewModel.setSignUpCode(signUpCode ?: "")
                                findNavController().navigate(R.id.action_navigation_login_web_view_to_sign_up_email)
                            }
                        }
                        return true // WebView가 해당 URL을 추가로 로드하지 않음
                    }
                    return false // 그 외 URL은 WebView에서 로드
                }
            }
            // Kakao 로그인 엔드포인트 로드
            binding.webView.loadUrl("https://api.whidy.site/auth/KAKAO")
        }
    }

    private fun openCustomTab(url: String) {
        val customTabsIntent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .build()
        customTabsIntent.launchUrl(requireContext(), Uri.parse(url))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
