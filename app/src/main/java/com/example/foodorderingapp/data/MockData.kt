package com.example.foodorderingapp.data

import com.example.foodorderingapp.R

data class BannerItem(val imageUrl: Int)
object MockData {
    val sampleProducts = listOf(
        Product(
            id = 1,
            name = "Hamburger 2 Bò",
            price = 59000.0,
            description = "Hamburger bò là sự kết hợp hoàn hảo giữa bánh mì mềm và nhân thịt bò xay đậm đà. 2 miếng thịt được nướng thơm phức, phủ lên trên lớp phô mai vàng óng tan chảy. Kèm theo là những lát rau củ tươi giòn như xà lách, cà chua và hành tây. Tất cả được kết hợp hài hòa với các loại sốt đặc trưng, tạo nên hương vị khó cưỡng trong từng miếng cắn.",
            imageUrl = R.drawable.ic_burger_2b,
            rating = 4.6,
            category = "Burger"
        ),

        Product(
            id = 2,
            name = "Hamburger Tôm",
            price = 49000.0,
            description = "Hamburger tôm là sự sáng tạo độc đáo với nhân chính từ những con tôm tươi ngon, được xay nhuyễn và áp chảo đến độ vàng ruộm thơm phức. Lớp phô mai béo ngậy tan chảy ôm trọn miếng tôm đậm đà, kết hợp cùng các loại rau sống giòn tan như xà lách và cà chua. Tất cả được hòa quyện với sốt mayonnaise hoặc sốt đặc biệt, tạo nên trải nghiệm hương vị biển cả tinh tế và khó quên.",
            imageUrl = R.drawable.ic_burger_t,
            rating = 4.5,
            category = "Burger"
        ),

        Product(
            id = 3,
            name = "Hamburger Trứng",
            price = 35000.0,
            description = "Hamburger  trứng ốp la và rau củ.",
            imageUrl = R.drawable.ic_burger_trung,
            rating = 4.6,
            category = "Burger"
        ),

        // 4. Hamburger Gà + Cheese
        Product(
            id = 4,
            name = "Hamburger Gà + Cheese",
            price = 45000.0,
            description = "Hamburger gà chiên giòn cùng một lát phô mai tan chảy, rau và sốt.",
            imageUrl = R.drawable.ic_burger_gc,
            rating = 4.7,
            category = "Burger"
        ),
        Product(
            id = 5,
            name = "Mỳ Ý Bò Bằm",
            price = 85000.0,
            description = "Mì Ý với bò bằm, và sốt cà chua, hành tây cay nhẹ.",
            imageUrl = R.drawable.ic_spaghetti,
            rating = 4.8,
            category = "Pasta"
        ),
        Product(
            id = 6,
            name = "Mỳ Ý Hải Sản",
            price = 88000.0,
            description = "Mì Ý sốt kem tươi với các loại hải sản (tôm, mực) phong phú.",
            imageUrl = R.drawable.mi_y_hai_san,
            rating = 4.1,
            category = "Pasta"
        ),
        Product(
            id = 7,
            name = "Hamburger Gà",
            price = 88000.0,
            description = "Hamburger gà chiên giòn cùng rau và sốt.",
            imageUrl = R.drawable.ic_burger_g,
            rating = 4.0,
            category = "Pasta"
        ),
        Product(
            id = 8,
            name = "Pizza Hải Sản",
            price = 88000.0,
            description = "Pizza hải sản là sự kết hợp hoàn hảo giữa lớp vỏ bánh giòn tan và phần nhân hải sản phong phú gồm tôm, mực, nghêu tươi ngon. Lớp phô mai Mozzarella vàng óng, kéo sợi quyến rũ phủ kín bề mặt, hòa quyện cùng sốt cà chua đậm đà và hương thơm đặc trưng của các loại gia vị. Mỗi miếng cắn đều mang đến sự hài hòa giữa vị ngọt tự nhiên của hải sản, vị béo ngậy của phô mai và độ giòn của đế bánh, tạo nên trải nghiệm ẩm thực đầy hấp dẫn.",
            imageUrl = R.drawable.ic_pizza_hs,
            rating = 4.6,
            category = "Pasta"
        ),
        Product(
            id = 9,
            name = "Pizza bò bulgogi",
            price = 99000.0,
            description = "Pizza bò bulgogi là sự giao thoa độc đáo giữa ẩm thực Hàn Quốc và Italy. Lớp vỏ bánh giòn tan được phủ đầy những lát thịt bò bulgogi mềm, thấm đẫm sốt truyền thống ngọt dịu đặc trưng. Phía trên là lớp phô mai Mozzarella vàng óng tan chảy, điểm xuyến cùng hành tây, nấm và hạt mè thơm lừng. Mỗi miếng cắn là sự hòa quyện hoàn hảo giữa vị ngọt đậm đà của thịt bò, vị béo của phô mai và hương khói đặc trưng từ sốt bulgogi.",
            imageUrl = R.drawable.ic_spaghetti,
            rating = 4.7,
            category = "Pasta"
        ),
        Product(
            id = 10,
            name = "Gà sốt cay",
            price = 119000.0,
            description = "Gà sốt cay là món ăn đầy hấp dẫn với những miếng gà vàng giòn, được phủ lớp sốt đỏ rực quyến rũ. Thịt gà mềm ngọt bên trong, lớp vỏ ngoài giòn rụm hòa quyện cùng vị cay nồng, ngọt dịu đặc trưng của sốt. Món ăn thường được điểm thêm hạt mè thơm lừng và hành lá xanh tươi, tạo nên sự cân bằng hài hòa giữa vị cay xé lưỡi và hương thơm khó cưỡnɡ.",
            imageUrl = R.drawable.ic_chicken_wings,
            rating = 3.9,
            category = "Gà"
        ),
    )
    val sampleBanners = listOf(
        BannerItem(R.drawable.ic_banner_sample_1), // Cập nhật Drawable ID
        BannerItem(R.drawable.ic_banner_sample_2)  // Cập nhật Drawable ID
    ).map { it.imageUrl } // Lấy list<Int>

