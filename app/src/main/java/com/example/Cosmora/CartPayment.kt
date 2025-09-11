package com.example.Cosmora

import android.app.Activity
import android.util.Log
import com.example.Cosmora.payment.UnifiedPaymentManager
import com.example.Cosmora.payment.PaymentGatewayConfig
import com.example.Cosmora.payment.PaymentResult  // Import from payment package
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// Remove the duplicate PaymentResult class - using the one from payment package

// Sample saved cards
fun sampleSavedCards() = listOf(
    SavedCard("card1", "ICICI", "Johny Jacob", "1111", CardType.VISA, R.drawable.visa_icon),
    SavedCard("card2", "HDFC", "Carol Catherine", "2222", CardType.MASTERCARD, R.drawable.mastercard_icon),
    SavedCard("card3", "SBI", "Alice Smith", "3333", CardType.RUPAY, R.drawable.rupay_icon)
)

enum class CardType { VISA, MASTERCARD, RUPAY }

data class SavedCard(
    val id: String,
    val bankName: String,
    val holderName: String,
    val lastFourDigits: String,
    val type: CardType,
    val iconRes: Int
)

data class PaymentMethod(
    val id: String,
    val name: String,
    val iconRes: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentTypeScreen(
    navController: NavController,
    savedCards: List<SavedCard> = sampleSavedCards()
) {
    val cartItems = CartManager.cartItems.values.toList()
    val itemTotal = CartManager.getTotalAmount()
    val deliveryFee = 80.0
    val totalItems = CartManager.getTotalItems()
    val total = itemTotal + deliveryFee

    var selectedPaymentId by remember { mutableStateOf("cosmora_card") }

    if (cartItems.isEmpty()) {
        LaunchedEffect(Unit) { navController.popBackStack() }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Payment Type",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            )
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cosmora Card
            item {
                PaymentSection(
                    title = "Cosmora Card",
                    subtitle = "Use your Cosmora balance for faster payments"
                ) {
                    PaymentOptionItem(
                        id = "cosmora_card",
                        title = "₹1000.00",
                        subtitle = "Pay using Cosmora Card balance",
                        iconRes = R.drawable.ic_payment_modes,
                        isSelected = selectedPaymentId == "cosmora_card",
                        onSelected = { selectedPaymentId = it }
                    )
                }
            }

            // Saved Payment Methods
            if (savedCards.isNotEmpty()) {
                item {
                    PaymentSection(title = "Saved Payment Methods") {
                        savedCards.forEach { card ->
                            PaymentOptionItem(
                                id = card.id,
                                title = "${card.bankName} Credit Card",
                                subtitle = "${card.holderName}\n****${card.lastFourDigits}",
                                iconRes = card.iconRes,
                                isSelected = selectedPaymentId == card.id,
                                onSelected = { selectedPaymentId = it }
                            )
                        }
                    }
                }
            }

            // Wallet Section
            item {
                PaymentSection(
                    title = "Wallet",
                    subtitle = "Use e-wallet for faster payments"
                ) {
                    val walletOptions = listOf(
                        PaymentMethod("phonepe", "PhonePe", R.drawable.phonepe_icon),
                        PaymentMethod("paytm", "Paytm", R.drawable.paytm_icon),
                        PaymentMethod("googlepay", "Google Pay", R.drawable.googlepay_icon)
                    )
                    walletOptions.forEach { method ->
                        PaymentOptionItem(
                            id = method.id,
                            title = method.name,
                            iconRes = method.iconRes,
                            isSelected = selectedPaymentId == method.id,
                            onSelected = { selectedPaymentId = it }
                        )
                    }
                }
            }

            // Cards & Online
            item {
                PaymentSection(title = "Cards & Online") {
                    val cardOptions = listOf(
                        PaymentMethod("card", "Credit/Debit Card", R.drawable.ic_payment_modes),
                        PaymentMethod("razorpay", "Razorpay", R.drawable.razorpay_icon),
                        PaymentMethod("paypal", "PayPal", R.drawable.paypal_icon)
                    )
                    cardOptions.forEach { method ->
                        PaymentOptionItem(
                            id = method.id,
                            title = method.name,
                            iconRes = method.iconRes,
                            isSelected = selectedPaymentId == method.id,
                            onSelected = { selectedPaymentId = it }
                        )
                    }
                }
            }

            // Other
            item {
                PaymentSection(title = "Other") {
                    val otherOptions = listOf(
                        PaymentMethod("cash_store", "Cash (At Store)", R.drawable.cash),
                        PaymentMethod("cash_delivery", "Cash on Delivery", R.drawable.cash)
                    )
                    otherOptions.forEach { method ->
                        PaymentOptionItem(
                            id = method.id,
                            title = method.name,
                            iconRes = method.iconRes,
                            isSelected = selectedPaymentId == method.id,
                            onSelected = { selectedPaymentId = it }
                        )
                    }
                }
            }

            // Order Summary Card
            item {
                PaymentOrderSummaryCard(
                    itemTotal = itemTotal,
                    deliveryFee = deliveryFee,
                    total = total
                )
            }
        }

        PaymentBottomButton(
            amount = total,
            itemCount = totalItems,
            selectedPaymentId = selectedPaymentId,
            onPayNow = {
                navController.navigate("payment_success/$selectedPaymentId/$total")
            }
        )
    }
}

