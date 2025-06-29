package com.example.multiplepages

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(
    onLogin: (String, String) -> Unit,
    onGoogleLogin: () -> Unit,
    // Removed onFacebookLogin parameter - no longer needed
    onSignupClick: () -> Unit,
    isAdminPassword: (String) -> Boolean,
    isProcessing: Boolean = false,
    failedAttempts: Int = 0
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val maxAttempts = 4
    val showWarning = failedAttempts >= 2 && failedAttempts < maxAttempts

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            "Welcome Back 👋",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            "Sign in to your account",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )

        // Show warning message if there are failed attempts
        if (showWarning) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
            ) {
                Text(
                    text = "⚠️ ${maxAttempts - failedAttempts} attempts remaining before redirect to signup",
                    color = Color(0xFFE65100),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it.trim() },
            label = { Text("Email Address") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            enabled = !isProcessing,
            isError = failedAttempts > 0 && email.isBlank(),
            supportingText = {
                if (failedAttempts > 0 && email.isBlank()) {
                    Text("Email is required", color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )

        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            enabled = !isProcessing,
            isError = failedAttempts > 0 && password.isBlank(),
            supportingText = {
                if (failedAttempts > 0 && password.isBlank()) {
                    Text("Password is required", color = MaterialTheme.colorScheme.error)
                }
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )

        // Remember Me & Forgot Password Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = { rememberMe = it },
                enabled = !isProcessing
            )
            Text(
                "Remember Me",
                modifier = Modifier.clickable(enabled = !isProcessing) { rememberMe = !rememberMe }
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Forgot Password?",
                color = if (isProcessing) Color.Gray else MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(enabled = !isProcessing) {
                    Toast.makeText(context, "Password recovery feature coming soon", Toast.LENGTH_SHORT).show()
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Login Button
        Button(
            onClick = {
                if (isProcessing) return@Button

                val trimmedEmail = email.trim()

                // Input validation
                when {
                    trimmedEmail.isBlank() -> {
                        Toast.makeText(context, "Please enter your email address", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    password.isBlank() -> {
                        Toast.makeText(context, "Please enter your password", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches() -> {
                        Toast.makeText(context, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    password == "admin123" -> {
                        // Handle admin login
                        if (isAdminPassword(password)) {
                            Toast.makeText(context, "Admin Login Successful", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Admin login failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else -> {
                        // Regular user login
                        try {
                            onLogin(trimmedEmail, password)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            enabled = !isProcessing && failedAttempts < maxAttempts,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
        ) {
            when {
                isProcessing -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Verifying...", fontSize = 16.sp)
                    }
                }
                failedAttempts >= maxAttempts -> {
                    Text("Max Attempts Reached", fontSize = 16.sp)
                }
                else -> {
                    Text("Sign In", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Divider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.Divider(modifier = Modifier.weight(1f))
            Text(
                "  Or continue with  ",
                fontSize = 14.sp,
                color = Color.Gray
            )
            androidx.compose.material3.Divider(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Social Login Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Google Login Button
            Card(
                modifier = Modifier
                    .clickable(enabled = !isProcessing) {
                        try {
                            onGoogleLogin()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Google login error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .size(60.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google_icon_2),
                        contentDescription = "Google",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Facebook Login Button - DISABLED BUT KEPT FOR FUTURE
            Card(
                modifier = Modifier
                    .clickable(enabled = false) {
                        // Disabled for now - will be enabled when Facebook SDK is properly configured
                        Toast.makeText(context, "Facebook login coming soon", Toast.LENGTH_SHORT).show()
                    }
                    .size(60.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Gray.copy(alpha = 0.3f) // Make it look disabled
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.facebook_3_1),
                        contentDescription = "Facebook (Coming Soon)",
                        modifier = Modifier.size(24.dp),
                        alpha = 0.5f // Make the icon look disabled
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Signup Link
        Row {
            Text("Don't have an account?")
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Sign Up",
                color = if (isProcessing) Color.Gray else MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(enabled = !isProcessing) {
                    try {
                        onSignupClick()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Navigation error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }
}