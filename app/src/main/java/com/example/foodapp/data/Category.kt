package com.example.foodapp.data

import com.google.firebase.firestore.DocumentId

data class Category(
    @DocumentId val id: String = "",
    val image: String = "", // Emoji icon
    val name: String = "",
    val colorHex: Long = 0xFFFFFFFF // Lưu màu dạng Hex (Long)
)