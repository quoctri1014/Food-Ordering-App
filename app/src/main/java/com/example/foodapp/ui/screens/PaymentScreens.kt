package com.example.foodapp.ui.screens

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import com.example.foodapp.data.model.PaymentMethod
import com.example.foodapp.data.model.PaymentInfo
import com.example.foodapp.ui.theme.PrimaryOrange
import com.example.foodapp.ui.theme.AppFoodTotalRed
import com.example.foodapp.utils.toVND // Cần hàm toVND

// --- PaymentMethodScreen Composable ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodScreen(
    initialSubtotalAmount: Int,
    onOrderCompleted: (PaymentInfo) -> Unit,
    onBackClick: () -> Unit,
    onNavigateToQrDetail: (String, Int, String) -> Unit,
    onTempPaymentInfoSaved: (PaymentInfo) -> Unit
) {
    val context = LocalContext.current
    var info by remember { mutableStateOf(PaymentInfo(method = PaymentMethod.COD)) }

    // Giả định phí ship và tổng tiền
    var shippingFee by remember { mutableIntStateOf(15000) }
    val finalTotalAmount = initialSubtotalAmount + shippingFee

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
                Column(modifier = Modifier.padding(16.dp)) {
                    PaymentTotalRow("Tổng tiền hàng:", initialSubtotalAmount.toVND(), PrimaryOrange, isFinal = false)
                    PaymentTotalRow("Phí vận chuyển:", shippingFee.toVND(), PrimaryOrange, isFinal = false)

                    Divider(Modifier.padding(vertical = 8.dp))

                    PaymentTotalRow("Tổng thanh toán:", finalTotalAmount.toVND(), AppFoodTotalRed, isFinal = true)
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
            // Logic đã được sửa để chỉ dùng BANK và COD
            PaymentMethod.entries.forEach { method ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                    .fillMaxWidth()
                    .clickable { info = info.copy(method = method) }
                    .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = info.method == method,
                        onClick = { info = info.copy(method = method) },
                        colors = RadioButtonDefaults.colors(selectedColor = PrimaryOrange)
                    )
                    Text(method.displayName, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Nút Xác nhận
            Button(
                onClick = {
                    if (isFormValid) {
                        // KHẮC PHỤC LỖI: Chỉ dùng PaymentMethod.BANK cho QR/Chuyển khoản
                        if (info.method == PaymentMethod.BANK) {
                            onTempPaymentInfoSaved(info.copy(shippingFee = shippingFee))
                            onNavigateToQrDetail(info.method.methodId, finalTotalAmount, info.fullName)
                        } else {
                            onOrderCompleted(info.copy(shippingFee = shippingFee))
                            Toast.makeText(context, "Thanh toán COD thành công! Đơn đang được xử lý.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Vui lòng điền đủ và đúng thông tin giao hàng", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = isFormValid,
                modifier = Modifier.fillMaxWidth().height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
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

// HÀM HỖ TRỢ PaymentTotalRow
@Composable
fun PaymentTotalRow(label: String, value: String, valueColor: Color, isFinal: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label,
            fontWeight = if (isFinal) FontWeight.ExtraBold else FontWeight.Medium,
            fontSize = if (isFinal) 18.sp else 16.sp
        )
        Text(
            value,
            fontWeight = FontWeight.ExtraBold,
            fontSize = if (isFinal) 20.sp else 16.sp,
            color = valueColor
        )
    }
}