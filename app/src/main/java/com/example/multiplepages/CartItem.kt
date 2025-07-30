package com.example.multiplepages.model

data class CartItem(
    val menuItem: CafeMenuItem,
    val quantity: Int = 1
)

data class CartState(
    val items: List<CartItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val totalItems: Int = 0
)