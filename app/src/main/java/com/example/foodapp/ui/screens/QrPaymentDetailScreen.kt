package com.example.foodapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import android.widget.Toast
import com.example.foodapp.R // Cần import R cho hình ảnh
import com.example.foodapp.data.model.PaymentMethod
import com.example.foodapp.utils.toVND // Cần hàm toVND

// --- KHAI BÁO CÁC HẰNG SỐ MÀU THIẾU (TẠM THỜI) ---
val PrimaryAccentColor = Color(0xFFFF6B3A)
val LightAccentBackground = Color(0xFFFFF3E0)
val AppFoodTotalRed = Color(0xFFD32F2F)
// --- KẾT THÚC KHAI BÁO MÀU ---


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

    // ⭐ KHẮC PHỤC LỖI: Chỉ kiểm tra cho BANK (đã loại bỏ MOMO/QR_BIDV) ⭐
    val isBankTransfer = methodId == PaymentMethod.BANK.methodId

    val accentColor = PrimaryAccentColor
    val bgColor = LightAccentBackground

    // Tiêu đề
    val titleText = if (isBankTransfer) "Thanh toán Chuyển khoản" else "Lỗi thanh toán"

    // Dữ liệu giả định QR/Bank Info
    val bankName = "Ngân hàng: BIDV (Tạm thời)"
    val accountName = "Nguyễn Thị Thanh Vân"
    val accountNumber = "0908082005"

    // ⭐ KHẮC PHỤC LỖI: Chỉ dùng QR_Payment ⭐
    // Giả định R.drawable.qr_payment tồn tại
    val qrImageRes = R.drawable.qr_payment

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

                    // ⭐ CẬP NHẬT DÒNG THÔNG TIN ⭐
                    PaymentDetailRow("Phương thức:", "Chuyển khoản", accentColor)
                    PaymentDetailRow("Chủ TK:", accountName, accentColor)
                    PaymentDetailRow("Số TK:", accountNumber, accentColor)
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

// Hàm hỗ trợ PaymentDetailRow (Đặt lại trong file này)
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