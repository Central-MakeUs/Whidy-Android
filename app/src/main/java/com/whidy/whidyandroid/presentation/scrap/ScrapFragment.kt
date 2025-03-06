package com.whidy.whidyandroid.presentation.scrap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.databinding.FragmentScrapBinding
import com.whidy.whidyandroid.presentation.base.MainActivity
import com.whidy.whidyandroid.presentation.map.home.MapViewModel
import timber.log.Timber

class ScrapFragment : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentScrapBinding? = null
    private val binding: FragmentScrapBinding
        get() = requireNotNull(_binding){"FragmentHomeBinding -> null"}

    private lateinit var scrapAdapter: ScrapAdapter
    private var scrapList = mutableListOf<ScrapItem>()

    private val viewModel: ScrapViewModel by activityViewModels()
    private val mapViewModel: MapViewModel by activityViewModels()

    private var isSortedByName = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentScrapBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        (requireActivity() as MainActivity).hideBottomNavigation(false)

        viewModel.fetchScrapItems()

        scrapAdapter = ScrapAdapter(scrapList, { itemCount ->
            updateItemCount(itemCount)
        }, { scrapId ->
            viewModel.deleteScrap(scrapId)
        }, { item ->
            when (item.placeType) {
                "GENERAL_CAFE" -> mapViewModel.fetchPlaceGeneralCafe(item.placeId)
                "STUDY_CAFE" -> mapViewModel.fetchPlaceStudyCafe(item.placeId)
                "FREE_STUDY_SPACE" -> mapViewModel.fetchPlaceFreeStudy(item.placeId)
                "FREE_PICTURE" -> mapViewModel.fetchPlaceFreePicture(item.placeId)
                "FREE_CLOTHES_RENTAL" -> mapViewModel.fetchPlaceFreeClothes(item.placeId)
                "FRANCHISE_CAFE" -> mapViewModel.fetchPlaceFranchiseCafe(item.placeId)
                else -> Timber.e("Unknown reviewType: ${item.placeType}")
            }
            navController.navigate(R.id.navigation_place_info)
        })
        binding.rvScrap.adapter = scrapAdapter

        viewModel.scrapItems.observe(viewLifecycleOwner) { items ->
            scrapList = items.toMutableList()
            applySorting()
        }

        binding.btnScrapFilter.setOnClickListener {
            viewModel.fetchScrapItems()
            ScrapFilterBottomSheet({ sortByName ->
                isSortedByName = sortByName
                applySorting()
            }, isSortedByName).show(parentFragmentManager, "ScrapFilterBottomSheet")
        }
    }

    private fun applySorting() {
        scrapList = if (isSortedByName) {
            scrapList.sortedBy { it.name }.toMutableList()
        } else {
            scrapList.sortedByDescending { it.name }.toMutableList()
        }
        scrapAdapter.updateData(scrapList)
        binding.btnScrapFilter.text = if (isSortedByName) "가나다순" else "등록순"

        updateItemCount(scrapList.size)
    }

    private fun updateItemCount(count: Int) {
        binding.tvAmountScrap.text = "전체 $count"
        binding.clScrapEmptyView.visibility = if (count == 0) View.VISIBLE else View.GONE
        binding.rvScrap.visibility = if (count == 0) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}