package com.example.foodapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.foodapp.data.CartItem
import com.example.foodapp.data.FirestoreHelper
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.model.PaymentInfo
import com.example.foodapp.ui.theme.AppFoodTotalRed
import com.example.foodapp.ui.theme.PrimaryOrange
import com.example.foodapp.utils.toVND
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    cartItems: List<CartItem>,
    paymentInfo: PaymentInfo,
    onNavigateToHome: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var realOrder by remember { mutableStateOf<Order?>(null) }
    var currentStep by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    // Biến loading riêng cho nút hủy để tránh bấm nhiều lần
    var isCancelling by remember { mutableStateOf(false) }

    val steps = listOf("Đã xác nhận", "Đang chuẩn bị", "Đang giao", "Hoàn tất")

    // LẮNG NGHE DỮ LIỆU THỰC TẾ TỪ FIREBASE
    DisposableEffect(Unit) {
        val userId = auth.currentUser?.uid ?: return@DisposableEffect onDispose { }

        val registration = FirestoreHelper.listenToLatestOrder(userId) { order ->
            realOrder = order
            isLoading = false

            if (order != null) {
                currentStep = when (order.status) {
                    "Đang xử lý" -> 0
                    "Đang chuẩn bị" -> 1
                    "Đang giao" -> 2
                    "Hoàn tất", "Đã hoàn thành" -> 3
                    "Đã hủy" -> -1 // Trạng thái hủy
                    else -> 0
                }
            }
        }

        onDispose {
            registration.remove()
        }
    }

    // --- XỬ LÝ HỦY ĐƠN HÀNG ---
    fun cancelOrder() {
        val orderId = realOrder?.id
        if (orderId != null) {
            isCancelling = true
            scope.launch {
                // Gọi hàm updateOrderStatus trong FirestoreHelper
                val success = FirestoreHelper.updateOrderStatus(orderId, "Đã hủy")
                isCancelling = false
                if (success) {
                    Toast.makeText(context, "Đã hủy đơn hàng thành công", Toast.LENGTH_SHORT).show()
                    // Không cần navigate về Home ngay, để người dùng thấy trạng thái đã hủy
                } else {
                    Toast.makeText(context, "Lỗi khi hủy đơn hàng", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // --- QUYẾT ĐỊNH DỮ LIỆU HIỂN THỊ ---
    val rawAddress = realOrder?.address ?: paymentInfo.address
    val displayAddress = remember(rawAddress) {
        try {
            URLDecoder.decode(rawAddress, StandardCharsets.UTF_8.toString()).ifBlank { "Chưa có địa chỉ" }
        } catch (e: Exception) {
            rawAddress.ifBlank { "Chưa có địa chỉ" }
        }
    }

    val displayPhone = realOrder?.phone?.takeIf { it.isNotBlank() } ?: paymentInfo.phone.takeIf { it.isNotBlank() } ?: "..."
    val displayName = realOrder?.userName?.takeIf { it.isNotBlank() } ?: paymentInfo.fullName.takeIf { it.isNotBlank() } ?: "Khách hàng"
    val displayMethod = realOrder?.paymentMethod ?: paymentInfo.method.displayName
    val displayShippingFee = realOrder?.shippingFee ?: paymentInfo.shippingFee
    val displayTotal = realOrder?.finalAmount ?: (cartItems.sumOf { it.food.price * it.quantity } + displayShippingFee)
    val displaySubtotal = if (realOrder != null) realOrder!!.totalPrice else cartItems.sumOf { it.food.price * it.quantity }
    val orderItemsToDisplay = realOrder?.items

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
        if (isLoading && realOrder == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryOrange)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // BANNER TRẠNG THÁI
                val statusText = realOrder?.status ?: "Đang xử lý..."
                val statusColor = if (statusText == "Đã hủy") Color.Red else PrimaryOrange

                Text(
                    text = "Trạng thái: $statusText",
                    fontSize = 18.sp,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))

                // --- 1. THÔNG TIN GIAO HÀNG ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Thông tin nhận hàng", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = PrimaryOrange)
                        Spacer(Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(10.dp))
                            Text(displayName, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(10.dp))
                            Text(displayPhone, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(10.dp))
                            Text(displayAddress, fontSize = 15.sp, color = Color.DarkGray, maxLines = 3, overflow = TextOverflow.Ellipsis)
                        }
                        Divider(Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.5f))
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Thanh toán:", fontSize = 14.sp, color = Color.Gray)
                            Text(displayMethod, fontWeight = FontWeight.Bold, color = PrimaryOrange)
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // --- 2. Progress Stepper (Ẩn nếu đã hủy) ---
                if (currentStep != -1) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(2.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            steps.forEachIndexed { index, step ->
                                TrackingStepItem(step, index, currentStep)
                                if (index < steps.size - 1) {
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
                } else {
                    // Hiển thị thông báo Đã hủy
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("ĐƠN HÀNG ĐÃ BỊ HỦY", color = Color.Red, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }

                Text("Chi tiết món ăn:", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Spacer(Modifier.height(10.dp))

                // --- Danh sách món ăn ---
                if (orderItemsToDisplay != null && orderItemsToDisplay.isNotEmpty()) {
                    orderItemsToDisplay.forEach { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            elevation = CardDefaults.cardElevation(1.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = rememberAsyncImagePainter(model = item.foodImage),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp))
                                )
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("${item.foodName} (x${item.quantity})", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                                }
                                Text((item.price * item.quantity).toVND(), fontWeight = FontWeight.Bold, color = PrimaryOrange, fontSize = 15.sp)
                            }
                        }
                    }
                } else {
                    cartItems.forEach { item ->
                        TrackingOrderItemCardReadOnly(item = item)
                        Spacer(Modifier.height(10.dp))
                    }
                }

                Spacer(Modifier.height(24.dp))

                // --- TỔNG CỘNG ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(1.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F7FA))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Chi phí:", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Spacer(Modifier.height(8.dp))
                        TotalRow(label = "Tổng tiền hàng:", amount = displaySubtotal, isFinal = false)
                        TotalRow(label = "Phí vận chuyển:", amount = displayShippingFee, isFinal = false, isAccent = true)
                        Divider(Modifier.padding(vertical = 8.dp), color = Color.Gray.copy(alpha = 0.5f))
                        TotalRow(label = "Thành tiền:", amount = displayTotal, isFinal = true)
                    }
                }

                Spacer(Modifier.height(24.dp))

                // ⭐ NÚT HỦY ĐƠN HÀNG (ĐÃ SỬA LOGIC) ⭐
                // Chỉ hiện khi đơn hàng ở trạng thái "Đang xử lý" (index 0) hoặc chưa có status (vừa đặt)
                if (currentStep == 0 && realOrder?.status != "Đã hủy") {
                    OutlinedButton(
                        onClick = { cancelOrder() },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        border = BorderStroke(1.dp, Color.Red),
                        enabled = !isCancelling // Vô hiệu hóa khi đang xử lý
                    ) {
                        if (isCancelling) {
                            CircularProgressIndicator(color = Color.Red, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Hủy đơn hàng", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }

                // Nút quay về trang chủ (Luôn hiện)
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onNavigateToHome,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                ) {
                    Text("Quay về Trang chủ", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(30.dp))
            }
        }
    }
}

