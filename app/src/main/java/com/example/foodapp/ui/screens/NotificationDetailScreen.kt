package com.example.foodapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodapp.ui.theme.PrimaryOrange
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDetailScreen(
    title: String,
    body: String,
    onBackClick: () -> Unit
) {
    // Thời gian hiện tại
    val currentTime = remember {
        SimpleDateFormat("HH:mm • dd/MM/yyyy", Locale.getDefault()).format(Date())
    }

    // State để kích hoạt animation khi màn hình vừa mở
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    Scaffold(
        containerColor = Color(0xFFFAFAFA), // Nền trắng sứ tinh tế
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Chi tiết thông báo",
                        fontWeight = FontWeight.SemiBold, // Font chữ vừa phải, không quá đậm
                        fontSize = 17.sp
                    )
                },
                navigationIcon = {
                    // Nút Back được bo tròn nhẹ
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .background(Color.White, CircleShape)
                            .border(1.dp, Color(0xFFEEEEEE), CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(20.dp),
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent, // Làm trong suốt để hòa vào nền
                    titleContentColor = Color.Black
                )
            )
        },
        bottomBar = {
            // Hiệu ứng nút bấm hiện ra sau cùng
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(initialOffsetY = { 100 }) + fadeIn(animationSpec = tween(600, delayMillis = 300))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White) // Nền trắng dưới nút để tách biệt
                        .padding(16.dp)
                        .shadow(elevation = 0.dp) // Loại bỏ shadow cứng, dùng background
                ) {
                    Button(
                        onClick = onBackClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .shadow(
                                elevation = 10.dp,
                                shape = RoundedCornerShape(16.dp),
                                spotColor = PrimaryOrange.copy(alpha = 0.5f) // Đổ bóng màu cam nhẹ
                            ),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                    ) {
                        Text(
                            text = "Đã hiểu",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()), // Cho phép cuộn nếu nội dung dài
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Animation cho nội dung chính
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(500)) + slideInVertically(initialOffsetY = { 50 })
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Spacer(modifier = Modifier.height(20.dp))

                    // 1. HERO ICON (Đẹp hơn, mềm mại hơn)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(140.dp)
                    ) {
                        // Vòng tỏa sáng mờ
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            PrimaryOrange.copy(alpha = 0.15f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                        // Vòng tròn chính
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .shadow(15.dp, CircleShape, spotColor = PrimaryOrange.copy(alpha = 0.4f))
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.dp, PrimaryOrange.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = PrimaryOrange
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // 2. NGÀY GIỜ & BADGE
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = currentTime,
                            fontSize = 13.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 3. TIÊU ĐỀ (Tập trung thị giác)
                    Text(
                        text = title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A),
                        textAlign = TextAlign.Center,
                        lineHeight = 32.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 4. ĐƯỜNG KẺ TRANG TRÍ
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFFEEEEEE))
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 5. NỘI DUNG CHI TIẾT
                    Text(
                        text = body,
                        fontSize = 16.sp,
                        color = Color(0xFF555555),
                        textAlign = TextAlign.Start, // Căn trái cho nội dung dài dễ đọc hơn
                        lineHeight = 26.sp, // Tăng khoảng cách dòng cho thoáng
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Khoảng trống dưới cùng để không bị nút che mất khi cuộn
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}