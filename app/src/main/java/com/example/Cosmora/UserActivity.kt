package com.example.Cosmora
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.Cosmora.admin.MenuUpload
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class UserActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    private val categoryMap = mapOf(
        "Hot Coffee" to "Hot_coffee",
        "Cold Coffee" to "Cold_coffee",
        "Sandwiches & Burger" to "Sandwiches_and_Burger",
        "Dessert" to "Dessert"
    )

    private val categoryList = categoryMap.keys.toList()
    private val dropdownStates = mutableMapOf<AutoCompleteTextView, Boolean>()
    private val currentCategories = mutableMapOf<AutoCompleteTextView, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        MenuUpload.uploadFullMenu()
        val et1 = findViewById<AutoCompleteTextView>(R.id.et1)
        val et2 = findViewById<AutoCompleteTextView>(R.id.et2)
        val et3 = findViewById<AutoCompleteTextView>(R.id.et3)
        val et4 = findViewById<AutoCompleteTextView>(R.id.et4)
        val orderButton = findViewById<Button>(R.id.btnOrder)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        setupDynamicDropdown(et1)
        setupDynamicDropdown(et2)
        setupDynamicDropdown(et3)
        setupDynamicDropdown(et4)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Home Clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_order -> {
                    Toast.makeText(this, "Order Clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_payment -> {
                    Toast.makeText(this, "Payment Clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_user -> {
                    Toast.makeText(this, "User Clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        orderButton.setOnClickListener {
            val orders = listOf(et1, et2, et3, et4)
                .map { it.text.toString().trim() }
                .filter { it.isNotEmpty() }

            if (orders.isEmpty()) {
                Toast.makeText(this, "Please select at least one item", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d("UserActivity", "Selected items: $orders")

            val orderMap = hashMapOf(
                "orders" to orders,
                "timestamp" to Timestamp.now()
            )

            db.collection("orders")
                .add(orderMap)
                .addOnSuccessListener { documentReference ->
                    Log.d("UserActivity", "Order placed with ID: ${documentReference.id}")
                    Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, ShoworderActivity::class.java)
                    intent.putStringArrayListExtra("selectedItems", ArrayList(orders))
                    startActivity(intent)
                }
                .addOnFailureListener { exception ->
                    Log.e("UserActivity", "Failed to place order", exception)
                    Toast.makeText(this, "Failed to place order: ${exception.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun setupDynamicDropdown(et: AutoCompleteTextView) {
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categoryList)
        et.setAdapter(categoryAdapter)
        et.setOnClickListener { et.showDropDown() }

        et.setOnItemClickListener { _, _, position, _ ->
            val selectedCategory = categoryList[position]
            val (mainDoc, subCol) = when (selectedCategory) {
                "Hot Coffee" -> "beverages" to "Hot_coffee"
                "Cold Coffee" -> "beverages" to "Cold_coffee"
                "Sandwiches & Burger" -> "food" to "Sandwiches_and_Burger"
                "Dessert" -> "food" to "Dessert"
                else -> return@setOnItemClickListener
            }

            et.isEnabled = false
            et.setText("Loading...")

            FirebaseFirestore.getInstance()
                .collection("menu")
                .document(mainDoc)
                .collection(subCol)
                .get()
                .addOnSuccessListener { documents ->
                    val itemList = documents.mapNotNull {
                        it.getString("name") ?: it.getString("itemName") ?: it.getString("title") ?: it.id
                    }

                    if (itemList.isNotEmpty()) {
                        runOnUiThread {
                            val itemAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, itemList)
                            et.setAdapter(itemAdapter)
                            et.setText("")  // Clear the previous text
                            et.hint = "Select Item from $selectedCategory"
                            et.isEnabled = true
                            et.showDropDown()  // 👈 Force the dropdown to show
                        }
                    } else {
                        runOnUiThread {
                            et.setAdapter(categoryAdapter)
                            et.setText("")
                            et.hint = "Select Category"
                            et.isEnabled = true
                            Toast.makeText(this, "No items found in $selectedCategory", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .addOnFailureListener {
                    runOnUiThread {
                        et.setAdapter(categoryAdapter)
                        et.setText("")
                        et.hint = "Select Category"
                        et.isEnabled = true
                        Toast.makeText(this, "Failed to load items", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

}