// ... (Các hàm hỗ trợ TotalRow, TrackingStepItem, TrackingOrderItemCardReadOnly giữ nguyên như cũ)
@Composable
fun TotalRow(label: String, amount: Int, isFinal: Boolean, isAccent: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 17.sp, fontWeight = if (isFinal) FontWeight.ExtraBold else FontWeight.Normal, color = if (isFinal) Color.Black else Color.DarkGray)
        Text(amount.toVND(), fontSize = if (isFinal) 19.sp else 15.sp, fontWeight = if (isFinal) FontWeight.ExtraBold else FontWeight.SemiBold, color = if (isAccent || isFinal) PrimaryOrange else Color.Black)
    }
}

@Composable
fun TrackingStepItem(step: String, index: Int, currentStep: Int) {
    val isActive = index <= currentStep
    val circleColor = if (isActive) PrimaryOrange else Color.LightGray
    val textColor = if (isActive) Color.Black else Color.Gray
    val stepTime = when (index) { 0 -> "Đã nhận"; 1 -> "Đang làm"; 2 -> "Đang giao"; 3 -> "Xong"; else -> "" }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(24.dp).background(circleColor, shape = CircleShape), contentAlignment = Alignment.Center) {
            Text("${index + 1}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(step, color = textColor, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            if (isActive && index == currentStep) Text(stepTime, color = PrimaryOrange, fontSize = 12.sp)
        }
    }
}

@Composable
fun TrackingOrderItemCardReadOnly(item: CartItem) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), elevation = CardDefaults.cardElevation(2.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(painter = rememberAsyncImagePainter(model = item.food.imageUrl), contentDescription = item.food.name, contentScale = ContentScale.Crop, modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)))
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("${item.food.name} (x${item.quantity})", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
            Text((item.food.price * item.quantity).toVND(), fontWeight = FontWeight.Bold, color = PrimaryOrange, fontSize = 15.sp)
        }
    }
}