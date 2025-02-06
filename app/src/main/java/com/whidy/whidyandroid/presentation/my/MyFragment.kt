package com.whidy.whidyandroid.presentation.my

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.databinding.FragmentMyBinding
import com.whidy.whidyandroid.presentation.base.MainActivity
import timber.log.Timber

class MyFragment : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentMyBinding? = null
    private val binding: FragmentMyBinding
        get() = requireNotNull(_binding){"FragmentHomeBinding -> null"}

    private val myViewModel: MyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMyBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        (requireActivity() as MainActivity).hideBottomNavigation(false)

        binding.btnProfileEdit.setOnClickListener {
            navController.navigate(R.id.action_navigation_my_to_profile_edit)
        }

        binding.clMyReview.setOnClickListener {
            navController.navigate(R.id.action_navigation_my_to_my_review)
        }

        myViewModel.nickname.observe(viewLifecycleOwner) { nickname ->
            binding.tvUserNickname.text = nickname
            Timber.d("nickname: $nickname")
        }

        myViewModel.profileImage.observe(viewLifecycleOwner) { imageRes ->
            binding.ivUserProfile.setImageResource(imageRes)
            Timber.d("imageRes: $imageRes")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}