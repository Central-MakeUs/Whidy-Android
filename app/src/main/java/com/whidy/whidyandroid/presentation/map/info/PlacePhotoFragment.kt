package com.whidy.whidyandroid.presentation.map.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.databinding.FragmentPlacePhotoBinding
import com.whidy.whidyandroid.utils.ItemMixDecoration

class PlacePhotoFragment : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentPlacePhotoBinding? = null
    private val binding: FragmentPlacePhotoBinding
        get() = requireNotNull(_binding){"FragmentPlacePhotoBinding -> null"}

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

        val placePhotoAdapter = PlacePhotoAdapter(getPlacePhotoData())
        binding.rvPhoto.apply {
            adapter = placePhotoAdapter
            val itemSpace = resources.getDimensionPixelSize(R.dimen.place_photo)
            addItemDecoration(ItemMixDecoration(itemSpace))
        }
    }

    private fun getPlacePhotoData() : List<String> {
        return listOf(
            "https://images.pexels.com/photos/303324/pexels-photo-303324.jpeg",
            "https://images.pexels.com/photos/374885/pexels-photo-374885.jpeg",
            "https://images.pexels.com/photos/1267320/pexels-photo-1267320.jpeg",
            "https://images.pexels.com/photos/1107840/pexels-photo-1107840.jpeg",
            "https://images.pexels.com/photos/110854/pexels-photo-110854.jpeg",
            "https://images.pexels.com/photos/549083/pexels-photo-549083.jpeg",
            "https://images.pexels.com/photos/1395158/pexels-photo-1395158.jpeg",
            "https://images.pexels.com/photos/303324/pexels-photo-303324.jpeg",
            "https://images.pexels.com/photos/374885/pexels-photo-374885.jpeg",
            "https://images.pexels.com/photos/1267320/pexels-photo-1267320.jpeg",
            "https://images.pexels.com/photos/1107840/pexels-photo-1107840.jpeg",
            "https://images.pexels.com/photos/110854/pexels-photo-110854.jpeg",
            "https://images.pexels.com/photos/549083/pexels-photo-549083.jpeg",
            "https://images.pexels.com/photos/1395158/pexels-photo-1395158.jpeg",
            "https://images.pexels.com/photos/303324/pexels-photo-303324.jpeg",
            "https://images.pexels.com/photos/374885/pexels-photo-374885.jpeg",
            "https://images.pexels.com/photos/1267320/pexels-photo-1267320.jpeg",
            "https://images.pexels.com/photos/1107840/pexels-photo-1107840.jpeg",
            "https://images.pexels.com/photos/110854/pexels-photo-110854.jpeg",
            "https://images.pexels.com/photos/549083/pexels-photo-549083.jpeg"
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}