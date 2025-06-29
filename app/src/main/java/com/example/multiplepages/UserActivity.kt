package com.example.multiplepages

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class UserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user) // ✅ using your layout

        val order1 = findViewById<EditText>(R.id.et1)
        val order2 = findViewById<EditText>(R.id.et2)
        val order3 = findViewById<EditText>(R.id.et3)
        val order4 = findViewById<EditText>(R.id.et4)
        val button = findViewById<Button>(R.id.btnOrder)

        button.setOnClickListener {
            val orders = listOf(
                order1.text.toString(),
                order2.text.toString(),
                order3.text.toString(),
                order4.text.toString()
            )
            Toast.makeText(this, "Order placed: ${orders.filter { it.isNotBlank() }}", Toast.LENGTH_LONG).show()
        }
    }
}
