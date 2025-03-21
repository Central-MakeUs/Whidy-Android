package com.whidy.whidyandroid.presentation.scrap

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.databinding.ItemScrapBinding
import com.whidy.whidyandroid.model.PlaceType

class ScrapAdapter(
    private var items: MutableList<ScrapItem>,
    private val onItemRemoved: (Int) -> Unit,
    private val onDeleteScrap: (scrapId: Int) -> Unit,
    private val onItemClick: (ScrapItem) -> Unit
) : RecyclerView.Adapter<ScrapAdapter.ScrapViewHolder>() {

    inner class ScrapViewHolder(private val binding: ItemScrapBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ScrapItem) {
            binding.tvName.text = item.name
            binding.tvAddress.text = item.address
            binding.tvPlaceType.text = PlaceType.fromString(item.placeType)
            binding.ivScrapIc.setImageResource(getIconForType(item.placeType))

            binding.btnScrap.setOnClickListener {
                removeItem(position)
                onDeleteScrap(item.scrapId)
            }

            binding.root.setOnClickListener { onItemClick(item) }
        }

        private fun getIconForType(placeType: String): Int {
            return when (placeType) {
                "STUDY_CAFE" -> R.drawable.ic_scrap_study_cafe
                "GENERAL_CAFE" -> R.drawable.ic_scrap_personal_cafe
                "FREE_STUDY_SPACE" -> R.drawable.ic_scrap_free_study_space
                "FREE_PICTURE" -> R.drawable.ic_scrap_free_picture
                "FREE_CLOTHES_RENTAL" -> R.drawable.ic_scrap_free_clothes_rental
                "FRANCHISE_CAFE" -> R.drawable.ic_scrap_franchise_cafe
                else -> { R.drawable.ic_scrap_study_cafe }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScrapViewHolder {
        val binding = ItemScrapBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScrapViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScrapViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    private fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
        onItemRemoved(items.size) // 아이템 개수 업데이트 콜백 호출
    }

    fun updateData(newItems: MutableList<ScrapItem>) {
        items = newItems
        notifyDataSetChanged()
        onItemRemoved(items.size) // 아이템 개수 업데이트 콜백 호출
    }
}
