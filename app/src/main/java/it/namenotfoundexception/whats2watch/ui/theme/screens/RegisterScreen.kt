package it.namenotfoundexception.whats2watch.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onRegisterClick: (username: String, password: String) -> Unit,
    onLoginClick: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Background movie posters
        AsyncImage(
            model = "https://i.ibb.co/qMBj6J9V/4839516-277052265.jpg",
            contentDescription = "Movie posters background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.4f // Dim the background
        )

        // App title at the top left
        Text(
            text = "Whats2Watch",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        )

        // Login button at the top right
        Button(
            onClick = { onLoginClick() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE53935)
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text("Login")
        }

        // Registration form in the center
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .align(Alignment.Center)
                .background(Color(0x99000000), RoundedCornerShape(16.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Create your profile",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Username field
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username", color = Color.White) },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedTextColor         = Color.White,
                    unfocusedTextColor       = Color.White,
                    focusedContainerColor    = Color.Transparent,
                    unfocusedContainerColor  = Color.Transparent,
                    focusedIndicatorColor    = Color.White,
                    unfocusedIndicatorColor  = Color.Gray,
                    cursorColor              = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = Color.White) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedTextColor         = Color.White,
                    unfocusedTextColor       = Color.White,
                    focusedContainerColor    = Color.Transparent,
                    unfocusedContainerColor  = Color.Transparent,
                    focusedIndicatorColor    = Color.White,
                    unfocusedIndicatorColor  = Color.Gray,
                    cursorColor              = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Confirm Password field
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Conferma Password", color = Color.White) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedTextColor         = Color.White,
                    unfocusedTextColor       = Color.White,
                    focusedContainerColor    = Color.Transparent,
                    unfocusedContainerColor  = Color.Transparent,
                    focusedIndicatorColor    = Color.White,
                    unfocusedIndicatorColor  = Color.Gray,
                    cursorColor              = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            // Already have an account text
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Do you already have an account? ",
                    color = Color.White
                )

                Text(
                    text = "Login",
                    color = Color(0xFFE53935),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onLoginClick() }
                )
            }

            // Register button
            Button(
                onClick = {
                    if (password == confirmPassword && password.isNotEmpty()) {
                        onRegisterClick(username, password)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53935)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "Register Now",
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Preview(
    name = "Register Screen",
    showBackground = true
)
@Composable
private fun RegisterScreenPreview() {
    RegisterScreen(
        onLoginClick = { /* no-op for preview */ },
        onRegisterClick = { username: String, password: String ->
            /* no-op for preview */
        }
    )
}