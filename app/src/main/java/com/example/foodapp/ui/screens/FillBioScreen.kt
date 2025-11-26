package com.example.foodapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack // <--- ĐÃ SỬA IMPORT
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.foodapp.navigation.Screen
import com.example.foodapp.ui.theme.PrimaryOrange

@Composable
fun FillBioScreen(navController: NavController) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            // --- BACK BUTTON ---
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .background(PrimaryOrange.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .clickable { navController.popBackStack() },
                contentAlignment = Alignment.Center
            ) {
                // SỬA LỖI Ở ĐÂY: Dùng Icons.Default.ArrowBack thay vì AutoMirrored
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = PrimaryOrange
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- TITLE ---
            Text(
                text = "Điền thông tin cá nhân của bạn để bắt đầu",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                lineHeight = 34.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Thông tin sẽ hiển thị trong hồ sơ của bạn nhằm đảm bảo an toàn.",
                fontSize = 14.sp,
                color = Color.Gray,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- FORM INPUTS ---
            CustomTextField(
                value = firstName,
                onValueChange = { firstName = it },
                placeholder = "First Name"
            )
            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                value = lastName,
                onValueChange = { lastName = it },
                placeholder = "Last Name"
            )
            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                value = mobileNumber,
                onValueChange = { mobileNumber = it },
                placeholder = "Mobile Number",
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone
            )
            Spacer(modifier = Modifier.height(16.dp))

             CustomTextField(
                value = address,
                onValueChange = { address = it },
                placeholder = "Address"
            )

            Spacer(modifier = Modifier.weight(1f))

            // --- BUTTON NEXT ---
            Button(
                onClick = {
                     navController.navigate(Screen.Home) {
                         popUpTo(Screen.Onboarding) { inclusive = true }
                     }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(vertical = 32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(text = "Next", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}