package com.example.multiplepages.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.multiplepages.screens.*

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "drinks"
    ) {
        composable("drinks") {
            DrinksScreen(navController)
        }
        
        composable("food") {
            FoodScreen(navController)
        }
        
        composable("merchandise") {
            MerchandiseScreen(navController)
        }
        
        composable("coffee_at_home") {
            CoffeeAtHomeScreen(navController)
        }
        
        composable("viewCart") {
            ViewCartScreen(navController)
        }
        
        composable("productDetail/{itemName}") { backStackEntry ->
            val itemName = backStackEntry.arguments?.getString("itemName") ?: ""
            // You can create a ProductDetailScreen here if needed
            // For now, just navigate back or show a placeholder
            DrinksScreen(navController)
        }
    }
}