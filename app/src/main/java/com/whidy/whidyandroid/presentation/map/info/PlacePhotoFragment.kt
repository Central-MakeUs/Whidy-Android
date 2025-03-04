package com.whidy.whidyandroid.presentation.map.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.databinding.FragmentPlacePhotoBinding
import com.whidy.whidyandroid.presentation.map.home.MapViewModel
import com.whidy.whidyandroid.utils.ItemMixDecoration

class PlacePhotoFragment : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentPlacePhotoBinding? = null
    private val binding: FragmentPlacePhotoBinding
        get() = requireNotNull(_binding){"FragmentPlacePhotoBinding -> null"}

    private val mapViewModel: MapViewModel by activityViewModels()

    private lateinit var placePhotoAdapter: PlacePhotoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPlacePhotoBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        binding.btnBack.setOnClickListener {
            navController.navigateUp()
        }

        placePhotoAdapter = PlacePhotoAdapter(emptyList())
        binding.rvPhoto.apply {
            adapter = placePhotoAdapter
            val itemSpace = resources.getDimensionPixelSize(R.dimen.place_photo)
            addItemDecoration(ItemMixDecoration(itemSpace))
        }

        mapViewModel.placeDetail.observe(viewLifecycleOwner) { place ->
            placePhotoAdapter.updateData(place.images)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}