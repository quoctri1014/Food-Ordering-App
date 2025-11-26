package com.example.foodapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.foodapp.data.api.ResetPassRequest
import com.example.foodapp.data.api.RetrofitClient
import com.example.foodapp.navigation.Screen
import com.example.foodapp.ui.theme.PrimaryOrange
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(navController: NavController, email: String, otp: String) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var newPass by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Biến trạng thái hiển thị mật khẩu
    var isNewPassVisible by remember { mutableStateOf(false) }
    var isConfirmPassVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Đặt lại mật khẩu", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 24.dp)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text("Tạo mật khẩu mới", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            Text(
                "Mật khẩu mới của bạn phải khác với mật khẩu đã sử dụng trước đó.",
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp, bottom = 30.dp),
                lineHeight = 20.sp
            )

            // Ô nhập mật khẩu mới (Có nút mắt)
            OutlinedTextField(
                value = newPass,
                onValueChange = { newPass = it },
                label = { Text("Mật khẩu mới") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryOrange) },
                trailingIcon = {
                    val icon = if (isNewPassVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { isNewPassVisible = !isNewPassVisible }) {
                        Icon(icon, contentDescription = "Toggle Password")
                    }
                },
                visualTransformation = if (isNewPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryOrange,
                    focusedLabelColor = PrimaryOrange,
                    cursorColor = PrimaryOrange
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Ô nhập xác nhận mật khẩu (Có nút mắt)
            OutlinedTextField(
                value = confirmPass,
                onValueChange = { confirmPass = it },
                label = { Text("Xác nhận mật khẩu") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryOrange) },
                trailingIcon = {
                    val icon = if (isConfirmPassVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { isConfirmPassVisible = !isConfirmPassVisible }) {
                        Icon(icon, contentDescription = "Toggle Password")
                    }
                },
                visualTransformation = if (isConfirmPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryOrange,
                    focusedLabelColor = PrimaryOrange,
                    cursorColor = PrimaryOrange
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (newPass == confirmPass && newPass.length >= 6) {
                        isLoading = true
                        scope.launch {
                            try {
                                val response = RetrofitClient.instance.resetPassword(ResetPassRequest(email, otp, newPass))
                                isLoading = false
                                if (response.success) {
                                    Toast.makeText(context, "Đổi mật khẩu thành công! Vui lòng đăng nhập.", Toast.LENGTH_LONG).show()
                                    // Quay về màn hình Login và xóa hết lịch sử
                                    navController.navigate(Screen.Login) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                } else {
                                    Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                isLoading = false
                                Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Mật khẩu không khớp hoặc quá ngắn (tối thiểu 6 ký tự)", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                else Text("Hoàn tất", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}