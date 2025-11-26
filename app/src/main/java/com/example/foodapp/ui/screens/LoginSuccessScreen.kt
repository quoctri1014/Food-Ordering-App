package com.example.foodapp.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.foodapp.navigation.Screen
import com.example.foodapp.ui.theme.PrimaryOrange
import com.example.foodapp.data.FirestoreHelper
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun LoginSuccessScreen(navController: NavController) {

    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    val auth = FirebaseAuth.getInstance()

    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid

        // Hiệu ứng hoạt hình
        scale.animateTo(1f, animationSpec = tween(700))
        alpha.animateTo(1f, animationSpec = tween(500, delayMillis = 200))

        if (userId != null) {
            // 1. Lấy thông tin người dùng từ Firestore
            val userProfile = FirestoreHelper.getUserProfile(userId)

            // 2. Đợi một chút cho người dùng thấy thông báo thành công
            delay(1500)

            // 3. ⭐ LOGIC KIỂM TRA THÔNG TIN ⭐
            val targetRoute = if (userProfile == null || userProfile.address.isBlank() || userProfile.phoneNumber.isBlank()) {
                // A. Nếu chưa có địa chỉ HOẶC chưa có số điện thoại -> Bắt nhập
                Screen.AddressInput
            } else {
                // B. Nếu đã có đủ -> Vào trang chủ
                Screen.Root
            }

            // 4. Điều hướng
            navController.navigate(targetRoute) {
                // Xóa các màn hình Login/Register khỏi lịch sử back stack
                popUpTo(Screen.Login) { inclusive = true }
                popUpTo(Screen.SignUp) { inclusive = true }
                popUpTo(Screen.LoginSuccess) { inclusive = true }
            }
        } else {
            // Nếu lỗi auth, quay về login
            navController.navigate(Screen.Login) {
                popUpTo(Screen.LoginSuccess) { inclusive = true }
            }
        }
    }

    // Giao diện (Giữ nguyên)
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
             Box(
                modifier = Modifier
                    .size(150.dp)
                    .scale(scale.value)
                    .clip(CircleShape)
                    .background(PrimaryOrange),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Success",
                    tint = Color.White,
                    modifier = Modifier.size(80.dp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text("Đăng nhập thành công!", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            // Cập nhật dòng text để người dùng biết đang làm gì
            Text("Đang kiểm tra thông tin tài khoản...", color = Color.Gray)
        }
    }
}