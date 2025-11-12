package com.example.foodorderingapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.foodorderingapp.data.CartItem
import com.example.foodorderingapp.data.CartManager // <-- IMPORT CartManager
import com.example.foodorderingapp.data.FavoriteManager
import com.example.foodorderingapp.data.MockData
import com.example.foodorderingapp.data.Product
import com.example.foodorderingapp.ui.theme.FoodOrderingAppTheme
import java.text.NumberFormat
import java.util.*

// --- NAVIGATION ROUTES ---
sealed class Screen(val route: String, val icon: ImageVector, val label: String) {
    data object Home : Screen("home", Icons.Filled.Home, "Home")
    data object Cart : Screen("cart", Icons.Filled.ShoppingCart, "Cart")
    data object Favorite : Screen("favorite", Icons.Filled.Favorite, "Favorite")
    data object Profile : Screen("profile", Icons.Filled.Person, "Profile")
    data object AllProducts : Screen("all_products/{title}", Icons.Filled.Search, "All Products")
}
val items = listOf(Screen.Home, Screen.Cart, Screen.Favorite, Screen.Profile)
// -------------------------

// Màu sắc Compose
val PrimaryOrange = Color(0xFFFFC107)
val WhiteSmoke = Color(0xFFF5F5F5)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FoodOrderingAppTheme {
                MainAppContent()
            }
        }
    }
}

@Composable
fun MainAppContent() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = { AppBottomBar(navController, currentRoute) },
        containerColor = WhiteSmoke
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(navController = navController)
            }
            composable(Screen.Cart.route) {
                CartScreen()
            }
            composable(Screen.Favorite.route) {

                FavoriteProductsScreen(navController)
            }
            composable(Screen.Profile.route) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Profile Screen") }
            }

            composable(
                route = Screen.AllProducts.route,
                arguments = listOf(navArgument("title") { type = NavType.StringType })
            ) { backStackEntry ->
                val title = backStackEntry.arguments?.getString("title") ?: "Tất cả Món ăn"
                AllProductsScreenCompose(
                    title = title,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}


// --- BOTTOM BAR ---

@Composable
fun AppBottomBar(navController: NavController, currentRoute: String?) {
    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        items.forEach { screen ->
            val selected = currentRoute == screen.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(screen.icon, contentDescription = screen.label)
                },
                label = {
                    Text(
                        screen.label,
                        color = if (selected) PrimaryOrange else Color.Gray,
                        fontSize = 12.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryOrange,
                    unselectedIconColor = Color.Gray,
                    indicatorColor = Color.White
                )
            )
        }
    }
}


// --- HOME SCREEN COMPOSE ---

@Composable
fun HomeScreen(navController: NavController, modifier: Modifier = Modifier) {
    val products = MockData.sampleProducts
    val bannerImages = MockData.sampleBanners

    val navigateToAllProducts: (String) -> Unit = { title ->
        navController.navigate("all_products/$title")
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item { SearchBarLayout(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) }
        // BannerSection sử dụng LazyRow
        item { BannerSection(bannerImages, Modifier.padding(vertical = 12.dp)) }

        item {
            HorizontalProductSection(
                title = "Our trusted picks",
                products = products,
                onViewAllClick = { navigateToAllProducts("Our trusted picks") },
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )
        }

        item {
            Text(
                text = "More to love",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )
        }

        val chunkedProducts = products.chunked(2)
        items(chunkedProducts) { row ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                row.forEach { product ->
                    ProductGridItem(product = product, modifier = Modifier.weight(1f).padding(6.dp))
                }
            }
        }
    }
}

// --- SearchBar and BannerSection ---

@Composable
fun SearchBarLayout(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Search, contentDescription = "Search", tint = Color.Gray)
        Spacer(Modifier.width(8.dp))
        Text(text = "Search...", color = Color.Gray, modifier = Modifier.weight(1f))
        Icon(Icons.Filled.Mic, contentDescription = "Voice Search", tint = Color.Gray)
    }
}

@Composable
fun BannerSection(imageUrls: List<Int>, modifier: Modifier = Modifier) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(imageUrls) { imageUrl ->
            Card(
                modifier = Modifier.width(340.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Image(
                    painter = painterResource(id = imageUrl),
                    contentDescription = "Banner quảng cáo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clickable { /* Handle banner click */ }
                )
            }
        }
    }
}

