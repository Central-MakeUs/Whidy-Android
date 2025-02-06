package com.whidy.whidyandroid.presentation.my

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.whidy.whidyandroid.databinding.ItemMyReviewBinding

class MyReviewAdapter(
    private var reviews: MutableList<MyReview>,
    private val onDeleteClick: (MyReview) -> Unit,
    private val onPlaceNameClick: (MyReview) -> Unit,
    private val onEditClick: (MyReview) -> Unit
) : RecyclerView.Adapter<MyReviewAdapter.ReviewViewHolder>() {

    inner class ReviewViewHolder(private val binding: ItemMyReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(review: MyReview) {
            binding.tvPlaceName.text = review.placeName
            binding.tvPlaceScore.text = review.score.toString()
            binding.tvPlaceReviewTime.text = review.reviewTime
            binding.tvPlaceReviewComment.text = review.reviewComment

            // 태그 설정
            val tagAdapter = MyReviewTagAdapter(review.tags)
            binding.rvPlaceReviewTag.adapter = tagAdapter

            binding.tvPlaceName.setOnClickListener {
                onPlaceNameClick(review)
            }

            binding.btnEdit.setOnClickListener {
                onEditClick(review)
            }

            binding.btnDelete.setOnClickListener {
                onDeleteClick(review)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMyReviewBinding.inflate(inflater, parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(reviews[position])
    }

    override fun getItemCount() = reviews.size

    fun removeItem(review: MyReview) {
        val position = reviews.indexOf(review)
        if (position != -1) {
            reviews.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, reviews.size) // 인덱스 재조정
        }
    }
}