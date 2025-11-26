@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.foodapp.ui.screens.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.foodapp.R
import com.example.foodapp.data.FirestoreHelper
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.model.Voucher
import com.example.foodapp.ui.theme.PrimaryOrange
import com.example.foodapp.utils.toVND
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

// --------------------------- MODEL ---------------------------
data class ProfileMenuItem(
    val title: String,
    val icon: ImageVector,
    val iconColor: Color,
    val route: String? = null
)

// --------------------------- PROFILE SCREEN (CHÍNH) ---------------------------
@Composable
fun ProfileScreen(onNavigateToScreen: (String) -> Unit) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    // Quản lý trạng thái thông báo
    val sharedPrefs = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    var isNotificationEnabled by remember {
        mutableStateOf(sharedPrefs.getBoolean("push_notifications", true))
    }

    var userName by remember { mutableStateOf("Đang tải...") }
    var userEmail by remember { mutableStateOf("...") }
    var userAvatar by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            userEmail = auth.currentUser?.email ?: ""
            val userProfile = FirestoreHelper.getUserProfile(userId)
            if (userProfile != null) {
                userName = userProfile.username.ifBlank { "Người dùng mới" }
                userAvatar = userProfile.avatarUrl
            }
        }
    }

    // Hàm xử lý bật/tắt thông báo
    fun toggleNotification(enabled: Boolean) {
        isNotificationEnabled = enabled
        // Lưu vào bộ nhớ máy
        sharedPrefs.edit().putBoolean("push_notifications", enabled).apply()

        val fcm = FirebaseMessaging.getInstance()
        if (enabled) {
            // Đăng ký nhận thông báo từ topic chung
            fcm.subscribeToTopic("promotions")
                .addOnSuccessListener { Toast.makeText(context, "Đã BẬT thông báo", Toast.LENGTH_SHORT).show() }
        } else {
            // Hủy đăng ký
            fcm.unsubscribeFromTopic("promotions")
                .addOnSuccessListener { Toast.makeText(context, "Đã TẮT thông báo", Toast.LENGTH_SHORT).show() }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF2F4F8) // Màu nền xám xanh nhạt hiện đại
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(top = 20.dp, bottom = 40.dp)
        ) {
            // 1. Header
            item {
                ProfileHeaderModern(
                    name = userName,
                    email = userEmail,
                    avatarUrl = userAvatar,
                    onEditProfileClick = { onNavigateToScreen("edit_profile") }
                )
            }

            // 2. Nhóm Tài Khoản
            item {
                Text("Tài khoản", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(start = 8.dp, bottom = 4.dp))
                ProfileSectionGroup {
                    ProfileRowItem("Lịch sử Đơn hàng", Icons.Filled.Receipt, Color(0xFFFF7043)) { onNavigateToScreen("order_history") }
                    Divider(color = Color(0xFFF0F0F0), thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))
                    ProfileRowItem("Địa chỉ Giao hàng", Icons.Filled.LocationOn, Color(0xFF4CAF50)) { onNavigateToScreen("delivery_address") }
                    Divider(color = Color(0xFFF0F0F0), thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))
                    ProfileRowItem("Phương thức thanh toán", Icons.Filled.CreditCard, Color(0xFF1E88E5)) { onNavigateToScreen("payment_management") }
                    Divider(color = Color(0xFFF0F0F0), thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))
                    ProfileRowItem("Voucher & Ưu đãi", Icons.Filled.LocalOffer, Color(0xFFFFA000)) { onNavigateToScreen("voucher") }
                }
            }

            // 3. Nhóm Cài Đặt (Có nút Toggle)
            item {
                Text("Cài đặt", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(start = 8.dp, bottom = 4.dp))
                ProfileSectionGroup {
                    // Item Toggle Thông báo
                    ProfileToggleRowItem(
                        title = "Thông báo",
                        icon = Icons.Filled.Notifications,
                        iconColor = Color(0xFFFF9800),
                        checked = isNotificationEnabled,
                        onCheckedChange = { toggleNotification(it) }
                    )
                    Divider(color = Color(0xFFF0F0F0), thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))
                    ProfileRowItem("Bảo mật & Mật khẩu", Icons.Filled.Security, Color(0xFF9C27B0)) { onNavigateToScreen("security") }
                    Divider(color = Color(0xFFF0F0F0), thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))
                    ProfileRowItem("Cài đặt chung", Icons.Filled.Settings, Color(0xFF607D8B)) { onNavigateToScreen("app_settings") }
                }
            }

            // 4. Nhóm Hỗ trợ
            item {
                Text("Khác", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(start = 8.dp, bottom = 4.dp))
                ProfileSectionGroup {
                    ProfileRowItem("Trợ giúp & Liên hệ", Icons.Filled.SupportAgent, Color(0xFF009688)) { onNavigateToScreen("support") }
                    Divider(color = Color(0xFFF0F0F0), thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))
                    ProfileRowItem("Đăng xuất", Icons.Filled.Logout, Color(0xFFE53935), isDestructive = true) { onNavigateToScreen("logout") }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Phiên bản 1.0.0",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}

// --------------------------- MODERN COMPONENTS ---------------------------

@Composable
fun ProfileHeaderModern(name: String, email: String, avatarUrl: String, onEditProfileClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        val painter = if (avatarUrl.isNotBlank()) rememberAsyncImagePainter(model = avatarUrl) else painterResource(id = R.drawable.profile_burger)

        Box(contentAlignment = Alignment.BottomEnd) {
            Image(
                painter = painter,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.White, CircleShape), // Viền trắng
                contentScale = ContentScale.Crop
            )
            // Edit Icon nhỏ trên avatar
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(PrimaryOrange)
                    .clickable { onEditProfileClick() }
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Info
        Column(modifier = Modifier.weight(1f)) {
            Text(name, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.Black)
            Text(email, fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                color = PrimaryOrange.copy(alpha = 0.1f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    "Thành viên Vàng",
                    color = PrimaryOrange,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun ProfileSectionGroup(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp) // Flat style giống iOS
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            content = content
        )
    }
}

@Composable
fun ProfileRowItem(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon với nền màu nhạt
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = if (isDestructive) Color.Red else Color.Black,
            modifier = Modifier.weight(1f)
        )

        if (!isDestructive) {
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun ProfileToggleRowItem(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PrimaryOrange,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.LightGray
            ),
            modifier = Modifier.scale(0.8f)
        )
    }
}

