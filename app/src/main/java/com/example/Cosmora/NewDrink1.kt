@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.Cosmora

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.Cosmora.model.CafeMenuItem

// Sample menu items for display
private fun getSampleMenuItems(): List<CafeMenuItem> {
    return listOf(
        CafeMenuItem("Strawberry Funnel", "290.00", "350ml", "368", "cold_coffees", "Signature Frappuccino with strawberry flavour", "", "Frappuccino base, strawberry syrup, whipped cream", true),
        CafeMenuItem("Caramel Frappuccino", "250.00", "350ml", "380", "cold_coffees", "Blended coffee with caramel flavor", "", "Coffee, caramel syrup, milk, whipped cream", true),
        CafeMenuItem("Vanilla Latte", "220.00", "240ml", "200", "hot_coffees", "Espresso with vanilla syrup and steamed milk", "", "Espresso, vanilla syrup, steamed milk", true),
        CafeMenuItem("Mocha Delight", "280.00", "240ml", "260", "hot_coffees", "Rich chocolate and espresso combination", "", "Espresso, chocolate syrup, steamed milk", true)
    )
}

private fun getSpecialMenuItem(): CafeMenuItem {
    return CafeMenuItem("Caramel Frappuccino", "300.00", "350ml", "420", "cold_coffees", "Premium Caramel Frappuccino with extra whipped cream", "", "Coffee, caramel syrup, milk, extra whipped cream", true)
}

@Composable
fun CoffeeMainScreen(navController: NavController) {
    val menuItems = getSampleMenuItems()
    val specialMenuItem = getSpecialMenuItem()
    var selectedSpecialQuantity by remember { mutableStateOf(2) }

    // Get cart items count from CartManager
    val cartItemsCount = CartManager.getTotalItems()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header with wavy green background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF2E7D32),
                            Color(0xFF4CAF50)
                        )
                    ),
                    shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Profile Avatar
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.White, CircleShape)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color(0xFFFFE0B2), Color(0xFFFF8A65))
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color(0xFF6D4C41),
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                // Top Icons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    // Cart Icon with Badge
                    Box(
                        modifier = Modifier.clickable { navController.navigate("cart") }
                    ) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = "Cart",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                        if (cartItemsCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(Color.Red, CircleShape)
                                    .align(Alignment.TopEnd),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cartItemsCount.toString(),
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Search Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-20).dp)
                .padding(horizontal = 20.dp)
                .background(Color.White, RoundedCornerShape(25.dp))
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Search here...",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // New Menu Section
        Column(
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "New Menu",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "View All",
                    fontSize = 16.sp,
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable {
                        navController.navigate("menu")
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Horizontal scrolling drinks
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(end = 20.dp)
            ) {
                itemsIndexed(menuItems) { index, menuItem ->
                    MenuItemCard(
                        menuItem = menuItem,
                        onAddClick = {
                            CartManager.addItem(menuItem)
                            navController.navigate("cart")
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Special for you Section
        Column(
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Special for you",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "View All",
                    fontSize = 16.sp,
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable {
                        navController.navigate("menu")
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Special drink card
            SpecialMenuItemCard(
                menuItem = specialMenuItem,
                quantity = selectedSpecialQuantity,
                onQuantityChange = { selectedSpecialQuantity = it },
                onAddToOrderClick = {
                    repeat(selectedSpecialQuantity) {
                        CartManager.addItem(specialMenuItem)
                    }
                    navController.navigate("cart")
                }
            )
        }
    }
}

@Composable
fun MenuItemCard(
    menuItem: CafeMenuItem,
    onAddClick: () -> Unit
) {
    var isFavorite by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .width(180.dp)
            .height(240.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Favorite button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = { isFavorite = !isFavorite },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Drink Image (placeholder based on category)
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        brush = when (menuItem.category) {
                            "cold_coffees" -> when {
                                menuItem.name.contains("Strawberry", ignoreCase = true) ->
                                    Brush.verticalGradient(colors = listOf(Color(0xFFE91E63), Color(0xFFF8BBD9)))
                                menuItem.name.contains("Caramel", ignoreCase = true) ->
                                    Brush.verticalGradient(colors = listOf(Color(0xFFD2691E), Color(0xFFFFE4B5)))
                                else -> Brush.verticalGradient(colors = listOf(Color(0xFF4CAF50), Color(0xFF81C784)))
                            }
                            "hot_coffees" -> when {
                                menuItem.name.contains("Vanilla", ignoreCase = true) ->
                                    Brush.verticalGradient(colors = listOf(Color(0xFFFFF8DC), Color(0xFFFFE4E1)))
                                menuItem.name.contains("Mocha", ignoreCase = true) ->
                                    Brush.verticalGradient(colors = listOf(Color(0xFF8B4513), Color(0xFFDEB887)))
                                else -> Brush.verticalGradient(colors = listOf(Color(0xFF6B4423), Color(0xFFD2B48C)))
                            }
                            else -> Brush.verticalGradient(colors = listOf(Color(0xFF4CAF50), Color(0xFF81C784)))
                        },
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Cosmora logo
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color(0xFF2E7D32), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "C",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = menuItem.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                maxLines = 2
            )

            Text(
                text = "₹${menuItem.price}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Add button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.Black, CircleShape)
                    .clickable { onAddClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun SpecialMenuItemCard(
    menuItem: CafeMenuItem,
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    onAddToOrderClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2E7D32)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side content
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = menuItem.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "₹${menuItem.price}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Quantity selector
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.White, CircleShape)
                                .clickable {
                                    if (quantity > 1) onQuantityChange(quantity - 1)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Remove,
                                contentDescription = "Decrease",
                                tint = Color(0xFF2E7D32)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(40.dp)
                                .background(Color.White, RoundedCornerShape(20.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = quantity.toString(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.White, CircleShape)
                                .clickable { onQuantityChange(quantity + 1) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Increase",
                                tint = Color(0xFF2E7D32)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Add to Order button
                    Button(
                        onClick = onAddToOrderClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black
                        ),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text(
                            text = "Add To Order",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Right side - Large drink image
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF4CAF50), Color(0xFF81C784))
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "C",
                            color = Color(0xFF2E7D32),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Small decorative drinks
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFE91E63), Color(0xFFF8BBD9))
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .align(Alignment.BottomStart)
                    .offset(x = 10.dp, y = (-10).dp)
            )

            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(Color.White, CircleShape)
                    .align(Alignment.TopEnd)
                    .offset(x = (-40).dp, y = 20.dp)
            )
        }
    }
}


fun getImageResourceForItem(itemName: String, category: String): Any {

    return when {
        itemName.contains("Strawberry", ignoreCase = true) -> "strawberry_placeholder"
        itemName.contains("Caramel", ignoreCase = true) -> "caramel_placeholder"
        itemName.contains("Vanilla", ignoreCase = true) -> "vanilla_placeholder"
        else -> "default_placeholder"
    }
}

@Preview(showBackground = true)
@Composable
fun CoffeeMainScreenPreview() {
    MaterialTheme {
        // Note: Preview won't work with NavController - for actual testing use the real app
    }
}