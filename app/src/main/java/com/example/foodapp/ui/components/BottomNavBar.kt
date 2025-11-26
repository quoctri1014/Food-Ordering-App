package com.example.foodapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavDestination.Companion.hierarchy

import com.example.foodapp.navigation.Screen // Import đúng Screen object
import com.example.foodapp.ui.theme.PrimaryOrange

// Định nghĩa các mục cho Bottom Bar
data class BottomBarScreen(val route: String, val label: String, val icon: ImageVector)

// CHÍNH XÁC: Sử dụng các hằng số string route từ Screen object
val BottomBarItems = listOf(
    BottomBarScreen(Screen.Home, "Trang Chủ", Icons.Default.Home),
    BottomBarScreen(Screen.Favorites, "Yêu Thích", Icons.Default.Favorite),
    BottomBarScreen(Screen.Cart, "Giỏ Hàng", Icons.Default.ShoppingCart),
    BottomBarScreen(Screen.Profile, "Tài Khoản", Icons.Default.Person)
)

@Composable
fun BottomNavBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val selectedColor = PrimaryOrange
    val unselectedColor = Color.Gray

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp
    ) {
        BottomBarItems.forEach { screen ->

            // Logic khớp route: So sánh route hiện tại với route được định nghĩa
            val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.label
                    )
                },
                label = {
                    Text(
                        screen.label,
                        color = if (isSelected) selectedColor else unselectedColor,
                        fontSize = 12.sp
                    )
                },
                selected = isSelected,
                onClick = {
                    // Navigate bằng hằng số route chính xác
                    navController.navigate(screen.route) {

                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }

                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = selectedColor,
                    unselectedIconColor = unselectedColor,
                    indicatorColor = Color.Transparent,
                    selectedTextColor = selectedColor,
                    unselectedTextColor = unselectedColor
                )
            )
        }
    }
}