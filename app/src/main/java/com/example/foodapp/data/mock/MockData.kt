package com.example.foodapp.data

import com.example.foodapp.R

object MockData {

    val categories = listOf(
        Category(id = "C1", image = "🍔", name = "Burger", colorHex = 0xFFFFE0B2),
        Category(id = "C2", image = "🍕", name = "Pizza", colorHex = 0xFFFFCCBC),
        Category(id = "C3", image = "🍣", name = "Sushi", colorHex = 0xFFB2DFDB),
        Category(id = "C4", image = "🥗", name = "Salad", colorHex = 0xFFC8E6C9),
        Category(id = "C5", image = "🍜", name = "Mì/Phở", colorHex = 0xFFB3E5FC),
        Category(id = "C6", image = "☕", name = "Đồ Uống", colorHex = 0xFFD7CCC8)
    )

    // --- DỮ LIỆU MÓN ĂN ---
    val mockFoods = listOf(
        Food(id = "1", name = "Hamburger 2 miếng Bò", price = 59000, rating = 4.9,
            description = "Hamburger bò 2 miếng thịt nướng thơm phức.", imageUrl = R.drawable.burger_2mieng_bo, categoryId = "C1", time = 15, kCal = 650),
        Food(id = "2", name = "Hamburger Tôm", price = 49000, rating = 4.7,
            description = "Hamburger nhân tôm tươi giòn rụm.", imageUrl = R.drawable.burger_tom, categoryId = "C1", time = 12, kCal = 580),
        Food(id = "3", name = "Hamburger Trứng", price = 29000, rating = 4.8,
            description = "Burger trứng ốp la béo ngậy.", imageUrl = R.drawable.burger_trung, categoryId = "C1", time = 10, kCal = 450),
        Food(id = "4", name = "Hamburger Gà + Cheese", price = 45000, rating = 4.6,
            description = "Gà chiên giòn tan kết hợp phô mai.", imageUrl = R.drawable.burger_ga_cheese, categoryId = "C1", time = 18, kCal = 620),
        Food(id = "5", name = "Pizza Hải Sản", price = 109000, rating = 4.9,
            description = "Pizza đầy ắp tôm mực tươi ngon.", imageUrl = R.drawable.pizza_haisan, categoryId = "C2", time = 25, kCal = 950),
        Food(id = "6", name = "Pizza Bò Băm", price = 98000, rating = 4.8,
            description = "Pizza bò băm sốt cà chua đậm đà.", imageUrl = R.drawable.pizza_bobam, categoryId = "C2", time = 20, kCal = 890),
        Food(id = "7", name = "Pizza Rau Củ", price = 85000, rating = 4.7,
            description = "Pizza chay thanh đạm.", imageUrl = R.drawable.pizza_raucu, categoryId = "C2", time = 15, kCal = 750),
        Food(id = "8", name = "Gà Rán Giòn Cay", price = 78000, rating = 4.5,
            description = "Gà rán sốt cay Hàn Quốc.", imageUrl = R.drawable.ga_ran_cay, categoryId = "C2", time = 20, kCal = 720),
        Food(id = "9", name = "Mì Ý Bò Băm", price = 39000, rating = 4.5,
            description = "Spaghetti sốt bò băm truyền thống.", imageUrl = R.drawable.mi_y_bobam, categoryId = "C5", time = 15, kCal = 550),
        Food(id = "10", name = "Mì Ý Hải Sản", price = 59000, rating = 4.7,
            description = "Mì Ý sốt kem hải sản.", imageUrl = R.drawable.mi_y_haisan, categoryId = "C5", time = 18, kCal = 600),
        Food(id = "11", name = "Trà Đào Cam Sả", price = 45000, rating = 4.6,
            description = "Trà trái cây giải nhiệt.", imageUrl = R.drawable.tra_dao_cam_sa, categoryId = "C6", time = 5, kCal = 180),
        Food(id = "12", name = "Cà Phê Đen", price = 30000, rating = 4.9,
            description = "Cà phê đen đá đậm chất Việt.", imageUrl = R.drawable.ca_phe_den, categoryId = "C6", time = 5, kCal = 5),
        Food(id = "13", name = "Sushi Set Lớn", price = 150000, rating = 4.6,
            description = "Combo sushi cá hồi tươi sống.", imageUrl = R.drawable.sushi_set, categoryId = "C3", time = 30, kCal = 1100),
        Food(id = "14", name = "Salad Gà Nướng", price = 70000, rating = 4.5,
            description = "Salad ức gà healthy.", imageUrl = R.drawable.salad_ga_nuong, categoryId = "C4", time = 10, kCal = 350)
    )

    // ⭐ ĐÃ XÓA PHẦN CHAT DƯ THỪA ⭐
}