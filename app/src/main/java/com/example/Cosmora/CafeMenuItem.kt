package com.example.Cosmora.model

data class CafeMenuItem(
    val name: String = "",
    val price: String = "0",
    val size: String = "",
    val calories: String = "0",
    val category: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val ingredients: String = "",
    val availability: Boolean = true,
    val id: String = ""  // Moved id to the end since it's not used in constructor calls
) {
    // Computed property for backward compatibility if needed
    val isAvailable: Boolean get() = availability

    // Helper methods
    fun getPriceAsDouble(): Double = price.toDoubleOrNull() ?: 0.0
    fun getCaloriesAsInt(): Int = calories.toIntOrNull() ?: 0

    // Validation method
    fun isValid(): Boolean {
        return name.isNotBlank() &&
                price.isNotBlank() &&
                category.isNotBlank()
    }
}