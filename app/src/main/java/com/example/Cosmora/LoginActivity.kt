package com.example.Cosmora

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import com.example.Cosmora.screens.OTPVerificationScreen
import com.example.Cosmora.ui.theme.MultiplepagesTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.security.KeyStore
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class LoginActivity : ComponentActivity() {

    companion object {
        private const val TAG = "LoginActivity"
        private const val MAX_FAILED_ATTEMPTS = 4
        private const val PREFS_NAME = "login_preferences"
        private const val KEY_REMEMBER_ME = "remember_me"
        private const val KEY_SAVED_EMAIL = "saved_email"
        private const val KEY_SAVED_PASSWORD = "saved_password"
        private const val KEY_DEVICE_ID = "device_id"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "LoginSecretKey"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var securePrefs: SharedPreferences
    private lateinit var otpManager: FirebaseOtpManager
    private var otpErrorMessage: String = ""
    private var failedAttempts by mutableIntStateOf(0)
    private var isProcessing by mutableStateOf(false)
    private var forceShowLogin = false

    // Navigation states
    private var currentFlow by mutableStateOf("login") // "login", "phone", "otp", "home"
    private var userEmail by mutableStateOf("")
    private var userPhone by mutableStateOf("")

    // Remember Me states
    private var savedEmail by mutableStateOf("")
    private var savedPassword by mutableStateOf("")
    private var isRememberMeChecked by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            // Check if we should force show login (e.g., user logged out)
            forceShowLogin = intent.getBooleanExtra("force_login", false)

            // Initialize Firebase Auth
            auth = Firebase.auth

            // Initialize OTP Manager
            otpManager = FirebaseOtpManager.getInstance()

            // Initialize secure preferences
            initializeSecurePreferences()

            // Load saved credentials if remember me was enabled
            loadSavedCredentials()

            setContent {
                MultiplepagesTheme {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        when (currentFlow) {
                            "login" -> {
                                LoginScreen(
                                    onLogin = { email, password, rememberMe ->
                                        handleLogin(email, password, rememberMe)
                                    },
                                    onGoogleLogin = {
                                        handleGoogleLogin()
                                    },
                                    onSignupClick = {
                                        handleSignupClick()
                                    },
                                    onPhoneLogin = { email: String ->
                                        // Start phone verification flow after basic validation
                                        userEmail = email
                                        currentFlow = "phone"
                                    },
                                    isAdminPassword = { password: String ->
                                        handleAdminLogin(password)
                                    },
                                    isProcessing = isProcessing,
                                    failedAttempts = failedAttempts,
                                    savedEmail = savedEmail,
                                    savedPassword = savedPassword,
                                    isRememberMeChecked = isRememberMeChecked
                                )
                            }
                            "phone" -> {
                                PhoneNumberScreen(
                                    onSendOtp = { phoneNumber ->
                                        userPhone = "+91$phoneNumber" // Adding country code
                                        handleSendOtp(phoneNumber)
                                    },
                                    onBack = {
                                        currentFlow = "login"
                                    },
                                    onSkip = {
                                        navigateToHome()
                                    },
                                    isLoading = isProcessing,
                                    errorMessage = otpErrorMessage
                                )
                            }
                            "otp" -> {
                                OTPVerificationScreen(
                                    phoneNumber = userPhone,
                                    onOtpVerified = { otp ->
                                        handleOtpVerification(otp)
                                    },
                                    onResendOtp = {
                                        handleResendOtp()
                                    },
                                    onBack = {
                                        currentFlow = "phone"
                                    },
                                    isLoading = isProcessing,
                                    errorMessage = otpErrorMessage
                                )
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate: ${e.message}", e)
            Toast.makeText(this, "App initialization failed", Toast.LENGTH_SHORT).show()
        }
    }

    // Custom encryption methods using Android Keystore
    private fun generateSecretKey() {
        try {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()

            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
            Log.d(TAG, "Secret key generated successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error generating secret key: ${e.message}", e)
        }
    }

    private fun getSecretKey(): SecretKey? {
        return try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
            keyStore.load(null)

            if (!keyStore.containsAlias(KEY_ALIAS)) {
                generateSecretKey()
            }

            keyStore.getKey(KEY_ALIAS, null) as SecretKey
        } catch (e: Exception) {
            Log.e(TAG, "Error getting secret key: ${e.message}", e)
            null
        }
    }

    private fun encryptData(plaintext: String): String? {
        return try {
            val secretKey = getSecretKey() ?: return null
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)

            val iv = cipher.iv
            val encryptionResult = cipher.doFinal(plaintext.toByteArray())

            // Combine IV and encrypted data
            val combined = iv + encryptionResult
            Base64.encodeToString(combined, Base64.DEFAULT)
        } catch (e: Exception) {
            Log.e(TAG, "Error encrypting data: ${e.message}", e)
            null
        }
    }

    private fun decryptData(encryptedData: String): String? {
        return try {
            val secretKey = getSecretKey() ?: return null
            val decodedData = Base64.decode(encryptedData, Base64.DEFAULT)

            // Extract IV (first 12 bytes for GCM)
            val iv = decodedData.copyOfRange(0, 12)
            val encryptedBytes = decodedData.copyOfRange(12, decodedData.size)

            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

            val decryptedBytes = cipher.doFinal(encryptedBytes)
            String(decryptedBytes)
        } catch (e: Exception) {
            Log.e(TAG, "Error decrypting data: ${e.message}", e)
            null
        }
    }

    private fun handleSendOtp(phoneNumber: String) {
        lifecycleScope.launch {
            try {
                Log.d(TAG, "Sending OTP to: +91$phoneNumber")
                isProcessing = true
                otpErrorMessage = ""

                val result = otpManager.sendOtp(
                    phoneNumber = phoneNumber,
                    email = userEmail,
                    activity = this@LoginActivity
                )

                result.fold(
                    onSuccess = { (message, method) ->
                        isProcessing = false
                        Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "OTP sent via method: $method")
                        currentFlow = "otp"
                    },
                    onFailure = { exception ->
                        isProcessing = false
                        otpErrorMessage = exception.message ?: "Failed to send OTP"
                        Log.e(TAG, "Failed to send OTP: ${exception.message}", exception)
                        Toast.makeText(this@LoginActivity, otpErrorMessage, Toast.LENGTH_SHORT).show()
                    }
                )

            } catch (e: Exception) {
                isProcessing = false
                otpErrorMessage = "Error sending OTP: ${e.message}"
                Log.e(TAG, "Error sending OTP: ${e.message}", e)
                Toast.makeText(this@LoginActivity, otpErrorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleOtpVerification(otp: String) {
        lifecycleScope.launch {
            try {
                Log.d(TAG, "Verifying OTP: $otp")
                isProcessing = true
                otpErrorMessage = ""

                val result = otpManager.verifyOtp(
                    phoneNumber = userPhone.removePrefix("+91"),
                    email = userEmail,
                    otp = otp
                )

                result.fold(
                    onSuccess = { verified ->
                        isProcessing = false
                        if (verified) {
                            Toast.makeText(this@LoginActivity, "Phone number verified successfully!", Toast.LENGTH_SHORT).show()
                            navigateToHome()
                        } else {
                            otpErrorMessage = "Verification failed"
                            Toast.makeText(this@LoginActivity, "Verification failed", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onFailure = { exception ->
                        isProcessing = false
                        otpErrorMessage = exception.message ?: "Invalid OTP"
                        Log.e(TAG, "OTP verification failed: ${exception.message}", exception)
                        Toast.makeText(this@LoginActivity, otpErrorMessage, Toast.LENGTH_SHORT).show()
                    }
                )

            } catch (e: Exception) {
                isProcessing = false
                otpErrorMessage = "Error verifying OTP: ${e.message}"
                Log.e(TAG, "Error verifying OTP: ${e.message}", e)
                Toast.makeText(this@LoginActivity, otpErrorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleResendOtp() {
        lifecycleScope.launch {
            try {
                Log.d(TAG, "Resending OTP to: $userPhone")
                isProcessing = true
                otpErrorMessage = ""

                otpManager.reset()

                val result = otpManager.sendOtp(
                    phoneNumber = userPhone.removePrefix("+91"),
                    email = userEmail,
                    activity = this@LoginActivity
                )

                result.fold(
                    onSuccess = { (message, method) ->
                        isProcessing = false
                        Toast.makeText(this@LoginActivity, "OTP resent successfully", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "OTP resent via method: $method")
                    },
                    onFailure = { exception ->
                        isProcessing = false
                        otpErrorMessage = exception.message ?: "Failed to resend OTP"
                        Log.e(TAG, "Failed to resend OTP: ${exception.message}", exception)
                        Toast.makeText(this@LoginActivity, otpErrorMessage, Toast.LENGTH_SHORT).show()
                    }
                )

            } catch (e: Exception) {
                isProcessing = false
                otpErrorMessage = "Error resending OTP: ${e.message}"
                Log.e(TAG, "Error resending OTP: ${e.message}", e)
                Toast.makeText(this@LoginActivity, otpErrorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToHome(phoneNumber: String = userPhone, email: String = userEmail) {
        try {
            val user = auth.currentUser
            Log.d(TAG, "Navigating to HomeActivity")

            val intent = Intent(this, HomeActivity::class.java).apply {
                putExtra("email", email.ifEmpty { user?.email ?: "" })
                putExtra("phoneNumber", phoneNumber)
                putExtra("userId", user?.uid)
                putExtra("userName", user?.displayName ?: email.substringBefore("@"))
                putExtra("isEmailVerified", user?.isEmailVerified ?: false)
                putExtra("isPhoneVerified", phoneNumber.isNotEmpty())
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            startActivity(intent)
            finish()

        } catch (e: Exception) {
            Log.e(TAG, "Error navigating to HomeActivity: ${e.message}", e)
            Toast.makeText(this, "Navigation error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun initializeSecurePreferences() {
        try {
            securePrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            Log.d(TAG, "Secure preferences initialized")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing secure preferences: ${e.message}", e)
        }
    }

    private fun loadSavedCredentials() {
        try {
            val currentDeviceId = generateDeviceId()
            val savedDeviceId = securePrefs.getString(KEY_DEVICE_ID, "")
            val rememberMe = securePrefs.getBoolean(KEY_REMEMBER_ME, false)

            if (rememberMe && currentDeviceId == savedDeviceId) {
                val encryptedEmail = securePrefs.getString(KEY_SAVED_EMAIL, "")
                val encryptedPassword = securePrefs.getString(KEY_SAVED_PASSWORD, "")

                savedEmail = if (!encryptedEmail.isNullOrEmpty()) {
                    decryptData(encryptedEmail) ?: ""
                } else ""

                savedPassword = if (!encryptedPassword.isNullOrEmpty()) {
                    decryptData(encryptedPassword) ?: ""
                } else ""

                isRememberMeChecked = savedEmail.isNotEmpty() && savedPassword.isNotEmpty()
                Log.d(TAG, "Loaded saved credentials for device: $currentDeviceId")
            } else {
                clearSavedCredentials()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading saved credentials: ${e.message}", e)
        }
    }

    private fun saveCredentials(email: String, password: String, rememberMe: Boolean) {
        try {
            val editor = securePrefs.edit()

            if (rememberMe) {
                val deviceId = generateDeviceId()
                val encryptedEmail = encryptData(email.lowercase().trim())
                val encryptedPassword = encryptData(password)

                if (encryptedEmail != null && encryptedPassword != null) {
                    editor.putString(KEY_SAVED_EMAIL, encryptedEmail)
                    editor.putString(KEY_SAVED_PASSWORD, encryptedPassword)
                    editor.putString(KEY_DEVICE_ID, deviceId)
                    editor.putBoolean(KEY_REMEMBER_ME, true)
                    Log.d(TAG, "Credentials saved for device: $deviceId")
                } else {
                    Log.e(TAG, "Failed to encrypt credentials")
                }
            } else {
                clearSavedCredentials()
            }

            editor.apply()
        } catch (e: Exception) {
            Log.e(TAG, "Error saving credentials: ${e.message}", e)
        }
    }

    private fun clearSavedCredentials() {
        try {
            val editor = securePrefs.edit()
            editor.remove(KEY_SAVED_EMAIL)
            editor.remove(KEY_SAVED_PASSWORD)
            editor.remove(KEY_DEVICE_ID)
            editor.remove(KEY_REMEMBER_ME)
            editor.apply()

            savedEmail = ""
            savedPassword = ""
            isRememberMeChecked = false

            Log.d(TAG, "Saved credentials cleared")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing credentials: ${e.message}", e)
        }
    }

    private fun generateDeviceId(): String {
        return try {
            val androidId = android.provider.Settings.Secure.getString(
                contentResolver,
                android.provider.Settings.Secure.ANDROID_ID
            )

            val deviceInfo = "${android.os.Build.MANUFACTURER}_${android.os.Build.MODEL}_$androidId"
            val bytes = MessageDigest.getInstance("SHA-256").digest(deviceInfo.toByteArray())
            bytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting device ID: ${e.message}", e)
            "unknown_device"
        }
    }

    private fun redirectToSignup() {
        try {
            Toast.makeText(this, "Please create a new account", Toast.LENGTH_LONG).show()
            val intent = Intent(this, SignupActivity::class.java).apply {
                putExtra("from_failed_login", true)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Log.e(TAG, "Error redirecting to signup: ${e.message}", e)
        }
    }

    private fun handleLogin(email: String, password: String, rememberMe: Boolean) {
        if (isProcessing) {
            Log.d(TAG, "Already processing, ignoring request")
            return
        }

        try {
            Log.d(TAG, "=== Starting login process ===")

            if (email.isBlank()) {
                Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show()
                return
            }

            if (password.isBlank()) {
                Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show()
                return
            }

            val normalizedEmail = email.lowercase().trim()

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(normalizedEmail).matches()) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                return
            }

            if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
                Toast.makeText(this, "Too many failed attempts. Redirecting to signup...", Toast.LENGTH_LONG).show()
                redirectToSignup()
                return
            }

            isProcessing = true
            Toast.makeText(this, "Verifying credentials...", Toast.LENGTH_SHORT).show()

            performPasswordLogin(normalizedEmail, password, rememberMe)

        } catch (e: Exception) {
            isProcessing = false
            Log.e(TAG, "Error in handleLogin: ${e.message}", e)
            Toast.makeText(this, "Login error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun performPasswordLogin(email: String, password: String, rememberMe: Boolean) {
        Log.d(TAG, "Attempting Firebase authentication for: $email")

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                Log.d(TAG, "=== LOGIN SUCCESS ===")
                isProcessing = false
                failedAttempts = 0

                val user = authResult.user
                saveCredentials(email, password, rememberMe)
                userEmail = email

                Toast.makeText(this, "Login successful! Please verify your phone number.", Toast.LENGTH_SHORT).show()
                currentFlow = "phone"
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "=== LOGIN FAILURE ===")
                isProcessing = false
                failedAttempts++

                if (rememberMe) {
                    clearSavedCredentials()
                }

                val errorMessage = when {
                    exception.message?.contains("INVALID_LOGIN_CREDENTIALS", ignoreCase = true) == true ||
                            exception.message?.contains("invalid-credential", ignoreCase = true) == true ->
                        "Invalid email or password. Please check your credentials and try again."

                    exception.message?.contains("user-not-found", ignoreCase = true) == true ->
                        "No account found with this email. Please sign up first."

                    exception.message?.contains("wrong-password", ignoreCase = true) == true ->
                        "Incorrect password. Please try again."

                    exception.message?.contains("network-request-failed", ignoreCase = true) == true ->
                        "Network error. Please check your internet connection and try again."

                    else -> "Login failed. Please check your credentials and try again."
                }

                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()

                if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
                    Toast.makeText(this, "Maximum login attempts exceeded. Redirecting to signup...", Toast.LENGTH_LONG).show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        redirectToSignup()
                    }, 2000)
                }
            }
    }

    private fun handleGoogleLogin() {
        try {
            Log.d(TAG, "Google login clicked")
            Toast.makeText(this, "After Google login, phone verification is recommended for security", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e(TAG, "Error in handleGoogleLogin: ${e.message}", e)
            Toast.makeText(this, "Google login error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleSignupClick() {
        try {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error in handleSignupClick: ${e.message}", e)
            Toast.makeText(this, "Navigation to signup failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleAdminLogin(password: String): Boolean {
        return try {
            if (password == "admin123") {
                val intent = Intent(this, AdminActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in handleAdminLogin: ${e.message}", e)
            Toast.makeText(this, "Admin login failed", Toast.LENGTH_SHORT).show()
            false
        }
    }

    override fun onResume() {
        super.onResume()
        isProcessing = false
        if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
            failedAttempts = 0
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "LoginActivity started - showing login screen")
    }

    override fun onDestroy() {
        try {
            super.onDestroy()
            Log.d(TAG, "LoginActivity destroyed")
        } catch (e: Exception) {
            Log.e(TAG, "Error in onDestroy: ${e.message}", e)
        }
    }
}