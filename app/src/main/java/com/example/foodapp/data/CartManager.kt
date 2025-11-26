package com.example.foodapp.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

object CartManager {
    private val _cartItems = mutableStateListOf<CartItem>()
    val cartItems: SnapshotStateList<CartItem>
        get() = _cartItems

    fun addItemToCart(food: Food) {
        val existingItem = _cartItems.find { it.food.id == food.id }
        if (existingItem != null) {
            val index = _cartItems.indexOf(existingItem)
            _cartItems[index] = existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            _cartItems.add(CartItem(food, 1))
        }
    }

    fun increaseQuantity(item: CartItem) {
        val index = _cartItems.indexOf(item)
        if (index != -1) {
            _cartItems[index] = item.copy(quantity = item.quantity + 1)
        }
    }

    fun decreaseQuantity(item: CartItem) {
        val index = _cartItems.indexOf(item)
        if (index != -1) {
            if (item.quantity > 1) {
                _cartItems[index] = item.copy(quantity = item.quantity - 1)
            } else {
                _cartItems.removeAt(index)
            }
        }
    }

    fun removeItem(item: CartItem) {
        _cartItems.remove(item)
    }

    fun getSubtotal(): Int {
        return _cartItems.sumOf { it.food.price * it.quantity }
    }

    fun clearCart() {
        _cartItems.clear()
    }
}