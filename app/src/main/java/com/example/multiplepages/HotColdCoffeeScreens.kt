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

// HOT COFFEE SCREEN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotCoffeeScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
        CategoryScreen(
            navController = navController,
            title = "Hot Coffees",
            categoryId = "hot_coffees",
            subcategories = listOf("Espresso", "Latte", "Cappuccino", "Filter Coffee", "Americano"),
            filters = listOf("Hot", "Medium", "Strong", "Mild", "Decaf")
        )
        
        // Bottom Cart Indicator
        BottomCartIndicator(
            navController = navController,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

// COLD COFFEE SCREEN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColdCoffeeScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
        CategoryScreen(
            navController = navController,
            title = "Cold Coffees",
            categoryId = "cold_coffees",
            subcategories = listOf("Iced Latte", "Cold Brew", "Frappe", "Iced Mocha", "Frappuccino®"),
            filters = listOf("Cold", "Iced", "Blended", "Sweet", "Sugar-free")
        )
        
        // Bottom Cart Indicator
        BottomCartIndicator(
            navController = navController,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

// Updated DRINKS SCREEN to show Hot/Cold Coffee options
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrinksScreen(navController: NavController) {
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
                text = "Agra East Gate Rd",
                color = Color.Black,
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
                    text = "26mins",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
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
                onClick = { },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00704A)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("🍽️ Dine In", color = Color.White)
            }

            OutlinedButton(
                onClick = { },
                modifier = Modifier.weight(1f),
                border = BorderStroke(1.dp, Color(0xFF00704A)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("📦 Takeaway", color = Color(0xFF00704A))
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
                            "Drinks" -> { /* Already on drinks */ }
                            "Food" -> navController.navigate("food")
                            "Merchandise" -> navController.navigate("merchandise")
                            "Coffee At Home" -> navController.navigate("coffee_at_home")
                        }
                    }
                ) {
                    Text(
                        text = category,
                        color = if (category == "Drinks") Color(0xFF00704A) else Color.Gray,
                        fontWeight = if (category == "Drinks") FontWeight.Bold else FontWeight.Normal,
                        fontSize = 16.sp
                    )
                    if (category == "Drinks") {
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

        // Category Description
        Text(
            text = "DRINKS",
            modifier = Modifier.padding(horizontal = 16.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Text(
            text = "Our smooth signature coffee roasts with rich flavor and caramelly sweetness is at the very heart of everything we do.",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 14.sp,
            color = Color.Gray
        )

                 // Coffee Category Cards
         Box(modifier = Modifier.fillMaxSize()) {
             LazyColumn(
                 modifier = Modifier.fillMaxSize(),
                 contentPadding = PaddingValues(16.dp),
                 verticalArrangement = Arrangement.spacedBy(16.dp)
             ) {
                 item {
                     DrinkCategoryCard(
                         title = "Hot Coffees",
                         description = "Espresso, Latte, Cappuccino, Filter Coffee and more",
                         imageUrl = "https://via.placeholder.com/80x80/D2691E/FFFFFF?text=☕",
                         onClick = { navController.navigate("hot_coffees") }
                     )
                 }
                 
                 item {
                     DrinkCategoryCard(
                         title = "Cold Coffees",
                         description = "Iced Latte, Cold Brew, Frappe, Frappuccino® and more",
                         imageUrl = "https://via.placeholder.com/80x80/87CEEB/FFFFFF?text=🧊☕",
                         onClick = { navController.navigate("cold_coffees") }
                     )
                 }
                 
                 item {
                     DrinkCategoryCard(
                         title = "Tea & Others",
                         description = "Green Tea, Chai, Hot Chocolate and other beverages",
                         imageUrl = "https://via.placeholder.com/80x80/228B22/FFFFFF?text=🍵",
                         onClick = { navController.navigate("tea_others") }
                     )
                 }
                 
                 item {
                     DrinkCategoryCard(
                         title = "Refreshers",
                         description = "Fresh juices, smoothies and refreshing drinks",
                         imageUrl = "https://via.placeholder.com/80x80/FF6347/FFFFFF?text=🥤",
                         onClick = { navController.navigate("refreshers") }
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
fun DrinkCategoryCard(
    title: String,
    description: String,
    imageUrl: String,
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
            // Category Image
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Category Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp),
                    maxLines = 2
                )
            }

            // Arrow Icon
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Go to category",
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

// Helper function to get sample hot coffee items
fun getHotCoffeeItems(): List<CafeMenuItem> = listOf(
    CafeMenuItem(
        name = "Barista Pride Latte",
        price = "430.50",
        size = "240ml",
        calories = "240",
        category = "hot_coffees",
        description = "Barista Pride Latte with signature blend",
        imageUrl = "",
        ingredients = "Espresso, milk, pride syrup",
        availability = true
    ),
    CafeMenuItem(
        name = "Filter Coffee",
        price = "320.00",
        size = "150ml",
        calories = "90",
        category = "hot_coffees",
        description = "Strong South Indian filter coffee",
        imageUrl = "",
        ingredients = "Coffee powder, milk, water, sugar",
        availability = true
    ),
    CafeMenuItem(
        name = "Cappuccino",
        price = "380.00",
        size = "180ml",
        calories = "120",
        category = "hot_coffees",
        description = "Classic cappuccino with thick foam",
        imageUrl = "",
        ingredients = "Espresso, steamed milk, foam",
        availability = true
    ),
    CafeMenuItem(
        name = "Café Americano",
        price = "350.00",
        size = "240ml",
        calories = "15",
        category = "hot_coffees",
        description = "Rich espresso with hot water",
        imageUrl = "",
        ingredients = "Espresso, hot water",
        availability = true
    ),
    CafeMenuItem(
        name = "Café Latte",
        price = "410.00",
        size = "200ml",
        calories = "140",
        category = "hot_coffees",
        description = "Smooth latte with steamed milk",
        imageUrl = "",
        ingredients = "Espresso, steamed milk",
        availability = true
    )
)

// Helper function to get sample cold coffee items
fun getColdCoffeeItems(): List<CafeMenuItem> = listOf(
    CafeMenuItem(
        name = "Date Cortado",
        price = "383.25",
        size = "168ml",
        calories = "168",
        category = "cold_coffees",
        description = "Double shot blonde espresso, paired with date flavoured sauce and steamed milk",
        imageUrl = "",
        ingredients = "Espresso, date syrup, milk",
        availability = true
    ),
    CafeMenuItem(
        name = "Churro Frappuccino",
        price = "430.50",
        size = "368ml",
        calories = "368",
        category = "cold_coffees",
        description = "Signature Starbucks Frappuccino with churro flavor",
        imageUrl = "",
        ingredients = "Coffee, churro syrup, milk, whipped cream",
        availability = true
    ),
    CafeMenuItem(
        name = "Iced Caramel Latte",
        price = "420.00",
        size = "250ml",
        calories = "180",
        category = "cold_coffees",
        description = "Chilled espresso with caramel and milk over ice",
        imageUrl = "",
        ingredients = "Espresso, caramel, milk, ice",
        availability = true
    ),
    CafeMenuItem(
        name = "Cold Brew",
        price = "390.00",
        size = "300ml",
        calories = "50",
        category = "cold_coffees",
        description = "Slow-brewed cold coffee, smooth and refreshing",
        imageUrl = "",
        ingredients = "Cold brewed coffee, ice",
        availability = true
    ),
    CafeMenuItem(
        name = "Iced Mocha",
        price = "450.00",
        size = "250ml",
        calories = "220",
        category = "cold_coffees",
        description = "Iced coffee with rich chocolate flavor",
        imageUrl = "",
        ingredients = "Espresso, chocolate, milk, ice, whipped cream",
        availability = true
    )
)