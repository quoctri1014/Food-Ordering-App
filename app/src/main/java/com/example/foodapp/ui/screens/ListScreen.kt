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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavController
import com.example.foodapp.data.Food
import com.example.foodapp.data.mockFoods
import com.example.foodapp.ui.components.CategoriesSection
import com.example.foodapp.ui.theme.PrimaryOrange
import com.example.foodapp.data.Category as DataModelCategory
import com.example.foodapp.ui.components.SearchBar
import java.util.Locale

// SỬA LỖI: Thêm Locale vào String.format
fun Int.toVND(): String {
    return String.format(Locale.getDefault(), "%,d VNĐ", this)
}


// --- COMPONENT: SearchBar (Giữ nguyên) ---
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryOrange,
            unfocusedBorderColor = Color.LightGray
        )
    )
}

// ------------------- COMPONENT: FoodItemCard (Giữ nguyên) -------------------
@Composable
fun FoodItemCard(
    food: Food,
    onDetailClick: () -> Unit,
    onToggleSaved: () -> Unit,
    isSaved: Boolean,
    modifier: Modifier = Modifier
) {
    val drawableId = food.imageUrl

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp)
            .clickable { onDetailClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Phần 1: Hình ảnh
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

            // Phần 2: Tên, Rating, Time/KCal
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    food.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    // SỬA LỖI: Thêm Locale vào String.format
                    Text(
                        String.format(Locale.getDefault(), "%.1f", food.rating),
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Schedule, contentDescription = "Time", tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Text("${food.time} mins", fontSize = 12.sp, color = Color.Gray)

                    Icon(Icons.Default.LocalFireDepartment, contentDescription = "Calories", tint = PrimaryOrange, modifier = Modifier.size(14.dp))
                    Text("${food.kCal} kCal", fontSize = 12.sp, color = Color.Gray)
                }
            }

            // Phần 3: Giá và Nút Yêu Thích
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxHeight()
            ) {
                IconButton(onClick = onToggleSaved, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Yêu thích",
                        tint = if (isSaved) Color.Red else Color.LightGray,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    food.price.toVND(),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = PrimaryOrange
                )
            }
        }
    }
}


// --- MÀN HÌNH CHÍNH ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    foods: List<Food> = mockFoods,
    onDetailClick: (Food) -> Unit,
    onToggleSaved: (Food) -> Unit,
    savedFoodIds: List<String>
) {
    val categories = listOf(
        DataModelCategory("C1", "🍔", "Burger", Color(0xFFFFE0B2)),
        DataModelCategory("C2", "🍕", "Pizza", Color(0xFFFFCCBC)),
        DataModelCategory("C3", "🍣", "Sushi", Color(0xFFB2DFDB)),
        DataModelCategory("C4", "🥗", "Salad", Color(0xFFC8E6C9)),
        DataModelCategory("C5", "🍜", "Mì/Phở", Color(0xFFB3E5FC)),
        DataModelCategory("C6", "☕", "Đồ Uống", Color(0xFFD7CCC8))
    )

    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredFoods = foods
        .filter { it.name.contains(searchQuery, ignoreCase = true) }
        .filter {
            if (searchQuery.isNotEmpty()) {
                true
            } else selectedCategoryId == null || it.categoryId == selectedCategoryId
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App Food", fontWeight = FontWeight.Bold, color = PrimaryOrange) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                actions = {
                    // Nút Giỏ hàng
                    IconButton(onClick = { navController.navigate("cart") }) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Giỏ hàng", tint = PrimaryOrange)
                    }
                    // Nút Yêu thích (hoặc Account)
                    IconButton(onClick = { navController.navigate("profile") }) {
                        Icon(Icons.Default.Person, contentDescription = "Account", tint = Color.Black)
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp) // Điều chỉnh padding sau TopBar
        ) {

            item {
                Text(
                    text = "🍔 Thực đơn hôm nay",
                    modifier = Modifier.padding(horizontal = 16.dp).padding(top = 16.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                SearchBar(onSearch = { query ->
                    searchQuery = query
                    selectedCategoryId = null
                })
                Spacer(modifier = Modifier.height(16.dp))
            }

            // --- Phần Danh mục ---
            item {
                CategoriesSection(
                    categories = categories,
                    selectedCategoryId = selectedCategoryId,
                    onCategoryClick = { categoryClicked ->
                        if (categoryClicked.id == selectedCategoryId) {
                            selectedCategoryId = null
                        } else {
                            selectedCategoryId = categoryClicked.id
                            searchQuery = ""
                        }
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))
            }


            // --- Phần Danh sách món ăn ---
            items(filteredFoods) { food ->
                FoodItemCard(
                    food = food,
                    onDetailClick = { onDetailClick(food) },
                    onToggleSaved = { onToggleSaved(food) },
                    isSaved = savedFoodIds.contains(food.id),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}