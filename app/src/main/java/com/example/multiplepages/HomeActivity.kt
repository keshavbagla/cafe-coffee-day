package com.example.multiplepages

import HomeScreen
import SuccessSplashScreen
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.multiplepages.model.CafeMenuItem
import com.example.multiplepages.screens.PaymentScreen
import com.example.multiplepages.ui.theme.MultiplepagesTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay

class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MultiplepagesTheme {
                val navController = rememberNavController()

                Surface(color = MaterialTheme.colorScheme.background) {
                    NavHost(navController = navController, startDestination = "home") {

                        composable("home") {
                            HomeScreen(navController)
                        }

                        // Category navigation routes
                        composable("category/{categoryId}") { backStackEntry ->
                            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
                            CategoryRouter(navController, categoryId)
                        }

                        // Product detail route
                        composable("productDetail/{productName}") { backStackEntry ->
                            val productName = backStackEntry.arguments?.getString("productName") ?: ""
                            ProductDetailScreen(navController, productName)
                        }

                        // Full menu route
                        composable("fullMenu") {
                            FullMenuScreen(navController)
                        }

                        composable("cart") {
                            CartScreen(navController)
                        }
                        composable("HotCoffeesScreen") { HotCoffeesScreen(navController) }
                        composable("ColdCoffeesScreen") { ColdCoffeesScreen(navController) }
                        composable("SandwichesScreen") { SandwichesScreen(navController) }
                        composable("BurgersScreen") { BurgersScreen(navController) }
                        composable("DessertsScreen") { DessertsScreen(navController) }

                        composable("confirmOrder") {
                            val selectedItems = remember { mutableListOf<CafeMenuItem>() }
                            ConfirmOrderScreen(selectedItems = selectedItems, navController = navController)
                        }

                        composable("payment") {
                            PaymentScreen(navController)
                        }

                        composable("user") {
                            UserScreen(navController = navController)
                        }

                        composable("success") {
                            SuccessSplashScreen(navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SplashScreen(onAuthCheck: (Boolean) -> Unit) {
    val auth = Firebase.auth

    LaunchedEffect(Unit) {
        delay(2000) // Show splash for 2 seconds

        // Check if user is logged in
        val currentUser = auth.currentUser
        onAuthCheck(currentUser != null)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Add your app logo here
            // Image(
            //     painter = painterResource(id = R.drawable.app_logo),
            //     contentDescription = "App Logo",
            //     modifier = Modifier.size(120.dp)
            // )

            Text(
                text = "☕ CafeApp",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            androidx.compose.foundation.layout.Spacer(
                modifier = Modifier.size(32.dp)
            )

            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = MaterialTheme.colorScheme.primary
            )

            androidx.compose.foundation.layout.Spacer(
                modifier = Modifier.size(16.dp)
            )

            Text(
                text = "Loading...",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

// Category router function
@Composable
fun CategoryRouter(navController: NavController, categoryId: String) {
    when (categoryId) {
        "bestseller" -> BestsellerScreen(navController)
        "hotcoffee" -> HotCoffeesScreen(navController)
        "Coldcoffee" -> ColdCoffeesScreen(navController)
        "sandwiches" -> SandwichesScreen(navController)
        "Burger" -> BurgersScreen(navController)
        "Desserts" -> DessertsScreen(navController)
        "coffee_at_home" -> CoffeeAtHomeScreen(navController)
        "ready_to_eat" -> ReadyToEatScreen(navController)
        else -> {
            Text(
                "Category not found: $categoryId",
                modifier = Modifier.fillMaxSize(),
                textAlign = TextAlign.Center
            )
        }
    }
}

// Missing category screens
@Composable
fun BestsellerScreen(navController: NavController) {
    CategoryScreenTemplate(
        navController = navController,
        title = "Bestsellers",
        categoryId = "bestseller",
        subcategories = listOf("Top Picks", "Most Ordered", "Customer Favorites"),
        filters = listOf("Popular", "Trending", "Classic"),
        categoryDescription = "BESTSELLERS",
        categorySubtitle = "Our most popular items that customers love and order again and again."
    )
}

@Composable
fun CoffeeAtHomeScreen(navController: NavController) {
    CategoryScreenTemplate(
        navController = navController,
        title = "Coffee At Home",
        categoryId = "coffee_at_home",
        subcategories = listOf("Ground Coffee", "Whole Beans", "Instant Coffee"),
        filters = listOf("Dark Roast", "Medium Roast", "Light Roast"),
        categoryDescription = "COFFEE AT HOME",
        categorySubtitle = "Bring the Starbucks experience home with our premium coffee beans and ground coffee."
    )
}



@Composable
fun ReadyToEatScreen(navController: NavController) {
    CategoryScreenTemplate(
        navController = navController,
        title = "Ready to Eat",
        categoryId = "ready_to_eat",
        subcategories = listOf("Snacks", "Salads", "Wraps"),
        filters = listOf("Quick", "Healthy", "Filling"),
        categoryDescription = "READY TO EAT",
        categorySubtitle = "Quick and convenient food options perfect for on-the-go dining."
    )
}

// Placeholder screens
@Composable
fun ProductDetailScreen(navController: NavController, productName: String) {
    Text(
        "Product Detail: $productName\n\n(To be implemented)",
        modifier = Modifier.fillMaxSize(),
        textAlign = TextAlign.Center
    )
}

@Composable
fun FullMenuScreen(navController: NavController) {
    Text(
        "Full Menu Screen\n\n(To be implemented)",
        modifier = Modifier.fillMaxSize(),
        textAlign = TextAlign.Center
    )
}