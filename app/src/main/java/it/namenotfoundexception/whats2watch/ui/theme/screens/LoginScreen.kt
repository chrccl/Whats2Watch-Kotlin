package it.namenotfoundexception.whats2watch.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import it.namenotfoundexception.whats2watch.R
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.AppTitle
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.CustomTextField
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.ErrorText
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.FormContainer
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.MovieBackgroundImage
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.PrimaryButton
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.SecondaryButton
import it.namenotfoundexception.whats2watch.viewmodels.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val currentUser by viewModel.currentUser.collectAsState()
    val authError by viewModel.authError.collectAsState()

    LaunchedEffect(currentUser) {
        if (currentUser != null && !isLoading) {
            onLoginSuccess()
        }
    }

    LaunchedEffect(authError) {
        if (authError != null) {
            isLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        MovieBackgroundImage()

        AppTitle(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        )

        SecondaryButton(
            text = "Register Now",
            onClick = onRegisterClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        )

        FormContainer(
            modifier = Modifier.align(Alignment.Center)
        ) {
            LoginForm(
                username = username,
                password = password,
                isLoading = isLoading,
                authError = authError,
                onUsernameChange = { username = it },
                onPasswordChange = { password = it },
                onLoginClick = {
                    if (username.isNotEmpty() && password.isNotEmpty()) {
                        isLoading = true
                        viewModel.login(username.trim(), password)
                    }
                }
            )
        }
    }
}

@Composable
private fun LoginForm(
    username: String,
    password: String,
    isLoading: Boolean,
    authError: String?,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.welcome),
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        ErrorText(authError)

        CustomTextField(
            value = username,
            onValueChange = onUsernameChange,
            label = stringResource(R.string.username),
            isEnabled = !isLoading
        )

        CustomTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = stringResource(R.string.password),
            isPassword = true,
            isEnabled = !isLoading,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        PrimaryButton(
            text = stringResource(R.string.login),
            onClick = onLoginClick,
            isLoading = isLoading
        )
    }
}

@Preview(
    name = "Login Screen",
    showBackground = true
)
@Composable
private fun LoginScreenPreview() {
    LoginScreen(
        onLoginSuccess = { /* no-op for preview */ },
        onRegisterClick = { /* no-op for preview */ }
    )
}