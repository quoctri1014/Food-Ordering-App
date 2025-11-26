package com.example.foodapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.foodapp.R
import com.example.foodapp.navigation.Screen
import com.example.foodapp.ui.theme.PrimaryOrange

@Composable
fun OnboardingScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // --- 1. BACKGROUND DECORATION (Giữ nguyên) ---
        Image(
            painter = painterResource(id = R.drawable.splash_burger_top_right),
            contentDescription = null,
            modifier = Modifier.size(80.dp).align(Alignment.TopEnd).offset(x = (-20).dp, y = 80.dp)
        )
        Image(
             painter = painterResource(id = R.drawable.splash_burger_bottom_left),
             contentDescription = null,
             modifier = Modifier.size(90.dp).align(Alignment.CenterStart).offset(x = (-20).dp, y = 20.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.splash_cake),
            contentDescription = null,
            modifier = Modifier.size(70.dp).align(Alignment.CenterEnd).offset(x = 20.dp, y = 120.dp)
        )

        // --- 2. MAIN CONTENT (Logo & Text) ---
        // Sử dụng Column để căn giữa nội dung chính
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .padding(bottom = 100.dp), // Chừa khoảng trống bên dưới cho nút Next không đè lên chữ
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Căn giữa toàn bộ nội dung theo chiều dọc
        ) {
            Image(
                painter = painterResource(id = R.drawable.profile_burger),
                contentDescription = "Main Logo",
                modifier = Modifier.size(240.dp), // Tăng kích thước logo lên xíu cho đẹp
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Tìm món ăn yêu thích của\nbạn tại đây",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                color = Color.Black,
                lineHeight = 32.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Món ngon \"chữa lành\" chiếc bụng đói\ncủa bạn ở đây nè!",
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                color = Color.Gray,
                lineHeight = 24.sp
            )
        }

        // --- 3. BUTTON (Nằm riêng trong Box để ghim xuống đáy) ---
        Button(
            onClick = { navController.navigate(Screen.Login) },
            modifier = Modifier
                .align(Alignment.BottomCenter) // Ghim xuống đáy chính giữa
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .padding(bottom = 48.dp) // Cách đáy màn hình 48dp
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            Text(
                text = "Next",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}