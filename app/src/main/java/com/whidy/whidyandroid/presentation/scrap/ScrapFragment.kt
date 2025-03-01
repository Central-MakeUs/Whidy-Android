package com.whidy.whidyandroid.presentation.scrap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.whidy.whidyandroid.databinding.FragmentScrapBinding
import com.whidy.whidyandroid.presentation.base.MainActivity

class ScrapFragment : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentScrapBinding? = null
    private val binding: FragmentScrapBinding
        get() = requireNotNull(_binding){"FragmentHomeBinding -> null"}

    private lateinit var scrapAdapter: ScrapAdapter
    private var scrapList = mutableListOf<ScrapItem>()

    private val viewModel: ScrapViewModel by activityViewModels()

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
            // 삭제 API 호출: ViewModel에서 처리
            viewModel.deleteScrap(scrapId)
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