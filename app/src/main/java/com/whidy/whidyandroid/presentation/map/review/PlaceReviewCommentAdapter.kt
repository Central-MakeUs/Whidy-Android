package com.whidy.whidyandroid.presentation.map.review

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.whidy.whidyandroid.data.review.ReviewResponse
import com.whidy.whidyandroid.databinding.ItemPlaceReviewCommentBinding
import com.whidy.whidyandroid.model.ItemType
import com.whidy.whidyandroid.presentation.map.info.PlaceInfoReviewTagAdapter
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Locale

class PlaceReviewCommentAdapter(
    private var items: List<ReviewResponse>
) : RecyclerView.Adapter<PlaceReviewCommentAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemPlaceReviewCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ReviewResponse) {
            binding.apply {
                Glide.with(ivUserProfile.context)
                    .load(item.userProfileImage)
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivUserProfile)

                tvUserNickname.text = item.userName
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
                val tagAdapter = PlaceInfoReviewTagAdapter(displayTags)
                rvPlaceReviewTag.adapter = tagAdapter
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPlaceReviewCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<ReviewResponse>) {
        items = newItems
        notifyDataSetChanged()
    }
}