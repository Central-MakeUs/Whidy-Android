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
import com.whidy.whidyandroid.model.ItemType
import com.whidy.whidyandroid.model.PlaceType
import com.whidy.whidyandroid.presentation.base.MainActivity
import com.whidy.whidyandroid.presentation.map.home.MapViewModel
import com.whidy.whidyandroid.presentation.map.review.PlaceReviewCommentAdapter
import com.whidy.whidyandroid.presentation.map.review.PlaceReviewTag
import com.whidy.whidyandroid.presentation.map.review.PlaceReviewTagAdapter
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

        scrapViewModel.fetchScrapItems()
        mapViewModel.fetchMyReviews()

        mapViewModel.placeDetail.observe(viewLifecycleOwner) { place ->
            binding.tvPlaceName.text = place.name
            binding.tvPlaceType.text = PlaceType.fromString(place.placeType)
            binding.tvPlaceInfoAddress.text = place.address
            binding.tvPlacePrice.text = "${place.beveragePrice}원"
            binding.tvPlaceScore.text = (place.reviewScore ?: 0.0).toString()
            binding.tvPlaceReview.text = "후기 ${place.reviewNum}개"
            binding.tvPlaceInfoScore.text = (place.reviewScore ?: 0.0).toString()
            binding.tvPlaceInfoReviewAmount.text = "(${place.reviewNum})"

            updateScrapStatus(place.id)
            mapViewModel.fetchReviews(place.id)

            binding.btnScrap.setOnClickListener {
                if (!binding.btnScrap.isSelected) {
                    scrapViewModel.setScrap(place.id)
                } else {
                    val scrapItem = scrapViewModel.scrapItems.value?.find { it.placeId == place.id }
                    if (scrapItem != null) {
                        scrapViewModel.deleteScrap(scrapItem.scrapId)
                    }
                }
            }

            fun setImages(imageUrls: List<String>) {
                val imageViews = listOf(binding.ivPlaceImage1, binding.ivPlaceImage2, binding.ivPlaceImage3)
                val overlayView = binding.tvOverlay

                // 이미지가 없으면 모든 이미지뷰와 오버레이 숨김
                if (imageUrls.isEmpty()) {
                    imageViews.forEach { it.visibility = View.GONE }
                    overlayView.visibility = View.GONE
                    return
                }

                // 이미지가 있다면 기존 로직대로 처리
                for (i in imageViews.indices) {
                    if (i < imageUrls.size) {
                        Glide.with(this)
                            .load(imageUrls[i])
                            .into(imageViews[i])
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

            setImages(place.images)

            // 원래 요일 순서
            val dayOrder = listOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY")

            val calendar = java.util.Calendar.getInstance()
            val currentDayOfWeek = when(calendar.get(java.util.Calendar.DAY_OF_WEEK)) {
                java.util.Calendar.MONDAY -> "MONDAY"
                java.util.Calendar.TUESDAY -> "TUESDAY"
                java.util.Calendar.WEDNESDAY -> "WEDNESDAY"
                java.util.Calendar.THURSDAY -> "THURSDAY"
                java.util.Calendar.FRIDAY -> "FRIDAY"
                java.util.Calendar.SATURDAY -> "SATURDAY"
                java.util.Calendar.SUNDAY -> "SUNDAY"
                else -> ""
            }

            // 현재 요일부터 시작하는 순서로 dayOrder 회전
            val currentIndex = dayOrder.indexOf(currentDayOfWeek)
            val rotatedDayOrder = dayOrder.subList(currentIndex, dayOrder.size) + dayOrder.subList(0, currentIndex)

            // 회전된 순서로 영업시간 데이터 정렬 및 변환
            val placeTimeData = place.businessHours
                .sortedBy { rotatedDayOrder.indexOf(it.dayOfWeek) }
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

            val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
            val currentTimeStr = sdf.format(calendar.time)
            val currentTime = sdf.parse(currentTimeStr)

            // 현재 요일에 해당하는 영업시간 찾기
            val currentBusinessHour = place.businessHours.find { it.dayOfWeek == currentDayOfWeek }
            val statusText = if (currentBusinessHour?.openTime != null &&
                currentBusinessHour.closeTime != null) {

                val openTimeStr = currentBusinessHour.openTime.substring(0, 5)
                val closeTimeStr = currentBusinessHour.closeTime.substring(0, 5)
                val openTime = sdf.parse(openTimeStr)
                val closeTime = sdf.parse(closeTimeStr)

                binding.tvPlaceTime.text = "${closeTimeStr} 까지"

                if (currentTime.after(openTime) && currentTime.before(closeTime)) "영업중" else "영업종료"
            } else {
                "휴무"
            }
            binding.tvPlaceInfoOpen.text = statusText
            binding.tvPlaceOpen.text = statusText

            if (statusText == "영업종료") {
                binding.tvPlaceInfoOpen.setTextColor(ContextCompat.getColor(requireContext(), R.color.R400))
                binding.tvPlaceOpen.setTextColor(ContextCompat.getColor(requireContext(), R.color.R400))
            } else {
                binding.tvPlaceInfoOpen.setTextColor(ContextCompat.getColor(requireContext(), R.color.G900))
                binding.tvPlaceOpen.setTextColor(ContextCompat.getColor(requireContext(), R.color.G900))
            }

            binding.btnShare.setOnClickListener {
                val imageUrl = if (place.images.isNotEmpty()) place.images[0] else ""

                val defaultFeed = FeedTemplate(
                    content = Content(
                        title = place.address,
                        description = "장소의 자세한 정보를 확인해보세요",
                        imageUrl = imageUrl,
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
                            try {
                                startActivity(sharingResult.intent)
                            } catch (e: ActivityNotFoundException) {
                                // 앱이 설치되어 있지 않으므로 앱 다운로드 링크로 리다이렉트
                                val downloadIntent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id=com.whidy.whidyandroid")
                                )
                                startActivity(downloadIntent)
                            }
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

        scrapViewModel.scrapItems.observe(viewLifecycleOwner) { scrapItems ->
            mapViewModel.placeDetail.value?.let { place ->
                updateScrapStatus(place.id)
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

        binding.btnPlaceReviewAll.setOnClickListener {
            navController.navigate(R.id.action_navigation_place_info_to_review_all)
        }

        binding.clPlaceReviewStars.setOnClickListener {
            navController.navigate(R.id.action_navigation_place_info_to_review_write)
        }
    }

    private fun updateScrapStatus(placeId: Int) {
        val isScrapped = scrapViewModel.isScrapped(placeId)
        binding.btnScrap.isSelected = isScrapped
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}