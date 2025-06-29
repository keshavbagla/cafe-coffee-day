package com.example.multiplepages.admin

import android.util.Log
import com.example.multiplepages.model.CafeMenuItem
import com.google.firebase.firestore.FirebaseFirestore

object MenuUpload {

    private val db = FirebaseFirestore.getInstance()

    fun uploadFullMenu() {
        // Upload Hot Coffees
        uploadCategory("hot_coffees", getHotCoffeeItems())

        // Upload Cold Coffees
        uploadCategory("cold_coffees", getColdCoffeeItems())

        // Upload Sandwiches
        uploadCategory("sandwiches", getSandwichItems())

        // Upload Burgers
        uploadCategory("burgers", getBurgerItems())

        // Upload Desserts
        uploadCategory("desserts", getDessertItems())

        // Upload Food (General Food Items)
        uploadCategory("food", getFoodItems())

        // Upload Merchandise
        uploadCategory("merchandise", getMerchandiseItems())

        // Upload Tea & Other Beverages
        uploadCategory("tea_beverages", getTeaBeverageItems())

        // Upload Breakfast Items
        uploadCategory("breakfast", getBreakfastItems())
    }

    private fun uploadCategory(categoryName: String, items: List<CafeMenuItem>) {
        val categoryRef = db.collection("menu").document(categoryName).collection("items")

        // Clear existing items first (optional)
        categoryRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                }

