package com.example.multiplepages.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.multiplepages.model.CafeMenuItem
import com.example.multiplepages.manager.CartManager
import com.example.multiplepages.screens.BottomCartIndicator
import com.example.multiplepages.screens.getHotCoffeeItems
import com.example.multiplepages.screens.getColdCoffeeItems

// 1. DRINKS PAGE (Main Category)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrinksScreen(navController: NavController) {
    CategoryScreen(
        navController = navController,
        title = "Drinks",
        categoryId = "drinks",
        subcategories = listOf("Espresso", "Frappuccino®", "Blended Beverages", "Other Beverages"),
        filters = listOf("Hot", "Cold", "Milkshake", "Black", "Blended")
    )
}

// 2. FOOD PAGE
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodScreen(navController: NavController) {
    CategoryScreen(
        navController = navController,
        title = "Food",
        categoryId = "food",
        subcategories = listOf("Veg", "Paneer", "Cheese", "Grilled"),
        filters = listOf("Vegetarian", "Spicy", "Mild", "Cheesy")
    )
}

// 3. MERCHANDISE PAGE
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MerchandiseScreen(navController: NavController) {
    CategoryScreen(
        navController = navController,
        title = "Merchandise",
        categoryId = "merchandise",
        subcategories = listOf("Mugs", "Tumblers", "Coffee Beans", "Accessories"),
        filters = listOf("Gift", "Premium", "Limited", "Classic")
    )
}

// 4. COFFEE AT HOME PAGE
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoffeeAtHomeScreen(navController: NavController) {
    CategoryScreen(
        navController = navController,
        title = "Coffee At Home",
        categoryId = "coffee_at_home",
        subcategories = listOf("Whole Bean", "Ground", "Instant", "Pods"),
        filters = listOf("Dark Roast", "Medium Roast", "Light Roast", "Decaf")
    )
}

