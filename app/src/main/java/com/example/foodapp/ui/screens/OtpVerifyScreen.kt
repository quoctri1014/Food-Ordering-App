package com.example.foodapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions // ⭐ ĐÃ THÊM IMPORT
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.foodapp.data.api.RetrofitClient
import com.example.foodapp.data.api.VerifyOtpRequest
import com.example.foodapp.navigation.Screen
import com.example.foodapp.ui.theme.PrimaryOrange
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpVerifyScreen(
    navController: NavController,
    type: String,
    contact: String,
    verificationId: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val auth = Firebase.auth

    var otpValue by remember { mutableStateOf("") }
    var timeLeft by remember { mutableStateOf(120) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        while (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
    }

    fun verifyOtp() {
        isLoading = true
        if (type == "sms") {
            val credential = PhoneAuthProvider.getCredential(verificationId, otpValue)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    isLoading = false
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                        navController.navigate(Screen.Root) {
                            popUpTo(0) { inclusive = true }
                        }
                    } else {
                        Toast.makeText(context, "Mã OTP không đúng", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            scope.launch {
                try {
                    val response = RetrofitClient.instance.verifyOtp(VerifyOtpRequest(contact, otpValue))
                    isLoading = false
                    if (response.success) {
                        navController.navigate("reset_password_screen/$contact/$otpValue")
                    } else {
                        Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    isLoading = false
                    Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Xác thực OTP", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(horizontal = 24.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Text("Nhập mã xác thực", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Mã OTP đã được gửi đến:\n$contact",
                textAlign = TextAlign.Center, color = Color.Gray, lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            BasicTextField(
                value = otpValue,
                onValueChange = { if (it.length <= 6 && it.all { char -> char.isDigit() }) otpValue = it },
                // ⭐ ĐÃ SỬA LỖI Ở ĐÂY (Thêm import ở trên):
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                decorationBox = {
                    val codeLength = if (type == "sms") 6 else 4
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        repeat(codeLength) { index ->
                            val char = if (index >= otpValue.length) "" else otpValue[index].toString()
                            val isFocused = otpValue.length == index
                            Box(
                                modifier = Modifier.size(if (type=="sms") 45.dp else 60.dp).background(if (isFocused) PrimaryOrange.copy(0.1f) else Color(0xFFF5F5F5), RoundedCornerShape(12.dp)).border(2.dp, if (isFocused) PrimaryOrange else Color.Transparent, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) { Text(char, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = if(isFocused) PrimaryOrange else Color.Black) }
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(40.dp))
            // ⭐ ĐÃ SỬA LỖI: Hàm formatTime đã được định nghĩa ở dưới cùng
            Text(text = "Mã hết hạn sau ${formatTime(timeLeft)}", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val requiredLen = if (type == "sms") 6 else 4
                    if (otpValue.length == requiredLen) verifyOtp()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                else Text("Xác nhận", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

// ⭐ HÀM NÀY QUAN TRỌNG (Để sửa lỗi Unresolved reference: formatTime)
fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return String.format("%02d:%02d", m, s)
}