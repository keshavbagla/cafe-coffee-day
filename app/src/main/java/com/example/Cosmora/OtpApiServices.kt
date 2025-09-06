package com.example.Cosmora.api

import retrofit2.Response
import retrofit2.http.*

// Data classes for API requests/responses
data class SendOtpRequest(
    val mobile: String? = null,
    val email: String? = null,
    val template: String = "Your OTP is {{otp}}",
    val sender: String = "OTPSMS"
)

data class VerifyOtpRequest(
    val mobile: String? = null,
    val email: String? = null,
    val otp: String
)

data class OtpResponse(
    val success: Boolean,
    val message: String,
    val requestId: String? = null,
    val otp: String? = null, // Only for development/testing
    val verificationMethod: String = "SMS" // "SMS" or "EMAIL"
)

// MSG91 API Response
data class MSG91Response(
    val type: String,
    val message: String
)

// 2Factor API Response
data class TwoFactorResponse(
    val Status: String,
    val Details: String,
    val SessionId: String? = null
)

// Email OTP Service Response
data class EmailOtpResponse(
    val success: Boolean,
    val message: String,
    val otpId: String? = null
)

// EmailJS request/response models
data class EmailJSRequest(
    val service_id: String,
    val template_id: String,
    val user_id: String,
    val template_params: Map<String, String>
)

data class EmailJSResponse(
    val status: Int,
    val text: String
)

// OTP Method Enum
enum class OtpMethod {
    SMS, EMAIL, FIREBASE_SMS
}

// Retrofit API interface for MSG91
interface MSG91ApiService {
    @FormUrlEncoded
    @POST("api/v5/otp")
    suspend fun sendOtp(
        @Header("authkey") authKey: String,
        @Field("mobile") mobile: String,
        @Field("template_id") templateId: String,
        @Field("otp") otp: String? = null
    ): Response<MSG91Response>

    @GET("api/v5/otp/verify")
    suspend fun verifyOtp(
        @Header("authkey") authKey: String,
        @Query("otp") otp: String,
        @Query("mobile") mobile: String
    ): Response<MSG91Response>
}

// Retrofit API interface for 2Factor
interface TwoFactorApiService {
    @GET("API/V1/{api_key}/SMS/{mobile_number}/AUTOGEN")
    suspend fun sendOtp(
        @Path("api_key") apiKey: String,
        @Path("mobile_number") mobileNumber: String,
        @Query("co") companyName: String = "OTPSMS",
        @Query("otl") otpLength: Int = 6
    ): Response<TwoFactorResponse>

    @GET("API/V1/{api_key}/SMS/VERIFY/{session_id}/{otp}")
    suspend fun verifyOtp(
        @Path("api_key") apiKey: String,
        @Path("session_id") sessionId: String,
        @Path("otp") otp: String
    ): Response<TwoFactorResponse>
}

// Email OTP API interface using EmailJS (100% free service)
interface EmailOtpApiService {
    @POST("api/v1.0/email/send")
    @Headers("Content-Type: application/json")
    suspend fun sendEmailOtp(
        @Body request: EmailJSRequest
    ): Response<EmailJSResponse>
}

// Generic OTP API interface (for other services)
interface OtpApiService {
    @POST("send-otp")
    @Headers("Content-Type: application/json")
    suspend fun sendOtp(
        @Header("Authorization") apiKey: String,
        @Body request: SendOtpRequest
    ): Response<OtpResponse>

    @POST("verify-otp")
    @Headers("Content-Type: application/json")
    suspend fun verifyOtp(
        @Header("Authorization") apiKey: String,
        @Body request: VerifyOtpRequest
    ): Response<OtpResponse>
}