// MAIN CATEGORY SCREEN TEMPLATE
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    navController: NavController,
    title: String,
    categoryId: String,
    subcategories: List<String>,
    filters: List<String>
) {
    var selectedSubcategory by remember { mutableStateOf(subcategories.firstOrNull()) }
    var selectedFilter by remember { mutableStateOf(filters.firstOrNull()) }
    var orderType by remember { mutableStateOf("Dine In") }

    // Fetch menu items from Firebase
    val menuItems by produceState<List<CafeMenuItem>>(initialValue = emptyList(), categoryId, selectedSubcategory) {
        try {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("menu")
                .document(categoryId)
                .collection("items")
                .get()
                .await()

            value = snapshot.documents.mapNotNull { doc ->
                doc.toObject(CafeMenuItem::class.java)
            }.filter { item ->
                selectedSubcategory?.let { subcat ->
                    item.category.contains(subcat, ignoreCase = true) ||
                    item.name.contains(subcat, ignoreCase = true)
                } ?: true
            }
        } catch (e: Exception) {
            // Fallback to sample data if Firebase fails
            value = getSampleMenuItems(categoryId, selectedSubcategory)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header with back button and search
        TopAppBar(
            title = {
                Text(
                    text = "Mobile Order and Pay",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            },
            actions = {
                IconButton(onClick = { /* Search action */ }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF00704A)
            )
        )

        // Location and Time Info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location",
                tint = Color(0xFF00704A),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (menuItems.isEmpty()) "No Store Found!" else "Agra East Gate Rd",
                color = if (menuItems.isEmpty()) Color.Gray else Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "Time",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (menuItems.isEmpty()) "00mins" else "26mins",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }

        // Show "No Store Found" message if no items
        if (menuItems.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE4E1)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0xFFDC143C),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "No Store Found!",
                            color = Color(0xFFDC143C),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Please enter a different location to find the nearest Starbucks.",
                            color = Color(0xFFDC143C),
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFFDC143C)
                        )
                    }
                }
            }
        }

        // Order Type Selection (Dine In / Takeaway)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { orderType = "Dine In" },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (orderType == "Dine In") Color(0xFF00704A) else Color.Transparent
                ),
                border = if (orderType != "Dine In") BorderStroke(1.dp, Color(0xFF00704A)) else null,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "🍽️ Dine In", 
                    color = if (orderType == "Dine In") Color.White else Color(0xFF00704A)
                )
            }

            Button(
                onClick = { orderType = "Takeaway" },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (orderType == "Takeaway") Color(0xFF00704A) else Color.Transparent
                ),
                border = if (orderType != "Takeaway") BorderStroke(1.dp, Color(0xFF00704A)) else null,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "📦 Takeaway", 
                    color = if (orderType == "Takeaway") Color.White else Color(0xFF00704A)
                )
            }
        }

        // Main Category Tabs
        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            items(listOf("Drinks", "Food", "Merchandise", "Coffee At Home")) { category ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { 
                        when (category) {
                            "Drinks" -> navController.navigate("drinks")
                            "Food" -> navController.navigate("food")
                            "Merchandise" -> navController.navigate("merchandise")
                            "Coffee At Home" -> navController.navigate("coffee_at_home")
                        }
                    }
                ) {
                    Text(
                        text = category,
                        color = if (title == category) Color(0xFF00704A) else Color.Gray,
                        fontWeight = if (title == category) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 16.sp
                    )
                    if (title == category) {
                        Box(
                            modifier = Modifier
                                .height(3.dp)
                                .width(50.dp)
                                .background(Color(0xFF00704A))
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Subcategories
        if (subcategories.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(subcategories) { subcategory ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { selectedSubcategory = subcategory }
                    ) {
                        Text(
                            text = subcategory,
                            color = if (selectedSubcategory == subcategory)
                                Color(0xFF00704A) else Color.Gray,
                            fontWeight = if (selectedSubcategory == subcategory)
                                FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                        if (selectedSubcategory == subcategory) {
                            Box(
                                modifier = Modifier
                                    .height(2.dp)
                                    .width(40.dp)
                                    .background(Color(0xFF00704A))
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Filter Chips
        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filters) { filter ->
                FilterChip(
                    onClick = { selectedFilter = filter },
                    label = { Text(filter, fontSize = 12.sp) },
                    selected = selectedFilter == filter,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF00704A).copy(alpha = 0.2f),
                        selectedLabelColor = Color(0xFF00704A)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Category Description
        if (menuItems.isNotEmpty()) {
            Text(
                text = getCategoryDescription(categoryId),
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = getCategorySubtitle(categoryId),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        // Menu Items List
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(menuItems) { item ->
                    MenuItemCard(
                        item = item,
                        onAddClick = { CartManager.addItem(item) },
                        onRemoveClick = { CartManager.removeItem(item) },
                        quantity = CartManager.getItemQuantity(item),
                        onClick = {
                            navController.navigate("productDetail/${item.name}")
                        }
                    )
                }
                // Add bottom padding for cart indicator
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
            
            // Bottom Cart Indicator
            BottomCartIndicator(
                navController = navController,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun MenuItemCard(
    item: CafeMenuItem,
    onAddClick: () -> Unit,
    onRemoveClick: () -> Unit,
    quantity: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image
            AsyncImage(
                model = if (item.imageUrl.isNotEmpty()) item.imageUrl
                else getDefaultImageForItem(item.name),
                contentDescription = item.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            item.name.contains("Cortado", ignoreCase = true) -> Color(0xFFD2691E)
                            item.name.contains("Frappe", ignoreCase = true) -> Color(0xFF87CEEB)
                            item.category.contains("food", ignoreCase = true) -> Color(0xFF228B22)
                            else -> Color(0xFFD2691E)
                        }
                    ),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Product Info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Availability indicator
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                if (item.availability) Color(0xFF00704A) else Color.Red,
                                CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = item.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Text(
                    text = "${item.calories} kcal",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(
                    text = item.description,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp),
                    maxLines = 2
                )

                Text(
                    text = "₹${item.price}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Add/Remove Controls
            if (quantity > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            Color(0xFF00704A),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    IconButton(
                        onClick = onRemoveClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Remove",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    
                    Text(
                        text = quantity.toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    
                    IconButton(
                        onClick = onAddClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            } else {
                Button(
                    onClick = onAddClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00704A)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Add Item", color = Color.White)
                }
            }
        }
    }
}

// Helper Functions
fun getCategoryDescription(categoryId: String): String = when (categoryId) {
    "drinks" -> "DRINKS"
    "food" -> "FOOD"
    "merchandise" -> "MERCHANDISE"
    "coffee_at_home" -> "COFFEE AT HOME"
    else -> categoryId.uppercase()
}

fun getCategorySubtitle(categoryId: String): String = when (categoryId) {
    "drinks" -> "Our smooth signature coffee roasts with rich flavor and caramelly sweetness is at the very heart of everything we do."
    "food" -> "Freshly made food items with premium ingredients."
    "merchandise" -> "Take home your favorite Starbucks merchandise."
    "coffee_at_home" -> "Enjoy Starbucks coffee at home with our premium beans and blends."
    else -> "Delicious items from our menu."
}

fun getDefaultImageForItem(itemName: String): String {
    val baseUrl = "https://via.placeholder.com/80x80"
    return when {
        itemName.contains("Cortado", ignoreCase = true) -> "$baseUrl/D2691E/FFFFFF?text=☕"
        itemName.contains("Frappe", ignoreCase = true) -> "$baseUrl/CD853F/FFFFFF?text=🥤"
        itemName.contains("Coffee", ignoreCase = true) -> "$baseUrl/D2691E/FFFFFF?text=☕"
        itemName.contains("Latte", ignoreCase = true) -> "$baseUrl/CD853F/FFFFFF?text=🥛☕"
        else -> "$baseUrl/808080/FFFFFF?text=🍽️"
    }
}

fun getSampleMenuItems(categoryId: String, subcategory: String?): List<CafeMenuItem> = when (categoryId) {
    "hot_coffees" -> getHotCoffeeItems()
    "cold_coffees" -> getColdCoffeeItems()
    "drinks" -> when (subcategory) {
        "Espresso" -> listOf(
            CafeMenuItem("Date Cortado", "383.25", "168ml", "168", "drinks", "Double shot blonde espresso, paired with date flavoured sauce and steamed milk", "", "Espresso, date syrup, milk", true),
            CafeMenuItem("Churro Frappuccino", "430.50", "368ml", "368", "drinks", "Signature Starbucks Frappuccino with churro flavor", "", "Coffee, churro syrup, milk, whipped cream", true),
            CafeMenuItem("Barista Pride Latte", "430.50", "240ml", "240", "drinks", "Barista Pride Latte", "", "Espresso, milk, pride syrup", true)
        )
        "Frappuccino®" -> listOf(
            CafeMenuItem("Churro Frappuccino", "430.50", "368ml", "368", "drinks", "Signature Starbucks Frappuccino with churro flavor", "", "Coffee, churro syrup, milk, whipped cream", true),
            CafeMenuItem("Caramel Frappuccino", "380.00", "350ml", "320", "drinks", "Caramel blended with coffee and milk", "", "Coffee, caramel, milk, ice", true)
        )
        else -> listOf(
            CafeMenuItem("Date Cortado", "383.25", "168ml", "168", "drinks", "Double shot blonde espresso, paired with date flavoured sauce and steamed milk", "", "Espresso, date syrup, milk", true),
            CafeMenuItem("Churro Frappuccino", "430.50", "368ml", "368", "drinks", "Signature Starbucks Frappuccino with churro flavor", "", "Coffee, churro syrup, milk, whipped cream", true)
        )
    }
    "food" -> listOf(
        CafeMenuItem("Veg Sandwich", "250", "200g", "280", "food", "Fresh vegetable sandwich", "", "Bread, vegetables, mayo", true),
        CafeMenuItem("Paneer Tikka Wrap", "320", "250g", "350", "food", "Spiced paneer in a wrap", "", "Paneer, spices, tortilla", true)
    )
    else -> emptyList()
}