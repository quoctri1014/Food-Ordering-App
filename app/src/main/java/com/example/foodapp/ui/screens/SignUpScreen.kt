package com.example.foodapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.foodapp.R
import com.example.foodapp.data.FirestoreHelper
import com.example.foodapp.navigation.Screen
import com.example.foodapp.ui.theme.PrimaryOrange
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var keepSignedIn by remember { mutableStateOf(true) }
    var emailPromo by remember { mutableStateOf(true) }

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    var isLoading by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // Hình nền trang trí
        Image(
            painter = painterResource(id = R.drawable.splash_burger_top_right), // Đảm bảo hình này tồn tại
            contentDescription = null,
            modifier = Modifier.size(150.dp).align(Alignment.TopEnd).offset(y = (-50).dp),
            alpha = 0.1f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Image(
                painter = painterResource(id = R.drawable.profile_burger), // Đảm bảo hình này tồn tại
                contentDescription = "Logo",
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("BURGERKING", fontSize = 24.sp, fontWeight = FontWeight.Black, color = PrimaryOrange, letterSpacing = 1.sp)
            Text("Food Ordering App", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(30.dp))
            Text("Đăng ký miễn phí", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(24.dp))

            // Input fields
            CustomTextField(value = username, onValueChange = { username = it }, placeholder = "User Name", icon = Icons.Default.Person)
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(value = email, onValueChange = { email = it }, placeholder = "Email", icon = Icons.Default.MailOutline, keyboardType = KeyboardType.Email)
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Password",
                icon = Icons.Default.Lock,
                isPassword = true,
                isPasswordVisible = isPasswordVisible,
                onVisibilityChange = { isPasswordVisible = !isPasswordVisible }
            )

            Spacer(modifier = Modifier.height(20.dp))
            RowItemCheckbox(checked = keepSignedIn, onCheckedChange = { keepSignedIn = it }, text = "Duy trì đăng nhập")
            Spacer(modifier = Modifier.height(8.dp))
            RowItemCheckbox(checked = emailPromo, onCheckedChange = { emailPromo = it }, text = "Thông báo qua email")

            Spacer(modifier = Modifier.height(30.dp))

            // --- BUTTON TẠO TÀI KHOẢN (LOGIC FIREBASE) ---
            Button(
                onClick = {
                    if (email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty()) {
                        isLoading = true
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                isLoading = false
                                if (task.isSuccessful) {
                                    // 1. Lấy user vừa tạo
                                    val firebaseUser = auth.currentUser
                                    if (firebaseUser != null) {
                                        // 2. Lưu thông tin user vào Firestore
                                        FirestoreHelper.syncUser(firebaseUser, username)

                                        Toast.makeText(context, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                                        // 3. Điều hướng về Login
                                        navController.navigate(Screen.Login) {
                                            popUpTo(Screen.SignUp) { inclusive = true }
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Lỗi: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(text = "Tạo tài khoản", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Đã có tài khoản? ", fontSize = 14.sp, color = Color.Gray)
                Text(
                    text = "Đăng nhập ngay",
                    fontSize = 14.sp,
                    color = PrimaryOrange,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { navController.navigate(Screen.Login) }
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// Giữ nguyên các hàm phụ trợ
@Composable
fun RowItemCheckbox(checked: Boolean, onCheckedChange: (Boolean) -> Unit, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { onCheckedChange(!checked) }) {
        Icon(imageVector = if (checked) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, fontSize = 14.sp, color = Color.Gray)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(value: String, onValueChange: (String) -> Unit, placeholder: String, icon: androidx.compose.ui.graphics.vector.ImageVector? = null, isPassword: Boolean = false, isPasswordVisible: Boolean = false, onVisibilityChange: () -> Unit = {}, keyboardType: KeyboardType = KeyboardType.Text) {
    val containerColor = Color(0xFFFBE9E7)
    TextField(
        value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(focusedContainerColor = containerColor, unfocusedContainerColor = containerColor, disabledContainerColor = containerColor, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
        leadingIcon = if (icon != null) { { Icon(imageVector = icon, contentDescription = null, tint = PrimaryOrange) } } else null,
        trailingIcon = if (isPassword) { { IconButton(onClick = onVisibilityChange) { Icon(imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, contentDescription = null, tint = Color.Gray) } } } else null,
        placeholder = { Text(placeholder, color = Color.Gray.copy(alpha = 0.6f)) }, singleLine = true,
        visualTransformation = if (isPassword && !isPasswordVisible) androidx.compose.ui.text.input.PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}