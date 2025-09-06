package com.example.Cosmora

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SplashActivity : AppCompatActivity() {

    private lateinit var coffeeImage: ImageView
    private lateinit var splashText: TextView
    private var splashHandler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splashscreen)

        initializeViews()
        handleWindowInsets()
        startAnimations()
        scheduleSplashEnd()
    }

    private fun initializeViews() {
        coffeeImage = findViewById(R.id.coffeeView)
        splashText = findViewById(R.id.textView2)
    }

    private fun handleWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Apply padding to avoid overlap with system bars
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }
    }

    private fun startAnimations() {
        try {
            val shake = AnimationUtils.loadAnimation(this, R.anim.shake)
            coffeeImage.startAnimation(shake)

            // Optional: Add fade in animation for text
            splashText.alpha = 0f
            splashText.animate()
                .alpha(1f)
                .setDuration(1000)
                .setStartDelay(500)
                .start()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun scheduleSplashEnd() {
        splashHandler = Handler(Looper.getMainLooper())
        splashHandler?.postDelayed({
            navigateToLogin()
        }, resources.getInteger(R.integer.splash_animation_duration).toLong())
    }

    private fun navigateToLogin() {
        try {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()

            // Add smooth transition animation
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        } catch (e: Exception) {
            // Handle navigation error
            e.printStackTrace()
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        splashHandler?.removeCallbacksAndMessages(null)
        splashHandler = null
    }

    override fun onPause() {
        super.onPause()
        splashHandler?.removeCallbacksAndMessages(null)
    }

    override fun onResume() {
        super.onResume()
        if (splashHandler == null && !isFinishing) {
            scheduleSplashEnd()
        }
    }
}