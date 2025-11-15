@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.foodapp.ui.screens.profile // Giữ nguyên package này

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image // ⭐ Cần thêm cho ảnh profile
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodapp.ui.theme.PrimaryOrange
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import com.example.foodapp.R
import coil.compose.rememberAsyncImagePainter

// --------------------------- MODEL ---------------------------
data class ProfileMenuItem(
    val title: String,
    val subtitle: String? = null,
    val icon: ImageVector,
    val iconColor: Color,
    val route: String? = null
)

data class PaymentMethod(val type: String, val icon: ImageVector, val lastDigits: String, val isCOD: Boolean = false)
data class Voucher(val code: String, val discount: String, val expiry: String, val condition: String)

// --------------------------- MENU LIST ---------------------------
val profileMenuItems = listOf(
    ProfileMenuItem("Lịch sử Đơn hàng", "Chi tiết các đơn gần đây", Icons.Default.Receipt, Color(0xFFFF7043), "order_history"),
    ProfileMenuItem("Địa chỉ Giao hàng", "Quản lý địa chỉ nhận hàng", Icons.Default.LocationOn, Color(0xFF4CAF50), "delivery_address"),
    ProfileMenuItem("Thanh toán", "Thẻ, ví & COD", Icons.Default.CreditCard, Color(0xFF1E88E5), "payment_management"),
    ProfileMenuItem("Voucher & Khuyến mãi", "Xem mã giảm giá", Icons.Default.LocalOffer, Color(0xFFFFA000), "voucher"),
    ProfileMenuItem("Bảo mật & Quyền riêng tư", "Mật khẩu, sinh trắc học", Icons.Default.Security, Color(0xFF9C27B0), "security"),
    ProfileMenuItem("Cài đặt Ứng dụng", "Ngôn ngữ & giao diện", Icons.Default.Settings, Color(0xFF607D8B), "app_settings"),
    ProfileMenuItem("Trợ giúp & Liên hệ", "Hỗ trợ, phản hồi", Icons.Default.Help, Color(0xFF4CAF50), "support")
)

// --------------------------- PROFILE SCREEN ---------------------------
@Composable
fun ProfileScreen(onNavigateToScreen: (String) -> Unit) {
    var pushNotificationEnabled by remember { mutableStateOf(true) }
    var smsNotificationEnabled by remember { mutableStateOf(false) }

    Scaffold(containerColor = Color(0xFFF5F5F5)) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                ProfileHeader(
                    name = "BurgerKingg",
                    email = "burgerkingshop@gmail.com",
                    onEditProfileClick = { onNavigateToScreen("edit_profile") }
                )
            }

            items(profileMenuItems) { item ->
                ProfileNavigationItem(item = item) {
                    item.route?.let { onNavigateToScreen(it) }
                }
            }

            item {
                ProfileToggleItem(
                    item = ProfileMenuItem("Thông báo Đẩy", "Nhận thông tin ưu đãi", Icons.Default.Notifications, Color(0xFFFF9800)),
                    checked = pushNotificationEnabled,
                    onCheckedChange = { pushNotificationEnabled = it }
                )
            }

            item {
                ProfileToggleItem(
                    item = ProfileMenuItem("Thông báo SMS", "Thông tin trong ứng dụng", Icons.Default.Sms, Color(0xFF4CAF50)),
                    checked = smsNotificationEnabled,
                    onCheckedChange = { smsNotificationEnabled = it }
                )
            }

            item {
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { onNavigateToScreen("logout") },
                    modifier = Modifier.fillMaxWidth().height(55.dp), // Tăng chiều cao
                    shape = RoundedCornerShape(14.dp), // Bo góc mềm hơn
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                ) {
                    Text("Đăng Xuất", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 17.sp) // Tăng size chữ
                }
            }
        }
    }
}

