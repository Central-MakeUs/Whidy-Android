package com.whidy.whidyandroid.presentation.map.info

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.whidy.whidyandroid.databinding.ItemMyReviewTagBinding

class PlaceInfoReviewTagAdapter(private val tags: List<String>) :
    RecyclerView.Adapter<PlaceInfoReviewTagAdapter.TagViewHolder>() {

    inner class TagViewHolder(private val binding: ItemMyReviewTagBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tag: String) {
            binding.tvPlaceTag.text = tag
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMyReviewTagBinding.inflate(inflater, parent, false)
        return TagViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        if (tags.size > 3 && position == 3) {
            val extraCount = tags.size - 3
            holder.bind("+$extraCount")
        } else {
            holder.bind(tags[position])
        }
    }

    override fun getItemCount() = if (tags.size > 3) 4 else tags.size
}