@Composable
fun HorizontalProductSection(
    title: String,
    products: List<Product>,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(
                text = "View all",
                color = PrimaryOrange,
                fontSize = 14.sp,
                modifier = Modifier.clickable(onClick = onViewAllClick)
            )
        }
        Spacer(Modifier.height(8.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(products) { product: Product ->
                ProductGridItem(product = product, isHorizontal = true)
            }
        }
    }
}


@Composable
fun ProductGridItem(product: Product, modifier: Modifier = Modifier, isHorizontal: Boolean = false) {
    val isFavorited = FavoriteManager.isFavorite(product.id)
    Card(
        modifier = modifier
            .width(if (isHorizontal) 160.dp else Dp.Unspecified)
            .height(220.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            // Image and Favorite Icon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                Image(
                    painter = painterResource(id = product.imageUrl),
                    contentDescription = "Product Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                )
                Icon(
                    Icons.Filled.Favorite,
                    contentDescription = "Favorite",
                    // Màu sắc thay đổi: Đỏ nếu được yêu thích, Trắng mờ nếu chưa
                    tint = if (isFavorited) Color.Red else Color.White.copy(alpha = 0.8f),
                    modifier = Modifier
                        .align(Alignment.TopEnd) // Đặt ở góc trên bên phải
                        .padding(4.dp)
                        .size(24.dp)
                        .clickable {
                            FavoriteManager.toggleFavorite(product)
                        }
                )

            }

            Spacer(Modifier.height(8.dp))

            // Name
            Text(
                text = product.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )

            // Rating
            Text(
                text = "${product.rating} ★",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(Modifier.weight(1f))

            // Price and Add button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    NumberFormat.getCurrencyInstance(Locale.Builder().setLanguage("vi").setRegion("VN").build())
                } else {
                    @Suppress("DEPRECATION")
                    NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
                }

                Text(
                    text = format.format(product.price),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryOrange
                )

                Icon(
                    Icons.Filled.AddCircle,
                    contentDescription = "Add to Cart",
                    tint = PrimaryOrange,
                    modifier = Modifier.size(32.dp).clickable {
                        // XỬ LÝ THÊM SẢN PHẨM VÀO GIỎ HÀNG
                        CartManager.addItemToCart(product)
                    }
                )
            }
        }
    }
}


// --- CART SCREEN COMPOSE (Đã cập nhật sử dụng CartManager) ---

@Composable
fun CartScreen() {
    // Lấy dữ liệu giỏ hàng từ CartManager
    val cartItems = CartManager.cartItems

    val shippingFee = 25000.0
    val subtotal = CartManager.getSubtotal() // Lấy tổng từ Manager
    val total = subtotal + shippingFee

    Scaffold(
        topBar = { CartTopBar() },
        bottomBar = { if (cartItems.isNotEmpty()) CartCheckoutButton(total) }, // Chỉ hiện nút Checkout khi có sản phẩm
        containerColor = WhiteSmoke
    ) { paddingValues ->

        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Giỏ hàng của bạn đang rỗng",
                    fontSize = 18.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.SemiBold
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(cartItems, key = { it.product.id }) { item ->
                    CartItemCard(
                        item = item,
                        onIncrease = { CartManager.increaseQuantity(item) },
                        onDecrease = { CartManager.decreaseQuantity(item) },
                        onRemove = { CartManager.removeItem(item) }
                    )
                }

                item { Spacer(Modifier.height(16.dp)) }

                item { OrderSummaryCard(subtotal, shippingFee, total) }

                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartTopBar() {
    TopAppBar(
        title = { Text("Giỏ hàng", fontWeight = FontWeight.Bold, color = Color.Black) },
        navigationIcon = {
            IconButton(onClick = { /* Handle back navigation */ }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
    )
}

// --- CartItemCard đã nhận callback ---
@Composable
fun CartItemCard(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = item.product.imageUrl),
                contentDescription = item.product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.product.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1)
                Text(
                    text = formatCurrency(item.product.price),
                    color = PrimaryOrange,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }

            // Bộ đếm số lượng và nút xóa
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDecrease) {
                        Icon(Icons.Filled.RemoveCircle, contentDescription = "Decrease", tint = Color.Gray)
                    }
                    Text("${item.quantity}", fontWeight = FontWeight.Bold)
                    IconButton(onClick = onIncrease) {
                        Icon(Icons.Filled.AddCircle, contentDescription = "Increase", tint = PrimaryOrange)
                    }
                }
                Spacer(Modifier.height(4.dp))
                // Nút xóa
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Remove",
                    tint = Color.Red,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(onClick = onRemove)
                )
            }
        }
    }
}

