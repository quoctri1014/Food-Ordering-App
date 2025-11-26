package com.example.foodapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodapp.data.CartItem
import com.example.foodapp.data.FirestoreHelper
import com.example.foodapp.data.model.Voucher
import com.example.foodapp.ui.theme.PrimaryOrange
import com.example.foodapp.utils.toVND

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyNowScreen(
    buyNowItems: List<CartItem>,
    // Callback này giờ nhận thêm tham số discountAmount (số tiền được giảm)
    onConfirmClick: (List<CartItem>, Int) -> Unit,
    onBackClick: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onUpdateItem: (CartItem, Int) -> Unit
) {
    // --- STATE QUẢN LÝ VOUCHER ---
    var vouchers by remember { mutableStateOf<List<Voucher>>(emptyList()) }
    var selectedVoucher by remember { mutableStateOf<Voucher?>(null) }
    var showVoucherSheet by remember { mutableStateOf(false) }

    // Lấy danh sách voucher từ Firebase khi mở màn hình
    LaunchedEffect(Unit) {
        FirestoreHelper.getAllVouchers { fetchedVouchers ->
            vouchers = fetchedVouchers
        }
    }

    // Tính tổng tiền hàng (chưa giảm)
    val subtotal = remember(buyNowItems) { buyNowItems.sumOf { it.food.price * it.quantity } }

    // Tính số tiền được giảm (kiểm tra điều kiện đơn tối thiểu)
    val discountAmount = remember(subtotal, selectedVoucher) {
        if (selectedVoucher != null && subtotal >= selectedVoucher!!.minOrderValue) {
            selectedVoucher!!.discountAmount
        } else {
            0
        }
    }

    // Tính tổng tiền cuối cùng
    val finalTotal = (subtotal - discountAmount).coerceAtLeast(0)

    Scaffold(
        containerColor = Color(0xFFF8F9FA),
        topBar = {
            Surface(shadowElevation = 4.dp) {
                CenterAlignedTopAppBar(
                    title = {
                        Text("Xác nhận đơn hàng", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Surface(shape = CircleShape, color = Color.White, shadowElevation = 2.dp) {
                                Box(modifier = Modifier.padding(8.dp)) {
                                    Icon(Icons.Default.ArrowBack, "Back", modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                )
            }
        },
        bottomBar = {
            if (buyNowItems.isNotEmpty()) {
                BeautifulBottomBar(
                    totalPrice = finalTotal, // Hiển thị giá đã giảm
                    onConfirm = {
                        // Truyền danh sách món và số tiền giảm sang màn hình tiếp theo
                        onConfirmClick(buyNowItems, discountAmount)
                    }
                )
            }
        }
    ) { paddingValues ->
        if (buyNowItems.isEmpty()) {
            EmptyStateView()
        } else {
            LazyColumn(
                contentPadding = PaddingValues(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize().padding(paddingValues)
            ) {
                item {
                    Text("Danh sách món", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.DarkGray)
                }

                items(buyNowItems, key = { it.food.id }) { item ->
                    BeautifulItemCard(
                        item = item,
                        onIncrease = { onUpdateItem(item, 1) },
                        onDecrease = { onUpdateItem(item, -1) },
                        onClick = { onNavigateToDetail(item.food.id) }
                    )
                }

                // ⭐ PHẦN CHỌN VOUCHER ⭐
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Ưu đãi", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(8.dp))

                    VoucherSelectorCard(
                        selectedVoucher = selectedVoucher,
                        onClick = { showVoucherSheet = true }
                    )
                }

                // Section Tóm tắt hóa đơn (Cập nhật hiển thị giảm giá)
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    BillSummaryCard(
                        subtotal = subtotal,
                        discount = discountAmount,
                        finalTotal = finalTotal
                    )
                }
            }
        }
    }

    // ⭐ BOTTOM SHEET HIỂN THỊ DANH SÁCH VOUCHER ⭐
    if (showVoucherSheet) {
        ModalBottomSheet(
            onDismissRequest = { showVoucherSheet = false },
            containerColor = Color.White
        ) {
            VoucherBottomSheetContent(
                vouchers = vouchers,
                currentSubtotal = subtotal,
                selectedVoucher = selectedVoucher,
                onSelect = { voucher ->
                    // Nếu chọn lại voucher đang chọn -> Bỏ chọn (toggle)
                    selectedVoucher = if (selectedVoucher == voucher) null else voucher
                    showVoucherSheet = false
                }
            )
        }
    }
}

// --- CÁC UI COMPONENT ---

@Composable
fun VoucherSelectorCard(
    selectedVoucher: Voucher?,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Discount, contentDescription = null, tint = PrimaryOrange)
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("FoodApp Voucher", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                if (selectedVoucher != null) {
                    Text(
                        "Đã áp dụng: -${selectedVoucher.discountAmount.toVND()}",
                        color = PrimaryOrange,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Text("Chọn hoặc nhập mã", color = Color.Gray, fontSize = 14.sp)
                }
            }

            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
        }
    }
}

@Composable
fun BillSummaryCard(subtotal: Int, discount: Int, finalTotal: Int) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Chi tiết thanh toán", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Tạm tính", color = Color.Gray)
                Text(subtotal.toVND(), fontWeight = FontWeight.SemiBold)
            }

            // Hiển thị dòng giảm giá nếu có
            if (discount > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Voucher giảm giá", color = Color.Gray)
                    Text("-${discount.toVND()}", fontWeight = FontWeight.SemiBold, color = PrimaryOrange)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color.LightGray.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Tổng thanh toán", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(finalTotal.toVND(), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = PrimaryOrange)
            }
        }
    }
}

