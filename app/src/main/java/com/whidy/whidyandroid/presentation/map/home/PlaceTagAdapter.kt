package com.whidy.whidyandroid.presentation.map.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.whidy.whidyandroid.databinding.ItemPlaceTagBinding

class PlaceTagAdapter(
    private val placeTags: List<String>
) : RecyclerView.Adapter<PlaceTagAdapter.PlaceTagViewHolder>(){

    var onItemClick: ((position: Int, tag: String) -> Unit)? = null

    class PlaceTagViewHolder(private val binding: ItemPlaceTagBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String, position: Int, clickListener: ((Int, String) -> Unit)?) {
            binding.tvPlaceTag.text = item
            itemView.setOnClickListener {
                clickListener?.invoke(position, item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceTagViewHolder {
        val binding = ItemPlaceTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaceTagViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaceTagViewHolder, position: Int) {
        holder.bind(placeTags[position], position, onItemClick)
    }

    override fun getItemCount(): Int = placeTags.size
}