    // Dữ liệu mẫu cho Cart (Dùng các sản phẩm vừa thêm)
    val sampleCartItems = listOf(
        CartItem(sampleProducts[0], 2), // Hamburger 2Bò
        CartItem(sampleProducts[4], 1)  // Mỳ Ý Tôm Bacon
    )
    // File: MockData.kt (hoặc nơi định nghĩa sampleConversations)

    val sampleConversations = listOf(
        Conversation(
            id = 1,
            userName = "Nguyễn Văn Hùng",
            // SỬ DỤNG STRING THUẦN TÚY
            lastMessage = "Đặt hàng thành công chưa shop?",
            timestamp = "10 phút trước",
            avatarUrl = R.drawable.user_avatar_1
        ),
        Conversation(
            id = 2,
            userName = "Trần Thu Hà",
            lastMessage = "Giao hàng hơi chậm nha.",
            timestamp = "2 giờ trước",
            avatarUrl = R.drawable.user_avatar_2
        ),
        Conversation(
            id = 3,
            userName = "Phạm Minh Cường",
            lastMessage = "Tôi cần hỗ trợ đổi món này.",
            timestamp = "Hôm qua",
            avatarUrl = R.drawable.user_avatar_1
        ),
        Conversation(
            id = 4,
            userName = "Lê Đức Mạnh",
            lastMessage = " ",
            timestamp = "Hôm qua",
            avatarUrl = R.drawable.user_avatar_2
        ),
    )
    // ... các định nghĩa lớp Message, Sender ...

    val sampleChatMessages = mapOf(
        // Tin nhắn mẫu cho 'Nguyễn Văn Hùng' (id=1 trong sampleConversations)
        "Nguyễn Văn Hùng" to listOf(
            Message(1, "Tôi muốn đặt một burger.", Sender.USER),
            Message(2, "Bạn muốn ăn loại nào?", Sender.SHOP),
            Message(3, "Ok, tôi đợi một chút nhé.", Sender.USER),
        ),

        // Tin nhắn mẫu cho 'Trần Thu Hà' (id=2)
        "Trần Thu Hà" to listOf(
            Message(1, "Giao hàng hơi chậm nha.", Sender.USER),
            Message(2, "Chúng tôi xin lỗi, đơn hàng đang trên đường.", Sender.SHOP),
        ),

        // Tin nhắn mẫu cho 'Phạm Minh Cường' (id=3)
        "Phạm Minh Cường" to listOf(
            Message(1, "Tôi cần hỗ trợ đổi món này.", Sender.USER),
            Message(2, "Vui lòng cho biết món ăn bạn muốn đổi ạ.", Sender.SHOP),
        ),

        // Tin nhắn mẫu cho 'Lê Đức Mạnh' (id=4)
        "Lê Đức Mạnh" to listOf(
            Message(1, "Cảm ơn món ăn rất ngon!", Sender.USER),
            Message(2, "Rất vui được phục vụ bạn!", Sender.SHOP),
        ),

        // Thêm các userName khác nếu cần...
        "Haha" to listOf(
            Message(1, "Xin chào.", Sender.USER),
            Message(2, "Shop có thể giúp gì cho bạn?", Sender.SHOP),
        ),
        "Hihi" to listOf(
            Message(1, "Bạn hàng của bạn đã được giao.", Sender.SHOP),
        )
    )
}