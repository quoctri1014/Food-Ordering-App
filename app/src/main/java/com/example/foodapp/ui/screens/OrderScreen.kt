package com.example.foodapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodapp.data.CartItem
import com.example.foodapp.ui.theme.PrimaryOrange
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    initialCartItems: List<CartItem>,
    onCheckoutClick: (List<CartItem>) -> Unit,
    onBackClick: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onUpdateCart: (CartItem, Int) -> Unit
) {
    // Tính tổng tiền sản phẩm
    val totalPrice = remember(initialCartItems) { initialCartItems.sumOf { it.food.price * it.quantity } }

    // Tổng cộng hiển thị (Chưa bao gồm ship, vì ship tính ở màn sau)
    val finalTotal = totalPrice

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Giỏ Hàng",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryOrange
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            if (initialCartItems.isNotEmpty()) {
                CheckoutButtonOnly(
                    subtotal = totalPrice,
                    finalTotal = finalTotal,
                    onCheckoutClick = { onCheckoutClick(initialCartItems) }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF9F9F9))
        ) {
            if (initialCartItems.isEmpty()) {
                OrderScreenEmptyState()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(initialCartItems, key = { it.food.id }) { item ->
                        OrderScreenItemRow(
                            item = item,
                            onIncrease = { onUpdateCart(item, 1) },
                            onDecrease = { onUpdateCart(item, -1) },
                            onItemClick = { onNavigateToDetail(item.food.id) }
                        )
                    }
                }
            }
        }
    }
}

// --- CÁC HÀM HỖ TRỢ CHO MÀN HÌNH GIỎ HÀNG ---

@Composable
private fun CheckoutButtonOnly(
    subtotal: Int,
    finalTotal: Int,
    onCheckoutClick: () -> Unit
) {
    Surface(
        color = Color.White,
        shadowElevation = 16.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Tạm tính", fontSize = 14.sp, color = Color.Gray)
                Text(
                    formatCurrency(subtotal),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryOrange
                )
            }

            Button(
                onClick = onCheckoutClick,
                modifier = Modifier
                    .width(180.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                shape = RoundedCornerShape(14.dp),
                elevation = ButtonDefaults.buttonElevation(4.dp)
            ) {
                Text("Thanh Toán", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun OrderScreenEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = null,
            tint = Color.LightGray.copy(alpha = 0.5f),
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Giỏ hàng đang đói bụng!",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Hãy chọn vài món ngon để lấp đầy nhé.",
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun OrderScreenItemRow(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onItemClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = item.food.imageUrl),
                contentDescription = item.food.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray.copy(alpha = 0.2f))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = item.food.name,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formatCurrency(item.food.price),
                    fontSize = 15.sp,
                    color = PrimaryOrange,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .height(36.dp)
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                ) {
                    IconButton(
                        onClick = onDecrease,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Giảm",
                            tint = if (item.quantity > 1) Color.Black else Color.Red,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = "${item.quantity}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp),
                        color = Color.Black
                    )

                    IconButton(
                        onClick = onIncrease,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tăng",
                            tint = PrimaryOrange,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun formatCurrency(amount: Int): String {
    return NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(amount)
}