package com.example.foodapp.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/** Dùng cho NavGraph và Bottom Navigation */
sealed class Screen(val route: String, val title: String? = null, val icon: ImageVector? = null) {

    // --- Bottom Navigation Screens ---
    object Home : Screen("home", "Trang Chủ", Icons.Default.Home)
    object Favorites : Screen("favorites", "Yêu thích", Icons.Default.Favorite)
    object Cart : Screen("cart", "Giỏ hàng", Icons.Default.ShoppingCart)
    object Profile : Screen("profile", "Tài khoản", Icons.Default.Person)

    // --- Product Screens ---
    object FoodDetail : Screen("food_detail/{foodId}", "Chi tiết món ăn") {
        fun createRoute(foodId: String) = "food_detail/$foodId"
    }

    // --- Order & Payment Screens ---
    object Order : Screen("order", "Đơn hàng")
    object OrderTracking : Screen("order_tracking", "Theo dõi Đơn hàng")
    object PaymentMethodRoute : Screen("payment_method/{finalTotal}", "Thông tin Thanh toán")
    object QrDetailRoute : Screen("qr_detail/{methodId}/{finalTotalAmount}/{customerName}", "Thanh toán QR")

    // --- Profile Sub Screens ---
    object EditProfile : Screen("edit_profile", "Chỉnh sửa thông tin")
    object OrderHistory : Screen("order_history", "Lịch sử đơn hàng")
    object DeliveryAddress : Screen("delivery_address", "Địa chỉ giao hàng")
    object PaymentManagement : Screen("payment_management", "Quản lý Thanh toán")
    object Voucher : Screen("voucher", "Mã giảm giá")
    object Security : Screen("security", "Bảo mật")
    object AppSettings : Screen("app_settings", "Cài đặt ứng dụng")
    object Support : Screen("support", "Hỗ trợ")
    object Logout : Screen("logout", "Đăng xuất")
    object Saved : Screen("saved", "Đã lưu")
}