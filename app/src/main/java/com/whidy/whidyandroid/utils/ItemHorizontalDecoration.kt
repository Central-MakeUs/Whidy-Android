package com.whidy.whidyandroid.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemHorizontalDecoration(private val space: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.right = space
        outRect.left = space

        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.left = 0
        }
    }
}