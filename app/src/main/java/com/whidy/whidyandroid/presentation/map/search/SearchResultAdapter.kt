package com.whidy.whidyandroid.presentation.map.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.whidy.whidyandroid.data.place.GetPlaceResponse
import com.whidy.whidyandroid.databinding.ItemSearchResultBinding
import com.whidy.whidyandroid.model.PlaceType

class SearchResultAdapter(
    private val onItemClick: (GetPlaceResponse) -> Unit
) : RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    private val searchResults = mutableListOf<GetPlaceResponse>()

    fun updateData(newResults: List<GetPlaceResponse>) {
        searchResults.clear()
        searchResults.addAll(newResults)
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: ItemSearchResultBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GetPlaceResponse, onItemClick: (GetPlaceResponse) -> Unit) {
            binding.tvPlaceName.text = item.name
            binding.tvAddress.text = item.address
            binding.tvPlaceType.text = PlaceType.fromString(item.placeType)

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSearchResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(searchResults[position], onItemClick)
    }

    override fun getItemCount(): Int = searchResults.size
}