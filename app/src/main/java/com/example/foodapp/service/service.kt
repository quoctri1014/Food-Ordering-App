package com.example.foodapp.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.foodapp.MainActivity
import com.example.foodapp.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class MyFirebaseMessagingService : FirebaseMessagingService() {

    // 1. Hàm này được gọi khi có Token mới (dùng để gửi thông báo riêng cho thiết bị này)
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Ở đây bạn nên gửi token này lên Firestore và lưu vào User Profile
        // để sau này Backend biết gửi thông báo cho ai.
    }

    // 2. Hàm này được gọi khi nhận được thông báo khi app ĐANG MỞ (Foreground)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Lấy thông tin tin nhắn
        val title = remoteMessage.notification?.title ?: "Thông báo mới"
        val body = remoteMessage.notification?.body ?: ""

        // Hiển thị thông báo
        showNotification(title, body)
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "FOOD_APP_NOTIFICATIONS"
        val notificationId = Random.nextInt()

        // ⭐ CẬP NHẬT: Thêm dữ liệu vào Intent để mở màn hình chi tiết
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // Gửi kèm tín hiệu để MainActivity biết
            putExtra("navigate_to", "notification_detail")
            putExtra("title", title)
            putExtra("body", message)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Thay bằng icon app của bạn (vd: R.drawable.ic_burger_icon)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent) // Quan trọng: Gắn sự kiện click
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Tạo Channel cho Android O trở lên
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Thông báo đơn hàng",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}