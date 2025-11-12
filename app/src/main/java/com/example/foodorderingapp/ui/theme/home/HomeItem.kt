package com.example.foodorderingapp.ui.theme.home

import com.example.foodorderingapp.data.Product

// Định nghĩa cơ sở cho sản phẩm
data class Product(val id: Int, val name: String, val price: Double)
// Định nghĩa cơ sở cho banner (giả định)
data class BannerItem(val imageUrl: String)
// Định nghĩa cơ sở cho danh mục (giả định)
data class Category(val id: Int, val name: String)


// Sử dụng sealed class để định nghĩa các loại item khác nhau (View Types)
sealed class HomeItem {
    data object SearchBar : HomeItem()
    data class Banner(val imageUrls: List<Int>) : HomeItem() // Giả định dùng BannerItem
    data class HorizontalProductSection(val title: String, val products: List<Product>) : HomeItem()
    data class CategorySection(val categories: List<Category>) : HomeItem() // Giữ lại nếu bạn muốn dùng
}