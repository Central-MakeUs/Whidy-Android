package com.whidy.whidyandroid.presentation.map.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.databinding.FragmentPlaceFilterBinding
import com.whidy.whidyandroid.presentation.base.MainActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber

class PlaceFilterFragment : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentPlaceFilterBinding? = null
    private val binding: FragmentPlaceFilterBinding
        get() = requireNotNull(_binding){"FragmentPlaceFilterBinding -> null"}

    private val viewModel: PlaceFilterViewModel by activityViewModels()

    private var loadingJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPlaceFilterBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).hideBottomNavigation(true)

        navController = Navigation.findNavController(view)

        performSearch()

        binding.btnBack.setOnClickListener {
            navController.navigateUp()
        }

        val priceOptions = listOf(
            binding.btnPrice1000,
            binding.btnPrice2000,
            binding.btnPrice3000,
            binding.btnPrice4000,
            binding.btnPrice5000
        )
        priceOptions.forEach { button ->
            button.setOnClickListener {
                if (button.isSelected) {
                    button.isSelected = false
                    viewModel.setPriceRange("")
                } else {
                    priceOptions.forEach { it.isSelected = false }
                    button.isSelected = true
                    // "1000원"과 같이 되어 있으므로 "원"을 제거하고 정수로 변환
                    val priceText = button.text.toString().trim().replace("원", "").trim()
                    val priceValue = priceText.toIntOrNull() ?: 0
                    // 예: 1000원 → beverageFrom=1000, beverageTo=1999, 가격 범위를 "1000-1999" 문자열로 저장
                    val range = "$priceValue-${priceValue + 999}"
                    viewModel.setPriceRange(range)
                }
                performSearch()
            }
        }

        // 요일 옵션 버튼 그룹
        val weekOptions = listOf(
            binding.btnMon,
            binding.btnTue,
            binding.btnWed,
            binding.btnThu,
            binding.btnFri,
            binding.btnSat,
            binding.btnSun,
            binding.btnAllDay
        )
        // 매핑: "월" -> "MONDAY", "화" -> "TUESDAY", ..., "전체" -> ""
        val dayMapping = mapOf(
            "월" to "MONDAY",
            "화" to "TUESDAY",
            "수" to "WEDNESDAY",
            "목" to "THURSDAY",
            "금" to "FRIDAY",
            "토" to "SATURDAY",
            "일" to "SUNDAY",
            "전체" to ""
        )
        weekOptions.forEach { button ->
            button.setOnClickListener {
                if (button.isSelected) {
                    button.isSelected = false
                    viewModel.setVisitWeek("")
                } else {
                    weekOptions.forEach { it.isSelected = false }
                    button.isSelected = true
                    val dayText = button.text.toString().trim()
                    viewModel.setVisitWeek(dayMapping[dayText] ?: "")
                }
                performSearch()
            }
        }

        val peopleOptions = listOf(
            binding.btnPrivate,
            binding.btnGroup
        )
        peopleOptions.forEach { button ->
            button.setOnClickListener {
                if (button.isSelected) {
                    button.isSelected = false
                    viewModel.setVisitPeople("")
                } else {
                    peopleOptions.forEach { it.isSelected = false }
                    button.isSelected = true
                    viewModel.setVisitPeople(button.text.toString().trim())
                }
                performSearch()
            }
        }

        // 예약 옵션 (예약 사용, 예약 미사용)
        val reservOptions = listOf(
            binding.btnReservNeeded,
            binding.btnReservNeededNot
        )
        reservOptions.forEach { button ->
            button.setOnClickListener {
                if (button.isSelected) {
                    button.isSelected = false
                    viewModel.setReservation("")
                } else {
                    reservOptions.forEach { it.isSelected = false }
                    button.isSelected = true
                    viewModel.setReservation(button.text.toString().trim())
                }
                performSearch()
            }
        }

        // 리뷰 점수 옵션 (단일 선택)
        val reviewScoreOptions = listOf(
            binding.btn1,
            binding.btn2,
            binding.btn3,
            binding.btn4,
            binding.btn5
        )
        reviewScoreOptions.forEach { button ->
            button.setOnClickListener {
                if (button.isSelected) {
                    button.isSelected = false
                    viewModel.setReviewScore("")
                } else {
                    reviewScoreOptions.forEach { it.isSelected = false }
                    button.isSelected = true
                    viewModel.setReviewScore(button.text.toString().trim())
                }
                performSearch()
            }
        }

        binding.btnPlaceFilterInitial.setOnClickListener {
            priceOptions.forEach { it.isSelected = false }
            viewModel.setPriceRange("")

            weekOptions.forEach { it.isSelected = false }
            viewModel.setVisitWeek("")

            peopleOptions.forEach { it.isSelected = false }
            viewModel.setVisitPeople("")

            reservOptions.forEach { it.isSelected = false }
            viewModel.setReservation("")

            reviewScoreOptions.forEach { it.isSelected = false }
            viewModel.setReviewScore("")

            performSearch()
        }

        binding.btnPlaceFilterResult.setOnClickListener {
            navController.navigate(R.id.action_navigation_place_filter_to_search)
        }

        // 검색 결과 개수 갱신
        viewModel.searchResults.observe(viewLifecycleOwner) { places ->
            Timber.d("검색 결과 개수: ${places?.size}")
            val count = places?.size ?: 0
            stopLoadingAnimation("${count}곳 보기")
        }
    }

    private fun performSearch() {
        startLoadingAnimation()

        // 키워드는 항상 null로 처리
        val keyword: String? = null

        // 가격 범위: viewModel.priceRange가 "1000-1999" 형식으로 저장되어 있다고 가정
        val priceRange: String? = viewModel.priceRange.value.takeUnless { it.isNullOrBlank() }
        val beverageFrom: Int? = if (!priceRange.isNullOrEmpty() && priceRange.contains("-")) {
            val parts = priceRange.split("-")
            parts[0].toIntOrNull()
        } else {
            null
        }
        val beverageTo: Int? = if (!priceRange.isNullOrEmpty() && priceRange.contains("-")) {
            val parts = priceRange.split("-")
            parts[1].toIntOrNull()
        } else {
            null
        }

        // 리뷰 점수: 선택된 리뷰 점수가 있다면 reviewScoreFrom는 해당 값, reviewScoreTo는 값+1로 계산
        val reviewScoreStr: String? = viewModel.reviewScore.value.takeUnless { it.isNullOrBlank() }
        val reviewScoreFrom: Int? = reviewScoreStr?.toIntOrNull()
        val reviewScoreTo: Int? = reviewScoreFrom?.plus(1)

        // 요일: 선택된 요일을 그대로 사용 (예: "MONDAY")
        val businessDayOfWeek: String? = viewModel.visitWeek.value.takeUnless { it.isNullOrBlank() }

        // API 호출: keyword는 항상 null로 전달
        viewModel.fetchPlaceList(
            keyword = keyword,
            reviewScoreFrom = reviewScoreFrom,
            reviewScoreTo = reviewScoreTo,
            beverageFrom = beverageFrom,
            beverageTo = beverageTo,
            businessDayOfWeek = businessDayOfWeek
        )
    }

    // 로딩 애니메이션 시작 함수
    private fun startLoadingAnimation() {
        loadingJob?.cancel()  // 기존 애니메이션 취소
        loadingJob = viewLifecycleOwner.lifecycleScope.launch {
            val baseText = "로딩중"
            var dotCount = 0
            while (isActive) {
                val dots = ".".repeat(dotCount)
                binding.btnPlaceFilterResult.text = "$baseText$dots"
                dotCount = (dotCount + 1) % 4  // 0~3 사이 반복
                delay(100)
            }
        }
    }

    // 로딩 애니메이션 정지 및 최종 텍스트 설정 함수
    private fun stopLoadingAnimation(finalText: String) {
        loadingJob?.cancel()
        binding.btnPlaceFilterResult.text = finalText
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}