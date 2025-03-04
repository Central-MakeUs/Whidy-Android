package com.whidy.whidyandroid.presentation.map.review

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.databinding.FragmentPlaceReviewAllBinding
import com.whidy.whidyandroid.model.ItemType
import com.whidy.whidyandroid.presentation.map.home.MapViewModel
import com.whidy.whidyandroid.utils.ItemVerticalDecoration

class PlaceReviewAllFragment : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentPlaceReviewAllBinding? = null
    private val binding: FragmentPlaceReviewAllBinding
        get() = requireNotNull(_binding){"FragmentPlaceReviewAllBinding -> null"}

    private lateinit var placeReviewCommentAdapter: PlaceReviewCommentAdapter

    private var isExpanded = false

    private val mapViewModel: MapViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPlaceReviewAllBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        binding.btnBack.setOnClickListener {
            navController.navigateUp()
        }

        mapViewModel.placeDetail.observe(viewLifecycleOwner) { place ->
            binding.tvPlaceInfoScore.text = (place.reviewScore ?: 0.0).toString()
            binding.tvPlaceInfoReviewAmount.text = "(${place.reviewNum})"
        }

        placeReviewCommentAdapter = PlaceReviewCommentAdapter(emptyList())
        binding.rvPlaceReview.adapter = placeReviewCommentAdapter

        // 3개만 보이는 RecyclerView 설정
        val collapsedAdapter = PlaceReviewTagAdapter(emptyList())
        binding.rvPlaceReviewTagCollapsed.apply {
            adapter = collapsedAdapter
            addItemDecoration(ItemVerticalDecoration(resources.getDimensionPixelSize(R.dimen.place_review_tag)))
        }

        // 전체 리스트를 보이는 RecyclerView 설정
        val expandedAdapter = PlaceReviewTagAdapter(emptyList())
        binding.rvPlaceReviewTagExpanded.apply {
            adapter = expandedAdapter
            addItemDecoration(ItemVerticalDecoration(resources.getDimensionPixelSize(R.dimen.place_review_tag)))
        }

        mapViewModel.reviews.observe(viewLifecycleOwner) { reviews ->
            placeReviewCommentAdapter.updateData(reviews)

            val keywordCountMap = mutableMapOf<String, Int>()
            reviews.forEach { review ->
                review.keywords.forEach { keyword ->
                    // 키워드가 이미 존재하면 +1, 없으면 1로 초기화
                    keywordCountMap[keyword] = keywordCountMap.getOrDefault(keyword, 0) + 1
                }
            }

            // 각 키워드를 ItemType enum으로 변환하여 PlaceReviewTag 객체 생성
            val placeReviewTags = keywordCountMap.mapNotNull { (keyword, count) ->
                try {
                    val itemType = ItemType.valueOf(keyword)
                    PlaceReviewTag(type = itemType, peopleCount = count)
                } catch (e: Exception) {
                    null
                }
            }

            // 어댑터에 업데이트
            collapsedAdapter.updateData(placeReviewTags.take(3))
            expandedAdapter.updateData(placeReviewTags)

            if (placeReviewTags.size <= 3) {
                binding.btnPlaceReviewTagAllDown.visibility = View.GONE
                binding.btnPlaceReviewTagAllUp.visibility = View.GONE
            } else {
                // 3개 초과이면 기본적으로 축소 상태이므로 down 버튼 보이고, up 버튼 숨김
                binding.btnPlaceReviewTagAllDown.visibility = View.VISIBLE
                binding.btnPlaceReviewTagAllUp.visibility = View.INVISIBLE
            }
        }

        binding.btnPlaceReviewTagAllDown.setOnClickListener {
            isExpanded = !isExpanded
            collapseView(binding.rvPlaceReviewTagCollapsed) // 3개 보이는 RecyclerView 숨기기
            expandView(binding.rvPlaceReviewTagExpanded) // 전체 리스트 보이는 RecyclerView 펼치기
            binding.btnPlaceReviewTagAllDown.visibility = View.INVISIBLE
            binding.btnPlaceReviewTagAllUp.visibility = View.VISIBLE
        }

        binding.btnPlaceReviewTagAllUp.setOnClickListener {
            isExpanded = !isExpanded
            collapseView(binding.rvPlaceReviewTagExpanded) // 전체 리스트 숨기기
            expandView(binding.rvPlaceReviewTagCollapsed) // 3개 보이는 RecyclerView 다시 보이기
            binding.btnPlaceReviewTagAllUp.visibility = View.INVISIBLE
            binding.btnPlaceReviewTagAllDown.visibility = View.VISIBLE
        }

        binding.clPlaceReviewStars.setOnClickListener {
            navController.navigate(R.id.action_navigation_place_review_all_to_write)
        }
    }

    private fun expandView(view: View) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val targetHeight = view.measuredHeight

        view.layoutParams.height = 0
        view.visibility = View.VISIBLE

        val animator = ValueAnimator.ofInt(0, targetHeight)
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            view.layoutParams.height = value
            view.requestLayout()
        }
        animator.duration = 500
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
    }

    private fun collapseView(view: View) {
        val initialHeight = view.measuredHeight

        val animator = ValueAnimator.ofInt(initialHeight, 0)
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            view.layoutParams.height = value
            view.requestLayout()
            if (value == 0) {
                view.visibility = View.GONE
            }
        }
        animator.duration = 500
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}