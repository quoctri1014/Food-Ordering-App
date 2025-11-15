package com.example.foodapp.data

import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.foodapp.R // Cần đảm bảo R.drawable tồn tại

// ------------------- FOOD MODEL -------------------
data class Food(
    val id: String,
    val name: String,
    val price: Int,
    val rating: Double,
    val description: String,
    val imageUrl: Int, // Resource ID
    val categoryId: String,
    val time: Int,
    val kCal: Int
)

// ------------------- CATEGORY MODEL -------------------
data class Category(val id: String, val icon: String, val name: String, val color: Color)

// ------------------- USER MODEL -------------------
data class User(
    val id: String,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val address: String,
    val profilePictureUrl: String
)

// ------------------- CART ITEM MODEL (Đã fix lỗi 'note' và 'subtotal') -------------------
data class CartItem(
    val food: Food,
    val quantity: Int,
    val note: String = ""
) {
    val subtotal: Int
        get() = food.price * quantity
}
// ------------------- MOCK CATEGORIES -------------------
val categories = listOf(
    Category("C1", "🍔", "Burger", Color(0xFFFFE0B2)),
    Category("C2", "🍕", "Pizza", Color(0xFFFFCCBC)),
    Category("C3", "🍣", "Sushi", Color(0xFFB2DFDB)),
    Category("C4", "🥗", "Salad", Color(0xFFC8E6C9)),
    Category("C5", "🍜", "Mì/Phở", Color(0xFFB3E5FC)),
    Category("C6", "☕", "Đồ Uống", Color(0xFFD7CCC8))
)

