package com.example.Cosmora

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// Data Models
data class Order(
    val id: Int,
    val storeName: String,
    val items: List<String>,
    val status: OrderStatus,
    val time: String,
    val total: String,
    val orderNumber: String,
    val isFavorite: Boolean
)

enum class OrderStatus {
    PREPARING,
    READY_FOR_PICKUP,
    COMPLETED
}

enum class TabType {
    PENDING,
    ALL,
    FAVOURITES
}

// Color Scheme
object StarbucksColors {
    val Primary = Color(0xFF00704A)
    val PrimaryDark = Color(0xFF005A3A)
    val Background = Color(0xFFF5F5F5)
    val Surface = Color.White
    val OnSurface = Color(0xFF1C1C1E)
    val Secondary = Color(0xFF6B6B6B)
    val Success = Color(0xFF34C759)
    val Warning = Color(0xFFFF9500)
    val Error = Color(0xFFFF3B30)
}

@Composable
fun OrdersScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf(TabType.ALL) }
    var hasOrders by remember { mutableStateOf(false) }

    val sampleOrders = remember {
        listOf(
            Order(
                id = 1,
                storeName = "Starbucks - Connaught Place",
                items = listOf("Grande Caramel Macchiato", "Chocolate Croissant"),
                status = OrderStatus.READY_FOR_PICKUP,
                time = "2 mins ago",
                total = "₹485",
                orderNumber = "#12345",
                isFavorite = true
            ),
            Order(
                id = 2,
                storeName = "Starbucks - Select City Walk",
                items = listOf("Venti Cold Brew", "Blueberry Muffin"),
                status = OrderStatus.PREPARING,
                time = "5 mins ago",
                total = "₹420",
                orderNumber = "#12344",
                isFavorite = false
            ),
            Order(
                id = 3,
                storeName = "Starbucks - DLF Mall",
                items = listOf("Tall Cappuccino", "Spinach Wrap"),
                status = OrderStatus.COMPLETED,
                time = "1 day ago",
                total = "₹365",
                orderNumber = "#12340",
                isFavorite = true
            )
        )
    }

    val filteredOrders = remember(selectedTab, sampleOrders) {
        when (selectedTab) {
            TabType.PENDING -> sampleOrders.filter {
                it.status == OrderStatus.PREPARING || it.status == OrderStatus.READY_FOR_PICKUP
            }
            TabType.FAVOURITES -> sampleOrders.filter { it.isFavorite }
            TabType.ALL -> sampleOrders
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StarbucksColors.Background)
    ) {
        // Header
        HeaderSection(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )

        // Content
        if (!hasOrders || filteredOrders.isEmpty()) {
            EmptyOrdersState(
                onOrderNowClick = { hasOrders = true }
            )
        } else {
            OrdersList(
                orders = filteredOrders,
                onClearOrders = { hasOrders = false }
            )
        }
    }
}

@Composable
fun HeaderSection(
    selectedTab: TabType,
    onTabSelected: (TabType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(StarbucksColors.Primary)
            .padding(16.dp)
    ) {
        // Title Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Recent Orders",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tab Navigation
        TabNavigation(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected
        )
    }
}

@Composable
fun TabNavigation(
    selectedTab: TabType,
    onTabSelected: (TabType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = StarbucksColors.PrimaryDark,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(4.dp)
    ) {
        TabType.values().forEach { tab ->
            val isSelected = selectedTab == tab
            val tabName = when (tab) {
                TabType.PENDING -> "Pending"
                TabType.ALL -> "All"
                TabType.FAVOURITES -> "Favourites"
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = if (isSelected) Color.White else Color.Transparent,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { onTabSelected(tab) }
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = tabName,
                    color = if (isSelected) StarbucksColors.Primary else Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun EmptyOrdersState(onOrderNowClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        // Illustration
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(120.dp)
        ) {
            // Main circle background
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        color = Color(0xFFE8F5E8),
                        shape = CircleShape
                    )
            )

            // Document icon
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .width(if (index == 1) 24.dp else 32.dp)
                                .height(3.dp)
                                .background(
                                    color = StarbucksColors.Primary,
                                    shape = RoundedCornerShape(1.5.dp)
                                )
                        )
                        if (index < 2) Spacer(modifier = Modifier.height(4.dp))
                    }
                }

                // X marks
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier
                        .offset(x = 20.dp, y = (-10).dp)
                        .size(16.dp)
                )
            }

            // Warning icon
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = StarbucksColors.Error,
                        shape = CircleShape
                    )
                    .align(Alignment.TopEnd)
                    .offset(x = (-8).dp, y = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "No orders found!",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = StarbucksColors.OnSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Start your Starbucks journey today",
            fontSize = 16.sp,
            color = StarbucksColors.Secondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onOrderNowClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = StarbucksColors.Primary
            ),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = "Order now",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun OrdersList(
    orders: List<Order>,
    onClearOrders: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(orders) { order ->
            OrderCard(order = order)
        }

        item {
            TextButton(
                onClick = onClearOrders,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Clear all orders (Demo)",
                    color = StarbucksColors.Primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun OrderCard(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = StarbucksColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.storeName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StarbucksColors.OnSurface
                    )
                    Text(
                        text = "${order.orderNumber} • ${order.time}",
                        fontSize = 12.sp,
                        color = StarbucksColors.Secondary
                    )
                }

                if (order.isFavorite) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Favorite",
                        tint = StarbucksColors.Error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Items
            order.items.forEach { item ->
                Text(
                    text = "• $item",
                    fontSize = 14.sp,
                    color = StarbucksColors.OnSurface,
                    modifier = Modifier.padding(vertical = 1.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Status and Total Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OrderStatusIcon(status = order.status)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = getStatusText(order.status),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = getStatusColor(order.status)
                    )
                }

                Text(
                    text = order.total,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StarbucksColors.OnSurface
                )
            }

            // Action Buttons
            when (order.status) {
                OrderStatus.READY_FOR_PICKUP -> {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StarbucksColors.Primary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("View Details")
                    }
                }
                OrderStatus.COMPLETED -> {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = StarbucksColors.Primary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Reorder")
                        }

                        OutlinedButton(
                            onClick = { },
                            border = BorderStroke(1.dp, StarbucksColors.Primary),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = StarbucksColors.Primary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rate",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                else -> { /* No additional buttons for PREPARING */ }
            }
        }
    }
}

@Composable
fun OrderStatusIcon(status: OrderStatus) {
    val (icon, color) = when (status) {
        OrderStatus.READY_FOR_PICKUP -> Icons.Default.CheckCircle to StarbucksColors.Success
        OrderStatus.PREPARING -> Icons.Default.AccessTime to StarbucksColors.Warning
        OrderStatus.COMPLETED -> Icons.Default.CheckCircle to StarbucksColors.Secondary
    }

    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = color,
        modifier = Modifier.size(20.dp)
    )
}

fun getStatusText(status: OrderStatus): String {
    return when (status) {
        OrderStatus.READY_FOR_PICKUP -> "Ready for Pickup"
        OrderStatus.PREPARING -> "Preparing"
        OrderStatus.COMPLETED -> "Completed"
    }
}

fun getStatusColor(status: OrderStatus): Color {
    return when (status) {
        OrderStatus.READY_FOR_PICKUP -> StarbucksColors.Success
        OrderStatus.PREPARING -> StarbucksColors.Warning
        OrderStatus.COMPLETED -> StarbucksColors.Secondary
    }
}