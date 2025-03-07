package com.whidy.whidyandroid.presentation.my

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.databinding.FragmentMyBinding
import com.whidy.whidyandroid.di.AuthViewModelFactory
import com.whidy.whidyandroid.network.RetrofitClient
import com.whidy.whidyandroid.network.TokenManager
import com.whidy.whidyandroid.presentation.base.MainActivity
import com.whidy.whidyandroid.presentation.onboarding.SignUpViewModel
import timber.log.Timber

class MyFragment : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentMyBinding? = null
    private val binding: FragmentMyBinding
        get() = requireNotNull(_binding){"FragmentHomeBinding -> null"}

    private val viewModel: MyViewModel by activityViewModels()
    private val signUpViewModel: SignUpViewModel by activityViewModels {
        AuthViewModelFactory(
            RetrofitClient.authService,
            TokenManager(requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE))
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMyBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        (requireActivity() as MainActivity).hideBottomNavigation(false)

        viewModel.fetchMyPage()

        binding.btnProfileEdit.setOnClickListener {
            navController.navigate(R.id.action_navigation_my_to_profile_edit)
        }

        binding.clMyReview.setOnClickListener {
            navController.navigate(R.id.action_navigation_my_to_my_review)
        }

        binding.clMyPlace.setOnClickListener {
            navController.navigate(R.id.action_navigation_my_to_my_place_request)
        }

        binding.btnLogout.setOnClickListener {
            signUpViewModel.logout(
                onLogoutSuccess = {
                    // 로그아웃 성공 시 로그인 화면으로 이동하고, 뒤로가기 시 앱 종료되도록 백스택 클리어
                    navController.navigate(R.id.navigation_login, null,
                        androidx.navigation.navOptions {
                            popUpTo(R.id.nav_graph) { inclusive = true }
                        }
                    )
                },
                onLogoutFailure = {
                    Toast.makeText(context, "로그아웃 실패", Toast.LENGTH_SHORT).show()
                }
            )
        }

        binding.btnSignOut.setOnClickListener {
            val dialog = SignOutDialog(requireContext()) {
                signUpViewModel.logout(
                    onLogoutSuccess = {
                        // 로그아웃 성공 시 로그인 화면으로 이동하고, 뒤로가기 시 앱 종료되도록 백스택 클리어
                        navController.navigate(
                            R.id.navigation_login,
                            null,
                            androidx.navigation.navOptions {
                                popUpTo(R.id.nav_graph) { inclusive = true }
                            }
                        )
                    },
                    onLogoutFailure = {
                        Toast.makeText(context, "탈퇴 실패", Toast.LENGTH_SHORT).show()
                    }
                )
            }
            dialog.show()
        }

        viewModel.nickname.observe(viewLifecycleOwner) { nickname ->
            binding.tvUserNickname.text = nickname
            Timber.d("nickname: $nickname")
        }

        viewModel.profileImageUrl.observe(viewLifecycleOwner) { imageUrl ->
            Glide.with(requireContext())
                .load(imageUrl)
                .into(binding.ivUserProfile)
            Timber.d("imageUrl: $imageUrl")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}