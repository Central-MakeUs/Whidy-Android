package com.whidy.whidyandroid.presentation.my.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.databinding.FragmentMyReviewBinding
import com.whidy.whidyandroid.presentation.base.MainActivity
import com.whidy.whidyandroid.presentation.map.home.MapViewModel
import timber.log.Timber

class MyReviewFragment : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentMyReviewBinding? = null
    private val binding: FragmentMyReviewBinding
        get() = requireNotNull(_binding){"FragmentMyReviewBinding -> null"}

    private val mapViewModel: MapViewModel by activityViewModels()

    private lateinit var myReviewAdapter: MyReviewAdapter

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

        myReviewAdapter = MyReviewAdapter(mutableListOf(),
            onItemClick = { review ->
                when (review.placeType) {
                    "GENERAL_CAFE" -> mapViewModel.fetchPlaceGeneralCafe(review.placeId)
                    "STUDY_CAFE" -> mapViewModel.fetchPlaceStudyCafe(review.placeId)
                    "FREE_STUDY_SPACE" -> mapViewModel.fetchPlaceFreeStudy(review.placeId)
                    "FREE_PICTURE" -> mapViewModel.fetchPlaceFreePicture(review.placeId)
                    "FREE_CLOTHES_RENTAL" -> mapViewModel.fetchPlaceFreeClothes(review.placeId)
                    "FRANCHISE_CAFE" -> mapViewModel.fetchPlaceFranchiseCafe(review.placeId)
                    else -> Timber.e("Unknown reviewType: ${review.placeType}")
                }
                navController.navigate(R.id.navigation_place_info) },
            onEditClick = { review ->
                when (review.placeType) {
                    "GENERAL_CAFE" -> mapViewModel.fetchPlaceGeneralCafe(review.placeId)
                    "STUDY_CAFE" -> mapViewModel.fetchPlaceStudyCafe(review.placeId)
                    "FREE_STUDY_SPACE" -> mapViewModel.fetchPlaceFreeStudy(review.placeId)
                    "FREE_PICTURE" -> mapViewModel.fetchPlaceFreePicture(review.placeId)
                    "FREE_CLOTHES_RENTAL" -> mapViewModel.fetchPlaceFreeClothes(review.placeId)
                    "FRANCHISE_CAFE" -> mapViewModel.fetchPlaceFranchiseCafe(review.placeId)
                    else -> Timber.e("Unknown reviewType: ${review.placeType}")
                }
                navController.navigate(R.id.navigation_place_review_write) },
            onDeleteClick = { review ->
                mapViewModel.deleteReview(review.id) { success ->
                    if (success) {
                        val newList = myReviewAdapter.items.toMutableList()
                        newList.remove(review)
                        myReviewAdapter.updateData(newList)
                    }
                }
            }
        )
        binding.rvMyReview.adapter = myReviewAdapter

        mapViewModel.fetchMyReviews()

        binding.btnBack.setOnClickListener {
            navController.popBackStack()
        }

        mapViewModel.myReviews.observe(viewLifecycleOwner) { reviews ->
            myReviewAdapter.updateData(reviews)
        }
        binding.rvMyReview.adapter = myReviewAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}