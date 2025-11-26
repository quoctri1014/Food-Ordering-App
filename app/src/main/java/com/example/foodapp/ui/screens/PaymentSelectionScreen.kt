package com.example.foodapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodapp.data.model.PaymentMethod

// ⭐ DANH SÁCH PHƯƠNG THỨC THANH TOÁN ĐƯỢC GIỚI HẠN ⭐
private val availablePaymentMethods = listOf(
    PaymentMethod.BANK, // KHẮC PHỤC LỖI: BANK ĐÃ CÓ
    PaymentMethod.COD   // COD ĐÃ CÓ
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentSelectionScreen(
    onBackClick: () -> Unit,
    onPaymentMethodSelected: (PaymentMethod) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chọn Phương thức Thanh toán", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            item {
                Text(
                    "Vui lòng chọn một phương thức thanh toán",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
            // ⭐ CHỈ HIỂN THỊ COD VÀ BANK ⭐
            items(availablePaymentMethods) { method ->
                // Chọn Icon dựa trên phương thức
                val icon = when (method) {
                    PaymentMethod.BANK -> Icons.Default.CreditCard // Sử dụng CreditCard cho chuyển khoản
                    PaymentMethod.COD -> Icons.Default.AttachMoney
                    // MỌI PHƯƠNG THỨC KHÁC ĐỀU ĐÃ BỊ LOẠI BỎ
                }

                PaymentOptionCard(
                    icon = icon,
                    iconTint = if (method == PaymentMethod.COD) Color(0xFF43A047) else Color(0xFF1976D2),
                    methodName = method.displayName,
                    description = if (method == PaymentMethod.COD) "Tiền mặt khi nhận hàng" else "Quét mã hoặc số tài khoản",
                    onClick = { onPaymentMethodSelected(method) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// Hàm hỗ trợ cho từng option thanh toán (Giữ nguyên)
@Composable
fun PaymentOptionCard(
    icon: ImageVector,
    iconTint: Color,
    methodName: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Box
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconTint.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text Info
            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    text = methodName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    text = description,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }
    }
}