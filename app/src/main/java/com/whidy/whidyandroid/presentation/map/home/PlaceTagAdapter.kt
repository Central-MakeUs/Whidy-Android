package com.whidy.whidyandroid.presentation.map.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.databinding.ItemPlaceTagBinding

class PlaceTagAdapter(
    private val placeTags: List<String>
) : RecyclerView.Adapter<PlaceTagAdapter.PlaceTagViewHolder>(){

    var onItemClick: ((position: Int, tag: String) -> Unit)? = null
    private var selectedPosition = -1

    inner class PlaceTagViewHolder(private val binding: ItemPlaceTagBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String, position: Int) {
            binding.tvPlaceTag.text = item
            binding.tvPlaceTag.setTextColor(
                if (position == selectedPosition)
                    ContextCompat.getColor(itemView.context, R.color.B400)
                else
                    ContextCompat.getColor(itemView.context, R.color.G800)
            )
            itemView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = position
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
                onItemClick?.invoke(position, item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceTagViewHolder {
        val binding = ItemPlaceTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaceTagViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaceTagViewHolder, position: Int) {
        holder.bind(placeTags[position], position)
    }

    override fun getItemCount(): Int = placeTags.size
}