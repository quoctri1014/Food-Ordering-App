package com.example.foodapp.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.foodapp.R
import com.example.foodapp.navigation.Screen
import com.example.foodapp.ui.theme.PrimaryOrange
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(navController: NavController) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    // Setup vị trí ban đầu
    val logoOffsetY = remember { Animatable(-screenHeight.value) }
    val textOffsetY = remember { Animatable(screenHeight.value) }

    // Độ mờ (Fade in)
    val alphaAnim = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        // GIAI ĐOẠN 1: Màn hình trắng 1 giây
        delay(1000L)

        // GIAI ĐOẠN 2: Chạy Animation nảy (Bouncy)
        launch {
            alphaAnim.animateTo(1f, animationSpec = tween(500))
        }

        launch {
            logoOffsetY.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }

        launch {
            textOffsetY.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }

        // Chờ 3 giây tổng cộng
        delay(3000L)

        // --- TEST MODE nếu muốn để chạy các luông từ đầu thì bỏ comment còn nếu muốn lưu đăng nhập thì comment dong dươi lai ---
         FirebaseAuth.getInstance().signOut()
        // -----------------

        // GIAI ĐOẠN 3: Điều hướng
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            navController.navigate(Screen.Root) {
                popUpTo(Screen.Splash) { inclusive = true }
            }
        } else {
            navController.navigate(Screen.Onboarding) {
                popUpTo(Screen.Splash) { inclusive = true }
            }
        }
    }

    // UI
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // LOGO
            Image(
                painter = painterResource(id = R.drawable.ic_burger_icon),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(250.dp)
                    .offset(y = logoOffsetY.value.dp)
            )

            // ⭐ ĐÃ GIẢM KHOẢNG CÁCH CÒN 4.dp
            Spacer(modifier = Modifier.height(4.dp))

            // CHỮ WELCOME (Font cơ bản)
            Text(
                text = "WELCOME",
                style = TextStyle(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    // Đã bỏ fontFamily = FontFamily.Serif để về font cơ bản
                    color = PrimaryOrange,
                    shadow = Shadow(
                        color = Color.Gray.copy(alpha = 0.3f),
                        offset = Offset(4f, 4f),
                        blurRadius = 8f
                    ),
                    letterSpacing = 2.sp
                ),
                modifier = Modifier.offset(y = textOffsetY.value.dp)
            )

            // SLOGAN
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Taste the best",
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp
                    // Cũng về font cơ bản cho đồng bộ
                ),
                modifier = Modifier.offset(y = textOffsetY.value.dp)
            )
        }
    }
}