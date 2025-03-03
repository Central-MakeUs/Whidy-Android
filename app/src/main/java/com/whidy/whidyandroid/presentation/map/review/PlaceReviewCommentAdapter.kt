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

class PlaceReviewCommentAdapter(
    private var items: List<ReviewResponse>
) : RecyclerView.Adapter<PlaceReviewCommentAdapter.ViewHolder>() {

    private val reviewTimes: MutableMap<Int, String> = mutableMapOf()

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
                tvPlaceReviewTime.text = reviewTimes[item.id] ?: ""
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

    fun updateReviewTime(reviewId: Int, time: String) {
        reviewTimes[reviewId] = time
        // 해당 리뷰 항목만 새로고침
        val index = items.indexOfFirst { it.id == reviewId }
        if (index != -1) {
            notifyItemChanged(index)
        }
    }
}