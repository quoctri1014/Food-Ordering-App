package com.example.foodapp.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.foodapp.R
import com.example.foodapp.data.FirestoreHelper
import com.example.foodapp.navigation.Screen
import com.example.foodapp.ui.theme.PrimaryOrange
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseAuth
// ⭐ THÊM CÁC IMPORT ĐỂ XỬ LÝ LỖI ⭐
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.FirebaseNetworkException

val TextFieldBackground = Color(0xFFFBE9E7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    var isLoading by remember { mutableStateOf(false) }

    // CẤU HÌNH GOOGLE
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("753450806986-2o2bjqo1bv9gbq0v0htue44up7e03fr9.apps.googleusercontent.com")
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            isLoading = true
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.result
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        isLoading = false
                        if (task.isSuccessful) {
                            auth.currentUser?.let { FirestoreHelper.syncUser(it) }
                            Toast.makeText(context, "Google Login thành công!", Toast.LENGTH_SHORT).show()
                            navController.navigate(Screen.LoginSuccess) { popUpTo(Screen.Login) { inclusive = true } }
                        } else {
                            Toast.makeText(context, "Lỗi Google: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } catch (e: Exception) {
                isLoading = false
                Toast.makeText(context, "Google Sign In thất bại", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            Image(painter = painterResource(id = R.drawable.profile_burger), contentDescription = "Logo", modifier = Modifier.size(100.dp), contentScale = ContentScale.Fit)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "BURGERKING", fontSize = 28.sp, fontWeight = FontWeight.Black, color = PrimaryOrange, letterSpacing = 1.sp)
            Text(text = "Food Ordering App", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(40.dp))
            Text(text = "Đăng Nhập", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(20.dp))

            // Email
            TextField(
                value = email, onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(focusedContainerColor = TextFieldBackground, unfocusedContainerColor = TextFieldBackground, disabledContainerColor = TextFieldBackground, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                leadingIcon = { Icon(Icons.Default.MailOutline, contentDescription = null, tint = PrimaryOrange) },
                placeholder = { Text("Email", color = PrimaryOrange.copy(0.7f)) },
                singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password
            TextField(
                value = password, onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(focusedContainerColor = TextFieldBackground, unfocusedContainerColor = TextFieldBackground, disabledContainerColor = TextFieldBackground, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryOrange) },
                placeholder = { Text("Password", color = PrimaryOrange.copy(0.7f)) },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, contentDescription = null, tint = PrimaryOrange)
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Hoặc tiếp tục với", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))

            // Nút Social
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                SocialLoginButton(
                    modifier = Modifier.widthIn(min = 200.dp),
                    iconRes = R.drawable.ic_google,
                    text = "Google",
                    onClick = { googleLauncher.launch(googleSignInClient.signInIntent) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navController.navigate(Screen.ForgotPassword) }) {
                Text("Quên mật khẩu?", color = PrimaryOrange, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        isLoading = true
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                isLoading = false
                                if (task.isSuccessful) {
                                    auth.currentUser?.let { FirestoreHelper.syncUser(it) }
                                    Toast.makeText(context, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                                    navController.navigate(Screen.LoginSuccess) { popUpTo(Screen.Login) { inclusive = true } }
                                } else {
                                    // ⭐ XỬ LÝ LỖI TIẾNG VIỆT TẠI ĐÂY ⭐
                                    val errorMessage = when (task.exception) {
                                        is FirebaseAuthInvalidUserException -> "Tài khoản không tồn tại hoặc đã bị xóa."
                                        is FirebaseAuthInvalidCredentialsException -> "Sai email hoặc mật khẩu. Vui lòng kiểm tra lại."
                                        is FirebaseNetworkException -> "Lỗi kết nối mạng. Vui lòng kiểm tra Wifi/3G."
                                        else -> "Đăng nhập thất bại. Vui lòng thử lại."
                                    }
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(context, "Vui lòng nhập Email và Password", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                 if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                 else Text(text = "Đăng Nhập", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 32.dp).clickable { navController.navigate(Screen.SignUp) }
            ) {
                Text(text = "Chưa có tài khoản? ", color = Color.Gray, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                Text(text = "Đăng ký ngay", color = PrimaryOrange, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
            }
        }
    }
}

@Composable
fun SocialLoginButton(modifier: Modifier = Modifier, iconRes: Int, text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(0.dp),
        border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(Color(0xFFEEEEEE)))
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Image(painter = painterResource(id = iconRes), contentDescription = text, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}