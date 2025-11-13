package com.example.foodorderingapp.data

import android.os.Parcelable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.example.foodorderingapp.data.MockData.sampleConversations
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
object ConversationStateManager {
    private val conversationStates: SnapshotStateMap<String, Pair<MutableState<String>, MutableState<String>>> = mutableStateMapOf()
    fun getConversationState(userName: String, initialMessage: String, initialTimestamp: String): Pair<MutableState<String>, MutableState<String>> {
        return conversationStates.getOrPut(userName) {
            // Khởi tạo các MutableState với giá trị ban đầu từ MockData
            Pair(mutableStateOf(initialMessage), mutableStateOf(initialTimestamp))
        }
    }

    /**
     * Cập nhật trạng thái hiển thị khi có tin nhắn mới.
     */
    fun updateState(userName: String, lastMessageText: String) {
        // Tìm Conversation Item tương ứng trong danh sách mẫu
        val conversationItem = sampleConversations.find { it.userName == userName }

        // Lấy trạng thái hiện tại (hoặc khởi tạo nếu cần)
        val (messageState, timestampState) = getConversationState(
            userName = userName,
            initialMessage = conversationItem?.lastMessage ?: "",
            initialTimestamp = conversationItem?.timestamp ?: ""
        )

        // Cập nhật giá trị
        messageState.value = lastMessageText
        timestampState.value = "Vừa xong" // Cập nhật thời gian thực tế hơn
    }
}
