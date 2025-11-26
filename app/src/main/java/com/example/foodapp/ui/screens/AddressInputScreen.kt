package com.example.foodapp.ui.screens

import android.location.Geocoder
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.foodapp.ui.theme.PrimaryOrange
import com.example.foodapp.navigation.Screen
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import com.example.foodapp.ui.screens.profile.AddressViewModel
import com.example.foodapp.ui.screens.profile.UserAddressStatus

data class DeliveryAddressFields(
    val streetNumber: String = "",
    val streetName: String = "",
    val ward: String = "",
    val city: String = "",
    val phoneNumber: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressInputScreen(
    navController: NavController,
    isCheckout: Boolean = false, // ⭐ THÊM BIẾN NÀY (Mặc định là false)
    viewModel: AddressViewModel = viewModel()
) {
    val context = LocalContext.current
    var deliveryAddress by remember { mutableStateOf(DeliveryAddressFields()) }

    val existingAddress by viewModel.userAddress.collectAsState(initial = "")
    val existingPhone by viewModel.userPhone.collectAsState(initial = "")

    var isFormInitialized by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(existingAddress, existingPhone) {
        if (!isFormInitialized) {
            var updated = false
            if (existingAddress.isNotBlank()) {
                val parts = existingAddress.split(",").map { it.trim() }
                if (parts.size >= 4) {
                    deliveryAddress = deliveryAddress.copy(
                        streetNumber = parts[0], streetName = parts[1], ward = parts[2], city = parts[3]
                    )
                    updated = true
                }
            }
            if (existingPhone.isNotBlank()) {
                deliveryAddress = deliveryAddress.copy(phoneNumber = existingPhone)
                updated = true
            }
            if (updated) isFormInitialized = true
        }
    }

    val fullAddress = remember(deliveryAddress) {
        "${deliveryAddress.streetNumber}, ${deliveryAddress.streetName}, ${deliveryAddress.ward}, ${deliveryAddress.city}"
    }

    val saveStatus by viewModel.status.collectAsState()
    val isLoading = saveStatus is UserAddressStatus.Saving

    val isFormValid = deliveryAddress.streetNumber.isNotBlank() &&
                      deliveryAddress.streetName.isNotBlank() &&
                      deliveryAddress.ward.isNotBlank() &&
                      deliveryAddress.city.isNotBlank() &&
                      deliveryAddress.phoneNumber.length >= 9

    suspend fun geocodeAddress(address: String): Pair<Double, Double>? {
        return suspendCoroutine { continuation ->
            try {
                @Suppress("DEPRECATION")
                val geocoder = Geocoder(context, Locale.getDefault())
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocationName(address, 1)
                if (!addresses.isNullOrEmpty()) {
                    continuation.resume(Pair(addresses[0].latitude, addresses[0].longitude))
                } else {
                    continuation.resume(null)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                continuation.resume(null)
            }
        }
    }

    // ⭐ LOGIC ĐIỀU HƯỚNG THÔNG MINH ⭐
    LaunchedEffect(saveStatus) {
        if (saveStatus is UserAddressStatus.AddressExists) {
            Toast.makeText(context, "Lưu thành công!", Toast.LENGTH_SHORT).show()

            if (isCheckout) {
                // CASE A: Đang mua hàng -> Chuyển sang Xác nhận đơn
                val coordinates = geocodeAddress(fullAddress)
                val lat = coordinates?.first ?: 0.0
                val lon = coordinates?.second ?: 0.0
                navController.navigate(Screen.createConfirmOrderRoute(fullAddress, lat, lon))
            } else {
                // CASE B: Đang cập nhật Profile/Login -> Về trang chủ
                navController.navigate(Screen.Root) {
                    popUpTo(0) { inclusive = true } // Xóa sạch lịch sử để tránh back lại login
                }
            }
            viewModel.resetStatus()
        } else if (saveStatus is UserAddressStatus.Error) {
            Toast.makeText(context, (saveStatus as UserAddressStatus.Error).message, Toast.LENGTH_LONG).show()
            viewModel.resetStatus()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thông tin Giao hàng") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 24.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text("Địa Chỉ & Liên Hệ", fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            Text("Nhập thông tin để shipper giao hàng.", fontSize = 16.sp, color = Color.Gray, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = deliveryAddress.phoneNumber,
                onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 11) deliveryAddress = deliveryAddress.copy(phoneNumber = it) },
                label = { Text("Số điện thoại") },
                leadingIcon = { Icon(Icons.Default.Phone, null) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )

            OutlinedTextField(
                value = deliveryAddress.streetNumber,
                onValueChange = { deliveryAddress = deliveryAddress.copy(streetNumber = it) },
                label = { Text("Số nhà/Tòa nhà") },
                leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = deliveryAddress.streetName,
                onValueChange = { deliveryAddress = deliveryAddress.copy(streetName = it) },
                label = { Text("Tên đường") },
                leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = deliveryAddress.ward,
                onValueChange = { deliveryAddress = deliveryAddress.copy(ward = it) },
                label = { Text("Phường/Xã") },
                leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = deliveryAddress.city,
                onValueChange = { deliveryAddress = deliveryAddress.copy(city = it) },
                label = { Text("Thành phố") },
                leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (isFormValid) {
                        viewModel.saveContactInfo(fullAddress, deliveryAddress.phoneNumber)
                    } else {
                        Toast.makeText(context, "Vui lòng điền đầy đủ thông tin.", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = isFormValid && !isLoading,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("LƯU VÀ TIẾP TỤC", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}