package com.example.multiplepages

import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = {
                if (currentRoute != "home") {
                    navController.navigate("home") {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_home),
                    contentDescription = "Home",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified
                )
            },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = currentRoute == "order",
            onClick = {
                if (currentRoute != "order") {
                    navController.navigate("order") {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_order),
                    contentDescription = "Order",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified
                )
            },
            label = { Text("Order") }
        )
        NavigationBarItem(
            selected = currentRoute == "payment",
            onClick = {
                if (currentRoute != "payment") {
                    navController.navigate("payment") {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_payment),
                    contentDescription = "Payment",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified
                )
            },
            label = { Text("Pay") }
        )
        NavigationBarItem(
            selected = currentRoute?.startsWith("user") == true,
            onClick = {
                if (currentRoute?.startsWith("user") != true) {
                    // Simply navigate to "user" route - no parameters needed
                    try {
                        navController.navigate("user") {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    } catch (e: IllegalArgumentException) {
                        // Route doesn't exist in navigation graph
                        println("User route not found in navigation graph")
                    }
                }
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_user),
                    contentDescription = "User",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified
                )
            },
            label = { Text("User") }
        )
    }
}