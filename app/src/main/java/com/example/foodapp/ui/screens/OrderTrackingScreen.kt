package com.example.foodapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.foodapp.data.CartItem
import kotlinx.coroutines.delay
import com.example.foodapp.data.model.PaymentInfo
import com.example.foodapp.data.model.PrimaryOrange
import com.example.foodapp.data.model.AppFoodTotalRed
import com.example.foodapp.utils.toVND

// --- OrderTrackingScreen Composable ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    cartItems: List<CartItem>,
    paymentInfo: PaymentInfo,
    onNavigateToHome: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }
    val steps = listOf("Đã xác nhận", "Đang chuẩn bị", "Đang giao", "Hoàn tất")

    // Tính toán các giá trị cần thiết
    val subtotal = cartItems.sumOf { it.subtotal } // Dùng thuộc tính subtotal
    val shippingFee = 15000
    val finalTotal = subtotal + shippingFee

    // Giả lập tiến trình tự động
    LaunchedEffect(Unit) {
        while (currentStep < steps.size - 1) {
            delay(15000)
            currentStep++
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Theo dõi đơn hàng", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                navigationIcon = {
                    IconButton(onClick = onNavigateToHome) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại", tint = Color.Black)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToHome) {
                        Icon(Icons.Default.Home, contentDescription = "Trang chủ", tint = PrimaryOrange)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Thời gian giao dự kiến: 15:30 - 15:45", fontSize = 15.sp, color = PrimaryOrange, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))

            // --- 1. THÔNG TIN GIAO HÀNG THỰC TẾ ---
            OrderInfoCard(paymentInfo = paymentInfo)
            Spacer(Modifier.height(24.dp))

            // --- 2. Progress Stepper (Timeline Style) ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    steps.forEachIndexed { index, step ->
                        TrackingStepItem(
                            step = step,
                            index = index,
                            currentStep = currentStep
                        )
                        if (index < steps.size - 1) {
                            // Thanh nối (Timeline Connector)
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(30.dp)
                                    .background(if (index < currentStep) PrimaryOrange else Color.LightGray)
                                    .align(Alignment.Start)
                                    .offset(x = 10.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Text("Chi tiết các món:", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Spacer(Modifier.height(10.dp))

            // --- Danh sách món ăn ---
            cartItems.forEach { item ->
                TrackingOrderItemCardReadOnly(item = item)
                Spacer(Modifier.height(10.dp))
            }

            // --- PHẦN TỔNG CỘNG ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(1.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F7FA))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Tóm tắt thanh toán:", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Spacer(Modifier.height(8.dp))

                    TotalRow(label = "Tổng tiền hàng:", amount = subtotal, isFinal = false)
                    TotalRow(label = "Phí vận chuyển:", amount = shippingFee, isFinal = false, isAccent = true)

                    Divider(Modifier.padding(vertical = 8.dp), color = Color.Gray.copy(alpha = 0.5f))

                    TotalRow(label = "Tổng tiền đã thanh toán:", amount = finalTotal, isFinal = true)
                }
            }
            // --- HẾT PHẦN TỔNG CỘNG ---


            Spacer(Modifier.height(24.dp))

            // --- Nút hành động ---
            Button(
                onClick = { /* Mở ứng dụng gọi điện/chat với shipper */ },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                shape = RoundedCornerShape(14.dp),
                enabled = currentStep >= 2
            ) {
                Text("Liên hệ Shipper", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(Modifier.height(10.dp))
            OutlinedButton(
                onClick = onNavigateToHome,
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(2.dp, Color.Red),
                enabled = currentStep < 2
            ) {
                Text("Hủy đơn hàng", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

// --- COMPOSABLE HỖ TRỢ ---

@Composable
fun TotalRow(label: String, amount: Int, isFinal: Boolean, isAccent: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            fontSize = if (isFinal) 17.sp else 15.sp,
            fontWeight = if (isFinal) FontWeight.ExtraBold else FontWeight.Normal,
            color = if (isFinal) Color.Black else Color.DarkGray
        )
        Text(
            amount.toVND(),
            fontSize = if (isFinal) 19.sp else 15.sp,
            fontWeight = if (isFinal) FontWeight.ExtraBold else FontWeight.SemiBold,
            color = when {
                isFinal -> AppFoodTotalRed
                isAccent -> PrimaryOrange
                else -> Color.Black
            }
        )
    }
}

@Composable
fun OrderInfoCard(paymentInfo: PaymentInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Thông tin Giao hàng & Thanh toán:", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Spacer(Modifier.height(8.dp))

            OrderInfoRow("Họ và tên:", paymentInfo.fullName)
            OrderInfoRow("SĐT:", paymentInfo.phone)
            OrderInfoRow("Địa chỉ:", paymentInfo.address)
            if (paymentInfo.note.isNotBlank()) {
                OrderInfoRow("Ghi chú:", paymentInfo.note)
            }
            Divider(Modifier.padding(vertical = 8.dp), color = Color.LightGray)
            OrderInfoRow("Phương thức TT:", paymentInfo.method.displayName, isAccent = true)
        }
    }
}

@Composable
fun OrderInfoRow(label: String, value: String, isAccent: Boolean = false) {
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text(label, fontSize = 14.sp, color = Color.Gray)
        Text(
            value,
            fontSize = 14.sp,
            fontWeight = if (isAccent) FontWeight.Bold else FontWeight.SemiBold,
            color = if (isAccent) PrimaryOrange else Color.Black
        )
    }
}

@Composable
fun TrackingStepItem(step: String, index: Int, currentStep: Int) {
    val isActive = index <= currentStep
    val circleColor = if (isActive) PrimaryOrange else Color.LightGray
    val textColor = if (isActive) Color.Black else Color.Gray
    val stepTime = when (index) {
        0 -> "Vừa xong"
        1 -> "Đang làm"
        2 -> "Đang trên đường"
        3 -> "Giao thành công"
        else -> ""
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(circleColor, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("${index + 1}", color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(step, color = textColor, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            if (isActive) {
                Text(stepTime, color = PrimaryOrange, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun TrackingOrderItemCardReadOnly(item: CartItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = item.food.imageUrl),
                contentDescription = item.food.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("${item.food.name} (x${item.quantity})", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                // Lỗi: Dùng item.food.description thay vì item.note (không có trong CartItem)
                val noteDisplay = if (item.food.description.length > 50) item.food.description.take(50) + "..." else item.food.description
                Text("Mô tả: $noteDisplay", fontSize = 13.sp, color = Color.Gray)
            }
            Text((item.food.price * item.quantity).toVND(), fontWeight = FontWeight.Bold, color = PrimaryOrange, fontSize = 15.sp)
        }
    }
}