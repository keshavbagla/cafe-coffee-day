package com.example.multiplepages

import com.example.multiplepages.model.CafeMenuItem
import com.google.firebase.firestore.FirebaseFirestore

class MenuManager(private val db: FirebaseFirestore) {

    fun uploadMenuItem(category: String, item: CafeMenuItem, onComplete: () -> Unit = {}) {
        val documentName = item.name.replace(" ", "")
        db.collection("menu")
            .document("beverages")
            .collection(category)
            .document(documentName)
            .set(item)
            .addOnSuccessListener { onComplete() }
    }

    fun getMenuItems(category: String, onResult: (List<CafeMenuItem>) -> Unit) {
        db.collection("menu")
            .document("beverages")
            .collection(category)
            .get()
            .addOnSuccessListener { snapshot ->
                val items = snapshot.documents.mapNotNull { it.toObject(CafeMenuItem::class.java) }
                onResult(items)
            }
    }
}

