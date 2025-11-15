package com.example.foodapp.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType


import com.example.foodapp.data.* // Food, CartItem, mockFoods
import com.example.foodapp.data.model.Screen // Screen, Home, Cart, etc.
import com.example.foodapp.data.model.PaymentInfo // PaymentInfo
import com.example.foodapp.data.model.PaymentMethod // PaymentMethod

import com.example.foodapp.ui.screens.*
import com.example.foodapp.ui.screens.profile.* // Đảm bảo đã import các màn hình profile còn lại

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {

    val foods: List<Food> = mockFoods
    // ⭐ GIỎ HÀNG CHUNG: Dữ liệu được lưu trữ ở đây, đảm bảo không bị mất khi thoát màn hình
    val savedFoods = remember { mutableStateListOf<String>() }
    val cartItemsList = remember { mutableStateListOf<CartItem>() }

    val lastOrderItems = remember { mutableStateListOf<CartItem>() }
    val lastPaymentInfo = remember { mutableStateOf<PaymentInfo?>(null) }

    val onAddToCart: (Food, Int) -> Unit = { food, qty ->
        val existing = cartItemsList.find { it.food.id == food.id }
        if (existing != null) {
            val idx = cartItemsList.indexOf(existing)
            cartItemsList[idx] = existing.copy(quantity = existing.quantity + qty)
        } else {
            cartItemsList.add(CartItem(food = food, quantity = qty))
        }
    }

    // ⭐ LOGIC CẬP NHẬT: Thay đổi trực tiếp cartItemsList
    val onUpdateCart: (CartItem, Int) -> Unit = { cartItem, change ->
        val existing = cartItemsList.find { it.food.id == cartItem.food.id }
        if (existing != null) {
            val newQty = existing.quantity + change
            if (newQty <= 0) cartItemsList.remove(existing)
            else {
                val idx = cartItemsList.indexOf(existing)
                cartItemsList[idx] = existing.copy(quantity = newQty)
            }
        }
    }

    val onToggleSaved: (Food) -> Unit = { food ->
        if (savedFoods.contains(food.id)) savedFoods.remove(food.id)
        else savedFoods.add(food.id)
    }

    // --- CALLBACKS ĐIỀU HƯỚNG CHUNG ---
    val onBack: () -> Unit = { navController.popBackStack() }
    val onNavigateToHome: () -> Unit = {
        navController.navigate(Screen.Home.route) {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
        }
    }

    val onNavigateToQrDetail: (String, Int, String) -> Unit = { methodId, total, name ->
        navController.navigate(Screen.QrDetailRoute.route
            .replace("{methodId}", methodId)
            .replace("{finalTotalAmount}", total.toString())
            .replace("{customerName}", name)
        )
    }

    // Hàm điều hướng chi tiết món ăn (chỉ cần ID)
    val onNavigateToDetail: (String) -> Unit = { foodId ->
        navController.navigate(Screen.FoodDetail.createRoute(foodId))
    }

    val onTempPaymentInfoSaved: (PaymentInfo) -> Unit = { info ->
        lastPaymentInfo.value = info
    }

    val processOrderToPayment: (List<CartItem>) -> Unit = { itemsToOrder ->
        lastOrderItems.clear()
        lastOrderItems.addAll(itemsToOrder)

        val subtotal = itemsToOrder.sumOf { it.food.price * it.quantity }
        val shippingFee = if (subtotal > 0) 15000 else 0 // Phí ship chỉ tính khi có món
        val finalTotal = subtotal + shippingFee

        navController.navigate(Screen.PaymentMethodRoute.route.replace("{finalTotal}", finalTotal.toString())) {
            launchSingleTop = true
        }
    }

    val onOrderCompleted: (PaymentInfo) -> Unit = { info ->
        lastPaymentInfo.value = info
        cartItemsList.clear() // Xóa giỏ hàng chung khi đặt hàng thành công
        navController.navigate(Screen.OrderTracking.route) {
            popUpTo(Screen.Home.route) { inclusive = false }
        }
    }

    val onBuyNow: (Food, Int) -> Unit = { food, qty ->
        val singleItemOrder = listOf(CartItem(food = food, quantity = qty))
        processOrderToPayment(singleItemOrder)
    }

    // --- NAV HOST START ---

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {

        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController,
                foods = foods,
                onDetailClick = { food -> onNavigateToDetail(food.id) },
                onToggleSaved = onToggleSaved,
                savedFoodIds = savedFoods
            )
        }

        // ⭐ Dùng OrderScreen vì nó có logic chỉnh sửa số lượng và ghi chú
        composable(Screen.Cart.route) {
            OrderScreen(
                initialCartItems = cartItemsList,
                onCheckoutClick = { items -> processOrderToPayment(items) },
                onBackClick = onBack,
                onNavigateToDetail = onNavigateToDetail,
                onUpdateCart = onUpdateCart // Truyền callback cập nhật giỏ hàng chung
            )
        }

        composable(
            route = Screen.PaymentMethodRoute.route,
            arguments = listOf(navArgument("finalTotal") { type = NavType.IntType })
        ) { backStackEntry ->
            val total = backStackEntry.arguments?.getInt("finalTotal") ?: 0
            PaymentMethodScreen(
                finalTotalAmount = total,
                onOrderCompleted = onOrderCompleted,
                onBackClick = onBack,
                onNavigateToQrDetail = onNavigateToQrDetail,
                onTempPaymentInfoSaved = onTempPaymentInfoSaved
            )
        }

        composable(
            route = Screen.QrDetailRoute.route,
            arguments = listOf(
                navArgument("methodId") { type = NavType.StringType },
                navArgument("finalTotalAmount") { type = NavType.IntType },
                navArgument("customerName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val methodId = backStackEntry.arguments?.getString("methodId") ?: PaymentMethod.COD.methodId
            val total = backStackEntry.arguments?.getInt("finalTotalAmount") ?: 0
            val name = backStackEntry.arguments?.getString("customerName") ?: ""

            QrPaymentDetailScreen(
                methodId = methodId,
                finalTotalAmount = total,
                customerName = name,
                onOrderCompleted = {
                    val currentMethod = PaymentMethod.entries.find { it.methodId == methodId } ?: PaymentMethod.COD
                    val finalInfo = lastPaymentInfo.value?.copy(method = currentMethod) ?: PaymentInfo(fullName = name, method = currentMethod)
                    onOrderCompleted(finalInfo)
                },
                onBackClick = onBack
            )
        }

        composable(Screen.OrderTracking.route) {
            OrderTrackingScreen(
                cartItems = lastOrderItems,
                paymentInfo = lastPaymentInfo.value ?: PaymentInfo(),
                onNavigateToHome = onNavigateToHome
            )
        }

        // --- Màn hình Yêu thích (Favorites) ---
        composable(Screen.Favorites.route) {
            val savedFoodItems = foods.filter { savedFoods.contains(it.id) }
            FavoritesScreen(
                savedFoods = savedFoodItems,
                onDetailClick = { food -> onNavigateToDetail(food.id) },
                onToggleSaved = onToggleSaved
            )
        }

        // --- Các màn hình Profile ---
        composable(Screen.Profile.route) {
            ProfileScreen(onNavigateToScreen = { route -> navController.navigate(route) })
        }
        composable(Screen.EditProfile.route) { EditProfileScreen(onBack = onBack) }
        composable(Screen.OrderHistory.route) { OrderHistoryScreen(onBack = onBack) }
        composable(Screen.DeliveryAddress.route) { DeliveryAddressScreen(onBack = onBack) }
        composable(Screen.PaymentManagement.route) { PaymentManagementScreen(onBack = onBack) }
        composable(Screen.Voucher.route) { VoucherScreen(onBack = onBack) }
        composable(Screen.Security.route) { SecurityScreen(onBack = onBack) }
        composable(Screen.AppSettings.route) { AppSettingsScreen(onBack = onBack) }
        // composable(Screen.Support.route) { SupportScreen(onBack = onBack) }

        composable(Screen.Logout.route) {
            LaunchedEffect(Unit) { onNavigateToHome() }
        }

        composable(Screen.FoodDetail.route) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("foodId") ?: ""
            val foodDetail = foods.find { it.id == id }
            foodDetail?.let { currentFood ->
                ProductDetailScreen(
                    food = currentFood,
                    onBackClick = onBack,
                    onAddItemToCart = onAddToCart,
                    onNavigateToCart = { navController.navigate(Screen.Cart.route) },
                    onToggleSaved = { onToggleSaved(currentFood) },
                    isSaved = savedFoods.contains(currentFood.id),
                    onBuyNow = onBuyNow
                )
            }
        }
    }
}