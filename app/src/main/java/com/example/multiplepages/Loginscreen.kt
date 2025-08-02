package com.example.multiplepages

 import android.widget.Toast
 import androidx.compose.foundation.layout.*
 import androidx.compose.foundation.layout.Column
 import androidx.compose.foundation.layout.Row
 import androidx.compose.foundation.layout.Spacer
 import androidx.compose.foundation.text.KeyboardOptions
 import androidx.compose.material3.Button
 import androidx.compose.material3.Icon
 import androidx.compose.material3.IconButton
 import androidx.compose.runtime.Composable
 import androidx.compose.runtime.getValue
 import androidx.compose.runtime.mutableStateOf
 import androidx.compose.runtime.remember
 import androidx.compose.runtime.setValue
 import androidx.compose.ui.Alignment
 import androidx.compose.ui.graphics.Color
 import androidx.compose.ui.platform.LocalContext
 import androidx.compose.ui.res.painterResource
 import androidx.compose.ui.text.font.FontWeight
 import androidx.compose.ui.text.input.KeyboardType
 import androidx.compose.ui.text.input.PasswordVisualTransformation
 import androidx.compose.ui.unit.dp
 import androidx.compose.ui.unit.sp
 import androidx.compose.foundation.clickable
 import androidx.compose.material3.Checkbox
 import androidx.compose.material3.OutlinedTextField
 import androidx.compose.material3.Text
 import androidx.compose.ui.Modifier

@Composable
fun LoginScreen(
    onLogin: (String, String) -> Unit,
    onGoogleLogin: () -> Unit,
    onFacebookLogin: () -> Unit,
    onSignupClick: () -> Unit,
    isAdminPassword: (String) -> Boolean
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Login", fontSize = 30.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email or Phone") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = { rememberMe = it }
            )
            Text("Remember Me", modifier = Modifier.clickable { rememberMe = !rememberMe })

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Forgot Password?",
                color = Color.Blue,
                modifier = Modifier.clickable {
                    Toast.makeText(context, "Password recovery not implemented", Toast.LENGTH_SHORT).show()
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (isAdminPassword(password)) {
                    Toast.makeText(context, "Admin Login", Toast.LENGTH_SHORT).show()
                } else {
                    onLogin(email, password)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onGoogleLogin) {
                Icon(painter = painterResource(id = R.drawable.ic_google), contentDescription = "Google Login")
            }
            IconButton(onClick = onFacebookLogin) {
                Icon(painter = painterResource(id = R.drawable.ic_facebook), contentDescription = "Facebook Login")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row {
            Text("Don't have an account?")
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Sign Up",
                color = Color.Blue,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onSignupClick() }
            )
        }
    }
}