// Extension function scale để chỉnh kích thước Switch
fun Modifier.scale(scale: Float) = this.then(Modifier.graphicsLayer(scaleX = scale, scaleY = scale))


// --------------------------- SUB SCREENS ---------------------------

// 1. CHỈNH SỬA HỒ SƠ (CÓ UPLOAD ẢNH & FIREBASE STORAGE)
@Composable
fun EditProfileScreen(onBack: () -> Unit) {
    val auth = FirebaseAuth.getInstance()
    val storage = FirebaseStorage.getInstance() // Instance của Storage
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // State dữ liệu người dùng
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var currentAvatarUrl by remember { mutableStateOf("") }

    // State chọn ảnh mới
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    var isLoading by remember { mutableStateOf(false) }
    var isDataLoaded by remember { mutableStateOf(false) }

    // Launcher để mở thư viện ảnh
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        // Khi người dùng chọn ảnh xong, uri sẽ trả về đây
        if (uri != null) {
            selectedImageUri = uri
        }
    }

    // Tải dữ liệu ban đầu
    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val user = FirestoreHelper.getUserProfile(userId)
            name = user?.username ?: ""
            phone = user?.phoneNumber ?: ""
            currentAvatarUrl = user?.avatarUrl ?: ""
            isDataLoaded = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chỉnh sửa Hồ sơ", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back") } }
            )
        }
    ) { padding ->
        if (!isDataLoaded) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryOrange)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()), // Cho phép cuộn nếu màn hình nhỏ
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- PHẦN AVATAR ---
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clickable {
                            // Mở trình chọn ảnh (Chỉ chọn ảnh)
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.BottomEnd
                ) {
                    // Logic hiển thị ảnh:
                    // 1. Nếu vừa chọn ảnh mới (selectedImageUri) -> Hiển thị ảnh đó (Preview)
                    // 2. Nếu không -> Hiển thị ảnh từ Server (currentAvatarUrl)
                    // 3. Nếu không có cả 2 -> Hiển thị Placeholder
                    val model = if (selectedImageUri != null) selectedImageUri else currentAvatarUrl

                    val painter = if (model.toString().isNotBlank()) {
                        rememberAsyncImagePainter(model)
                    } else {
                        painterResource(id = R.drawable.profile_burger)
                    }

                    Image(
                        painter = painter,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .border(2.dp, Color.LightGray, CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    // Icon Camera nhỏ
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PrimaryOrange)
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CameraAlt,
                            contentDescription = "Change Avatar",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                // --- FORM NHẬP LIỆU ---
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên hiển thị") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    leadingIcon = { Icon(imageVector = Icons.Filled.Person, contentDescription = null) }
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { if (it.all { c -> c.isDigit() }) phone = it },
                    label = { Text("Số điện thoại") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    leadingIcon = { Icon(imageVector = Icons.Filled.Phone, contentDescription = null) }
                )

                Spacer(Modifier.height(40.dp))

                // --- NÚT LƯU ---
                Button(
                    onClick = {
                        val userId = auth.currentUser?.uid
                        if (userId != null && name.isNotBlank()) {
                            isLoading = true
                            scope.launch {
                                try {
                                    var finalAvatarUrl = currentAvatarUrl

                                    // 1. NẾU CÓ CHỌN ẢNH MỚI -> UPLOAD LÊN STORAGE
                                    if (selectedImageUri != null) {
                                        // Tạo tên file ngẫu nhiên
                                        val storageRef = storage.reference.child("avatars/${userId}_${UUID.randomUUID()}.jpg")

                                        // Upload
                                        storageRef.putFile(selectedImageUri!!).await()

                                        // Lấy URL tải xuống
                                        finalAvatarUrl = storageRef.downloadUrl.await().toString()
                                    }

                                    // 2. GỌI HÀM UPDATE PROFILE TRONG HELPER (GỌN GÀNG)
                                    FirestoreHelper.updateUserProfile(userId, name, phone, finalAvatarUrl)

                                    // 3. Cập nhật Auth Profile (để hiển thị nhanh ở nơi khác nếu dùng Auth)
                                    val profileUpdates = UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .setPhotoUri(Uri.parse(finalAvatarUrl))
                                        .build()
                                    auth.currentUser?.updateProfile(profileUpdates)?.await()

                                    Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                                    isLoading = false
                                    onBack() // Quay lại màn hình Profile

                                } catch (e: Exception) {
                                    Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_LONG).show()
                                    isLoading = false
                                }
                            }
                        } else {
                            Toast.makeText(context, "Tên không được để trống", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Đang xử lý...", color = Color.White)
                    } else {
                        Text("Lưu Thay Đổi", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// 2. LỊCH SỬ ĐƠN HÀNG
@Composable
fun OrderHistoryScreen(onBack: () -> Unit) {
    val tabs = listOf("Đang xử lý", "Đang giao", "Hoàn tất", "Đã hủy")
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var orders by remember { mutableStateOf<List<Order>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val auth = FirebaseAuth.getInstance()

    DisposableEffect(Unit) {
        val userId = auth.currentUser?.uid
        var registration: ListenerRegistration? = null
        if (userId != null) {
            isLoading = true
            registration = FirestoreHelper.listenToUserOrders(userId) { newOrders ->
                orders = newOrders
                isLoading = false
            }
        } else { isLoading = false }
        onDispose { registration?.remove() }
    }

    val filteredOrders = orders.filter { order ->
        val status = order.status
        when (selectedTabIndex) {
            0 -> status == "Đang xử lý" || status == "Đang chuẩn bị"
            1 -> status == "Đang giao"
            2 -> status == "Hoàn tất" || status == "Đã hoàn thành"
            3 -> status == "Đã hủy"
            else -> true
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Lịch sử Đơn hàng", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Quay lại") } }) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            ScrollableTabRow(selectedTabIndex = selectedTabIndex, containerColor = Color.White, edgePadding = 0.dp, indicator = { tabPositions -> TabRowDefaults.Indicator(Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]), color = PrimaryOrange) }) {
                tabs.forEachIndexed { index, title ->
                    Tab(selected = selectedTabIndex == index, onClick = { selectedTabIndex = index }, text = { Text(title, fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal, color = if (selectedTabIndex == index) PrimaryOrange else Color.Gray) })
                }
            }
            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = PrimaryOrange) }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5)),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredOrders.size) { index -> OrderCard(filteredOrders[index]) }
                    if (filteredOrders.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                                Text("Chưa có đơn hàng nào.", color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Đơn #${order.id.takeLast(5).uppercase()}", fontWeight = FontWeight.Bold)
                val statusColor = when(order.status) { "Hoàn tất" -> Color(0xFF4CAF50); "Đã hủy" -> Color.Red; else -> PrimaryOrange }
                Text(order.status, color = statusColor, fontWeight = FontWeight.Bold)
            }
            Divider(Modifier.padding(vertical = 8.dp), color = Color(0xFFEEEEEE))
            val itemsSummary = order.items.joinToString(", ") { "${it.quantity}x ${it.foodName}" }
            Text(itemsSummary, maxLines = 2, overflow = TextOverflow.Ellipsis, color = Color.Gray, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Tổng tiền:", fontWeight = FontWeight.Medium)
                Text(order.finalAmount.toVND(), fontWeight = FontWeight.Bold, color = PrimaryOrange)
            }
        }
    }
}

