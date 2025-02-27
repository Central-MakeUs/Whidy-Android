package com.whidy.whidyandroid.presentation.my.edit

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.databinding.FragmentProfileEditBinding
import com.whidy.whidyandroid.presentation.base.MainActivity
import com.whidy.whidyandroid.presentation.my.MyViewModel
import timber.log.Timber

class ProfileEditFragment : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentProfileEditBinding? = null
    private val binding: FragmentProfileEditBinding
        get() = requireNotNull(_binding){"FragmentProfileEditBinding -> null"}

    private val myViewModel: MyViewModel by activityViewModels()

    private var selectedProfileImage: Int? = null  // 모달에서 선택한 프로필 이미지 (임시 저장)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileEditBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        (requireActivity() as MainActivity).hideBottomNavigation(true)

        binding.btnBack.setOnClickListener {
            navController.popBackStack()
        }

        binding.etNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Timber.d("닉네임 입력 중: $s")  // 닉네임 변경 로그
                // 사용자가 입력 중일 때 배경을 입력 중 상태 drawable로 변경
                binding.etNickname.setBackgroundResource(R.drawable.bg_input_place_add_active)
            }

            override fun afterTextChanged(s: Editable?) {
                val nickname = s.toString().trim()
                if (nickname.isEmpty()) {
                    // 입력이 없으면 기본 배경, 오류 메시지 감추기, 버튼 비활성화
                    binding.etNickname.setBackgroundResource(R.drawable.bg_input_place_add_default)
                    binding.tvError.visibility = View.GONE
                    binding.tvDefault.visibility = View.VISIBLE
                    binding.btnSubmit.isEnabled = false
                    binding.btnSubmit.isSelected = false
                } else if (isValidNickname(nickname)) {
                    // 이메일 형식이 올바르면 기본 배경, 오류 메시지 감추기, 버튼 활성화
                    binding.etNickname.setBackgroundResource(R.drawable.bg_input_place_add_active)
                    binding.tvError.visibility = View.GONE
                    binding.tvDefault.visibility = View.VISIBLE
                    binding.btnSubmit.isEnabled = true
                    binding.btnSubmit.isSelected = true
                } else {
                    // 이메일 형식이 틀리면 오류 배경, 오류 메시지 표시, 버튼 비활성화
                    binding.etNickname.setBackgroundResource(R.drawable.bg_sign_up_error)
                    binding.tvError.visibility = View.VISIBLE
                    binding.tvDefault.visibility = View.GONE
                    binding.btnSubmit.isEnabled = false
                    binding.btnSubmit.isSelected = false
                }
            }
        })

        binding.btnProfileEdit.setOnClickListener {
            val bottomSheet = MyProfileEditBottomSheet { selectedImage ->
                selectedProfileImage = selectedImage  // 선택한 이미지 임시 저장
                binding.ivUserProfile.setImageResource(selectedImage)  // UI에 즉시 반영
            }
            bottomSheet.show(parentFragmentManager, "ProfileImageBottomSheet")
        }

        myViewModel.profileImage.observe(viewLifecycleOwner) { imageRes ->
            binding.ivUserProfile.setImageResource(imageRes)  // ViewModel에 저장된 경우 UI 반영
        }

        myViewModel.nickname.observe(viewLifecycleOwner) { nickname ->
            binding.etNickname.setText(nickname)  // ViewModel에 저장된 경우 UI 반영
        }

        binding.btnSubmit.setOnClickListener {
            val nickname = binding.etNickname.text.toString()
            val profileImage = selectedProfileImage
            if (nickname.isEmpty() || !isValidNickname(nickname)) {
                return@setOnClickListener
            }

            myViewModel.setMyName(nickname)
            myViewModel.setNickname(nickname)
            if (profileImage != null) {
                myViewModel.setProfileImage(profileImage)
                myViewModel.setMyProfileImage(profileImage, requireContext())
            }

            navController.navigate(R.id.action_navigation_profile_edit_to_my)
        }
    }

    private fun isValidNickname(nickname: String): Boolean {
        return nickname.matches(Regex("^[A-Za-z가-힣]{1,5}$"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}