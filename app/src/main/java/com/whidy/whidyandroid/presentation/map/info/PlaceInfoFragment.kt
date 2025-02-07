package com.whidy.whidyandroid.presentation.map.info

import android.animation.ValueAnimator
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.databinding.FragmentPlaceInfoBinding
import com.whidy.whidyandroid.presentation.base.MainActivity
import com.whidy.whidyandroid.presentation.map.ItemType
import com.whidy.whidyandroid.utils.ItemVerticalDecoration

class PlaceInfoFragment: Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentPlaceInfoBinding? = null
    private val binding: FragmentPlaceInfoBinding
        get() = requireNotNull(_binding){"FragmentPlaceInfoBinding -> null"}

    private lateinit var placeReviewCommentAdapter: PlaceReviewCommentAdapter
    private lateinit var placeTimeAdapter: PlaceTimeAdapter

    private var isExpanded = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPlaceInfoBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        (requireActivity() as MainActivity).hideBottomNavigation(true)

        binding.btnBack.setOnClickListener {
            navController.navigateUp()
        }

        fun setImages(imageUrls: List<String>) {
            val imageViews = listOf(binding.ivPlaceImage1, binding.ivPlaceImage2, binding.ivPlaceImage3)
            val overlayView = binding.tvOverlay

            for (i in imageViews.indices) {
                if (i < imageUrls.size) {
                    Glide.with(this).load(imageUrls[i]).into(imageViews[i])
                    imageViews[i].visibility = View.VISIBLE
                } else {
                    imageViews[i].visibility = View.GONE
                }
            }

            if (imageUrls.size > 3) {
                Glide.with(this).load(imageUrls[2]).into(imageViews[2])
                overlayView.visibility = View.VISIBLE
                overlayView.text = "더보기\n+${imageUrls.size - 3}"
            } else {
                overlayView.visibility = View.GONE
            }
        }

        binding.ivPlaceImage3.setOnClickListener {
            navController.navigate(R.id.action_navigation_place_info_to_photo)
        }

        setImages(getDummyImages())

        placeTimeAdapter = PlaceTimeAdapter(getPlaceTimeData())
        binding.rvPlaceInfoTime.apply {
            adapter = placeTimeAdapter
        }

        binding.tvPlaceInfoTime.setOnClickListener {
            isExpanded = !isExpanded
            if (isExpanded) {
                expandView(binding.rvPlaceInfoTime, 300)
            } else {
                collapseView(binding.rvPlaceInfoTime, 300)
            }
            updateTimeDrawable(isExpanded)
        }

        // 3개만 보이는 RecyclerView 설정
        val collapsedAdapter = PlaceReviewTagAdapter(getPlaceReviewTagData().take(3))
        binding.rvPlaceReviewTagCollapsed.apply {
            adapter = collapsedAdapter
            val itemSpace = resources.getDimensionPixelSize(R.dimen.place_review_tag)
            addItemDecoration(ItemVerticalDecoration(itemSpace))
        }

        // 전체 리스트를 보이는 RecyclerView 설정
        val expandedAdapter = PlaceReviewTagAdapter(getPlaceReviewTagData())
        binding.rvPlaceReviewTagExpanded.apply {
            adapter = expandedAdapter
            val itemSpace = resources.getDimensionPixelSize(R.dimen.place_review_tag)
            addItemDecoration(ItemVerticalDecoration(itemSpace))
        }

        binding.btnPlaceReviewTagAllDown.setOnClickListener {
            isExpanded = !isExpanded
            collapseView(binding.rvPlaceReviewTagCollapsed, 500) // 3개 보이는 RecyclerView 숨기기
            expandView(binding.rvPlaceReviewTagExpanded, 500) // 전체 리스트 보이는 RecyclerView 펼치기
            binding.btnPlaceReviewTagAllDown.visibility = View.INVISIBLE
            binding.btnPlaceReviewTagAllUp.visibility = View.VISIBLE
        }

        binding.btnPlaceReviewTagAllUp.setOnClickListener {
            isExpanded = !isExpanded
            collapseView(binding.rvPlaceReviewTagExpanded, 500) // 전체 리스트 숨기기
            expandView(binding.rvPlaceReviewTagCollapsed, 500) // 3개 보이는 RecyclerView 다시 보이기
            binding.btnPlaceReviewTagAllUp.visibility = View.INVISIBLE
            binding.btnPlaceReviewTagAllDown.visibility = View.VISIBLE
        }

        placeReviewCommentAdapter = PlaceReviewCommentAdapter(getPlaceReviewCommentData())
        binding.rvPlaceReview.apply {
            adapter = placeReviewCommentAdapter
        }

        binding.btnScrap.setOnClickListener {
            it.isSelected = !it.isSelected
        }

        binding.btnPlaceReviewAll.setOnClickListener {
            navController.navigate(R.id.action_navigation_place_info_to_review_all)
        }

        binding.clPlaceReviewStars.setOnClickListener {
            navController.navigate(R.id.action_navigation_place_info_to_review_write)
        }
    }

    private fun expandView(view: View, duration: Long) {
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
        animator.duration = duration
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
    }

    private fun collapseView(view: View, duration: Long) {
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
        animator.duration = duration
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
    }

    private fun updateTimeDrawable(isExpanded: Boolean) {
        val drawableRes = if (isExpanded) R.drawable.ic_small_up else R.drawable.ic_small_down
        val drawable: Drawable? = ContextCompat.getDrawable(requireContext(), drawableRes)
        binding.tvPlaceInfoTime.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
    }

    private fun getDummyImages(): List<String> {
        return listOf(
            "https://helios-i.mashable.com/imagery/articles/04GeUVUQwZxpTYXdqbocKH2/hero-image.fill.size_1248x702.v1722586579.jpg",
            "https://helios-i.mashable.com/imagery/articles/04GeUVUQwZxpTYXdqbocKH2/hero-image.fill.size_1248x702.v1722586579.jpg",
            "https://helios-i.mashable.com/imagery/articles/04GeUVUQwZxpTYXdqbocKH2/hero-image.fill.size_1248x702.v1722586579.jpg",
            "https://helios-i.mashable.com/imagery/articles/04GeUVUQwZxpTYXdqbocKH2/hero-image.fill.size_1248x702.v1722586579.jpg",
            "https://helios-i.mashable.com/imagery/articles/04GeUVUQwZxpTYXdqbocKH2/hero-image.fill.size_1248x702.v1722586579.jpg",
        )
    }

    private fun getPlaceReviewTagData(): List<PlaceReviewTag> {
        return listOf(
            PlaceReviewTag(ItemType.COFFEE, 10),
            PlaceReviewTag(ItemType.SEAT, 6),
            PlaceReviewTag(ItemType.FOCUS, 3),
            PlaceReviewTag(ItemType.OTHER, 2),
            PlaceReviewTag(ItemType.ETC, 1)
        )
    }

    private fun getPlaceTimeData(): List<PlaceTime> {
        return listOf(
            PlaceTime("월", "08:00 ~ 22:00"),
            PlaceTime("화", "08:00 ~ 22:00"),
            PlaceTime("수", "08:00 ~ 22:00"),
            PlaceTime("목", "08:00 ~ 22:00"),
            PlaceTime("금", "08:00 ~ 22:00"),
            PlaceTime("토", "10:00 ~ 20:00"),
            PlaceTime("일", "10:00 ~ 20:00")
        )
    }

    private fun getPlaceReviewCommentData(): List<PlaceReviewComment> {
        return listOf(
            PlaceReviewComment("https://helios-i.mashable.com/imagery/articles/04GeUVUQwZxpTYXdqbocKH2/hero-image.fill.size_1248x702.v1722586579.jpg",
                "거부기",
                3,
                "2024.12.31",
                "1층은 분위기 좋고 2층은 작업이나 공부하기 좋은 곳이네요 ㅎㅎ 인테리어도 이쁘고 음악도 잔잔하니 다시 방문하고 싶 어요! 말차라떼랑 크로플도 맛있네요 :)",
                listOf(ItemType.COFFEE, ItemType.SEAT)
            ),
            PlaceReviewComment("https://helios-i.mashable.com/imagery/articles/04GeUVUQwZxpTYXdqbocKH2/hero-image.fill.size_1248x702.v1722586579.jpg",
                "거부기",
                3,
                "2024.12.31",
                "1층은 분위기 좋고 2층은 작업이나 공부하기 좋은 곳이네요 ㅎㅎ 인테리어도 이쁘고 음악도 잔잔하니 다시 방문하고 싶 어요! 말차라떼랑 크로플도 맛있네요 :)",
                listOf(ItemType.FOCUS)
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}