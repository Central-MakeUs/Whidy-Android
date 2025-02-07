package com.whidy.whidyandroid.presentation.map.info

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.whidy.whidyandroid.databinding.ItemPlacePhotoBinding

class PlacePhotoAdapter(private val imageUrls: List<String>) :
    RecyclerView.Adapter<PlacePhotoAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(private val binding: ItemPlacePhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(imageUrl: String) {
            Glide.with(binding.root.context)
                .load(imageUrl)
                .into(binding.ivImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPlacePhotoBinding.inflate(inflater, parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageUrls[position])
    }

    override fun getItemCount(): Int = imageUrls.size
}