package com.whidy.whidyandroid.presentation.map.home

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
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
import com.whidy.whidyandroid.databinding.FragmentMapBinding
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

        navController.currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Boolean>("showPlaceAddSuccessDialog")
            ?.observe(viewLifecycleOwner) { showDialog ->
                if (showDialog == true) {
                    val dialog = PlaceAddDialog(
                        context = requireContext()
                    )
                    dialog.show()
                    navController.currentBackStackEntry?.savedStateHandle?.remove<Boolean>("showPlaceAddSuccessDialog")
                }
            }

        mapViewModel.placeDetail.observe(viewLifecycleOwner) { place ->
            binding.tvPlaceName.text = place.name
            binding.tvPlaceAddress.text = place.address
            binding.tvPlacePrice.text = "${place.beveragePrice}원"
            binding.tvPlaceScore.text = place.reviewScore.toString()
            binding.tvPlaceReview.text = "후기 ${place.reviewNum}개"

            val isScrapped = scrapViewModel.isScrapped(place.id)
            binding.btnScrap.isSelected = isScrapped

            binding.btnScrap.setOnClickListener {
                if (!binding.btnScrap.isSelected) {
                    scrapViewModel.setScrap(place.id)
                } else {
                    scrapViewModel.deleteScrap(place.id)
                }
            }

            binding.apply {
                Glide.with(ivPlaceImage.context)
                    .load(place.images[0])
                    .into(ivPlaceImage)
            }
        }

        placeTagAdapter = PlaceTagAdapter(getDummyData())
        binding.rvPlaceTag.apply {
            adapter = placeTagAdapter
            val itemSpace = resources.getDimensionPixelSize(R.dimen.place_tag)
            addItemDecoration(ItemHorizontalDecoration(itemSpace))
        }

        placeTagAdapter.onItemClick = { position, tag ->
            if (position == 2) {  // 0부터 시작하므로 3번째 아이템은 인덱스 2
                addMultiMarkers()
            }
        }

        binding.tvSearch.setOnClickListener {
            binding.tvSearch.setBackgroundResource(R.drawable.bg_search_bar_clicked)
            navController.navigate(R.id.action_navigation_map_to_place_search)
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
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_fragment) as com.naver.maps.map.MapFragment?
            ?: com.naver.maps.map.MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_fragment, it).commit()
            }

        mapFragment.getMapAsync(this)
        locationSource = FusedLocationSource(this, REQUEST_CODE_LOCATION_PERMISSION)
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initMapView()
            } else {
                Toast.makeText(requireContext(), "Permission denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 다중 마커 추가 함수 (예시 좌표 3개)
    private fun addMultiMarkers() {
        // 기존 다중 마커가 있다면 지도에서 제거
        multiMarkers.forEach { it.map = null }
        multiMarkers.clear()

        val coordinates = listOf(
            LatLng(37.541113416270406, 127.05062406417724),
            LatLng(37.54611341627041,  127.05362406417724),
            LatLng(37.53711341627041, 127.05762406417723)
        )

        coordinates.forEach { latLng ->
            val marker = Marker().apply {
                position = latLng
                icon = OverlayImage.fromResource(R.drawable.ic_marker_general_cafe)
                map = naverMap
            }
            multiMarkers.add(marker)
        }

        moveCameraToMarkers(coordinates)
    }

    private fun moveCameraToMarkers(coordinates: List<LatLng>) {
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