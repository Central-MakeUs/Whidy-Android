package com.whidy.whidyandroid.presentation.map.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.databinding.FragmentPlaceReviewWriteBinding
import com.whidy.whidyandroid.model.PlaceType
import com.whidy.whidyandroid.presentation.base.MainActivity
import com.whidy.whidyandroid.presentation.map.home.MapViewModel
import timber.log.Timber

class PlaceReviewWriteFragment : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentPlaceReviewWriteBinding? = null
    private val binding: FragmentPlaceReviewWriteBinding
        get() = requireNotNull(_binding){"FragmentPlaceReviewWriteBinding -> null"}

    private val viewModel: PlaceReviewWriteViewModel by activityViewModels()
    private val mapViewModel: MapViewModel by activityViewModels()

    // 버튼 그룹 참조
    private lateinit var keywordOptions: List<View>
    private lateinit var howToUseOptions: List<View>
    private lateinit var howLongWaitOptions: List<View>
    private lateinit var withWhoOptions: List<View>
    private lateinit var visitPurposeOptions: List<View>

    // 매핑 정의 (버튼 텍스트 -> API/Enum 값)
    private val keywordMapping = mapOf(
        "커피가 맛있어요" to "TASTY_COFFEE",
        "디저트가 맛있어요" to "TASTY_DESSERT",
        "가성비가 좋아요" to "GOOD_VALUE",
        "매장이 넓어요" to "SPACIOUS",
        "음악이 좋아요" to "GOOD_MUSIC",
        "차분한 분위기에요" to "CALM_AMBIENCE",
        "좌석이 편해요" to "COMFORTABLE_SEAT",
        "오래 머무르기 좋아요" to "LONG_STAY"
    )
    private val howToUseMapping = mapOf(
        "예약 사용" to "true",
        "예약 미사용" to "false"
    )
    private val howLongWaitMapping = mapOf(
        "바로 입장" to "NO_WAIT",
        "10분 이내" to "WITHIN_10",
        "30분 이내" to "WITHIN_30",
        "30분 이상" to "OVER_30"
    )
    private val withWhoMapping = mapOf(
        "개인" to "SOLO",
        "단체" to "GROUP"
    )
    private val visitPurposeMapping = mapOf(
        "공부" to "STUDY",
        "노트북 작업" to "NOTEBOOK_WORK",
        "스터디" to "STUDY_GROUP",
        "독서" to "READING",
        "업무" to "WORK",
        "미팅" to "MEETING"
    )

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

        keywordOptions = listOf(
            binding.btnCoffee,
            binding.btnDessert,
            binding.btnValue,
            binding.btnWide,
            binding.btnMusic,
            binding.btnPlug,
            binding.btnSeat,
            binding.btnStay
        )
        howToUseOptions = listOf(
            binding.btnNoReservUse,
            binding.btnReservUse
        )
        howLongWaitOptions = listOf(
            binding.btnEnter,
            binding.btnEnter10,
            binding.btnEnter30,
            binding.btnEnter30Over
        )
        withWhoOptions = listOf(
            binding.btnPersonal,
            binding.btnGroup
        )
        visitPurposeOptions = listOf(
            binding.btnStudy,
            binding.btnNotebook,
            binding.btnStudyGroup,
            binding.btnRead,
            binding.btnWork,
            binding.btnMeeting
        )

        fun updateNextButtonState() {
            binding.btnPlaceReviewWriteNext.isSelected = keywordOptions.any { it.isSelected }
        }

        // 단일 선택 그룹: 선택된 버튼은 해당 그룹의 다른 버튼은 해제하고, 다시 누르면 해제
        fun setSingleSelectOption(options: List<View>, mapping: Map<String, String>, valueSetter: (String) -> Unit) {
            options.forEach { button ->
                button.setOnClickListener {
                    if (button.isSelected) {
                        button.isSelected = false
                        valueSetter("")
                    } else {
                        options.forEach { it.isSelected = false }
                        button.isSelected = true
                        // 버튼의 텍스트를 사용하여 매핑된 값을 viewModel에 전달
                        val text = if (button is TextView) button.text.toString() else ""
                        valueSetter(mapping[text] ?: text)
                    }
                    updateNextButtonState()
                }
            }
        }

        // 복수 선택 그룹: 각 버튼 개별 토글, 선택된 버튼들의 값을 모두 viewModel에 전달
        fun setMultiSelectOption(options: List<View>, mapping: Map<String, String>, valueSetter: (List<String>) -> Unit) {
            options.forEach { button ->
                button.setOnClickListener {
                    button.isSelected = !button.isSelected
                    val selected = options.filter { it.isSelected }
                        .mapNotNull { if (it is TextView) mapping[it.text.toString()] ?: it.text.toString() else null }
                    valueSetter(selected)
                    updateNextButtonState()
                }
            }
        }

        // 각 그룹에 대한 토글 로직 적용
        setMultiSelectOption(keywordOptions, keywordMapping) { selectedList ->
            viewModel.setKeyword(selectedList)
        }
        setSingleSelectOption(howToUseOptions, howToUseMapping) { selected ->
            viewModel.setHowToUse(selected)
        }
        setSingleSelectOption(howLongWaitOptions, howLongWaitMapping) { selected ->
            viewModel.setHowLongWait(selected)
        }
        setSingleSelectOption(withWhoOptions, withWhoMapping) { selected ->
            viewModel.setWithWho(selected)
        }
        setMultiSelectOption(visitPurposeOptions, visitPurposeMapping) { selectedList ->
            viewModel.setVisitPurpose(selectedList)
        }

        setupRatingTouchListener()

        binding.btnBack.setOnClickListener {
            navController.navigateUp()
        }

        binding.btnPlaceReviewWriteNext.setOnClickListener {
            navController.navigate(R.id.action_navigation_place_review_write_to_2)
        }

        // 만약 이미 작성된 리뷰가 있다면, viewModel.myReviews와 viewModel.placeDetail에서 가져와서 미리 반영
        mapViewModel.placeDetail.observe(viewLifecycleOwner) { place ->
            binding.tvPlaceName.text = place.name
            binding.tvPlaceType.text = PlaceType.fromString(place.placeType)
            binding.apply {
                if (place.images.isNotEmpty()) {
                    Glide.with(ivPlaceImage.context)
                        .load(place.images[0])
                        .into(ivPlaceImage)
                } else {
                    ivPlaceImage.visibility = View.INVISIBLE
                }
            }
            // placeDetail이 로드된 후, myReviews에서 해당 placeId와 일치하는 리뷰가 있는지 확인
            val myReview = mapViewModel.myReviews.value?.find { it.placeId == place.id }
            Timber.d("myReview: $myReview")
            if (myReview != null) {
                viewModel.setExistingReviewId(myReview.id)
                updateStars(myReview.score)

                keywordOptions.forEach { button ->
                    if (button is TextView) {
                        val mapped = keywordMapping[button.text.toString()] ?: button.text.toString()
                        button.isSelected = myReview.keywords.contains(mapped)
                    }
                }
                howToUseOptions.forEach { it.isSelected = false }
                if (myReview.isReserved) {
                    howToUseOptions.find { (it as TextView).text.toString() == "예약 사용" }?.isSelected = true
                    viewModel.setHowToUse("true")
                } else {
                    howToUseOptions.find { (it as TextView).text.toString() == "예약 미사용" }?.isSelected = true
                    viewModel.setHowToUse("false")
                }
                howLongWaitOptions.forEach { it.isSelected = false }
                howLongWaitOptions.find { (it as TextView).text.toString() ==
                        howLongWaitMapping.filterValues { it == myReview.waitTime }.keys.firstOrNull()
                }?.isSelected = true
                viewModel.setHowLongWait(myReview.waitTime)
                withWhoOptions.forEach { it.isSelected = false }
                withWhoOptions.find { (it as TextView).text.toString() ==
                        withWhoMapping.filterValues { it == myReview.companionType }.keys.firstOrNull()
                }?.isSelected = true
                viewModel.setWithWho(myReview.companionType)
                visitPurposeOptions.forEach { it.isSelected = false }
                visitPurposeOptions.forEach { button ->
                    if (button is TextView) {
                        val mapped = visitPurposeMapping[button.text.toString()] ?: button.text.toString()
                        if (myReview.visitPurposes.contains(mapped)) {
                            button.isSelected = true
                        }
                    }
                }
                viewModel.setVisitPurpose(myReview.visitPurposes)
            } else {
                viewModel.setExistingReviewId(null)
            }
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

        val reviewText = when (rating) {
            1 -> "별로에요"
            2 -> "나쁘지 않아요"
            3 -> "괜찮아요"
            4 -> "좋아요"
            5 -> "최고에요"
            else -> "솔직한 방문 후기를 남겨주세요!"
        }
        binding.tvTitleReview.text = reviewText
        viewModel.setScore(rating.toDouble())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}