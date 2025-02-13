package com.whidy.whidyandroid.presentation.my.edit

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.databinding.BottomSheetMyProfileEditBinding
import com.whidy.whidyandroid.presentation.my.MyViewModel

class MyProfileEditBottomSheet(
    private val onImageSelected: (Int) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetMyProfileEditBinding? = null
    private val binding get() = _binding!!

    private var selectedImageRes: Int? = null

    private val myViewModel: MyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetMyProfileEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myViewModel.profileImage.observe(viewLifecycleOwner) { imageRes ->
            if (imageRes != null) {
                selectedImageRes = imageRes
                binding.ivUserProfile.setImageResource(imageRes)
            }
        }

        binding.ivProfileCafe.setOnClickListener {
            selectImage(R.drawable.ic_profile_cafe)
            vibrateDoubleClick()
        }
        binding.ivProfilePencil.setOnClickListener {
            selectImage(R.drawable.ic_profile_pencil)
            vibrateDoubleClick()
        }
        binding.ivProfileMouse.setOnClickListener {
            selectImage(R.drawable.ic_profile_mouse)
            vibrateDoubleClick()
        }
        binding.ivProfileBook.setOnClickListener {
            selectImage(R.drawable.ic_profile_book)
            vibrateDoubleClick()
        }

        binding.btnProfileComplete.setOnClickListener {
            selectedImageRes?.let { onImageSelected(it) }
            dismiss()
        }

        binding.btnCancel.setOnClickListener { dismiss() }
    }

    private fun vibrateDoubleClick() {
        // Vibrator 객체 얻기
        val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        // 진동 패턴: 시작 지연 0ms, 진동 50ms, 대기 50ms, 진동 50ms
        val pattern = longArrayOf(0, 50, 50, 50)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // API 26 이상에서는 VibrationEffect 사용
            val effect = VibrationEffect.createWaveform(pattern, -1)  // -1은 반복 없음
            vibrator.vibrate(effect)
        } else {
            // API 26 미만에서는 기존 방식 사용
            vibrator.vibrate(pattern, -1)
        }
    }

    private fun selectImage(imageRes: Int) {
        selectedImageRes = imageRes
        binding.ivUserProfile.setImageResource(imageRes)
        binding.btnProfileComplete.isEnabled = true
        binding.btnProfileComplete.isSelected = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
