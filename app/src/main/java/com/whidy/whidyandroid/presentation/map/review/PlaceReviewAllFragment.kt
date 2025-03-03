package com.whidy.whidyandroid.presentation.map.review

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.databinding.FragmentPlaceReviewAllBinding

class PlaceReviewAllFragment : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentPlaceReviewAllBinding? = null
    private val binding: FragmentPlaceReviewAllBinding
        get() = requireNotNull(_binding){"FragmentPlaceReviewAllBinding -> null"}

    private lateinit var placeReviewCommentAdapter: PlaceReviewCommentAdapter

    private var isExpanded = false

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

        // 3개만 보이는 RecyclerView 설정
//        val collapsedAdapter = PlaceReviewTagAdapter(getPlaceReviewTagData().take(3))
//        binding.rvPlaceReviewTagCollapsed.apply {
//            adapter = collapsedAdapter
//            val itemSpace = resources.getDimensionPixelSize(R.dimen.place_review_tag)
//            addItemDecoration(ItemVerticalDecoration(itemSpace))
//        }
//
//        // 전체 리스트를 보이는 RecyclerView 설정
//        val expandedAdapter = PlaceReviewTagAdapter(getPlaceReviewTagData())
//        binding.rvPlaceReviewTagExpanded.apply {
//            adapter = expandedAdapter
//            val itemSpace = resources.getDimensionPixelSize(R.dimen.place_review_tag)
//            addItemDecoration(ItemVerticalDecoration(itemSpace))
//        }

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

//        placeReviewCommentAdapter = PlaceReviewCommentAdapter(getPlaceReviewCommentData())
//        binding.rvPlaceReview.apply {
//            adapter = placeReviewCommentAdapter
//        }

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