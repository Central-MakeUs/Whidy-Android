package com.whidy.whidyandroid.presentation.my.review

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.whidy.whidyandroid.data.my.MyReviewResponse
import com.whidy.whidyandroid.databinding.ItemMyReviewBinding
import com.whidy.whidyandroid.model.ItemType
import com.whidy.whidyandroid.presentation.map.info.PlaceInfoReviewTagAdapter
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Locale

class MyReviewAdapter(
    var items: MutableList<MyReviewResponse>,
    private val onItemClick: (MyReviewResponse) -> Unit,
    private val onDeleteClick: (MyReviewResponse) -> Unit
) : RecyclerView.Adapter<MyReviewAdapter.ReviewViewHolder>() {

    inner class ReviewViewHolder(private val binding: ItemMyReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MyReviewResponse) {
            binding.apply {
                tvPlaceName.text = item.placeName
                tvPlaceScore.text = item.score.toString()
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val date = inputFormat.parse(item.lastModifiedDateTime)
                val outputFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
                val formattedDate = date?.let { outputFormat.format(it) }
                tvPlaceReviewTime.text = formattedDate
                tvPlaceReviewComment.text = ""

                val displayTags = item.keywords.map { keyword ->
                    try {
                        ItemType.valueOf(keyword).description
                    } catch (e: Exception) {
                        Timber.e(e, "Unknown keyword: $keyword")
                        keyword
                    }
                }
                val tagAdapter = MyReviewTagAdapter(displayTags)
                rvPlaceReviewTag.adapter = tagAdapter

                root.setOnClickListener { onItemClick(item) }

                btnDelete.setOnClickListener { onDeleteClick(item) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMyReviewBinding.inflate(inflater, parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: MutableList<MyReviewResponse>) {
        items = newItems
        notifyDataSetChanged()
    }
}