package com.example.multiplepages

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.multiplepages.model.CafeMenuItem
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun AdminPanelScreen() {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val menuManager = remember { MenuManager(db) }

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

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = calories, onValueChange = { calories = it }, label = { Text("Calories") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = size, onValueChange = { size = it }, label = { Text("Size") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Image URL") }, modifier = Modifier.fillMaxWidth())

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
                val item = com.example.multiplepages.model.CafeMenuItem(
                    name = name,
                    price = price.toIntOrNull() ?: 0,
                    size_ml = size.toIntOrNull() ?: 0,
                    imageUrl = imageUrl,
                    calories = calories.toIntOrNull() ?: 0
                )

                menuManager.uploadMenuItem(selectedCategory, item) {
                    Toast.makeText(context, "Item uploaded successfully!", Toast.LENGTH_SHORT).show()
                }

                name = ""; calories = ""; price = ""; size = ""; imageUrl = ""
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Upload")
        }
    }
}
