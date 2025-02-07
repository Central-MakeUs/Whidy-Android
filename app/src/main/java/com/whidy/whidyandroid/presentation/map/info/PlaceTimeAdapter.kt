package com.whidy.whidyandroid.presentation.map.info

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.whidy.whidyandroid.databinding.ItemPlaceTimeBinding

class PlaceTimeAdapter(private val timeList: List<PlaceTime>) :
    RecyclerView.Adapter<PlaceTimeAdapter.TimeViewHolder>() {

    inner class TimeViewHolder(private val binding: ItemPlaceTimeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(time: PlaceTime) {
            binding.tvDay.text = time.day
            binding.tvHours.text = time.hours
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPlaceTimeBinding.inflate(inflater, parent, false)
        return TimeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimeViewHolder, position: Int) {
        holder.bind(timeList[position])
    }

    override fun getItemCount(): Int = timeList.size
}
