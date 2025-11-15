package com.example.foodapp

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Khởi tạo thư viện ThreeTenABP
        AndroidThreeTen.init(this)
    }
}
