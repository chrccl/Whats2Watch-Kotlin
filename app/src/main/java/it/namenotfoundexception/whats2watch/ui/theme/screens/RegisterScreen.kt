
package it.namenotfoundexception.whats2watch.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import it.namenotfoundexception.whats2watch.viewmodels.AuthViewModel

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel(),
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }

    val currentUser by viewModel.currentUser.collectAsState()
    val authError by viewModel.authError.collectAsState()

    // Osserva quando l'utente è registrato con successo
    LaunchedEffect(currentUser) {
        if (currentUser != null && !isLoading) {
            onRegisterSuccess()
        }
    }

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

            // Mostra errori se presenti
            val errorToShow = authError ?: validationError
            errorToShow?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Username field
            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    validationError = null // Reset validation error
                },
                label = { Text("Username", color = Color.White) },
                singleLine = true,
                enabled = !isLoading,
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
                onValueChange = {
                    password = it
                    validationError = null // Reset validation error
                },
                label = { Text("Password", color = Color.White) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                enabled = !isLoading,
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
                onValueChange = {
                    confirmPassword = it
                    validationError = null // Reset validation error
                },
                label = { Text("Conferma Password", color = Color.White) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                enabled = !isLoading,
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
                    when {
                        username.isEmpty() -> {
                            validationError = "Username is required"
                        }
                        password.isEmpty() -> {
                            validationError = "Password is required"
                        }
                        password != confirmPassword -> {
                            validationError = "Passwords do not match"
                        }
                        password.length < 4 -> {
                            validationError = "Password must be at least 4 characters"
                        }
                        else -> {
                            isLoading = true
                            validationError = null
                            viewModel.register(username.trim(), password)
                        }
                    }
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53935)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "Register Now",
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }
            }
        }
    }

    // Reset loading quando cambiano i valori osservati
    LaunchedEffect(authError) {
        if (authError != null) {
            isLoading = false
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
        onRegisterSuccess = { /* no-op for preview */ }
    )
}