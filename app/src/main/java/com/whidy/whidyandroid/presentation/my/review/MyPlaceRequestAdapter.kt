package com.whidy.whidyandroid.presentation.my.review

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.data.my.MyPlaceRequestResponse
import com.whidy.whidyandroid.databinding.ItemMyPlaceRequestBinding
import java.text.SimpleDateFormat
import java.util.Locale

class MyPlaceRequestAdapter(
    private var items: List<MyPlaceRequestResponse>
) : RecyclerView.Adapter<MyPlaceRequestAdapter.ReviewViewHolder>() {

    inner class ReviewViewHolder(private val binding: ItemMyPlaceRequestBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MyPlaceRequestResponse) {
            binding.apply {
                tvPlaceName.text = item.name
                tvPlaceAddress.text = item.address
                val formattedDate = if (!item.createdDate.isNullOrEmpty()) {
                    try {
                        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                        val date = inputFormat.parse(item.createdDate)
                        val outputFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
                        if (date != null) outputFormat.format(date) else ""
                    } catch (e: Exception) {
                        ""
                    }
                } else {
                    ""
                }
                tvProcessDate.text = formattedDate

                if (item.processed) {
                    tvProcessed.text = "추가완료"
                    tvProcessed.setTextColor(tvProcessed.context.getColor(R.color.PB600))
                    tvProcessed.setBackgroundResource(R.drawable.bg_my_place_request_processed)
                } else {
                    tvProcessed.text = "검수중"
                    tvProcessed.setTextColor(tvProcessed.context.getColor(R.color.White))
                    tvProcessed.setBackgroundResource(R.drawable.bg_my_place_request_processing)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMyPlaceRequestBinding.inflate(inflater, parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<MyPlaceRequestResponse>) {
        items = newItems.reversed()
        notifyDataSetChanged()
    }
}