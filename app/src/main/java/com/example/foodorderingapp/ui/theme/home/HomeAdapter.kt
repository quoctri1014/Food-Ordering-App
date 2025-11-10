package com.example.foodorderingapp.ui.theme.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodorderingapp.databinding.ItemBannerBinding
import com.example.foodorderingapp.databinding.ItemProductHorizontalBinding
import com.example.foodorderingapp.databinding.ItemSearchBarBinding

// Adapter chính cho Home Fragment, sử dụng Multiple View Types
class HomeAdapter : ListAdapter<HomeItem, RecyclerView.ViewHolder>(HomeDiffCallback()) {

    // Định nghĩa các loại View Type
    companion object {
        private const val VIEW_TYPE_SEARCH = 1
        private const val VIEW_TYPE_BANNER = 2
        private const val VIEW_TYPE_HORIZONTAL_SECTION = 3
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is HomeItem.SearchBar -> VIEW_TYPE_SEARCH
            is HomeItem.Banner -> VIEW_TYPE_BANNER
            is HomeItem.HorizontalProductSection -> VIEW_TYPE_HORIZONTAL_SECTION
            else -> throw IllegalArgumentException("Invalid view type at position $position")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_SEARCH -> SearchBarViewHolder(ItemSearchBarBinding.inflate(inflater, parent, false))
            VIEW_TYPE_BANNER -> BannerViewHolder(ItemBannerBinding.inflate(inflater, parent, false))
            VIEW_TYPE_HORIZONTAL_SECTION -> HorizontalSectionViewHolder(ItemProductHorizontalBinding.inflate(inflater, parent, false))
            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is HomeItem.SearchBar -> (holder as SearchBarViewHolder).bind()
            is HomeItem.Banner -> (holder as BannerViewHolder).bind(item)
            is HomeItem.HorizontalProductSection -> (holder as HorizontalSectionViewHolder).bind(item)
            is HomeItem.CategorySection -> TODO()
        }
    }

    // --- ViewHolders ---
    class SearchBarViewHolder(binding: ItemSearchBarBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            // Logic cho Search Bar (nếu có: ví dụ: setup listeners)
        }
    }

    class BannerViewHolder(private val binding: ItemBannerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeItem.Banner) {
            // TODO: Thiết lập ViewPager2 để hiển thị banners (cần tạo BannerAdapter riêng)
        }
    }

    class HorizontalSectionViewHolder(private val binding: ItemProductHorizontalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeItem.HorizontalProductSection) {
            binding.tvSectionTitle.text = item.title
            // TODO: Thiết lập RecyclerView ngang với ProductAdapter
        }
    }
}

class HomeDiffCallback : DiffUtil.ItemCallback<HomeItem>() {
    override fun areItemsTheSame(oldItem: HomeItem, newItem: HomeItem): Boolean {
        // Cần logic phức tạp hơn cho item view, tạm thời dùng so sánh đối tượng
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: HomeItem, newItem: HomeItem): Boolean {
        return oldItem == newItem
    }
}