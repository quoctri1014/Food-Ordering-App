package com.example.foodapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.navigation.NavController
import com.example.foodapp.data.Category as DataModelCategory
import com.example.foodapp.data.Food
import com.example.foodapp.data.MockData
import com.example.foodapp.ui.components.CategoriesSection
import com.example.foodapp.ui.theme.PrimaryOrange
import java.util.Locale

// Hàm format tiền Việt
fun Int.toVND(): String {
    return String.format(Locale("vi", "VN"), "%,d VNĐ", this)
}

// --- COMPONENT: SearchBar ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(onSearch: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
            onSearch(it)
        },
        label = { Text("Tìm kiếm món ăn...") },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryOrange,
            unfocusedBorderColor = Color.LightGray
        )
    )
}

// --- COMPONENT: FoodItemCard ---
@Composable
fun FoodItemCard(
    food: Food,
    onDetailClick: (String) -> Unit,
    onToggleSaved: () -> Unit,
    isSaved: Boolean,
    modifier: Modifier = Modifier
) {
    val drawableId = food.imageUrl
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp)
            .clickable { onDetailClick(food.id) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ảnh món ăn
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Gray.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                if (drawableId != 0) {
                    Image(
                        painter = painterResource(id = drawableId),
                        contentDescription = food.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text("No Img", fontSize = 10.sp, color = Color.DarkGray)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))

            // Thông tin món ăn
            Column(modifier = Modifier.weight(1f)) {
                Text(food.name, fontWeight = FontWeight.Bold, fontSize = 17.sp, maxLines = 1)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(String.format(Locale.US, "%.1f", food.rating), fontSize = 14.sp, color = Color.DarkGray)
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Text(" ${food.time}p", fontSize = 12.sp, color = Color.Gray)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Default.LocalFireDepartment, null, tint = PrimaryOrange, modifier = Modifier.size(14.dp))
                    Text(" ${food.kCal} kcal", fontSize = 12.sp, color = Color.Gray)
                }
            }

            // Giá tiền & Nút Like
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxHeight()) {
                IconButton(onClick = onToggleSaved, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = if (isSaved) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Save",
                        tint = if (isSaved) Color.Red else Color.LightGray
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(food.price.toVND(), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = PrimaryOrange)
            }
        }
    }
}

// --- MÀN HÌNH DANH SÁCH (LIST SCREEN) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    navController: NavController,
    foods: List<Food> = MockData.mockFoods,
    onDetailClick: (String) -> Unit,
    onToggleSaved: (Food) -> Unit,
    savedFoodIds: List<String>,
    cartItemCount: Int
) {
    val categories = listOf(
        DataModelCategory("C1", "🍔", "Burger", 0xFFFFE0B2L),
        DataModelCategory("C2", "🍕", "Pizza", 0xFFFFCCBCL),
        DataModelCategory("C3", "🍣", "Sushi", 0xFFB2DFDBL),
        DataModelCategory("C4", "🥗", "Salad", 0xFFC8E6C9L),
        DataModelCategory("C5", "🍜", "Mì/Phở", 0xFFB3E5FCL),
        DataModelCategory("C6", "☕", "Đồ Uống", 0xFFD7CCC8L)
    )

    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredFoods = foods.filter { food ->
        val matchesSearch = food.name.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategoryId == null || food.categoryId == selectedCategoryId
        matchesSearch && matchesCategory
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {

        // --- Header Tùy chỉnh (ĐÃ SỬA) ---
        // Đổi từ Row sang Box để dễ căn giữa và xếp chồng
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // 1. Nút Back (Nằm bên trái)
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
            }

            // 2. Chữ BURGERKING (Căn giữa màn hình)
            Text(
                text = "BURGERKING",
                fontWeight = FontWeight.ExtraBold,
                color = PrimaryOrange,
                fontSize = 24.sp,
                modifier = Modifier.align(Alignment.Center)
            )

            // Đã xóa các icon bên phải
        }

        Text(
            text = "🍔 Thực đơn hôm nay",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                SearchBar(onSearch = { query ->
                    searchQuery = query
                    selectedCategoryId = null
                })
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                CategoriesSection(
                    categories = categories,
                    selectedCategoryId = selectedCategoryId,
                    onCategoryClick = { category ->
                        if (category.id == selectedCategoryId) {
                            selectedCategoryId = null
                        } else {
                            selectedCategoryId = category.id
                            searchQuery = ""
                        }
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            items(filteredFoods) { food ->
                FoodItemCard(
                    food = food,
                    onDetailClick = onDetailClick,
                    onToggleSaved = { onToggleSaved(food) },
                    isSaved = savedFoodIds.contains(food.id),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}