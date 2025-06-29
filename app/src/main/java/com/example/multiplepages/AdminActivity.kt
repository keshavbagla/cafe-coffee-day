package com.example.multiplepages

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.firestore.FirebaseFirestore

class AdminActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Optional: Pre-initialize Firebase Firestore if needed
        val db = FirebaseFirestore.getInstance()

        // Show the Compose screen
        setContent {
            AdminPanelScreen()
        }
    }
}
