package com.whidy.whidyandroid.presentation.map.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.data.place.GetPlaceResponse
import com.whidy.whidyandroid.databinding.FragmentPlaceSearchBinding
import com.whidy.whidyandroid.presentation.base.MainActivity
import com.whidy.whidyandroid.presentation.map.home.MapViewModel
import com.whidy.whidyandroid.utils.ItemHorizontalDecoration
import timber.log.Timber

class PlaceSearchFragment : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentPlaceSearchBinding? = null
    private val binding: FragmentPlaceSearchBinding
        get() = requireNotNull(_binding){"FragmentPlaceSearchBinding -> null"}

    private lateinit var recentSearchAdapter: RecentSearchAdapter
    private lateinit var searchResultAdapter: SearchResultAdapter

    private val mapViewModel: MapViewModel by activityViewModels()
    private var currentSearchResults: List<GetPlaceResponse> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPlaceSearchBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        (requireActivity() as MainActivity).hideBottomNavigation(true)

        recentSearchAdapter = RecentSearchAdapter(getDummyData())
        binding.rvRecentSearch.apply {
            adapter = recentSearchAdapter
            val itemSpace = resources.getDimensionPixelSize(R.dimen.place_tag)
            addItemDecoration(ItemHorizontalDecoration(itemSpace))
        }

        searchResultAdapter = SearchResultAdapter { place ->
            handlePlaceClick(place)
        }

        binding.rvSearchResult.apply {
            adapter = searchResultAdapter
        }

        binding.btnBack.setOnClickListener {
            navController.popBackStack()
        }

        binding.etSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.btnDeleteEt.visibility = View.VISIBLE
                binding.tvTitleRecentSearch.visibility = View.GONE
                binding.rvRecentSearch.visibility = View.GONE
                binding.btnDeleteEntire.visibility = View.GONE
            } else {
                binding.btnDeleteEt.visibility = View.GONE
                binding.tvTitleRecentSearch.visibility = View.VISIBLE
                binding.rvRecentSearch.visibility = View.VISIBLE
                binding.btnDeleteEntire.visibility = View.VISIBLE
            }
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    binding.tvTitleRecentSearch.visibility = View.VISIBLE
                    binding.rvRecentSearch.visibility = View.VISIBLE
                    binding.btnDeleteEntire.visibility = View.VISIBLE
                    binding.rvSearchResult.visibility = View.GONE
                    binding.clSearchEmptyView.visibility = View.GONE
                } else {
                    binding.tvTitleRecentSearch.visibility = View.GONE
                    binding.rvRecentSearch.visibility = View.GONE
                    binding.btnDeleteEntire.visibility = View.GONE
                    val query = s.toString().trim()
                    if (query.isNotEmpty()) {
                        binding.rvSearchResult.visibility = View.VISIBLE
                        mapViewModel.fetchPlaceList(query)
                    } else {
                        binding.rvSearchResult.visibility = View.GONE
                        binding.clSearchEmptyView.visibility = View.GONE
                    }
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        mapViewModel.searchResults.observe(viewLifecycleOwner) { places ->
            searchResultAdapter.updateData(places)

            if (places.isEmpty()) {
                binding.rvSearchResult.visibility = View.GONE
                binding.clSearchEmptyView.visibility = View.VISIBLE
            } else {
                binding.rvSearchResult.visibility = View.VISIBLE
                binding.clSearchEmptyView.visibility = View.GONE
            }
        }

        binding.btnDeleteEt.setOnClickListener {
            binding.etSearch.text.clear()
        }

        binding.btnDeleteEntire.setOnClickListener {
            binding.rvRecentSearch.visibility = View.GONE
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.etSearch.text.toString().trim()

                if (query.isNotEmpty() && currentSearchResults.isNotEmpty()) {
                    handlePlaceClick(currentSearchResults.first())
                }
                true
            } else {
                false
            }
        }

        binding.btnPlaceAdd.setOnClickListener {
            navController.navigate(R.id.action_navigation_place_search_to_add)
        }
    }

    private fun handlePlaceClick(place: GetPlaceResponse) {
        when (place.placeType) {
            "GENERAL_CAFE" -> mapViewModel.fetchPlaceGeneralCafe(place.id)
            "STUDY_CAFE" -> mapViewModel.fetchPlaceStudyCafe(place.id)
            "FREE_STUDY_SPACE" -> mapViewModel.fetchPlaceFreeStudy(place.id)
            "FREE_PICTURE" -> mapViewModel.fetchPlaceFreePicture(place.id)
            "FREE_CLOTHES_RENTAL" -> mapViewModel.fetchPlaceFreeClothes(place.id)
            "FRANCHISE_CAFE" -> mapViewModel.fetchPlaceFranchiseCafe(place.id)
            else -> Timber.e("Unknown placeType: ${place.placeType}")
        }

        if (!navController.popBackStack(R.id.navigation_map, false)) {
            navController.navigate(R.id.navigation_map)
        }
    }

    private fun getDummyData(): List<String> {
        return listOf("캐치카페 서울대", "캐치카페 신촌", "프로토콜 연희점")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}