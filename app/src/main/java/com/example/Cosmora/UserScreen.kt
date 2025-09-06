package com.example.Cosmora

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    navController: NavController,
    userName: String = "Welcome User"  // Default value to prevent crashes
) {
    var showNetworkDialog by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Account",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Handle notifications */ }) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = { /* Handle settings */ }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E3932)
                )
            )
        },
        bottomBar = {
            SimpleVoiceBottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1E3932))
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // Profile Header Section
                item {
                    ProfileHeaderSection(userName = userName)
                }

                // Account Options Section
                item {
                    AccountOptionsSection(navController = navController)
                }
            }

            // Network Dialog
            if (showNetworkDialog) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    NetworkErrorDialog(
                        onClose = { showNetworkDialog = false }
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileHeaderSection(userName: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E3932))
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Decorative leaves background
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            // Profile Avatar
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .background(Color.White, CircleShape)
                    .padding(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFD4A574), CircleShape)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Coffee cup avatar
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Cup lid
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(12.dp)
                                .background(Color.White, RoundedCornerShape(6.dp))
                        )

                        // Cup body
                        Box(
                            modifier = Modifier
                                .width(50.dp)
                                .height(40.dp)
                                .background(Color.White, RoundedCornerShape(4.dp))
                                .padding(4.dp)
                        ) {
                            // Eyes
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .background(Color.Black, CircleShape)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .background(Color.Black, CircleShape)
                                )
                            }

                            // Coffee part (green mask)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(20.dp)
                                    .background(Color(0xFF00704A))
                                    .align(Alignment.BottomCenter)
                            )
                        }

                        // Smile
                        Text(
                            "︶",
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                    }
                }
            }

            // Edit icon
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-20).dp, y = (-20).dp)
                    .size(32.dp)
                    .background(Color(0xFF00704A), CircleShape)
                    .clickable { /* Handle edit profile */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit Profile",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // User Name
        Text(
            text = userName,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Welcome Tier
        Text(
            text = "Welcome Tier",
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun AccountOptionsSection(navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
        ) {
            // Using Material Icons instead of drawable resources to prevent resource errors
            AccountMenuItem(
                icon = Icons.Default.Star,
                title = "STARBUCKS REWARDS",
                onClick = { /* Handle rewards navigation - implement when ready */ }
            )

            AccountMenuItem(
                icon = Icons.Default.ShoppingCart,
                title = "ORDERS",
                onClick = { navController.navigate("orders") }
            )

            AccountMenuItem(
                icon = Icons.Default.Event,
                title = "MY EVENTS",
                onClick = { /* Handle events navigation - implement when ready */ }
            )

            AccountMenuItem(
                icon = Icons.Default.AccountBalance,
                title = "STARBUCKS PAY",
                onClick = { /* Handle starbucks pay navigation - implement when ready */ }
            )

            AccountMenuItem(
                icon = Icons.Default.Payment,
                title = "OTHER PAYMENT MODES",
                onClick = { /* Handle payment modes navigation - implement when ready */ }
            )

            AccountMenuItem(
                icon = Icons.Default.LocationOn,
                title = "MY ADDRESSES",
                onClick = { /* Handle addresses navigation - implement when ready */ }
            )

            AccountMenuItem(
                icon = Icons.Default.Help,
                title = "HELP CENTER",
                onClick = { /* Handle help navigation - implement when ready */ }
            )

            AccountMenuItem(
                icon = Icons.Default.Bookmark,
                title = "SAVED ARTICLES AND NEWS",
                onClick = { /* Handle saved articles navigation - implement when ready */ }
            )

            AccountMenuItem(
                icon = Icons.Default.RateReview,
                title = "RATE US ON THE APP STORE",
                onClick = { /* Handle app store rating */ },
                showDivider = false
            )
        }
    }
}

@Composable
fun AccountMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    showDivider: Boolean = true
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFF00704A),
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )

            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = "Go to $title",
                tint = Color(0xFF00704A),
                modifier = Modifier.size(24.dp)
            )
        }

        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 24.dp),
                color = Color.Gray.copy(alpha = 0.2f),
                thickness = 1.dp
            )
        }
    }
}

@Composable
fun NetworkErrorDialog(onClose: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFD700))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Your coffee cable's loose",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Please check your network connection and try again.",
                    fontSize = 14.sp,
                    color = Color.Black.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = onClose,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    "Close",
                    color = Color.Black,
                    fontSize = 14.sp
                )
            }
        }
    }
}