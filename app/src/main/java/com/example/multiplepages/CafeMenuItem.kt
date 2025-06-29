package com.example.multiplepages.model


data class CafeMenuItem(
    val name: String = "",
    val price: String = "",
    val size: String = "", // in ml or L
    val calories: String = "",
    val category: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val ingredients: String = "",
    val availability: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
