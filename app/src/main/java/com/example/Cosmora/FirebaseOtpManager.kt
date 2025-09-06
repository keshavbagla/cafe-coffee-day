package com.example.Cosmora

import android.app.Activity
import android.util.Log
import com.example.Cosmora.api.*
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

class FirebaseOtpManager private constructor() {

    companion object {
        private const val TAG = "FirebaseOtpManager"

        // Configuration constants
        private const val USE_FIREBASE = true // Set to true for Firebase, false for other services

        // MSG91 Configuration
        private const val MSG91_BASE_URL = "https://api.msg91.com/"
        private const val MSG91_AUTH_KEY = "465468AG5F3BZ1Dae68a6deccP1"
        private const val MSG91_TEMPLATE_ID = "YOUR_TEMPLATE_ID"

        // 2Factor Configuration
        private const val TWO_FACTOR_BASE_URL = "https://2factor.in/"
        private const val TWO_FACTOR_API_KEY = "YOUR_2FACTOR_API_KEY"

        // Email OTP Configuration (using EmailJS - 100% free)
        private const val EMAILJS_BASE_URL = "https://api.emailjs.com/"
        private const val EMAILJS_SERVICE_ID = "service_v2tq0ig"
        private const val EMAILJS_TEMPLATE_ID = "template_ib02r1d"
        private const val EMAILJS_USER_ID = "YOUR_EMAILJS_USER_ID"

        // SMS Attempt limits
        private const val MAX_SMS_ATTEMPTS = 3
        private const val SMS_RATE_LIMIT_WINDOW_MINUTES = 60

        @Volatile
        private var INSTANCE: FirebaseOtpManager? = null

        fun getInstance(): FirebaseOtpManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: FirebaseOtpManager().also { INSTANCE = it }
            }
        }
    }

    private var currentVerificationId: String? = null
    private var currentSessionId: String? = null
    private var currentEmailOtpId: String? = null
    private var currentOtpMethod: OtpMethod = OtpMethod.FIREBASE_SMS
    private var smsAttemptCount = 0
    private var lastSmsAttemptTime = 0L

    // Firebase Auth instance
    private val firebaseAuth = FirebaseAuth.getInstance()

    // Retrofit services
    private val msg91Service by lazy {
        Retrofit.Builder()
            .baseUrl(MSG91_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MSG91ApiService::class.java)
    }

    private val twoFactorService by lazy {
        Retrofit.Builder()
            .baseUrl(TWO_FACTOR_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TwoFactorApiService::class.java)
    }

    private val emailOtpService by lazy {
        Retrofit.Builder()
            .baseUrl(EMAILJS_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EmailOtpApiService::class.java)
    }

    // Store generated OTP for email verification (since EmailJS doesn't handle verification)
    private var generatedEmailOtp: String? = null

    /**
     * Send OTP - automatically determines method based on limits and fallback
     */
    suspend fun sendOtp(
        phoneNumber: String? = null,
        email: String? = null,
        activity: Activity
    ): Result<Pair<String, OtpMethod>> {
        return try {
            // Check if we should use email due to SMS limits
            if (shouldUseEmailFallback()) {
                if (email.isNullOrEmpty()) {
                    return Result.failure(Exception("Email is required when SMS limit reached"))
                }
                sendEmailOtp(email)
            } else if (phoneNumber != null) {
                // Try Firebase SMS first
                sendFirebaseSmsOtp(phoneNumber, activity)
            } else {
                Result.failure(Exception("Either phone number or email must be provided"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error sending OTP", e)
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    /**
     * Verify OTP based on current method
     */
    suspend fun verifyOtp(
        phoneNumber: String? = null,
        email: String? = null,
        otp: String
    ): Result<Boolean> {
        return try {
            // Validate OTP format
            if (!isValidOtp(otp)) {
                return Result.failure(Exception("Invalid OTP format. OTP must be 6 numeric digits."))
            }

            when (currentOtpMethod) {
                OtpMethod.FIREBASE_SMS -> verifyFirebaseSmsOtp(otp)
                OtpMethod.EMAIL -> verifyEmailOtpInternal(otp)
                OtpMethod.SMS -> {
                    if (phoneNumber != null) {
                        verifyOtpViaMSG91(phoneNumber, otp)
                    } else {
                        Result.failure(Exception("Phone number required for SMS verification"))
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error verifying OTP", e)
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    /**
     * Get current OTP method being used
     */
    fun getCurrentOtpMethod(): OtpMethod = currentOtpMethod

    /**
     * Check if email fallback should be used
     */
    private fun shouldUseEmailFallback(): Boolean {
        val currentTime = System.currentTimeMillis()

        // Reset counter if rate limit window has passed
        if (currentTime - lastSmsAttemptTime > TimeUnit.MINUTES.toMillis(SMS_RATE_LIMIT_WINDOW_MINUTES.toLong())) {
            smsAttemptCount = 0
        }

        return smsAttemptCount >= MAX_SMS_ATTEMPTS
    }

    /**
     * Send Firebase SMS OTP
     */
    private suspend fun sendFirebaseSmsOtp(phoneNumber: String, activity: Activity): Result<Pair<String, OtpMethod>> {
        return suspendCancellableCoroutine { continuation ->
            val formattedPhone = if (phoneNumber.startsWith("+91")) phoneNumber else "+91$phoneNumber"

            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    Log.d(TAG, "Firebase SMS verification completed automatically")
                    // Auto-verification completed
                    if (continuation.isActive) {
                        continuation.resume(Result.success("Auto-verified" to OtpMethod.FIREBASE_SMS))
                    }
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.e(TAG, "Firebase SMS verification failed", e)

                    when (e) {
                        is FirebaseTooManyRequestsException -> {
                            // SMS limit reached, increment counter
                            smsAttemptCount++
                            lastSmsAttemptTime = System.currentTimeMillis()

                            if (continuation.isActive) {
                                continuation.resume(
                                    Result.failure(Exception("SMS limit reached. Please use email verification."))
                                )
                            }
                        }
                        is FirebaseAuthInvalidCredentialsException -> {
                            if (continuation.isActive) {
                                continuation.resume(Result.failure(Exception("Invalid phone number")))
                            }
                        }
                        else -> {
                            if (continuation.isActive) {
                                continuation.resume(Result.failure(Exception("SMS verification failed: ${e.message}")))
                            }
                        }
                    }
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    Log.d(TAG, "Firebase SMS OTP sent")
                    currentVerificationId = verificationId
                    currentOtpMethod = OtpMethod.FIREBASE_SMS

                    if (continuation.isActive) {
                        continuation.resume(Result.success("OTP sent successfully via SMS" to OtpMethod.FIREBASE_SMS))
                    }
                }
            }

            val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(formattedPhone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }

    /**
     * Verify Firebase SMS OTP
     */
    private suspend fun verifyFirebaseSmsOtp(otp: String): Result<Boolean> {
        return suspendCancellableCoroutine { continuation ->
            val verificationId = currentVerificationId
                ?: run {
                    continuation.resume(Result.failure(Exception("No verification in progress")))
                    return@suspendCancellableCoroutine
                }

            val credential = PhoneAuthProvider.getCredential(verificationId, otp)

            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Firebase SMS OTP verified successfully")
                        currentVerificationId = null
                        continuation.resume(Result.success(true))
                    } else {
                        Log.e(TAG, "Firebase SMS OTP verification failed", task.exception)
                        val errorMessage = when (task.exception) {
                            is FirebaseAuthInvalidCredentialsException -> "Invalid OTP"
                            else -> "Verification failed: ${task.exception?.message}"
                        }
                        continuation.resume(Result.failure(Exception(errorMessage)))
                    }
                }
        }
    }

    /**
     * Send Email OTP using EmailJS (100% free service)
     */
    private suspend fun sendEmailOtp(email: String): Result<Pair<String, OtpMethod>> {
        // Generate 6-digit OTP
        generatedEmailOtp = (100000..999999).random().toString()

        val templateParams = mapOf(
            "to_email" to email,
            "otp_code" to generatedEmailOtp!!,
            "app_name" to "Your App Name"
        )

        val emailRequest = EmailJSRequest(
            service_id = EMAILJS_SERVICE_ID,
            template_id = EMAILJS_TEMPLATE_ID,
            user_id = EMAILJS_USER_ID,
            template_params = templateParams
        )

        val response = emailOtpService.sendEmailOtp(emailRequest)

        return if (response.isSuccessful && response.body()?.status == 200) {
            currentOtpMethod = OtpMethod.EMAIL
            Result.success("OTP sent successfully to email" to OtpMethod.EMAIL)
        } else {
            Result.failure(Exception("Failed to send email OTP: ${response.body()?.text ?: "Unknown error"}"))
        }
    }

    /**
     * Verify Email OTP (compare with generated OTP)
     */
    private suspend fun verifyEmailOtpInternal(otp: String): Result<Boolean> {
        val expectedOtp = generatedEmailOtp
            ?: return Result.failure(Exception("No email verification in progress"))

        return if (otp == expectedOtp) {
            generatedEmailOtp = null // Clear after successful verification
            Result.success(true)
        } else {
            Result.failure(Exception("Invalid email OTP"))
        }
    }

    // MSG91 Implementation (fallback)
    private suspend fun sendOtpViaMSG91(phoneNumber: String): Result<Pair<String, OtpMethod>> {
        val response = msg91Service.sendOtp(
            authKey = MSG91_AUTH_KEY,
            mobile = "91$phoneNumber",
            templateId = MSG91_TEMPLATE_ID
        )

        return if (response.isSuccessful) {
            val body = response.body()
            if (body?.type == "success") {
                currentOtpMethod = OtpMethod.SMS
                Result.success("OTP sent successfully via SMS" to OtpMethod.SMS)
            } else {
                Result.failure(Exception("Failed to send OTP: ${body?.message}"))
            }
        } else {
            Result.failure(Exception("API Error: ${response.code()} - ${response.message()}"))
        }
    }

    private suspend fun verifyOtpViaMSG91(phoneNumber: String, otp: String): Result<Boolean> {
        val response = msg91Service.verifyOtp(
            authKey = MSG91_AUTH_KEY,
            otp = otp,
            mobile = "91$phoneNumber"
        )

        return if (response.isSuccessful) {
            val body = response.body()
            if (body?.type == "success") {
                Result.success(true)
            } else {
                Result.failure(Exception("Invalid OTP: ${body?.message}"))
            }
        } else {
            Result.failure(Exception("API Error: ${response.code()} - ${response.message()}"))
        }
    }

    /**
     * Helper function to validate OTP format
     */
    private fun isValidOtp(otp: String): Boolean {
        return otp.length == 6 && otp.all { it.isDigit() }
    }


    fun reset() {
        currentVerificationId = null
        currentSessionId = null
        currentEmailOtpId = null
        generatedEmailOtp = null
        currentOtpMethod = OtpMethod.FIREBASE_SMS
        smsAttemptCount = 0
    }

    /**
     * Get remaining SMS attempts
     */
    fun getRemainingSmsAttempts(): Int {
        return (MAX_SMS_ATTEMPTS - smsAttemptCount).coerceAtLeast(0)
    }
}