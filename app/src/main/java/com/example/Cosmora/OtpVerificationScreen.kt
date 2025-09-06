package com.example.Cosmora.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun OTPVerificationScreen(
    phoneNumber: String,
    onOtpVerified: (String) -> Unit, // Pass the entered OTP
    onResendOtp: () -> Unit,
    onBack: () -> Unit,
    isLoading: Boolean = false,
    errorMessage: String = ""
) {
    var otpValues by remember { mutableStateOf(List(6) { "" }) }
    var currentIndex by remember { mutableStateOf(0) }
    var timeLeft by remember { mutableStateOf(60) }
    var canResend by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequesters = remember { List(6) { FocusRequester() } }

    // Timer for resend OTP
    LaunchedEffect(Unit) {
        while (timeLeft > 0&& !canResend) {
            delay(1000)
            timeLeft--
        }
        canResend = true
    }

    // Auto-focus first field
    LaunchedEffect(Unit) {
        focusRequesters[0].requestFocus()
    }

    // Reset OTP fields when error message changes
    LaunchedEffect(errorMessage) {
        if (errorMessage.isNotEmpty()) {
            otpValues = List(6) { "" }
            focusRequesters[0].requestFocus()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }

            TextButton(onClick = {
                // Skip functionality - you can handle this as needed
                // For now, it goes back to previous screen
                onBack()
            }) {
                Text("Skip", color = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Title
        Text(
            text = "We just sent you an SMS",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle with masked phone number
        Text(
            text = "Enter the security code we sent to\n${maskPhoneNumber(phoneNumber)}",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        // OTP Input Fields
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            otpValues.forEachIndexed { index, value ->
                OtpInputField(
                    value = value,
                    onValueChange = { newValue ->
                        // Only allow single numeric digit
                        val numericValue = newValue.filter { it.isDigit() }.take(1)

                        if (numericValue != value) {
                            val newOtpValues = otpValues.toMutableList()
                            newOtpValues[index] = numericValue
                            otpValues = newOtpValues

                            // Auto-focus next field when digit entered
                            if (numericValue.isNotEmpty() && index < 5) {
                                focusRequesters[index + 1].requestFocus()
                                currentIndex = index + 1
                            }

                            // Auto-focus previous field on delete
                            if (numericValue.isEmpty() && index > 0) {
                                focusRequesters[index - 1].requestFocus()
                                currentIndex = index - 1
                            }
                        }
                    },
                    focusRequester = focusRequesters[index],
                    isError = errorMessage.isNotEmpty(),
                    modifier = Modifier.weight(1f)
                )

                if (index < 5) {
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }
        }

        // Error message
        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Resend OTP
        TextButton(
            onClick = {
                if (canResend) {
                    onResendOtp()
                    timeLeft = 60
                    canResend = false
                    otpValues = List(6) { "" }
                    focusRequesters[0].requestFocus()
                }
            },
            enabled = canResend
        ) {
            Text(
                text = if (canResend) "Didn't receive a code?" else "Resend in ${timeLeft}s",
                color = if (canResend) MaterialTheme.colorScheme.primary else Color.Gray,
                fontSize = 16.sp,
                textDecoration = if (canResend) TextDecoration.Underline else null
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Done Button
        Button(
            onClick = {
                val enteredOtp = otpValues.joinToString("")
                // Validate that OTP is exactly 6 numeric digits
                if (enteredOtp.length == 6 && enteredOtp.all { it.isDigit() }) {
                    keyboardController?.hide()
                    onOtpVerified(enteredOtp)
                }
            },
            enabled = otpValues.all { it.isNotEmpty() && it.all { char -> char.isDigit() } } && !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (otpValues.all { it.isNotEmpty() })
                    Color(0xFF4CAF50) else Color.Gray
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Done",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun OtpInputField(
    value: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester,
    isError: Boolean = false,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            // Strictly filter only numeric characters and take first one
            val numericOnly = newValue.filter { it.isDigit() }.take(1)
            onValueChange(numericOnly)
        },
        modifier = modifier
            .width(48.dp)
            .height(56.dp)
            .focusRequester(focusRequester)
            .clip(RoundedCornerShape(8.dp)),
        textStyle = androidx.compose.ui.text.TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword // More secure for OTP
        ),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (isError) MaterialTheme.colorScheme.error
            else Color(0xFF4CAF50),
            unfocusedBorderColor = if (isError) MaterialTheme.colorScheme.error
            else Color.Gray,
            cursorColor = Color(0xFF4CAF50)
        )
    )
}

fun maskPhoneNumber(phoneNumber: String): String {
    return if (phoneNumber.length >= 10) {
        val lastFour = phoneNumber.takeLast(4)
        "*".repeat(phoneNumber.length - 4) + lastFour
    } else {
        phoneNumber
    }
}