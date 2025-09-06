package com.example.Cosmora

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

// Data class for menu items
data class MenuItem(val name: String, val price: Int)

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

    // Fixed route mapping - Check longer phrases first
    fun getRouteForCategory(category: String): String {
        val text = category.lowercase().trim()

        // Check for multi-word phrases first (most specific to least specific)
        return when {
            text.contains("cold coffee") || text.contains("cold coffees") -> "ColdCoffeesScreen"
            text.contains("iced coffee") || text.contains("ice coffee") -> "ColdCoffeesScreen"
            text.contains("cold brew") -> "ColdCoffeesScreen"
            text.contains("frappuccino") || text.contains("frappe") -> "ColdCoffeesScreen"
            text.contains("iced latte") || text.contains("ice latte") -> "ColdCoffeesScreen"
            text.contains("iced americano") || text.contains("ice americano") -> "ColdCoffeesScreen"
            text.contains("iced") -> "ColdCoffeesScreen"
            text.contains("cold") -> "ColdCoffeesScreen"

            text.contains("hot coffee") || text.contains("hot coffees") -> "HotCoffeesScreen"
            text.contains("americano") || text.contains("cappuccino") -> "HotCoffeesScreen"
            text.contains("latte") || text.contains("espresso") -> "HotCoffeesScreen"
            text.contains("mocha") || text.contains("macchiato") -> "HotCoffeesScreen"
            text.contains("coffee") -> "HotCoffeesScreen" // Generic coffee goes to hot

            text.contains("veggie sandwich") || text.contains("paneer sandwich") -> "SandwichesScreen"
            text.contains("sandwich") || text.contains("sandwiches") -> "SandwichesScreen"
            text.contains("paneer") && !text.contains("burger") -> "SandwichesScreen"
            text.contains("grilled") && !text.contains("burger") -> "SandwichesScreen"

            text.contains("veggie burger") || text.contains("aloo tikki") -> "BurgersScreen"
            text.contains("burger") || text.contains("burgers") -> "BurgersScreen"
            text.contains("tikki") || text.contains("deluxe") -> "BurgersScreen"

            text.contains("dessert") || text.contains("desserts") -> "DessertsScreen"
            text.contains("brownie") || text.contains("cake") -> "DessertsScreen"
            text.contains("cookie") || text.contains("sweet") -> "DessertsScreen"
            text.contains("chocolate") && !text.contains("coffee") -> "DessertsScreen"

            else -> "HotCoffeesScreen" // Default fallback
        }
    }

    // Enhanced menu item matching
    fun findMenuItem(spokenText: String): Pair<String, String>? {
        val text = spokenText.lowercase().trim()
        println("🔍 Searching for: '$text'")

        // Enhanced matching logic with category keywords - Order matters!
        val categoryKeywords = mapOf(
            "ColdCoffeesScreen" to listOf("cold coffee", "cold coffees", "iced coffee", "ice coffee", "cold brew", "frappuccino", "frappe", "iced latte", "ice latte", "iced americano", "ice americano", "iced", "cold", "frozen"),
            "HotCoffeesScreen" to listOf("hot coffee", "hot coffees", "americano", "cappuccino", "latte", "espresso", "mocha", "cortado", "macchiato", "hot"),
            "SandwichesScreen" to listOf("sandwich", "sandwiches", "paneer sandwich", "veggie sandwich", "grilled sandwich", "cheese sandwich", "paneer", "grilled"),
            "BurgersScreen" to listOf("burger", "burgers", "tikki", "aloo tikki", "veggie burger", "deluxe burger", "mexican burger", "deluxe"),
            "DessertsScreen" to listOf("dessert", "desserts", "cake", "brownie", "cookie", "sweet", "muffin", "cheesecake", "tiramisu")
        )

        // FIRST: Check if user wants to navigate to a different category
        for ((route, keywords) in categoryKeywords) {
            // Sort keywords by length (longest first) to match most specific phrases first
            val sortedKeywords = keywords.sortedByDescending { it.length }
            for (keyword in sortedKeywords) {
                if (text.contains(keyword)) {
                    println("🎯 Found category match: $route for keyword: '$keyword' (current: $currentRoute)")
                    if (route != currentRoute) {
                        return Pair(route, "category")
                    }
                    // If we're already on the right screen, continue to item search
                    break
                }
            }
        }

        // SECOND: If no category match, try to find items in current category
        val currentCategory = when (currentRoute) {
            "HotCoffeesScreen" -> "hot_coffees"
            "ColdCoffeesScreen" -> "cold_coffees"
            "SandwichesScreen" -> "sandwiches"
            "BurgersScreen" -> "burgers"
            "DessertsScreen" -> "desserts"
            else -> "hot_coffees"
        }

        val currentMenuItems = getSampleMenuItems(currentCategory)

        // Item name variations for better matching
        val itemVariations = mapOf(
            "cortado" to listOf("cortado", "date cortado"),
            "churro" to listOf("churro", "churro frappuccino"),
            "americano" to listOf("americano", "american", "black coffee"),
            "cappuccino" to listOf("cappuccino", "cappucino", "cap"),
            "macchiato" to listOf("macchiato", "caramel macchiato"),
            "mocha" to listOf("mocha", "chocolate coffee"),
            "latte" to listOf("latte", "coffee latte"),
            "frappuccino" to listOf("frappuccino", "frappe", "frap"),
            "sandwich" to listOf("sandwich", "sandwitch"),
            "burger" to listOf("burger", "burgur"),
            "brownie" to listOf("brownie", "browny"),
            "paneer" to listOf("paneer", "panir"),
            "tikka" to listOf("tikka", "tika"),
            "veggie" to listOf("veggie", "veg", "vegetable")
        )

        // Search in current category
        for (item in currentMenuItems) {
            val itemName = item.name.lowercase()

            // Direct match
            if (text.contains(itemName) || itemName.contains(text)) {
                return Pair("${item.name} - ₹${item.price}", "item")
            }

            // Variation match
            for ((key, variants) in itemVariations) {
                if (itemName.contains(key)) {
                    for (variant in variants) {
                        if (text.contains(variant)) {
                            return Pair("${item.name} - ₹${item.price}", "item")
                        }
                    }
                }
            }

            // Word-by-word matching
            val spokenWords = text.split(" ").filter { it.length > 2 }
            val itemWords = itemName.split(" ").filter { it.length > 2 }

            var matchCount = 0
            for (spokenWord in spokenWords) {
                for (itemWord in itemWords) {
                    if (spokenWord.startsWith(itemWord) || itemWord.startsWith(spokenWord)) {
                        matchCount++
                    }
                }
            }

            if (matchCount > 0 && matchCount >= (spokenWords.size / 2.0)) {
                return Pair("${item.name} - ₹${item.price}", "item")
            }
        }

        return null
    }

    // Enhanced voice recognition
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
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false) // Disable partial results to reduce noise
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2000) // Wait 2 seconds
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 2000)
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
                    SpeechRecognizer.ERROR_NO_MATCH -> {
                        println("🔊 No clear speech detected - staying on current screen")
                        return // Don't navigate anywhere
                    }
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                        println("🔊 No speech input detected - staying on current screen")
                        return // Don't navigate anywhere
                    }
                    SpeechRecognizer.ERROR_NETWORK -> "Network error - check connection"
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission required"
                    else -> "Voice recognition error: $error"
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

                    // Filter out low confidence results and very short/unclear speech
                    val filteredMatches = matches.filterIndexed { index, match ->
                        val confidence = confidenceScores?.getOrNull(index) ?: 0f
                        val isLongEnough = match.trim().length >= 3 // At least 3 characters
                        val hasGoodConfidence = confidence > 0.3f || confidenceScores == null // Accept if no confidence scores

                        if (!isLongEnough) {
                            println("  ❌ Filtered out '$match' - too short")
                        }
                        if (!hasGoodConfidence) {
                            println("  ❌ Filtered out '$match' - low confidence ($confidence)")
                        }

                        isLongEnough && hasGoodConfidence
                    }

                    if (filteredMatches.isEmpty()) {
                        println("🔊 All results filtered out - no clear speech detected")
                        println("   Staying on current screen: $currentRoute")
                        return
                    }

                    // Try each filtered recognition result
                    for (spokenText in filteredMatches) {
                        val result = findMenuItem(spokenText)
                        if (result != null) {
                            val (foundItem, type) = result

                            when (type) {
                                "category" -> {
                                    // Navigate to the found category
                                    try {
                                        println("🎯 Navigating to: $foundItem")
                                        navController.navigate(foundItem) {
                                            popUpTo(navController.graph.startDestinationId) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                        return
                                    } catch (e: Exception) {
                                        println("❌ Navigation error: ${e.message}")
                                    }
                                }
                                "item" -> {
                                    println("✅ Found item: $foundItem")
                                    println("🛒 Item would be added to cart")
                                    // Here you can add to cart logic
                                    return
                                }
                            }
                        }
                    }

                    // If NO match found at all, don't navigate anywhere
                    println("❌ No match found for any of: ${filteredMatches.joinToString(", ")}")
                    println("   Staying on current screen: $currentRoute")
                } else {
                    println("🔊 No speech was recognized - staying on current screen")
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
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
                    painter = painterResource(id = R.drawable.ic_rewards),
                    contentDescription = "Rewards",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Rewards") }
        )

        // Voice Order Button
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
                                        Color(0xFF00704A),
                                        Color(0xFF00A862)
                                    )
                                )
                            }
                        )
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    // Only start voice recognition on press and hold
                                    isOrderPressed = true
                                    startVoiceRecognition()
                                    tryAwaitRelease()
                                    isOrderPressed = false
                                    stopVoiceRecognition()
                                }
                                // REMOVED onTap - no automatic navigation on simple tap
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
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