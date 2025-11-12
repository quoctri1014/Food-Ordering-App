package com.example.foodorderingapp.data

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

// Import lớp Product (giả định nằm trong cùng package)
import com.example.foodorderingapp.data.Product

object FavoriteManager {
    // Sử dụng mutableStateOf để lưu trữ Set<Int> của các ID sản phẩm được yêu thích.
    // Dùng property delegate (by) để tự động kích hoạt recompose khi _favoriteProductIds thay đổi.
    private var _favoriteProductIds by mutableStateOf(setOf<Int>())

    val favoriteProductIds: Set<Int>
        get() = _favoriteProductIds

    fun isFavorite(productId: Int): Boolean {
        return _favoriteProductIds.contains(productId)
    }

    /**
     * Thêm hoặc xóa sản phẩm khỏi danh sách yêu thích.
     */
    fun toggleFavorite(product: Product) {
        val productId = product.id
        _favoriteProductIds = if (_favoriteProductIds.contains(productId)) {
            // Nếu đã có: Xóa khỏi Set (Unfavorite)
            _favoriteProductIds - productId
        } else {
            // Nếu chưa có: Thêm vào Set (Favorite)
            _favoriteProductIds + productId
        }
    }
}