package com.example.Cosmora.payment.adapters

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.Cosmora.payment.PaymentConfig
import com.example.Cosmora.payment.PaymentResult  // Import your custom PaymentResult
import com.example.Cosmora.payment.PaytmTransactionResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URLEncoder

abstract class BasePaymentAdapter(
    protected val activity: Activity,
    protected val onPaymentResult: (PaymentResult) -> Unit
) {
    abstract fun processPayment(
        amount: Double,
        orderId: String,
        customerId: String,
        config: PaymentConfig,
        additionalData: Map<String, Any> = emptyMap()
    )
}

// PhonePe Payment Adapter
class PhonePePaymentAdapter(
    activity: Activity,
    onPaymentResult: (PaymentResult) -> Unit
) : BasePaymentAdapter(activity, onPaymentResult) {

    override fun processPayment(
        amount: Double,
        orderId: String,
        customerId: String,
        config: PaymentConfig,
        additionalData: Map<String, Any>
    ) {
        try {
            // Create PhonePe UPI intent
            val upiId = "paytmqr281005050101@paytm" // Replace with actual UPI ID
            val payeeName = "Cosmora"
            val transactionNote = "Payment for Order: $orderId"

            val upiUrl = "upi://pay?pa=$upiId&pn=${URLEncoder.encode(payeeName, "UTF-8")}&" +
                    "am=$amount&cu=INR&tr=$orderId&tn=${URLEncoder.encode(transactionNote, "UTF-8")}"

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(upiUrl))
            intent.setPackage("com.phonepe.app") // PhonePe package

            if (intent.resolveActivity(activity.packageManager) != null) {
                activity.startActivityForResult(intent, PHONEPE_REQUEST_CODE)
            } else {
                onPaymentResult(PaymentResult.Failed("PhonePe app not found"))
            }
        } catch (e: Exception) {
            Log.e("PhonePePayment", "Error processing payment", e)
            onPaymentResult(PaymentResult.Failed(e.message ?: "Payment failed"))
        }
    }

    companion object {
        const val PHONEPE_REQUEST_CODE = 1001
    }
}

// Google Pay Payment Adapter
class GooglePayPaymentAdapter(
    activity: Activity,
    onPaymentResult: (PaymentResult) -> Unit
) : BasePaymentAdapter(activity, onPaymentResult) {

    override fun processPayment(
        amount: Double,
        orderId: String,
        customerId: String,
        config: PaymentConfig,
        additionalData: Map<String, Any>
    ) {
        try {
            val upiId = "your-upi-id@okaxis" // Replace with actual UPI ID
            val payeeName = "Cosmora"
            val transactionNote = "Payment for Order: $orderId"

            val upiUrl = "upi://pay?pa=$upiId&pn=${URLEncoder.encode(payeeName, "UTF-8")}&" +
                    "am=$amount&cu=INR&tr=$orderId&tn=${URLEncoder.encode(transactionNote, "UTF-8")}"

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(upiUrl))
            intent.setPackage("com.google.android.apps.nbu.paisa.user") // Google Pay package

            if (intent.resolveActivity(activity.packageManager) != null) {
                activity.startActivityForResult(intent, GOOGLEPAY_REQUEST_CODE)
            } else {
                onPaymentResult(PaymentResult.Failed("Google Pay app not found"))
            }
        } catch (e: Exception) {
            Log.e("GooglePayPayment", "Error processing payment", e)
            onPaymentResult(PaymentResult.Failed(e.message ?: "Payment failed"))
        }
    }

    companion object {
        const val GOOGLEPAY_REQUEST_CODE = 1002
    }
}

class RazorpayPaymentAdapter(
    activity: Activity,
    onPaymentResult: (PaymentResult) -> Unit
) : BasePaymentAdapter(activity, onPaymentResult) {

    override fun processPayment(
        amount: Double,
        orderId: String,
        customerId: String,
        config: PaymentConfig,
        additionalData: Map<String, Any>
    ) {
        try {
            // Razorpay integration (you'll need to add Razorpay SDK dependency)
            onPaymentResult(PaymentResult.Loading)

            // Simulate Razorpay payment for now
            CoroutineScope(Dispatchers.Main).launch {
                kotlinx.coroutines.delay(2000) // Simulate processing

                // In real implementation, use Razorpay SDK
                // Your PaymentResult.Success doesn't take parameters, so just call Success
                onPaymentResult(PaymentResult.Success)
            }
        } catch (e: Exception) {
            Log.e("RazorpayPayment", "Error processing payment", e)
            onPaymentResult(PaymentResult.Failed(e.message ?: "Payment failed"))
        }
    }
}

