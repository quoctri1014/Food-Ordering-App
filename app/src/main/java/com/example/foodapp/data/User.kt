package com.example.foodapp.data

import com.google.firebase.firestore.DocumentId
import androidx.annotation.Keep

@Keep
data class User(
    val id: String = "",
    val username: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val address: String = "", // Đảm bảo trường này tồn tại
    val role: String = "user",
    val avatarUrl: String = ""
)