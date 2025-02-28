package com.whidy.whidyandroid.presentation.onboarding

import android.content.Context
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
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.databinding.FragmentLoginWebViewBinding
import com.whidy.whidyandroid.di.AuthViewModelFactory
import com.whidy.whidyandroid.network.RetrofitClient
import com.whidy.whidyandroid.network.TokenManager
import timber.log.Timber

class LoginWebViewFragment : Fragment() {

    private var _binding: FragmentLoginWebViewBinding? = null
    private val binding get() = requireNotNull(_binding) { "FragmentLoginWebViewBinding is null" }

    // 전달받은 인자에 따라 로그인 방식을 결정 ("KAKAO"가 기본)
    private val loginMethod: String by lazy { arguments?.getString("loginMethod") ?: "KAKAO" }

    private val viewModel: SignUpViewModel by activityViewModels {
        AuthViewModelFactory(
            RetrofitClient.authService,
            TokenManager(requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE))
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginWebViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 로그인 URL 결정 (Google과 Kakao 모두 웹뷰를 이용)
        val loginUrl = if (loginMethod.equals("GOOGLE", ignoreCase = true)) {
            "https://api.whidy.site/auth/GOOGLE"
        } else {
            "https://api.whidy.site/auth/KAKAO"
        }

        // 웹뷰 기본 설정
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.domStorageEnabled = true

        // Google 로그인일 경우에만 user agent 우회 적용
        if (loginMethod.equals("GOOGLE", ignoreCase = true)) {
            val userAgentString = binding.webView.settings.userAgentString
            val newUserAgentString = userAgentString
                .replace("; wv", "")
                .replace("Android ${android.os.Build.VERSION.RELEASE};", "")
            binding.webView.settings.userAgentString = newUserAgentString
        }

        // WebViewClient 설정: 리디렉션 URL을 감지하여 토큰 추출 처리
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
                            // 로그인 실패 또는 회원가입 필요: signUpCode 추출 후 처리
                            val signUpCode = uri.getQueryParameter("signUpCode")
                            Timber.d("signUpCode: $signUpCode")
                            if (signUpCode != null) {
                                viewModel.setSignUpCode(signUpCode)
                            }
                            findNavController().navigate(R.id.action_navigation_login_web_view_to_sign_up_email)
                        }
                    }
                    return true // 해당 URL은 웹뷰에서 추가로 로드하지 않음
                }
                return false // 그 외 URL은 웹뷰에서 로드
            }
        }

        // 지정된 로그인 URL 로드
        binding.webView.loadUrl(loginUrl)
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