@Composable
fun PaymentSection(
    title: String,
    subtitle: String? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            subtitle?.let {
                Text(
                    text = it,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun PaymentOptionItem(
    id: String,
    title: String,
    subtitle: String? = null,
    iconRes: Int,
    isSelected: Boolean,
    onSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelected(id) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = { onSelected(id) },
            colors = RadioButtonDefaults.colors(
                selectedColor = Color(0xFF00BFA5),
                unselectedColor = Color.Gray
            )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = title,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            subtitle?.let {
                Text(
                    text = it,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
fun PaymentOrderSummaryCard(
    itemTotal: Double,
    deliveryFee: Double,
    total: Double
) {
    val amountColor = Color(0xFF212121)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Amount",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Item Total",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "₹${String.format("%.2f", itemTotal)}",
                    fontSize = 14.sp,
                    color = amountColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Delivery Fee",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "₹${String.format("%.2f", deliveryFee)}",
                    fontSize = 14.sp,
                    color = amountColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color.Gray.copy(alpha = 0.3f)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "₹${String.format("%.2f", total)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = amountColor
                )
            }
        }
    }
}

@Composable
fun PaymentBottomButton(
    amount: Double,
    itemCount: Int,
    selectedPaymentId: String,
    onPayNow: () -> Unit
) {
    val context = LocalContext.current
    var paymentResult: PaymentResult? by remember { mutableStateOf(null) }

    // Initialize unified payment manager
    val paymentManager = remember {
        try {
            UnifiedPaymentManager(
                activity = context as Activity,
                onPaymentResult = { result ->
                    paymentResult = result
                }
            ).apply {
                // Setup all payment gateways
                PaymentGatewayConfig.setupPaymentGateways(this)
            }
        } catch (e: Exception) {
            Log.e("Payment", "Failed to initialize payment manager", e)
            null
        }
    }

    // Handle payment
    val handlePayment = {
        if (paymentManager != null) {
            val gateway = PaymentGatewayConfig.getGatewayById(selectedPaymentId)

            if (gateway != null) {
                paymentResult = PaymentResult.Loading
                try {
                    paymentManager.initiatePayment(
                        gateway = gateway,
                        amount = amount,
                        orderId = "ORDER_${System.currentTimeMillis()}", // Generate order ID
                        customerId = "CUSTOMER_${System.currentTimeMillis()}", // Use actual customer ID
                        customerEmail = "customer@example.com", // Optional: actual email
                        customerMobile = "9876543210" // Optional: actual mobile
                    )
                } catch (e: Exception) {
                    Log.e("Payment", "Failed to initiate payment", e)
                    paymentResult = PaymentResult.Failed("Failed to initiate payment: ${e.message}")
                }
            } else {
                // Handle unknown payment method - proceed with navigation for cash payments
                when (selectedPaymentId) {
                    "cash_store", "cash_delivery", "cosmora_card" -> {
                        onPayNow() // Navigate to success screen
                    }
                    else -> {
                        Log.e("Payment", "Unknown payment method: $selectedPaymentId")
                        paymentResult = PaymentResult.Failed("Unsupported payment method")
                    }
                }
            }
        } else {
            // Fallback for when payment manager is not available
            when (selectedPaymentId) {
                "cash_store", "cash_delivery", "cosmora_card" -> {
                    onPayNow() // Navigate to success screen for non-gateway payments
                }
                else -> {
                    Log.e("Payment", "Payment manager not available")
                    paymentResult = PaymentResult.Failed("Payment system unavailable")
                }
            }
        }
    }

    // Handle payment result
    LaunchedEffect(paymentResult) {
        when (val result = paymentResult) {
            is PaymentResult.Success -> {
                // Payment successful - navigate to success screen
                onPayNow()
                paymentResult = null // Reset state
            }
            is PaymentResult.Failed -> {
                // Show error message to user
                Log.e("Payment", "Payment failed: ${result.error}")
                // TODO: Show error dialog or snackbar
            }
            is PaymentResult.Cancelled -> {
                // Payment cancelled by user
                Log.d("Payment", "Payment cancelled by user")
                paymentResult = null // Reset state
            }
            is PaymentResult.Loading -> {
                // Loading state is handled in UI
                Log.d("Payment", "Processing payment...")
            }
            null -> {
                // Initial state
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF00704A),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "₹${String.format("%.2f", amount)} ($itemCount Items)",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Button(
                onClick = handlePayment,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00704A)
                ),
                shape = RoundedCornerShape(8.dp),
                enabled = paymentResult !is PaymentResult.Loading
            ) {
                if (paymentResult is PaymentResult.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Pay Now",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFFFFFFF)
                    )
                }
            }
        }
    }
}

// Helper function to get payment method name
fun getPaymentMethodName(id: String): String {
    return when (id) {
        "cosmora_card" -> "Cosmora Card"
        "phonepe" -> "PhonePe"
        "paytm" -> "Paytm"
        "googlepay" -> "Google Pay"
        "card" -> "Credit/Debit Card"
        "razorpay" -> "Razorpay"
        "paypal" -> "PayPal"
        "cash_store" -> "Cash (At Store)"
        "cash_delivery" -> "Cash on Delivery"
        else -> "Unknown Payment Method"
    }
}