package com.example.multiplepages

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.multiplepages.ui.theme.MultiplepagesTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.security.MessageDigest

class LoginActivity : ComponentActivity() {

    companion object {
        private const val TAG = "LoginActivity"
        private const val MAX_FAILED_ATTEMPTS = 4
        private const val PREFS_NAME = "login_preferences"
        private const val KEY_REMEMBER_ME = "remember_me"
        private const val KEY_SAVED_EMAIL = "saved_email"
        private const val KEY_SAVED_PASSWORD = "saved_password"
        private const val KEY_DEVICE_ID = "device_id"
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var encryptedPrefs: SharedPreferences
    private var failedAttempts by mutableIntStateOf(0)
    private var isProcessing by mutableStateOf(false)
    private var forceShowLogin = false

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

            // Initialize encrypted preferences for secure storage
            initializeEncryptedPreferences()

            // Load saved credentials if remember me was enabled
            loadSavedCredentials()

            setContent {
                MultiplepagesTheme {
                    Surface(color = MaterialTheme.colorScheme.background) {
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
                            isAdminPassword = { password ->
                                handleAdminLogin(password)
                            },
                            isProcessing = isProcessing,
                            failedAttempts = failedAttempts,
                            savedEmail = savedEmail,
                            savedPassword = savedPassword,
                            isRememberMeChecked = isRememberMeChecked
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate: ${e.message}", e)
            Toast.makeText(this, "App initialization failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initializeEncryptedPreferences() {
        try {
            val masterKey = MasterKey.Builder(this)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            encryptedPrefs = EncryptedSharedPreferences.create(
                this,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing encrypted preferences: ${e.message}", e)
            // Fallback to regular SharedPreferences (less secure but functional)
            encryptedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
    }

    private fun loadSavedCredentials() {
        try {
            val currentDeviceId = generateDeviceId()
            val savedDeviceId = encryptedPrefs.getString(KEY_DEVICE_ID, "")
            val rememberMe = encryptedPrefs.getBoolean(KEY_REMEMBER_ME, false)

            // Only load credentials if remember me is enabled and device matches
            if (rememberMe && currentDeviceId == savedDeviceId) {
                savedEmail = encryptedPrefs.getString(KEY_SAVED_EMAIL, "") ?: ""
                savedPassword = encryptedPrefs.getString(KEY_SAVED_PASSWORD, "") ?: ""
                isRememberMeChecked = true

                Log.d(TAG, "Loaded saved credentials for device: $currentDeviceId")
            } else {
                // Clear saved credentials if device doesn't match or remember me disabled
                clearSavedCredentials()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading saved credentials: ${e.message}", e)
        }
    }

    private fun saveCredentials(email: String, password: String, rememberMe: Boolean) {
        try {
            val editor = encryptedPrefs.edit()

            if (rememberMe) {
                val deviceId = generateDeviceId()
                editor.putString(KEY_SAVED_EMAIL, email.lowercase().trim())
                editor.putString(KEY_SAVED_PASSWORD, password)
                editor.putString(KEY_DEVICE_ID, deviceId)
                editor.putBoolean(KEY_REMEMBER_ME, true)
                Log.d(TAG, "Credentials saved for device: $deviceId")
            } else {
                // Clear saved credentials if remember me is unchecked
                clearSavedCredentials()
            }

            editor.apply()
        } catch (e: Exception) {
            Log.e(TAG, "Error saving credentials: ${e.message}", e)
        }
    }

    private fun clearSavedCredentials() {
        try {
            val editor = encryptedPrefs.edit()
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

    // FIXED: Renamed from getDeviceId to generateDeviceId to avoid conflict
    private fun generateDeviceId(): String {
        try {
            // Create a unique device identifier based on Android ID and other factors
            val androidId = android.provider.Settings.Secure.getString(
                contentResolver,
                android.provider.Settings.Secure.ANDROID_ID
            )

            // Add additional device info for uniqueness
            val deviceInfo = "${android.os.Build.MANUFACTURER}_${android.os.Build.MODEL}_$androidId"

            // Hash the device info to create a consistent ID
            val bytes = MessageDigest.getInstance("SHA-256").digest(deviceInfo.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting device ID: ${e.message}", e)
            return "unknown_device"
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
        // Prevent multiple simultaneous login attempts
        if (isProcessing) {
            Log.d(TAG, "Already processing, ignoring request")
            return
        }

        try {
            Log.d(TAG, "=== Starting login process ===")
            Log.d(TAG, "Email: $email")
            Log.d(TAG, "Remember Me: $rememberMe")
            Log.d(TAG, "Failed attempts before: $failedAttempts")

            // Add basic validation
            if (email.isBlank()) {
                Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show()
                return
            }

            if (password.isBlank()) {
                Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show()
                return
            }

            // Normalize email to lowercase for case-insensitive comparison
            val normalizedEmail = email.lowercase().trim()

            // Validate email format
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(normalizedEmail).matches()) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                return
            }

            // Check if maximum attempts reached
            if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
                Log.d(TAG, "Max attempts reached, redirecting to signup")
                Toast.makeText(this, "Too many failed attempts. Redirecting to signup...", Toast.LENGTH_LONG).show()
                redirectToSignup()
                return
            }

            // Set processing state
            isProcessing = true
            Log.d(TAG, "Set isProcessing to true, starting Firebase auth")

            // Show loading message
            Toast.makeText(this, "Verifying credentials...", Toast.LENGTH_SHORT).show()

            // Perform Firebase authentication with normalized email
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
                Log.d(TAG, "User authenticated: ${user?.email}, UID: ${user?.uid}")
                Log.d(TAG, "User verified: ${user?.isEmailVerified}")

                // Save credentials if remember me is checked
                saveCredentials(email, password, rememberMe)

                Toast.makeText(this, "Welcome back, ${user?.displayName ?: email.substringBefore("@")}!", Toast.LENGTH_SHORT).show()

                // Navigate to HomeActivity after successful login
                try {
                    Log.d(TAG, "Creating intent for HomeActivity")
                    val intent = Intent(this, HomeActivity::class.java).apply {
                        putExtra("email", email)
                        putExtra("userId", user?.uid)
                        putExtra("userName", user?.displayName ?: email.substringBefore("@"))
                        putExtra("isEmailVerified", user?.isEmailVerified ?: false)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }

                    Log.d(TAG, "Starting HomeActivity...")
                    startActivity(intent)
                    Log.d(TAG, "Finishing LoginActivity...")
                    finish()

                } catch (e: Exception) {
                    Log.e(TAG, "Error navigating to HomeActivity: ${e.message}", e)
                    Toast.makeText(this, "Navigation error: ${e.message}", Toast.LENGTH_LONG).show()
                    isProcessing = false
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "=== LOGIN FAILURE ===")
                Log.w(TAG, "signInWithEmail:failure", exception)
                Log.e(TAG, "Firebase Auth Error: ${exception.message}")

                isProcessing = false
                failedAttempts++

                // Clear saved credentials on login failure
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

                    exception.message?.contains("invalid-email", ignoreCase = true) == true ->
                        "Invalid email format. Please check and try again."

                    exception.message?.contains("user-disabled", ignoreCase = true) == true ->
                        "This account has been disabled. Please contact support."

                    exception.message?.contains("too-many-requests", ignoreCase = true) == true ->
                        "Too many failed attempts. Please try again later."

                    exception.message?.contains("network-request-failed", ignoreCase = true) == true ->
                        "Network error. Please check your internet connection and try again."

                    exception.message?.contains("internal-error", ignoreCase = true) == true ->
                        "Internal error occurred. Please try again later."

                    else -> {
                        Log.e(TAG, "Unknown error: ${exception.message}")
                        "Login failed. Please check your credentials and try again."
                    }
                }

                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()

                // Check if max attempts reached after this failure
                if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
                    Log.d(TAG, "Max attempts reached after this failure")
                    Toast.makeText(this, "Maximum login attempts exceeded. Redirecting to signup...", Toast.LENGTH_LONG).show()
                    // Delay redirect to let user see the message
                    Handler(Looper.getMainLooper()).postDelayed({
                        redirectToSignup()
                    }, 2000)
                } else {
                    // Show remaining attempts
                    val remainingAttempts = MAX_FAILED_ATTEMPTS - failedAttempts
                    if (remainingAttempts <= 2) {
                        Toast.makeText(this, "$remainingAttempts attempts remaining", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun handleGoogleLogin() {
        try {
            Log.d(TAG, "Google login clicked")
            Toast.makeText(this, "Google login not implemented yet", Toast.LENGTH_SHORT).show()
            // TODO: Implement Google Sign-In
        } catch (e: Exception) {
            Log.e(TAG, "Error in handleGoogleLogin: ${e.message}", e)
            Toast.makeText(this, "Google login error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleSignupClick() {
        try {
            Log.d(TAG, "Signup clicked")
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error in handleSignupClick: ${e.message}", e)
            Toast.makeText(this, "Navigation to signup failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleAdminLogin(password: String): Boolean {
        return try {
            Log.d(TAG, "Checking admin password")

            if (password == "admin123") {
                Log.d(TAG, "Admin login successful")
                val intent = Intent(this, AdminActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
                true
            } else {
                Log.d(TAG, "Admin password incorrect")
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

        // Reset failed attempts when user returns to login screen
        if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
            failedAttempts = 0
        }
    }

    override fun onStart() {
        super.onStart()
        // Removed auto-navigation logic - always show login screen when coming from splash
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