@Composable
fun VoucherBottomSheetContent(
    vouchers: List<Voucher>,
    currentSubtotal: Int,
    selectedVoucher: Voucher?,
    onSelect: (Voucher) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 30.dp)
    ) {
        Text(
            "Chọn Voucher",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp).align(Alignment.CenterHorizontally)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.heightIn(max = 400.dp)
        ) {
            items(vouchers) { voucher ->
                val isEligible = currentSubtotal >= voucher.minOrderValue
                val isSelected = selectedVoucher?.id == voucher.id

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isEligible) Color(0xFFFFF8E1) else Color(0xFFF5F5F5)
                    ),
                    border = if (isSelected) BorderStroke(1.5.dp, PrimaryOrange) else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = isEligible) { onSelect(voucher) }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Phần hiển thị mã
                        Column(
                            modifier = Modifier
                                .width(80.dp)
                                .padding(end = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Discount, null, tint = if(isEligible) PrimaryOrange else Color.Gray)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                voucher.code,
                                fontWeight = FontWeight.Bold,
                                color = if(isEligible) Color.Black else Color.Gray
                            )
                        }

                        Divider(
                            modifier = Modifier
                                .width(1.dp)
                                .height(40.dp),
                            color = Color.Gray.copy(alpha = 0.3f)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        // Thông tin chi tiết
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Giảm ${voucher.discountAmount.toVND()}",
                                fontWeight = FontWeight.Bold,
                                color = if(isEligible) Color.Black else Color.Gray
                            )
                            Text(
                                "Đơn tối thiểu ${voucher.minOrderValue.toVND()}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                            if (!isEligible) {
                                Text(
                                    "Chưa đủ điều kiện",
                                    fontSize = 12.sp,
                                    color = Color.Red
                                )
                            }
                        }

                        // Radio button
                        Icon(
                            imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (isSelected) PrimaryOrange else Color.Gray
                        )
                    }
                }
            }

            if (vouchers.isEmpty()) {
                item {
                    Text(
                        "Không có voucher nào khả dụng",
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

// --- CÁC COMPONENT CŨ GIỮ NGUYÊN ---

@Composable
fun BeautifulItemCard(item: CartItem, onIncrease: () -> Unit, onDecrease: () -> Unit, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(12.dp), color = Color.LightGray.copy(alpha = 0.2f), modifier = Modifier.size(80.dp)) {
                Image(painter = painterResource(id = item.food.imageUrl), contentDescription = item.food.name, contentScale = ContentScale.Crop)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.SpaceBetween) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                    Text(text = item.food.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                    Text(text = item.food.price.toVND(), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PrimaryOrange)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.background(Color(0xFFF5F5F5), RoundedCornerShape(24.dp)).padding(horizontal = 4.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    QuantityIconButton(icon = Icons.Default.Remove, tint = if (item.quantity > 1) Color.Black else Color.Red, onClick = onDecrease)
                    Text(text = "${item.quantity}", fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(horizontal = 12.dp))
                    QuantityIconButton(icon = Icons.Default.Add, tint = Color.White, backgroundColor = PrimaryOrange, onClick = onIncrease)
                }
            }
        }
    }
}

@Composable
fun QuantityIconButton(icon: androidx.compose.ui.graphics.vector.ImageVector, tint: Color, backgroundColor: Color = Color.White, onClick: () -> Unit) {
    Surface(onClick = onClick, shape = CircleShape, color = backgroundColor, shadowElevation = if (backgroundColor == Color.White) 2.dp else 0.dp, modifier = Modifier.size(28.dp)) {
        Box(contentAlignment = Alignment.Center) { Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp)) }
    }
}

@Composable
fun BeautifulBottomBar(totalPrice: Int, onConfirm: () -> Unit) {
    Surface(color = Color.White, shadowElevation = 20.dp, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("Tổng cộng", fontSize = 14.sp, color = Color.Gray)
                Text(totalPrice.toVND(), fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryOrange)
            }
            Button(onClick = onConfirm, modifier = Modifier.width(160.dp).height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange), shape = RoundedCornerShape(14.dp), elevation = ButtonDefaults.buttonElevation(6.dp)) {
                Text("Tiếp Tục", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun EmptyStateView() {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(100.dp), tint = Color.LightGray.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(16.dp))
        Text("Chưa có món nào để mua ngay", color = Color.Gray, fontSize = 16.sp)
    }
}