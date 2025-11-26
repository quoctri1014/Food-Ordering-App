package com.example.foodapp.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap

object ChatManager {
    // Lưu trữ tin nhắn tạm thời trong bộ nhớ (RAM)
    private val conversations: SnapshotStateMap<String, SnapshotStateList<Message>> = mutableStateMapOf()

    fun getMessagesForUser(userName: String): SnapshotStateList<Message> {
        return conversations.getOrPut(userName) {
            // ⭐ ĐÃ SỬA: Khởi tạo danh sách rỗng thay vì lấy từ MockData
            mutableStateListOf()
        }
    }

    fun sendMessage(userName: String, text: String, sender: Sender) {
        if (text.isNotBlank()) {
            val chatList = getMessagesForUser(userName)
            val newMessage = Message(
                id = chatList.size + 1,
                text = text,
                sender = sender
            )
            chatList.add(newMessage)
        }
    }
}