package com.example.foodapp.utils

import android.location.Location

// Tọa độ quán ăn (Ví dụ: 70 Tô Ký, Q12, TP.HCM)
const val SHOP_LATITUDE = 10.853822
const val SHOP_LONGITUDE = 106.627522

// Hàm tính khoảng cách (Giữ nguyên)
fun calculateDistanceKm(customerLat: Double, customerLon: Double): Double {
    if (customerLat == 0.0 && customerLon == 0.0) return 0.0

    val results = FloatArray(1)
    Location.distanceBetween(
        SHOP_LATITUDE, SHOP_LONGITUDE,
        customerLat, customerLon,
        results
    )
    return (results[0] / 1000).toDouble()
}

// ⭐ CẬP NHẬT LOGIC TÍNH PHÍ SHIP MỚI ⭐
fun calculateShippingFee(distanceKm: Double): Int {
    // Nếu chưa xác định được khoảng cách, lấy phí tối thiểu 5k
    if (distanceKm == 0.0) return 5000

    val baseFee = 5000       // Phí cơ bản: 5.000đ
    val baseDistance = 5.0   // Khoảng cách cơ bản: 5km
    val extraPricePerKm = 10000 // Phí thêm: 10.000đ/km

    return if (distanceKm <= baseDistance) {
        // Nếu dưới 5km -> Đồng giá 5k
        baseFee
    } else {
        // Nếu trên 5km -> 5k + (số km dư ra * 10k)
        // Math.ceil để làm tròn lên (ví dụ 5.1km tính là dư 1km)
        val extraKm = Math.ceil(distanceKm - baseDistance).toInt()
        baseFee + (extraKm * extraPricePerKm)
    }
}