// ------------------- MOCK FOODS -------------------
val mockFoods = listOf(
    Food("1", "Hamburger 2 miếng Bò", 59000, 4.9,
        "Thưởng thức hamburger bò thơm lừng với 2 miếng thịt bò xay nướng hoàn hảo, kết hợp phô mai tan chảy, rau tươi giòn và sốt đặc trưng, tạo nên hương vị đậm đà, béo ngậy mà vẫn cân bằng. Một bữa ăn nhanh lý tưởng nhưng đầy đủ dinh dưỡng và ngon miệng.",
        R.drawable.burger_2mieng_bo, "C1", 15, 650),
    Food("2", "Hamburger Tôm", 49000, 4.7,
        "Hamburger tôm giòn rụm, kẹp với rau xanh tươi ngon, sốt mayonnaise kiểu riêng, mang đến cảm giác giòn bên ngoài, mềm ngọt bên trong. Hương vị hải sản hòa quyện với bánh mì nướng thơm, chắc chắn sẽ khiến bạn muốn thưởng thức thêm lần nữa.",
        R.drawable.burger_tom, "C1", 12, 580),
    Food("3", "Hamburger Trứng", 29000, 4.8,
        "Burger trứng phô mai thơm béo, kết hợp rau xanh giòn mát và bánh mì mềm mại, bữa sáng hoàn hảo, vừa tiện lợi, vừa cung cấp năng lượng. Hương vị ngậy của trứng hòa cùng phô mai tạo nên sự cân bằng tuyệt vời.",
        R.drawable.burger_trung, "C1", 10, 450),
    Food("4", "Hamburger Gà + Cheese", 45000, 4.6,
        "Burger gà nướng mềm mại, phô mai tan chảy, rau củ tươi ngon, kèm với sốt đặc trưng. Miếng burger thơm lừng, thịt gà đậm đà và bánh mì vàng giòn, mang đến trải nghiệm thưởng thức hấp dẫn từ miếng đầu tiên.",
        R.drawable.burger_ga_cheese, "C1", 18, 620),
    Food("5", "Pizza Hải Sản", 109000, 4.9,
        "Pizza hải sản sốt kem béo ngậy, phủ tôm, mực và phô mai Mozzarella tan chảy, nướng vàng giòn. Hương vị hải sản tươi ngon hòa quyện với sốt kem thơm, mang đến cảm giác trọn vẹn cho bữa ăn gia đình hoặc gặp gỡ bạn bè.",
        R.drawable.pizza_haisan, "C2", 25, 950),
    Food("6", "Pizza Bò Băm", 98000, 4.8,
        "Pizza thịt bò băm kiểu Ý, sốt cà chua tươi đậm đà, phô mai Mozzarella béo ngậy, gia vị oregano và húng quế thơm nồng. Vỏ bánh giòn rụm bên ngoài, mềm bên trong, mang đến trải nghiệm pizza đúng chuẩn.",
        R.drawable.pizza_bobam, "C2", 20, 890),
    Food("7", "Pizza Rau Củ", 85000, 4.7,
        "Pizza chay rau củ tươi ngon, phô mai béo mịn, kết hợp các loại rau củ giòn ngọt như ớt chuông, cà chua, bí đỏ, tạo hương vị thanh mát và hấp dẫn. Lựa chọn lý tưởng cho những ai muốn bữa ăn nhẹ mà vẫn ngon miệng.",
        R.drawable.pizza_raucu, "C2", 15, 750),
    Food("8", "Gà Rán Giòn Cay", 78000, 4.5,
        "Gà rán giòn cay, vàng ruộm bên ngoài, thịt mềm ngọt bên trong, ướp gia vị cay nồng đặc trưng. Dùng kèm sốt chua ngọt hoặc mayonnaise, món ăn mang đến sự kích thích vị giác và cảm giác thỏa mãn tuyệt đối.",
        R.drawable.ga_ran_cay, "C2", 20, 720),
    Food("9", "Mì Ý Bò Băm", 39000, 4.5,
        "Mì Ý sốt cà chua tươi ngon, thịt bò băm thơm lừng, rắc phô mai Mozzarella béo mịn. Vị ngọt từ cà chua, đậm đà từ bò băm và hương thơm của phô mai kết hợp hài hòa, bữa trưa tiện lợi mà vẫn ngon miệng.",
        R.drawable.mi_y_bobam, "C5", 15, 550),
    Food("10", "Mì Ý Hải Sản", 59000, 4.7,
        "Mì Ý hải sản sốt kem béo ngậy, tôm và mực tươi ngon, hòa quyện cùng phô mai tan chảy. Hương vị phong phú, thơm nồng và đầy dinh dưỡng, món ăn hoàn hảo cho những buổi tối muốn thưởng thức hương vị Ý ngay tại nhà.",
        R.drawable.mi_y_haisan, "C5", 18, 600),
    Food("11", "Trà Đào Cam Sả", 45000, 4.6,
        "Trà đào cam sả mát lạnh, hương thơm tự nhiên từ trái đào, chanh và sả tươi. Vị thanh ngọt dịu dàng, giải nhiệt và sảng khoái, thích hợp cho mùa hè nóng nực hay lúc cần một ly giải khát nhẹ nhàng.",
        R.drawable.tra_dao_cam_sa, "C6", 5, 180),
    Food("12", "Cà Phê Đen", 30000, 4.9,
        "Cà phê đen nguyên chất, đậm đà, thơm nồng, giữ trọn hương vị truyền thống Việt Nam. Thưởng thức từng ngụm, cảm nhận vị đắng nhẹ, hậu ngọt và hương thơm quyến rũ, khởi đầu một ngày đầy năng lượng.",
        R.drawable.ca_phe_den, "C6", 5, 5),
    Food("13", "Sushi Set Lớn", 150000, 4.6,
        "Set Sushi tổng hợp với cá hồi, tôm, trứng cuộn và rong biển tươi ngon, trình bày đẹp mắt. Mỗi miếng sushi mềm, tươi và thơm, hòa cùng nước tương và wasabi, mang đến trải nghiệm chuẩn Nhật ngay tại bàn ăn của bạn.",
        R.drawable.sushi_set, "C3", 30, 1100),
    Food("14", "Salad Gà Nướng", 70000, 4.5,
        "Salad gà nướng vàng thơm, rau củ tươi giòn, sốt dầu giấm thanh nhẹ. Bữa ăn nhẹ nhàng, giàu dinh dưỡng, cân bằng vị giác, thích hợp cho những ai muốn ăn ngon nhưng không quá ngán.",
        R.drawable.salad_ga_nuong, "C4", 10, 350)
)

// ------------------- SCREEN DEFINITIONS -------------------
sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Home : Screen("home", "Trang Chủ", Icons.Default.Home)
    object Favorites : Screen("favorites", "Yêu thích", Icons.Default.Favorite)
    object Cart : Screen("cart", "Giỏ hàng", Icons.Default.ShoppingCart)
    object Profile : Screen("profile", "Tài khoản", Icons.Default.Person)
    object FoodDetail : Screen("food_detail/{foodId}", "Chi tiết món ăn") {
        fun createRoute(foodId: String) = "food_detail/$foodId"
    }
    object Search : Screen("search", "Tìm kiếm", Icons.Default.Search)
    object Order : Screen("order", "Đơn hàng")
    object OrderTracking : Screen("order_tracking", "Theo dõi Đơn hàng")
    object Payment : Screen("payment", "Thanh toán", Icons.Default.Payment)

}

val bottomNavItems = listOf(Screen.Home, Screen.Favorites, Screen.Cart, Screen.Profile)