package com.example.foodorderingapp.data

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: Int, // URL hoặc tên drawable
    val rating: Double,
    val isFavorite: Boolean = false
)