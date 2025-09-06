package com.example.Cosmora


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class WatingQueue : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tableNumber = intent.getStringExtra("tableNumber") ?: "N/A"
        val orderNumber = intent.getIntExtra("orderNumber", -1)

        setContent {
            WaitingQueueScreen(orderNumber = orderNumber, tableNumber = tableNumber)
        }
    }
}
