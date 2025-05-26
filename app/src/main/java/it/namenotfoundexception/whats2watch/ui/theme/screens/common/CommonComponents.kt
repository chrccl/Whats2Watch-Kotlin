package it.namenotfoundexception.whats2watch.ui.theme.screens.common

import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import it.namenotfoundexception.whats2watch.R

@Composable
fun AppTitle(
    modifier: Modifier = Modifier,
    fontSize: Int = AppTextSizes.Title
) {
    Text(
        text = "Whats2Watch",
        color = AppColors.OnBackground,
        fontSize = fontSize.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@Composable
fun TopBar(
    title: @Composable () -> Unit,
    subtitle: String? = null,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            title()
            subtitle?.let { subtitleText ->
                Text(
                    text = subtitleText,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }

        SecondaryButton(
            text = stringResource(R.string.logout),
            onClick = onLogoutClick
        )
    }
}

@Composable
fun LoadingScreen(
    backgroundColor: Color = AppColors.Background
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = AppColors.Primary
        )
    }
}

@Composable
fun MovieBackgroundImage(
    imageUrl: String = DefaultBackgroundImageUrl,
    alpha: Float = 0.4f
) {
    val imageRequest = ImageRequest.Builder(LocalContext.current)
        .data(imageUrl)
        .crossfade(true)
        .build()

    AsyncImage(
        model = imageRequest,
        contentDescription = "Movie posters background",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
        alpha = alpha
    )
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = AppColors.Primary
        ),
        shape = RoundedCornerShape(AppDimensions.BorderRadius.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(AppDimensions.ButtonHeight.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = AppColors.OnPrimary,
                modifier = Modifier.size(AppDimensions.LoadingIndicatorSize.dp)
            )
        } else {
            Text(
                text = text,
                fontSize = AppTextSizes.Body.sp,
                color = AppColors.OnPrimary
            )
        }
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = AppColors.Primary
        ),
        shape = RoundedCornerShape(AppDimensions.CardRadius.dp),
        modifier = modifier
    ) {
        Text(text, color = AppColors.OnPrimary)
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    isEnabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = AppColors.OnBackground) },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        singleLine = true,
        enabled = isEnabled,
        colors = TextFieldDefaults.colors(
            focusedTextColor = AppColors.OnBackground,
            unfocusedTextColor = AppColors.OnBackground,
            focusedContainerColor = AppColors.Transparent,
            unfocusedContainerColor = AppColors.Transparent,
            focusedIndicatorColor = AppColors.OnBackground,
            unfocusedIndicatorColor = AppColors.Secondary,
            cursorColor = AppColors.OnBackground
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    )
}

@Composable
fun ErrorText(
    error: String?,
    modifier: Modifier = Modifier
) {
    error?.let {
        Text(
            text = it,
            color = Color.Red,
            modifier = modifier.padding(bottom = 16.dp)
        )
    }
}

@Composable
fun FormContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth(0.8f)
            .background(Color(0x99000000), RoundedCornerShape(16.dp))
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}