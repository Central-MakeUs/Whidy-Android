package com.whidy.whidyandroid.presentation.map

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.databinding.ItemPlaceReviewTagBinding
import timber.log.Timber

class PlaceReviewTagAdapter(private val items: List<PlaceReviewTag>) :
    RecyclerView.Adapter<PlaceReviewTagAdapter.ViewHolder>() {

    private var isExpanded = false

    inner class ViewHolder(private val binding: ItemPlaceReviewTagBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PlaceReviewTag, position: Int) {
            binding.tvPlaceReviewTag.text = item.type.description
            binding.tvPlaceReviewAmount.text = "${item.peopleCount}명"

            // 배경 변경 (인원 수 순서대로 적용)
            val imageRes = when (position) {
                0 -> R.drawable.ic_coffee
                1 -> R.drawable.ic_chair
                2 -> R.drawable.ic_focus
                3 -> R.drawable.ic_focus
                else -> R.drawable.ic_coffee
            }
            binding.ivPlaceReviewTag.setImageResource(imageRes)

            // 처음 3개만 표시, 나머지는 isExpanded 상태에 따라 보이기
            binding.root.visibility = if (position < 3 || isExpanded) View.VISIBLE else View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPlaceReviewTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size

    fun toggleItems() {
        Timber.d("toggleItems()")
        isExpanded = !isExpanded
        notifyDataSetChanged()
    }
}