package com.example.foodapp.data.model

import androidx.compose.ui.graphics.Color

/**
 * Enum PaymentMethod chứa các phương thức thanh toán có thể sử dụng.
 */
enum class PaymentMethod(val methodId: String, val displayName: String) {
    COD("COD", "Thanh toán khi nhận hàng (Tiền mặt)"),
    // ⭐ KHẮC PHỤC LỖI: THAY THẾ QR/MOMO BẰNG BANK ⭐
    BANK("BANK", "Chuyển khoản Ngân hàng (QR/VCB)")
}

/**
 * Data class chứa tất cả thông tin cần thiết để hoàn tất đơn hàng và thanh toán.
 */
data class PaymentInfo(
    val fullName: String = "",
    val phone: String = "",
    val address: String = "",
    val note: String = "",
    val method: PaymentMethod = PaymentMethod.COD,
    // ⭐ Thuộc tính này là BẮT BUỘC để tránh lỗi biên dịch trong PaymentScreens.kt
    val shippingFee: Int = 0
)