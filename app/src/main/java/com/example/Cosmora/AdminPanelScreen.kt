package com.example.Cosmora

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Cosmora.admin.MenuUpload
import com.example.Cosmora.manager.MenuManager
import com.example.Cosmora.model.CafeMenuItem
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AdminPanelScreen() {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val menuManager = remember { MenuManager(db) }

    // Map UI categories to database categories
    val categoryMap = mapOf(
        "Hot_Coffee" to "hot_coffees",
        "Cold_Coffee" to "cold_coffees",
        "Sandwiches_Burgers" to "sandwiches", // or "burgers" - you may need to split this
        "Dessert" to "desserts"
    )

    val categories = listOf("Hot_Coffee", "Cold_Coffee", "Sandwiches_Burgers", "Dessert")

    var name by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var size by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(categories[0]) }
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {

        Text("Admin Menu Upload", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = calories,
            onValueChange = { calories = it },
            label = { Text("Calories") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Price") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = size,
            onValueChange = { size = it },
            label = { Text("Size") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = imageUrl,
            onValueChange = { imageUrl = it },
            label = { Text("Image URL") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = {},
                label = { Text("Category") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(text = category) },
                        onClick = {
                            selectedCategory = category
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Validate input
                if (name.isBlank() || price.isBlank()) {
                    Toast.makeText(context, "Please fill in required fields", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                // Map UI category to database category
                val dbCategory = categoryMap[selectedCategory] ?: "food"

                val item = CafeMenuItem(
                    name = name.trim(),
                    price = price.trim(),
                    size = size.trim(),
                    calories = calories.trim(),
                    category = dbCategory,
                    description = "", // You might want to add a description field
                    imageUrl = imageUrl.trim(),
                    ingredients = "", // You might want to add an ingredients field
                    availability = true
                )

                // Use the correct function from MenuUpload
                MenuUpload.addMenuItem(dbCategory, item)

                Toast.makeText(context, "Item uploaded successfully!", Toast.LENGTH_SHORT).show()

                // Clear form
                name = ""
                calories = ""
                price = ""
                size = ""
                imageUrl = ""
                selectedCategory = categories[0]
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Upload")
        }
    }


}
