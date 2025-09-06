package com.example.Cosmora

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.firestore.FirebaseFirestore

class ShoworderActivity : ComponentActivity() {

    private var totalAmount = 0.0
    private var selectedItems = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_order)

        val firestore = FirebaseFirestore.getInstance()

        val paymentOptions = listOf("UPI", "Credit Card", "Debit Card", "Cash")
        val dropdown = findViewById<AutoCompleteTextView>(R.id.paymentDropdown)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, paymentOptions)
        dropdown.setAdapter(adapter)

        val tableNumbers = listOf("T1", "T2", "T3", "T4", "T5")
        val randomTable = tableNumbers.random()
        val orderNumber = (1000..9999).random()

        // ✅ Get selected items using correct key
        selectedItems = intent.getStringArrayListExtra("selectedItems") ?: ArrayList()

        val tableTextView = findViewById<TextView>(R.id.etnum)
        val orderTextView = findViewById<TextView>(R.id.ettable)

        tableTextView.text = "Table: $randomTable"
        orderTextView.text = "Order No: $orderNumber"

        calculateTotalAmount()

        val button = findViewById<Button>(R.id.button01)
        button.setOnClickListener {
            val intent = Intent(this, WatingQueue::class.java)
            intent.putExtra("tableNumber", randomTable)
            intent.putExtra("orderNumber", orderNumber)
            intent.putExtra("totalAmount", totalAmount)
            intent.putStringArrayListExtra("selectedItems", selectedItems)
            startActivity(intent)
        }
    }

    private fun calculateTotalAmount() {
        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "No items selected", Toast.LENGTH_SHORT).show()
            return
        }

        totalAmount = 0.0
        val completedItems = mutableSetOf<String>()
        val firestore = FirebaseFirestore.getInstance()

        val collections = listOf(
            "menu/beverages/Hot_coffee",
            "menu/beverages/Cold_coffee",
            "menu/food/Sandwiches_and_Burger",
            "menu/food/Dessert"
        )

        for (itemName in selectedItems) {
            var itemFound = false

            for (collectionPath in collections) {
                val pathParts = collectionPath.split("/")
                val mainDoc = pathParts[1]
                val subCol = pathParts[2]

                firestore.collection("menu")
                    .document(mainDoc)
                    .collection(subCol)
                    .whereEqualTo("name", itemName)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (!documents.isEmpty && !itemFound) {
                            itemFound = true
                            for (document in documents) {
                                val priceString = document.getString("price") ?: "₹0"
                                val numericPrice = priceString.replace("[^\\d.]".toRegex(), "").toDoubleOrNull() ?: 0.0
                                totalAmount += numericPrice
                                break
                            }
                        }

                        completedItems.add(itemName)
                        if (completedItems.size == selectedItems.size) {
                            runOnUiThread { updateTotalAmountDisplay() }
                        }
                    }
                    .addOnFailureListener {
                        completedItems.add(itemName)
                        if (completedItems.size == selectedItems.size) {
                            runOnUiThread { updateTotalAmountDisplay() }
                        }
                    }
            }
        }
    }

    private fun updateTotalAmountDisplay() {
        val totalAmountTextView = findViewById<TextView>(R.id.totalAmountText)
        totalAmountTextView?.text = "Total Amount: ₹${"%.2f".format(totalAmount)}"
    }
}
