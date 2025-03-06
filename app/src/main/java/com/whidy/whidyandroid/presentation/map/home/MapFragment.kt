package com.whidy.whidyandroid.presentation.map.home

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.data.place.GetPlaceResponse
import com.whidy.whidyandroid.databinding.FragmentMapBinding
import com.whidy.whidyandroid.model.PlaceType
import com.whidy.whidyandroid.presentation.base.MainActivity
import com.whidy.whidyandroid.presentation.map.add.PlaceAddDialog
import com.whidy.whidyandroid.presentation.map.info.PlaceInfoPopup
import com.whidy.whidyandroid.presentation.scrap.ScrapViewModel
import com.whidy.whidyandroid.utils.ItemHorizontalDecoration
import timber.log.Timber

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var navController: NavController
    private var _binding: FragmentMapBinding? = null
    private val binding: FragmentMapBinding
        get() = requireNotNull(_binding) { "FragmentMapBinding -> null" }

    private lateinit var naverMap: NaverMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationSource: FusedLocationSource

    private lateinit var placeTagAdapter: PlaceTagAdapter

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private val mapViewModel: MapViewModel by activityViewModels()
    private val scrapViewModel: ScrapViewModel by activityViewModels()

    private var currentMarker: Marker? = null
    private var selectedCategoryPosition: Int = 0
    private val multiMarkers = mutableListOf<Marker>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        if (!hasPermission()) {
            requestLocationPermission()
        } else {
            initMapView()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        (requireActivity() as MainActivity).hideBottomNavigation(false)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })

        arguments?.let { bundle ->
            if (bundle.getBoolean("showPlaceAddSuccessDialog", false)) {
                val dialog = PlaceAddDialog(requireContext())
                dialog.show()
            }
        }

        scrapViewModel.fetchScrapItems()

        mapViewModel.placeDetail.observe(viewLifecycleOwner) { place ->
            Timber.d("placeDetail: $place")
            binding.tvPlaceName.text = place.name
            binding.tvPlaceType.text = PlaceType.fromString(place.placeType)
            binding.tvPlaceAddress.text = place.address
            binding.tvPlacePrice.text = "${place.beveragePrice}원"
            binding.tvPlaceScore.text = (place.reviewScore ?: 0.0).toString()
            binding.tvPlaceReview.text = "후기 ${place.reviewNum}개"

            updateScrapStatus(place.id)

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

            binding.apply {
                if (place.images.isNotEmpty()) {
                    Glide.with(ivPlaceImage.context)
                        .load(place.images[0])
                        .into(ivPlaceImage)
                } else {
                    ivPlaceImage.visibility = View.GONE
                }
            }

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
            binding.tvPlaceOpen.text = statusText

            if (statusText == "영업종료") {
                binding.tvPlaceOpen.setTextColor(ContextCompat.getColor(requireContext(), R.color.R400))
            } else {
                binding.tvPlaceOpen.setTextColor(ContextCompat.getColor(requireContext(), R.color.G900))
            }
        }

        scrapViewModel.scrapItems.observe(viewLifecycleOwner) { scrapItems ->
            mapViewModel.placeDetail.value?.let { place ->
                updateScrapStatus(place.id)
            }
        }

        placeTagAdapter = PlaceTagAdapter(getDummyData())
        binding.rvPlaceTag.apply {
            adapter = placeTagAdapter
            val itemSpace = resources.getDimensionPixelSize(R.dimen.place_tag)
            val firstMargin = resources.getDimensionPixelSize(R.dimen.place_tag_first_margin)
            addItemDecoration(ItemHorizontalDecoration(itemSpace, firstMargin))
        }

        mapViewModel.fetchPlaceList("")

        placeTagAdapter.onItemClick = { position, _ ->
            selectedCategoryPosition = position
            val center = naverMap.cameraPosition.target
            mapViewModel.fetchPlaceList("", center.latitude, center.longitude)
            Timber.d("${center.latitude}, ${center.longitude}")
            when (position) {
                0 -> mapViewModel.searchFreeStudySpaceResults.value?.let { addMarkersWithInfo(it, naverMap, requireContext()) }
                1 -> mapViewModel.searchFranchiseCafeResults.value?.let { addMarkersWithInfo(it, naverMap, requireContext()) }
                2 -> mapViewModel.searchGeneralCafeResults.value?.let { addMarkersWithInfo(it, naverMap, requireContext()) }
                3 -> mapViewModel.searchStudyCafeResults.value?.let { addMarkersWithInfo(it, naverMap, requireContext()) }
                4 -> mapViewModel.searchFreeClothesRentalResults.value?.let { addMarkersWithInfo(it, naverMap, requireContext()) }
                5 -> mapViewModel.searchFreePictureResults.value?.let { addMarkersWithInfo(it, naverMap, requireContext()) }
            }
        }

        binding.tvSearch.setOnClickListener {
            binding.tvSearch.setBackgroundResource(R.drawable.bg_search_bar_clicked)
            navController.navigate(R.id.action_navigation_map_to_place_search)
        }

        binding.btnReSearch.setOnClickListener {
            hideReSearchButton()
            val center = naverMap.cameraPosition.target
            val currentZoom = naverMap.cameraPosition.zoom
            mapViewModel.fetchPlaceList("", center.latitude, center.longitude)

            Timber.d("${center.latitude}, ${center.longitude}")
            when (selectedCategoryPosition) {
                0 -> mapViewModel.searchFreeStudySpaceResults.value?.let { addMarkersWithInfo(it, naverMap, requireContext()) }
                1 -> mapViewModel.searchFranchiseCafeResults.value?.let { addMarkersWithInfo(it, naverMap, requireContext()) }
                2 -> mapViewModel.searchGeneralCafeResults.value?.let { addMarkersWithInfo(it, naverMap, requireContext()) }
                3 -> mapViewModel.searchStudyCafeResults.value?.let { addMarkersWithInfo(it, naverMap, requireContext()) }
                4 -> mapViewModel.searchFreeClothesRentalResults.value?.let { addMarkersWithInfo(it, naverMap, requireContext()) }
                5 -> mapViewModel.searchFreePictureResults.value?.let { addMarkersWithInfo(it, naverMap, requireContext()) }
            }
        }

        binding.btnFilter.setOnClickListener {
            navController.navigate(R.id.action_navigation_map_to_place_filter)
        }

        binding.floatingActionButton.setOnClickListener{
            navController.navigate(R.id.action_navigation_map_to_place_add)
        }

        val bottomSheet: ConstraintLayout = binding.homeBottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        binding.root.post {
            val targetView = binding.clAppBar
            val targetY = targetView.y.toInt()
            val screenHeight = resources.displayMetrics.heightPixels
            val maxHeight = screenHeight - targetY

            bottomSheetBehavior.maxHeight = maxHeight
        }

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.peekHeight = resources.getDimensionPixelSize(R.dimen.bottom_sheet_peek_height)

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    // 최대로 올렸을 때 Fragment 전환
                    navController.navigate(R.id.action_navigation_map_to_place_info)
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        binding.homeBottomSheet.apply {
            setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                        binding.touchBar.setBackgroundResource(R.drawable.bg_touch_bar_clicked)
                        true
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        binding.touchBar.setBackgroundResource(R.drawable.bg_touch_bar)
                        v.performClick()
                        true
                    }
                    else -> false
                }
            }

            setOnClickListener {}
        }

        binding.btnCancel.setOnClickListener {
            binding.btnFilter.visibility = View.VISIBLE
            binding.rvPlaceTag.visibility = View.VISIBLE
            binding.btnCancel.visibility = View.GONE
            binding.homeBottomSheet.visibility = View.GONE

            currentMarker?.map = null
            currentMarker = null

            clearMarkers()
            mapViewModel.clearLocation()

            mapViewModel.selectedLocation.observe(viewLifecycleOwner) { latLng ->
                if (latLng == null) {
                    Timber.d("latLng is null")
                } else {
                    val cameraUpdate = CameraUpdate.scrollTo(latLng)
                    naverMap.moveCamera(cameraUpdate)
                }
            }
        }

        binding.btnPopup.setOnClickListener {
            PlaceInfoPopup(
                context = requireContext(),
                anchorView = binding.btnPopup
            )
        }

        binding.btnScrap.setOnClickListener {
            it.isSelected = !it.isSelected
        }
    }

    private fun updateScrapStatus(placeId: Int) {
        val isScrapped = scrapViewModel.isScrapped(placeId)
        binding.btnScrap.isSelected = isScrapped
    }

    private fun hasPermission(): Boolean {
        for (permission in PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    private fun requestLocationPermission() {
        requestPermissions(PERMISSIONS, REQUEST_CODE_LOCATION_PERMISSION)
    }

    private fun initMapView() {
        locationSource = FusedLocationSource(this, REQUEST_CODE_LOCATION_PERMISSION)

        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_fragment) as com.naver.maps.map.MapFragment?
            ?: com.naver.maps.map.MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_fragment, it).commit()
            }

        mapFragment.getMapAsync(this)
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "위치 권한이 허용되었습니다", Toast.LENGTH_SHORT).show()
                initMapView()
            } else {
                Toast.makeText(requireContext(), "위치 권한이 거부되었습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addMarkersWithInfo(places: List<GetPlaceResponse>, naverMap: NaverMap, context: Context) {
        // 기존 마커 제거
        multiMarkers.forEach { it.map = null }
        multiMarkers.clear()
        Timber.d("places: $places")
        Timber.d("$multiMarkers")

        places.forEach { place ->
            val position = LatLng(place.latitude, place.longitude)
            val marker = Marker().apply {
                this.position = position
                this.icon = OverlayImage.fromResource(mapViewModel.getMarkerIcon(place.placeType))
                captionText = place.name
                captionTextSize = 17F
                captionColor = Color.BLACK
                captionHaloColor = Color.TRANSPARENT
                tag = place  // 마커에 장소 정보 저장
                this.map = naverMap
            }
            marker.setOnClickListener { clickedMarker ->
                val selectedPlace = clickedMarker.tag as? GetPlaceResponse
                selectedPlace?.let { place ->
                    when (place.placeType) {
                        "GENERAL_CAFE" -> mapViewModel.fetchPlaceGeneralCafe(place.id)
                        "STUDY_CAFE" -> mapViewModel.fetchPlaceStudyCafe(place.id)
                        "FREE_STUDY_SPACE" -> mapViewModel.fetchPlaceFreeStudy(place.id)
                        "FREE_CLOTHES_RENTAL" -> mapViewModel.fetchPlaceFreeClothes(place.id)
                        "FREE_PICTURE" -> mapViewModel.fetchPlaceFreePicture(place.id)
                        "FRANCHISE_CAFE" -> mapViewModel.fetchPlaceFranchiseCafe(place.id)
                        else -> {
                            Toast.makeText(context, "알 수 없는 장소 타입", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener false
                        }
                    }
                    navController.navigate(R.id.navigation_place_info)
                }
                true  // 이벤트 소비
            }
            multiMarkers.add(marker)
        }

        // 모든 마커를 포함하도록 카메라 이동
        val coordinates = places.map { LatLng(it.latitude, it.longitude) }
        Timber.d("coordinates: $coordinates")
        moveCameraToMarkers(coordinates, naverMap)
    }

    private fun moveCameraToMarkers(coordinates: List<LatLng>, naverMap: NaverMap) {
        if (coordinates.isEmpty()) return

        // 좌표들의 경계 영역 계산
        val builder = LatLngBounds.Builder()
        coordinates.forEach { builder.include(it) }
        val bounds = builder.build()

        // 패딩을 주어 카메라 이동 (패딩 값은 필요에 따라 조정)
        val padding = 100
        val cameraUpdate = CameraUpdate.fitBounds(bounds, padding)
        naverMap.moveCamera(cameraUpdate)
    }

    // 취소 버튼 클릭 시 단일 마커와 다중 마커 모두 제거
    private fun clearMarkers() {
        currentMarker?.map = null
        currentMarker = null
        multiMarkers.forEach { it.map = null }
        multiMarkers.clear()
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        mapViewModel.setNaverMap(naverMap)

        naverMap.addOnCameraIdleListener {
            showReSearchButton()
        }

        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow

        naverMap.uiSettings.logoGravity = Gravity.END and Gravity.TOP
        naverMap.uiSettings.setLogoMargin(30, 350, 0, 0)

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        binding.btnGps.setOnClickListener {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        naverMap.locationOverlay.run {
                            isVisible = true
                            position = LatLng(it.latitude, it.longitude)
                        }
                        val cameraUpdate = CameraUpdate.scrollTo(LatLng(it.latitude, it.longitude))
                        naverMap.moveCamera(cameraUpdate)
                    }
                }
        }

        mapViewModel.selectedLocation.observe(viewLifecycleOwner) { latLng ->
            if (latLng == null) {
                currentMarker?.map = null
                currentMarker = null

                binding.btnFilter.visibility = View.VISIBLE
                binding.btnSearch.visibility = View.VISIBLE
                binding.rvPlaceTag.visibility = View.VISIBLE
                binding.btnCancel.visibility = View.GONE
                binding.homeBottomSheet.visibility = View.GONE
            } else {
                currentMarker?.map = null

                mapViewModel.placeDetail.observe(viewLifecycleOwner) { place ->
                    currentMarker = Marker().apply {
                        position = LatLng(place.latitude, place.longitude)
                        icon = OverlayImage.fromResource(mapViewModel.getMarkerIcon(place.placeType))
                        map = naverMap
                    }
                }

                val cameraUpdate = CameraUpdate.scrollTo(latLng)
                naverMap.moveCamera(cameraUpdate)

                binding.btnFilter.visibility = View.GONE
                binding.btnSearch.visibility = View.GONE
                binding.rvPlaceTag.visibility = View.GONE
                binding.btnCancel.visibility = View.VISIBLE
                binding.homeBottomSheet.visibility = View.VISIBLE
            }
        }

        mapViewModel.searchFreeStudySpaceResults.observe(viewLifecycleOwner) { data ->
            if (selectedCategoryPosition == 0) {
                if (data != null) {
                    addMarkersWithInfo(data, naverMap, requireContext())
                    moveCameraToMarkers(data.map { LatLng(it.latitude, it.longitude) }, naverMap)
                }
            }
        }
        mapViewModel.searchFranchiseCafeResults.observe(viewLifecycleOwner) { data ->
            if (selectedCategoryPosition == 1) {
                if (data != null) {
                    addMarkersWithInfo(data, naverMap, requireContext())
                    moveCameraToMarkers(data.map { LatLng(it.latitude, it.longitude) }, naverMap)
                }
            }
        }
        mapViewModel.searchGeneralCafeResults.observe(viewLifecycleOwner) { data ->
            if (selectedCategoryPosition == 2) {
                if (data != null) {
                    addMarkersWithInfo(data, naverMap, requireContext())
                    moveCameraToMarkers(data.map { LatLng(it.latitude, it.longitude) }, naverMap)
                }
            }
        }
        mapViewModel.searchStudyCafeResults.observe(viewLifecycleOwner) { data ->
            if (selectedCategoryPosition == 3) {
                if (data != null) {
                    addMarkersWithInfo(data, naverMap, requireContext())
                    moveCameraToMarkers(data.map { LatLng(it.latitude, it.longitude) }, naverMap)
                }
            }
        }
        mapViewModel.searchFreeClothesRentalResults.observe(viewLifecycleOwner) { data ->
            if (selectedCategoryPosition == 4) {
                if (data != null) {
                    addMarkersWithInfo(data, naverMap, requireContext())
                    moveCameraToMarkers(data.map { LatLng(it.latitude, it.longitude) }, naverMap)
                }
            }
        }
        mapViewModel.searchFreePictureResults.observe(viewLifecycleOwner) { data ->
            if (selectedCategoryPosition == 5) {
                if (data != null) {
                    addMarkersWithInfo(data, naverMap, requireContext())
                    moveCameraToMarkers(data.map { LatLng(it.latitude, it.longitude) }, naverMap)
                }
            }
        }
    }

    private fun showReSearchButton() {
        binding.btnReSearch.visibility = View.VISIBLE
        val slideInAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_top)
        binding.btnReSearch.startAnimation(slideInAnim)
    }

    private fun hideReSearchButton() {
        val slideOutAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out_top)
        slideOutAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                binding.btnReSearch.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
        binding.btnReSearch.startAnimation(slideOutAnim)
    }

    companion object {
        private const val REQUEST_CODE_LOCATION_PERMISSION = 1
        private val PERMISSIONS = arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    private fun getDummyData(): List<String> {
        return listOf("무료 공부 공간", "프랜차이즈 카페", "개인 카페", "스터디 카페", "정장 대여", "면접 사진")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}