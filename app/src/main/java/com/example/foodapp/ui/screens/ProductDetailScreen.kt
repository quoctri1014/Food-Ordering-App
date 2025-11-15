package com.example.foodapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable // Import này là cần thiết cho rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodapp.R
import com.example.foodapp.data.Food as ModelFood
import com.example.foodapp.utils.toVND
import java.util.Locale // THÊM IMPORT LOCALE CHO String.format

// Giả định màu PrimaryOrange đã được định nghĩa
val PrimaryOrange = Color(0xFFFF6B3A)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    food: ModelFood,
    onBackClick: () -> Unit,
    onAddItemToCart: (ModelFood, Int) -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onBuyNow: (ModelFood, Int) -> Unit
) {
    // SỬA LỖI 1: Dùng rememberSaveable và mutableIntStateOf
    var quantity by rememberSaveable { mutableIntStateOf(1) }
    val drawableId = food.imageUrl // Int ID

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
                    IconButton(onClick = onNavigateToFavorites) {
                        Icon(Icons.Filled.FavoriteBorder, "Favorite")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {

            // --- 1. Phần Hình Ảnh Món Ăn ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                if (drawableId != 0) {
                    Image(
                        // SỬ DỤNG food.imageUrl (drawableId) TRỰC TIẾP
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

            // --- 2. Tên món ăn & Icon Yêu thích ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(food.name, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Icon(Icons.Filled.FavoriteBorder, contentDescription = "Favorite", modifier = Modifier.size(28.dp), tint = Color.Gray)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- 3. Đánh giá, Thời gian, Calo ---
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                // SỬA LỖI 2: Dùng String.format có Locale
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

            Spacer(modifier = Modifier.height(20.dp))

            // --- 4. Mô tả sản phẩm ---
            Text(
                text = buildAnnotatedString {
                    val description = food.description
                    val firstSentenceEndIndex = description.indexOf(". ")
                    if (firstSentenceEndIndex != -1) {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) { append(description.substring(0, firstSentenceEndIndex + 1)) }
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, color = Color.Gray)) { append(description.substring(firstSentenceEndIndex + 2)) }
                    } else { append(description) }
                },
                fontSize = 16.sp,
                lineHeight = 24.sp,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- 5. Thêm lưu ý và Bộ đếm Số Lượng ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Thêm lưu ý
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Thêm lưu ý", fontSize = 15.sp, color = Color.DarkGray)
                        Spacer(modifier = Modifier.width(8.dp))
                        Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))) {
                            Text("không bắt buộc", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth().heightIn(min = 40.dp, max = 100.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color.LightGray)
                    ) {
                        Text("Việc thực hiện yêu cầu sẽ phụ thuộc vào khả năng của quán", modifier = Modifier.padding(8.dp), fontSize = 13.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Bộ đếm Số Lượng
                Column(horizontalAlignment = Alignment.Start) {
                    Text("Số Lượng", fontSize = 15.sp, color = Color.DarkGray, modifier = Modifier.align(Alignment.CenterHorizontally))
                    Spacer(Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(Color(0xFFF0F0F0))
                    ) {
                        // Giả định R.drawable.ic_remove và ic_add tồn tại
                        IconButton(onClick = { if (quantity > 1) quantity-- }, modifier = Modifier.size(40.dp)) { Icon(painterResource(id = R.drawable.ic_remove), contentDescription = "Decrease") }
                        Text("$quantity", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
                        IconButton(onClick = { quantity++ }, modifier = Modifier.size(40.dp)) { Icon(painterResource(id = R.drawable.ic_add), contentDescription = "Increase") }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- 6. PHẦN THANH TOÁN ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Giá Tổng
                Column(
                    modifier = Modifier.weight(0.5f)
                ) {
                    Text("Total Amount", fontSize = 15.sp, color = Color.Gray)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        (food.price * quantity).toVND(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryOrange
                    )
                }

                // Các Nút Hành Động (Giỏ Hàng và Mua Ngay)
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Nút Thêm vào Giỏ Hàng & Điều hướng
                    OutlinedButton(
                        onClick = {
                            onAddItemToCart(food, quantity)
                            onNavigateToCart()
                        },
                        modifier = Modifier.height(56.dp).width(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(2.dp, PrimaryOrange),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = PrimaryOrange
                        )
                    ) {
                        Icon(
                            Icons.Filled.ShoppingCart,
                            contentDescription = "Thêm vào Giỏ Hàng",
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Nút "Mua Ngay"
                    Button(
                        onClick = { onBuyNow(food, quantity) },
                        modifier = Modifier
                            .height(56.dp)
                            .weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                    ) {
                        Text(
                            "Mua Ngay",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}