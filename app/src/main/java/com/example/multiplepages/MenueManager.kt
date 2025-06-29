import com.example.multiplepages.model.CafeMenuItem


import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MenuManager(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    // Get all items under a section (e.g., "hot_coffees")
    suspend fun getMenuItems(section: String): List<CafeMenuItem> {
        return try {
            db.collection("menu")
                .document(section)
                .collection("items")
                .get()
                .await()
                .toObjects(CafeMenuItem::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Get all unique categories inside all sections (optional helper)
    suspend fun getAllCategories(): List<String> {
        val sections = listOf("hot_coffees", "cold_coffees", "sandwiches", "burgers", "desserts")
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
}

fun UploadMenue(section: String, category: String, item: CafeMenuItem, onSuccess: () -> Unit) {
    val itemData = hashMapOf(
        "name" to item.name,
        "price" to item.price,
        "size" to item.size,
        "calories" to item.calories,
        "category" to category,
    )

    FirebaseFirestore.getInstance()
        .collection("menu")
        .document(section)
        .collection("items")
        .add(itemData)
        .addOnSuccessListener { onSuccess() }
}
