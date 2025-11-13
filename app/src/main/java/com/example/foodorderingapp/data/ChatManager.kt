package com.example.foodorderingapp.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.toMutableStateList
import com.example.foodorderingapp.data.MockData.sampleChatMessages

object ChatManager {
    private val conversations: SnapshotStateMap<String, SnapshotStateList<Message>> = mutableStateMapOf()
    fun getMessagesForUser(userName: String): SnapshotStateList<Message> {
        return conversations.getOrPut(userName) {
            val initialMessages = sampleChatMessages[userName]
            if (initialMessages != null && initialMessages.isNotEmpty()) {
                initialMessages.toMutableStateList()
            } else {
                mutableStateListOf()
            }
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
            ConversationStateManager.updateState(userName, text)
        }
    }
}