package com.whidy.whidyandroid.presentation.map.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.naver.maps.geometry.LatLng
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.databinding.FragmentPlaceSearchBinding
import com.whidy.whidyandroid.presentation.map.home.MapViewModel
import com.whidy.whidyandroid.utils.ItemHorizontalDecoration

class PlaceSearchFragment : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentPlaceSearchBinding? = null
    private val binding: FragmentPlaceSearchBinding
        get() = requireNotNull(_binding){"FragmentPlaceSearchBinding -> null"}

    private lateinit var recentSearchAdapter: RecentSearchAdapter

    private val mapViewModel: MapViewModel by activityViewModels()

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

        recentSearchAdapter = RecentSearchAdapter(getDummyData())
        binding.rvRecentSearch.apply {
            adapter = recentSearchAdapter
            val itemSpace = resources.getDimensionPixelSize(R.dimen.place_tag)
            addItemDecoration(ItemHorizontalDecoration(itemSpace))
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
                if (s.isNullOrEmpty()) {
                    binding.rvRecentSearch.visibility = View.VISIBLE
                } else {
                    binding.rvRecentSearch.visibility = View.INVISIBLE
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.btnDeleteEt.setOnClickListener {
            binding.etSearch.text.clear()
        }

        binding.btnDeleteEntire.setOnClickListener {
            binding.rvRecentSearch.visibility = View.GONE
        }

        binding.etSearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val query = binding.etSearch.text.toString().trim()

                    if (query.isNotEmpty()) {
                        navController.navigateUp()
                    }
                    if (query.isNotEmpty()) {
                        // 검색 결과에서 위도, 경도 값을 가져왔다고 가정
                        val latitude = 37.546752
                        val longitude = 126.949977

                        // 🔹 ViewModel에 검색한 위치 저장
                        mapViewModel.setSelectedLocation(LatLng(latitude, longitude))

                        // 🔹 기존 MapFragment가 백 스택에 있으면 popBackStack 사용하여 되돌아가기
                        if (!navController.popBackStack(R.id.navigation_map, false)) {
                            navController.navigate(R.id.navigation_map)
                        }
                    }
                    return true
                }
                return false
            }
        })

        binding.btnPlaceAdd.setOnClickListener {
            navController.navigate(R.id.action_navigation_place_search_to_add)
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