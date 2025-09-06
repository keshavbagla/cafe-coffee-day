package com.example.Cosmora

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.Cosmora.ui.theme.CosmoraBlack
import com.example.Cosmora.ui.theme.CosmoraGreen



@Composable
fun CosmoraCoffeeScreen(
    onGetStartedClick: () -> Unit,
    navController: NavController
) {
    val greenBg = CosmoraGreen
    val whiteCard = Color.White

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(greenBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            // Logo
            Image(
                painter = painterResource(id = R.drawable.cosmora_logo),
                contentDescription = "Logo",
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            val cornerRadiusDp = 40.dp
            val cornerRadiusPx = with(LocalDensity.current) { cornerRadiusDp.toPx() }
            Box(
                modifier = Modifier
                    .width(330.dp)
                    .height(440.dp)
                    .graphicsLayer(
                        rotationZ = -8f, // Tilt the trapezium
                        shadowElevation = 16f,
                    )
                    .alpha(0.95f) // Fake blur via alpha
                    .clip(RoundedTrapeziumShape(cornerRadiusPx))
                    .background(whiteCard)
                    .align(Alignment.CenterHorizontally)
            ) {
                // Drinks images layered, tilted, with ice and mint
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    // Matcha drink
                    Image(
                        painter = painterResource(id = R.drawable.matcha_drink),
                        contentDescription = "Matcha Drink",
                        modifier = Modifier
                            .size(440.dp, 480.dp)
                            .offset(x = (-38).dp, y = 10.dp)
                            .graphicsLayer { rotationZ = -10f }
                    )
                    // Mint leaf under matcha drink
                    Image(
                        painter = painterResource(id = R.drawable.mint),
                        contentDescription = "Mint leaf",
                        modifier = Modifier
                            .size(36.dp)
                            .offset(x = (-64).dp, y = 78.dp)
                            .graphicsLayer { rotationZ = -25f }
                    )
                    // Strawberry drink
                    Image(
                        painter = painterResource(id = R.drawable.strawberry_drink),
                        contentDescription = "Strawberry Drink",
                        modifier = Modifier
                            .size(440.dp, 480.dp)
                            .offset(x = 38.dp, y = 10.dp)
                            .graphicsLayer { rotationZ = 20f }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
            SwipeIndicator()
            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Amazing Taste\nof coffee",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 36.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(0.7f))

            CustomTrapeziumButton(
                text = "Get Started",
                onClick = onGetStartedClick
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// Trapezium with rounded corners
fun RoundedTrapeziumShape(cornerRadiusPx: Float): GenericShape {
    return GenericShape { size, _ ->
        val r = cornerRadiusPx
        val w = size.width
        val h = size.height
        moveTo(r, 0f)
        lineTo(w - r, 0f)
        quadraticBezierTo(w, 0f, w, r)
        lineTo(w, h - r)
        quadraticBezierTo(w, h, w - r, h)
        lineTo(r, h)
        quadraticBezierTo(0f, h, 0f, h - r)
        lineTo(0f, r)
        quadraticBezierTo(0f, 0f, r, 0f)
        close()
    }
}

@Composable
fun SwipeIndicator() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .width(32.dp)
                .height(7.dp)
                .background(Color.White, RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(4.dp))
        repeat(3) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(Color.White.copy(alpha = 0.4f), shape = RoundedCornerShape(50))
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}

@Composable
fun CustomTrapeziumButton(text: String, onClick: () -> Unit) {
    val customShape = GenericShape { size, _ ->
        val r = 24f
        val w = size.width
        val h = size.height
        moveTo(0f, r)
        quadraticBezierTo(0f, 0f, r, 0f)
        lineTo(w - r, 0f)
        quadraticBezierTo(w, 0f, w, r)
        lineTo(w, h)
        lineTo(0f, h)
        lineTo(0f, r)
        close()
    }
    Button(
        onClick = onClick,
        shape = customShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = CosmoraBlack
        ),
        modifier = Modifier
            .width(220.dp)
            .height(56.dp)
            .clip(customShape)
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}