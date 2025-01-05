package com.whidy.whidyandroid.presentation.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.databinding.ActivityMainBinding
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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
    }

    fun hideBottomNavigation(state:Boolean){
        if(state) binding.bottomNavi.visibility = View.GONE else binding.bottomNavi.visibility=
            View.VISIBLE
    }
}