package com.example.multiplepages

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : ComponentActivity() {

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LoginScreen(
                onLogin = { email, password ->
                    if (password == "admin@123") {
                        startActivity(Intent(this, AdminActivity::class.java))
                        finish()
                    } else {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    startActivity(Intent(this, UserActivity::class.java))
                                    finish()
                                } else {
                                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                },
                onGoogleLogin = {
                    Toast.makeText(this, "Google login not yet implemented", Toast.LENGTH_SHORT).show()
                },
                onFacebookLogin = {
                    Toast.makeText(this, "Facebook login not yet implemented", Toast.LENGTH_SHORT).show()
                },
                onSignupClick = {
                    startActivity(Intent(this, SignupActivity::class.java))
                },
                isAdminPassword = { password -> password == "admin@123" }
            )
        }
    }
}
