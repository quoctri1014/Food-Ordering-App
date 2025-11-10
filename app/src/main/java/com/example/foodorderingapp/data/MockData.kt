package com.example.foodorderingapp.data

import com.example.foodorderingapp.R

object MockData {
    val sampleProducts = listOf(
        // SỬA LỖI: Thay thế String bằng R.drawable.* (Int)
        // LƯU Ý: Bạn PHẢI có file Product.kt với imageUrl: Int để code này biên dịch
        Product("p1", "Burger Bò", "Burger bò nướng phô mai, rau tươi", 45000.0, R.drawable.ic_burger_b, 4.8),
        Product("p2", "Burger Gà", "Burger gà rán giòn rụm", 39000.0, R.drawable.ic_burger_g, 4.7),
        Product("p3", "Cánh Gà Sốt", "Cánh gà chiên sốt cay", 49000.0, R.drawable.ic_chicken_wings, 4.9),
        Product("p4", "Mì Ý Bò Bằm", "Mì spaghetti sốt bò bằm truyền thống", 55000.0, R.drawable.ic_spaghetti, 4.5),
        Product("p5", "Burger Phô Mai", "Burger phô mai tan chảy", 42000.0, R.drawable.ic_burger_c, 4.6),
        // Thêm các sản phẩm khác nếu cần...
    )
    val sampleCartItems = listOf(
        CartItem(sampleProducts[0], 2), // Burger Bò x 2
        CartItem(sampleProducts[2], 1), // Cánh Gà Sốt x 1
        CartItem(sampleProducts[3], 3)  // Mì Ý Bò Bằm x 3
    )

    val sampleBanners = listOf(
        R.drawable.ic_banner_sample_1, // Thay "banner_1" bằng tên Resource ID
//        R.drawable.ic_banner_sample_2  // Thay "banner_2" bằng tên Resource ID
    )
}