package com.example.foodorderingapp.ui.theme.home


import com.example.foodorderingapp.data.Product

// Sử dụng Sealed Interface để định nghĩa các loại mục khác nhau trên màn hình Home
sealed interface HomeItem {
    object SearchBar : HomeItem
    data class Banner(val imageUrls: List<Int>) : HomeItem
    data class HorizontalProductSection(val title: String, val products: List<Product>) : HomeItem
    data class CategorySection(val categories: List<String>) : HomeItem
}