// --------------------------- HEADER---------------------------
@Composable
fun ProfileHeader(name: String, email: String, onEditProfileClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp) // Thêm bóng
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val imageResId = com.example.foodapp.R.drawable.profile_burger

            Box(
                modifier = Modifier.size(120.dp).clip(CircleShape)
                    .border(3.dp, PrimaryOrange, CircleShape),
                contentAlignment = Alignment.Center
            ) {

                Image(
                    painter = rememberAsyncImagePainter(model = imageResId),
                    contentDescription = "Ảnh đại diện Burger King",
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                    contentScale = ContentScale.Crop // Crop ảnh cho vừa với hình tròn
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(name, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp) // Tăng size chữ
            Text(email, fontSize = 15.sp, color = Color.Gray) // Tăng size chữ
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onEditProfileClick,
                modifier = Modifier.width(200.dp).height(45.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
            ) {
                Text("Chỉnh sửa Hồ sơ", color = Color.White, fontSize = 15.sp)
            }
        }
    }
}
// --------------------------- NAV ITEM ---------------------------
@Composable
fun ProfileNavigationItem(item: ProfileMenuItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp), // Bo góc mềm hơn
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp) // Tăng độ nổi
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp), // Tăng padding
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(45.dp) // Tăng size icon box
                        .clip(CircleShape)
                        .background(item.iconColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(item.icon, null, tint = item.iconColor, modifier = Modifier.size(26.dp))
                }
                Spacer(Modifier.width(18.dp)) // Tăng khoảng cách
                Column {
                    Text(item.title, fontWeight = FontWeight.SemiBold, fontSize = 17.sp) // Tăng size chữ
                    item.subtitle?.let { Text(it, fontSize = 13.sp, color = Color.Gray) } // Tăng size chữ
                }
            }
            Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.Gray.copy(alpha = 0.6f))
        }
    }
}

// --------------------------- TOGGLE ITEM ---------------------------
@Composable
fun ProfileToggleItem(item: ProfileMenuItem, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp), // Bo góc mềm hơn
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp) // Tăng độ nổi
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp), // Tăng padding
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(45.dp) // Tăng size icon box
                        .clip(CircleShape)
                        .background(item.iconColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(item.icon, null, tint = item.iconColor, modifier = Modifier.size(26.dp))
                }
                Spacer(Modifier.width(18.dp)) // Tăng khoảng cách
                Column {
                    Text(item.title, fontWeight = FontWeight.SemiBold, fontSize = 17.sp) // Tăng size chữ
                    item.subtitle?.let { Text(it, fontSize = 13.sp, color = Color.Gray) } // Tăng size chữ
                }
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = PrimaryOrange
                )
            )
        }
    }
}

// --------------------------- UPDATED SUB-SCREENS ---------------------------

/** 1. EDIT PROFILE SCREEN */
@Composable
fun EditProfileScreen(onBack: () -> Unit) {
    var fullName by rememberSaveable { mutableStateOf("BurgerKingg") }
    var phone by rememberSaveable { mutableStateOf("0901234567") }
    var email by rememberSaveable { mutableStateOf("burgerkingshop@gmail.com") }
    var selectedDate by rememberSaveable { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    val dateState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate ?: Instant.now().toEpochMilli()
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chỉnh sửa Hồ sơ", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp)
        ) {
            item {
                Box(
                    modifier = Modifier.size(120.dp).clip(CircleShape)
                        .background(Color(0xFFE0E0E0))
                        .border(3.dp, PrimaryOrange, CircleShape)
                        .clickable { /* Mở Image Picker */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Chỉnh sửa ảnh", modifier = Modifier.size(48.dp), tint = Color.Gray)
                }
                Text("Nhấn để thay đổi ảnh", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp, bottom = 32.dp))
            }

            item {
                OutlinedTextField(
                    value = fullName, onValueChange = { fullName = it }, label = { Text("Tên đầy đủ", fontSize = 15.sp) },
                    leadingIcon = { Icon(Icons.Default.Person, null) }, shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = phone, onValueChange = { phone = it }, label = { Text("Số điện thoại", fontSize = 15.sp) },
                    leadingIcon = { Icon(Icons.Default.Phone, null) }, shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = email, onValueChange = { /* read-only */ }, label = { Text("Email", fontSize = 15.sp) },
                    leadingIcon = { Icon(Icons.Default.Email, null) }, enabled = false,
                    shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
                )
            }

            item {
                val dateText = selectedDate?.let {
                    Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                } ?: "Chọn ngày sinh"

                OutlinedTextField(
                    value = dateText, onValueChange = { }, label = { Text("Ngày sinh", fontSize = 15.sp) },
                    leadingIcon = { Icon(Icons.Default.CalendarToday, null) }, readOnly = true,
                    shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) { Icon(Icons.Default.Edit, contentDescription = "Chọn ngày") }
                    }
                )
            }

            item {
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = { /* Save action */ onBack() },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                ) {
                    Text("Lưu Thay đổi", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 17.sp)
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = { TextButton(onClick = { selectedDate = dateState.selectedDateMillis; showDatePicker = false }) { Text("Xác nhận") } },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Hủy") } }
        ) {
            DatePicker(state = dateState)
        }
    }
}

