package com.example.foodorderingapp.ui.theme.splash

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.example.foodorderingapp.MainActivity // Giả định MainActivity là màn hình Home
import com.example.foodorderingapp.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    // Tổng thời gian chờ: 2 giây. Ta sẽ điều chỉnh thời gian animation sao cho vừa đủ.
    private val TOTAL_DELAY: Long = 2000

    // Thời gian cho phase 1 (Burger)
    private val ANIM_DURATION_PHASE1: Long = 1000

    // Thời gian cho phase 2 (WELCOME)
    private val ANIM_DURATION_PHASE2: Long = 500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        // --- 1. Cài đặt trạng thái BAN ĐẦU ---

        // Ẩn các thành phần
        binding.ivAppIcon.alpha = 0f
        binding.tvWelcomeText.alpha = 0f

        // Đặt Burger ở vị trí trên cao bên trái và kích thước nhỏ (cho hiệu ứng di chuyển và phóng to)
        val displayMetrics = resources.displayMetrics
        binding.ivAppIcon.translationX = displayMetrics.density * -100f // Đẩy sang trái
        binding.ivAppIcon.translationY = displayMetrics.density * -100f // Đẩy lên trên
        binding.ivAppIcon.scaleX = 0.5f
        binding.ivAppIcon.scaleY = 0.5f

        // Đặt WELCOME ở vị trí thấp hơn (cho hiệu ứng nổi lên)
        binding.tvWelcomeText.translationY = displayMetrics.density * 50f


        // --- 2. Tạo Animation Set (Burger) ---

        // Di chuyển (X, Y), Phóng to (Scale X, Y), và Hiện dần (Alpha)
        val burgerMoveX = ObjectAnimator.ofFloat(binding.ivAppIcon, View.TRANSLATION_X, 0f)
        val burgerMoveY = ObjectAnimator.ofFloat(binding.ivAppIcon, View.TRANSLATION_Y, 0f)
        val burgerScaleX = ObjectAnimator.ofFloat(binding.ivAppIcon, View.SCALE_X, 1f)
        val burgerScaleY = ObjectAnimator.ofFloat(binding.ivAppIcon, View.SCALE_Y, 1f)
        val burgerFadeIn = ObjectAnimator.ofFloat(binding.ivAppIcon, View.ALPHA, 1f)

        val animatorSetPhase1 = AnimatorSet().apply {
            playTogether(burgerMoveX, burgerMoveY, burgerScaleX, burgerScaleY, burgerFadeIn)
            duration = ANIM_DURATION_PHASE1
            interpolator = AccelerateDecelerateInterpolator()
        }


        // --- 3. Tạo Animation Set (WELCOME) ---

        // Đẩy lên (Y) và Hiện dần (Alpha)
        val welcomePushY = ObjectAnimator.ofFloat(binding.tvWelcomeText, View.TRANSLATION_Y, 0f)
        val welcomeFadeIn = ObjectAnimator.ofFloat(binding.tvWelcomeText, View.ALPHA, 1f)

        val animatorSetPhase2 = AnimatorSet().apply {
            playTogether(welcomePushY, welcomeFadeIn)
            duration = ANIM_DURATION_PHASE2
            interpolator = AccelerateDecelerateInterpolator()
        }


        // --- 4. Chạy theo trình tự ---
        val masterAnimatorSet = AnimatorSet().apply {
            // Chạy Burger trước, sau đó là WELCOME
            playSequentially(animatorSetPhase1, animatorSetPhase2)
            start()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java).apply {
                // Thêm cờ để xóa Stack và tránh quay lại màn hình Splash
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }, TOTAL_DELAY)
    }
}