                // Then add new items
                for (item in items) {
                    val data = hashMapOf(
                        "name" to item.name,
                        "price" to item.price,
                        "size" to item.size,
                        "calories" to item.calories,
                        "category" to item.category,
                        "description" to item.description,
                        "imageUrl" to item.imageUrl,
                        "ingredients" to item.ingredients,
                        "availability" to item.availability,
                        "createdAt" to System.currentTimeMillis()
                    )

                    categoryRef.add(data)
                        .addOnSuccessListener {
                            Log.d("MenuUpload", "Item '${item.name}' uploaded to $categoryName.")
                        }
                        .addOnFailureListener { e ->
                            Log.e("MenuUpload", "Error uploading '${item.name}'", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("MenuUpload", "Error clearing category $categoryName", e)
            }
    }

    // Function to upload individual category (useful for testing)
    fun uploadSingleCategory(categoryName: String) {
        val items = when (categoryName) {
            "hot_coffees" -> getHotCoffeeItems()
            "cold_coffees" -> getColdCoffeeItems()
            "sandwiches" -> getSandwichItems()
            "burgers" -> getBurgerItems()
            "desserts" -> getDessertItems()
            "food" -> getFoodItems()
            "merchandise" -> getMerchandiseItems()
            "tea_beverages" -> getTeaBeverageItems()
            "breakfast" -> getBreakfastItems()
            else -> emptyList()
        }
        uploadCategory(categoryName, items)
    }

    // Helper functions to get items for each category
    private fun getHotCoffeeItems() = listOf(
        CafeMenuItem("Date Cortado", "383.25", "180ml", "168", "hot_coffees", "Double shot blonde espresso, paired with date flavoured sauce and steamed milk", "", "Espresso, date syrup, milk", true),
        CafeMenuItem("Churro Frappuccino", "445.50", "350ml", "368", "hot_coffees", "Signature Starbucks Frappuccino with churro flavour", "", "Frappuccino base, churro syrup, whipped cream", true),
        CafeMenuItem("Barista Pride Latte", "430.50", "240ml", "210", "hot_coffees", "Premium latte with signature blend", "", "Espresso, steamed milk, pride blend", true),
        CafeMenuItem("Signature Hot Chocolate", "330.75", "240ml", "280", "hot_coffees", "Rich and creamy hot chocolate", "", "Cocoa, milk, whipped cream", true),
        CafeMenuItem("Caffe Americano", "283.50", "350ml", "15", "hot_coffees", "Espresso shots topped with hot water", "", "Espresso, hot water", true),
        CafeMenuItem("Cappuccino", "304.50", "180ml", "120", "hot_coffees", "Espresso with steamed milk and foam", "", "Espresso, steamed milk, foam", true),
        CafeMenuItem("Caramel Macchiato", "398.25", "240ml", "190", "hot_coffees", "Espresso with vanilla syrup and caramel drizzle", "", "Espresso, vanilla syrup, steamed milk, caramel", true),
        CafeMenuItem("Mocha", "356.75", "240ml", "260", "hot_coffees", "Rich chocolate and espresso combination", "", "Espresso, chocolate syrup, steamed milk", true),
        CafeMenuItem("White Chocolate Mocha", "378.25", "240ml", "310", "hot_coffees", "Espresso with white chocolate and steamed milk", "", "Espresso, white chocolate syrup, steamed milk", true),
        CafeMenuItem("Vanilla Latte", "367.50", "240ml", "200", "hot_coffees", "Espresso with vanilla syrup and steamed milk", "", "Espresso, vanilla syrup, steamed milk", true)
    )

    private fun getColdCoffeeItems() = listOf(
        CafeMenuItem("Iced Barista Pride Latte", "430.50", "240ml", "180", "cold_coffees", "Chilled version of our signature latte", "", "Espresso, cold milk, ice, pride blend", true),
        CafeMenuItem("Iced Caffe Americano", "283.50", "350ml", "25", "cold_coffees", "Espresso shots with cold water over ice", "", "Espresso, cold water, ice", true),
        CafeMenuItem("Cold Brew", "325.50", "350ml", "5", "cold_coffees", "Slow-steeped cold brew coffee", "", "Cold brew concentrate, water", true),
        CafeMenuItem("Iced Latte", "346.50", "240ml", "130", "cold_coffees", "Espresso with cold milk over ice", "", "Espresso, cold milk, ice", true),
        CafeMenuItem("Java Chip Frappuccino", "472.50", "350ml", "420", "cold_coffees", "Frappuccino with chocolate chips", "", "Coffee, milk, chocolate chips, whipped cream", true),
        CafeMenuItem("Caramel Frappuccino", "456.75", "350ml", "380", "cold_coffees", "Blended coffee with caramel flavor", "", "Coffee, caramel syrup, milk, whipped cream", true),
        CafeMenuItem("Iced Mocha", "378.25", "350ml", "220", "cold_coffees", "Iced coffee with rich chocolate", "", "Espresso, chocolate syrup, cold milk, ice", true),
        CafeMenuItem("Vanilla Sweet Cream Cold Brew", "356.50", "350ml", "110", "cold_coffees", "Cold brew topped with vanilla sweet cream", "", "Cold brew, vanilla sweet cream", true),
        CafeMenuItem("Iced Caramel Macchiato", "398.25", "350ml", "250", "cold_coffees", "Iced version of our signature macchiato", "", "Espresso, vanilla syrup, cold milk, caramel drizzle, ice", true),
        CafeMenuItem("Nitro Cold Brew", "389.75", "350ml", "5", "cold_coffees", "Cold brew infused with nitrogen for creamy texture", "", "Nitro-infused cold brew", true)
    )

    private fun getSandwichItems() = listOf(
        CafeMenuItem("Paneer Tikka Sandwich", "289.50", "220g", "320", "sandwiches", "Spiced paneer tikka with mint chutney", "", "Paneer, tikka spices, mint chutney, bread", true),
        CafeMenuItem("Veg Supreme Sandwich", "245.75", "200g", "280", "sandwiches", "Mixed vegetables with cheese and herbs", "", "Mixed vegetables, cheese, herbs, bread", true),
        CafeMenuItem("Corn & Cheese Sandwich", "234.25", "190g", "260", "sandwiches", "Sweet corn with melted cheese", "", "Sweet corn, cheese, mayo, bread", true),
        CafeMenuItem("Grilled Veggie Sandwich", "267.50", "210g", "290", "sandwiches", "Grilled vegetables with pesto sauce", "", "Grilled vegetables, pesto, bread", true),
        CafeMenuItem("Mushroom & Cheese Sandwich", "298.75", "225g", "310", "sandwiches", "Sautéed mushrooms with melted cheese", "", "Mushrooms, cheese, herbs, bread", true),
        CafeMenuItem("Italian Herb Sandwich", "278.25", "200g", "270", "sandwiches", "Italian herbs with mozzarella", "", "Italian herbs, mozzarella, tomato, bread", true),
        CafeMenuItem("Club Sandwich Deluxe", "356.50", "280g", "420", "sandwiches", "Triple-decker sandwich with fresh veggies", "", "Multi-grain bread, vegetables, cheese, sauce", true),
        CafeMenuItem("Mediterranean Sandwich", "334.75", "250g", "380", "sandwiches", "Mediterranean vegetables with feta cheese", "", "Mediterranean vegetables, feta, olives, bread", true)
    )

    private fun getBurgerItems() = listOf(
        CafeMenuItem("Aloo Tikki Supreme Burger", "198.50", "250g", "380", "burgers", "Crispy aloo tikki with special sauce", "", "Aloo tikki, lettuce, tomato, special sauce, bun", true),
        CafeMenuItem("Paneer Makhani Burger", "267.75", "280g", "420", "burgers", "Paneer in rich makhani sauce", "", "Paneer, makhani sauce, onions, bun", true),
        CafeMenuItem("Veggie Deluxe Burger", "234.25", "260g", "350", "burgers", "Mixed veggie patty with cheese", "", "Veggie patty, cheese, lettuce, mayo, bun", true),
        CafeMenuItem("Classic Veg Burger", "189.50", "230g", "320", "burgers", "Traditional veg burger with fresh veggies", "", "Veg patty, tomato, lettuce, sauce, bun", true),
        CafeMenuItem("Spicy Mexican Burger", "245.75", "270g", "390", "burgers", "Spicy mexican-style veggie burger", "", "Spicy patty, jalapeños, cheese, salsa, bun", true),
        CafeMenuItem("Mushroom Swiss Burger", "278.25", "275g", "360", "burgers", "Mushroom patty with swiss cheese", "", "Mushroom patty, swiss cheese, onions, bun", true),
        CafeMenuItem("BBQ Veggie Burger", "298.50", "290g", "410", "burgers", "BBQ-flavored veggie patty with crispy onions", "", "BBQ veggie patty, crispy onions, lettuce, bun", true),
        CafeMenuItem("Mediterranean Burger", "312.75", "300g", "380", "burgers", "Mediterranean-style burger with herbs", "", "Herb patty, feta cheese, tomato, cucumber, bun", true)
    )

    private fun getDessertItems() = listOf(
        CafeMenuItem("Chocolate Brownie Supreme", "189.75", "120g", "450", "desserts", "Rich fudgy brownie with chocolate chips", "", "Dark chocolate, flour, butter, chocolate chips", true),
        CafeMenuItem("Choco Lava Cake", "234.50", "100g", "480", "desserts", "Warm chocolate cake with molten center", "", "Chocolate, flour, sugar, butter", true),
        CafeMenuItem("New York Cheesecake", "267.25", "140g", "520", "desserts", "Classic creamy New York-style cheesecake", "", "Cream cheese, graham crackers, vanilla", true),
        CafeMenuItem("Tiramisu", "298.75", "130g", "410", "desserts", "Italian coffee-flavored dessert", "", "Mascarpone, coffee, ladyfingers, cocoa", true),
        CafeMenuItem("Red Velvet Cake", "245.50", "120g", "380", "desserts", "Moist red velvet cake with cream cheese frosting", "", "Red velvet cake, cream cheese frosting", true),
        CafeMenuItem("Chocolate Chip Cookie", "134.25", "80g", "320", "desserts", "Freshly baked chocolate chip cookies", "", "Flour, chocolate chips, butter, sugar", true),
        CafeMenuItem("Blueberry Muffin", "156.75", "90g", "290", "desserts", "Soft muffin loaded with fresh blueberries", "", "Flour, blueberries, butter, sugar", true),
        CafeMenuItem("Lemon Tart", "198.50", "110g", "280", "desserts", "Tangy lemon tart with buttery crust", "", "Lemon curd, pastry, butter", true),
        CafeMenuItem("Apple Pie Slice", "223.75", "130g", "340", "desserts", "Classic apple pie with cinnamon", "", "Apples, cinnamon, pastry crust", true),
        CafeMenuItem("Chocolate Eclair", "167.50", "85g", "260", "desserts", "Choux pastry filled with chocolate cream", "", "Choux pastry, chocolate cream, icing", true)
    )

    private fun getFoodItems() = listOf(
        CafeMenuItem("Chicken Tikka Wrap", "298.50", "250g", "420", "food", "Grilled chicken tikka in soft tortilla", "", "Chicken tikka, tortilla, vegetables, sauce", true),
        CafeMenuItem("Paneer Wrap Supreme", "267.75", "230g", "380", "food", "Spiced paneer with fresh vegetables", "", "Paneer, vegetables, tortilla, mint chutney", true),
        CafeMenuItem("Caesar Salad", "234.25", "200g", "180", "food", "Fresh greens with caesar dressing", "", "Lettuce, croutons, parmesan, caesar dressing", true),
        CafeMenuItem("Greek Salad", "245.50", "190g", "160", "food", "Mediterranean salad with feta cheese", "", "Mixed greens, feta, olives, tomatoes", true),
        CafeMenuItem("Croissant", "134.75", "80g", "280", "food", "Buttery, flaky French croissant", "", "Flour, butter, yeast", true),
        CafeMenuItem("Pain au Chocolat", "167.25", "90g", "320", "food", "Chocolate-filled pastry", "", "Pastry, dark chocolate", true),
        CafeMenuItem("Bagel with Cream Cheese", "189.50", "120g", "340", "food", "Fresh bagel with cream cheese spread", "", "Bagel, cream cheese", true),
        CafeMenuItem("Quinoa Bowl", "312.75", "280g", "320", "food", "Healthy quinoa bowl with vegetables", "", "Quinoa, mixed vegetables, dressing", true),
        CafeMenuItem("Hummus & Pita", "198.25", "150g", "240", "food", "Creamy hummus served with pita bread", "", "Hummus, pita bread, olive oil", true),
        CafeMenuItem("Avocado Salad", "289.50", "220g", "220", "food", "Fresh avocado salad with mixed greens", "", "Avocado, mixed greens, vinaigrette", true)
    )

    private fun getMerchandiseItems() = listOf(
        CafeMenuItem("Starbucks Tumbler Premium", "1299.00", "473ml", "0", "merchandise", "Premium insulated tumbler for hot and cold beverages", "", "Stainless steel, BPA-free, double-wall insulation", true),
        CafeMenuItem("Pike Place Roast Coffee Beans", "1650.00", "250g", "0", "merchandise", "Medium roast whole bean coffee - our original blend", "", "100% Arabica beans, medium roast", true),
        CafeMenuItem("Ceramic Mug Classic", "899.00", "355ml", "0", "merchandise", "Classic Starbucks ceramic mug", "", "High-quality ceramic, dishwasher safe", true),
        CafeMenuItem("Travel Mug", "1456.00", "414ml", "0", "merchandise", "Leak-proof travel mug with ergonomic design", "", "Stainless steel, leak-proof lid", true),
        CafeMenuItem("French Press", "2299.00", "946ml", "0", "merchandise", "Premium French press for perfect coffee brewing", "", "Borosilicate glass, stainless steel", true),
        CafeMenuItem("Coffee Grinder", "3456.00", "N/A", "0", "merchandise", "Burr coffee grinder for consistent grind", "", "Ceramic burr, multiple grind settings", true),
        CafeMenuItem("Gift Card", "500.00", "N/A", "0", "merchandise", "Starbucks gift card - perfect for coffee lovers", "", "Digital or physical card available", true),
        CafeMenuItem("Espresso Roast Beans", "1789.00", "250g", "0", "merchandise", "Dark roast espresso beans", "", "100% Arabica beans, dark roast", true),
        CafeMenuItem("Cold Cup Reusable", "799.00", "473ml", "0", "merchandise", "Reusable cold cup with straw", "", "BPA-free plastic, reusable straw", true),
        CafeMenuItem("Coffee Beans Sampler", "2299.00", "3x100g", "0", "merchandise", "Variety pack of three different roasts", "", "Light, medium, dark roast beans", true),
        CafeMenuItem("Starbucks Apron", "1899.00", "N/A", "0", "merchandise", "Official Starbucks partner apron", "", "Cotton blend, adjustable straps", true),
        CafeMenuItem("Thermal Carafe", "3299.00", "1.5L", "0", "merchandise", "Keeps beverages hot for hours", "", "Stainless steel, thermal insulation", true)
    )

    private fun getTeaBeverageItems() = listOf(
        CafeMenuItem("Masala Chai Latte", "234.50", "240ml", "140", "tea_beverages", "Traditional Indian spiced chai with steamed milk", "", "Black tea, spices, steamed milk", true),
        CafeMenuItem("Green Tea Latte", "267.25", "240ml", "120", "tea_beverages", "Matcha green tea with steamed milk", "", "Matcha powder, steamed milk", true),
        CafeMenuItem("Earl Grey Tea", "189.75", "240ml", "5", "tea_beverages", "Classic bergamot-flavored black tea", "", "Earl Grey tea leaves, bergamot oil", true),
        CafeMenuItem("Chamomile Tea", "178.50", "240ml", "0", "tea_beverages", "Soothing herbal chamomile tea", "", "Chamomile flowers", true),
        CafeMenuItem("Iced Green Tea", "198.25", "350ml", "0", "tea_beverages", "Refreshing iced green tea", "", "Green tea, ice", true),
        CafeMenuItem("Hot Chocolate Premium", "298.75", "240ml", "320", "tea_beverages", "Rich premium hot chocolate with whipped cream", "", "Premium cocoa, milk, whipped cream", true),
        CafeMenuItem("Jasmine Green Tea", "198.75", "240ml", "0", "tea_beverages", "Delicate jasmine-scented green tea", "", "Jasmine green tea leaves", true),
        CafeMenuItem("Peppermint Tea", "178.50", "240ml", "0", "tea_beverages", "Refreshing peppermint herbal tea", "", "Peppermint leaves", true),
        CafeMenuItem("Iced Chai Latte", "245.50", "350ml", "120", "tea_beverages", "Chilled version of our popular chai latte", "", "Chai tea, cold milk, ice, spices", true),
        CafeMenuItem("White Hot Chocolate", "312.75", "240ml", "340", "tea_beverages", "Creamy white chocolate with steamed milk", "", "White chocolate, milk, whipped cream", true)
    )

    private fun getBreakfastItems() = listOf(
        CafeMenuItem("Avocado Toast", "345.50", "150g", "280", "breakfast", "Smashed avocado on multigrain toast", "", "Avocado, multigrain bread, lime, seasoning", true),
        CafeMenuItem("Egg & Cheese Croissant", "267.25", "180g", "420", "breakfast", "Scrambled eggs and cheese in buttery croissant", "", "Eggs, cheese, croissant", true),
        CafeMenuItem("Oatmeal Bowl", "189.75", "200g", "220", "breakfast", "Hearty oatmeal with fruits and nuts", "", "Oats, fruits, nuts, honey", true),
        CafeMenuItem("Pancakes Stack", "298.50", "250g", "520", "breakfast", "Fluffy pancakes with maple syrup", "", "Flour, eggs, milk, maple syrup", true),
        CafeMenuItem("French Toast", "334.75", "220g", "480", "breakfast", "Golden French toast with berries", "", "Bread, eggs, milk, berries, syrup", true),
        CafeMenuItem("Granola Parfait", "234.25", "180g", "280", "breakfast", "Greek yogurt layered with granola and berries", "", "Greek yogurt, granola, mixed berries", true),
        CafeMenuItem("Breakfast Burrito", "356.50", "300g", "480", "breakfast", "Scrambled eggs with vegetables in tortilla", "", "Eggs, vegetables, cheese, tortilla", true),
        CafeMenuItem("Acai Bowl", "378.75", "250g", "320", "breakfast", "Acai berry bowl topped with granola and fruits", "", "Acai, granola, fresh fruits, coconut", true),
        CafeMenuItem("English Muffin Sandwich", "245.50", "160g", "380", "breakfast", "Egg and cheese on toasted English muffin", "", "English muffin, egg, cheese", true),
        CafeMenuItem("Chia Pudding", "223.75", "150g", "240", "breakfast", "Creamy chia pudding with fresh fruits", "", "Chia seeds, almond milk, fruits", true)
    )

    // Utility function to clear all menu data (use with caution)
    fun clearAllMenuData() {
        val categories = listOf(
            "hot_coffees", "cold_coffees", "sandwiches", "burgers",
            "desserts", "food", "merchandise", "tea_beverages", "breakfast"
        )

        categories.forEach { category ->
            db.collection("menu").document(category).collection("items")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        document.reference.delete()
                    }
                    Log.d("MenuUpload", "Cleared category: $category")
                }
                .addOnFailureListener { e ->
                    Log.e("MenuUpload", "Error clearing category $category", e)
                }
        }
    }

    // Function to update a specific item
    fun updateMenuItem(categoryId: String, itemName: String, updatedItem: CafeMenuItem) {
        db.collection("menu")
            .document(categoryId)
            .collection("items")
            .whereEqualTo("name", itemName)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val data = hashMapOf(
                        "name" to updatedItem.name,
                        "price" to updatedItem.price,
                        "size" to updatedItem.size,
                        "calories" to updatedItem.calories,
                        "category" to updatedItem.category,
                        "description" to updatedItem.description,
                        "imageUrl" to updatedItem.imageUrl,
                        "ingredients" to updatedItem.ingredients,
                        "availability" to updatedItem.availability,
                        "updatedAt" to System.currentTimeMillis()
                    )

                    document.reference.update(data as Map<String, Any>)
                        .addOnSuccessListener {
                            Log.d("MenuUpload", "Item '$itemName' updated successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.e("MenuUpload", "Error updating item '$itemName'", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("MenuUpload", "Error finding item '$itemName'", e)
            }
    }

    // Function to add a single item to a category
    fun addMenuItem(categoryId: String, item: CafeMenuItem) {
        val data = hashMapOf(
            "name" to item.name,
            "price" to item.price,
            "size" to item.size,
            "calories" to item.calories,
            "category" to item.category,
            "description" to item.description,
            "imageUrl" to item.imageUrl,
            "ingredients" to item.ingredients,
            "availability" to item.availability,
            "createdAt" to System.currentTimeMillis()
        )

        db.collection("menu")
            .document(categoryId)
            .collection("items")
            .add(data)
            .addOnSuccessListener {
                Log.d("MenuUpload", "Item '${item.name}' added to $categoryId successfully")
            }
            .addOnFailureListener { e ->
                Log.e("MenuUpload", "Error adding item '${item.name}' to $categoryId", e)
            }
    }

    // Function to delete a specific item
    fun deleteMenuItem(categoryId: String, itemName: String) {
        db.collection("menu")
            .document(categoryId)
            .collection("items")
            .whereEqualTo("name", itemName)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                        .addOnSuccessListener {
                            Log.d("MenuUpload", "Item '$itemName' deleted successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.e("MenuUpload", "Error deleting item '$itemName'", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("MenuUpload", "Error finding item '$itemName' for deletion", e)
            }
    }

    // Function to check if menu data exists
    fun checkMenuData(callback: (Boolean) -> Unit) {
        db.collection("menu")
            .document("hot_coffees")
            .collection("items")
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                callback(documents.size() > 0)
            }
            .addOnFailureListener {
                callback(false)
            }
    }
}