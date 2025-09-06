package com.example.Cosmora

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.Cosmora.model.CafeMenuItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// Data class for cart items
data class CartItem(
    val menuItem: CafeMenuItem,
    var quantity: Int
)

// Data class for location
data class LocationInfo(
    val name: String,
    val address: String,
    val estimatedTime: String
)

// Order type enum
enum class OrderType {
    DINE_IN, TAKEAWAY
}

// Cart state management
object CartManager {
    private val _cartItems = mutableStateMapOf<String, CartItem>()
    val cartItems: Map<String, CartItem> = _cartItems

    fun addItem(item: CafeMenuItem) {
        val existingItem = _cartItems[item.name]
        if (existingItem != null) {
            existingItem.quantity++
        } else {
            _cartItems[item.name] = CartItem(item, 1)
        }
    }

    fun removeItem(itemName: String) {
        val existingItem = _cartItems[itemName]
        if (existingItem != null) {
            if (existingItem.quantity > 1) {
                existingItem.quantity--
            } else {
                _cartItems.remove(itemName)
            }
        }
    }

    fun getItemCount(itemName: String): Int {
        return _cartItems[itemName]?.quantity ?: 0
    }

    fun getTotalAmount(): Double {
        return _cartItems.values.sumOf {
            it.menuItem.price.toDouble() * it.quantity
        }
    }

    fun getTotalItems(): Int {
        return _cartItems.values.sumOf { it.quantity }
    }

    fun getLastAddedItem(): String {
        return _cartItems.values.lastOrNull()?.menuItem?.name ?: "Item"
    }
}

// Location state management
object LocationManager {
    private var _selectedLocation = mutableStateOf<LocationInfo?>(null)
    val selectedLocation: State<LocationInfo?> = _selectedLocation

    fun setLocation(location: LocationInfo) {
        _selectedLocation.value = location
    }

    fun clearLocation() {
        _selectedLocation.value = null
    }
}

// 1. HOT COFFEES SCREEN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotCoffeesScreen(navController: NavController) {
    val subcategories = listOf("Espresso", "Americano", "Latte", "Cappuccino", "Macchiato", "Mocha")
    val filters = listOf("Hot", "Strong", "Milky", "Sweet", "Classic")

    CategoryScreenTemplate(
        navController = navController,
        title = "Hot Coffees",
        categoryId = "hot_coffees",
        subcategories = subcategories,
        filters = filters,
        categoryDescription = "ESPRESSO",
        categorySubtitle = "Our smooth signature Espresso Roast with rich flavor and caramelly sweetness is at the very heart of everything we do."
    )
}

// 2. COLD COFFEES SCREEN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColdCoffeesScreen(navController: NavController) {
    val subcategories = listOf("Iced Coffee", "Cold Brew", "Frappuccino", "Iced Latte", "Iced Americano")
    val filters = listOf("Cold", "Iced", "Blended", "Sweet", "Refreshing")

    CategoryScreenTemplate(
        navController = navController,
        title = "Cold Coffees",
        categoryId = "cold_coffees",
        subcategories = subcategories,
        filters = filters,
        categoryDescription = "COLD BEVERAGES",
        categorySubtitle = "Refreshing cold coffee beverages perfect for any time of day."
    )
}

// 3. SANDWICHES SCREEN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SandwichesScreen(navController: NavController) {
    val subcategories = listOf("Veg Sandwich", "Paneer", "Cheese", "Grilled", "Italian", "Club")
    val filters = listOf("Vegetarian", "Spicy", "Mild", "Cheesy", "Grilled")

    CategoryScreenTemplate(
        navController = navController,
        title = "Sandwiches",
        categoryId = "sandwiches",
        subcategories = subcategories,
        filters = filters,
        categoryDescription = "SANDWICHES",
        categorySubtitle = "Freshly made sandwiches with premium ingredients and authentic flavors."
    )
}

// 4. BURGERS SCREEN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BurgersScreen(navController: NavController) {
    val subcategories = listOf("Aloo Tikki", "Paneer Burger", "Veggie", "Classic", "Mexican", "Supreme")
    val filters = listOf("Spicy", "Cheesy", "Crispy", "Supreme", "Classic")

    CategoryScreenTemplate(
        navController = navController,
        title = "Burgers",
        categoryId = "burgers",
        subcategories = subcategories,
        filters = filters,
        categoryDescription = "BURGERS",
        categorySubtitle = "Delicious vegetarian burgers with flavorful patties and fresh toppings."
    )
}

