package com.example.foodapp.data.model

import com.example.foodapp.data.Food as DataFood
import com.example.foodapp.data.CartItem as DataCartItem
import androidx.compose.ui.graphics.Color


typealias Food = DataFood
typealias CartItem = DataCartItem

@Suppress("unused")
val mockFoodsBridge get() = com.example.foodapp.data.mockFoods


data class Category(
    val id: String,
    val emoji: String,
    val name: String,
    val color: Color
)