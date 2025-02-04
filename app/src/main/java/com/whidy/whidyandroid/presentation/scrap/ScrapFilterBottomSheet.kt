package com.whidy.whidyandroid.presentation.scrap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.databinding.BottomSheetScrapFilterBinding

class ScrapFilterBottomSheet(
    private val onFilterSelected: (Boolean) -> Unit,
    private val isSortedByName: Boolean
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetScrapFilterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetScrapFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 선택된 필터에 따라 체크 아이콘 표시
        if (isSortedByName) {
            binding.ivAlphabeticalOrder.visibility = View.VISIBLE
            binding.ivRegisterOrder.visibility = View.GONE
        } else {
            binding.ivAlphabeticalOrder.visibility = View.GONE
            binding.ivRegisterOrder.visibility = View.VISIBLE
        }

        binding.btnRegisterOrder.setOnClickListener {
            onFilterSelected(false) // 등록순
            dismiss()
        }

        binding.btnAlphabeticalOrder.setOnClickListener {
            onFilterSelected(true) // 가나다순
            dismiss()
        }

        adjustBottomSheetHeight()
    }

    private fun adjustBottomSheetHeight() {
        view?.post {
            val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navi)
            val bottomNavHeight = bottomNavigationView?.height ?: 0

            view?.layoutParams?.height = (view?.height ?: 0) + bottomNavHeight
            view?.requestLayout()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
