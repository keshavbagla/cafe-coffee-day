package com.example.multiplepages.manager

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.multiplepages.model.CafeMenuItem
import com.example.multiplepages.model.CartItem
import com.example.multiplepages.model.CartState

object CartManager {
    var cartState by mutableStateOf(CartState())
        private set

    fun addItem(menuItem: CafeMenuItem) {
        val existingItemIndex = cartState.items.indexOfFirst { 
            it.menuItem.name == menuItem.name 
        }
        
        val updatedItems = if (existingItemIndex >= 0) {
            cartState.items.toMutableList().apply {
                this[existingItemIndex] = this[existingItemIndex].copy(
                    quantity = this[existingItemIndex].quantity + 1
                )
            }
        } else {
            cartState.items + CartItem(menuItem, 1)
        }
        
        updateCartState(updatedItems)
    }
    
    fun removeItem(menuItem: CafeMenuItem) {
        val existingItemIndex = cartState.items.indexOfFirst { 
            it.menuItem.name == menuItem.name 
        }
        
        if (existingItemIndex >= 0) {
            val currentItem = cartState.items[existingItemIndex]
            val updatedItems = if (currentItem.quantity > 1) {
                cartState.items.toMutableList().apply {
                    this[existingItemIndex] = this[existingItemIndex].copy(
                        quantity = this[existingItemIndex].quantity - 1
                    )
                }
            } else {
                cartState.items.filterIndexed { index, _ -> index != existingItemIndex }
            }
            
            updateCartState(updatedItems)
        }
    }
    
    fun getItemQuantity(menuItem: CafeMenuItem): Int {
        return cartState.items.find { it.menuItem.name == menuItem.name }?.quantity ?: 0
    }
    
    private fun updateCartState(items: List<CartItem>) {
        val totalAmount = items.sumOf { 
            it.menuItem.price.toDoubleOrNull()?.times(it.quantity) ?: 0.0 
        }
        val totalItems = items.sumOf { it.quantity }
        
        cartState = CartState(
            items = items,
            totalAmount = totalAmount,
            totalItems = totalItems
        )
    }
    
    fun clearCart() {
        cartState = CartState()
    }
}