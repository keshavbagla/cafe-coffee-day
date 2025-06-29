import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Menu {
    fun getUserData() {
        val db = Firebase.firestore

        val docRef = db.collection("menu")
            .document("beverages")
            .collection("Hot_coffee")
            .document("Laate")

        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val price = document.getString("price")
                    val size = document.getLong("size")
                    val calories = document.getLong("calories")
                    println("price: $price, size: $size, calories: $calories")
                } else {
                    println("No such document exists.")
                }
            }
            .addOnFailureListener { exception ->
                println("Failed to get document: $exception")
            }
    }
}
