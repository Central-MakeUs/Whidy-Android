package com.whidy.whidyandroid.presentation.map.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.databinding.FragmentPlaceReviewWriteBinding
import com.whidy.whidyandroid.presentation.base.MainActivity

class PlaceReviewWriteFragment : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentPlaceReviewWriteBinding? = null
    private val binding: FragmentPlaceReviewWriteBinding
        get() = requireNotNull(_binding){"FragmentPlaceReviewWriteBinding -> null"}

    private val viewModel: PlaceReviewWriteViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPlaceReviewWriteBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).hideBottomNavigation(true)

        navController = Navigation.findNavController(view)

        // 단일 선택 그룹
        val keywordOptions = listOf(
            binding.btnCoffee,
            binding.btnDessert,
            binding.btnValue,
            binding.btnWide,
            binding.btnMusic,
            binding.btnPlug,
            binding.btnSeat,
            binding.btnStay
        )
        val howToUseOptions = listOf(
            binding.btnNoReservUse,
            binding.btnReservUse
        )
        val howLongWaitOptions = listOf(
            binding.btnEnter,
            binding.btnEnter10,
            binding.btnEnter30,
            binding.btnEnter30Over
        )
        val withWhoOptions = listOf(
            binding.btnPersonal,
            binding.btnGroup
        )

        // 복수 선택 그룹
        val visitPurposeOptions = listOf(
            binding.btnStudy,
            binding.btnNotebook,
            binding.btnStudyGroup,
            binding.btnRead,
            binding.btnWork,
            binding.btnMeeting
        )

        setupRatingTouchListener()

        fun updateNextButtonState() {
            binding.btnPlaceReviewWriteNext.isSelected = keywordOptions.any { it.isSelected }
        }

        // 단일 선택 그룹의 토글 로직 (이미 선택된 버튼을 다시 누르면 해제)
        fun setSingleSelectOption(options: List<View>, valueSetter: (String) -> Unit) {
            options.forEach { button ->
                button.setOnClickListener {
                    if (button.isSelected) {
                        // 이미 선택되어 있다면 해제
                        button.isSelected = false
                        valueSetter("")
                    } else {
                        // 해당 그룹의 다른 버튼은 해제하고, 현재 버튼 선택
                        options.forEach { it.isSelected = false }
                        button.isSelected = true
                        if (button is android.widget.TextView) {
                            valueSetter(button.text.toString())
                        }
                    }
                    updateNextButtonState()
                }
            }
        }

        // 복수 선택 그룹의 토글 로직 (각 버튼 개별 토글)
        fun setMultiSelectOption(options: List<View>, valueSetter: (List<String>) -> Unit) {
            options.forEach { button ->
                button.setOnClickListener {
                    // 선택 상태 토글
                    button.isSelected = !button.isSelected
                    // 선택된 버튼들의 텍스트 목록 업데이트
                    val selected = options.filter { it.isSelected }
                        .mapNotNull { if (it is android.widget.TextView) it.text.toString() else null }
                    valueSetter(selected)
                    updateNextButtonState()
                }
            }
        }

        // 각 그룹에 대해 토글 로직 적용
        setMultiSelectOption(keywordOptions) { selectedList ->
            viewModel.setKeyword(selectedList)
        }
        setSingleSelectOption(howToUseOptions) { selected ->
            viewModel.setHowToUse(selected)
        }
        setSingleSelectOption(howLongWaitOptions) { selected ->
            viewModel.setHowLongWait(selected)
        }
        // withWho 그룹은 단일 선택이라고 가정 (viewModel에 별도의 메소드가 있다면 사용)
        setSingleSelectOption(withWhoOptions) { selected ->
            viewModel.setWithWho(selected)
        }
        // visitPurpose는 복수 선택 가능
        setMultiSelectOption(visitPurposeOptions) { selectedList ->
            viewModel.setVisitPurpose(selectedList)
        }

        binding.btnBack.setOnClickListener {
            navController.navigateUp()
        }

        binding.btnPlaceReviewWriteNext.setOnClickListener {
            navController.navigate(R.id.action_navigation_place_review_write_to_2)
        }
    }

    private fun setupRatingTouchListener() {
        binding.clPlaceReviewStars.apply {
            setOnTouchListener { v, event ->
                v.performClick()
                when (event.action) {
                    MotionEvent.ACTION_DOWN,
                    MotionEvent.ACTION_MOVE,
                    MotionEvent.ACTION_UP -> {
                        // 컨테이너 전체 너비
                        val containerWidth = binding.starContainers.width.toFloat()
                        // 보정 계수: 값이 1보다 크면 터치 감도가 높아진 효과가 있다.
                        val sensitivityMultiplier = 1.2f
                        // 보정된 터치 위치 계산
                        val effectiveX = event.x * sensitivityMultiplier
                        // 5점 만점 기준 별 하나당 영역을 계산하여 정수 점수로 변환
                        var rating = (effectiveX / containerWidth * 5).toInt()
                        // 터치가 좌측에 있으면 최소 1점, 5점을 초과하면 5점으로 보정
                        if (rating == 0 && event.x > 0) rating = 1
                        if (rating > 5) rating = 5
                        updateStars(rating)
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun updateStars(rating: Int) {
        val stars = listOf(
            binding.star1,
            binding.star2,
            binding.star3,
            binding.star4,
            binding.star5
        )
        stars.forEachIndexed { index, imageView ->
            if (index < rating) {
                imageView.setImageResource(R.drawable.ic_review_write_star)
            } else {
                imageView.setImageResource(R.drawable.ic_review_write_star_empty)
            }
        }

        // 별 개수에 따른 텍스트 업데이트
        val reviewText = when (rating) {
            1 -> "나빠요"
            2 -> "나쁘지 않아요"
            3 -> "보통이네요"
            4 -> "좋아요"
            5 -> "아주 좋아요"
            else -> "솔직한 방문 후기를 남겨주세요!"
        }
        binding.tvTitleReview.text = reviewText
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}