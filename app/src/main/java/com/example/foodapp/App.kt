package com.example.foodapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.foodapp.navigation.NavGraph
import com.example.foodapp.ui.components.BottomNavBar
import com.example.foodapp.data.model.Screen

@Composable
fun App() {
    val navController = rememberNavController()

    val bottomBarRoutes = listOf(
        Screen.Home.route,
        Screen.Cart.route,
        Screen.Favorites.route,
        Screen.Profile.route
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ""

    val showBottomBar = bottomBarRoutes.any { currentRoute.startsWith(it) }

    Scaffold(
        bottomBar = {
            if (showBottomBar) BottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        NavGraph(navController = navController, modifier = Modifier.padding(innerPadding))
    }
}
