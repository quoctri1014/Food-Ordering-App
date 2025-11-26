package com.example.foodapp.data

data class CartItem(
    val food: Food,
    var quantity: Int,
    var note: String = ""
){
    val subtotal: Int
        get() = food.price * quantity
}