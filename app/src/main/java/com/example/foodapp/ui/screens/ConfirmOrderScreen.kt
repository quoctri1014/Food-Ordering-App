package com.example.foodapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodapp.data.FirestoreHelper
import com.example.foodapp.data.model.PaymentMethod
import com.example.foodapp.data.model.CartItem
import com.example.foodapp.ui.theme.PrimaryOrange

// Import các hàm tiện ích từ ShippingUtils
import com.example.foodapp.utils.calculateDistanceKm
import com.example.foodapp.utils.calculateShippingFee
import com.example.foodapp.utils.toVND

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmOrderScreen(
    initialCartItems: List<CartItem>,
    customerAddress: String,
    customerLat: Double,
    customerLon: Double,
    discountAmount: Int, // ⭐ Nhận tham số giảm giá từ NavGraph
    selectedPaymentMethod: PaymentMethod,
    onBackClick: () -> Unit,
    onEditAddressClick: () -> Unit,
    onEditPaymentClick: () -> Unit,
    onConfirmOrder: (Int) -> Unit
) {
    // 1. GIẢI MÃ ĐỊA CHỈ
    val decodedAddress = remember(customerAddress) {
        try {
            URLDecoder.decode(customerAddress, StandardCharsets.UTF_8.toString())
        } catch (e: Exception) {
            customerAddress
        }
    }
    val addressDisplay = decodedAddress

    // 2. LẤY THÔNG TIN NGƯỜI DÙNG
    var userName by remember { mutableStateOf("Đang tải...") }
    var userPhone by remember { mutableStateOf("...") }
    val auth = FirebaseAuth.getInstance()

    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val user = FirestoreHelper.getUserProfile(userId)
            if (user != null) {
                userName = user.username.ifBlank { user.email?.substringBefore("@") ?: "Khách hàng" }
                userPhone = user.phoneNumber.ifBlank { "Chưa có SĐT" }
            }
        }
    }

    // 3. TÍNH TOÁN KHOẢNG CÁCH VÀ PHÍ SHIP
    val distanceKm = remember(customerLat, customerLon) {
        calculateDistanceKm(customerLat, customerLon)
    }

    val shippingFee = remember(distanceKm) {
        calculateShippingFee(distanceKm)
    }

    val subtotal = initialCartItems.sumOf { it.food.price * it.quantity }

    // ⭐ TÍNH TỔNG TIỀN SAU KHI TRỪ VOUCHER ⭐
    val finalTotal = (subtotal + shippingFee - discountAmount).coerceAtLeast(0)

    Scaffold(
        topBar = {
             TopAppBar(
                title = { Text("Xác nhận Đơn hàng", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Button(
                onClick = { onConfirmOrder(finalTotal) },
                enabled = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    "Xác Nhận & Thanh Toán (${finalTotal.toVND()})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            Text("Chi tiết đơn hàng", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(30.dp))

            // --- CARD THÔNG TIN NHẬN HÀNG ---
            ConfirmInfoCard(
                label = "Thông tin nhận hàng",
                content = addressDisplay,
                userName = userName,
                userPhone = userPhone,
                icon = {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(PrimaryOrange.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.LocationOn, null, tint = PrimaryOrange)
                    }
                },
                onEditClick = onEditAddressClick
            )

            Spacer(modifier = Modifier.height(20.dp))

            // --- CARD PHƯƠNG THỨC THANH TOÁN ---
            ConfirmInfoCard(
                label = "Phương thức thanh toán",
                content = selectedPaymentMethod.displayName,
                icon = { Text("PAY", fontWeight = FontWeight.Black, color = Color.Blue) },
                onEditClick = onEditPaymentClick
            )

            Spacer(modifier = Modifier.height(30.dp))

            // --- CARD TÓM TẮT CHI PHÍ ---
            CostSummaryCard(
                subtotal = subtotal,
                shippingFee = shippingFee,
                discountAmount = discountAmount, // ⭐ Truyền discount vào Card
                distanceKm = distanceKm,
                finalTotal = finalTotal
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun CostSummaryCard(
    subtotal: Int,
    shippingFee: Int,
    discountAmount: Int, // ⭐ Thêm tham số
    distanceKm: Double,
    finalTotal: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Tóm tắt Chi phí", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Tiền hàng:", color = Color.Gray)
                Text(subtotal.toVND(), fontWeight = FontWeight.SemiBold)
            }

            // ⭐ HIỂN THỊ DÒNG GIẢM GIÁ ⭐
            if (discountAmount > 0) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                    Text("Voucher giảm giá:", color = Color.Gray)
                    Text("-${discountAmount.toVND()}", fontWeight = FontWeight.SemiBold, color = PrimaryOrange)
                }
            }

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                Text("Khoảng cách (Ước tính):", color = Color.Gray)
                Text("${"%.1f".format(distanceKm)} km", fontWeight = FontWeight.SemiBold)
            }

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                Text("Phí giao hàng:", color = Color.Gray)
                Text(shippingFee.toVND(), fontWeight = FontWeight.SemiBold, color = PrimaryOrange)
            }

            Divider(Modifier.padding(vertical = 12.dp))

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Tổng cộng:", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                Text(finalTotal.toVND(), fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = PrimaryOrange)
            }
        }
    }
}

@Composable
fun ConfirmInfoCard(
    label: String,
    content: String,
    userName: String? = null,
    userPhone: String? = null,
    icon: @Composable () -> Unit,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(label, fontSize = 14.sp, color = Color.Gray)
                TextButton(onClick = onEditClick) {
                    Text("Edit", color = PrimaryOrange, fontSize = 14.sp)
                }
            }

            if (userName != null && userPhone != null && label == "Thông tin nhận hàng") {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 4.dp)) {
                    Icon(Icons.Default.Person, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(userName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                    Icon(Icons.Default.Phone, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(userPhone, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }

            Row(verticalAlignment = Alignment.Top) {
                Box(modifier = Modifier.padding(top = 2.dp)) { icon() }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = content,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 3
                )
            }
        }
    }
}