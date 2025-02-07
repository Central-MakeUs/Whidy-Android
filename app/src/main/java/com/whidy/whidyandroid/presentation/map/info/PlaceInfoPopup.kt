package com.whidy.whidyandroid.presentation.map.info

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.whidy.whidyandroid.databinding.PopupPlaceInfoBinding

class PlaceInfoPopup(
    context: Context,
    anchorView: View,
) {
    private val popupWindow: PopupWindow
    private val binding: PopupPlaceInfoBinding

    init {
        val inflater = LayoutInflater.from(context)
        binding = PopupPlaceInfoBinding.inflate(inflater)

        popupWindow = PopupWindow(
            binding.root,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true // 포커스 가능
        )

        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true
        popupWindow.setBackgroundDrawable(null) // 배경 없애기

        popupWindow.showAsDropDown(anchorView, -12, -180, 0)
    }
}