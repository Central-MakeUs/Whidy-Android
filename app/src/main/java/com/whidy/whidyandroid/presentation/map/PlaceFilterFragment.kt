package com.whidy.whidyandroid.presentation.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.whidy.whidyandroid.databinding.FragmentPlaceFilterBinding
import com.whidy.whidyandroid.presentation.base.MainActivity

class PlaceFilterFragment : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentPlaceFilterBinding? = null
    private val binding: FragmentPlaceFilterBinding
        get() = requireNotNull(_binding){"FragmentPlaceFilterBinding -> null"}

    private val viewModel: PlaceFilterViewModel by activityViewModels()

    private lateinit var amTimeAdapter: PlaceFilterAMTimeAdapter
    private lateinit var pmTimeAdapter: PlaceFilterPMTimeAdapter

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

        binding.btnBack.setOnClickListener {
            navController.navigateUp()
        }

        // 1. 공간 종류 그룹 처리
        val spaceOptions = listOf(
            binding.btnFreeSpace,
            binding.btnStudyCafe,
            binding.btnFranchiseCafe,
            binding.btnPersonalCafe,
            binding.btnSuitRental,
            binding.btnInterviewPhoto
        )
        spaceOptions.forEach { textView ->
            textView.setOnClickListener {
                // 그룹 내 모든 옵션 초기화
                spaceOptions.forEach {
                    it.isSelected = false
                }
                // 현재 옵션 선택
                textView.isSelected = true
                viewModel.setSpaceType(textView.text.toString())
            }
        }

        // 2. 아메리카노 가격 그룹 처리
        val priceOptions = listOf(
            binding.btnPrice1000,
            binding.btnPrice2000,
            binding.btnPrice3000,
            binding.btnPrice4000,
            binding.btnPrice5000
        )
        priceOptions.forEach { textView ->
            textView.setOnClickListener {
                priceOptions.forEach {
                    it.isSelected = false
                }
                textView.isSelected = true
                viewModel.setPriceRange(textView.text.toString())
            }
        }

        // 3. 방문 요일 그룹 처리
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
        weekOptions.forEach { textView ->
            textView.setOnClickListener {
                weekOptions.forEach {
                    it.isSelected = false
                }
                textView.isSelected = true
                viewModel.setVisitWeek(textView.text.toString())
            }
        }

        // 4. 방문 시간 그룹 처리 (예: 영업중 / 24시간 영업)
        val visitTimeOptions = listOf(
            binding.btnOpen,
            binding.btnOpenAllDay
        )
        visitTimeOptions.forEach { textView ->
            textView.setOnClickListener {
                visitTimeOptions.forEach {
                    it.isSelected = false
                }
                textView.isSelected = true
                viewModel.setVisitTime(textView.text.toString())
            }
        }

        // 5. 방문 인원 그룹 처리
        val peopleOptions = listOf(
            binding.btnPrivate,
            binding.btnGroup
        )
        peopleOptions.forEach { textView ->
            textView.setOnClickListener {
                peopleOptions.forEach {
                    it.isSelected = false
                }
                textView.isSelected = true
                viewModel.setVisitPeople(textView.text.toString())
            }
        }

        // 6. 예약 필수 유무 그룹 처리
        val reservOptions = listOf(
            binding.btnReservNeeded,
            binding.btnReservNeededNot
        )
        reservOptions.forEach { textView ->
            textView.setOnClickListener {
                reservOptions.forEach {
                    it.isSelected = false
                }
                textView.isSelected = true
                viewModel.setReservation(textView.text.toString())
            }
        }

        // 7. 리뷰 점수 그룹 처리
        val reviewScoreOptions = listOf(
            binding.btn1,
            binding.btn2,
            binding.btn3,
            binding.btn4,
            binding.btn5
        )
        reviewScoreOptions.forEach { textView ->
            textView.setOnClickListener {
                reviewScoreOptions.forEach {
                    it.isSelected = false
                }
                textView.isSelected = true
                viewModel.setReviewScore(textView.text.toString())
            }
        }

        val hours = (1..12).map { "${it}시" }

        amTimeAdapter = PlaceFilterAMTimeAdapter(hours) { selectedTime ->
            binding.tvVisitTimeAm.text = selectedTime
            binding.rvVisitTimeAm.visibility = View.GONE
            binding.tvVisitTimeAm.visibility = View.VISIBLE
            viewModel.setVisitTime(selectedTime)
        }
        binding.rvVisitTimeAm.adapter = amTimeAdapter

        pmTimeAdapter = PlaceFilterPMTimeAdapter(hours) { selectedTime ->
            binding.tvVisitTimePm.text = selectedTime
            binding.rvVisitTimePm.visibility = View.GONE
            binding.tvVisitTimePm.visibility = View.VISIBLE
            viewModel.setVisitTime(selectedTime)
        }
        binding.rvVisitTimePm.adapter = pmTimeAdapter

        binding.rvVisitTimeAm.apply {
            setOnTouchListener { view, event ->
                when (event.action) {
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_DOWN -> {
                        binding.btnAm.isActivated = true
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        binding.btnAm.isActivated = false
                        view.performClick()
                    }
                }
                false
            }
        }

        binding.rvVisitTimePm.apply {
            setOnTouchListener { view, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        binding.btnPm.isActivated = true
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        binding.btnPm.isActivated = false
                        view.performClick()
                    }
                }
                false
            }
        }

        // 오전 버튼 클릭 시: 오전 리사이클러뷰 표시, 오후 관련 뷰는 감춤
        binding.btnAm.setOnClickListener {
            binding.rvVisitTimeAm.visibility = View.VISIBLE
            binding.tvVisitTimeAm.visibility = View.GONE

            binding.rvVisitTimePm.visibility = View.GONE
            binding.tvVisitTimePm.visibility = View.GONE

            binding.btnAm.isSelected = true
            binding.btnPm.isSelected = false
            binding.btnPm.isActivated = false
        }

        // 오후 버튼 클릭 시: 오후 리사이클러뷰 표시, 오전 관련 뷰는 감춤
        binding.btnPm.setOnClickListener {
            binding.rvVisitTimeAm.visibility = View.GONE
            binding.tvVisitTimeAm.visibility = View.GONE

            binding.rvVisitTimePm.visibility = View.VISIBLE
            binding.tvVisitTimePm.visibility = View.GONE

            binding.btnPm.isSelected = true
            binding.btnAm.isSelected = false
            binding.btnAm.isActivated = false
        }

        binding.btnPlaceFilterInitial.setOnClickListener {
            // 1. 공간 종류 그룹 초기화
            spaceOptions.forEach { it.isSelected = false }
            viewModel.setSpaceType("")

            // 2. 아메리카노 가격 그룹 초기화
            priceOptions.forEach { it.isSelected = false }
            viewModel.setPriceRange("")

            // 3. 방문 요일 그룹 초기화
            weekOptions.forEach { it.isSelected = false }
            viewModel.setVisitWeek("")

            // 4. 방문 시간 그룹 초기화 (영업중/24시간 영업 버튼)
            visitTimeOptions.forEach { it.isSelected = false }
            viewModel.setVisitTime("")

            // 5. 방문 인원 그룹 초기화
            peopleOptions.forEach { it.isSelected = false }
            viewModel.setVisitPeople("")

            // 6. 예약 필수 유무 그룹 초기화
            reservOptions.forEach { it.isSelected = false }
            viewModel.setReservation("")

            // 7. 리뷰 점수 그룹 초기화
            reviewScoreOptions.forEach { it.isSelected = false }
            viewModel.setReviewScore("")

            // 8. 오전/오후 리사이클러뷰와 시간 텍스트뷰 초기화
            binding.rvVisitTimeAm.visibility = View.GONE
            binding.rvVisitTimePm.visibility = View.GONE
            binding.tvVisitTimeAm.visibility = View.GONE
            binding.tvVisitTimePm.visibility = View.GONE

            // 9. 오전/오후 버튼의 선택/activated 상태 초기화
            binding.btnAm.isSelected = false
            binding.btnPm.isSelected = false
            binding.btnAm.isActivated = false
            binding.btnPm.isActivated = false
        }

        binding.btnPlaceFilterResult.setOnClickListener {
            navController.navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}