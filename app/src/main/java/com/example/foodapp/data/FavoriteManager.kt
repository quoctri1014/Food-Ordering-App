package com.example.foodapp.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object FavoriteManager {
    // Sử dụng String vì Food.id là String
    private var _favoriteProductIds by mutableStateOf(setOf<String>())

    val favoriteProductIds: Set<String>
        get() = _favoriteProductIds

    fun isFavorite(foodId: String): Boolean {
        return _favoriteProductIds.contains(foodId)
    }

    fun toggleFavorite(foodId: String) {
        _favoriteProductIds = if (_favoriteProductIds.contains(foodId)) {
            _favoriteProductIds - foodId
        } else {
            _favoriteProductIds + foodId
        }
    }
}