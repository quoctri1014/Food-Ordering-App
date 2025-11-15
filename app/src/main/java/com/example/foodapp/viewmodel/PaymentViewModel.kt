package com.example.foodapp.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.example.foodapp.data.model.PaymentInfo
import com.example.foodapp.data.model.PaymentMethod // Đảm bảo lớp này được import

class PaymentViewModel(application: Application) : AndroidViewModel(application) {
    // SharedPreferences để lưu trữ dữ liệu
    private val prefs = application.getSharedPreferences("payment_prefs", Context.MODE_PRIVATE)

    // Lưu trữ trạng thái hiện tại bằng Compose mutableState
    var paymentInfo = mutableStateOf(loadPaymentInfo())
        private set

    // Cập nhật thông tin và lưu vào SharedPreferences
    fun updatePaymentInfo(info: PaymentInfo) {
        paymentInfo.value = info
        savePaymentInfo(info)
    }

    // Lưu vào SharedPreferences
    private fun savePaymentInfo(info: PaymentInfo) {
        prefs.edit().apply {
            putString("fullName", info.fullName)
            putString("phone", info.phone)
            putString("address", info.address)
            putString("note", info.note)
            putString("method", info.method.name) // Lưu tên enum dưới dạng chuỗi
            apply()
        }
    }

    // Tải từ SharedPreferences
    private fun loadPaymentInfo(): PaymentInfo {
        val fullName = prefs.getString("fullName", "") ?: ""
        val phone = prefs.getString("phone", "") ?: ""
        val address = prefs.getString("address", "") ?: ""
        val note = prefs.getString("note", "") ?: ""

        val defaultMethodName = PaymentMethod.COD.name
        val methodName = prefs.getString("method", defaultMethodName) ?: defaultMethodName

        val method: PaymentMethod = try {

            PaymentMethod.valueOf(methodName)
        } catch (e: IllegalArgumentException) {

            PaymentMethod.COD
        }

        // Trả về đối tượng PaymentInfo hoàn chỉnh
        return PaymentInfo(fullName, phone, address, note, method)
    }
}