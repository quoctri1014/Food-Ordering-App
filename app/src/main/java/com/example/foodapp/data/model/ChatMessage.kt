package com.example.foodapp.data.model

import java.util.Date

data class ChatMessage(
    val content: String = "",
    val isFromUser: Boolean = true, // true: User, false: Admin
    val timestamp: Date = Date()
)