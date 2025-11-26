package com.example.foodapp

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController

import com.example.foodapp.navigation.NavGraph
import com.example.foodapp.ui.theme.FoodAppTheme
// import com.facebook.CallbackManager đã bị xóa

class MainActivity : ComponentActivity() {

    // ⭐ BỊ XÓA: companion object liên quan đến Facebook Callback Manager ⭐

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // BỊ XÓA: Dòng khởi tạo facebookCallbackManager = CallbackManager.Factory.create()

        // Cho phép vẽ đè lên system bars (status bar, navigation bar)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            FoodAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Khởi tạo NavController
                    val navController = rememberNavController()

                    // Gọi hàm xin quyền
                    RequestNotificationPermission()

                    // Gọi NavGraph chính
                    NavGraph(navController = navController)
                }
            }
        }
    }

    // BỊ XÓA: Ghi đè hàm onActivityResult()
}

// --- HÀM XỬ LÝ XIN QUYỀN THÔNG BÁO (ANDROID 13+) ---
@Composable
fun RequestNotificationPermission() {
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // Quyền đã được cấp
            } else {
                // Người dùng từ chối
            }
        }
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}