package com.whidy.whidyandroid.presentation.base

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.databinding.ActivityMainBinding
import com.whidy.whidyandroid.presentation.map.home.MapViewModel
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.plant(Timber.DebugTree())

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment

        val navController = navHostFragment.navController

        // BottomNavigationView 설정
        binding.bottomNavi.setupWithNavController(navController)

        // BottomNavigationView 아이템 선택 리스너 설정
        binding.bottomNavi.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_map -> {
                    navController.navigate(R.id.navigation_map)
                    true
                }
                R.id.navigation_scrap -> {
                    navController.navigate(R.id.navigation_scrap)
                    true
                }
                R.id.navigation_my -> {
                    navController.navigate(R.id.navigation_my)
                    true
                }
                else -> false
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent) {
        intent.data?.let { uri ->
            val placeId = uri.getQueryParameter("placeId")
            val placeType = uri.getQueryParameter("placeType")
            if (!placeId.isNullOrEmpty() && !placeType.isNullOrEmpty()) {
                when (placeType) {
                    "GENERAL_CAFE" -> viewModel.fetchPlaceGeneralCafe(placeId.toInt())
                    "STUDY_CAFE" -> viewModel.fetchPlaceStudyCafe(placeId.toInt())
                    "FREE_STUDY_SPACE" -> viewModel.fetchPlaceFreeStudy(placeId.toInt())
                    "FREE_PICTURE" -> viewModel.fetchPlaceFreePicture(placeId.toInt())
                    "FREE_CLOTHES_RENTAL" -> viewModel.fetchPlaceFreeClothes(placeId.toInt())
                    "FRANCHISE_CAFE" -> viewModel.fetchPlaceFranchiseCafe(placeId.toInt())
                    else -> Timber.e("Unknown placeType: $placeType")
                }
            }
            viewModel.placeDetail.observe(this) {
                findNavController(R.id.fragment_container).navigate(R.id.navigation_place_info)
            }
        }
    }

    fun hideBottomNavigation(state:Boolean){
        if(state) binding.bottomNavi.visibility = View.GONE else binding.bottomNavi.visibility=
            View.VISIBLE
    }
}