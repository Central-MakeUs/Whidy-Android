package com.whidy.whidyandroid.presentation.map

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.databinding.FragmentMapBinding
import com.whidy.whidyandroid.utils.PlaceTagItemDecoration

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

    private var currentMarker: Marker? = null

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

        placeTagAdapter = PlaceTagAdapter(getDummyData())
        binding.rvPlaceTag.apply {
            adapter = placeTagAdapter
            val itemSpace = resources.getDimensionPixelSize(R.dimen.place_tag)
            addItemDecoration(PlaceTagItemDecoration(itemSpace))
        }

        binding.tvSearch.setOnClickListener {
            binding.tvSearch.setBackgroundResource(R.drawable.bg_search_bar_clicked)
            navController.navigate(R.id.action_navigation_map_to_place_search)
        }

        binding.btnFilter.setOnClickListener {
            navController.navigate(R.id.action_navigation_map_to_place_filter)
        }

        val bottomSheet: ConstraintLayout = binding.homeBottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.peekHeight = resources.getDimensionPixelSize(R.dimen.bottom_sheet_peek_height)

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

            mapViewModel.clearLocation()
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

                currentMarker = Marker().apply {
                    position = latLng
                    icon = OverlayImage.fromResource(R.drawable.ic_marker)
                    map = naverMap
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
        return listOf("무료 공간", "프랜차이즈 카페", "개인 카페", "스터디 카페")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}