// 5. DESSERTS SCREEN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DessertsScreen(navController: NavController) {
    val subcategories = listOf("Brownie", "Cake", "Cheesecake", "Chocolate", "Cookies", "Pastries")
    val filters = listOf("Sweet", "Rich", "Creamy", "Warm", "Chocolate")

    CategoryScreenTemplate(
        navController = navController,
        title = "Desserts",
        categoryId = "desserts",
        subcategories = subcategories,
        filters = filters,
        categoryDescription = "DESSERTS",
        categorySubtitle = "Sweet treats and indulgent desserts to satisfy your cravings."
    )
}

// 6. FOOD SCREEN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodScreen(navController: NavController) {
    val subcategories = listOf("Wraps", "Salads", "Pastries", "Snacks", "Croissants", "Bagels")
    val filters = listOf("Vegetarian", "Healthy", "Light", "Filling", "Fresh")

    CategoryScreenTemplate(
        navController = navController,
        title = "Food",
        categoryId = "food",
        subcategories = subcategories,
        filters = filters,
        categoryDescription = "FOOD",
        categorySubtitle = "Freshly prepared food items to complement your beverage."
    )
}

// 7. MERCHANDISE SCREEN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MerchandiseScreen(navController: NavController) {
    val subcategories = listOf("Mugs", "Tumblers", "Coffee Beans", "Accessories", "Gift Cards", "Grinders")
    val filters = listOf("Ceramic", "Steel", "Glass", "Limited Edition", "Premium")

    CategoryScreenTemplate(
        navController = navController,
        title = "Merchandise",
        categoryId = "merchandise",
        subcategories = subcategories,
        filters = filters,
        categoryDescription = "MERCHANDISE",
        categorySubtitle = "Take home the Starbucks experience with our exclusive merchandise."
    )
}

// 8. TEA & BEVERAGES SCREEN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeaBeveragesScreen(navController: NavController) {
    val subcategories = listOf("Chai", "Green Tea", "Herbal", "Iced Tea", "Hot Chocolate", "Matcha")
    val filters = listOf("Hot", "Cold", "Herbal", "Spiced", "Sweet")

    CategoryScreenTemplate(
        navController = navController,
        title = "Tea & Beverages",
        categoryId = "tea_beverages",
        subcategories = subcategories,
        filters = filters,
        categoryDescription = "TEA & BEVERAGES",
        categorySubtitle = "Premium tea blends and refreshing beverages for every mood."
    )
}

// 9. BREAKFAST SCREEN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreakfastScreen(navController: NavController) {
    val subcategories = listOf("Toast", "Pancakes", "Oatmeal", "Pastries", "Croissants", "Parfait")
    val filters = listOf("Healthy", "Sweet", "Savory", "Light", "Filling")

    CategoryScreenTemplate(
        navController = navController,
        title = "Breakfast",
        categoryId = "breakfast",
        subcategories = subcategories,
        filters = filters,
        categoryDescription = "BREAKFAST",
        categorySubtitle = "Start your day right with our delicious breakfast options."
    )
}

