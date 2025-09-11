package com.example.Cosmora.payment

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.util.*

// Custom PaymentResult sealed class (not Stripe's)
sealed class PaymentResult {
    object Loading : PaymentResult()
    object Success : PaymentResult()
    data class Failed(val error: String) : PaymentResult()
    object Cancelled : PaymentResult()
}

// Transaction result data class
data class PaytmTransactionResult(
    val orderId: String,
    val txnId: String,
    val txnAmount: String,
    val status: String,
    val respCode: String,
    val respMsg: String,
    val checksum: String?
)

// Enum for all supported payment methods
enum class PaymentGateway(
    val id: String,
    val displayName: String,
    val packageName: String?,
    val playStoreUrl: String?,
    val intentAction: String? = null,
    val supportedCountries: List<String> = listOf("IN")
) {
    PAYTM(
        id = "paytm",
        displayName = "Paytm",
        packageName = "net.one97.paytm",
        playStoreUrl = "https://play.google.com/store/apps/details?id=net.one97.paytm"
    ),
    PHONEPE(
        id = "phonepe",
        displayName = "PhonePe",
        packageName = "com.phonepe.app",
        playStoreUrl = "https://play.google.com/store/apps/details?id=com.phonepe.app",
        intentAction = "android.intent.action.VIEW"
    ),
    GOOGLEPAY(
        id = "googlepay",
        displayName = "Google Pay",
        packageName = "com.google.android.apps.nbu.paisa.user",
        playStoreUrl = "https://play.google.com/store/apps/details?id=com.google.android.apps.nbu.paisa.user"
    ),
    RAZORPAY(
        id = "razorpay",
        displayName = "Razorpay",
        packageName = null, // Web-based primarily
        playStoreUrl = null
    ),
    PAYPAL(
        id = "paypal",
        displayName = "PayPal",
        packageName = "com.paypal.android.p2pmobile",
        playStoreUrl = "https://play.google.com/store/apps/details?id=com.paypal.android.p2pmobile",
        supportedCountries = listOf("US", "UK", "CA", "AU", "IN")
    ),
    CARD_PAYMENT(
        id = "card",
        displayName = "Credit/Debit Card",
        packageName = null,
        playStoreUrl = null
    ),
    CASH_DELIVERY(
        id = "cash_delivery",
        displayName = "Cash on Delivery",
        packageName = null,
        playStoreUrl = null
    ),
    CASH_STORE(
        id = "cash_store",
        displayName = "Cash at Store",
        packageName = null,
        playStoreUrl = null
    ),
    COSMORA_CARD(
        id = "cosmora_card",
        displayName = "Cosmora Card",
        packageName = null,
        playStoreUrl = null
    )
}

// Base payment configuration
data class PaymentConfig(
    val gateway: PaymentGateway,
    val merchantId: String? = null,
    val merchantKey: String? = null,
    val environment: PaymentEnvironment = PaymentEnvironment.STAGING,
    val additionalParams: Map<String, String> = emptyMap()
)

enum class PaymentEnvironment {
    STAGING, PRODUCTION
}

