package com.example.foodapp.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

// Class Order đại diện cho một đơn hàng
data class Order(
    val id: String = "", // ID sẽ được gán sau khi lấy từ Firebase
    val userId: String = "",
    val userName: String = "",
    val address: String = "",
    val phone: String = "",
    val totalPrice: Int = 0,
    val shippingFee: Int = 0,
    val finalAmount: Int = 0,
    val paymentMethod: String = "",
    val status: String = "Đang xử lý", // Đang xử lý, Đang giao, Hoàn tất, Đã hủy
    @ServerTimestamp val createdAt: Date? = null, // Thời gian server
    val items: List<OrderItem> = emptyList()
)

// Class con lưu chi tiết món ăn trong đơn
data class OrderItem(
    val foodId: String = "",
    val foodName: String = "",
    val foodImage: Int = 0,
    val price: Int = 0,
    val quantity: Int = 0,
    val note: String = "" // ⭐ ĐÃ THÊM: Trường lưu ghi chú/lưu ý của món ăn
)