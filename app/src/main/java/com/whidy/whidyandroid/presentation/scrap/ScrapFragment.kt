package com.whidy.whidyandroid.presentation.scrap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.whidy.whidyandroid.databinding.FragmentScrapBinding

class ScrapFragment : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentScrapBinding? = null
    private val binding: FragmentScrapBinding
        get() = requireNotNull(_binding){"FragmentHomeBinding -> null"}

    private lateinit var scrapAdapter: ScrapAdapter
    private var scrapList = mutableListOf(
        ScrapItem("폴드 커피", "서울 성북구 종암로 214-3 1, 2층", PlaceType.STUDY_CAFE),
        ScrapItem("스타벅스 강남점", "서울 강남구 테헤란로 152", PlaceType.FRANCHISE_CAFE),
        ScrapItem("무료 독서실", "서울 서대문구 신촌로 35", PlaceType.FREE_STUDY),
        ScrapItem("카페 베네", "서울 종로구 대학로 12", PlaceType.GENERAL_CAFE),
        ScrapItem("공유 스터디룸", "서울 마포구 홍대입구 23", PlaceType.FREE_STUDY),
        ScrapItem("이디야 커피 홍대점", "서울 마포구 양화로 127", PlaceType.FRANCHISE_CAFE),
        ScrapItem("무료 면접사진 촬영소", "서울 강남구 역삼로 45", PlaceType.FREE_PICTURE),
        ScrapItem("네스프레소 카페", "서울 서초구 서초대로 77", PlaceType.GENERAL_CAFE),
        ScrapItem("무료 면접복 대여소", "서울 중구 남대문로 100", PlaceType.FREE_CLOTHES),
        ScrapItem("커피빈 종각점", "서울 종로구 종로 78", PlaceType.FRANCHISE_CAFE)
    )
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

        scrapAdapter = ScrapAdapter(scrapList) { itemCount ->
            updateItemCount(itemCount)
        }
        binding.rvScrap.adapter = scrapAdapter
        updateItemCount(scrapList.size)

        binding.btnScrapFilter.setOnClickListener {
            ScrapFilterBottomSheet({ sortByName ->
                isSortedByName = sortByName
                applySorting()
            }, isSortedByName).show(parentFragmentManager, "ScrapFilterBottomSheet")
        }
    }

    private fun updateItemCount(count: Int) {
        binding.tvAmountScrap.text = "전체 $count"
    }

    private fun applySorting() {
        scrapList = if (isSortedByName) {
            scrapList.sortedBy { it.name }.toMutableList()
        } else {
            scrapList.sortedByDescending { it.name }.toMutableList()
        }
        scrapAdapter.updateData(scrapList)
        binding.btnScrapFilter.text = if (isSortedByName) "가나다순" else "등록순"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}