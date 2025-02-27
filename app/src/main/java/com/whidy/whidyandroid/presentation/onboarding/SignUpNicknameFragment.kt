package com.whidy.whidyandroid.presentation.onboarding

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.databinding.FragmentSignUpNicknameBinding
import com.whidy.whidyandroid.di.AuthViewModelFactory
import com.whidy.whidyandroid.network.RetrofitClient
import com.whidy.whidyandroid.network.TokenManager
import com.whidy.whidyandroid.presentation.base.MainActivity

class SignUpNicknameFragment : Fragment() {

    private lateinit var navController: NavController
    private var _binding: FragmentSignUpNicknameBinding? = null
    private val binding: FragmentSignUpNicknameBinding
        get() = requireNotNull(_binding){"FragmentSignUpNicknameBinding -> null"}

    private val viewModel: SignUpViewModel by activityViewModels {
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

        _binding = FragmentSignUpNicknameBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        (requireActivity() as MainActivity).hideBottomNavigation(true)

        binding.btnBack.setOnClickListener {
            navController.popBackStack()
        }

        binding.etSignUpNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 별도 처리 없음
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 사용자가 입력 중일 때 배경을 입력 중 상태 drawable로 변경
                binding.etSignUpNickname.setBackgroundResource(R.drawable.bg_input_place_add_active)
            }

            override fun afterTextChanged(s: Editable?) {
                val email = s.toString().trim()
                if (email.isEmpty()) {
                    // 입력이 없으면 기본 배경, 오류 메시지 감추기, 버튼 비활성화
                    binding.etSignUpNickname.setBackgroundResource(R.drawable.bg_input_place_add_default)
                    binding.tvError.visibility = View.GONE
                    binding.tvDefault.visibility = View.VISIBLE
                    binding.btnSignUpNicknameComplete.isEnabled = false
                    binding.btnSignUpNicknameComplete.isSelected = false
                } else if (isValidEmail(email)) {
                    // 이메일 형식이 올바르면 기본 배경, 오류 메시지 감추기, 버튼 활성화
                    binding.etSignUpNickname.setBackgroundResource(R.drawable.bg_input_place_add_active)
                    binding.tvError.visibility = View.GONE
                    binding.tvDefault.visibility = View.VISIBLE
                    binding.btnSignUpNicknameComplete.isEnabled = true
                    binding.btnSignUpNicknameComplete.isSelected = true
                } else {
                    // 이메일 형식이 틀리면 오류 배경, 오류 메시지 표시, 버튼 비활성화
                    binding.etSignUpNickname.setBackgroundResource(R.drawable.bg_sign_up_error)
                    binding.tvError.visibility = View.VISIBLE
                    binding.tvDefault.visibility = View.GONE
                    binding.btnSignUpNicknameComplete.isEnabled = false
                    binding.btnSignUpNicknameComplete.isSelected = false
                }
            }
        })

        binding.btnSignUpNicknameComplete.setOnClickListener {
            hideKeyboard(binding.etSignUpNickname)
            viewModel.setNickname(binding.etSignUpNickname.text.toString().trim())
            viewModel.signUp()
        }

        viewModel.signUpResult.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = {
                    navController.navigate(R.id.action_navigation_sign_up_nickname_to_onboarding)
                },
                onFailure = { error ->
                    Toast.makeText(context, "회원가입 실패: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun hideKeyboard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun isValidEmail(email: String): Boolean {
        // 로컬 파트에 해당하는 문자열이 한글 또는 영문으로 1~5자 구성되었는지 검사
        val regex = "^[A-Za-z가-힣]{1,5}$"
        return Regex(regex).matches(email)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}