package com.example.Cosmora
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment

import kotlinx.coroutines.delay




@Composable
fun WaitingQueueScreen(
    orderNumber: Int,
    tableNumber: String,
    modifier: Modifier = Modifier
) {
    val startTime = remember { System.currentTimeMillis() }
    var elapsedTime by remember { mutableStateOf("0:00") }
    var statusIndex by remember { mutableStateOf(0) }
    val statusSteps = listOf("Order Received", "Preparing", "Almost Ready", "Ready for Pickup")
    val currentStatus = remember { derivedStateOf { statusSteps[statusIndex.coerceAtMost(statusSteps.lastIndex)] } }

    // Update elapsed time & status
    LaunchedEffect(Unit) {
        while (statusIndex < statusSteps.size) {
            val elapsedMillis = System.currentTimeMillis() - startTime
            val minutes = (elapsedMillis / 60000).toInt()
            val seconds = ((elapsedMillis % 60000) / 1000).toInt()
            elapsedTime = String.format("%d:%02d", minutes, seconds)

            if (minutes >= 30) break // Max wait time

            delay(5000L) // Update every 5 sec
            if (statusIndex < statusSteps.lastIndex) statusIndex++
        }
    }

    // Shaking animation
    val infiniteTransition = rememberInfiniteTransition()
    val offsetX by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(150, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Order No: $orderNumber", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Table: $tableNumber", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(24.dp))

        Image(
            painter = painterResource(id = R.drawable.coffee),
            contentDescription = "Coffee Cup",
            modifier = Modifier
                .size(160.dp)
                .offset(x = offsetX.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))
        Text("Status: ${currentStatus.value}", fontSize = 20.sp, color = Color(0xFF6F4E37))
        Text("Time since ordered: $elapsedTime", fontSize = 16.sp)
    }
}
