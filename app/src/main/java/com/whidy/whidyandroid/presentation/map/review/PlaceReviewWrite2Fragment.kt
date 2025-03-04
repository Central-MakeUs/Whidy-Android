package com.whidy.whidyandroid.presentation.map.review

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.databinding.FragmentPlaceReviewWrite2Binding
import com.whidy.whidyandroid.presentation.base.MainActivity
import com.whidy.whidyandroid.presentation.map.home.MapViewModel

class PlaceReviewWrite2Fragment : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentPlaceReviewWrite2Binding? = null
    private val binding: FragmentPlaceReviewWrite2Binding
        get() = requireNotNull(_binding){"FragmentPlaceReviewWrite2Binding -> null"}

    private val mapViewModel: MapViewModel by activityViewModels()
    private val viewModel: PlaceReviewWriteViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPlaceReviewWrite2Binding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).hideBottomNavigation(true)

        navController = Navigation.findNavController(view)

        binding.btnBack.setOnClickListener {
            navController.navigateUp()
        }

        binding.etPlaceReview.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.etPlaceReview.setBackgroundResource(R.drawable.bg_input_place_add_active)
            } else {
                // 포커스 해제 시, 텍스트가 없으면 원래 배경 복원
                if (binding.etPlaceReview.text.isNullOrEmpty()) {
                    binding.etPlaceReview.setBackgroundResource(R.drawable.bg_input_place_add_default)
                }
            }
        }

        binding.etPlaceReview.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val length = s?.length ?: 0
                binding.tvCharCount.text = "$length"
                binding.btnPlaceReviewWriteComplete.isSelected = length > 0
                viewModel.setComment(binding.etPlaceReview.text.toString())

                // 글자가 400글자면 카운트 TextView의 색상 변경, 아니면 원래 색상 적용
                if (length == 400) {
                    binding.tvCharCountMax.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.G900)
                    )
                } else {
                    binding.tvCharCountMax.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.G300)
                    )
                }
            }
        })

        mapViewModel.placeDetail.observe(viewLifecycleOwner) { place ->
            val existingReview = mapViewModel.myReviews.value?.find { it.placeId == place.id }
            if (existingReview != null) {
                // 기존 리뷰가 있다면 그 id를 ViewModel에 저장
                viewModel.setExistingReviewId(existingReview.id)
                binding.etPlaceReview.setText(existingReview.comment)
            } else {
                viewModel.setExistingReviewId(null)
            }
        }

        binding.btnPlaceReviewWriteComplete.setOnClickListener {
            mapViewModel.placeDetail.observe(viewLifecycleOwner) { place ->
                if (viewModel.existingReviewId.value == null) {
                    // 신규 리뷰 저장 (POST)
                    viewModel.postReview(place.id,
                        onSuccess = {
                            navController.navigate(R.id.action_navigation_place_write_2_to_info, null,
                                androidx.navigation.navOptions {
                                    popUpTo(R.id.navigation_place_review_write) { inclusive = true }
                                }
                            )
                        },
                        onFailure = {
                            Toast.makeText(requireContext(), "리뷰 저장 실패", Toast.LENGTH_SHORT).show()
                        }
                    )
                } else {
                    // 기존 리뷰 업데이트 (PUT)
                    viewModel.updateReview(place.id,
                        onSuccess = {
                            navController.navigate(R.id.action_navigation_place_write_2_to_info, null,
                                androidx.navigation.navOptions {
                                    popUpTo(R.id.navigation_place_review_write) { inclusive = true }
                                }
                            )
                        },
                        onFailure = {
                            Toast.makeText(requireContext(), "리뷰 업데이트 실패", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}