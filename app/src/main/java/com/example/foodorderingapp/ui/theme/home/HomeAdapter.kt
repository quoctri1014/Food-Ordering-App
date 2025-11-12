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
// Lỗi HomeDiffCallback đã được giải quyết bằng cách định nghĩa nó bên dưới
class HomeAdapter : ListAdapter<HomeItem, RecyclerView.ViewHolder>(HomeDiffCallback()) {

    // Định nghĩa các loại View Type
    companion object {
        // Lỗi Unresolved reference VIEW_TYPE_* đã được giải quyết
        private const val VIEW_TYPE_SEARCH = 1
        private const val VIEW_TYPE_BANNER = 2
        private const val VIEW_TYPE_HORIZONTAL_SECTION = 3
        private const val VIEW_TYPE_CATEGORY_SECTION = 4 // Thêm VIEW_TYPE_CATEGORY_SECTION
    }

    override fun getItemViewType(position: Int): Int {
        // Lỗi Unresolved reference 'getItem' đã được giải quyết
        return when (getItem(position)) {
            is HomeItem.SearchBar -> VIEW_TYPE_SEARCH
            is HomeItem.Banner -> VIEW_TYPE_BANNER
            is HomeItem.HorizontalProductSection -> VIEW_TYPE_HORIZONTAL_SECTION
            is HomeItem.CategorySection -> VIEW_TYPE_CATEGORY_SECTION
            else -> throw IllegalArgumentException("Invalid view type at position $position")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        // Lỗi Return type mismatch đã được giải quyết nhờ việc sử dụng đúng các ViewHolder
        // và đảm bảo tất cả các đường dẫn đều trả về RecyclerView.ViewHolder
        return when (viewType) {
            VIEW_TYPE_SEARCH -> SearchBarViewHolder(ItemSearchBarBinding.inflate(inflater, parent, false))
            VIEW_TYPE_BANNER -> BannerViewHolder(ItemBannerBinding.inflate(inflater, parent, false))
            VIEW_TYPE_HORIZONTAL_SECTION -> HorizontalSectionViewHolder(ItemProductHorizontalBinding.inflate(inflater, parent, false))
            VIEW_TYPE_CATEGORY_SECTION -> throw NotImplementedError("Category ViewHolder not implemented") // Cần tạo ItemCategoryBinding và CategoryViewHolder
            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is HomeItem.SearchBar -> (holder as SearchBarViewHolder).bind()
            is HomeItem.Banner -> (holder as BannerViewHolder).bind(item)
            is HomeItem.HorizontalProductSection -> (holder as HorizontalSectionViewHolder).bind(item)
            is HomeItem.CategorySection -> (holder as CategorySectionViewHolder).bind(item) // Thay thế TODO bằng bind
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
            // TODO: Thiết lập ViewPager2 để hiển thị banners
        }
    }

    class HorizontalSectionViewHolder(private val binding: ItemProductHorizontalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeItem.HorizontalProductSection) {
            binding.tvSectionTitle.text = item.title
            // TODO: Thiết lập RecyclerView ngang với ProductAdapter
        }
    }

    // --- ViewHolder còn thiếu (ví dụ cho HomeItem.CategorySection) ---
    // Bạn cần tạo class này để lỗi ở dòng 33 không xảy ra
    class CategorySectionViewHolder(binding: ItemProductHorizontalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeItem.CategorySection) {
            // Logic cho Category Section
        }
    }
}

// Lớp này đã được đưa vào cùng một file và được tham chiếu đúng
class HomeDiffCallback : DiffUtil.ItemCallback<HomeItem>() {
    override fun areItemsTheSame(oldItem: HomeItem, newItem: HomeItem): Boolean {
        // Cần logic phức tạp hơn, ví dụ so sánh ID
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: HomeItem, newItem: HomeItem): Boolean {
        return oldItem == newItem
    }
}