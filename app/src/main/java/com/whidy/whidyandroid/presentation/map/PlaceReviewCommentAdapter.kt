package com.whidy.whidyandroid.presentation.map

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.whidy.whidyandroid.databinding.ItemPlaceReviewCommentBinding

class PlaceReviewCommentAdapter(private val items: List<PlaceReviewComment>) :
    RecyclerView.Adapter<PlaceReviewCommentAdapter.ViewHolder>() {

    private var isExpanded = false

    inner class ViewHolder(private val binding: ItemPlaceReviewCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PlaceReviewComment, position: Int) {
            binding.apply {
                Glide.with(ivUserProfile.context)
                    .load("https://helios-i.mashable.com/imagery/articles/04GeUVUQwZxpTYXdqbocKH2/hero-image.fill.size_1248x702.v1722586579.jpg")
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivUserProfile)
            }
            binding.tvUserNickname.text = item.userName
            binding.tvPlaceScore.text = item.reviewScore.toString()
            binding.tvPlaceReviewTime.text = item.reviewTime
            binding.tvPlaceReviewComment.text = item.reviewComment

            // 태그 설정
            val tagAdapter = PlaceInfoReviewTagAdapter(item.tags)
            binding.rvPlaceReviewTag.adapter = tagAdapter

            // 처음 3개만 표시, 나머지는 isExpanded 상태에 따라 보이기
            binding.root.visibility = if (position < 3 || isExpanded) View.VISIBLE else View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPlaceReviewCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size
}