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

import com.example.foodapp.data.model.Screen
import com.example.foodapp.ui.theme.PrimaryOrange

// Định nghĩa các mục cho Bottom Bar
data class BottomBarScreen(val route: String, val label: String, val icon: ImageVector)

val BottomBarItems = listOf(
    BottomBarScreen(Screen.Home.route, "Trang Chủ", Icons.Default.Home),
    BottomBarScreen(Screen.Favorites.route, "Yêu Thích", Icons.Default.Favorite),
    BottomBarScreen(Screen.Cart.route, "Giỏ Hàng", Icons.Default.ShoppingCart),
    BottomBarScreen(Screen.Profile.route, "Tài Khoản", Icons.Default.Person)
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

            // Logic khớp route
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
                    navController.navigate(screen.route) {

                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }

                        launchSingleTop = true
                        // Khôi phục trạng thái khi điều hướng lại
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