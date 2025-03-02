package com.whidy.whidyandroid.presentation.map.search

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.data.place.GetPlaceResponse
import com.whidy.whidyandroid.databinding.FragmentPlaceSearchBinding
import com.whidy.whidyandroid.presentation.base.MainActivity
import com.whidy.whidyandroid.presentation.map.home.MapViewModel
import com.whidy.whidyandroid.utils.ItemHorizontalDecoration
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
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

    private var searchJob: Job? = null

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

        val recentSearchList = getRecentSearches()
        recentSearchAdapter = RecentSearchAdapter(recentSearchList)
        binding.rvRecentSearch.apply {
            adapter = recentSearchAdapter
            val itemSpace = resources.getDimensionPixelSize(R.dimen.place_tag)
            val firstMargin = resources.getDimensionPixelSize(R.dimen.place_tag_first_margin)
            addItemDecoration(ItemHorizontalDecoration(itemSpace, firstMargin))
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
            } else {
                binding.btnDeleteEt.visibility = View.GONE
            }
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString()?.trim() ?: ""
                searchJob?.cancel()  // 이전에 진행 중인 작업 취소

                searchJob = lifecycleScope.launch {
                    delay(200) // 200ms 후에 실행 (디바운스 시간)
                    if (query.isEmpty()) {
                        Timber.d("입력 값이 비어있음 - 최근 검색 UI 보임")
                        binding.tvTitleRecentSearch.visibility = View.VISIBLE
                        binding.rvRecentSearch.visibility = View.VISIBLE
                        binding.btnDeleteEntire.visibility = View.VISIBLE
                        binding.rvSearchResult.visibility = View.GONE
                        binding.clSearchEmptyView.visibility = View.GONE
                        mapViewModel.clearSearchResults()
                    } else {
                        Timber.d("검색어가 유효함: '$query', 장소 리스트 요청")
                        binding.tvTitleRecentSearch.visibility = View.GONE
                        binding.rvRecentSearch.visibility = View.GONE
                        binding.btnDeleteEntire.visibility = View.GONE
                        binding.rvSearchResult.visibility = View.VISIBLE
                        mapViewModel.fetchPlaceList(query)
                    }
                }

                mapViewModel.searchResults.observe(viewLifecycleOwner) { places ->
                    Timber.d("검색 결과 업데이트: $places")
                    if (places != null) {
                        searchResultAdapter.updateData(places)
                        if (places.isEmpty()) {
                            Timber.d("검색 결과가 비어있음")
                            binding.rvSearchResult.visibility = View.GONE
                            binding.clSearchEmptyView.visibility = View.VISIBLE
                        } else {
                            Timber.d("검색 결과가 있음 - 아이템 개수: ${places.size}")
                            binding.rvSearchResult.visibility = View.VISIBLE
                            binding.clSearchEmptyView.visibility = View.GONE
                        }
                    } else {
                        Timber.d("검색 결과가 null")
                        binding.rvSearchResult.visibility = View.GONE
                        binding.clSearchEmptyView.visibility = View.GONE
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 필요 시 추가 로그
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 필요 시 추가 로그
            }
        })

        binding.btnDeleteEt.setOnClickListener {
            binding.etSearch.text.clear()
        }

        binding.btnDeleteEntire.setOnClickListener {
            clearRecentSearches()
            recentSearchAdapter.updateData(emptyList())
            binding.rvRecentSearch.visibility = View.GONE
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.etSearch.text.toString().trim()

                if (query.isNotEmpty()) {
                    val recentSearches = getRecentSearches()
                    // 이미 존재한다면 제거 후 최신 검색어로 갱신
                    recentSearches.remove(query)
                    recentSearches.add(0, query)
                    // 최대 10개까지만 저장
                    while (recentSearches.size > 10) {
                        recentSearches.removeAt(recentSearches.size - 1)
                    }
                    saveRecentSearches(recentSearches)
                    recentSearchAdapter.updateData(recentSearches)
                }

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

        addToRecentSearches(place.name)

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

    private fun addToRecentSearches(query: String) {
        if (query.isNotEmpty()) {
            val recentSearches = getRecentSearches()
            // 이미 존재하는 경우 제거하고 최신 순서로 추가
            recentSearches.remove(query)
            recentSearches.add(0, query)
            // 최대 10개까지만 저장
            while (recentSearches.size > 10) {
                recentSearches.removeAt(recentSearches.size - 1)
            }
            saveRecentSearches(recentSearches)
            recentSearchAdapter.updateData(recentSearches)
        }
    }

    // 최근 검색어를 JSONArray 형식으로 저장
    private fun getRecentSearches(): MutableList<String> {
        val prefs = requireContext().getSharedPreferences("recent_search", Context.MODE_PRIVATE)
        val json = prefs.getString("recent_searches", "[]")
        return try {
            val jsonArray = JSONArray(json)
            mutableListOf<String>().apply {
                for (i in 0 until jsonArray.length()) {
                    add(jsonArray.getString(i))
                }
            }
        } catch (e: Exception) {
            mutableListOf()
        }
    }

    private fun saveRecentSearches(list: List<String>) {
        val prefs = requireContext().getSharedPreferences("recent_search", Context.MODE_PRIVATE)
        val jsonArray = JSONArray()
        list.forEach { jsonArray.put(it) }
        prefs.edit().putString("recent_searches", jsonArray.toString()).apply()
    }

    private fun clearRecentSearches() {
        val prefs = requireContext().getSharedPreferences("recent_search", Context.MODE_PRIVATE)
        prefs.edit().remove("recent_searches").apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}