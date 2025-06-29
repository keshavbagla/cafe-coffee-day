package com.example.multiplepages
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splashscreen)

        val image = findViewById<ImageView>(R.id.coffeeView)
        val shake = AnimationUtils.loadAnimation(this, R.anim.shake)
        image.startAnimation(shake)

        Handler(Looper.getMainLooper()).postDelayed({
            // Always go to LoginActivity after splash screen
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}