package com.example.foodapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodapp.data.Food
import com.example.foodapp.ui.components.FoodItemCard


@Composable
fun FavoritesScreen(
    savedFoods: List<Food>,
    onDetailClick: (Food) -> Unit,
    onToggleSaved: (Food) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Món đã lưu ❤️",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (savedFoods.isEmpty()) {
            Text(
                text = "Bạn chưa lưu món nào cả.",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(savedFoods) { food ->
                    FoodItemCard(
                        food = food,
                        onDetailClick = onDetailClick,
                        onToggleSaved = onToggleSaved,
                        isSaved = true // Luôn là true vì đây là màn hình Favorites
                    )
                }
            }
        }
    }
}