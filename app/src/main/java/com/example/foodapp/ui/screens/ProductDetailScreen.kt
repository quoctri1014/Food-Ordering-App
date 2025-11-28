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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodapp.R
import com.example.foodapp.data.Food as ModelFood
import com.example.foodapp.utils.toVND
import java.util.Locale

// Định nghĩa màu cam chủ đạo (Nếu trong dự án đã có theme thì có thể bỏ dòng này)
val PrimaryOrange = Color(0xFFFF6B3A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    food: ModelFood,
    onBackClick: () -> Unit,
    onAddItemToCart: (ModelFood, Int, String) -> Unit,
    onNavigateToCart: () -> Unit,
    onToggleSaved: (ModelFood) -> Unit,
    isSaved: Boolean,
    onBuyNow: (ModelFood, Int, String) -> Unit,
    cartItemCount: Int
) {
    var quantity by rememberSaveable { mutableIntStateOf(1) }
    var note by rememberSaveable { mutableStateOf("") }

    val drawableId = food.imageUrl
    val context = LocalContext.current

    val favoriteIconColor = if (isSaved) Color.Red else Color.Gray

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onToggleSaved(food) }) {
                        Icon(
                            imageVector = if (isSaved) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = favoriteIconColor
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 16.dp,
                color = Color.White,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .navigationBarsPadding(), // Đảm bảo không bị che bởi thanh điều hướng hệ thống
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Cột hiển thị Tổng tiền
                    Column(
                        modifier = Modifier.weight(0.4f)
                    ) {
                        Text("Tổng tiền", fontSize = 14.sp, color = Color.Gray)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            (food.price * quantity).toVND(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryOrange
                        )
                    }

                    // Các Nút Hành Động
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Nút Thêm vào Giỏ Hàng (Icon + Badge)
                        OutlinedButton(
                            onClick = {
                                onAddItemToCart(food, quantity, note)
                                Toast.makeText(context, "Đã thêm ${quantity} món vào giỏ hàng!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.height(50.dp).width(60.dp),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.5.dp, PrimaryOrange),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White,
                                contentColor = PrimaryOrange
                            )
                        ) {
                            BadgedBox(
                                badge = {
                                    if (cartItemCount > 0) {
                                        Badge(
                                            containerColor = Color.Red,
                                            contentColor = Color.White,
                                            modifier = Modifier.offset(x = (-5).dp, y = 5.dp)
                                        ) {
                                            Text("$cartItemCount")
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Filled.ShoppingCart,
                                    contentDescription = "Thêm vào Giỏ Hàng",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Nút "Mua Ngay"
                        Button(
                            onClick = { onBuyNow(food, quantity, note) },
                            modifier = Modifier
                                .height(50.dp)
                                .weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                            elevation = ButtonDefaults.buttonElevation(4.dp)
                        ) {
                            Text(
                                "Mua Ngay",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        // ⭐ ĐÃ SỬA: Sắp xếp lại thứ tự Modifier để fix lỗi bàn phím
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // 1. Padding của Scaffold
                .imePadding()           // 2. Đẩy nội dung lên KHI bàn phím hiện (quan trọng phải đặt trước scroll)
                .verticalScroll(rememberScrollState()) // 3. Cho phép cuộn phần diện tích còn lại
                .padding(horizontal = 16.dp)
                .background(Color.White)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // --- 1. Hình Ảnh ---
            val imageModifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFF5F5F5))

            Box(
                modifier = imageModifier,
                contentAlignment = Alignment.Center
            ) {
                if (drawableId != 0) {
                    Image(
                        painter = painterResource(id = food.imageUrl),
                        contentDescription = food.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text("No Image", fontSize = 24.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- 2. Tên món ăn ---
            Text(food.name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)

            Spacer(modifier = Modifier.height(12.dp))

            // --- 3. Đánh giá, Thời gian, Calo ---
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text(String.format(Locale.getDefault(), "%.1f", food.rating), fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)

                Spacer(Modifier.width(16.dp))

                Icon(painterResource(id = R.drawable.ic_clock), null, tint = PrimaryOrange, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("${food.time} phút", fontSize = 15.sp, color = Color.Gray)

                Spacer(Modifier.width(16.dp))

                Icon(painterResource(id = R.drawable.ic_calories), null, tint = Color.Red, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("${food.kCal} calo", fontSize = 15.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- 4. Mô tả ---
            Text("Mô tả", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = food.description,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(24.dp))
            Divider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(24.dp))

            // --- 5. Ghi chú và Số lượng ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Ghi chú
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Thêm lưu ý", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFF0F0F0)) {
                            Text("tùy chọn", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        placeholder = { Text("Ví dụ: không hành, ít cay...", fontSize = 14.sp, color = Color.LightGray) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.LightGray,
                            focusedBorderColor = PrimaryOrange
                        )
                    )
                }

                Spacer(modifier = Modifier.width(24.dp))

                // Bộ đếm Số lượng
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Số Lượng", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF5F5F5))
                            .padding(4.dp)
                    ) {
                        IconButton(
                            onClick = { if (quantity > 1) quantity-- },
                            modifier = Modifier.size(32.dp).background(Color.White, CircleShape)
                        ) {
                            Icon(painterResource(id = R.drawable.ic_remove), null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        }

                        Text(
                            "$quantity",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        IconButton(
                            onClick = { quantity++ },
                            modifier = Modifier.size(32.dp).background(PrimaryOrange, CircleShape)
                        ) {
                            Icon(painterResource(id = R.drawable.ic_add), null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // Spacer cuối để khi cuộn hết cỡ, nội dung không bị sát mép dưới
            Spacer(modifier = Modifier.height(300.dp))
        }
    }
}
