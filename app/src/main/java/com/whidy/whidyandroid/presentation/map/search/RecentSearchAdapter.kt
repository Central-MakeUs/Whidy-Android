package com.whidy.whidyandroid.presentation.map.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.whidy.whidyandroid.databinding.ItemRecentSearchBinding

class RecentSearchAdapter(
    private var recentSearchTags: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<RecentSearchAdapter.RecentSearchViewHolder>(){

    inner class RecentSearchViewHolder(private val binding: ItemRecentSearchBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.tvRecentSearch.text = item
            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentSearchViewHolder {
        val binding = ItemRecentSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentSearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentSearchViewHolder, position: Int) {
        holder.bind(recentSearchTags[position])
    }

    override fun getItemCount(): Int = recentSearchTags.size

    fun updateData(newData: List<String>) {
        recentSearchTags = newData
        notifyDataSetChanged()
    }
}