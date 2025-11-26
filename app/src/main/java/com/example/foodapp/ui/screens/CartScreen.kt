package com.example.foodapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodapp.data.CartItem
import com.example.foodapp.ui.theme.PrimaryOrange
import com.example.foodapp.utils.toVND


/**
 * Màn hình Giỏ Hàng
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(

    cartItems: List<CartItem>,
    onUpdateCart: (CartItem, Int) -> Unit,
    onBackClick: () -> Unit,
    onCheckoutClick: (List<CartItem>) -> Unit
) {

    val totalAmount = remember(cartItems) { cartItems.sumOf { it.food.price * it.quantity } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Giỏ Hàng Của Bạn", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                CartSummary(
                    totalAmount = totalAmount,
                    onCheckoutClick = { onCheckoutClick(cartItems) }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            if (cartItems.isEmpty()) CartEmptyMessage()
            else LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(cartItems, key = { it.food.id }) { item ->
                    CartItemRow(item = item, onUpdateCart = onUpdateCart)
                    Divider(color = Color.LightGray, thickness = 1.dp)
                }
            }
        }
    }
}


/**
 * Thông báo giỏ hàng trống (ĐÃ ĐỔI TÊN HÀM)
 */
@Composable
fun CartEmptyMessage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = "Giỏ hàng trống",
            tint = Color.LightGray,
            modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text("Giỏ hàng trống", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Hãy thêm một vài món ăn ngon vào giỏ hàng của bạn!", color = Color.DarkGray)
    }
}


/**
 * Item của giỏ hàng
 */
@Composable
fun CartItemRow(
    item: CartItem,
    onUpdateCart: (CartItem, Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.food.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = (item.food.price * item.quantity).toVND(),
                color = PrimaryOrange,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
        ) {
            // NÚT GIẢM SỐ LƯỢNG (SỬA LOGIC)
            IconButton(
                onClick = {
                    if (item.quantity > 1) {
                        onUpdateCart(item, -1)
                    } else {
                        // Nếu số lượng là 1, giảm xuống 0 (xóa món)
                        onUpdateCart(item, -item.quantity)
                    }
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Giảm số lượng",
                    tint = if (item.quantity > 0) PrimaryOrange else Color.LightGray
                )
            }

            Text(
                text = "${item.quantity}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.width(20.dp),
                textAlign = TextAlign.Center
            )

            // NÚT TĂNG SỐ LƯỢNG (Giữ nguyên)
            IconButton(
                onClick = { onUpdateCart(item, 1) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tăng số lượng",
                    tint = PrimaryOrange
                )
            }
        }
    }
}
/**
 * Thanh tổng kết (bottom)
 */
@Composable
fun CartSummary(
    totalAmount: Int,
    onCheckoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .heightIn(min = 120.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Tổng Cộng:", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            Text(
                totalAmount.toVND(),
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PrimaryOrange
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onCheckoutClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
        ) {
            Text("Thanh Toán Ngay", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}