// Unified Payment Manager
class UnifiedPaymentManager(
    private val activity: Activity,
    private val onPaymentResult: (PaymentResult) -> Unit
) {

    private val paymentConfigs = mutableMapOf<PaymentGateway, PaymentConfig>()

    // Initialize payment configurations
    fun addPaymentConfig(config: PaymentConfig) {
        paymentConfigs[config.gateway] = config
    }

    fun initiatePayment(
        gateway: PaymentGateway,
        amount: Double,
        orderId: String? = null,
        customerId: String,
        customerEmail: String? = null,
        customerMobile: String? = null,
        additionalData: Map<String, Any> = emptyMap()
    ) {
        val config = paymentConfigs[gateway]
        if (config == null) {
            onPaymentResult(PaymentResult.Failed("Payment gateway not configured: ${gateway.displayName}"))
            return
        }

        when (gateway) {
            PaymentGateway.PAYTM -> handlePaytmPayment(amount, orderId, customerId, customerEmail, customerMobile, config)
            PaymentGateway.PHONEPE -> handlePhonePePayment(amount, orderId, customerId, customerMobile, config)
            PaymentGateway.GOOGLEPAY -> handleGooglePayPayment(amount, orderId, customerId, config)
            PaymentGateway.RAZORPAY -> handleRazorpayPayment(amount, orderId, customerId, customerEmail, customerMobile, config)
            PaymentGateway.PAYPAL -> handlePayPalPayment(amount, orderId, customerId, customerEmail, config)
            PaymentGateway.CARD_PAYMENT -> handleCardPayment(amount, orderId, customerId, customerEmail, config)
            PaymentGateway.CASH_DELIVERY -> handleCashOnDelivery(amount, orderId, customerId)
            PaymentGateway.CASH_STORE -> handleCashAtStore(amount, orderId, customerId)
            PaymentGateway.COSMORA_CARD -> handleCosmoraCard(amount, orderId, customerId)
        }
    }

    // Check if app is installed
    private fun isAppInstalled(packageName: String?): Boolean {
        if (packageName == null) return false
        return try {
            activity.packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun showInstallDialog(
        gateway: PaymentGateway,
        onInstall: () -> Unit,
        onContinueWeb: () -> Unit,
        onCancel: () -> Unit
    ) {
        AlertDialog.Builder(activity)
            .setTitle("${gateway.displayName} Payment")
            .setMessage("For better experience, install ${gateway.displayName} app. Or continue with web payment?")
            .setPositiveButton("Install ${gateway.displayName}") { _, _ -> onInstall() }
            .setNegativeButton("Continue with Web") { _, _ -> onContinueWeb() }
            .setNeutralButton("Cancel") { _, _ -> onCancel() }
            .setCancelable(false)
            .show()
    }

    // Open app in Play Store
    private fun openInPlayStore(gateway: PaymentGateway) {
        gateway.playStoreUrl?.let { url ->
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url.replace("https://play.google.com/store/apps/details?id=", "market://details?id=")))
                activity.startActivity(intent)
            } catch (e: Exception) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                activity.startActivity(intent)
            }
        }
    }

    private fun generateOrderId(): String {
        return "COSMORA_${System.currentTimeMillis()}_${Random().nextInt(1000)}"
    }

    private fun startPaytmSDKPayment(
        amount: Double,
        orderId: String,
        customerId: String,
        customerEmail: String?,
        customerMobile: String?,
        config: PaymentConfig,
        useAppInvoke: Boolean
    ) {
        onPaymentResult(PaymentResult.Loading)

        // Simulate Paytm SDK call
        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)

            val result = PaytmTransactionResult(
                orderId = orderId,
                txnId = "PAYTM_${System.currentTimeMillis()}",
                txnAmount = amount.toString(),
                status = "TXN_SUCCESS",
                respCode = "01",
                respMsg = "Payment successful via Paytm",
                checksum = null
            )

            // Convert to your payment result format - Success doesn't take parameters
            onPaymentResult(PaymentResult.Success)
        }
    }

    private fun handlePaytmPayment(
        amount: Double,
        orderId: String?,
        customerId: String,
        customerEmail: String?,
        customerMobile: String?,
        config: PaymentConfig
    ) {
        val finalOrderId = orderId ?: generateOrderId()

        if (isAppInstalled(PaymentGateway.PAYTM.packageName)) {
            // Use Paytm SDK with app invoke
            startPaytmSDKPayment(amount, finalOrderId, customerId, customerEmail, customerMobile, config, true)
        } else {
            showInstallDialog(
                gateway = PaymentGateway.PAYTM,
                onInstall = { openInPlayStore(PaymentGateway.PAYTM) },
                onContinueWeb = {
                    startPaytmSDKPayment(amount, finalOrderId, customerId, customerEmail, customerMobile, config, false)
                },
                onCancel = { onPaymentResult(PaymentResult.Cancelled) }
            )
        }
    }

    private fun handlePhonePePayment(
        amount: Double,
        orderId: String?,
        customerId: String,
        customerMobile: String?,
        config: PaymentConfig
    ) {
        val finalOrderId = orderId ?: generateOrderId()
        onPaymentResult(PaymentResult.Loading)

        // Simulate PhonePe payment
        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            if (isAppInstalled(PaymentGateway.PHONEPE.packageName)) {
                onPaymentResult(PaymentResult.Success)
            } else {
                showInstallDialog(
                    gateway = PaymentGateway.PHONEPE,
                    onInstall = { openInPlayStore(PaymentGateway.PHONEPE) },
                    onContinueWeb = { onPaymentResult(PaymentResult.Success) },
                    onCancel = { onPaymentResult(PaymentResult.Cancelled) }
                )
            }
        }
    }

    private fun handleGooglePayPayment(
        amount: Double,
        orderId: String?,
        customerId: String,
        config: PaymentConfig
    ) {
        val finalOrderId = orderId ?: generateOrderId()
        onPaymentResult(PaymentResult.Loading)

        // Simulate Google Pay payment
        CoroutineScope(Dispatchers.Main).launch {
            delay(1500)
            if (isAppInstalled(PaymentGateway.GOOGLEPAY.packageName)) {
                onPaymentResult(PaymentResult.Success)
            } else {
                showInstallDialog(
                    gateway = PaymentGateway.GOOGLEPAY,
                    onInstall = { openInPlayStore(PaymentGateway.GOOGLEPAY) },
                    onContinueWeb = { onPaymentResult(PaymentResult.Success) },
                    onCancel = { onPaymentResult(PaymentResult.Cancelled) }
                )
            }
        }
    }

    private fun handleRazorpayPayment(
        amount: Double,
        orderId: String?,
        customerId: String,
        customerEmail: String?,
        customerMobile: String?,
        config: PaymentConfig
    ) {
        val finalOrderId = orderId ?: generateOrderId()
        onPaymentResult(PaymentResult.Loading)

        // Simulate Razorpay payment
        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            onPaymentResult(PaymentResult.Success)
        }
    }

    private fun handlePayPalPayment(
        amount: Double,
        orderId: String?,
        customerId: String,
        customerEmail: String?,
        config: PaymentConfig
    ) {
        val finalOrderId = orderId ?: generateOrderId()
        onPaymentResult(PaymentResult.Loading)

        // Simulate PayPal payment
        CoroutineScope(Dispatchers.Main).launch {
            delay(2500)
            if (isAppInstalled(PaymentGateway.PAYPAL.packageName)) {
                onPaymentResult(PaymentResult.Success)
            } else {
                showInstallDialog(
                    gateway = PaymentGateway.PAYPAL,
                    onInstall = { openInPlayStore(PaymentGateway.PAYPAL) },
                    onContinueWeb = { onPaymentResult(PaymentResult.Success) },
                    onCancel = { onPaymentResult(PaymentResult.Cancelled) }
                )
            }
        }
    }

    private fun handleCardPayment(
        amount: Double,
        orderId: String?,
        customerId: String,
        customerEmail: String?,
        config: PaymentConfig
    ) {
        val finalOrderId = orderId ?: generateOrderId()
        onPaymentResult(PaymentResult.Loading)

        // Simulate card payment processing
        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            // In real implementation, this would open card input form
            onPaymentResult(PaymentResult.Success)
        }
    }

    private fun handleCashOnDelivery(amount: Double, orderId: String?, customerId: String) {
        val finalOrderId = orderId ?: generateOrderId()
        // Simulate COD success (in real app, you'd update order status)
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000) // Simulate processing
            onPaymentResult(PaymentResult.Success)
        }
    }

    private fun handleCashAtStore(amount: Double, orderId: String?, customerId: String) {
        val finalOrderId = orderId ?: generateOrderId()
        // Simulate Cash at Store success
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            onPaymentResult(PaymentResult.Success)
        }
    }

    private fun handleCosmoraCard(amount: Double, orderId: String?, customerId: String) {
        val finalOrderId = orderId ?: generateOrderId()
        onPaymentResult(PaymentResult.Loading)

        // Check Cosmora card balance and process
        CoroutineScope(Dispatchers.Main).launch {
            delay(1500) // Simulate processing
            // In real app, check actual balance from your backend
            val cosmoraBalance = 1000.0 // This should come from your backend

            if (cosmoraBalance >= amount) {
                onPaymentResult(PaymentResult.Success)
            } else {
                onPaymentResult(PaymentResult.Failed("Insufficient balance in Cosmora Card. Balance: $${cosmoraBalance}, Required: $${amount}"))
            }
        }
    }
}