package com.example.foodapp.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.FirestoreHelper
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ⭐ ĐỊNH NGHĨA TRẠNG THÁI Ở ĐÂY ⭐
sealed class UserAddressStatus {
    object Idle : UserAddressStatus()
    object Saving : UserAddressStatus()
    object AddressExists : UserAddressStatus()
    data class Error(val message: String) : UserAddressStatus()
}

class AddressViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    // ⭐ BIẾN QUAN TRỌNG: LƯU ĐỊA CHỈ CŨ ⭐
    private val _userAddress = MutableStateFlow("")
    val userAddress: StateFlow<String> = _userAddress.asStateFlow()

    // ⭐ BIẾN QUAN TRỌNG: LƯU SỐ ĐIỆN THOẠI CŨ ⭐
    private val _userPhone = MutableStateFlow("")
    val userPhone: StateFlow<String> = _userPhone.asStateFlow()

    private val _status = MutableStateFlow<UserAddressStatus>(UserAddressStatus.Idle)
    val status: StateFlow<UserAddressStatus> = _status.asStateFlow()

    init {
        fetchUserInfo()
    }

    private fun fetchUserInfo() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            val user = FirestoreHelper.getUserProfile(userId)
            _userAddress.value = user?.address ?: ""
            _userPhone.value = user?.phoneNumber ?: ""
        }
    }

    // Lưu cả địa chỉ và SĐT
    fun saveContactInfo(fullAddress: String, phoneNumber: String) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _status.value = UserAddressStatus.Error("Chưa đăng nhập")
            return
        }

        _status.value = UserAddressStatus.Saving

        viewModelScope.launch {
            val result = FirestoreHelper.saveContactInfo(userId, fullAddress, phoneNumber)
            if (result.isSuccess) {
                _userAddress.value = fullAddress
                _userPhone.value = phoneNumber
                _status.value = UserAddressStatus.AddressExists
            } else {
                _status.value = UserAddressStatus.Error("Lỗi lưu: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    // Lưu chỉ địa chỉ (Giữ tương thích cũ)
    fun saveAddress(fullAddress: String) {
        // Tái sử dụng hàm trên với SĐT hiện tại
        saveContactInfo(fullAddress, _userPhone.value)
    }

    fun resetStatus() {
        _status.value = UserAddressStatus.Idle
    }
}