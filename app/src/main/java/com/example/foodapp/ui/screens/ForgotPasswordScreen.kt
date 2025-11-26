package com.example.foodapp.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions // ⭐ ĐÃ THÊM IMPORT NÀY
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.foodapp.data.api.OtpRequest
import com.example.foodapp.data.api.RetrofitClient
import com.example.foodapp.ui.theme.PrimaryOrange
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(navController: NavController) {
    val context = LocalContext.current
    val activity = LocalContext.current as Activity
    val scope = rememberCoroutineScope()

    var inputValue by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(1) } // 0: SMS, 1: Email

    // Hàm gửi SMS qua Firebase
    fun sendSmsOtp(phoneNumber: String) {
        isLoading = true
        val formattedNum = if (phoneNumber.startsWith("0")) "+84${phoneNumber.substring(1)}" else phoneNumber

        val options = PhoneAuthOptions.newBuilder(Firebase.auth)
            .setPhoneNumber(formattedNum)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    isLoading = false
                }
                override fun onVerificationFailed(e: FirebaseException) {
                    isLoading = false
                    Toast.makeText(context, "Lỗi gửi SMS: ${e.message}", Toast.LENGTH_LONG).show()
                }
                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    isLoading = false
                    Toast.makeText(context, "Đã gửi mã SMS!", Toast.LENGTH_SHORT).show()
                    navController.navigate("otp_verify_screen/sms/$formattedNum/$verificationId")
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // Hàm gửi Email qua Server
    fun sendEmailOtp(email: String) {
        isLoading = true
        scope.launch {
            try {
                val response = RetrofitClient.instance.sendOtp(OtpRequest(email))
                isLoading = false
                if (response.success) {
                    navController.navigate("otp_verify_screen/email/$email/none")
                } else {
                    Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                isLoading = false
                Toast.makeText(context, "Lỗi kết nối Server: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quên mật khẩu", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
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
            Spacer(modifier = Modifier.height(10.dp))

            if (selectedTab == 1) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF4E5)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFE65100))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Server miễn phí nên cần 30s - 1 phút để khởi động. Vui lòng chờ nếu thấy lâu.",
                            fontSize = 13.sp, color = Color(0xFFE65100), lineHeight = 18.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            Text("Đừng lo lắng!", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            Text("Chọn phương thức liên hệ để nhận mã xác thực.", color = Color.Gray, modifier = Modifier.padding(top = 8.dp, bottom = 30.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    OptionCardSmall(
                        icon = Icons.Default.Phone,
                        title = "Via SMS",
                        isSelected = selectedTab == 0,
                        onClick = { selectedTab = 0; inputValue = "" }
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    OptionCardSmall(
                        icon = Icons.Default.Email,
                        title = "Via Email",
                        isSelected = selectedTab == 1,
                        onClick = { selectedTab = 1; inputValue = "" }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = inputValue,
                onValueChange = { inputValue = it },
                label = { Text(if (selectedTab == 0) "Số điện thoại" else "Địa chỉ Email") },
                leadingIcon = {
                    Icon(
                        if (selectedTab == 0) Icons.Default.Phone else Icons.Default.Email,
                        contentDescription = null,
                        tint = PrimaryOrange
                    )
                },
                // ⭐ ĐÃ SỬA LỖI Ở DÒNG NÀY:
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (selectedTab == 0) KeyboardType.Phone else KeyboardType.Email
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryOrange, focusedLabelColor = PrimaryOrange, cursorColor = PrimaryOrange
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (inputValue.isNotBlank()) {
                        if (selectedTab == 0) sendSmsOtp(inputValue) else sendEmailOtp(inputValue)
                    } else {
                        Toast.makeText(context, "Vui lòng nhập thông tin", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Đang gửi mã...", color = Color.White)
                } else {
                    Text("Tiếp tục", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun OptionCardSmall(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, isSelected: Boolean, onClick: () -> Unit) {
    val borderColor = if (isSelected) PrimaryOrange else Color(0xFFEEEEEE)
    val backgroundColor = if (isSelected) PrimaryOrange.copy(alpha = 0.05f) else Color.White
    val iconColor = if (isSelected) PrimaryOrange else Color.Gray

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(32.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = title, fontWeight = FontWeight.Bold, color = if(isSelected) PrimaryOrange else Color.Gray)
    }
}