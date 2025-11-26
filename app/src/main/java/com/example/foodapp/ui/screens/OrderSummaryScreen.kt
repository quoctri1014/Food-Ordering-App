package com.example.foodapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodapp.data.CartItem
import com.example.foodapp.ui.theme.PrimaryOrange
import com.example.foodapp.ui.theme.AppFoodTotalRed // ⭐ DÙNG IMPORT ĐỂ TRÁNH LỖI TRÙNG LẶP
import com.example.foodapp.utils.toVND

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderSummaryScreen(
    initialCartItems: List<CartItem>,
    onNavigateToPayment: (Int) -> Unit,
    onBackClick: () -> Unit,
    onUpdateCart: (CartItem, Int) -> Unit
) {
    val shippingFee = 15000
    val discount = 20000
    val subtotal = remember(initialCartItems) { initialCartItems.sumOf { it.food.price * it.quantity } }
    // Tính tổng cuối cùng (không âm)
    val finalTotal = (subtotal + shippingFee - discount).coerceAtLeast(0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết đơn hàng", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            SummaryFooter(
                subtotal = subtotal,
                shippingFee = shippingFee,
                discount = discount,
                finalTotal = finalTotal,
                onCompleteOrder = { onNavigateToPayment(finalTotal) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(initialCartItems) { item ->
                SummaryItemRow(
                    item = item,
                    onDecrease = { onUpdateCart(item, -1) },
                    onIncrease = { onUpdateCart(item, 1) }
                )
            }
        }
    }
}

@Composable
fun SummaryItemRow(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = item.food.imageUrl),
                contentDescription = item.food.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.food.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(item.food.price.toVND(), fontSize = 13.sp, color = Color.Gray)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFFF0F0F0))
            ) {
                IconButton(onClick = onDecrease, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = if (item.quantity > 1) Icons.Default.Remove else Icons.Default.Delete, contentDescription = "Decrease", tint = if (item.quantity > 1) Color.Black else Color.Red)
                }
                Text("${item.quantity}", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))
                IconButton(onClick = onIncrease, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Add, contentDescription = "Increase", tint = PrimaryOrange)
                }
            }
        }
    }
}

@Composable
fun SummaryFooter(subtotal: Int, shippingFee: Int, discount: Int, finalTotal: Int, onCompleteOrder: () -> Unit) {
    Surface(
        color = Color(0xFFFF6B3A),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
            SummaryDetailRow("Tổng phụ:", subtotal, Color.White)
            SummaryDetailRow("Phí giao hàng:", shippingFee, Color.White)
            SummaryDetailRow("Giảm giá:", -discount, Color(0xFFFBC02D))
            Divider(Modifier.padding(vertical = 8.dp), color = Color.White.copy(alpha = 0.5f))
            SummaryDetailRow("Tổng cộng:", finalTotal, Color.White, isBold = true)
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onCompleteOrder,
                modifier = Modifier.fillMaxWidth().height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Hoàn tất đặt hàng", color = Color(0xFFFF6B3A), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun SummaryDetailRow(label: String, amount: Int, valueColor: Color, isBold: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 16.sp, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal, color = Color.White)
        Text(if (amount < 0) "-${(-amount).toVND()}" else amount.toVND(), fontSize = 16.sp, fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.SemiBold, color = valueColor)
    }
}