// --- Các hàm phụ trợ (Giữ nguyên) ---

@Composable
fun OrderSummaryCard(subtotal: Double, shippingFee: Double, total: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SummaryRow("Tổng phụ:", subtotal)
            SummaryRow("Phí giao hàng:", shippingFee)
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            SummaryRow("TỔNG CỘNG:", total, isTotal = true)
        }
    }
}

@Composable
fun SummaryRow(label: String, amount: Double, isTotal: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal,
            fontSize = if (isTotal) 18.sp else 16.sp
        )
        Text(
            formatCurrency(amount),
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.SemiBold,
            color = if (isTotal) PrimaryOrange else Color.Black,
            fontSize = if (isTotal) 18.sp else 16.sp
        )
    }
}

@Composable
fun CartCheckoutButton(total: Double) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Button(
            onClick = { /* Handle checkout action */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
            contentPadding = PaddingValues(vertical = 14.dp)
        ) {
            Text(
                "Thanh toán ${formatCurrency(total)}",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun formatCurrency(amount: Double): String {
    val format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        NumberFormat.getCurrencyInstance(Locale.Builder().setLanguage("vi").setRegion("VN").build())
    } else {
        @Suppress("DEPRECATION")
        NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    }
    return format.format(amount)
}

// --- MÀN HÌNH TẤT CẢ SẢN PHẨM ---

@Composable
fun AllProductsScreenCompose(title: String, onBack: () -> Unit) {
    val allProducts = MockData.sampleProducts

    Scaffold(
        topBar = { SimpleAppBarCompose(title = title, onBack = onBack) },
        containerColor = WhiteSmoke
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 10.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val chunkedProducts = allProducts.chunked(2)
            items(chunkedProducts) { row ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    row.forEach { product ->
                        ProductGridItem(product = product, modifier = Modifier.weight(1f).padding(6.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleAppBarCompose(title: String, onBack: () -> Unit) {
    TopAppBar(
        title = { Text(text = title, fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
    )
}

@Composable
fun FavoriteProductsScreen(navController: NavController) {
    // Theo dõi danh sách ID sản phẩm yêu thích
    val favoriteIds = FavoriteManager.favoriteProductIds

    // Lọc ra các sản phẩm yêu thích từ MockData
    val favoriteProducts = MockData.sampleProducts.filter { favoriteIds.contains(it.id) }

    Scaffold(
        topBar = { SimpleAppBarCompose(title = "Món ăn yêu thích", onBack = { navController.popBackStack() }) },
        containerColor = WhiteSmoke
    ) { paddingValues ->
        if (favoriteProducts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Bạn chưa có món ăn yêu thích nào.",
                    color = Color.Gray,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 10.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Hiển thị sản phẩm theo dạng Grid 2 cột
                val chunkedProducts = favoriteProducts.chunked(2)
                items(chunkedProducts) { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        row.forEach { product ->
                            // Sử dụng ProductGridItem với chiều cao cố định để đảm bảo ảnh không bị cắt ngang
                            ProductGridItem(
                                product = product,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(6.dp)
                                    // Đặt chiều cao tối thiểu để đảm bảo bố cục đồng nhất
                                    .heightIn(min = 220.dp)
                            )
                        }
                        // Thêm Spacer nếu hàng chỉ có 1 sản phẩm để căn chỉnh (Fix bug layout 1 item)
                        if (row.size == 1) {
                            Spacer(modifier = Modifier.weight(1f).padding(6.dp))
                        }
                    }
                }
            }
        }
    }
}