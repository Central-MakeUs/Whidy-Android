package com.whidy.whidyandroid.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemMixDecoration(private val space: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom = space
        outRect.top = space
        outRect.right = space
        outRect.left = space

        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = 0
        }

        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.left = 0
        }
    }
}