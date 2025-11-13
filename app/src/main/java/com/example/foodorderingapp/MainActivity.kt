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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.ui.text.input.ImeAction
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
import com.example.foodorderingapp.data.CartManager
import com.example.foodorderingapp.data.Conversation
import com.example.foodorderingapp.data.FavoriteManager
import com.example.foodorderingapp.data.MockData
import com.example.foodorderingapp.data.Product
import com.example.foodorderingapp.data.Sender
import com.example.foodorderingapp.ui.theme.FoodOrderingAppTheme
import java.text.NumberFormat
import androidx.compose.ui.text.input.KeyboardType
import com.example.foodorderingapp.data.ChatManager
import com.example.foodorderingapp.data.ConversationStateManager
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.navigation.NavHostController
import com.example.foodorderingapp.data.MockData.sampleProducts
import java.util.*
import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext

sealed class Screen(val route: String, val icon: ImageVector, val label: String) {
    data object Home : Screen("home", Icons.Filled.Home, "Home")
    data object Cart : Screen("cart", Icons.Filled.ShoppingCart, "Cart")
    data object Favorite : Screen("favorite", Icons.Filled.Favorite, "Favorite")
    data object Message : Screen("message", Icons.Filled.MailOutline, "Message")
    data object Profile : Screen("profile", Icons.Filled.Person, "Profile")
    data object AllProducts : Screen("all_products/{title}", Icons.Filled.Search, "All Products")
    data object ChatDetail : Screen("chat_detail/{userName}", Icons.Filled.MailOutline, "Chat Detail")
    data object Search : Screen("search", Icons.Filled.Search, "Search")
}
val items = listOf(Screen.Home, Screen.Cart, Screen.Favorite,Screen.Message, Screen.Profile)
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
            composable(Screen.Search.route) {
                ProductSearchScreen(navController)
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
            composable(Screen.Message.route) {
                MessageScreen(navController)
            }
            composable(
                route = Screen.ChatDetail.route,
                arguments = listOf(navArgument("userName") { type = NavType.StringType })
            ) { backStackEntry ->
                val userName = backStackEntry.arguments?.getString("userName") ?: "Chat"
                ChatDetailScreen(navController, userName)
            }
        }
    }
}
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
@Composable
fun HomeScreen(navController: NavController, modifier: Modifier = Modifier) {
    val products = MockData.sampleProducts
    val bannerImages = MockData.sampleBanners
    val context = LocalContext.current
    val navigateToSearch: () -> Unit = {
        navController.navigate(Screen.Search.route)
    }
    val navigateToAllProducts: (String) -> Unit = { title ->
        navController.navigate("all_products/$title")
    }
    val voiceLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText: String? = result.data?.getStringArrayListExtra(
                RecognizerIntent.EXTRA_RESULTS
            )?.get(0)
            if (spokenText != null) {
                navController.navigate(Screen.Search.route)
            }
        }
    }
    val startVoiceSearch: () -> Unit = {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Nói tên món ăn bạn muốn tìm kiếm")
        }
        // Gọi Launcher để kích hoạt Intent và chờ kết quả
        voiceLauncher.launch(intent)
    }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            SearchBarLayout(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                onClick = navigateToSearch,
                onClickVoice = startVoiceSearch
            )
        }
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
            SearchBarLayout(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                onClick = { navController.navigate(Screen.Search.route) },
                onClickVoice = { navController.navigate(Screen.Search.route) }
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
fun SearchBarLayout(modifier: Modifier = Modifier, onClick: () -> Unit = {}, onClickVoice: () -> Unit = {}) { // <--- THÊM onClick
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Search, contentDescription = "Search", tint = Color.Gray)
        Spacer(Modifier.width(8.dp))
        Box(modifier = Modifier.weight(1f)) {
            Text(text = "Search...", color = Color.Gray)
        }
        IconButton(onClick = onClickVoice) {
            Icon(Icons.Filled.Mic, contentDescription = "Voice Search", tint = Color.Gray)
        }
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
                        .clickable { }
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
                    tint = if (isFavorited) Color.Red else Color.White.copy(alpha = 0.8f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(24.dp)
                        .clickable {
                            FavoriteManager.toggleFavorite(product)
                        }
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = product.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Text(
                text = "${product.rating} ★",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Spacer(Modifier.weight(1f))
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
                        CartManager.addItemToCart(product)
                    }
                )
            }
        }
    }
}
@Composable
fun CartScreen() {
    val cartItems = CartManager.cartItems
    val shippingFee = 25000.0
    val subtotal = CartManager.getSubtotal()
    val total = subtotal + shippingFee
    Scaffold(
        topBar = { CartTopBar() },
        bottomBar = { if (cartItems.isNotEmpty()) CartCheckoutButton(total) },
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
            onClick = {  },
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
    val favoriteIds = FavoriteManager.favoriteProductIds
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
                val chunkedProducts = favoriteProducts.chunked(2)
                items(chunkedProducts) { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        row.forEach { product ->
                            ProductGridItem(
                                product = product,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(6.dp)
                                    .heightIn(min = 220.dp)
                            )
                        }
                        if (row.size == 1) {
                            Spacer(modifier = Modifier.weight(1f).padding(6.dp))
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun MessageScreen(navController: NavController) {
    val conversations = MockData.sampleConversations
    Scaffold(
        topBar = { SimpleAppBarCompose(title = "Tin nhắn", onBack = { }) },
        containerColor = Color.White
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(conversations, key = { it.id }) { conversation ->
                ConversationItem(
                    conversation = conversation,
                    onClick = {
                        navController.navigate("chat_detail/${conversation.userName}")
                    }
                )
                Divider(color = WhiteSmoke, thickness = 1.dp)
            }
        }
    }
}
@Composable
fun ConversationItem(conversation: Conversation, onClick: () -> Unit) {
    val (lastMessageState, timestampState) = ConversationStateManager.getConversationState(
        userName = conversation.userName,
        initialMessage = conversation.lastMessage,
        initialTimestamp = conversation.timestamp
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = conversation.userName, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Text(
                text = lastMessageState.value, color = Color.Gray, fontSize = 14.sp, maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = timestampState.value, color = Color.DarkGray, fontSize = 12.sp
        )
    }
}
@Composable
fun MessageBubble(message: com.example.foodorderingapp.data.Message) {
    val isUser = message.sender == Sender.USER
    val alignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    val bubbleColor = if (isUser) PrimaryOrange else Color.White
    val textColor = if (isUser) Color.White else Color.Black
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Card(
            shape = RoundedCornerShape(
                topStart = 16.dp, topEnd = 16.dp, bottomStart = if (isUser) 16.dp else 4.dp, bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(containerColor = bubbleColor), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp), modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Text(
                text = message.text, color = textColor, fontSize = 15.sp, modifier = Modifier.padding(10.dp)
            )
        }
    }
}
@Composable
fun ChatDetailScreen(navController: NavController, userName: String) {
    val chatMessages = ChatManager.getMessagesForUser(userName)
    val onSendMessage: (String) -> Unit = { text ->
        ChatManager.sendMessage(userName, text, Sender.USER)
    }
    Scaffold(
        topBar = {
            SimpleAppBarCompose(title = userName, onBack = { navController.popBackStack() })
        },
        bottomBar = { ChatInputField(onSendMessage = onSendMessage) }, containerColor = Color(0xFFF7E2C9)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 4.dp),
            reverseLayout = true
        ) {
            items(chatMessages.reversed(), key = { it.id }) { message ->
                MessageBubble(message = message)
            }
        }
    }
}
@Composable
fun ChatInputField(onSendMessage: (String) -> Unit) {
    var message by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = message,
            onValueChange = { message = it }, placeholder = { Text("Nhập tin nhắn...") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(24.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text, imeAction = ImeAction.Send
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryOrange, unfocusedBorderColor = Color.LightGray, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
            )
        )
        Spacer(Modifier.width(8.dp))
        FloatingActionButton(
            onClick = {
                if (message.isNotBlank()) {
                    onSendMessage(message)
                    message = ""
                }
            },
            containerColor = PrimaryOrange, modifier = Modifier.size(48.dp)
        ) {
            Icon(Icons.Filled.Send, contentDescription = "Gửi", tint = Color.White)
        }
    }
}
@Composable
fun ProductSearchScreen(navController: NavHostController) {
    var searchText by remember { mutableStateOf("") }
    val filteredProducts = remember(searchText) {
        if (searchText.isBlank()) {
            emptyList()
        } else {
            sampleProducts.filter { product ->
                product.name.contains(searchText, ignoreCase = true)
            }
        }
    }
    val chunkedProducts = filteredProducts.chunked(2)
    Scaffold(
        topBar = {
            Column {
                SimpleAppBarCompose(title = "Tìm kiếm", onBack = { navController.popBackStack() })
                SearchInputField(
                    searchText = searchText,
                    onSearchTextChange = { searchText = it }
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 10.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (searchText.isNotBlank() && filteredProducts.isEmpty()) {
                item {
                    Text(
                        "Không tìm thấy món ăn nào khớp với '${searchText}'",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else if (searchText.isBlank()) {
                item {
                    Text(
                        "Hãy nhập tên món ăn để tìm kiếm...",
                        modifier = Modifier.padding(16.dp),
                        color = Color.Gray
                    )
                }
            }
            items(chunkedProducts) { row ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    row.forEach { product ->
                        ProductGridItem(
                            product = product,
                            modifier = Modifier.weight(1f).padding(6.dp)
                        )
                    }
                    if (row.size == 1) {
                        Spacer(modifier = Modifier.weight(1f).padding(6.dp))
                    }
                }
            }
        }
    }
}
@Composable
fun SearchInputField(searchText: String, onSearchTextChange: (String) -> Unit) {
    OutlinedTextField(
        value = searchText,
        onValueChange = onSearchTextChange,
        placeholder = { Text("Search...") },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Tìm kiếm") },
        trailingIcon = {
            if (searchText.isNotBlank()) {
                IconButton(onClick = { onSearchTextChange("") }) {
                    Icon(Icons.Filled.Clear, contentDescription = "Xóa")
                }
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        singleLine = true,
        shape = RoundedCornerShape(24.dp)
    )
}