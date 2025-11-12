package com.example.foodorderingapp.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

// Đây là nơi quản lý trạng thái giỏ hàng toàn cục
object CartManager {
    // Sử dụng mutableStateListOf để Compose tự động cập nhật UI khi list thay đổi
    private val _cartItems = mutableStateListOf<CartItem>()
    val cartItems: SnapshotStateList<CartItem>
        get() = _cartItems

    // 1. THÊM SẢN PHẨM VÀO GIỎ HÀNG (hoặc tăng số lượng nếu đã tồn tại)
    fun addItemToCart(product: Product) {
        val existingItem = _cartItems.find { it.product.id == product.id }
        if (existingItem != null) {
            // Nếu sản phẩm đã tồn tại, tăng số lượng
            val index = _cartItems.indexOf(existingItem)
            _cartItems[index] = existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            // Nếu sản phẩm chưa có, thêm mới với số lượng 1
            _cartItems.add(CartItem(product, 1))
        }
    }

    // 2. TĂNG SỐ LƯỢNG
    fun increaseQuantity(item: CartItem) {
        val index = _cartItems.indexOf(item)
        if (index != -1) {
            _cartItems[index] = item.copy(quantity = item.quantity + 1)
        }
    }

    // 3. GIẢM SỐ LƯỢNG (và xóa nếu số lượng về 0)
    fun decreaseQuantity(item: CartItem) {
        val index = _cartItems.indexOf(item)
        if (index != -1) {
            if (item.quantity > 1) {
                _cartItems[index] = item.copy(quantity = item.quantity - 1)
            } else {
                // Xóa nếu số lượng là 1 và bị giảm
                _cartItems.removeAt(index)
            }
        }
    }

    // 4. XÓA SẢN PHẨM HOÀN TOÀN
    fun removeItem(item: CartItem) {
        _cartItems.remove(item)
    }

    // Thêm hàm để tính toán Subtotal (Dùng trong CartScreen)
    fun getSubtotal(): Double {
        return _cartItems.sumOf { it.product.price * it.quantity }
    }
}