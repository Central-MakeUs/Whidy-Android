package com.whidy.whidyandroid.presentation.map.info

import android.animation.ValueAnimator
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.kakao.sdk.common.util.KakaoCustomTabsClient
import com.kakao.sdk.share.ShareClient
import com.kakao.sdk.share.WebSharerClient
import com.kakao.sdk.template.model.Button
import com.kakao.sdk.template.model.Content
import com.kakao.sdk.template.model.FeedTemplate
import com.kakao.sdk.template.model.ItemContent
import com.kakao.sdk.template.model.Link
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.databinding.FragmentPlaceInfoBinding
import com.whidy.whidyandroid.model.PlaceType
import com.whidy.whidyandroid.presentation.base.MainActivity
import com.whidy.whidyandroid.presentation.map.ItemType
import com.whidy.whidyandroid.presentation.map.home.MapViewModel
import com.whidy.whidyandroid.presentation.scrap.ScrapViewModel
import com.whidy.whidyandroid.utils.ItemVerticalDecoration
import timber.log.Timber

class PlaceInfoFragment: Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentPlaceInfoBinding? = null
    private val binding: FragmentPlaceInfoBinding
        get() = requireNotNull(_binding){"FragmentPlaceInfoBinding -> null"}

    private lateinit var placeReviewCommentAdapter: PlaceReviewCommentAdapter
    private lateinit var placeTimeAdapter: PlaceTimeAdapter

    private var isExpanded = false

    private val mapViewModel: MapViewModel by activityViewModels()
    private val scrapViewModel: ScrapViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
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

        mapViewModel.placeDetail.observe(viewLifecycleOwner) { place ->
            binding.tvPlaceName.text = place.name
            binding.tvPlaceInfoAddress.text = place.address
            binding.tvPlacePrice.text = "${place.beveragePrice}원"
            binding.tvPlaceScore.text = place.reviewScore.toString()
            binding.tvPlaceReview.text = "후기 ${place.reviewNum}개"

            val placeTypeText = PlaceType.entries.find { it.name == place.placeType }?.displayName ?: place.placeType
            binding.tvPlaceType.text = placeTypeText

            binding.tvPlaceInfoScore.text = place.reviewScore.toString()
            binding.tvPlaceInfoReviewAmount.text = "(${place.reviewNum})"

            val isScrapped = scrapViewModel.isScrapped(place.id)
            binding.btnScrap.isSelected = isScrapped

            binding.btnScrap.setOnClickListener {
                if (!binding.btnScrap.isSelected) {
                    scrapViewModel.setScrap(place.id)
                } else {
                    // scrapViewModel.deleteScrap(place.id)
                }
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

            setImages(place.images.ifEmpty { getDummyImages() })

            val dayOrder = listOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY")
            val placeTimeData = place.businessHours
                .sortedBy { dayOrder.indexOf(it.dayOfWeek) }
                .map { businessHour ->
                    val openTimeFormatted = businessHour.openTime?.substring(0, 5)
                    val closeTimeFormatted = businessHour.closeTime?.substring(0, 5)
                    val timeText = if (openTimeFormatted != null && closeTimeFormatted != null) {
                        "$openTimeFormatted ~ $closeTimeFormatted"
                    } else {
                        "휴무"
                    }
                    val dayKorean = when(businessHour.dayOfWeek) {
                        "MONDAY" -> "월"
                        "TUESDAY" -> "화"
                        "WEDNESDAY" -> "수"
                        "THURSDAY" -> "목"
                        "FRIDAY" -> "금"
                        "SATURDAY" -> "토"
                        "SUNDAY" -> "일"
                        else -> businessHour.dayOfWeek
                    }
                    PlaceTime(
                        day = dayKorean,
                        hours = timeText
                    )
                }

            placeTimeAdapter = PlaceTimeAdapter(placeTimeData)
            binding.rvPlaceInfoTime.adapter = placeTimeAdapter

            binding.btnShare.setOnClickListener {
                val defaultFeed = FeedTemplate(
                    content = Content(
                        title = place.address,
                        description = "장소의 자세한 정보를 확인해보세요",

                        imageUrl = place.images[0],
                        link = Link(
                            webUrl = "https://play.google.com/store/apps/details?id=com.whidy.whidyandroid",
                            mobileWebUrl = "https://play.google.com/store/apps/details?id=com.whidy.whidyandroid"
                        )
                    ),
                    itemContent = ItemContent(
                        profileText = place.name
                    ),
                    buttons = listOf(
                        Button(
                            "앱 다운로드",
                            Link(
                                webUrl = "https://play.google.com/store/apps/details?id=com.whidy.whidyandroid",
                                mobileWebUrl = "https://play.google.com/store/apps/details?id=com.whidy.whidyandroid"
                            )
                        ),
                        Button(
                            "앱으로 이동",
                            Link(
                                //바로 앱으로 이동하게 해주는 딥링크
                                androidExecutionParams = mapOf("screen" to "main", "placeId" to place.id.toString(), "placeType" to place.placeType)
                            )
                        )
                    )
                )

                val isKakaoTalkAvailable = ShareClient.instance.isKakaoTalkSharingAvailable(requireContext())

                // 카카오톡 설치여부 확인
                if (isKakaoTalkAvailable) {
                    // 카카오톡으로 공유
                    ShareClient.instance.shareDefault(requireContext(), defaultFeed) { sharingResult, error ->
                        if (error != null) {
                            Timber.e(error, "카카오톡 공유 실패")
                        } else if (sharingResult != null) {
                            Timber.d("카카오톡 공유 성공 " + sharingResult.intent)
                            startActivity(sharingResult.intent)

                        }
                    }
                } else {
                    // 웹으로 공유
                    val sharerUrl = WebSharerClient.instance.makeDefaultUrl(defaultFeed)
                    try {
                        KakaoCustomTabsClient.openWithDefault(requireContext(), sharerUrl)
                    } catch (e: UnsupportedOperationException) {
                        Timber.e(e, "CustomTabs 지원 브라우저가 없습니다.")
                        // CustomTabs 지원 브라우저가 없을 때, 기본 웹 브라우저로 열기
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl.toString()))
                            startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            Timber.e(e, "인터넷 브라우저가 없습니다.")
                        }
                    }
                }
            }

        }

        binding.ivPlaceImage3.setOnClickListener {
            navController.navigate(R.id.action_navigation_place_info_to_photo)
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