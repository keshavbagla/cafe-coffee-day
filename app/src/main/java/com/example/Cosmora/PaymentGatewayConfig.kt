package com.example.Cosmora.payment



object PaymentGatewayConfig {

    fun setupPaymentGateways(paymentManager: UnifiedPaymentManager) {

        // Paytm Configuration
        paymentManager.addPaymentConfig(
            PaymentConfig(
                gateway = PaymentGateway.PAYTM,
                merchantId = "YOUR_PAYTM_MID",
                merchantKey = "YOUR_PAYTM_KEY",
                environment = PaymentEnvironment.STAGING,
                additionalParams = mapOf(
                    "website" to "WEBSTAGING",
                    "industryType" to "Retail",
                    "callbackUrl" to "https://securegw-stage.paytm.in/theia/paytmCallback"
                )
            )
        )

        // PhonePe Configuration
        paymentManager.addPaymentConfig(
            PaymentConfig(
                gateway = PaymentGateway.PHONEPE,
                merchantId = "YOUR_PHONEPE_MERCHANT_ID",
                merchantKey = "YOUR_PHONEPE_SALT_KEY",
                environment = PaymentEnvironment.STAGING,
                additionalParams = mapOf(
                    "saltIndex" to "1",
                    "redirectUrl" to "https://your-app.com/phonepe/callback"
                )
            )
        )

        // Google Pay Configuration
        paymentManager.addPaymentConfig(
            PaymentConfig(
                gateway = PaymentGateway.GOOGLEPAY,
                merchantId = "YOUR_GOOGLE_MERCHANT_ID",
                environment = PaymentEnvironment.STAGING,
                additionalParams = mapOf(
                    "gatewayMerchantId" to "YOUR_GATEWAY_MERCHANT_ID",
                    "merchantName" to "Cosmora"
                )
            )
        )

        // Razorpay Configuration
        paymentManager.addPaymentConfig(
            PaymentConfig(
                gateway = PaymentGateway.RAZORPAY,
                merchantId = "YOUR_RAZORPAY_KEY_ID",
                merchantKey = "YOUR_RAZORPAY_SECRET",
                environment = PaymentEnvironment.STAGING,
                additionalParams = mapOf(
                    "theme_color" to "#00704A",
                    "company_name" to "Cosmora"
                )
            )
        )

        // PayPal Configuration
        paymentManager.addPaymentConfig(
            PaymentConfig(
                gateway = PaymentGateway.PAYPAL,
                merchantId = "YOUR_PAYPAL_CLIENT_ID",
                merchantKey = "YOUR_PAYPAL_CLIENT_SECRET",
                environment = PaymentEnvironment.STAGING,
                additionalParams = mapOf(
                    "currency" to "USD",
                    "intent" to "sale"
                )
            )
        )

        // Card Payment (Generic) - You can use Stripe, Square, etc.
        paymentManager.addPaymentConfig(
            PaymentConfig(
                gateway = PaymentGateway.CARD_PAYMENT,
                merchantId = "YOUR_STRIPE_PUBLISHABLE_KEY",
                merchantKey = "YOUR_STRIPE_SECRET_KEY",
                environment = PaymentEnvironment.STAGING
            )
        )

        // Cash payments don't need configuration
        paymentManager.addPaymentConfig(
            PaymentConfig(gateway = PaymentGateway.CASH_DELIVERY)
        )

        paymentManager.addPaymentConfig(
            PaymentConfig(gateway = PaymentGateway.CASH_STORE)
        )

        paymentManager.addPaymentConfig(
            PaymentConfig(gateway = PaymentGateway.COSMORA_CARD)
        )
    }

    fun getGatewayById(id: String): PaymentGateway? {
        return PaymentGateway.values().find { it.id == id }
    }
}