/** 2. ORDER HISTORY SCREEN */
@Composable
fun OrderHistoryScreen(onBack: () -> Unit) {
    val tabs = listOf("Đang xử lý", "Đang giao", "Đã hoàn thành", "Đã hủy")
    // SỬA CẢNH BÁO 1
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val orders = remember {
        listOf(
            "Đơn #4589 - 180.000₫",
            "Đơn #4588 - 350.000₫",
            "Đơn #4587 - 95.000₫",
            "Đơn #4586 - 500.000₫"
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lịch sử Đơn hàng", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Quay lại") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = selectedTabIndex, containerColor = Color.White) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp) }, // Tăng size chữ
                        selectedContentColor = PrimaryOrange,
                        unselectedContentColor = Color.Gray
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5)),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp) // Tăng khoảng cách
            ) {
                itemsIndexed(orders) { _, order -> OrderCard(order = order, status = tabs[selectedTabIndex]) }
                item {
                    if (orders.isEmpty()) { Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) { Text("Chưa có đơn hàng nào.", color = Color.Gray) } }
                }
            }
        }
    }
}

@Composable
fun OrderCard(order: String, status: String) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { /* Navigate to Order Detail */ },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp) // Tăng độ nổi
    ) {
        Column(modifier = Modifier.padding(18.dp)) // Tăng padding
        {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(order, fontWeight = FontWeight.Bold, fontSize = 17.sp) // Tăng size chữ
                Text(status, color = PrimaryOrange, fontWeight = FontWeight.SemiBold, fontSize = 15.sp) // Tăng size chữ
            }
            Spacer(Modifier.height(8.dp))
            Text("Ngày đặt: 12/11/2025", fontSize = 13.sp, color = Color.Gray) // Tăng size chữ
        }
    }
}

/** 3. DELIVERY ADDRESS SCREEN */
@Composable
fun DeliveryAddressScreen(onBack: () -> Unit) {
    val addresses = remember {
        mutableStateListOf(
            "Nhà riêng: 70 Đ. Tô Ký, Tân Chánh Hiệp, Quận 12, Thành phố Hồ Chí Minh",
            "Văn phòng: 02 Võ Oanh, Phường 25, Bình Thạnh, Thành phố Hồ Chí Minh"
        )
    }
    var defaultAddressIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Địa chỉ Giao hàng", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Quay lại") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Open Add New Address screen */ }, containerColor = PrimaryOrange) {
                Icon(Icons.Default.Add, contentDescription = "Thêm địa chỉ", tint = Color.White, modifier = Modifier.size(28.dp)) // Tăng size icon
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp) // Tăng khoảng cách
        ) {
            itemsIndexed(addresses) { index, address ->
                AddressCard(
                    address = address,
                    isDefault = index == defaultAddressIndex,
                    onSetDefault = { defaultAddressIndex = index },
                    onEdit = { /* Edit address */ },
                    onDelete = { addresses.removeAt(index) }
                )
            }
        }
    }
}

