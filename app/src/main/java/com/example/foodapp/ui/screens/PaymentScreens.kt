package com.example.foodapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.foodapp.R
import android.widget.Toast
import com.example.foodapp.data.model.PaymentMethod
import com.example.foodapp.data.model.PaymentInfo
import com.example.foodapp.data.model.PrimaryAccentColor
import com.example.foodapp.data.model.LightAccentBackground
import com.example.foodapp.data.model.AppFoodTotalRed
import com.example.foodapp.utils.toVND // Giữ lại import này

// --- PaymentMethodScreen Composable ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodScreen(

    finalTotalAmount: Int = 450000,
    onOrderCompleted: (PaymentInfo) -> Unit,
    onBackClick: () -> Unit,
    onNavigateToQrDetail: (String, Int, String) -> Unit,
    onTempPaymentInfoSaved: (PaymentInfo) -> Unit // Callback mới để lưu info tạm
) {
    val context = LocalContext.current
    var info by remember { mutableStateOf(PaymentInfo(method = PaymentMethod.COD)) }
    val isFormValid = info.fullName.isNotBlank() && info.phone.length >= 9 && info.address.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thông tin Thanh toán", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Tổng tiền
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDE7)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tổng Tiền Đơn Hàng:", fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
                    Text(finalTotalAmount.toVND(), fontWeight = FontWeight.ExtraBold, color = AppFoodTotalRed, fontSize = 20.sp)
                }
            }

            Divider()
            Text("1. Thông tin Giao hàng:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            // 2. Input Fields
            OutlinedTextField(
                value = info.fullName,
                onValueChange = { info = info.copy(fullName = it) },
                label = { Text("Họ và tên") },
                isError = info.fullName.isBlank(),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = info.phone,
                onValueChange = {
                    if (it.length <= 10) info = info.copy(phone = it)
                },
                label = { Text("Số điện thoại (Tối đa 10 số)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = info.phone.length < 9,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = info.address,
                onValueChange = { info = info.copy(address = it) },
                label = { Text("Địa chỉ giao hàng") },
                isError = info.address.isBlank(),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = info.note,
                onValueChange = { info = info.copy(note = it) },
                label = { Text("Ghi chú (tuỳ chọn: VD: ít cay, giao sau 5h)") },
                modifier = Modifier.fillMaxWidth()
            )

            Divider()
            Text("2. Phương thức Thanh toán:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            // 3. Lựa chọn phương thức
            PaymentMethod.entries.forEach { method ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                    .fillMaxWidth()
                    .clickable { info = info.copy(method = method) }
                    .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = info.method == method,
                        onClick = { info = info.copy(method = method) },
                        colors = RadioButtonDefaults.colors(selectedColor = PrimaryAccentColor)
                    )
                    Text(method.displayName, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Nút Xác nhận
            Button(
                onClick = {
                    if (isFormValid) {
                        if (info.method == PaymentMethod.QR_BIDV || info.method == PaymentMethod.MOMO) {
                            onTempPaymentInfoSaved(info)
                            onNavigateToQrDetail(info.method.methodId, finalTotalAmount, info.fullName)
                        } else {
                            onOrderCompleted(info)
                            Toast.makeText(context, "Thanh toán COD thành công! Đơn đang được xử lý.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Vui lòng điền đủ và đúng thông tin giao hàng", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = isFormValid,
                modifier = Modifier.fillMaxWidth().height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccentColor)
            ) {
                Text(
                    if (info.method == PaymentMethod.COD) "XÁC NHẬN ĐẶT HÀNG" else "TIẾP TỤC THANH TOÁN",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
            }
        }
    }
}


// --- QrPaymentDetailScreen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrPaymentDetailScreen(
    methodId: String,
    finalTotalAmount: Int,
    customerName: String,
    onOrderCompleted: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val isMomo = methodId == PaymentMethod.MOMO.methodId

    val accentColor = PrimaryAccentColor
    val bgColor = LightAccentBackground
    val titleText = if (isMomo) "Thanh toán Momo" else "Thanh toán QR (BIDV)"

    // Dữ liệu giả định QR/Bank Info
    val bankName = if (isMomo) "Ví điện tử Momo" else "Ngân hàng: BIDV"
    val accountName = if (isMomo) "Nguyễn Thị Thanh Vân" else "Nguyễn Thị Thanh Vân"
    val accountNumber = if (isMomo) "0908082005" else "0908082005"
    // ⭐ SỬA LỖI KHÔNG TÌM THẤY HÌNH ẢNH: Đảm bảo R.drawable.qr_momo và R.drawable.qr_payment tồn tại ⭐
    val qrImageRes = if (isMomo) R.drawable.qr_momo else R.drawable.qr_payment

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titleText, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Quét mã hoặc chuyển khoản thủ công với nội dung chính xác để hoàn tất đơn hàng.",
                textAlign = TextAlign.Center,
                color = Color.DarkGray
            )
            Spacer(Modifier.height(24.dp))

            // Khối Mã QR
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text("QUÉT MÃ THANH TOÁN", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = accentColor)
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .size(208.dp)
                            .background(bgColor, RoundedCornerShape(8.dp))
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(qrImageRes),
                            contentDescription = "QR Code",
                            modifier = Modifier.size(200.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // Khối Thông tin chi tiết
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Thông tin chuyển khoản:", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                    Divider(Modifier.padding(vertical = 8.dp))

                    PaymentDetailRow("Phương thức:", if (isMomo) "Momo" else "QR", accentColor)
                    PaymentDetailRow(if (isMomo) "Tên Ví:" else "Chủ TK:", accountName, accentColor)
                    PaymentDetailRow(if (isMomo) "SĐT/Mã Ví:" else "Số TK:", accountNumber, accentColor)
                    PaymentDetailRow("Ngân hàng:", bankName, Color.DarkGray)

                    Divider(Modifier.padding(vertical = 12.dp))

                    PaymentDetailRow("Nội dung:", "AppFood $customerName", accentColor, isBoldValue = true, isLargeText = true)
                    Spacer(Modifier.height(8.dp))
                    PaymentDetailRow("Số tiền cần chuyển:", finalTotalAmount.toVND(), AppFoodTotalRed, isBoldValue = true, isLargeText = true)
                }
            }

            Spacer(Modifier.height(48.dp))

            // Nút Xác nhận đã chuyển khoản
            Button(
                onClick = {
                    onOrderCompleted()
                    Toast.makeText(context, "Đã gửi yêu cầu kiểm tra thanh toán. Vui lòng đợi xác nhận.", Toast.LENGTH_LONG).show()
                },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor)
            ) {
                Text("TÔI ĐÃ CHUYỂN KHOẢN", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color.White)
            }
        }
    }
}

// Hàm hỗ trợ PaymentDetailRow
@Composable
fun PaymentDetailRow(label: String, value: String, valueColor: Color, isBoldValue: Boolean = false, isLargeText: Boolean = false) {
    val fontSize = if (isLargeText) 16.sp else 14.sp
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = fontSize, color = if (isLargeText) Color.Black else Color.Gray, fontWeight = if (isLargeText) FontWeight.SemiBold else FontWeight.Normal)
        Text(
            value,
            fontSize = if (isLargeText) 18.sp else fontSize,
            fontWeight = if (isBoldValue) FontWeight.ExtraBold else FontWeight.SemiBold,
            color = valueColor,
            textAlign = TextAlign.End
        )
    }
}