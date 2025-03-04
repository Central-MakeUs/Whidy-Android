package com.whidy.whidyandroid.presentation.map.review

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.databinding.ItemPlaceReviewTagBinding
import com.whidy.whidyandroid.model.ItemType

class PlaceReviewTagAdapter(private var items: List<PlaceReviewTag>) :
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

            val imageRes = when(item.type) {
                ItemType.TASTY_COFFEE       -> R.drawable.ic_coffee
                ItemType.COMFORTABLE_SEAT   -> R.drawable.ic_chair
                ItemType.TASTY_DESSERT      -> R.drawable.ic_dessert
                ItemType.GOOD_VALUE         -> R.drawable.ic_money
                ItemType.SPACIOUS           -> R.drawable.ic_wide
                ItemType.GOOD_MUSIC         -> R.drawable.ic_music
                ItemType.CALM_AMBIENCE      -> R.drawable.ic_focus
                ItemType.LONG_STAY          -> R.drawable.ic_clock
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

    fun updateData(newTags: List<PlaceReviewTag>) {
        items = newTags
        notifyDataSetChanged()
    }
}