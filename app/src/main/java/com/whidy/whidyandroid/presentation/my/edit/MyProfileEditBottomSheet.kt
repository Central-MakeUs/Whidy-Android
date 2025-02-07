package com.whidy.whidyandroid.presentation.my.edit

import android.os.Bundle
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

        binding.ivProfileCafe.setOnClickListener { selectImage(R.drawable.ic_profile_cafe) }
        binding.ivProfilePencil.setOnClickListener { selectImage(R.drawable.ic_profile_pencil) }
        binding.ivProfileMouse.setOnClickListener { selectImage(R.drawable.ic_profile_mouse) }
        binding.ivProfileBook.setOnClickListener { selectImage(R.drawable.ic_profile_book) }

        binding.btnProfileComplete.setOnClickListener {
            selectedImageRes?.let { onImageSelected(it) }
            dismiss()
        }

        binding.btnCancel.setOnClickListener { dismiss() }
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
