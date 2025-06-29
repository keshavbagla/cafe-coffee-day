package com.example.multiplepages.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Starbucks Inspired Palette
val CoffeeBrown = Color(0xFF4E342E)
val MochaCream = Color(0xFFD7CCC8)
val DeepGreen = Color(0xFF1B5E20)
val LightGreen = Color(0xFF81C784)
val RichBlack = Color(0xFF212121)
val SoftWhite = Color(0xFFF5F5F5)

// Light Theme
val LightColorScheme = lightColorScheme(
    primary = DeepGreen,        // Main accent (buttons, icons)
    secondary = CoffeeBrown,    // Secondary UI
    tertiary = LightGreen,      // Highlights
    background = SoftWhite,     // General background
    surface = SoftWhite,        // Cards, surfaces
    onPrimary = Color.White,    // Text/icon on primary
    onSecondary = Color.White,
    onTertiary = RichBlack,
    onBackground = RichBlack,
    onSurface = RichBlack
)

// Dark Theme
val DarkColorScheme = darkColorScheme(
    primary = LightGreen,
    secondary = MochaCream,
    tertiary = DeepGreen,
    background = RichBlack,
    surface = CoffeeBrown,
    onPrimary = RichBlack,
    onSecondary = RichBlack,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

