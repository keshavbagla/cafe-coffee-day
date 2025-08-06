import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import com.example.multiplepages.R
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage

import com.example.multiplepages.SimpleVoiceBottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    Scaffold(
        bottomBar = {
            SimpleVoiceBottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Header Section
            item {
                HeaderSection()
            }

            // Promotional Banner
            item {
                PromotionalBanner()
            }

            // Rewards Section
            item {
                RewardsSection()
            }

            // Featured Cards Section
            item {
                FeaturedCardsSection(navController)
            }

            // Handcrafted Curations Section
            item {
                HandcraftedCurationsSection(navController)
            }

            // Barista Recommends Section
            item {
                BaristaRecommendsSection(navController)
            }

            // Learn More Section
            item {
                LearnMoreSection()
            }
        }
    }
}

@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.store),
            contentDescription = "Store",
            modifier = Modifier.size(32.dp)
        )

        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Profile",
            modifier = Modifier.size(32.dp),
            tint = Color(0xFF00704A)
        )
    }
}

@Composable
fun PromotionalBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(200.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFFDB813),
                            Color(0xFFFFD700)
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "TAKE A",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "BLONDE",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "TURN",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Row {
                    // Hot Coffee Cup
                    AsyncImage(
                        model = "https://via.placeholder.com/80x80/FFFFFF/000000?text=☕",
                        contentDescription = "Hot Coffee",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Iced Coffee Cup
                    AsyncImage(
                        model = "https://via.placeholder.com/80x80/8B4513/FFFFFF?text=🧊☕",
                        contentDescription = "Iced Coffee",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
fun RewardsSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3932))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⭐", fontSize = 16.sp)
                    Text(
                        " 0",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "/5",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }
                Text(
                    "stars",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(24.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⭐", fontSize = 16.sp)
                    Text(
                        " 0",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    "rewards",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "You are 5 stars away",
                    color = Color.White,
                    fontSize = 14.sp
                )
                Text(
                    "from another reward",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun FeaturedCardsSection(navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(featuredItems) { item ->
                FeaturedCard(
                    item = item,
                    onClick = {
                        // Navigate to specific page based on item
                        navController.navigate("productDetail/${item.title}")
                    }
                )
            }
        }
    }
}

@Composable
fun FeaturedCard(
    item: FeaturedItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(300.dp)
            .height(200.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(item.startColor),
                            Color(item.endColor)
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "New",
                        fontSize = 14.sp,
                        color = Color.Black.copy(alpha = 0.7f)
                    )
                    Text(
                        text = item.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = item.description,
                        fontSize = 14.sp,
                        color = Color.Black.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00704A)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(item.buttonText, color = Color.White)
                    }
                }
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.title,
                    modifier = Modifier.size(100.dp)
                )
            }
        }
    }
}

@Composable
fun HandcraftedCurationsSection(navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Handcrafted Curations",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Calculate grid height based on number of items
        val itemsPerRow = 3
        val numberOfRows = (curationCategories.size + itemsPerRow - 1) / itemsPerRow
        val itemHeight = 120.dp // Approximate height per item (80dp image + 8dp spacer + 32dp text)
        val gridHeight = itemHeight * numberOfRows + (16.dp * (numberOfRows - 1)) // Add spacing between rows

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.height(gridHeight),
            userScrollEnabled = false // Disable grid scrolling since parent LazyColumn handles it
        ) {
            items(curationCategories.size) { index ->
                val category = curationCategories[index]
                CategoryCard(
                    category = category,
                    onClick = {
                        navController.navigate("category/${category.id}")
                    }
                )
            }
        }
    }
}

@Composable
fun CategoryCard(
    category: CategoryItem,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = category.imageRes),
            contentDescription = category.name,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = category.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            color = Color.Black
        )
    }
}

@Composable
fun BaristaRecommendsSection(navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Barista Recommends",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navController.navigate("productDetail/Java Chip Frappuccino")
                },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = "https://via.placeholder.com/80x80/8B4513/FFFFFF?text=☕",
                    contentDescription = "Java Chip Frappuccino",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(Color(0xFF00704A), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Java Chip Frappuccino",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                    Text(
                        "TALL (354 ml) • 392 kcal",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "₹441.00",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Button(
                    onClick = {
                        navController.navigate("productDetail/Java Chip Frappuccino")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00704A)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Add Item", color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("fullMenu") },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
            ),
            shape = RoundedCornerShape(25.dp)
        ) {
            Text("View Full Menu", color = Color.White)
        }
    }
}

@Composable
fun LearnMoreSection() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Learn more about the World of Coffee",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = "https://via.placeholder.com/400x250/8B4513/FFFFFF?text=Coffee+Machine",
                    contentDescription = "Coffee brewing",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.7f)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Coffee Culture",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 12.sp,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Art & Science of Coffee Brewing",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        "Master the perfect brew with Starbucks! Learn the art and science of coffee brewing.",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("Learn More", color = Color.Black)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
            ),
            shape = RoundedCornerShape(25.dp)
        ) {
            Text("Discover More", color = Color.White)
        }
    }
}

// Data Classes
data class FeaturedItem(
    val title: String,
    val description: String,
    val buttonText: String,
    val imageUrl: String,
    val startColor: Long,
    val endColor: Long
)

data class CategoryItem(
    val id: String,
    val name: String,
    val imageRes: Int
)

// Sample Data
val featuredItems = listOf(
    FeaturedItem(
        title = "Try the Blonde Roast!",
        description = "Bright, sweet citrus notes and a smooth body makes this a classic.",
        buttonText = "Know More",
        imageUrl = "https://via.placeholder.com/100x100/D4A574/FFFFFF?text=☕",
        startColor = 0xFFFDB813,
        endColor = 0xFFFFE0B3
    ),
    FeaturedItem(
        title = "Brewing for the Monsoon...",
        description = "Indulge in comforting new brews and monsoon-inspired merch, made for the season.",
        buttonText = "Try Now",
        imageUrl = "https://via.placeholder.com/100x100/87CEEB/FFFFFF?text=🌧️☕",
        startColor = 0xFF87CEEB,
        endColor = 0xFFE0F6FF
    )
)

val curationCategories = listOf(
    CategoryItem("bestseller", "Bestseller", R.drawable.bestseller_icon),
    CategoryItem("hotcoffee", "HotCoffee", R.drawable.hotcoffee),
    CategoryItem("Coldcoffee", "ColdCoffee", R.drawable.ic_coldcoffee),
    CategoryItem("sandwiches", "Sandwiches", R.drawable.ic_sandwich),
    CategoryItem("Burger", "Burger", R.drawable.ic_burger),
    CategoryItem("Desserts", "Desserts", R.drawable.ic_dessert),
    CategoryItem("coffee_at_home", "Coffee At Home", R.drawable.coffee_at_home_icon),
    CategoryItem("ready_to_eat", "Ready to Eat", R.drawable.ready_to_eat_icon)
)