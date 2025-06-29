package com.example.multiplepages

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.ComponentActivity


class ShoworderActivity :ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("ShoworderActivity", "onCreate called")
        setContentView(R.layout.activity_show_order)

        // Make sure these IDs match exactly with XML
        val paymentOptions = listOf("UPI", "Credit Card", "Debit Card", "Cash")
        val dropdown = findViewById<AutoCompleteTextView>(R.id.paymentDropdown)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, paymentOptions)
        dropdown.setAdapter(adapter)
    }


}