// COMMON CATEGORY SCREEN TEMPLATE
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreenTemplate(
    navController: NavController,
    title: String,
    categoryId: String,
    subcategories: List<String>,
    filters: List<String>,
    categoryDescription: String,
    categorySubtitle: String
) {
    var selectedSubcategory by remember { mutableStateOf(subcategories.firstOrNull()) }
    var selectedFilter by remember { mutableStateOf(filters.firstOrNull()) }
    var selectedOrderType by remember { mutableStateOf(OrderType.DINE_IN) }
    var showLocationDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Watch cart changes
    val cartItemCount = CartManager.getTotalItems()
    val cartTotal = CartManager.getTotalAmount()

    // Watch location changes
    val selectedLocation by LocationManager.selectedLocation

    // Fetch menu items from Firebase with improved error handling
    val menuItems by produceState<List<CafeMenuItem>>(initialValue = emptyList(), categoryId) {
        isLoading = true
        errorMessage = null
        try {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("menu")
                .document(categoryId)
                .collection("items")
                .get()
                .await()

            val items = snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(CafeMenuItem::class.java)?.copy(
                        name = doc.getString("name") ?: "",
                        price = doc.getString("price") ?: "0",
                        size = doc.getString("size") ?: "",
                        calories = doc.getString("calories") ?: "0",
                        category = doc.getString("category") ?: categoryId,
                        description = doc.getString("description") ?: "",
                        imageUrl = doc.getString("imageUrl") ?: "",
                        ingredients = doc.getString("ingredients") ?: "",
                        availability = doc.getBoolean("availability") ?: true
                    )
                } catch (e: Exception) {
                    null
                }
            }

            if (items.isEmpty()) {
                value = getSampleMenuItems(categoryId)
                errorMessage = "Using sample data - Firebase collection is empty"
            } else {
                value = items
            }
        } catch (e: Exception) {
            value = getSampleMenuItems(categoryId)
            errorMessage = "Firebase connection failed - Using sample data"
        } finally {
            isLoading = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Header with back button and search
            TopAppBar(
                title = {
                    Text(
                        text = title,
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

            // Location Selection Bar (shows only when location is selected)
            selectedLocation?.let { location ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable { showLocationDialog = true },
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = Color(0xFF00704A),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = location.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.Black
                            )
                            Text(
                                text = location.address,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        Text(
                            text = location.estimatedTime,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00704A)
                        )
                    }
                }

                // Order Type Selection (only shows when location is selected)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    Button(
                        onClick = { selectedOrderType = OrderType.DINE_IN },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedOrderType == OrderType.DINE_IN)
                                Color(0xFF00704A) else Color.White,
                            contentColor = if (selectedOrderType == OrderType.DINE_IN)
                                Color.White else Color(0xFF00704A)
                        ),
                        shape = RoundedCornerShape(
                            topStart = 8.dp,
                            bottomStart = 8.dp,
                            topEnd = 0.dp,
                            bottomEnd = 0.dp
                        )
                    ) {
                        Text("🍽️ Dine In")
                    }
                    Button(
                        onClick = { selectedOrderType = OrderType.TAKEAWAY },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedOrderType == OrderType.TAKEAWAY)
                                Color(0xFF00704A) else Color.White,
                            contentColor = if (selectedOrderType == OrderType.TAKEAWAY)
                                Color.White else Color(0xFF00704A)
                        ),
                        shape = RoundedCornerShape(
                            topStart = 0.dp,
                            bottomStart = 0.dp,
                            topEnd = 8.dp,
                            bottomEnd = 8.dp
                        )
                    ) {
                        Text("📦 Takeaway")
                    }
                }
            }

            // Location Selection Button (shows when no location is selected)
            if (selectedLocation == null) {
                Button(
                    onClick = { showLocationDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00704A)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Select Location",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Select Store Location",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Only show menu content when location is selected
            if (selectedLocation != null) {
                // Categories Tabs
                LazyRow(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
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
                                fontSize = 16.sp
                            )
                            if (selectedSubcategory == subcategory) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 4.dp)
                                        .height(2.dp)
                                        .width(40.dp)
                                        .background(Color(0xFF00704A))
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Filter Chips
                LazyRow(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filters) { filter ->
                        FilterChip(
                            onClick = { selectedFilter = filter },
                            label = { Text(filter, fontSize = 14.sp) },
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
                Text(
                    text = categoryDescription,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = categorySubtitle,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                // Error message (if any)
                errorMessage?.let { message ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE082))
                    ) {
                        Text(
                            text = message,
                            modifier = Modifier.padding(12.dp),
                            fontSize = 12.sp,
                            color = Color(0xFF795548)
                        )
                    }
                }

                // Loading indicator or Menu Items List
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF00704A))
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 16.dp,
                            bottom = if (cartItemCount > 0) 100.dp else 16.dp // Add bottom padding when cart is visible
                        ),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(menuItems) { item ->
                            MenuItemCard(
                                item = item,
                                onAddItem = { CartManager.addItem(item) },
                                onRemoveItem = { CartManager.removeItem(item.name) },
                                itemCount = CartManager.getItemCount(item.name)
                            )
                        }
                    }
                }
            } else {
                // Show message when no location is selected
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = Color.Gray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Please select a store location",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Text(
                            text = "to view our ${title.lowercase()} menu",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        // Floating Cart Button (only shows when location is selected and cart has items)
        if (selectedLocation != null && cartItemCount > 0) {
            Button(
                onClick = { navController.navigate("cart") },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00704A)
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${cartItemCount} ITEM${if (cartItemCount > 1) "S" else ""} ADDED",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = CartManager.getLastAddedItem(),
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "₹${String.format("%.2f", cartTotal)}",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "View Cart",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }

    // Location Selection Dialog
    if (showLocationDialog) {
        LocationSelectionDialog(
            onLocationSelected = { location ->
                LocationManager.setLocation(location)
                showLocationDialog = false
            },
            onDismiss = { showLocationDialog = false }
        )
    }
}

