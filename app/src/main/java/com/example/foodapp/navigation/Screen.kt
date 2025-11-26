package com.example.foodapp.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object Screen {
    const val Splash = "splash_screen"
    const val Onboarding = "onboarding_route"
    const val Login = "login_route"
    const val SignUp = "signup_route"
    const val LoginSuccess = "login_success_route"
    const val FillBio = "fillbio_route"

    const val Root = "root_graph"

    const val Home = "home_route"
    const val FoodList = "food_list_route"
    const val Cart = "cart_route"
    const val Favorites = "favorites_route"
    const val Profile = "profile_route"

    const val FoodDetail = "food_detail/{foodId}"
    const val OrderSummary = "order_summary_route"
    const val BuyNowSummary = "buy_now_summary_route"

    const val OrderTracking = "order_tracking_route"
    const val PaymentMethodRoute = "payment_method/{finalTotal}"
    const val QrDetailRoute = "qr_detail/{methodId}/{finalTotalAmount}/{customerName}"
    const val OrderSuccess = "order_success_route"

    const val ForgotPassword = "forgot_password"

    const val AddressInput = "address_input_route"
    const val OrderConfirmation = "order_confirmation_route"

    // ⭐ SỬA: Thêm tham số {discount} vào cuối route
    const val ConfirmOrderRoute = "confirm_order_route/{address}/{customerLat}/{customerLon}/{discount}"

    const val PaymentSelection = "payment_selection_route"

    const val EditProfile = "edit_profile"
    const val OrderHistory = "order_history"
    const val DeliveryAddress = "delivery_address"
    const val PaymentManagement = "payment_management"
    const val Voucher = "voucher"
    const val Security = "security"
    const val AppSettings = "app_settings"
    const val Logout = "logout"

    fun createRoute(foodId: String) = "food_detail/$foodId"

    // ⭐ SỬA: Thêm tham số discount vào hàm tạo route (Mặc định = 0)
    fun createConfirmOrderRoute(address: String, lat: Double, lon: Double, discount: Int = 0): String {
        val encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8.toString())
        // Thêm discount vào cuối chuỗi URL
        return "confirm_order_route/$encodedAddress/$lat/$lon/$discount"
    }
}