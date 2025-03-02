package com.whidy.whidyandroid.presentation.my.edit

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import com.bumptech.glide.Glide
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.databinding.FragmentProfileEditBinding
import com.whidy.whidyandroid.presentation.base.MainActivity
import com.whidy.whidyandroid.presentation.my.MyViewModel
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

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
                Timber.d("닉네임 입력 중: $s")
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
                binding.ivUserProfile.setImageResource(selectedImage)
            }
            bottomSheet.show(parentFragmentManager, "ProfileImageBottomSheet")
        }

        myViewModel.profileImage.observe(viewLifecycleOwner) { imageRes ->
            binding.ivUserProfile.setImageResource(imageRes)
        }

        myViewModel.nickname.observe(viewLifecycleOwner) { nickname ->
            binding.etNickname.setText(nickname)
        }

        myViewModel.profileImageUrl.observe(viewLifecycleOwner) { imageUrl ->
            Glide.with(requireContext())
                .load(imageUrl)
                .into(binding.ivUserProfile)
        }

        binding.btnSubmit.setOnClickListener {
            val nickname = binding.etNickname.text.toString()
            val profileImageRes = selectedProfileImage
            if (nickname.isEmpty() || !isValidNickname(nickname)) {
                return@setOnClickListener
            }

            myViewModel.setMyName(nickname)
            myViewModel.setNickname(nickname)

            if (profileImageRes != null) {
                val imageFile = getFileFromResource(profileImageRes)
                myViewModel.setMyProfileImage(imageFile)
                navController.popBackStack()
            }
        }

    }

    private fun isValidNickname(nickname: String): Boolean {
        return nickname.matches(Regex("^[A-Za-z가-힣]{1,5}$"))
    }

    private fun getFileFromResource(resId: Int): File {
        val bitmap = BitmapFactory.decodeResource(resources, resId)
        val file = File(requireContext().cacheDir, "temp_profile_image.png")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        return file
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}