// 3. ĐỊA CHỈ GIAO HÀNG
@Composable
fun DeliveryAddressScreen(onBack: () -> Unit, onEditClick: () -> Unit) {
    val auth = FirebaseAuth.getInstance()
    var address by remember { mutableStateOf("Đang tải...") }
    var phone by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userProfile = FirestoreHelper.getUserProfile(userId)
            address = userProfile?.address?.ifBlank { "" } ?: ""
            phone = userProfile?.phoneNumber ?: ""
        } else {
            address = ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Địa chỉ Giao hàng", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Quay lại") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF5F5F5)).padding(16.dp)
        ) {
            if (address == "Đang tải...") {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = PrimaryOrange) }
            } else if (address.isNotBlank()) {
                Text("Địa chỉ mặc định", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onEditClick() },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.LocationOn, contentDescription = null, tint = PrimaryOrange)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(address, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                if (phone.isNotBlank()) {
                                    Text(phone, color = Color.Gray, fontSize = 14.sp)
                                }
                            }
                        }
                        Icon(imageVector = Icons.Filled.Edit, contentDescription = "Sửa", tint = Color.Gray)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("* Nhấn vào khung trên để chỉnh sửa địa chỉ.", fontSize = 12.sp, color = Color.Gray, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)

            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Bạn chưa lưu địa chỉ nào.", color = Color.Gray)
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = onEditClick,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                        ) {
                            Text("Thêm địa chỉ mới", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// 4. VOUCHER
@Composable
fun VoucherScreen(onBack: () -> Unit) {
    var vouchers by remember { mutableStateOf<List<Voucher>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        FirestoreHelper.getAllVouchers {
            vouchers = it
            isLoading = false
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Voucher của bạn", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Quay lại") } }) }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = PrimaryOrange) }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF5F5F5)),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(vouchers.size) { index ->
                    val voucher = vouchers[index]
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(voucher.code, fontWeight = FontWeight.Bold, color = PrimaryOrange, fontSize = 18.sp)
                                Text("Giảm: ${voucher.discountAmount.toVND()}", fontWeight = FontWeight.Medium)
                                Text("Đơn tối thiểu: ${voucher.minOrderValue.toVND()}", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
                if (vouchers.isEmpty()) {
                    item {
                        Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            Text("Hiện không có mã giảm giá nào.", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

// 5. BẢO MẬT: ĐỔI MẬT KHẨU
@Composable
fun SecurityScreen(onBack: () -> Unit) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Đổi Mật Khẩu", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(24.dp)) {
            Text("Nhập mật khẩu mới. Vui lòng lưu ý: Firebase Auth yêu cầu phiên đăng nhập gần đây để thực hiện thao tác này.", color = Color.Gray)
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("Mật khẩu mới") },
                visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(imageVector = Icons.Filled.Lock, contentDescription = null) },
                trailingIcon = {
                    val image = if (newPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                        Icon(imageVector = image, contentDescription = "Toggle new password visibility")
                    }
                }
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Nhập lại mật khẩu mới") },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(imageVector = Icons.Filled.Lock, contentDescription = null) },
                trailingIcon = {
                    val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(imageVector = image, contentDescription = "Toggle confirm password visibility")
                    }
                }
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    if (newPassword.isBlank() || confirmPassword.isBlank()) {
                        Toast.makeText(context, "Vui lòng điền mật khẩu mới.", Toast.LENGTH_SHORT).show()
                    } else if (newPassword.length < 6) {
                        Toast.makeText(context, "Mật khẩu mới phải từ 6 ký tự", Toast.LENGTH_SHORT).show()
                    } else if (newPassword != confirmPassword) {
                        Toast.makeText(context, "Mật khẩu mới và xác nhận không khớp.", Toast.LENGTH_SHORT).show()
                    } else {
                        isLoading = true
                        auth.currentUser?.updatePassword(newPassword)
                            ?.addOnCompleteListener { updateTask ->
                                isLoading = false
                                if (updateTask.isSuccessful) {
                                    Toast.makeText(context, "Đổi mật khẩu thành công!", Toast.LENGTH_LONG).show()
                                    onBack()
                                } else {
                                    Toast.makeText(context, "Lỗi: ${updateTask.exception?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                 if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                 else Text("Xác nhận đổi", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// 6. TRỢ GIÚP & LIÊN HỆ
@Composable
fun SupportScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val hotline = "0918720115"
    val email = "quoctri1014@gmail.com"

    Scaffold(
        topBar = { TopAppBar(title = { Text("Trợ giúp & Liên hệ", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Quay lại") } }) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth().clickable {
                    try {
                        val intent = Intent(Intent.ACTION_DIAL).apply { data = Uri.parse("tel:$hotline") }
                        context.startActivity(intent)
                    } catch (e: Exception) { Toast.makeText(context, "Lỗi: Không thể gọi điện", Toast.LENGTH_SHORT).show() }
                },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.Phone, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(32.dp))
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("Tổng đài hỗ trợ", fontWeight = FontWeight.Bold)
                        Text(hotline, color = PrimaryOrange, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Card(
                modifier = Modifier.fillMaxWidth().clickable {
                    try {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:$email")
                            putExtra(Intent.EXTRA_SUBJECT, "Hỗ trợ FoodApp")
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) { Toast.makeText(context, "Lỗi: Không tìm thấy ứng dụng Email", Toast.LENGTH_SHORT).show() }
                },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.Email, contentDescription = null, tint = Color(0xFF2196F3), modifier = Modifier.size(32.dp))
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("Email hỗ trợ", fontWeight = FontWeight.Bold)
                        Text(email, color = Color.Gray)
                    }
                }
            }
        }
    }
}

// 7. CÁC TÍNH NĂNG KHÁC
@Composable
fun PaymentManagementScreen(onBack: () -> Unit) { PlaceholderScreen("Quản lý Thanh toán", onBack) }
@Composable
fun AppSettingsScreen(onBack: () -> Unit) { PlaceholderScreen("Cài đặt Ứng dụng", onBack) }

@Composable
fun PlaceholderScreen(title: String, onBack: () -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text(title, fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Quay lại") } }) }) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(imageVector = Icons.Filled.Construction, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                Spacer(Modifier.height(16.dp))
                Text("Tính năng đang được phát triển", color = Color.Gray, fontSize = 16.sp)
            }
        }
    }
}