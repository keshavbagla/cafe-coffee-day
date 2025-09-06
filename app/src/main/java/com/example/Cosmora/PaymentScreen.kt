package com.example.Cosmora.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PaymentScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) }
    var transactions by remember { mutableStateOf<List<Transaction>>(emptyList()) }

    // Removed Scaffold with bottomBar - now using simple Column layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2E7D32),
                        Color(0xFFF0F8F0),
                        Color(0xFFF0F8F0)
                    ),
                    startY = 0f,
                    endY = 600f
                )
            )
    ) {
        CosmoraPayHeader()

        CosmoraPayCard(
            balance = "₹0.0",
            hasTransactions = transactions.isNotEmpty(),
            transactions = transactions,
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            onLoadCard = { /* Handle load card */ },
            onAddTransaction = { transaction ->
                transactions = transactions + transaction
            }
        )
    }
}

@Composable
fun CosmoraPayCard(
    balance: String = "₹0.0",
    hasTransactions: Boolean = false,
    transactions: List<Transaction> = emptyList(),
    selectedTab: Int = 0,
    onTabSelected: (Int) -> Unit = {},
    onLoadCard: () -> Unit = {},
    onAddTransaction: (Transaction) -> Unit = {}
) {
    val currentDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            // Card Header with gradient background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF4CAF50),
                                Color(0xFF81C784),
                                Color(0xFF2E7D32)
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(1000f, 1000f)
                        )
                    )
            ) {
                // Decorative swirl pattern
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val path = Path().apply {
                        moveTo(size.width * 0.1f, size.height * 0.4f)

                        // First swirl
                        quadraticBezierTo(
                            size.width * 0.3f, size.height * 0.1f,
                            size.width * 0.6f, size.height * 0.3f
                        )
                        quadraticBezierTo(
                            size.width * 0.8f, size.height * 0.5f,
                            size.width * 0.9f, size.height * 0.2f
                        )

                        // Create flowing curves
                        lineTo(size.width * 1.1f, size.height * 0.1f)
                        lineTo(size.width * 1.1f, size.height * 0.8f)

                        quadraticBezierTo(
                            size.width * 0.7f, size.height * 0.9f,
                            size.width * 0.3f, size.height * 0.7f
                        )
                        quadraticBezierTo(
                            size.width * 0.1f, size.height * 0.6f,
                            size.width * 0.1f, size.height * 0.4f
                        )
                        close()
                    }

                    drawPath(
                        path = path,
                        color = Color.White.copy(alpha = 0.15f)
                    )

                    // Additional decorative curves
                    val path2 = Path().apply {
                        moveTo(size.width * 0.7f, size.height * 0.8f)
                        quadraticBezierTo(
                            size.width * 0.9f, size.height * 0.6f,
                            size.width * 1.2f, size.height * 0.9f
                        )
                        lineTo(size.width * 1.2f, size.height * 1.1f)
                        lineTo(size.width * 0.7f, size.height * 1.1f)
                        close()
                    }

                    drawPath(
                        path = path2,
                        color = Color.White.copy(alpha = 0.1f)
                    )
                }

                // Coffee cup icons
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    repeat(2) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    Color.White.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "☕",
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                    }
                }

                // Cosmora logo
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(20.dp)
                        .size(48.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                        .border(
                            2.dp,
                            Color.White.copy(alpha = 0.3f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "C",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Card Details Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Aroma",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black
                            )
                            Text(
                                text = "* 2108",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        }

                        Text(
                            text = balance,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        Text(
                            text = "Updated at $currentDate",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Button(
                        onClick = onLoadCard,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32)
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text(
                            text = "+ ",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Load Card",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Refresh and Settings icons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /* Refresh */ }) {
                        Text(
                            text = "↻",
                            fontSize = 20.sp,
                            color = Color.Gray
                        )
                    }
                    IconButton(onClick = { /* Settings */ }) {
                        Text(
                            text = "⚙",
                            fontSize = 18.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Tab Navigation
            PaymentTabs(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected
            )

            // Content based on selected tab
            when (selectedTab) {
                0 -> PayAtStoreContent(onAddTransaction = onAddTransaction)
                1 -> PastTransactionsContent(
                    transactions = transactions.filter { it.type == TransactionType.ONLINE },
                    hasTransactions = transactions.any { it.type == TransactionType.ONLINE }
                )
            }
        }
    }
}

@Composable
fun PaymentTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        TabButton(
            text = "Pay at Store",
            isSelected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(16.dp))
        TabButton(
            text = "Past Transactions",
            isSelected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF2E7D32) else Color.Transparent,
            contentColor = if (isSelected) Color.White else Color(0xFF2E7D32)
        ),
        border = if (!isSelected) BorderStroke(1.dp, Color(0xFF2E7D32)) else null,
        shape = RoundedCornerShape(24.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun PayAtStoreContent(
    onAddTransaction: (Transaction) -> Unit
) {
    // Only shows cash transaction functionality
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // QR Code placeholder or cash payment info
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(
                    Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "💳",
                    fontSize = 48.sp
                )
                Text(
                    text = "Show this to cashier",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "for cash transactions",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Demo: Add cash transaction button
        Button(
            onClick = {
                onAddTransaction(
                    Transaction(
                        id = System.currentTimeMillis().toString(),
                        amount = "₹150.0",
                        type = TransactionType.CASH,
                        date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()),
                        orderSummary = "Coffee + Sandwich"
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2E7D32)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Simulate Cash Payment",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun PastTransactionsContent(
    transactions: List<Transaction>,
    hasTransactions: Boolean
) {
    if (!hasTransactions || transactions.isEmpty()) {
        // Error/Empty state
        NoTransactionsView()
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(transactions) { transaction ->
                TransactionItem(transaction = transaction)
            }
        }
    }
}

@Composable
fun NoTransactionsView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(120.dp),
            contentAlignment = Alignment.Center
        ) {
            // Main circle background
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        color = Color(0xFFE8F5E8),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Document with X
                Canvas(
                    modifier = Modifier.size(60.dp)
                ) {
                    // Document background
                    drawRoundRect(
                        color = Color.White,
                        size = Size(size.width * 0.8f, size.height),
                        cornerRadius = CornerRadius(8.dp.toPx())
                    )

                    // Document lines
                    repeat(3) { index ->
                        drawLine(
                            color = Color(0xFF2E7D32),
                            start = Offset(size.width * 0.15f, size.height * (0.2f + index * 0.15f)),
                            end = Offset(size.width * 0.65f, size.height * (0.2f + index * 0.15f)),
                            strokeWidth = 2.dp.toPx()
                        )
                    }

                    // X mark
                    val xSize = size.width * 0.3f
                    val centerX = size.width * 0.5f
                    val centerY = size.height * 0.65f

                    drawLine(
                        color = Color(0xFF757575),
                        start = Offset(centerX - xSize/2, centerY - xSize/2),
                        end = Offset(centerX + xSize/2, centerY + xSize/2),
                        strokeWidth = 3.dp.toPx()
                    )
                    drawLine(
                        color = Color(0xFF757575),
                        start = Offset(centerX + xSize/2, centerY - xSize/2),
                        end = Offset(centerX - xSize/2, centerY + xSize/2),
                        strokeWidth = 3.dp.toPx()
                    )
                }
            }

            // Warning triangle overlay
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .offset(x = 30.dp, y = (-30).dp)
                    .background(
                        color = Color(0xFFFF7043),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "!",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Coffee cup icon at bottom
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .offset(x = 0.dp, y = 44.dp)
                    .background(
                        color = Color(0xFF2E7D32),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "☕",
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }

        Text(
            text = "No transactions yet",
            fontSize = 16.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Transaction icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = if (transaction.type == TransactionType.ONLINE) Color(0xFF2E7D32) else Color(0xFF4CAF50),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (transaction.type == TransactionType.ONLINE) "💳" else "💵",
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Transaction details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = transaction.amount,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    Text(
                        text = if (transaction.type == TransactionType.ONLINE) "Online" else "Cash",
                        fontSize = 12.sp,
                        color = Color.White,
                        modifier = Modifier
                            .background(
                                color = if (transaction.type == TransactionType.ONLINE) Color(0xFF2E7D32) else Color(0xFF4CAF50),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = transaction.date,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )

                if (transaction.orderSummary.isNotEmpty()) {
                    Text(
                        text = transaction.orderSummary,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 2.dp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun CosmoraPayHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Cosmora Pay",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    Color.White.copy(alpha = 0.2f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

data class Transaction(
    val id: String,
    val amount: String,
    val type: TransactionType,
    val date: String,
    val orderSummary: String
)

enum class TransactionType {
    ONLINE, CASH
}
