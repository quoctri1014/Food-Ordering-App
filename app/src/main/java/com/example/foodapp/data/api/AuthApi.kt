package com.example.foodapp.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class OtpRequest(val email: String)
data class VerifyOtpRequest(val email: String, val otp: String)
data class ResetPassRequest(val email: String, val otp: String, val newPassword: String)
data class ApiResponse(val message: String, val success: Boolean)

interface AuthApiService {
    @POST("/api/send-otp")
    suspend fun sendOtp(@Body request: OtpRequest): ApiResponse

    @POST("/api/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): ApiResponse

    @POST("/api/reset-password")
    suspend fun resetPassword(@Body request: ResetPassRequest): ApiResponse
}
// Nhớ thêm dấu gạch chéo / ở cuối cùng nhé
object RetrofitClient {
    private const val BASE_URL = "https://foodapp-server-txfk.onrender.com/"

    val instance: AuthApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }
}