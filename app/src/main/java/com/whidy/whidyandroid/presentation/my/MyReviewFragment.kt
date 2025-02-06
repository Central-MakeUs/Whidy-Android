package com.whidy.whidyandroid.presentation.my

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.naver.maps.geometry.LatLng
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.databinding.FragmentMyReviewBinding
import com.whidy.whidyandroid.presentation.base.MainActivity
import com.whidy.whidyandroid.presentation.map.ItemType
import com.whidy.whidyandroid.presentation.map.MapViewModel

class MyReviewFragment : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentMyReviewBinding? = null
    private val binding: FragmentMyReviewBinding
        get() = requireNotNull(_binding){"FragmentMyReviewBinding -> null"}

    private val mapViewModel: MapViewModel by activityViewModels()

    private lateinit var myReviewAdapter: MyReviewAdapter
    private val reviews = mutableListOf(
        MyReview(
            placeName = "폴드 커피",
            score = 4.5f,
            reviewTime = "2024.12.31",
            reviewComment = "커피 맛이 좋고 분위기가 훌륭합니다.",
            tags = listOf(ItemType.COFFEE, ItemType.SEAT)
        ),
        MyReview(
            placeName = "스타벅스",
            score = 3.8f,
            reviewTime = "2024.11.22",
            reviewComment = "조용해서 공부하기 좋아요.",
            tags = listOf(ItemType.FOCUS)
        ),
        MyReview(
            placeName = "블루보틀",
            score = 4.7f,
            reviewTime = "2024.10.15",
            reviewComment = "핸드드립 커피가 정말 맛있어요. 공간도 넓고 쾌적합니다.",
            tags = listOf(ItemType.COFFEE, ItemType.SEAT, ItemType.FOCUS)
        ),
        MyReview(
            placeName = "빽다방",
            score = 3.5f,
            reviewTime = "2024.09.30",
            reviewComment = "가격이 저렴하고 양이 많아요. 하지만 자리 찾기가 힘듭니다.",
            tags = listOf(ItemType.COFFEE)
        ),
        MyReview(
            placeName = "커피빈",
            score = 4.2f,
            reviewTime = "2024.08.10",
            reviewComment = "라떼가 부드럽고 매장이 조용해서 대화하기 좋아요.",
            tags = listOf(ItemType.COFFEE, ItemType.SEAT)
        ),
        MyReview(
            placeName = "탐앤탐스",
            score = 3.9f,
            reviewTime = "2024.07.05",
            reviewComment = "넓은 테이블이 많아서 공부나 작업하기 좋아요.",
            tags = listOf(ItemType.SEAT, ItemType.FOCUS)
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMyReviewBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        (requireActivity() as MainActivity).hideBottomNavigation(true)

        binding.btnBack.setOnClickListener {
            navController.popBackStack()
        }

        myReviewAdapter = MyReviewAdapter(reviews,
            onDeleteClick = { review -> removeReview(review) },
            onPlaceNameClick = { navigateToPlaceInfo() },
            onEditClick = { navigateToEditScreen() }
        )

        binding.rvMyReview.adapter = myReviewAdapter

        updateEmptyView()
    }

    private fun updateEmptyView() {
        if (reviews.isEmpty()) {
            binding.clMyReviewEmptyView.visibility = View.VISIBLE
            binding.rvMyReview.visibility = View.GONE
        } else {
            binding.clMyReviewEmptyView.visibility = View.GONE
            binding.rvMyReview.visibility = View.VISIBLE
        }
    }

    private fun removeReview(review: MyReview) {
        myReviewAdapter.removeItem(review)
        updateEmptyView()
    }

    private fun navigateToEditScreen() {
    }

    private fun navigateToPlaceInfo() {
        val latitude = 37.546752
        val longitude = 126.949977

        mapViewModel.setSelectedLocation(LatLng(latitude, longitude))

        if (!navController.popBackStack(R.id.navigation_map, false)) {
            navController.navigate(R.id.navigation_map)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}