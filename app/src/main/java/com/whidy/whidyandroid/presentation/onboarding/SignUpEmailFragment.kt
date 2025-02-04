package com.whidy.whidyandroid.presentation.onboarding

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.databinding.FragmentSignUpEmailBinding
import com.whidy.whidyandroid.presentation.base.MainActivity

class SignUpEmailFragment : Fragment() {

    private lateinit var navController: NavController
    private var _binding: FragmentSignUpEmailBinding? = null
    private val binding: FragmentSignUpEmailBinding
        get() = requireNotNull(_binding){"FragmentSignUpEmailBinding -> null"}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSignUpEmailBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        (requireActivity() as MainActivity).hideBottomNavigation(true)

        binding.btnBack.setOnClickListener {
            navController.popBackStack()
        }

        binding.etSignUpEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 별도 처리 없음
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 사용자가 입력 중일 때 배경을 입력 중 상태 drawable로 변경
                binding.etSignUpEmail.setBackgroundResource(R.drawable.bg_input_place_add_active)
            }

            override fun afterTextChanged(s: Editable?) {
                val email = s.toString().trim()
                if (email.isEmpty()) {
                    // 입력이 없으면 기본 배경, 오류 메시지 감추기, 버튼 비활성화
                    binding.etSignUpEmail.setBackgroundResource(R.drawable.bg_input_place_add_default)
                    binding.tvError.visibility = View.GONE
                    binding.btnSignUpEmailComplete.isEnabled = false
                    binding.btnSignUpEmailComplete.isSelected = false
                } else if (isValidEmail(email)) {
                    // 이메일 형식이 올바르면 기본 배경, 오류 메시지 감추기, 버튼 활성화
                    binding.etSignUpEmail.setBackgroundResource(R.drawable.bg_input_place_add_active)
                    binding.tvError.visibility = View.GONE
                    binding.btnSignUpEmailComplete.isEnabled = true
                    binding.btnSignUpEmailComplete.isSelected = true
                } else {
                    // 이메일 형식이 틀리면 오류 배경, 오류 메시지 표시, 버튼 비활성화
                    binding.etSignUpEmail.setBackgroundResource(R.drawable.bg_sign_up_error)
                    binding.tvError.visibility = View.VISIBLE
                    binding.btnSignUpEmailComplete.isEnabled = false
                    binding.btnSignUpEmailComplete.isSelected = false
                }
            }
        })

        binding.btnSignUpEmailComplete.setOnClickListener {
            navController.navigate(R.id.action_navigation_sign_up_email_to_nickname)
        }
    }

    // 이메일 유효성 검사 함수: Android 내장 패턴 사용
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}