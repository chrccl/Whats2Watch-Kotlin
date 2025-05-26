package it.namenotfoundexception.whats2watch.ui.theme.screens

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.AppTitle
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.CustomTextField
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.ErrorText
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.FormContainer
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.MovieBackgroundImage
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.PrimaryButton
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.SecondaryButton
import it.namenotfoundexception.whats2watch.viewmodels.AuthViewModel
import it.namenotfoundexception.whats2watch.R

@Composable
fun RegisterScreen(
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

    LaunchedEffect(currentUser) {
        if (currentUser != null && !isLoading) {
            onRegisterSuccess()
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
            text = stringResource(R.string.login),
            onClick = onLoginClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        )

        FormContainer(
            modifier = Modifier.align(Alignment.Center)
        ) {
            val context = LocalContext.current
            RegisterForm(
                username = username,
                password = password,
                confirmPassword = confirmPassword,
                isLoading = isLoading,
                authError = authError,
                validationError = validationError,
                onUsernameChange = {
                    username = it
                    validationError = null
                },
                onPasswordChange = {
                    password = it
                    validationError = null
                },
                onConfirmPasswordChange = {
                    confirmPassword = it
                    validationError = null
                },
                onRegisterClick = {
                    val error = validateRegistrationForm(context, username, password, confirmPassword)
                    if (error != null) {
                        validationError = error
                    } else {
                        isLoading = true
                        validationError = null
                        viewModel.register(username.trim(), password)
                    }
                }
            )
        }
    }
}

@Composable
private fun RegisterForm(
    username: String,
    password: String,
    confirmPassword: String,
    isLoading: Boolean,
    authError: String?,
    validationError: String?,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.create_your_profile),
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        ErrorText(authError ?: validationError)

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
            isEnabled = !isLoading
        )

        CustomTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = stringResource(R.string.confirm_password),
            isPassword = true,
            isEnabled = !isLoading,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        PrimaryButton(
            text = stringResource(R.string.register_now),
            onClick = onRegisterClick,
            isLoading = isLoading
        )
    }
}

private fun validateRegistrationForm(
    context: Context,
    username: String,
    password: String,
    confirmPassword: String
): String? {
    return when {
        username.isEmpty() -> context.getString(R.string.username_is_required)
        password.isEmpty() -> context.getString(R.string.password_is_required)
        password != confirmPassword -> context.getString(R.string.passwords_do_not_match)
        password.length < 4 -> context.getString(R.string.password_must_be_at_least_4_characters)
        else -> null
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