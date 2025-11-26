package com.example.foodapp.navigation

import android.app.Activity
import android.content.Context
import android.location.Geocoder
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.foodapp.data.*
import com.example.foodapp.data.model.PaymentInfo
import com.example.foodapp.data.model.PaymentMethod
import com.example.foodapp.ui.components.BottomNavBar
import com.example.foodapp.ui.components.ChatOverlay
import com.example.foodapp.ui.screens.*
import com.example.foodapp.ui.screens.profile.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

// --- HÀM WRAPPER CHO ROOT SCREEN (BOTTOM NAV) ---
@Composable
fun RootScreenWrapper(
    mainNavController: NavHostController,
    foods: List<Food>,
    savedFoods: MutableList<String>,
    cartItemsList: MutableList<CartItem>,
    lastOrderItems: MutableList<CartItem>,
    lastPaymentInfo: MutableState<PaymentInfo?>,
    onAddToCart: (Food, Int, String) -> Unit,
    onUpdateCart: (CartItem, Int) -> Unit,
    onToggleSaved: (Food) -> Unit,
    onBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onOrderCompleted: (PaymentInfo) -> Unit,
    onCartCheckout: (List<CartItem>) -> Unit
) {
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavBar(navController = bottomNavController)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            NavHost(
                navController = bottomNavController,
                startDestination = Screen.Home
            ) {
                composable(Screen.Home) {
                    HomeScreen(
                        onViewAllClick = { mainNavController.navigate(Screen.FoodList) },
                        onFoodClick = onNavigateToDetail,
                        foods = foods
                    )
                }
                composable(Screen.Favorites) {
                    val savedItems = foods.filter { savedFoods.contains(it.id) }
                    FavoritesScreen(
                        savedFoods = savedItems,
                        onDetailClick = { food -> mainNavController.navigate(Screen.createRoute(food.id)) },
                        onToggleSaved = onToggleSaved
                    )
                }
                composable(Screen.Cart) {
                    OrderScreen(
                        initialCartItems = cartItemsList,
                        onCheckoutClick = { items ->
                            onCartCheckout(items)
                        },
                        onBackClick = onBack,
                        onNavigateToDetail = { mainNavController.navigate(it) },
                        onUpdateCart = onUpdateCart
                    )
                }

                composable(Screen.Profile) {
                    ProfileScreen { route ->
                        if (route == "logout") {
                            FirebaseAuth.getInstance().signOut()
                        } else {
                            mainNavController.navigate(route)
                        }
                    }
                }
            }

            ChatOverlay(
                onClick = { mainNavController.navigate("chat_screen") }
            )
        }
    }
}

