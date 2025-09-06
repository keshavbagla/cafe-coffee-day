package com.example.Cosmora.manager

import com.example.Cosmora.model.CafeMenuItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MenuManager(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    // Get all items under a section (e.g., "hot_coffees")
    suspend fun getMenuItems(section: String): List<CafeMenuItem> {
        return try {
            val snapshot = db.collection("menu")
                .document(section)
                .collection("items")
                .get()
                .await()

            // Manual mapping to handle potential data inconsistencies
            snapshot.documents.mapNotNull { doc ->
                try {
                    CafeMenuItem(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        price = doc.getString("price") ?: "0",
                        size = doc.getString("size") ?: "",
                        calories = doc.getString("calories") ?: "0",
                        category = doc.getString("category") ?: section,
                        description = doc.getString("description") ?: "",
                        imageUrl = doc.getString("imageUrl") ?: "",
                        ingredients = doc.getString("ingredients") ?: "",
                        availability = doc.getBoolean("availability") ?: true // Fixed property name
                    )
                } catch (e: Exception) {
                    // Skip malformed documents
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Upload a menu item to Firebase
    suspend fun uploadMenuItem(section: String, category: String, item: CafeMenuItem) {
        val itemData = hashMapOf(
            "name" to item.name,
            "price" to item.price,
            "size" to item.size,
            "calories" to item.calories,
            "category" to category,
            "description" to item.description,
            "imageUrl" to item.imageUrl,
            "ingredients" to item.ingredients,
            "availability" to item.availability // Fixed property name
        )

        db.collection("menu")
            .document(section)
            .collection("items")
            .add(itemData)
            .await()
    }

    // Upload menu item with callback (non-suspend version)
    fun uploadMenuItemWithCallback(
        section: String,
        category: String,
        item: CafeMenuItem,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit = {}
    ) {
        val itemData = hashMapOf(
            "name" to item.name,
            "price" to item.price,
            "size" to item.size,
            "calories" to item.calories,
            "category" to category,
            "description" to item.description,
            "imageUrl" to item.imageUrl,
            "ingredients" to item.ingredients,
            "availability" to item.availability // Fixed property name
        )

        db.collection("menu")
            .document(section)
            .collection("items")
            .add(itemData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    // Update an existing menu item
    suspend fun updateMenuItem(section: String, itemId: String, item: CafeMenuItem) {
        val itemData = hashMapOf(
            "name" to item.name,
            "price" to item.price,
            "size" to item.size,
            "calories" to item.calories,
            "category" to item.category,
            "description" to item.description,
            "imageUrl" to item.imageUrl,
            "ingredients" to item.ingredients,
            "availability" to item.availability // Fixed property name
        )

        db.collection("menu")
            .document(section)
            .collection("items")
            .document(itemId)
            .set(itemData)
            .await()
    }

    // Delete a menu item
    suspend fun deleteMenuItem(section: String, itemId: String) {
        db.collection("menu")
            .document(section)
            .collection("items")
            .document(itemId)
            .delete()
            .await()
    }

    // Get a specific menu item by ID
    suspend fun getMenuItem(section: String, itemId: String): CafeMenuItem? {
        return try {
            val doc = db.collection("menu")
                .document(section)
                .collection("items")
                .document(itemId)
                .get()
                .await()

            if (doc.exists()) {
                CafeMenuItem(
                    id = doc.id,
                    name = doc.getString("name") ?: "",
                    price = doc.getString("price") ?: "0",
                    size = doc.getString("size") ?: "",
                    calories = doc.getString("calories") ?: "0",
                    category = doc.getString("category") ?: section,
                    description = doc.getString("description") ?: "",
                    imageUrl = doc.getString("imageUrl") ?: "",
                    ingredients = doc.getString("ingredients") ?: "",
                    availability = doc.getBoolean("availability") ?: true // Fixed property name
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    // Get all unique categories inside all sections
    suspend fun getAllCategories(): List<String> {
        val sections = listOf(
            "hot_coffees", "cold_coffees", "sandwiches", "burgers", "desserts",
            "food", "merchandise", "tea_beverages", "breakfast"
        )
        val categories = mutableSetOf<String>()

        try {
            for (section in sections) {
                val result = db.collection("menu")
                    .document(section)
                    .collection("items")
                    .get()
                    .await()

                result.documents.mapNotNullTo(categories) {
                    it.getString("category")
                }
            }
        } catch (e: Exception) {
            // Ignore error, just return whatever collected
        }

        return categories.toList()
    }

    // Get items by category across all sections
    suspend fun getItemsByCategory(categoryName: String): List<CafeMenuItem> {
        val sections = listOf(
            "hot_coffees", "cold_coffees", "sandwiches", "burgers", "desserts",
            "food", "merchandise", "tea_beverages", "breakfast"
        )
        val items = mutableListOf<CafeMenuItem>()

        try {
            for (section in sections) {
                val result = db.collection("menu")
                    .document(section)
                    .collection("items")
                    .whereEqualTo("category", categoryName)
                    .get()
                    .await()

                result.documents.mapNotNullTo(items) { doc ->
                    try {
                        CafeMenuItem(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            price = doc.getString("price") ?: "0",
                            size = doc.getString("size") ?: "",
                            calories = doc.getString("calories") ?: "0",
                            category = doc.getString("category") ?: section,
                            description = doc.getString("description") ?: "",
                            imageUrl = doc.getString("imageUrl") ?: "",
                            ingredients = doc.getString("ingredients") ?: "",
                            availability = doc.getBoolean("availability") ?: true // Fixed property name
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
            }
        } catch (e: Exception) {
            // Return whatever was collected
        }

        return items
    }

    // Search items by name
    suspend fun searchItemsByName(query: String): List<CafeMenuItem> {
        val sections = listOf(
            "hot_coffees", "cold_coffees", "sandwiches", "burgers", "desserts",
            "food", "merchandise", "tea_beverages", "breakfast"
        )
        val items = mutableListOf<CafeMenuItem>()

        try {
            for (section in sections) {
                val result = db.collection("menu")
                    .document(section)
                    .collection("items")
                    .get()
                    .await()

                result.documents.forEach { doc ->
                    val name = doc.getString("name") ?: ""
                    if (name.contains(query, ignoreCase = true)) {
                        try {
                            val item = CafeMenuItem(
                                id = doc.id,
                                name = name,
                                price = doc.getString("price") ?: "0",
                                size = doc.getString("size") ?: "",
                                calories = doc.getString("calories") ?: "0",
                                category = doc.getString("category") ?: section,
                                description = doc.getString("description") ?: "",
                                imageUrl = doc.getString("imageUrl") ?: "",
                                ingredients = doc.getString("ingredients") ?: "",
                                availability = doc.getBoolean("availability") ?: true // Fixed property name
                            )
                            items.add(item)
                        } catch (e: Exception) {
                            // Skip malformed documents
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // Return whatever was collected
        }

        return items
    }

    // Batch upload multiple items
    suspend fun uploadMultipleItems(section: String, category: String, items: List<CafeMenuItem>) {
        val batch = db.batch()
        val collectionRef = db.collection("menu").document(section).collection("items")

        items.forEach { item ->
            val itemData = hashMapOf(
                "name" to item.name,
                "price" to item.price,
                "size" to item.size,
                "calories" to item.calories,
                "category" to category,
                "description" to item.description,
                "imageUrl" to item.imageUrl,
                "ingredients" to item.ingredients,
                "availability" to item.availability // Fixed property name
            )

            val docRef = collectionRef.document()
            batch.set(docRef, itemData)
        }

        batch.commit().await()
    }
}

// Standalone upload function (keeping your original function name and style)
fun uploadMenu(
    section: String,
    category: String,
    item: CafeMenuItem,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit = {}
) {
    val itemData = hashMapOf(
        "name" to item.name,
        "price" to item.price,
        "size" to item.size,
        "calories" to item.calories,
        "category" to category,
        "description" to item.description,
        "imageUrl" to item.imageUrl,
        "ingredients" to item.ingredients,
        "availability" to item.availability // Fixed property name
    )

    FirebaseFirestore.getInstance()
        .collection("menu")
        .document(section)
        .collection("items")
        .add(itemData)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { exception -> onFailure(exception) }
}