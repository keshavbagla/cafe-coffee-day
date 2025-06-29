package com.example.multiplepages

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.multiplepages.model.CafeMenuItem
import com.example.multiplepages.screens.PaymentOptionBottomSheet


@Composable
fun ConfirmOrderScreen(
    selectedItems: List<CafeMenuItem>,
    navController: NavController
) {
    var selectedOption by remember { mutableStateOf("Dine-In") }
    val totalAmount = selectedItems.sumOf { it.price.toDouble() }
    var showPaymentSheet by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Confirm Your Order", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(selectedItems) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = item.name)
                    Text(text = "₹${item.price}")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Select Order Type:", style = MaterialTheme.typography.labelLarge)
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selectedOption == "Dine-In",
                onClick = { selectedOption = "Dine-In" }
            )
            Text("Dine-In")

            Spacer(modifier = Modifier.width(16.dp))

            RadioButton(
                selected = selectedOption == "Parcel",
                onClick = { selectedOption = "Parcel" }
            )
            Text("Parcel")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Total Amount: ₹$totalAmount", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (selectedOption == "Dine-In") {
                    context.startActivity(Intent(context, ShoworderActivity::class.java))
                } else {
                    showPaymentSheet = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Proceed to Payment")
        }
    }

    if (showPaymentSheet) {
        PaymentOptionBottomSheet(
            onOptionSelected = { selectedOption ->
                showPaymentSheet = false
                navController.navigate("successSplash")
            },
            onDismiss = {
                showPaymentSheet = false
            }
        )
    }
}