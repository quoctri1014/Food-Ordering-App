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
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.foodorderingapp.data.CartItem
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
        bottomBar = { AppBottomBar(navController, currentRoute) }, // Truyền NavController
        containerColor = WhiteSmoke
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen()
            }
            composable(Screen.Cart.route) {
                CartScreen()
            }
            composable(Screen.Favorite.route) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Favorite Screen") }
            }
            composable(Screen.Profile.route) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Profile Screen") }
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
                        fontSize = 12.sp // Giảm kích thước chữ để phù hợp với 4 item
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
fun HomeScreen(modifier: Modifier = Modifier) {
    val products = MockData.sampleProducts
    val bannerImages = MockData.sampleBanners

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item { SearchBarLayout(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) }
        item { BannerSection(bannerImages, Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) }

        item {
            HorizontalProductSection(
                title = "Our trusted picks",
                products = products,
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
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Image(
            painter = painterResource(id = imageUrls.first()),
            contentDescription = "Banner",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )
    }
}

@Composable
fun HorizontalProductSection(title: String, products: List<Product>, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = "View all", color = PrimaryOrange, fontSize = 14.sp, modifier = Modifier.clickable { /* Handle View All */ })
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
                    tint = Color.Red,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
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
                // Sửa lỗi Deprecated Locale
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
                    modifier = Modifier.size(32.dp).clickable { /* Handle Add to Cart */ }
                )
            }
        }
    }
}


// --- CART SCREEN COMPOSE ---

@Composable
fun CartScreen() {
    val cartItems = MockData.sampleCartItems
    val shippingFee = 25000.0
    val subtotal = cartItems.sumOf { it.product.price * it.quantity }
    val total = subtotal + shippingFee

    Scaffold(
        topBar = { CartTopBar() },
        bottomBar = { CartCheckoutButton(total) },
        containerColor = WhiteSmoke
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(cartItems) { item ->
                CartItemCard(item)
            }

            item { Spacer(Modifier.height(16.dp)) }

            item { OrderSummaryCard(subtotal, shippingFee, total) }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartTopBar() {
    // Lưu ý: Icon ArrowBack đã được chuyển sang Icons.AutoMirrored.Filled.ArrowBack
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

@Composable
fun CartItemCard(item: CartItem) {
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
            // Ảnh sản phẩm
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
                    IconButton(onClick = { /* Decrease quantity */ }) {
                        Icon(Icons.Filled.RemoveCircle, contentDescription = "Decrease", tint = Color.Gray)
                    }
                    Text("${item.quantity}", fontWeight = FontWeight.Bold)
                    IconButton(onClick = { /* Increase quantity */ }) {
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
                        .clickable { /* Handle item removal */ }
                )
            }
        }
    }
}

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

// Hàm hỗ trợ format tiền tệ
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