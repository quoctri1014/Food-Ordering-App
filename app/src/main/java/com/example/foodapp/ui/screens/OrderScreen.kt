package com.example.foodapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.foodapp.data.CartItem
import com.example.foodapp.ui.theme.PrimaryOrange


import androidx.compose.runtime.toMutableStateList

val AppFoodTotalRed = Color(0xFFD32F2F)



data class OrderItemState(
    val cartItem: CartItem,
    var note: String,
    var quantity: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    initialCartItems: List<CartItem>,
    onCheckoutClick: (List<CartItem>) -> Unit,
    onBackClick: () -> Unit,
    onNavigateToDetail: (String) -> Unit // Thêm callback navigate
) {
    // TẠO TRẠNG THÁI RIÊNG cho màn hình Order, bao gồm cả Ghi chú.
    val orderItemStates: SnapshotStateList<OrderItemState> = remember {
        initialCartItems.map { item ->
            OrderItemState(
                cartItem = item,
                note = item.note,
                quantity = item.quantity
            )
        }.toMutableStateList()
    }

    // Loại bỏ các món có số lượng bằng 0 (nếu có)
    val currentOrderItems = remember(orderItemStates.size) {
        orderItemStates.filter { it.quantity > 0 }
    }

    // Tính toán lại tổng tiền dựa trên trạng thái hiện tại
    val subtotal = currentOrderItems.sumOf { it.cartItem.food.price * it.quantity }
    val shippingFee = 15000
    val discount = 0
    val finalTotal = subtotal + shippingFee - discount

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Đơn hàng của bạn", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại", tint = Color.Black)
                    }
                },
                actions = {

                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {

            // --- Danh sách món ăn ---
            currentOrderItems.forEach { itemState ->

                OrderItemCard(

                    item = itemState.cartItem.copy(quantity = itemState.quantity, note = itemState.note),
                    note = itemState.note,
                    onNoteChange = { itemState.note = it },
                    onQuantityChange = { change ->
                        val index = orderItemStates.indexOf(itemState)
                        if (index != -1) {
                            val newQuantity = itemState.quantity + change
                            if (newQuantity <= 0) {
                                orderItemStates.removeAt(index)
                            } else {
                                // Cập nhật số lượng trực tiếp
                                orderItemStates[index] = itemState.copy(quantity = newQuantity)
                            }
                        }
                    },
                    onDetailClick = { onNavigateToDetail(itemState.cartItem.food.id) }
                )
                Spacer(Modifier.height(12.dp))
            }

            Spacer(Modifier.height(8.dp))

            // --- Tổng cộng ---
            TotalSummaryCard(cartItems = currentOrderItems.map { it.cartItem.copy(quantity = it.quantity) })

            Spacer(Modifier.height(24.dp))

            // --- Nút Xác nhận -> CHUYỂN ĐẾN MÀN HÌNH NHẬP THÔNG TIN/THANH TOÁN ---
            Button(
                onClick = {
                    val finalCart = currentOrderItems.map { state ->
                        state.cartItem.copy(
                            quantity = state.quantity,
                            note = state.note
                        )
                    }
                    onCheckoutClick(finalCart)
                },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                shape = RoundedCornerShape(14.dp),
                enabled = currentOrderItems.isNotEmpty()
            ) {
                Text("Xác nhận & Thanh toán (${finalTotal.toVND()})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}


@Composable
fun OrderItemCard(
    item: CartItem,
    note: String,
    onNoteChange: (String) -> Unit,
    onQuantityChange: (Int) -> Unit,
    onDetailClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = item.food.imageUrl),
                    contentDescription = item.food.name,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(item.food.name, fontWeight = FontWeight.SemiBold, color = Color.Black, fontSize = 16.sp)
                    Text("Đơn giá: ${item.food.price.toVND()}", fontSize = 13.sp, color = Color.Gray)
                    Text("Thành tiền: ${(item.food.price * item.quantity).toVND()}", fontWeight = FontWeight.Bold, color = PrimaryOrange, fontSize = 15.sp)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Nút thay đổi số lượng
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.background(Color(0xFFEFEFEF), RoundedCornerShape(18.dp))
                    ) {
                        IconButton(onClick = { onQuantityChange(-1) }, modifier = Modifier.size(30.dp)) {
                            Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                        Text(item.quantity.toString(), fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(horizontal = 4.dp))
                        IconButton(onClick = { onQuantityChange(1) }, modifier = Modifier.size(30.dp)) {
                            Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    TextButton(onClick = onDetailClick, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)) {
                        Text("Chi tiết món", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }

            // --- Ghi chú ---
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = note,
                onValueChange = onNoteChange,
                label = { Text("Ghi chú món ăn (ví dụ: ít cay, không hành)", fontSize = 13.sp) },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
}

@Composable
fun TotalSummaryCard(cartItems: List<CartItem>) {
    val subtotal = cartItems.sumOf { it.food.price * it.quantity }
    val shippingFee = 15000
    val discount = 0
    val total = subtotal + shippingFee - discount

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Chi tiết Thanh toán", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Spacer(Modifier.height(8.dp))

            // Subtotal
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Tổng phụ:", fontSize = 15.sp, color = Color.DarkGray)
                Text(subtotal.toVND(), fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(4.dp))

            // Shipping
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Phí giao hàng:", fontSize = 15.sp, color = Color.DarkGray)
                Text(shippingFee.toVND(), fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(4.dp))

            // Discount
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Giảm giá:", fontSize = 15.sp, color = Color.Red)
                Text("-${discount.toVND()}", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.Red)
            }

            Divider(Modifier.padding(vertical = 8.dp), color = Color.LightGray)

            // Total
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Tổng cộng cuối cùng:", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                Text(total.toVND(), fontWeight = FontWeight.ExtraBold, color = AppFoodTotalRed, fontSize = 18.sp)
            }
        }
    }
}