package com.example.foodapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodapp.data.Category
import com.example.foodapp.data.MockData
import com.example.foodapp.ui.theme.PrimaryOrange

@Composable
fun CategoriesSection(
    categories: List<Category> = MockData.categories,
    selectedCategoryId: String? = null,
    onCategoryClick: (Category) -> Unit = {}
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(categories) { category ->
            val isSelected = category.id == selectedCategoryId
            CategoryItem(
                category = category,
                isSelected = isSelected,
                onClick = { onCategoryClick(category) }
            )
        }
    }
}

@Composable
fun CategoryItem(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(category.colorHex)) // Chuyển đổi Long sang Color
                .border(
                    width = if (isSelected) 3.dp else 0.dp,
                    color = if (isSelected) PrimaryOrange else Color.Transparent,
                    shape = RoundedCornerShape(20.dp)
                )
                .wrapContentSize(Alignment.Center)
        ) {
            Text(
                text = category.image,
                fontSize = 28.sp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = category.name,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
            color = if (isSelected) PrimaryOrange else Color.Black
        )
    }
}