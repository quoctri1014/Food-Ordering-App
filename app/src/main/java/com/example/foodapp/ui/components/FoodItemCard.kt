package com.example.foodapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Đổi từ 'com.example.foodapp.data.model.Food' sang package Food thực tế của bạn
import com.example.foodapp.data.Food
import java.text.NumberFormat
import java.util.*
import androidx.compose.ui.platform.LocalContext


val PrimaryOrange = Color(0xFFFF9800)

fun Int.toVND(): String {
    val format = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    return format.format(this)
}

@Composable
fun FoodItemCard(
    food: Food,
    onDetailClick: (Food) -> Unit,
    onToggleSaved: ((Food) -> Unit)? = null,
    isSaved: Boolean = false,
    modifier: Modifier = Modifier
) {

    val drawableId = food.imageUrl // Lấy trực tiếp Int ID từ Food object

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onDetailClick(food) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // **HIỂN THỊ HÌNH ẢNH**
            // Dùng trực tiếp food.imageUrl (là Int ID)
            if (drawableId != 0) {
                Image(
                    // ✅ ĐÃ SỬA: Truyền trực tiếp Int ID vào painterResource
                    painter = painterResource(id = drawableId),
                    contentDescription = food.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(85.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE0E0E0))
                )
            } else {
                // Hiển thị Placeholder nếu ID là 0 (hoặc lỗi)
                Box(
                    modifier = Modifier
                        .size(85.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = food.name.first().toString(), fontSize = 30.sp, color = Color.Gray)
                }
            }


            Spacer(Modifier.width(16.dp))

            // Phần thông tin món ăn (Giữ nguyên)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    food.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        String.format("%.1f", food.rating),
                        fontSize = 13.sp,
                        color = Color.Gray.copy(alpha = 0.8f)
                    )
                }

                Text(
                    food.price.toVND(),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 19.sp,
                    color = PrimaryOrange
                )
            }

            // Phần nút Lưu/Xóa (Giữ nguyên)
            if (onToggleSaved != null && isSaved) {
                Button(
                    onClick = { onToggleSaved(food) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFE5E5)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text("Xóa", color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}