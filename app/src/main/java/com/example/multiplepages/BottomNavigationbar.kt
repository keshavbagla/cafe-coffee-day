package com.example.multiplepages

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.core.app.ActivityCompat
import java.util.Locale

@Composable
fun SimpleVoiceBottomNavigationBar(navController: NavController) {
    val context = LocalContext.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Voice states
    var isListening by remember { mutableStateOf(false) }
    var isOrderPressed by remember { mutableStateOf(false) }
    var speechRecognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }

    // Animation states
    val orderScale by animateFloatAsState(
        targetValue = if (isOrderPressed) 1.3f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "orderScale"
    )

    val pulseAnimation = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulseAnimation.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Voice recognition functions
    fun hasPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Enhanced Voice Recognition for BottomNavigationbar.kt
    // Replace your existing voice recognition functions with these:

    // Enhanced menu item matching using your existing menu data
    fun findMenuItem(spokenText: String): String? {
        val text = spokenText.lowercase().trim()

        // Get current route to determine category
        val currentCategory = when (currentRoute) {
            "hot_coffees" -> "hot_coffees"
            "cold_coffees" -> "cold_coffees"
            "sandwiches" -> "sandwiches"
            "burgers" -> "burgers"
            "desserts" -> "desserts"
            "food" -> "food"
            "merchandise" -> "merchandise"
            "tea_beverages" -> "tea_beverages"
            "breakfast" -> "breakfast"
            else -> "hot_coffees" // default to hot coffees
        }

        // Use your existing getSampleMenuItems function
        val menuItems = getSampleMenuItems(currentCategory)

        // Enhanced matching logic with variations for your actual menu items
        val itemVariations = mapOf(
            // Hot Coffees from your menu
            "cortado" to listOf("cortado", "date cortado", "spanish coffee"),
            "churro" to listOf("churro", "churro frappuccino"),
            "barista" to listOf("barista", "barista pride", "pride latte", "barista latte"),
            "americano" to listOf("americano", "caffe americano", "american", "black coffee"),
            "cappuccino" to listOf("cappuccino", "cappucino", "cap", "cappu"),
            "macchiato" to listOf("macchiato", "caramel macchiato", "mac"),
            "mocha" to listOf("mocha", "chocolate coffee", "choco coffee"),
            "hot chocolate" to listOf("hot chocolate", "chocolate", "cocoa", "signature hot chocolate"),

            // Cold Coffees from your menu
            "iced latte" to listOf("iced latte", "ice latte", "cold latte", "iced barista"),
            "cold brew" to listOf("cold brew", "cold", "ice coffee"),
            "frappuccino" to listOf("frappuccino", "frappe", "frap", "java chip", "caramel frappuccino"),
            "iced americano" to listOf("iced americano", "ice americano", "cold americano"),
            "iced mocha" to listOf("iced mocha", "ice mocha", "cold mocha"),
            "vanilla sweet cream" to listOf("vanilla sweet cream", "sweet cream", "vanilla cold brew"),

            // Sandwiches from your menu
            "paneer tikka" to listOf("paneer tikka", "paneer", "tikka sandwich", "indian sandwich"),
            "veg supreme" to listOf("veg supreme", "supreme sandwich", "veg sandwich"),
            "corn cheese" to listOf("corn cheese", "corn", "cheese sandwich", "sweet corn"),
            "grilled veggie" to listOf("grilled veggie", "grilled", "veggie sandwich"),
            "mushroom cheese" to listOf("mushroom cheese", "mushroom", "mushroom sandwich"),
            "italian herb" to listOf("italian herb", "italian", "herb sandwich"),

            // Burgers from your menu
            "aloo tikki" to listOf("aloo tikki", "aloo", "tikki burger", "potato burger"),
            "paneer makhani" to listOf("paneer makhani", "makhani burger", "paneer burger"),
            "veggie deluxe" to listOf("veggie deluxe", "deluxe burger", "veggie burger"),
            "classic veg" to listOf("classic veg", "classic", "veg burger"),
            "mexican" to listOf("mexican", "spicy mexican", "mexican burger"),
            "mushroom swiss" to listOf("mushroom swiss", "swiss burger", "mushroom burger"),

            // Desserts from your menu
            "brownie" to listOf("brownie", "chocolate brownie", "brownie supreme", "choco brownie"),
            "lava cake" to listOf("lava cake", "choco lava", "chocolate lava", "lava"),
            "cheesecake" to listOf("cheesecake", "cheese cake", "new york cheesecake"),
            "tiramisu" to listOf("tiramisu", "italian dessert"),
            "red velvet" to listOf("red velvet", "velvet cake", "red velvet cake"),
            "cookie" to listOf("cookie", "chocolate chip", "chip cookie"),
            "muffin" to listOf("muffin", "blueberry muffin", "blueberry"),

            // Food from your menu
            "chicken tikka wrap" to listOf("chicken tikka", "chicken wrap", "tikka wrap"),
            "paneer wrap" to listOf("paneer wrap", "wrap supreme", "paneer"),
            "caesar salad" to listOf("caesar salad", "caesar", "salad"),
            "greek salad" to listOf("greek salad", "greek", "mediterranean salad"),
            "croissant" to listOf("croissant", "french croissant"),
            "pain au chocolat" to listOf("pain au chocolat", "chocolate pastry", "chocolate croissant"),
            "bagel" to listOf("bagel", "cream cheese bagel", "bagel cream cheese"),

            // Merchandise from your menu
            "tumbler" to listOf("tumbler", "premium tumbler", "starbucks tumbler", "bottle"),
            "pike place" to listOf("pike place", "coffee beans", "pike place roast"),
            "ceramic mug" to listOf("ceramic mug", "mug", "classic mug", "cup"),
            "travel mug" to listOf("travel mug", "travel", "mug"),
            "french press" to listOf("french press", "press", "coffee press"),
            "grinder" to listOf("grinder", "coffee grinder", "burr grinder"),
            "gift card" to listOf("gift card", "card", "starbucks card"),
            "espresso roast" to listOf("espresso roast", "espresso beans", "dark roast"),

            // Tea & Beverages from your menu
            "masala chai" to listOf("masala chai", "chai latte", "chai", "indian tea", "masala"),
            "green tea latte" to listOf("green tea latte", "green tea", "matcha latte", "green"),
            "earl grey" to listOf("earl grey", "grey tea", "bergamot tea"),
            "chamomile" to listOf("chamomile", "herbal tea", "chamomile tea"),
            "iced green tea" to listOf("iced green tea", "ice green tea", "cold green tea"),
            "hot chocolate premium" to listOf("premium hot chocolate", "premium chocolate", "rich chocolate"),

            // Breakfast from your menu
            "avocado toast" to listOf("avocado toast", "avocado", "toast", "multigrain toast"),
            "egg cheese croissant" to listOf("egg cheese", "egg croissant", "breakfast croissant"),
            "oatmeal" to listOf("oatmeal", "oats", "oatmeal bowl", "hearty oatmeal"),
            "pancakes" to listOf("pancakes", "pancake stack", "stack", "fluffy pancakes"),
            "french toast" to listOf("french toast", "golden french toast", "berries toast"),
            "granola parfait" to listOf("granola parfait", "parfait", "yogurt parfait", "greek yogurt")
        )

        // First try exact name matching
        menuItems.forEach { item ->
            val itemName = item.name.lowercase()
            if (text.contains(itemName) || itemName.contains(text)) {
                return "${item.name} - ₹${item.price}"
            }
        }

        // Try variation matching
        menuItems.forEach { item ->
            val itemName = item.name.lowercase()

            // Check each variation
            itemVariations.forEach { (key, variants) ->
                if (itemName.contains(key)) {
                    variants.forEach { variant ->
                        if (text.contains(variant) || variant.contains(text)) {
                            return "${item.name} - ₹${item.price}"
                        }
                    }
                }
            }
        }

        // Try word-by-word matching for any remaining items
        val spokenWords = text.split(" ").filter { it.length > 2 }
        menuItems.forEach { item ->
            val itemWords = item.name.lowercase().split(" ").filter { it.length > 2 }

            var matchCount = 0
            spokenWords.forEach { spokenWord ->
                itemWords.forEach { itemWord ->
                    if (spokenWord.startsWith(itemWord) || itemWord.startsWith(spokenWord) ||
                        spokenWord.contains(itemWord) || itemWord.contains(spokenWord)) {
                        matchCount++
                    }
                }
            }

            // If at least half the words match, consider it a match
            if (matchCount > 0 && matchCount >= (spokenWords.size / 2.0)) {
                return "${item.name} - ₹${item.price}"
            }
        }

        // Try fuzzy matching for common misspellings
        val fuzzyMatches = mapOf(
            "cappucino" to "cappuccino",
            "expresso" to "espresso",
            "sandwitch" to "sandwich",
            "burgur" to "burger",
            "panir" to "paneer",
            "chocolat" to "chocolate",
            "vanila" to "vanilla",
            "caramell" to "caramel",
            "tikka" to "tikka",
            "makhni" to "makhani",
            "browny" to "brownie",
            "tiramsu" to "tiramisu"
        )

        var correctedText = text
        fuzzyMatches.forEach { (wrong, correct) ->
            correctedText = correctedText.replace(wrong, correct)
        }

        if (correctedText != text) {
            return findMenuItem(correctedText) // Recursive call with corrected text
        }

        return null
    }

    // Enhanced voice recognition with better error handling
    fun startVoiceRecognition() {
        if (!hasPermissions()) {
            println("🔊 Microphone permission required")
            return
        }

        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            println("🔊 Voice recognition not available on this device")
            return
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Say what you want to order...")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5) // Get more results for better matching
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 3000)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 3000)
        }

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                isListening = true
                println("🔊 Ready for speech - speak now!")
            }

            override fun onBeginningOfSpeech() {
                println("🔊 Speech detected...")
            }

            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                println("🔊 Speech ended, processing...")
            }

            override fun onError(error: Int) {
                isListening = false
                val errorMessage = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                    SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                    SpeechRecognizer.ERROR_NETWORK -> "Network error"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                    SpeechRecognizer.ERROR_NO_MATCH -> "No speech was heard - try speaking clearly"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service busy"
                    SpeechRecognizer.ERROR_SERVER -> "Server error"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input - try again"
                    else -> "Unknown error: $error"
                }
                println("🔊 Error: $errorMessage")
            }

            override fun onResults(results: Bundle?) {
                isListening = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val confidenceScores = results?.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)

                if (!matches.isNullOrEmpty()) {
                    println("🔊 Voice recognition results:")
                    matches.forEachIndexed { index, match ->
                        val confidence = confidenceScores?.getOrNull(index) ?: 0f
                        println("  $index: '$match' (confidence: ${String.format("%.2f", confidence)})")
                    }

                    // Try each recognition result in order of confidence
                    for ((index, spokenText) in matches.withIndex()) {
                        val foundItem = findMenuItem(spokenText)
                        if (foundItem != null) {
                            println("✅ Found item: $foundItem")
                            println("   Matched from: '$spokenText'")
                            println("   In category: $currentRoute")

                            // Here you would actually add the item to cart
                            // You can integrate with CartManager if you have it
                            println("🛒 Item would be added to cart")

                            // You can add navigation or show success feedback here
                            // navController.navigate("cart")
                            return
                        }
                    }

                    // If no exact match found, show what was heard and suggest alternatives
                    println("❌ No exact match found")
                    println("   You said: ${matches.joinToString(", ")}")
                    println("   Current category: $currentRoute")

                    // Show available items in current category for reference
                    val availableItems = getSampleMenuItems(when (currentRoute) {
                        "hot_coffees" -> "hot_coffees"
                        "cold_coffees" -> "cold_coffees"
                        "sandwiches" -> "sandwiches"
                        "burgers" -> "burgers"
                        "desserts" -> "desserts"
                        "food" -> "food"
                        "merchandise" -> "merchandise"
                        "tea_beverages" -> "tea_beverages"
                        "breakfast" -> "breakfast"
                        else -> "hot_coffees"
                    })

                    println("   💡 Available items in this category:")
                    availableItems.take(5).forEach { item ->
                        println("      - ${item.name}")
                    }
                } else {
                    println("🔊 No speech was recognized - please try again")
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                // Fixed: Use the correct constant name
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    println("🔊 Partial: ${matches[0]}")
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        speechRecognizer?.startListening(intent)
    }

    fun stopVoiceRecognition() {
        speechRecognizer?.stopListening()
        speechRecognizer?.destroy()
        isListening = false
    }

    NavigationBar(
        modifier = Modifier.height(90.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        // Home
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
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Home") }
        )

        // Rewards
        NavigationBarItem(
            selected = currentRoute == "rewards",
            onClick = {
                if (currentRoute != "rewards") {
                    navController.navigate("rewards") {
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
                    painter = painterResource(id = R.drawable.ic_rewards), // You need to add this
                    contentDescription = "Rewards",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Rewards") }
        )

        // Voice Order Button (Starbucks-style)
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .offset(y = (-8).dp)
                        .scale(if (isListening) pulseScale else orderScale)
                        .clip(CircleShape)
                        .background(
                            brush = if (isListening) {
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFFF6B6B),
                                        Color(0xFFFF8E53)
                                    )
                                )
                            } else {
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF00704A), // Starbucks green
                                        Color(0xFF00A862)
                                    )
                                )
                            }
                        )
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    isOrderPressed = true
                                    startVoiceRecognition()
                                    tryAwaitRelease()
                                    isOrderPressed = false
                                    stopVoiceRecognition()
                                },
                                onTap = {
                                    // Regular tap - navigate to hot_coffees instead of order
                                    if (currentRoute != "hot_coffees") {
                                        try {
                                            navController.navigate("hot_coffees") {
                                                popUpTo(navController.graph.startDestinationId) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        } catch (e: IllegalArgumentException) {
                                            println("Navigation error: ${e.message}")
                                            // Fallback - try navigating to home
                                            navController.navigate("home")
                                        }
                                    }
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Ripple effect when listening
                    if (isListening) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(
                                color = Color.White.copy(alpha = 0.3f),
                                radius = size.minDimension / 2 * pulseScale,
                                center = center
                            )
                        }
                    }

                    Icon(
                        painter = painterResource(
                            id = if (isListening) android.R.drawable.ic_btn_speak_now else R.drawable.ic_order
                        ),
                        contentDescription = if (isListening) "Listening..." else "Order",
                        modifier = Modifier.size(32.dp),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isListening) "Listening..." else "Order",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isListening) MaterialTheme.colorScheme.primary else LocalContentColor.current
                )
            }
        }

        // Payment
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
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Pay") }
        )

        // User
        NavigationBarItem(
            selected = currentRoute?.startsWith("user") == true,
            onClick = {
                if (currentRoute?.startsWith("user") != true) {
                    try {
                        navController.navigate("user") {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    } catch (e: IllegalArgumentException) {
                        println("User route not found in navigation graph")
                    }
                }
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_user),
                    contentDescription = "User",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("User") }
        )
    }
}