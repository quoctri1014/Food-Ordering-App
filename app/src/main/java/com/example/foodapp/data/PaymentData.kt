package com.example.foodapp.data.model

import androidx.compose.ui.graphics.Color

// --------------------------------------------------------------------
// --- ENUM VÀ DATA CLASS CHÍNH CHO THANH TOÁN VÀ ĐẶT HÀNG ---
// --------------------------------------------------------------------

/** Phương thức thanh toán */
enum class PaymentMethod(val displayName: String, val methodId: String) {
    COD("Thanh toán khi nhận hàng (COD)", "COD"),
    QR_BIDV("Quét mã QR (BIDV)", "BIDV"),
    MOMO("Ví điện tử Momo", "MOMO")
}

/** Thông tin thanh toán (PaymentInfo) - Được sử dụng khi hoàn tất đơn hàng */
data class PaymentInfo(
    val fullName: String = "",
    val phone: String = "",
    val address: String = "",
    val note: String = "",
    val method: PaymentMethod = PaymentMethod.COD
)

// --------------------------------------------------------------------
// --- HẰNG SỐ MÀU DÙNG CHUNG (Tách ra từ Screens) ---
// --------------------------------------------------------------------

val PrimaryOrange = Color(0xFFFF9800) // Màu Cam chủ đạo
val PrimaryAccentColor = Color(0xFFFF9800) // Tương đương PrimaryOrange
val LightAccentBackground = Color(0xFFFFF3E0) // Màu Cam nhạt cho nền
val AppFoodTotalRed = Color(0xFFD32F2F) // Màu Đỏ cho tổng tiền