// ⭐ HÀM GEOCODING ⭐
suspend fun geocodeAddress(context: Context, address: String): Pair<Double, Double>? {
    return suspendCoroutine { continuation ->
        try {
            @Suppress("DEPRECATION")
            val geocoder = Geocoder(context, Locale.getDefault())
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocationName(address, 1)

            if (!addresses.isNullOrEmpty()) {
                val lat = addresses[0].latitude
                val lon = addresses[0].longitude
                continuation.resume(Pair(lat, lon))
            } else {
                continuation.resume(null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            continuation.resume(null)
        }
    }
}

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val scope = rememberCoroutineScope()

    val foods: List<Food> = MockData.mockFoods
    val savedFoods = remember { mutableStateListOf<String>() }

    val cartItemsList = remember { mutableStateListOf<CartItem>() }
    val buyNowItems = remember { mutableStateListOf<CartItem>() }
    val lastOrderItems = remember { mutableStateListOf<CartItem>() }

    var isBuyingFromCart by remember { mutableStateOf(true) }

    val lastPaymentInfo = remember { mutableStateOf<PaymentInfo?>(null) }
    var currentPaymentMethod by remember { mutableStateOf(PaymentMethod.COD) }

    val currentUser = remember { mutableStateOf<User?>(null) }

    // ⭐ XỬ LÝ INTENT TỪ THÔNG BÁO (NOTIFICATION) ⭐
    val activity = context as? Activity
    // Lắng nghe thay đổi của Intent (khi App nhận NewIntent)
    // Cần kiểm tra liên tục hoặc khi recompose
    val currentIntent = activity?.intent

    LaunchedEffect(currentIntent) {
        if (currentIntent?.getStringExtra("navigate_to") == "notification_detail") {
            val title = currentIntent.getStringExtra("title") ?: "Thông báo"
            val body = currentIntent.getStringExtra("body") ?: "..."

            // Xóa flag để không navigate lại khi xoay màn hình
            currentIntent.removeExtra("navigate_to")

            // Navigate đến màn hình chi tiết
            navController.navigate("notification_detail_screen/$title/$body")
        }
    }

    // ⭐ LẮNG NGHE TRẠNG THÁI ĐĂNG NHẬP ⭐
    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                FirestoreHelper.getCart { items -> cartItemsList.clear(); cartItemsList.addAll(items) }
                FirestoreHelper.getFavorites { ids -> savedFoods.clear(); savedFoods.addAll(ids) }
                scope.launch { currentUser.value = FirestoreHelper.getUserProfile(user.uid) }
            } else {
                cartItemsList.clear()
                savedFoods.clear()
                lastOrderItems.clear()
                lastPaymentInfo.value = null
                currentUser.value = null

                val currentRoute = navController.currentBackStackEntry?.destination?.route
                if (currentRoute != Screen.Login && currentRoute != Screen.Onboarding && currentRoute != Screen.Splash) {
                    navController.navigate(Screen.Login) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }
        auth.addAuthStateListener(listener)
        onDispose { auth.removeAuthStateListener(listener) }
    }

    // ⭐ LOGIC THANH TOÁN & GEOCODING ⭐
    val proceedToCheckout: (List<CartItem>, Boolean, Int) -> Unit = { items, fromCart, discount ->
        lastOrderItems.clear()
        lastOrderItems.addAll(items)
        isBuyingFromCart = fromCart

        scope.launch {
            val userId = auth.currentUser?.uid
            val userProfile = if (userId != null) FirestoreHelper.getUserProfile(userId) else null
            currentUser.value = userProfile
            val address = userProfile?.address?.trim() ?: ""

            if (address.isNotBlank() && address.length > 5) {
                val coordinates = withContext(Dispatchers.IO) {
                    geocodeAddress(context, address)
                }
                val lat = coordinates?.first ?: 0.0
                val lon = coordinates?.second ?: 0.0

                navController.navigate(Screen.createConfirmOrderRoute(address, lat, lon, discount))
            } else {
                navController.navigate("${Screen.AddressInput}?isCheckout=true")
            }
        }
    }

    val onAddToCart: (Food, Int, String) -> Unit = { food, qty, note ->
        val existing = cartItemsList.find { it.food.id == food.id }
        if (existing != null) {
            val idx = cartItemsList.indexOf(existing)
            val newNote = if (note.isNotBlank()) note else existing.note
            val newItem = existing.copy(quantity = existing.quantity + qty, note = newNote)
            cartItemsList[idx] = newItem
            FirestoreHelper.updateCartItem(newItem)
        } else {
            val newItem = CartItem(food, qty, note)
            cartItemsList.add(newItem)
            FirestoreHelper.updateCartItem(newItem)
        }
    }

    val onUpdateCart: (CartItem, Int) -> Unit = { cartItem, change ->
        val existing = cartItemsList.find { it.food.id == cartItem.food.id }
        if (existing != null) {
            val newQty = existing.quantity + change
            if (newQty <= 0) {
                cartItemsList.remove(existing)
                FirestoreHelper.removeCartItem(existing.food.id)
            } else {
                val idx = cartItemsList.indexOf(existing)
                val newItem = existing.copy(quantity = newQty)
                cartItemsList[idx] = newItem
                FirestoreHelper.updateCartItem(newItem)
            }
        }
    }
    val onUpdateBuyNow: (CartItem, Int) -> Unit = { cartItem, change ->
        val existing = buyNowItems.find { it.food.id == cartItem.food.id }
        if (existing != null) {
            val newQty = existing.quantity + change
            if (newQty <= 0) {
                buyNowItems.remove(existing)
                if (buyNowItems.isEmpty()) navController.popBackStack()
            } else {
                val idx = buyNowItems.indexOf(existing)
                buyNowItems[idx] = existing.copy(quantity = newQty)
            }
        }
    }
    val onToggleSaved: (Food) -> Unit = { food ->
        if (savedFoods.contains(food.id)) {
            savedFoods.remove(food.id)
            FirestoreHelper.toggleFavorite(food, false)
        } else {
            savedFoods.add(food.id)
            FirestoreHelper.toggleFavorite(food, true)
        }
    }

    val onBack: () -> Unit = { navController.popBackStack() }
    val onNavigateToDetail: (String) -> Unit = { foodId -> navController.navigate(Screen.createRoute(foodId)) }

    val onOrderCompleted: (PaymentInfo) -> Unit = { info ->
        lastPaymentInfo.value = info
        val subtotal = lastOrderItems.sumOf { it.food.price * it.quantity }
        scope.launch {
            FirestoreHelper.saveOrder(lastOrderItems.toList(), info, subtotal)
            if (isBuyingFromCart) {
                cartItemsList.clear()
                FirestoreHelper.clearCart()
            }
            navController.navigate(Screen.OrderSuccess)
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash,
        modifier = modifier
    ) {
        composable(Screen.Splash) { SplashScreen(navController = navController) }
        composable(Screen.Onboarding) { OnboardingScreen(navController = navController) }
        composable(Screen.Login) { LoginScreen(navController = navController) }
        composable(Screen.SignUp) { SignUpScreen(navController = navController) }
        composable(Screen.LoginSuccess) { LoginSuccessScreen(navController = navController) }
        composable(Screen.FillBio) { FillBioScreen(navController = navController) }

        composable(
            route = "${Screen.AddressInput}?isCheckout={isCheckout}",
            arguments = listOf(
                navArgument("isCheckout") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { entry ->
            val isCheckout = entry.arguments?.getBoolean("isCheckout") ?: false
            AddressInputScreen(navController = navController, isCheckout = isCheckout)
        }

        composable(Screen.ForgotPassword) { ForgotPasswordScreen(navController = navController) }

        // OTP Screens
        composable(
            route = "otp_verify_screen/{type}/{contact}/{verificationId}",
            arguments = listOf(
                navArgument("type") { type = NavType.StringType },
                navArgument("contact") { type = NavType.StringType },
                navArgument("verificationId") { type = NavType.StringType }
            )
        ) { entry ->
            val type = entry.arguments?.getString("type") ?: "email"
            val contact = entry.arguments?.getString("contact") ?: ""
            val verId = entry.arguments?.getString("verificationId") ?: ""
            OtpVerifyScreen(navController, type, contact, verId)
        }
        composable(
            route = "reset_password_screen/{email}/{otp}",
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("otp") { type = NavType.StringType }
            )
        ) { entry ->
            val email = entry.arguments?.getString("email") ?: ""
            val otp = entry.arguments?.getString("otp") ?: ""
            ResetPasswordScreen(navController, email, otp)
        }

        composable(Screen.Cart) {
            navController.navigate(Screen.Root) { popUpTo(Screen.Home) }
        }

        navigation(startDestination = Screen.Home, route = Screen.Root) {
            composable(Screen.Home) {
                RootScreenWrapper(
                    mainNavController = navController,
                    foods = foods,
                    savedFoods = savedFoods,
                    cartItemsList = cartItemsList,
                    lastOrderItems = lastOrderItems,
                    lastPaymentInfo = lastPaymentInfo,
                    onAddToCart = onAddToCart,
                    onUpdateCart = onUpdateCart,
                    onToggleSaved = onToggleSaved,
                    onBack = onBack,
                    onNavigateToDetail = onNavigateToDetail,
                    onOrderCompleted = onOrderCompleted,
                    onCartCheckout = { items ->
                        buyNowItems.clear()
                        buyNowItems.addAll(items)
                        isBuyingFromCart = true
                        navController.navigate(Screen.BuyNowSummary)
                    }
                )
            }
            composable(Screen.Profile) {
                ProfileScreen { route ->
                    if (route == "logout") {
                        FirebaseAuth.getInstance().signOut()
                    } else {
                        navController.navigate(route)
                    }
                }
            }
        }

        composable(Screen.FoodList) {
            val totalItems by remember { derivedStateOf { cartItemsList.sumOf { it.quantity } } }
            ListScreen(navController, foods, onDetailClick = onNavigateToDetail, onToggleSaved = onToggleSaved, savedFoodIds = savedFoods, cartItemCount = totalItems)
        }

        composable(Screen.FoodDetail) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("foodId") ?: ""
            val foodDetail = foods.find { it.id == id }
            val totalItems by remember { derivedStateOf { cartItemsList.sumOf { it.quantity } } }

            foodDetail?.let { currentFood ->
                ProductDetailScreen(
                    food = currentFood,
                    onBackClick = onBack,
                    onAddItemToCart = { food, qty, note -> onAddToCart(food, qty, note) },
                    onNavigateToCart = {
                        navController.navigate(Screen.Cart) {
                            popUpTo(Screen.Root) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    onToggleSaved = { onToggleSaved(currentFood) },
                    isSaved = savedFoods.contains(currentFood.id),
                    onBuyNow = { food, qty, note ->
                        buyNowItems.clear()
                        buyNowItems.add(CartItem(food, qty, note))
                        isBuyingFromCart = false
                        navController.navigate(Screen.BuyNowSummary)
                    },
                    cartItemCount = totalItems
                )
            }
        }

        composable(Screen.OrderSummary) {
            OrderScreen(
                initialCartItems = cartItemsList,
                onCheckoutClick = { items -> proceedToCheckout(items, true, 0) },
                onBackClick = onBack,
                onNavigateToDetail = onNavigateToDetail,
                onUpdateCart = onUpdateCart
            )
        }

        composable(Screen.BuyNowSummary) {
            BuyNowScreen(
                buyNowItems = buyNowItems,
                onConfirmClick = { items, discount ->
                    proceedToCheckout(items, isBuyingFromCart, discount)
                },
                onBackClick = onBack,
                onNavigateToDetail = onNavigateToDetail,
                onUpdateItem = onUpdateBuyNow
            )
        }

        composable(
            route = Screen.ConfirmOrderRoute,
            arguments = listOf(
                navArgument("address") { type = NavType.StringType },
                navArgument("customerLat") { type = NavType.FloatType },
                navArgument("customerLon") { type = NavType.FloatType },
                navArgument("discount") { type = NavType.IntType }
            )
        ) { entry ->
            val customerAddress = entry.arguments?.getString("address") ?: ""
            val lat = entry.arguments?.getFloat("customerLat")?.toDouble() ?: 0.0
            val lon = entry.arguments?.getFloat("customerLon")?.toDouble() ?: 0.0
            val discount = entry.arguments?.getInt("discount") ?: 0

            ConfirmOrderScreen(
                initialCartItems = lastOrderItems.toList(),
                customerAddress = customerAddress,
                customerLat = lat,
                customerLon = lon,
                discountAmount = discount,
                selectedPaymentMethod = currentPaymentMethod,
                onBackClick = onBack,
                onEditAddressClick = { navController.navigate("${Screen.AddressInput}?isCheckout=true") },
                onEditPaymentClick = { navController.navigate(Screen.PaymentSelection) },
                onConfirmOrder = { finalTotal ->
                    val total = finalTotal
                    val user = currentUser.value
                    val fullName = user?.username ?: user?.email ?: "Khách hàng"
                    val phone = user?.phoneNumber ?: ""

                    if (currentPaymentMethod == PaymentMethod.COD) {
                        val shippingFee = total - (lastOrderItems.sumOf { it.food.price * it.quantity } - discount)
                        val info = PaymentInfo(fullName = fullName, phone = phone, address = customerAddress, method = currentPaymentMethod, shippingFee = shippingFee)
                        onOrderCompleted(info)
                    } else {
                        val encodedName = URLEncoder.encode(fullName, StandardCharsets.UTF_8.toString())
                        navController.navigate(Screen.QrDetailRoute.replace("{methodId}", currentPaymentMethod.methodId).replace("{finalTotalAmount}", total.toString()).replace("{customerName}", encodedName))
                    }
                }
            )
        }

        composable(Screen.PaymentSelection) {
            PaymentSelectionScreen(onBackClick = onBack, onPaymentMethodSelected = { method -> currentPaymentMethod = method; navController.popBackStack() })
        }

        composable(route = Screen.PaymentMethodRoute, arguments = listOf(navArgument("finalTotal") { type = NavType.IntType })) { entry ->
            val subtotal = entry.arguments?.getInt("finalTotal") ?: 0
            PaymentMethodScreen(initialSubtotalAmount = subtotal, onOrderCompleted = onOrderCompleted, onBackClick = onBack, onNavigateToQrDetail = { methodId, amount, name -> navController.navigate(Screen.QrDetailRoute.replace("{methodId}", methodId).replace("{finalTotalAmount}", amount.toString()).replace("{customerName}", name)) }, onTempPaymentInfoSaved = { info -> lastPaymentInfo.value = info })
        }

        composable(route = Screen.QrDetailRoute, arguments = listOf(navArgument("methodId") { type = NavType.StringType }, navArgument("finalTotalAmount") { type = NavType.IntType }, navArgument("customerName") { type = NavType.StringType })) { entry ->
            val methodId = entry.arguments?.getString("methodId") ?: "COD"
            val total = entry.arguments?.getInt("finalTotalAmount") ?: 0
            val name = entry.arguments?.getString("customerName") ?: ""
            QrPaymentDetailScreen(methodId = methodId, finalTotalAmount = total, customerName = name, onOrderCompleted = { val method = PaymentMethod.entries.find { it.methodId == methodId } ?: PaymentMethod.COD; val user = currentUser.value; val info = PaymentInfo(fullName = name, phone = user?.phoneNumber ?: "", address = user?.address ?: "Đã lưu trong hồ sơ", method = method); onOrderCompleted(info) }, onBackClick = onBack)
        }

        composable(Screen.OrderSuccess) {
            OrderSuccessScreen(onFinishClick = { navController.navigate(Screen.OrderTracking) { popUpTo(Screen.Root) { inclusive = false } } })
        }

        // Các màn hình phụ
        composable("edit_profile") { EditProfileScreen(onBack) }
        composable("order_history") { OrderHistoryScreen(onBack) }
        composable("delivery_address") { DeliveryAddressScreen(onBack = onBack, onEditClick = { navController.navigate(Screen.AddressInput) }) }
        composable("payment_management") { PaymentManagementScreen(onBack) }
        composable("voucher") { VoucherScreen(onBack) }
        composable("security") { SecurityScreen(onBack) }
        composable("app_settings") { AppSettingsScreen(onBack) }
        composable("support") { SupportScreen(onBack) }
        composable("chat_screen") { ChatScreen(onBackPressed = { navController.popBackStack() }) }
        composable(Screen.OrderTracking) { OrderTrackingScreen(cartItems = lastOrderItems, paymentInfo = lastPaymentInfo.value ?: PaymentInfo(), onNavigateToHome = { navController.navigate(Screen.Root) { popUpTo(0) } }) }

        // ⭐ MÀN HÌNH CHI TIẾT THÔNG BÁO (MỚI) ⭐
        composable(
            route = "notification_detail_screen/{title}/{body}",
            arguments = listOf(
                navArgument("title") { type = NavType.StringType },
                navArgument("body") { type = NavType.StringType }
            )
        ) { entry ->
            val title = entry.arguments?.getString("title") ?: ""
            val body = entry.arguments?.getString("body") ?: ""

            NotificationDetailScreen(
                title = title,
                body = body,
                onBackClick = {
                    navController.navigate(Screen.Root) {
                        popUpTo(Screen.Root) { inclusive = true }
                    }
                }
            )
        }
    }
}