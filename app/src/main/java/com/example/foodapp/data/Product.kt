package com.example.foodapp.data

data class Product(
    val id: Int = 0, // Firebase sẽ lưu cái này như một trường số
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: Int = 0, // Lưu tạm ID drawable
    val rating: Double = 0.0,
    val category: String = ""
)