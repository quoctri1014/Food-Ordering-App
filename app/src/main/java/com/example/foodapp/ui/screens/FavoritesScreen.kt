package com.example.foodapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodapp.data.Food
import com.example.foodapp.ui.theme.PrimaryOrange
import com.example.foodapp.utils.toVND // Đảm bảo import này
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    savedFoods: List<Food>,
    onDetailClick: (Food) -> Unit,
    onToggleSaved: (Food) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Yêu Thích",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryOrange
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF9F9F9)) // Nền xám nhạt hiện đại
        ) {
            if (savedFoods.isEmpty()) {
                EmptyFavoritesState()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(savedFoods) { food ->
                        FavoriteFoodCard(
                            food = food,
                            onDetailClick = { onDetailClick(food) },
                            onToggleSaved = { onToggleSaved(food) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyFavoritesState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.FavoriteBorder,
            contentDescription = null,
            tint = Color.LightGray.copy(alpha = 0.5f),
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Chưa có món yêu thích nào",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Hãy thả tim cho các món ăn bạn thích để lưu lại đây nhé!",
            color = Color.Gray,
            textAlign = TextAlign.Center,
            fontSize = 15.sp
        )
    }
}

@Composable
fun FavoriteFoodCard(
    food: Food,
    onDetailClick: () -> Unit,
    onToggleSaved: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDetailClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ảnh
            Image(
                painter = painterResource(id = food.imageUrl),
                contentDescription = food.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray.copy(alpha = 0.2f))
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Thông tin
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = food.name,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Rating
                Row(verticalAlignment = Alignment.CenterVertically) {
                     Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                     Text(" ${food.rating}", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                     Text(" • ${food.time} phút", fontSize = 14.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = food.price.toVND(), // Sử dụng hàm mở rộng nếu có, hoặc formatCurrency
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryOrange
                    )

                    // Nút Tim (Xóa khỏi yêu thích)
                    IconButton(
                        onClick = onToggleSaved,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Bỏ thích",
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }
}