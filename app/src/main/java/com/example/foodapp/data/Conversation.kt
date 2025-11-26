package com.example.foodapp.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Conversation(
    val id: Int,
    val userName: String,
    var lastMessage: String,
    var timestamp: String,
    val avatarUrl: Int
) : Parcelable

@Parcelize
enum class Sender : Parcelable {
    USER,
    SHOP
}

@Parcelize
data class Message(
    val id: Int,
    val text: String,
    val sender: Sender,
) : Parcelable