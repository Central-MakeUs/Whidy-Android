package com.whidy.whidyandroid.presentation.map.filter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.whidy.whidyandroid.databinding.ItemPlaceFilterBinding

class PlaceFilterAMTimeAdapter(
    private val times: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<PlaceFilterAMTimeAdapter.PlaceFilterTimeViewHolder>(){

    inner class PlaceFilterTimeViewHolder(val binding: ItemPlaceFilterBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceFilterTimeViewHolder {
        val binding = ItemPlaceFilterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaceFilterTimeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaceFilterTimeViewHolder, position: Int) {
        val time = times[position]
        holder.binding.tvFilterTime.text = time
        holder.binding.root.setOnClickListener {
            onItemClick(time)
        }
    }

    override fun getItemCount(): Int = times.size
}