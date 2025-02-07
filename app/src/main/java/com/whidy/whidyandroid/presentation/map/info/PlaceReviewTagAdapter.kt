package com.whidy.whidyandroid.presentation.map.info

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.databinding.ItemPlaceReviewTagBinding

class PlaceReviewTagAdapter(private val items: List<PlaceReviewTag>) :
    RecyclerView.Adapter<PlaceReviewTagAdapter.ViewHolder>() {

    private val fillRatios = listOf(0.8f, 0.7f, 0.6f, 0.5f, 0.4f)

    inner class ViewHolder(private val binding: ItemPlaceReviewTagBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PlaceReviewTag, position: Int) {
            binding.tvPlaceReviewTag.text = item.type.description
            binding.tvPlaceReviewAmount.text = "${item.peopleCount}명"

            val params = binding.viewBackgroundFill.layoutParams as ConstraintLayout.LayoutParams
            params.matchConstraintPercentWidth = fillRatios.getOrElse(position) { 0.5f } // 기본값 50%
            binding.viewBackgroundFill.layoutParams = params

            // 배경 변경 (인원 수 순서대로 적용)
            val imageRes = when (position) {
                0 -> R.drawable.ic_coffee
                1 -> R.drawable.ic_chair
                2 -> R.drawable.ic_focus
                3 -> R.drawable.ic_focus
                else -> R.drawable.ic_coffee
            }
            binding.ivPlaceReviewTag.setImageResource(imageRes)
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
}