@Composable
fun LocationSelectionDialog(
    onLocationSelected: (LocationInfo) -> Unit,
    onDismiss: () -> Unit
) {
    // Sample locations
    val locations = listOf(
        LocationInfo("Starbucks Agra East Gate", "East Gate Road, Near Mall", "26 mins"),
        LocationInfo("Starbucks Agra Sadar", "Sadar Bazaar Area", "18 mins"),
        LocationInfo("Starbucks Taj Ganj", "Near Taj Mahal Gate", "35 mins"),
        LocationInfo("Starbucks Civil Lines", "Civil Lines Area", "22 mins")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Select Store Location",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            LazyColumn {
                items(locations) { location ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onLocationSelected(location) },
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF5F5F5)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = Color(0xFF00704A),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = location.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = location.address,
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                            Text(
                                text = location.estimatedTime,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00704A)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF00704A))
            }
        }
    )
}

@Composable
fun MenuItemCard(
    item: CafeMenuItem,
    onAddItem: () -> Unit,
    onRemoveItem: () -> Unit,
    itemCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image (circular with gradient background like Starbucks)
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        when (item.category) {
                            "hot_coffees" -> Color(0xFFFFD700) // Golden for coffee
                            "cold_coffees" -> Color(0xFF87CEEB) // Light blue for cold
                            "sandwiches" -> Color(0xFF98FB98) // Light green for sandwiches
                            "burgers" -> Color(0xFFFF6347) // Tomato red for burgers
                            "desserts" -> Color(0xFFDDA0DD) // Light purple for desserts
                            "food" -> Color(0xFFFFA500) // Orange for general food
                            "merchandise" -> Color(0xFFD3D3D3) // Light gray for merch
                            "tea_beverages" -> Color(0xFF90EE90) // Light green for tea
                            "breakfast" -> Color(0xFFFFE4B5) // Moccasin for breakfast
                            else -> Color(0xFFD3D3D3) // Light gray default
                        }
                    )
            ) {
                AsyncImage(
                    model = getDrawableResourceForItem(item.name, item.category),
                    contentDescription = item.name,
                    modifier = Modifier
                        .size(60.dp)
                        .align(Alignment.Center),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Product Info
            Column(modifier = Modifier.weight(1f)) {
                // Availability indicator
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                if (item.availability) Color(0xFF00704A) else Color.Red,
                                CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Text(
                    text = item.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = "${item.calories} kcal",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Text(
                    text = item.description,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "₹${item.price}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            // Add/Remove Item Controls
            if (itemCount == 0) {
                Button(
                    onClick = onAddItem,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00704A)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Add Item", color = Color.White, fontSize = 12.sp)
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0xFF00704A), RoundedCornerShape(20.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    IconButton(
                        onClick = onRemoveItem,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Filled.RemoveCircle,
                            contentDescription = "Remove",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = itemCount.toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    IconButton(
                        onClick = onAddItem,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

fun getDrawableResourceForItem(itemName: String, category: String): Int {
    // Return R.drawable resource IDs based on item name and category
    return when (category) {
        "hot_coffees" -> when {
            itemName.contains("Cortado", true) -> R.drawable.date_cortado
            itemName.contains("Frappuccino", true) -> R.drawable.churro_frappuccino
            itemName.contains("Latte", true) -> R.drawable.barista_latte
            itemName.contains("Americano", true) -> R.drawable.americano
            itemName.contains("Cappuccino", true) -> R.drawable.cappuccino
            itemName.contains("Hot Chocolate", true) -> R.drawable.hot_chocolate
            itemName.contains("Mocha", true) -> R.drawable.hot_chocolate
            itemName.contains("Macchiato", true) -> R.drawable.barista_latte
            else -> R.drawable.hotcoffee
        }
        "cold_coffees" -> when {
            itemName.contains("Iced Latte", true) -> R.drawable.iced_latte
            itemName.contains("Cold Brew", true) -> R.drawable.cold_brew
            itemName.contains("Frappuccino", true) -> R.drawable.java_chip_frappuccino
            itemName.contains("Iced Americano", true) -> R.drawable.iced_americano
            itemName.contains("Iced Mocha", true) -> R.drawable.iced_latte
            else -> R.drawable.ic_coldcoffee
        }
        "sandwiches" -> when {
            itemName.contains("Veg", true) -> R.drawable.ic_sandwich
            itemName.contains("Paneer", true) -> R.drawable.ic_sandwich
            itemName.contains("Cheese", true) -> R.drawable.ic_sandwich
            itemName.contains("Corn", true) -> R.drawable.ic_sandwich
            itemName.contains("Grilled", true) -> R.drawable.ic_sandwich
            itemName.contains("Mushroom", true) -> R.drawable.ic_sandwich
            itemName.contains("Italian", true) -> R.drawable.ic_sandwich
            else -> R.drawable.ic_sandwich
        }
        "burgers" -> when {
            itemName.contains("Aloo", true) -> R.drawable.ic_burger
            itemName.contains("Paneer", true) -> R.drawable.ic_burger
            itemName.contains("Veggie", true) -> R.drawable.ic_burger
            itemName.contains("Classic", true) -> R.drawable.ic_burger
            itemName.contains("Mexican", true) -> R.drawable.ic_burger
            itemName.contains("Mushroom", true) -> R.drawable.ic_burger
            else -> R.drawable.ic_burger
        }
        "desserts" -> when {
            itemName.contains("Brownie", true) -> R.drawable.chocolate_brownie
            itemName.contains("Lava", true) -> R.drawable.choco_lava_cake
            itemName.contains("Cheesecake", true) -> R.drawable.cheese_cake
            itemName.contains("Tiramisu", true) -> R.drawable.tiramsu
            itemName.contains("Velvet", true) -> R.drawable.choco_lava_cake
            itemName.contains("Cookie", true) -> R.drawable.chocolate_brownie
            itemName.contains("Muffin", true) -> R.drawable.ic_dessert
            else -> R.drawable.ic_dessert
        }
        "food" -> when {
            itemName.contains("Wrap", true) -> R.drawable.food_wrap
            itemName.contains("Salad", true) -> R.drawable.saladjpg
            itemName.contains("Pastry", true) -> R.drawable.ic_dessert
            itemName.contains("Croissant", true) -> R.drawable.ic_dessert
            itemName.contains("Bagel", true) -> R.drawable.food_wrap
            else -> R.drawable.food_wrap
        }
        "merchandise" -> when {
            itemName.contains("Tumbler", true) -> R.drawable.tumbler
            itemName.contains("Mug", true) -> R.drawable.ceramic_mug
            itemName.contains("Beans", true) -> R.drawable.coffeebean
            itemName.contains("Grinder", true) -> R.drawable.ceramic_mug
            itemName.contains("Press", true) -> R.drawable.ceramic_mug
            else -> R.drawable.ceramic_mug
        }
        "tea_beverages" -> when {
            itemName.contains("Chai", true) -> R.drawable.hotcoffee
            itemName.contains("Green Tea", true) -> R.drawable.hotcoffee
            itemName.contains("Earl Grey", true) -> R.drawable.hotcoffee
            itemName.contains("Chamomile", true) -> R.drawable.hotcoffee
            itemName.contains("Hot Chocolate", true) -> R.drawable.hot_chocolate
            else -> R.drawable.hotcoffee
        }
        "breakfast" -> when {
            itemName.contains("Toast", true) -> R.drawable.ic_sandwich
            itemName.contains("Croissant", true) -> R.drawable.ic_dessert
            itemName.contains("Oatmeal", true) -> R.drawable.ic_dessert
            itemName.contains("Pancakes", true) -> R.drawable.ic_dessert
            itemName.contains("French Toast", true) -> R.drawable.ic_dessert
            itemName.contains("Parfait", true) -> R.drawable.ic_dessert
            else -> R.drawable.ic_sandwich
        }
        else -> R.drawable.hotcoffee
    }
}

fun getSampleMenuItems(categoryId: String): List<CafeMenuItem> = when (categoryId) {
    "hot_coffees" -> listOf(
        CafeMenuItem("Date Cortado", "383.25", "180ml", "168", "hot_coffees", "Double shot blonde espresso, paired with date flavoured sauce and steamed milk", "", "Espresso, date syrup, milk", true),
        CafeMenuItem("Churro Frappuccino", "445.50", "350ml", "368", "hot_coffees", "Signature Starbucks Frappuccino with churro flavour", "", "Frappuccino base, churro syrup, whipped cream", true),
        CafeMenuItem("Barista Pride Latte", "430.50", "240ml", "210", "hot_coffees", "Premium latte with signature blend", "", "Espresso, steamed milk, pride blend", true),
        CafeMenuItem("Signature Hot Chocolate", "330.75", "240ml", "280", "hot_coffees", "Rich and creamy hot chocolate", "", "Cocoa, milk, whipped cream", true),
        CafeMenuItem("Caffe Americano", "283.50", "350ml", "15", "hot_coffees", "Espresso shots topped with hot water", "", "Espresso, hot water", true),
        CafeMenuItem("Cappuccino", "304.50", "180ml", "120", "hot_coffees", "Espresso with steamed milk and foam", "", "Espresso, steamed milk, foam", true),
        CafeMenuItem("Caramel Macchiato", "398.25", "240ml", "190", "hot_coffees", "Espresso with vanilla syrup and caramel drizzle", "", "Espresso, vanilla syrup, steamed milk, caramel", true),
        CafeMenuItem("Mocha", "356.75", "240ml", "260", "hot_coffees", "Rich chocolate and espresso combination", "", "Espresso, chocolate syrup, steamed milk", true)
    )
    "cold_coffees" -> listOf(
        CafeMenuItem("Iced Barista Pride Latte", "430.50", "240ml", "180", "cold_coffees", "Chilled version of our signature latte", "", "Espresso, cold milk, ice, pride blend", true),
        CafeMenuItem("Iced Caffe Americano", "283.50", "350ml", "25", "cold_coffees", "Espresso shots with cold water over ice", "", "Espresso, cold water, ice", true),
        CafeMenuItem("Cold Brew", "325.50", "350ml", "5", "cold_coffees", "Slow-steeped cold brew coffee", "", "Cold brew concentrate, water", true),
        CafeMenuItem("Iced Latte", "346.50", "240ml", "130", "cold_coffees", "Espresso with cold milk over ice", "", "Espresso, cold milk, ice", true),
        CafeMenuItem("Java Chip Frappuccino", "472.50", "350ml", "420", "cold_coffees", "Frappuccino with chocolate chips", "", "Coffee, milk, chocolate chips, whipped cream", true),
        CafeMenuItem("Caramel Frappuccino", "456.75", "350ml", "380", "cold_coffees", "Blended coffee with caramel flavor", "", "Coffee, caramel syrup, milk, whipped cream", true),
        CafeMenuItem("Iced Mocha", "378.25", "350ml", "220", "cold_coffees", "Iced coffee with rich chocolate", "", "Espresso, chocolate syrup, cold milk, ice", true),
        CafeMenuItem("Vanilla Sweet Cream Cold Brew", "356.50", "350ml", "110", "cold_coffees", "Cold brew topped with vanilla sweet cream", "", "Cold brew, vanilla sweet cream", true)
    )
    "sandwiches" -> listOf(
        CafeMenuItem("Paneer Tikka Sandwich", "289.50", "220g", "320", "sandwiches", "Spiced paneer tikka with mint chutney", "", "Paneer, tikka spices, mint chutney, bread", true),
        CafeMenuItem("Veg Supreme Sandwich", "245.75", "200g", "280", "sandwiches", "Mixed vegetables with cheese and herbs", "", "Mixed vegetables, cheese, herbs, bread", true),
        CafeMenuItem("Corn & Cheese Sandwich", "234.25", "190g", "260", "sandwiches", "Sweet corn with melted cheese", "", "Sweet corn, cheese, mayo, bread", true),
        CafeMenuItem("Grilled Veggie Sandwich", "267.50", "210g", "290", "sandwiches", "Grilled vegetables with pesto sauce", "", "Grilled vegetables, pesto, bread", true),
        CafeMenuItem("Mushroom & Cheese Sandwich", "298.75", "225g", "310", "sandwiches", "Sautéed mushrooms with melted cheese", "", "Mushrooms, cheese, herbs, bread", true),
        CafeMenuItem("Italian Herb Sandwich", "278.25", "200g", "270", "sandwiches", "Italian herbs with mozzarella", "", "Italian herbs, mozzarella, tomato, bread", true)
    )
    "burgers" -> listOf(
        CafeMenuItem("Aloo Tikki Supreme Burger", "198.50", "250g", "380", "burgers", "Crispy aloo tikki with special sauce", "", "Aloo tikki, lettuce, tomato, special sauce, bun", true),
        CafeMenuItem("Paneer Makhani Burger", "267.75", "280g", "420", "burgers", "Paneer in rich makhani sauce", "", "Paneer, makhani sauce, onions, bun", true),
        CafeMenuItem("Veggie Deluxe Burger", "234.25", "260g", "350", "burgers", "Mixed veggie patty with cheese", "", "Veggie patty, cheese, lettuce, mayo, bun", true),
        CafeMenuItem("Classic Veg Burger", "189.50", "230g", "320", "burgers", "Traditional veg burger with fresh veggies", "", "Veg patty, tomato, lettuce, sauce, bun", true),
        CafeMenuItem("Spicy Mexican Burger", "245.75", "270g", "390", "burgers", "Spicy mexican-style veggie burger", "", "Spicy patty, jalapeños, cheese, salsa, bun", true),
        CafeMenuItem("Mushroom Swiss Burger", "278.25", "275g", "360", "burgers", "Mushroom patty with swiss cheese", "", "Mushroom patty, swiss cheese, onions, bun", true)
    )
    "desserts" -> listOf(
        CafeMenuItem("Chocolate Brownie Supreme", "189.75", "120g", "450", "desserts", "Rich fudgy brownie with chocolate chips", "", "Dark chocolate, flour, butter, chocolate chips", true),
        CafeMenuItem("Choco Lava Cake", "234.50", "100g", "480", "desserts", "Warm chocolate cake with molten center", "", "Chocolate, flour, sugar, butter", true),
        CafeMenuItem("New York Cheesecake", "267.25", "140g", "520", "desserts", "Classic creamy New York-style cheesecake", "", "Cream cheese, graham crackers, vanilla", true),
        CafeMenuItem("Tiramisu", "298.75", "130g", "410", "desserts", "Italian coffee-flavored dessert", "", "Mascarpone, coffee, ladyfingers, cocoa", true),
        CafeMenuItem("Red Velvet Cake", "245.50", "120g", "380", "desserts", "Moist red velvet cake with cream cheese frosting", "", "Red velvet cake, cream cheese frosting", true),
        CafeMenuItem("Chocolate Chip Cookie", "134.25", "80g", "320", "desserts", "Freshly baked chocolate chip cookies", "", "Flour, chocolate chips, butter, sugar", true),
        CafeMenuItem("Blueberry Muffin", "156.75", "90g", "290", "desserts", "Soft muffin loaded with fresh blueberries", "", "Flour, blueberries, butter, sugar", true)
    )
    "food" -> listOf(
        CafeMenuItem("Chicken Tikka Wrap", "298.50", "250g", "420", "food", "Grilled chicken tikka in soft tortilla", "", "Chicken tikka, tortilla, vegetables, sauce", true),
        CafeMenuItem("Paneer Wrap Supreme", "267.75", "230g", "380", "food", "Spiced paneer with fresh vegetables", "", "Paneer, vegetables, tortilla, mint chutney", true),
        CafeMenuItem("Caesar Salad", "234.25", "200g", "180", "food", "Fresh greens with caesar dressing", "", "Lettuce, croutons, parmesan, caesar dressing", true),
        CafeMenuItem("Greek Salad", "245.50", "190g", "160", "food", "Mediterranean salad with feta cheese", "", "Mixed greens, feta, olives, tomatoes", true),
        CafeMenuItem("Croissant", "134.75", "80g", "280", "food", "Buttery, flaky French croissant", "", "Flour, butter, yeast", true),
        CafeMenuItem("Pain au Chocolat", "167.25", "90g", "320", "food", "Chocolate-filled pastry", "", "Pastry, dark chocolate", true),
        CafeMenuItem("Bagel with Cream Cheese", "189.50", "120g", "340", "food", "Fresh bagel with cream cheese spread", "", "Bagel, cream cheese", true)
    )
    "merchandise" -> listOf(
        CafeMenuItem("Starbucks Tumbler Premium", "1299.00", "473ml", "0", "merchandise", "Premium insulated tumbler for hot and cold beverages", "", "Stainless steel, BPA-free, double-wall insulation", true),
        CafeMenuItem("Pike Place Roast Coffee Beans", "1650.00", "250g", "0", "merchandise", "Medium roast whole bean coffee - our original blend", "", "100% Arabica beans, medium roast", true),
        CafeMenuItem("Ceramic Mug Classic", "899.00", "355ml", "0", "merchandise", "Classic Starbucks ceramic mug", "", "High-quality ceramic, dishwasher safe", true),
        CafeMenuItem("Travel Mug", "1456.00", "414ml", "0", "merchandise", "Leak-proof travel mug with ergonomic design", "", "Stainless steel, leak-proof lid", true),
        CafeMenuItem("French Press", "2299.00", "946ml", "0", "merchandise", "Premium French press for perfect coffee brewing", "", "Borosilicate glass, stainless steel", true),
        CafeMenuItem("Coffee Grinder", "3456.00", "N/A", "0", "merchandise", "Burr coffee grinder for consistent grind", "", "Ceramic burr, multiple grind settings", true),
        CafeMenuItem("Gift Card", "500.00", "N/A", "0", "merchandise", "Starbucks gift card - perfect for coffee lovers", "", "Digital or physical card available", true),
        CafeMenuItem("Espresso Roast Beans", "1789.00", "250g", "0", "merchandise", "Dark roast espresso beans", "", "100% Arabica beans, dark roast", true)
    )
    "tea_beverages" -> listOf(
        CafeMenuItem("Masala Chai Latte", "234.50", "240ml", "140", "tea_beverages", "Traditional Indian spiced chai with steamed milk", "", "Black tea, spices, steamed milk", true),
        CafeMenuItem("Green Tea Latte", "267.25", "240ml", "120", "tea_beverages", "Matcha green tea with steamed milk", "", "Matcha powder, steamed milk", true),
        CafeMenuItem("Earl Grey Tea", "189.75", "240ml", "5", "tea_beverages", "Classic bergamot-flavored black tea", "", "Earl Grey tea leaves, bergamot oil", true),
        CafeMenuItem("Chamomile Tea", "178.50", "240ml", "0", "tea_beverages", "Soothing herbal chamomile tea", "", "Chamomile flowers", true),
        CafeMenuItem("Iced Green Tea", "198.25", "350ml", "0", "tea_beverages", "Refreshing iced green tea", "", "Green tea, ice", true),
        CafeMenuItem("Hot Chocolate Premium", "298.75", "240ml", "320", "tea_beverages", "Rich premium hot chocolate with whipped cream", "", "Premium cocoa, milk, whipped cream", true)
    )
    "breakfast" -> listOf(
        CafeMenuItem("Avocado Toast", "345.50", "150g", "280", "breakfast", "Smashed avocado on multigrain toast", "", "Avocado, multigrain bread, lime, seasoning", true),
        CafeMenuItem("Egg & Cheese Croissant", "267.25", "180g", "420", "breakfast", "Scrambled eggs and cheese in buttery croissant", "", "Eggs, cheese, croissant", true),
        CafeMenuItem("Oatmeal Bowl", "189.75", "200g", "220", "breakfast", "Hearty oatmeal with fruits and nuts", "", "Oats, fruits, nuts, honey", true),
        CafeMenuItem("Pancakes Stack", "298.50", "250g", "520", "breakfast", "Fluffy pancakes with maple syrup", "", "Flour, eggs, milk, maple syrup", true),
        CafeMenuItem("French Toast", "334.75", "220g", "480", "breakfast", "Golden French toast with berries", "", "Bread, eggs, milk, berries, syrup", true),
        CafeMenuItem("Granola Parfait", "234.25", "180g", "280", "breakfast", "Greek yogurt layered with granola and berries", "", "Greek yogurt, granola, mixed berries", true)
    )
    else -> emptyList()
}