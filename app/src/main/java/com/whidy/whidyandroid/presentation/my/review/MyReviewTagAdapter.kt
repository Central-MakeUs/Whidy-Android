package com.whidy.whidyandroid.presentation.my.review

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.whidy.whidyandroid.databinding.ItemMyReviewTagBinding
import com.whidy.whidyandroid.presentation.map.ItemType

class MyReviewTagAdapter(private val tags: List<ItemType>) :
    RecyclerView.Adapter<MyReviewTagAdapter.TagViewHolder>() {

    inner class TagViewHolder(private val binding: ItemMyReviewTagBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tag: ItemType) {
            binding.tvPlaceTag.text = tag.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMyReviewTagBinding.inflate(inflater, parent, false)
        return TagViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        holder.bind(tags[position])
    }

    override fun getItemCount() = tags.size
}