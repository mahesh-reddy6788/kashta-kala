package com.kashta.kala

import android.content.Intent
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.kashta.kala.databinding.ActivitySplashBinding
import com.kashta.kala.utils.SessionManager

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)
        startSplashAnimations()
    }

    private fun startSplashAnimations() {
        // Initial states
        binding.ivSplashLogo.apply {
            scaleX = 0.4f; scaleY = 0.4f; rotation = -15f; alpha = 0f
        }
        binding.tvSplashWordmark.apply { translationY = 40f; alpha = 0f }
        binding.vSplashDivider.scaleX = 0f
        binding.tvSplashTagline.alpha = 0f

        // Step 1 — Logo icon (100ms → 700ms)
        binding.ivSplashLogo.animate()
            .scaleX(1f).scaleY(1f).rotation(0f).alpha(1f)
            .setDuration(600).setStartDelay(100)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                // Step 2 — Wordmark slide-up (700ms → 1100ms)
                binding.tvSplashWordmark.animate()
                    .translationY(0f).alpha(1f)
                    .setDuration(400)
                    .setInterpolator(DecelerateInterpolator())
                    .start()

                // Step 3 — Divider draw (1000ms → 1300ms)
                binding.vSplashDivider.animate()
                    .scaleX(1f).setDuration(300).setStartDelay(300)
                    .withEndAction {
                        // Step 4 — Tagline fade (1400ms → 1800ms)
                        binding.tvSplashTagline.animate()
                            .alpha(1f).setDuration(400)
                            .withEndAction {
                                // Step 5 — Gentle breath pulse
                                binding.logoGroup.animate()
                                    .scaleX(0.95f).scaleY(0.95f).setDuration(200)
                                    .withEndAction {
                                        binding.logoGroup.animate()
                                            .scaleX(1f).scaleY(1f).setDuration(200)
                                            .withEndAction { navigateToNext() }
                                            .start()
                                    }.start()
                            }.start()
                    }.start()
            }.start()
    }

    private fun navigateToNext() {
        startActivity(Intent(this, MainActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}