@Composable
fun AddressCard(
    address: String,
    isDefault: Boolean,
    onSetDefault: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp), // Bo góc mềm hơn
        colors = CardDefaults.cardColors(containerColor = if (isDefault) PrimaryOrange.copy(alpha = 0.05f) else Color.White),
        border = if (isDefault) BorderStroke(2.dp, PrimaryOrange) else null, // Tăng độ dày border
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) { // Tăng padding
            Text(if (isDefault) "📍 Địa chỉ Mặc định" else "Địa chỉ", fontWeight = FontWeight.Bold, color = if (isDefault) PrimaryOrange else Color.Black, fontSize = 16.sp) // Tăng size chữ
            Spacer(Modifier.height(6.dp))
            Text(address, fontSize = 15.sp, color = Color.DarkGray) // Tăng size chữ
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isDefault) {
                    TextButton(onClick = onSetDefault) { Text("Đặt làm mặc định", color = PrimaryOrange, fontSize = 14.sp) }
                } else {
                    Spacer(Modifier.width(1.dp))
                }

                Row {
                    IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, "Sửa", tint = Color.Gray) }
                    IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Xóa", tint = Color.Red.copy(alpha = 0.7f)) }
                }
            }
        }
    }
}

/** 4. PAYMENT MANAGEMENT SCREEN */
@Composable
fun PaymentManagementScreen(onBack: () -> Unit) {
    val paymentMethods = remember {
        mutableStateListOf(
            PaymentMethod("Visa", Icons.Default.CreditCard, "**** 1234"),
            PaymentMethod("Momo Wallet", Icons.Default.AccountBalanceWallet, "090xxx89"),
            PaymentMethod("Thanh toán khi nhận hàng (COD)", Icons.Default.AttachMoney, "", true)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản lý Thanh toán", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Quay lại") } }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Text("Phương thức đã liên kết", fontWeight = FontWeight.SemiBold, fontSize = 17.sp) } // Tăng size chữ
            items(paymentMethods) { method -> PaymentCard(method = method) }

            item {
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { /* Open Add Payment screen */ },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, PrimaryOrange),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Icon(Icons.Default.Add, null, tint = PrimaryOrange)
                    Spacer(Modifier.width(8.dp))
                    Text("Thêm Thẻ/Ví Mới", color = PrimaryOrange, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun PaymentCard(method: PaymentMethod) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(45.dp).clip(CircleShape).background(Color(0xFFE3F2FD)), contentAlignment = Alignment.Center) {
                    Icon(method.icon, null, tint = Color(0xFF1E88E5), modifier = Modifier.size(26.dp))
                }
                Spacer(Modifier.width(18.dp))
                Column {
                    Text(method.type, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
                    if (!method.isCOD) {
                        Text(method.lastDigits, fontSize = 13.sp, color = Color.Gray)
                    } else {
                        Text("Luôn khả dụng", fontSize = 13.sp, color = Color.Gray)
                    }
                }
            }
            if (!method.isCOD) {
                Icon(Icons.Default.MoreVert, "Tùy chọn", tint = Color.Gray)
            }
        }
    }
}

/** 5. VOUCHER SCREEN */
@Composable
fun VoucherScreen(onBack: () -> Unit) {
    val tabs = listOf("Hiện có", "Đã dùng", "Đã hết hạn")
    // SỬA CẢNH BÁO 3
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val currentVouchers = remember {
        listOf(
            Voucher("SALE50K", "Giảm 50.000₫", "HSD: 31/12/2025", "ĐH từ 150K"),
            Voucher("FREESHIP", "Miễn phí vận chuyển", "HSD: 30/11/2025", "Tất cả đơn hàng")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Voucher & Khuyến mãi", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Quay lại") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Input field
            OutlinedTextField(
                value = "", onValueChange = { /* Handle code input */ },
                label = { Text("Nhập mã Voucher", fontSize = 15.sp) },
                trailingIcon = { Button(onClick = { /* Apply voucher */ }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange), modifier = Modifier.height(45.dp)) { Text("Áp dụng", fontSize = 14.sp) } },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )

            // Tabs
            TabRow(selectedTabIndex = selectedTabIndex, containerColor = Color.White) {
                tabs.forEachIndexed { index, title ->
                    Tab(selected = selectedTabIndex == index, onClick = { selectedTabIndex = index }, text = { Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp) })
                }
            }

            // Voucher List
            LazyColumn(
                modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5)),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(currentVouchers) { voucher -> VoucherCard(voucher = voucher) }
                item {
                    if (currentVouchers.isEmpty()) { Box(Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) { Text("Không có Voucher nào.", color = Color.Gray) } }
                }
            }
        }
    }
}

