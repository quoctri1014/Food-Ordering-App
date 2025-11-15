// File: com.example.foodapp.utils/AppUtils.kt

package com.example.foodapp.utils

import java.text.NumberFormat
import java.util.Locale

// ✅ ĐỊNH NGHĨA DUY NHẤT CHO HÀM CHUYỂN ĐỔI TIỀN TỆ
fun Int.toVND(): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    // Thay thế ký hiệu tiền tệ mặc định (₫) bằng "VND"
    return formatter.format(this).replace("₫", "đ").trim()
}