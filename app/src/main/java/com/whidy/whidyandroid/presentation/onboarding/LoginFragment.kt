package com.whidy.whidyandroid.presentation.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.trace
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.databinding.FragmentLoginBinding
import com.whidy.whidyandroid.presentation.base.MainActivity

class LoginFragment : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding
        get() = requireNotNull(_binding){"FragmentLoginBinding -> null"}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).hideBottomNavigation(true)

        navController = Navigation.findNavController(view)

        binding.btnKakaoLogin.setOnClickListener {
            val bundle = Bundle().apply { putString("loginMethod", "KAKAO") }
            navController.navigate(R.id.action_navigation_login_sign_up_email_to_login_web_view, bundle)
        }

        binding.btnGoogleLogin.setOnClickListener {
            val bundle = Bundle().apply { putString("loginMethod", "GOOGLE") }
            navController.navigate(R.id.action_navigation_login_sign_up_email_to_login_web_view, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}