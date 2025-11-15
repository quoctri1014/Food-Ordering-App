package com.example.foodapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// ************ ĐÃ SỬA LỖI: SỬA IMPORT CATEGORY CHÍNH XÁC ************
import com.example.foodapp.data.Category


@Composable
fun CategoriesSection(
    categories: List<Category>,
    selectedCategoryId: String?,
    onCategoryClick: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(categories) { category ->
            val isSelected = category.id == selectedCategoryId

            val backgroundColor = if (isSelected) {
                category.color
            } else {
                category.color.copy(alpha = 0.5f)
            }

            val contentColor = if (isSelected) {
                Color.Black
            } else {
                Color.DarkGray.copy(alpha = 0.8f)
            }


            Column(
                modifier = Modifier
                    .wrapContentWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(backgroundColor)
                    .clickable { onCategoryClick(category) }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Hiển thị Emoji (Dùng category.icon thay vì category.emoji)
                Text(
                    text = category.icon,
                    fontSize = 30.sp,
                    color = contentColor,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                // Hiển thị Tên Danh mục
                Text(
                    text = category.name,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    color = contentColor,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}