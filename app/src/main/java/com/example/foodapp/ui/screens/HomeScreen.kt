package com.example.foodapp.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodapp.R
import com.example.foodapp.data.Food
import com.example.foodapp.ui.theme.PrimaryOrange
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Dữ liệu giả lập danh mục
data class CategoryUI(val id: String, val name: String, val iconRes: Int, val color: Color)

val categoriesUI = listOf(
    CategoryUI("C1", "Burger", R.drawable.ic_burger_icon, Color(0xFFFFE0B2)),
    CategoryUI("C2", "Pizza", R.drawable.ic_pizza_icon, Color(0xFFFFCCBC)),
    CategoryUI("C3", "Sushi", R.drawable.ic_sushi_icon, Color(0xFFB2DFDB)),
    CategoryUI("C4", "Salad", R.drawable.ic_salad_icon, Color(0xFFC8E6C9)),
    CategoryUI("C5", "Spaghetti", R.drawable.ic_noodle_icon, Color(0xFFBBDEFB))
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onViewAllClick: () -> Unit,
    onFoodClick: (String) -> Unit,
    foods: List<Food>
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current

    // Logic lọc món ăn
    val filteredFoods = remember(foods, selectedCategoryId, searchQuery) {
        foods.filter { food ->
            val matchesCategory = selectedCategoryId == null || food.categoryId == selectedCategoryId
            val matchesSearch = searchQuery.isEmpty() || food.name.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    val popularFoods = remember(foods) { foods.filter { it.rating >= 4.5 } }

    // --- CẤU HÌNH BANNER SLIDE SHOW ---
    val banners = remember { mutableStateListOf(R.drawable.ic_banner_sample_1, R.drawable.ic_banner_sample_2) }
    // PagerState để quản lý trang hiện tại
    val pagerState = rememberPagerState(pageCount = { banners.size })

    // Tự động chuyển banner sau mỗi 3 giây
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000) // Chờ 3 giây
            if (banners.isNotEmpty()) {
                // Tính trang tiếp theo (xoay vòng)
                val nextPage = (pagerState.currentPage + 1) % banners.size
                // Cuộn mượt mà sang trang tiếp
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
    ) {
        // --- HEADER ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(top = 16.dp, bottom = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "BURGERKING",
                fontWeight = FontWeight.Black,
                color = PrimaryOrange,
                fontSize = 26.sp,
                letterSpacing = 3.sp,
                textAlign = TextAlign.Center
            )
        }

        // --- SEARCH BAR ---
        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            BeautifulSearchBar(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                onClear = {
                    searchQuery = ""
                    focusManager.clearFocus()
                }
            )
        }

        // --- NỘI DUNG CHÍNH ---
        if (searchQuery.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        "Kết quả tìm kiếm (${filteredFoods.size})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                if (filteredFoods.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(top = 50.dp), contentAlignment = Alignment.Center) {
                            Text("Không tìm thấy món nào phù hợp", color = Color.Gray)
                        }
                    }
                } else {
                    items(filteredFoods) { food ->
                        FoodSearchResultItem(food = food, onClick = { onFoodClick(food.id) })
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // 1. Greeting
                item {
                    Padding(horizontal = 20.dp) {
                        Text("Bạn muốn ăn gì hôm nay? 😋", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 2. Categories
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(categoriesUI) { cat ->
                            CategoryItem(
                                category = cat,
                                isSelected = selectedCategoryId == cat.id,
                                onClick = {
                                    selectedCategoryId = if (selectedCategoryId == cat.id) null else cat.id
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // 3. Filtered List
                if (selectedCategoryId != null) {
                    item {
                        Padding(horizontal = 20.dp) {
                            Text("Kết quả lọc", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(filteredFoods) { food ->
                                FoodItemStyledCard(food = food, onClick = { onFoodClick(food.id) })
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                // 4. Banner (SỬ DỤNG PAGER)
                item {
                    Padding(horizontal = 20.dp) {
                        // Truyền danh sách ảnh và state vào component mới
                        BannerSection(banners = banners, pagerState = pagerState)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // 5. Popular Section
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Món ăn phổ biến 🔥", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        TextButton(onClick = onViewAllClick) {
                            Text("Xem tất cả", color = PrimaryOrange, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(popularFoods) { food ->
                            FoodItemStyledCard(food = food, onClick = { onFoodClick(food.id) })
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // 6. Recommend Section
                item {
                    Padding(horizontal = 20.dp) {
                        Text("Gợi ý cho bạn", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(foods.take(5)) { food ->
                            FoodItemStyledCard(food = food, onClick = { onFoodClick(food.id) })
                        }
                    }
                }
            }
        }
    }
}

// --- CÁC COMPONENT CON ---

@Composable
fun Padding(horizontal: Dp, content: @Composable () -> Unit) {
    Box(modifier = Modifier.padding(horizontal = horizontal)) { content() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeautifulSearchBar(value: String, onValueChange: (String) -> Unit, onClear: () -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text("Tìm kiếm món ăn...", color = Color.Gray) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryOrange) },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(4.dp, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
    )
}

@Composable
fun FoodSearchResultItem(food: Food, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.size(80.dp)
            ) {
                Image(
                    painter = painterResource(id = food.imageUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(food.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(4.dp))
                Text("${food.price / 1000}k", color = PrimaryOrange, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Icon(Icons.Default.Add, contentDescription = null, tint = PrimaryOrange)
        }
    }
}

@Composable
fun CategoryItem(
    category: CategoryUI,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) PrimaryOrange else category.color.copy(alpha = 0.25f),
        label = "bgColor"
    )
    val elevation by animateDpAsState(
        targetValue = if (isSelected) 8.dp else 0.dp,
        label = "elevation"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) { onClick() }
    ) {
        Surface(
            modifier = Modifier.size(68.dp),
            shape = RoundedCornerShape(22.dp),
            color = backgroundColor,
            shadowElevation = elevation
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = category.iconRes),
                    contentDescription = category.name,
                    modifier = Modifier.size(36.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = category.name,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) PrimaryOrange else Color.DarkGray
        )
    }
}

// ⭐ BANNER SECTION MỚI (DÙNG HORIZONTAL PAGER) ⭐
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BannerSection(
    banners: List<Int>,
    pagerState: PagerState
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .shadow(6.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            // Hiển thị ảnh tương ứng với page
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = banners[page]),
                    contentDescription = "Banner $page",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Lớp phủ màu đen mờ bên dưới để chữ dễ đọc (nếu có)
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f)), startY = 200f))
                )
            }
        }

        // Chấm tròn chỉ dẫn (Indicator)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(banners.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) PrimaryOrange else Color.White.copy(alpha = 0.5f)
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(if (pagerState.currentPage == iteration) 10.dp else 8.dp)
                )
            }
        }
    }
}

@Composable
fun FoodItemStyledCard(food: Food, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(160.dp).height(230.dp).clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(130.dp)) {
                Image(painter = painterResource(id = food.imageUrl), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                Surface(modifier = Modifier.align(Alignment.TopStart).padding(8.dp), shape = RoundedCornerShape(8.dp), color = Color.White.copy(alpha = 0.9f)) {
                    Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(text = "${food.rating}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Column(modifier = Modifier.padding(12.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = food.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, color = Color.Black)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "${food.time} phút • ${food.kCal} kcal", fontSize = 11.sp, color = Color.Gray)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "${food.price / 1000}k", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryOrange)
                    Box(modifier = Modifier.size(28.dp).clip(CircleShape).background(PrimaryOrange), contentAlignment = Alignment.Center) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}