@Composable
fun VoucherCard(voucher: Voucher) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Discount info (Left side)
            Box(
                modifier = Modifier.width(110.dp).fillMaxHeight().background(PrimaryOrange.copy(alpha = 0.1f)).padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(voucher.discount, color = PrimaryOrange, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, lineHeight = 24.sp)
            }

            // Details and actions (Right side)
            Column(modifier = Modifier.weight(1f).padding(12.dp)) {
                Text(voucher.code, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text(voucher.condition, fontSize = 13.sp, color = Color.DarkGray)
                Spacer(Modifier.height(6.dp))
                Text(voucher.expiry, fontSize = 12.sp, color = Color.Red)
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { /* Copy code */ }) { Text("Sao chép", color = Color.Gray, fontSize = 14.sp) }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { /* Use voucher (navigate to Cart) */ }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)) {
                        Text("Sử dụng", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

/** 6. SECURITY SCREEN */
@Composable
fun SecurityScreen(onBack: () -> Unit) {
    var twoFactorEnabled by remember { mutableStateOf(false) }
    var biometricEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bảo mật & Quyền riêng tư", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Quay lại") } }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Text("Tài khoản", fontWeight = FontWeight.SemiBold, fontSize = 17.sp) }
            item { ProfileNavigationItem(ProfileMenuItem("Đổi mật khẩu", "Cập nhật mật khẩu thường xuyên", Icons.Default.Key, Color(0xFF673AB7))) { /* Navigate to Change Password */ } }

            item { Spacer(Modifier.height(16.dp)) }
            item { Text("Cài đặt đăng nhập", fontWeight = FontWeight.SemiBold, fontSize = 17.sp) }
            item {
                ProfileToggleItem(
                    ProfileMenuItem("Xác thực 2 yếu tố (2FA)", "Thêm lớp bảo vệ tài khoản", Icons.Default.VerifiedUser, Color(0xFF2196F3)),
                    checked = twoFactorEnabled,
                    onCheckedChange = { twoFactorEnabled = it }
                )
            }
            item {
                ProfileToggleItem(
                    ProfileMenuItem("Đăng nhập Sinh trắc học", "Vân tay/Face ID", Icons.Default.Fingerprint, Color(0xFF009688)),
                    checked = biometricEnabled,
                    onCheckedChange = { biometricEnabled = it }
                )
            }

            item { Spacer(Modifier.height(16.dp)) }
            item { Text("Hoạt động", fontWeight = FontWeight.SemiBold, fontSize = 17.sp) }
            item { ProfileNavigationItem(ProfileMenuItem("Quản lý thiết bị", "Xem các thiết bị đã đăng nhập", Icons.Default.Devices, Color(0xFF795548))) { /* Navigate to Device List */ } }
        }
    }
}

/** 7. APP SETTINGS SCREEN */
@Composable
fun AppSettingsScreen(onBack: () -> Unit) {
    var selectedLanguage by remember { mutableStateOf("Tiếng Việt") }
    var darkMode by remember { mutableStateOf(false) }

    // ... code tiếp tục như bạn đã cung cấp ...
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cài đặt Ứng dụng", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Quay lại") } }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Text("Giao diện", fontWeight = FontWeight.SemiBold, fontSize = 17.sp) }
            item {
                ProfileToggleItem(
                    ProfileMenuItem("Chế độ Tối (Dark Mode)", "Thay đổi giao diện ứng dụng", Icons.Default.DarkMode, Color(0xFF424242)),
                    checked = darkMode,
                    onCheckedChange = { darkMode = it }
                )
            }

            item { Spacer(Modifier.height(16.dp)) }
            item { Text("Ngôn ngữ", fontWeight = FontWeight.SemiBold, fontSize = 17.sp) }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { /* Open language selection dialog/screen */ },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(45.dp).clip(CircleShape).background(Color(0xFFE8F5E9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Language, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(26.dp))
                            }
                            Spacer(Modifier.width(18.dp))
                            Column {
                                Text("Ngôn ngữ Ứng dụng", fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
                                Text(selectedLanguage, fontSize = 13.sp, color = Color.Gray)
                            }
                        }
                        Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.Gray.copy(alpha = 0.6f))
                    }
                }
            }
        }
    }
}