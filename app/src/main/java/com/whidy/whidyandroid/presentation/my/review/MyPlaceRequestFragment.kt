package com.whidy.whidyandroid.presentation.my.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.whidy.whidyandroid.databinding.FragmentMyPlaceRequestBinding
import com.whidy.whidyandroid.presentation.base.MainActivity
import com.whidy.whidyandroid.presentation.map.home.MapViewModel
import com.whidy.whidyandroid.presentation.my.MyViewModel

class MyPlaceRequestFragment : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentMyPlaceRequestBinding? = null
    private val binding: FragmentMyPlaceRequestBinding
        get() = requireNotNull(_binding){"FragmentMyPlaceRequestBinding -> null"}

    private val mapViewModel: MapViewModel by activityViewModels()
    private val viewModel: MyViewModel by activityViewModels()

    private lateinit var myPlaceRequestAdapter: MyPlaceRequestAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMyPlaceRequestBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        (requireActivity() as MainActivity).hideBottomNavigation(true)

        myPlaceRequestAdapter = MyPlaceRequestAdapter(emptyList())
        binding.rvMyPlaceRequest.adapter = myPlaceRequestAdapter

        mapViewModel.fetchMyReviews()
        viewModel.fetchMyPlaceRequests()

        binding.btnBack.setOnClickListener {
            navController.popBackStack()
        }

        viewModel.myPlaceRequests.observe(viewLifecycleOwner) { myPlaceRequests ->
            myPlaceRequestAdapter.updateData(myPlaceRequests)
        }
        binding.rvMyPlaceRequest.adapter = myPlaceRequestAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}