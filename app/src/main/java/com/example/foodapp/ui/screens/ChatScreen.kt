package com.example.foodapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodapp.data.model.ChatMessage
import com.example.foodapp.ui.theme.PrimaryOrange
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onBackPressed: () -> Unit = {}
) {
    val messages = remember { mutableStateListOf<ChatMessage>() }
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val focusManager = LocalFocusManager.current // Để ẩn bàn phím nếu cần

    // Firebase setup
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val currentUser = auth.currentUser

    // 1. Lắng nghe tin nhắn
    LaunchedEffect(key1 = currentUser) {
        if (currentUser != null) {
            db.collection("chats")
                .document(currentUser.uid)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) return@addSnapshotListener
                    if (snapshot != null) {
                        messages.clear()
                        for (doc in snapshot.documents) {
                            val content = doc.getString("content") ?: ""
                            val isFromUser = doc.getBoolean("isFromUser") ?: true
                            val timestamp = doc.getDate("timestamp") ?: Date()
                            messages.add(ChatMessage(content, isFromUser, timestamp))
                        }
                    }
                }
        }
    }

    // 2. Auto scroll xuống cuối khi có tin mới
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        // SỬA LỖI MẤT Ô CHAT: Dùng bottomBar để ghim thanh nhập liệu xuống đáy
        bottomBar = {
            ChatInputBar(
                inputValue = inputText,
                onValueChange = { inputText = it },
                onSendClick = {
                    if (inputText.isNotBlank() && currentUser != null) {
                        val msgContent = inputText
                        inputText = "" // Xóa ô nhập ngay lập tức

                        // --- LOGIC GỬI TIN NHẮN ---

                        // A. Gửi tin nhắn vào subcollection
                        val newMessage = hashMapOf(
                            "content" to msgContent,
                            "isFromUser" to true,
                            "timestamp" to Date()
                        )

                        db.collection("chats")
                            .document(currentUser.uid)
                            .collection("messages")
                            .add(newMessage)

                        // B. SỬA LỖI TÊN HIỂN THỊ: Cập nhật Metadata ra ngoài document cha
                        // Lấy tên từ Firestore User (chính xác nhất) hoặc Auth làm dự phòng
                        db.collection("users").document(currentUser.uid).get()
                            .addOnSuccessListener { document ->
                                // Ưu tiên lấy tên từ Firestore (do người dùng update profile)
                                val firestoreName = document.getString("username")
                                // Nếu không có thì lấy display name của Google/Auth
                                val authName = currentUser.displayName
                                // Nếu không có nữa thì lấy Email
                                val finalName = if (!firestoreName.isNullOrEmpty()) firestoreName
                                                else if (!authName.isNullOrEmpty()) authName
                                                else currentUser.email ?: "User Khách"

                                val chatInfo = hashMapOf(
                                    "lastMessage" to msgContent,
                                    "lastUpdated" to Date(),
                                    "userEmail" to (currentUser.email ?: ""),
                                    "userName" to finalName, // <-- QUAN TRỌNG: Gửi tên đúng
                                    "id" to currentUser.uid
                                )
                                // Dùng SetOptions.merge() để cập nhật an toàn
                                db.collection("chats").document(currentUser.uid)
                                    .set(chatInfo, SetOptions.merge())
                            }
                    }
                }
            )
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Hỗ trợ khách hàng", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PrimaryOrange,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        // SỬA LỖI LAYOUT: PaddingValues + imePadding
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
                .imePadding() // Quan trọng: Đẩy nội dung lên khi bàn phím hiện
        ) {
            if (messages.isEmpty()) {
                EmptyChatState()
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
                    contentPadding = PaddingValues(bottom = 16.dp, top = 10.dp)
                ) {
                    items(messages) { msg ->
                        MessageBubble(msg)
                    }
                }
            }
        }
    }
}

// --- CÁC COMPONENT CON (GIỮ NGUYÊN HOẶC CHỈNH NHẸ) ---

@Composable
fun EmptyChatState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.SupportAgent, null, modifier = Modifier.size(80.dp), tint = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Xin chào! 👋", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.DarkGray)
        Text("Nhắn tin để Admin hỗ trợ bạn nhé.", fontSize = 14.sp, color = Color.Gray)
    }
}

@Composable
fun MessageBubble(message: ChatMessage) {
    val isUser = message.isFromUser
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .widthIn(max = 280.dp)
                .clip(RoundedCornerShape(
                    topStart = 16.dp, topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 0.dp,
                    bottomEnd = if (isUser) 0.dp else 16.dp
                ))
                .background(if (isUser) PrimaryOrange else Color.White)
                .padding(12.dp)
        ) {
            Text(
                text = message.content,
                color = if (isUser) Color.White else Color.Black,
                fontSize = 15.sp
            )
        }
        // Format giờ
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        Text(
            text = timeFormat.format(message.timestamp),
            fontSize = 10.sp,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

@Composable
fun ChatInputBar(
    inputValue: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    // Surface để tạo bóng đổ nhẹ ngăn cách với nội dung chat
    Surface(
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .navigationBarsPadding(), // Tránh bị đè bởi thanh điều hướng hệ thống
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputValue,
                onValueChange = onValueChange,
                placeholder = { Text("Nhập tin nhắn...", fontSize = 14.sp) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryOrange,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color(0xFFFAFAFA),
                    unfocusedContainerColor = Color(0xFFFAFAFA)
                ),
                maxLines = 3
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onSendClick,
                enabled = inputValue.isNotBlank(),
                modifier = Modifier
                    .size(48.dp)
                    .background(if (inputValue.isNotBlank()) PrimaryOrange else Color.LightGray, CircleShape)
            ) {
                Icon(Icons.Filled.Send, contentDescription = "Gửi", tint = Color.White)
            }
        }
    }
}