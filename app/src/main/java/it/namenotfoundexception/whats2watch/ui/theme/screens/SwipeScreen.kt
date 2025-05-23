
package it.namenotfoundexception.whats2watch.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import it.namenotfoundexception.whats2watch.model.entities.Movie
import it.namenotfoundexception.whats2watch.viewmodels.AuthViewModel
import it.namenotfoundexception.whats2watch.viewmodels.RecommendationViewModel
import kotlin.math.abs

@Composable
fun SwipeScreen(
    roomCode: String,
    username: String,
    modifier: Modifier = Modifier,
    viewModel: RecommendationViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    onLogoutClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var batchCount = 0
    val threshold = 10
    val backgroundColor = Color(0xFF1A1A1A)
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    var offsetX by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    val swipeThreshold = screenWidth.value * 0.3f

    val suggestions by viewModel.suggestions.collectAsState()
    val recError by viewModel.recError.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    // Carica le raccomandazioni quando il componente viene montato
    LaunchedEffect(roomCode, username) {
        viewModel.loadNextBatch(roomCode, username)
    }

    // Movie placeholder per quando non ci sono suggerimenti
    val currentMovie = if (suggestions.isNotEmpty()) {
        suggestions[0]
    } else {
        Movie(
            imdbID = "placeholder",
            title = "Loading...",
            year = "2024",
            runtime = null,
            genre = null,
            director = null,
            actors = null,
            plot = "Loading recommendations...",
            awards = null,
            poster = "https://via.placeholder.com/300x450/333/FFF?text=Loading",
            imdbRating = null
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Whats2Watch",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Room: $roomCode",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }

                Button(
                    onClick = onLogoutClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53935)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Logout")
                }
            }

            // Back button
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE53935)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }

            // Error message se presente
            recError?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }

            // Movie Card
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (suggestions.isNotEmpty()) {
                    // Swipeable Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .aspectRatio(0.75f)
                            .graphicsLayer {
                                translationX = offsetX
                                rotationZ = offsetX / 50f // Slight rotation during swipe
                                alpha = if (abs(offsetX) > swipeThreshold) 0.7f else 1f
                            }
                            .pointerInput(currentMovie.imdbID) {
                                detectDragGestures(
                                    onDragStart = { isDragging = true },
                                    onDragEnd = {
                                        isDragging = false
                                        when {
                                            offsetX > swipeThreshold -> {
                                                // Swipe right - Like
                                                viewModel.onMovieSwipe(roomCode, username, currentMovie.imdbID, true)
                                                batchCount++
                                                offsetX = 0f
                                            }
                                            offsetX < -swipeThreshold -> {
                                                // Swipe left - Dislike
                                                viewModel.onMovieSwipe(
                                                    roomCode,
                                                    username,
                                                    currentMovie.imdbID,
                                                    false
                                                )
                                                batchCount++
                                                offsetX = 0f
                                            }
                                            else -> {
                                                // Return to center
                                                offsetX = 0f
                                            }
                                        }
                                        if (batchCount > threshold){
                                            viewModel.loadNextBatch(roomCode, username)
                                            batchCount = 0
                                        }
                                    }
                                ) { _, dragAmount ->
                                    offsetX += dragAmount.x
                                }
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // Movie Poster
                            Image(
                                painter = rememberAsyncImagePainter(currentMovie.poster),
                                contentDescription = currentMovie.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                                contentScale = ContentScale.Crop
                            )

                            // Movie Info
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = currentMovie.title,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    currentMovie.year.let { year ->
                                        Text(
                                            text = year,
                                            fontSize = 14.sp,
                                            color = Color.Gray
                                        )
                                    }

                                    currentMovie.imdbRating?.let { rating ->
                                        Text(
                                            text = "★ $rating",
                                            fontSize = 14.sp,
                                            color = Color(0xFFFFD700)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Action buttons at the bottom
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(bottom = 32.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Dislike button
                        IconButton(
                            onClick = {
                                viewModel.onMovieSwipe(roomCode, username, currentMovie.imdbID, false)
                                viewModel.loadNextBatch(roomCode, username)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dislike",
                                tint = Color(0xFFE53935),
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        // Like button
                        IconButton(
                            onClick = {
                                viewModel.onMovieSwipe(roomCode, username, currentMovie.imdbID, true)
                                viewModel.loadNextBatch(roomCode, username)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Like",
                                tint = Color(0xFFE53935),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                } else {
                    // Loading or no suggestions
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFFE53935),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading recommendations...",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            // Bottom labels
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 64.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Swipe",
                    color = Color(0xFFE53935),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Matches",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}