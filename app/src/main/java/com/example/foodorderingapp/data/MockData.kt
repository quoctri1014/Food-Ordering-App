package com.example.foodorderingapp.data

import com.example.foodorderingapp.R // Cần import để tham chiếu tới R.drawable

// --- Định nghĩa Lớp Dữ liệu (Để đảm bảo Product class tồn tại) ---


data class BannerItem(val imageUrl: Int) // Giả định BannerItem dùng ID drawable

// --- Dữ liệu Mẫu ---
object MockData {

    // Giả định bạn đã có các drawable resources này trong dự án
    val sampleProducts = listOf(
        // 1. Hamburger Trứng Bò (Hamburger 2Bò)
        Product(
            id = 1,
            name = "Hamburger 2 Bò",
            price = 59000.0,
            description = "Hamburger bò là sự kết hợp hoàn hảo giữa bánh mì mềm và nhân thịt bò xay đậm đà. 2 miếng thịt được nướng thơm phức, phủ lên trên lớp phô mai vàng óng tan chảy. Kèm theo là những lát rau củ tươi giòn như xà lách, cà chua và hành tây. Tất cả được kết hợp hài hòa với các loại sốt đặc trưng, tạo nên hương vị khó cưỡng trong từng miếng cắn.",
            imageUrl = R.drawable.ic_burger_2b, // Cập nhật Drawable ID
            rating = 4.6,
            category = "Burger"
        ),

        Product(
            id = 2,
            name = "Hamburger Tôm",
            price = 49000.0,
            description = "Hamburger tôm là sự sáng tạo độc đáo với nhân chính từ những con tôm tươi ngon, được xay nhuyễn và áp chảo đến độ vàng ruộm thơm phức. Lớp phô mai béo ngậy tan chảy ôm trọn miếng tôm đậm đà, kết hợp cùng các loại rau sống giòn tan như xà lách và cà chua. Tất cả được hòa quyện với sốt mayonnaise hoặc sốt đặc biệt, tạo nên trải nghiệm hương vị biển cả tinh tế và khó quên.",
            imageUrl = R.drawable.ic_burger_t, // Cập nhật Drawable ID
            rating = 4.5,
            category = "Burger"
        ),

        Product(
            id = 3,
            name = "Hamburger Trứng",
            price = 35000.0,
            description = "Hamburger  trứng ốp la và rau củ.",
            imageUrl = R.drawable.ic_burger_trung, // Cập nhật Drawable ID
            rating = 4.6,
            category = "Burger"
        ),

        // 4. Hamburger Gà + Cheese
        Product(
            id = 4,
            name = "Hamburger Gà +Cheese",
            price = 45000.0,
            description = "Hamburger gà chiên giòn cùng một lát phô mai tan chảy, rau và sốt.",
            imageUrl = R.drawable.ic_burger_gc, // Cập nhật Drawable ID
            rating = 4.7,
            category = "Burger"
        ),

        // 5. Mì Ý Tôm Bacon
        Product(
            id = 5,
            name = "Mỳ Ý Bò Bằm",
            price = 85000.0,
            description = "Mì Ý với bò bằm, và sốt cà chua, hành tây cay nhẹ.",
            imageUrl = R.drawable.ic_spaghetti, // Cập nhật Drawable ID
            rating = 4.8,
            category = "Pasta"
        ),

        // 6. Mỳ Ý Hải Sản
        Product(
            id = 6,
            name = "Mỳ Ý Hải Sản",
            price = 88000.0,
            description = "Mì Ý sốt kem tươi với các loại hải sản (tôm, mực) phong phú.",
            imageUrl = R.drawable.mi_y_hai_san, // Cập nhật Drawable ID
            rating = 4.7,
            category = "Pasta"
        ),
        Product(
            id = 7,
            name = "Hamburger Gà",
            price = 88000.0,
            description = "Hamburger gà chiên giòn cùng rau và sốt.",
            imageUrl = R.drawable.ic_burger_g, // Cập nhật Drawable ID
            rating = 4.7,
            category = "Pasta"
        ),
        Product(
            id = 8,
            name = "Pizza Hải Sản",
            price = 88000.0,
            description = "Pizza hải sản là sự kết hợp hoàn hảo giữa lớp vỏ bánh giòn tan và phần nhân hải sản phong phú gồm tôm, mực, nghêu tươi ngon. Lớp phô mai Mozzarella vàng óng, kéo sợi quyến rũ phủ kín bề mặt, hòa quyện cùng sốt cà chua đậm đà và hương thơm đặc trưng của các loại gia vị. Mỗi miếng cắn đều mang đến sự hài hòa giữa vị ngọt tự nhiên của hải sản, vị béo ngậy của phô mai và độ giòn của đế bánh, tạo nên trải nghiệm ẩm thực đầy hấp dẫn.",
            imageUrl = R.drawable.ic_pizza_hs, // Cập nhật Drawable ID
            rating = 4.7,
            category = "Pasta"
        ),
    )

    // Dữ liệu mẫu cho Banner (Giả định Banner bạn đang dùng)
    val sampleBanners = listOf(
        BannerItem(R.drawable.ic_banner_sample_1), // Cập nhật Drawable ID
        BannerItem(R.drawable.ic_banner_sample_2)  // Cập nhật Drawable ID
    ).map { it.imageUrl } // Lấy list<Int>

    // Dữ liệu mẫu cho Cart (Dùng các sản phẩm vừa thêm)
    val sampleCartItems = listOf(
        CartItem(sampleProducts[0], 2), // Hamburger 2Bò
        CartItem(sampleProducts[4], 1)  // Mỳ Ý Tôm Bacon
    )
}