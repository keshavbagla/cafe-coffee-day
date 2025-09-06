package com.example.Cosmora

import android.app.Activity
import com.example.Cosmora.api.OtpMethod

/**
 * Mock OTP Manager for Development/Testing
 * Use this class during development to test OTP functionality without actual API calls
 */
class MockOtpManager {
    private val validOtp = "123456"
    private var currentMethod = OtpMethod.FIREBASE_SMS

    suspend fun sendOtp(
        phoneNumber: String? = null,
        email: String? = null,
        activity: Activity? = null
    ): Result<Pair<String, OtpMethod>> {
        // Determine method based on parameters
        currentMethod = when {
            phoneNumber != null -> OtpMethod.FIREBASE_SMS
            email != null -> OtpMethod.EMAIL
            else -> OtpMethod.FIREBASE_SMS
        }

        // Validate input
        when (currentMethod) {
            OtpMethod.FIREBASE_SMS, OtpMethod.SMS -> {
                if (phoneNumber?.let { !isValidPhoneNumber(it) } == true) {
                    return Result.failure(Exception("Invalid phone number format"))
                }
            }
            OtpMethod.EMAIL -> {
                if (email?.let { !isValidEmail(it) } == true) {
                    return Result.failure(Exception("Invalid email format"))
                }
            }
        }

        // Simulate network delay
        kotlinx.coroutines.delay(1500)

        val message = when (currentMethod) {
            OtpMethod.FIREBASE_SMS -> "OTP sent successfully via Firebase SMS (Mock: Use 123456)"
            OtpMethod.EMAIL -> "OTP sent successfully via Email (Mock: Use 123456)"
            OtpMethod.SMS -> "OTP sent successfully via SMS (Mock: Use 123456)"
        }

        return Result.success(message to currentMethod)
    }

    suspend fun verifyOtp(phoneNumber: String?, email: String?, otp: String): Result<Boolean> {
        if (!isValidOtp(otp)) {
            return Result.failure(Exception("Invalid OTP format. Must be 6 numeric digits."))
        }

        kotlinx.coroutines.delay(1000)

        return if (otp == validOtp) {
            Result.success(true)
        } else {
            Result.failure(Exception("Invalid OTP. Use 123456 for testing."))
        }
    }

    fun getCurrentOtpMethod(): OtpMethod = currentMethod

    fun reset() {
        currentMethod = OtpMethod.FIREBASE_SMS
    }

    fun getRemainingSmsAttempts(): Int = 3

    private fun isValidOtp(otp: String): Boolean {
        return otp.length == 6 && otp.all { it.isDigit() }
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        return phoneNumber.length == 10 && phoneNumber.all { it.isDigit() }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}