// PayPal Payment Adapter
class PayPalPaymentAdapter(
    activity: Activity,
    onPaymentResult: (PaymentResult) -> Unit
) : BasePaymentAdapter(activity, onPaymentResult) {

    override fun processPayment(
        amount: Double,
        orderId: String,
        customerId: String,
        config: PaymentConfig,
        additionalData: Map<String, Any>
    ) {
        try {
            onPaymentResult(PaymentResult.Loading)

            // PayPal integration (you'll need PayPal SDK)
            CoroutineScope(Dispatchers.Main).launch {
                kotlinx.coroutines.delay(2500) // Simulate processing

                // Your PaymentResult.Success doesn't take parameters
                onPaymentResult(PaymentResult.Success)
            }
        } catch (e: Exception) {
            Log.e("PayPalPayment", "Error processing payment", e)
            onPaymentResult(PaymentResult.Failed(e.message ?: "Payment failed"))
        }
    }
}

// Generic Card Payment Adapter (can be used with Stripe, Square, etc.)
class CardPaymentAdapter(
    activity: Activity,
    onPaymentResult: (PaymentResult) -> Unit
) : BasePaymentAdapter(activity, onPaymentResult) {

    override fun processPayment(
        amount: Double,
        orderId: String,
        customerId: String,
        config: PaymentConfig,
        additionalData: Map<String, Any>
    ) {
        try {
            onPaymentResult(PaymentResult.Loading)

            // Generic card payment (implement with your preferred payment processor)
            CoroutineScope(Dispatchers.Main).launch {
                kotlinx.coroutines.delay(3000) // Simulate card processing

                // Your PaymentResult.Success doesn't take parameters
                onPaymentResult(PaymentResult.Success)
            }
        } catch (e: Exception) {
            Log.e("CardPayment", "Error processing payment", e)
            onPaymentResult(PaymentResult.Failed(e.message ?: "Payment failed"))
        }
    }
}

// UPI Generic Adapter (for any UPI app)
class UPIPaymentAdapter(
    activity: Activity,
    onPaymentResult: (PaymentResult) -> Unit
) : BasePaymentAdapter(activity, onPaymentResult) {

    override fun processPayment(
        amount: Double,
        orderId: String,
        customerId: String,
        config: PaymentConfig,
        additionalData: Map<String, Any>
    ) {
        try {
            val upiId = config.additionalParams["upiId"] ?: "your-upi-id@paytm"
            val payeeName = config.additionalParams["payeeName"] ?: "Cosmora"
            val transactionNote = "Payment for Order: $orderId"

            val upiUrl = "upi://pay?pa=$upiId&pn=${URLEncoder.encode(payeeName, "UTF-8")}&" +
                    "am=$amount&cu=INR&tr=$orderId&tn=${URLEncoder.encode(transactionNote, "UTF-8")}"

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(upiUrl))

            if (intent.resolveActivity(activity.packageManager) != null) {
                activity.startActivityForResult(intent, UPI_REQUEST_CODE)
            } else {
                onPaymentResult(PaymentResult.Failed("No UPI app found"))
            }
        } catch (e: Exception) {
            Log.e("UPIPayment", "Error processing payment", e)
            onPaymentResult(PaymentResult.Failed(e.message ?: "Payment failed"))
        }
    }

    companion object {
        const val UPI_REQUEST_CODE = 1003
    }
}

// Payment Adapter Factory
object PaymentAdapterFactory {
    fun createAdapter(
        gatewayId: String,
        activity: Activity,
        onPaymentResult: (PaymentResult) -> Unit
    ): BasePaymentAdapter? {
        return when (gatewayId) {
            "phonepe" -> PhonePePaymentAdapter(activity, onPaymentResult)
            "googlepay" -> GooglePayPaymentAdapter(activity, onPaymentResult)
            "razorpay" -> RazorpayPaymentAdapter(activity, onPaymentResult)
            "paypal" -> PayPalPaymentAdapter(activity, onPaymentResult)
            "card" -> CardPaymentAdapter(activity, onPaymentResult)
            else -> null
        }
    }
}