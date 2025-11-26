package com.example.foodapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.foodapp.navigation.NavGraph
import com.example.foodapp.ui.theme.FoodAppTheme

@Composable
fun App() {
    FoodAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            // Khởi tạo NavController
            val navController = rememberNavController()

            // Gọi NavGraph chính
            // (Lưu ý: BottomBar đã được xử lý tự động bên trong NavGraph -> RootScreenWrapper
            // nên chúng ta không cần Scaffold hay logic kiểm tra route ở đây nữa)
            NavGraph(navController = navController)
        }
    }
}