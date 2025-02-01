package com.whidy.whidyandroid.presentation.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.whidy.whidyandroid.databinding.FragmentPlaceInfoBinding

class PlaceInfoFragment: Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentPlaceInfoBinding? = null
    private val binding: FragmentPlaceInfoBinding
        get() = requireNotNull(_binding){"FragmentPlaceInfoBinding -> null"}

    private lateinit var placeReviewTagAdapter: PlaceReviewTagAdapter
    private lateinit var placeReviewCommentAdapter: PlaceReviewCommentAdapter

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

        setImages(getDummyImages())

        placeReviewTagAdapter = PlaceReviewTagAdapter(getPlaceReviewTagData())
        binding.rvPlaceReviewTag.apply {
            adapter = placeReviewTagAdapter
        }

        binding.btnPlaceReviewTagAll.setOnClickListener {
            placeReviewTagAdapter.toggleItems()
        }

        placeReviewCommentAdapter = PlaceReviewCommentAdapter(getPlaceReviewCommentData())
        binding.rvPlaceReview.apply {
            adapter = placeReviewCommentAdapter
        }

        binding.btnPlaceReviewTagAll.setOnClickListener {
        }

        binding.btnScrap.setOnClickListener {
            it.isSelected = !it.isSelected
        }
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

    private fun getPlaceReviewCommentData(): List<PlaceReviewComment> {
        return listOf(
            PlaceReviewComment("https://helios-i.mashable.com/imagery/articles/04GeUVUQwZxpTYXdqbocKH2/hero-image.fill.size_1248x702.v1722586579.jpg",
                "거부기",
                3,
                "2024.12.31",
                "1층은 분위기 좋고 2층은 작업이나 공부하기 좋은 곳이네요 ㅎㅎ 인테리어도 이쁘고 음악도 잔잔하니 다시 방문하고 싶 어요! 말차라떼랑 크로플도 맛있네요 :)"
            ),
            PlaceReviewComment("https://helios-i.mashable.com/imagery/articles/04GeUVUQwZxpTYXdqbocKH2/hero-image.fill.size_1248x702.v1722586579.jpg",
                "거부기",
                3,
                "2024.12.31",
                "1층은 분위기 좋고 2층은 작업이나 공부하기 좋은 곳이네요 ㅎㅎ 인테리어도 이쁘고 음악도 잔잔하니 다시 방문하고 싶 어요! 말차라떼랑 크로플도 맛있네요 :)"
            ),
            PlaceReviewComment("https://helios-i.mashable.com/imagery/articles/04GeUVUQwZxpTYXdqbocKH2/hero-image.fill.size_1248x702.v1722586579.jpg",
                "거부기",
                3,
                "2024.12.31",
                "1층은 분위기 좋고 2층은 작업이나 공부하기 좋은 곳이네요 ㅎㅎ 인테리어도 이쁘고 음악도 잔잔하니 다시 방문하고 싶 어요! 말차라떼랑 크로플도 맛있네요 :)"
            ),
            PlaceReviewComment("https://helios-i.mashable.com/imagery/articles/04GeUVUQwZxpTYXdqbocKH2/hero-image.fill.size_1248x702.v1722586579.jpg",
                "거부기",
                3,
                "2024.12.31",
                "1층은 분위기 좋고 2층은 작업이나 공부하기 좋은 곳이네요 ㅎㅎ 인테리어도 이쁘고 음악도 잔잔하니 다시 방문하고 싶 어요! 말차라떼랑 크로플도 맛있네요 :)"
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}