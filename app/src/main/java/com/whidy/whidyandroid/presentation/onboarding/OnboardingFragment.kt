package com.whidy.whidyandroid.presentation.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.viewpager2.widget.ViewPager2
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.databinding.FragmentOnboardingBinding
import com.whidy.whidyandroid.presentation.base.MainActivity

class OnboardingFragment : Fragment() {

    private lateinit var navController: NavController
    private var _binding: FragmentOnboardingBinding? = null

    private val binding: FragmentOnboardingBinding
        get() = requireNotNull(_binding){"FragmentMyReviewBinding -> null"}
    private lateinit var onboardingAdapter: OnboardingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        (requireActivity() as MainActivity).hideBottomNavigation(true)

        val pages = listOf(
            OnboardingPage(R.drawable.ic_onboarding_1, R.color.R300, getString(R.string.onboarding_1_title), getString(R.string.onboarding_1_subtitle)),
            OnboardingPage(R.drawable.ic_onboarding_2, R.color.Y300, getString(R.string.onboarding_2_title), getString(R.string.onboarding_2_subtitle)),
            OnboardingPage(R.drawable.ic_onboarding_3, R.color.B300, getString(R.string.onboarding_3_title), getString(R.string.onboarding_3_subtitle))
        )

        onboardingAdapter = OnboardingAdapter(pages)
        binding.viewPager.adapter = onboardingAdapter
        binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        binding.dotsIndicator.setViewPager2(binding.viewPager)

        // 페이지 변경 시 StatusBar 색상 변경
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateStatusBarColor(position)
            }
        })

        binding.btnStart.setOnClickListener {
            navController.navigate(R.id.action_navigation_onboarding_to_map)
        }
    }

    private fun updateStatusBarColor(position: Int) {
        val window = requireActivity().window
        val statusBarColors = listOf(
            R.color.R300,  // 첫 번째 페이지 색상
            R.color.Y300,  // 두 번째 페이지 색상
            R.color.B300   // 세 번째 페이지 색상
        )
        window.statusBarColor = ContextCompat.getColor(requireContext(), statusBarColors[position])
    }

    private fun restoreDefaultStatusBarColor() {
        val window = requireActivity().window
        val defaultColor = ContextCompat.getColor(requireContext(), R.color.White) // 기본 색상
        window.statusBarColor = defaultColor
    }

    override fun onDestroyView() {
        super.onDestroyView()
        restoreDefaultStatusBarColor()
        _binding = null
    }
}