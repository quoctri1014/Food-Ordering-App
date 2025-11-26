package com.example.foodapp.data.model

import com.google.firebase.firestore.DocumentId

data class Voucher(
    @DocumentId val id: String = "", // ID tự động của Firestore
    val code: String = "",          // Mã hiển thị (VD: SALE50)
    val discountAmount: Int = 0,    // Số tiền giảm (VD: 50000)
    val minOrderValue: Int = 0,     // Đơn tối thiểu để dùng (VD: 100000)
    val description: String = ""    // Mô tả (VD: Giảm 50k cho đơn 100k)
)