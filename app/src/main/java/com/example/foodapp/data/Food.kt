package com.example.foodapp.data

import com.google.firebase.firestore.DocumentId

data class Food(
    val id: String = "",
    val name: String = "",
    val price: Int = 0,
    val rating: Double = 0.0,
    val description: String = "",
    val imageUrl: Int = 0, // ID ảnh drawable
    val categoryId: String = "",
    val time: Int = 0,
    val kCal: Int = 0
)