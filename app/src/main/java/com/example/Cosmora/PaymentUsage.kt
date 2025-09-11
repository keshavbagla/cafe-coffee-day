package com.example.Cosmora

import android.app.Activity
import androidx.compose.runtime.*
import com.example.Cosmora.payment.*
import com.example.Cosmora.payment.adapters.BasePaymentAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Remove Stripe import, use your custom PaymentResult

class PaymentUsageExample {

    fun setupPaymentInActivity(activity: Activity) {

        val paymentManager = UnifiedPaymentManager(
            activity = activity,
            onPaymentResult = { result ->
                when (result) {
                    is PaymentResult.Success -> {
                        // Handle success - your PaymentResult.Success doesn't have parameters
                        println("Payment successful!")
                    }
                    is PaymentResult.Failed -> {
                        // Handle failure
                        println("Payment failed: ${result.error}")
                    }
                    is PaymentResult.Cancelled -> {
                        // Handle cancellation
                        println("Payment cancelled by user")
                    }
                    is PaymentResult.Loading -> {
                        // Show loading indicator
                        println("Processing payment...")
                    }
                }
            }
        )

        // 2. Setup all payment gateways (one-time setup)
        PaymentGatewayConfig.setupPaymentGateways(paymentManager)

        // 3. Make payments - it's this simple!

        // Pay with Paytm
        paymentManager.initiatePayment(
            gateway = PaymentGateway.PAYTM,
            amount = 299.99,
            customerId = "USER123",
            customerEmail = "user@example.com",
            customerMobile = "9876543210"
        )

        // Pay with PhonePe
        paymentManager.initiatePayment(
            gateway = PaymentGateway.PHONEPE,
            amount = 150.00,
            customerId = "USER123",
            customerMobile = "9876543210"
        )

        // Pay with Google Pay
        paymentManager.initiatePayment(
            gateway = PaymentGateway.GOOGLEPAY,
            amount = 75.50,
            customerId = "USER123"
        )

        // Cash on Delivery
        paymentManager.initiatePayment(
            gateway = PaymentGateway.CASH_DELIVERY,
            amount = 199.99,
            customerId = "USER123"
        )

        // Cosmora Card
        paymentManager.initiatePayment(
            gateway = PaymentGateway.COSMORA_CARD,
            amount = 99.99,
            customerId = "USER123"
        )
    }
}

enum class CustomPaymentMethods(val id: String, val displayName: String) {
    CRYPTO("crypto", "Cryptocurrency"),
    BANK_TRANSFER("bank_transfer", "Bank Transfer"),
    GIFT_CARD("gift_card", "Gift Card"),
    LOYALTY_POINTS("loyalty_points", "Loyalty Points")
}

class CustomPaymentAdapter(
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

            // Implement your custom payment logic here
            // For example, if this is a cryptocurrency payment:
            when (config.gateway.id) {
                "crypto" -> {
                    // Handle crypto payment
                    // This is where you'd integrate with your crypto payment service
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(3000)
                        onPaymentResult(PaymentResult.Success)
                    }
                }
                "bank_transfer" -> {
                    // Handle bank transfer
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(2000)
                        onPaymentResult(PaymentResult.Success)
                    }
                }
                "gift_card" -> {
                    // Handle gift card payment
                    val giftCardBalance = additionalData["balance"] as? Double ?: 0.0
                    if (giftCardBalance >= amount) {
                        onPaymentResult(PaymentResult.Success)
                    } else {
                        onPaymentResult(PaymentResult.Failed("Insufficient gift card balance"))
                    }
                }
                "loyalty_points" -> {
                    // Handle loyalty points payment
                    val pointsBalance = additionalData["points"] as? Double ?: 0.0
                    val pointsRequired = amount * 10 // 1 rupee = 10 points
                    if (pointsBalance >= pointsRequired) {
                        onPaymentResult(PaymentResult.Success)
                    } else {
                        onPaymentResult(PaymentResult.Failed("Insufficient loyalty points"))
                    }
                }
                else -> {
                    onPaymentResult(PaymentResult.Failed("Unsupported payment method"))
                }
            }
        } catch (e: Exception) {
            onPaymentResult(PaymentResult.Failed(e.message ?: "Payment processing failed"))
        }
    }
}