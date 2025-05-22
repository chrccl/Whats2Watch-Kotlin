package it.namenotfoundexception.whats2watch.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import it.namenotfoundexception.whats2watch.viewmodels.RecommendationViewModel
import kotlin.math.abs

@Composable
fun SwipeScreen(
    //navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: RecommendationViewModel = hiltViewModel(),
    roomCode: String,
    username: String,
    onLogoutClick: () -> Unit
) {
    val backgroundColor = Color(0xFF1A1A1A)
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    var offsetX by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    val swipeThreshold = screenWidth.value * 0.3f

    // Sample movie data - in real implementation this would come from viewModel
    val suggestions by viewModel.suggestions.collectAsState()
    val currentMovie = suggestions[0]

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
                Text(
                    text = "Whats2Watch",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

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
                onClick = { /*navController.popBackStack() */},
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
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }

            // Movie Card
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
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
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { isDragging = true },
                                onDragEnd = {
                                    isDragging = false
                                    when {
                                        offsetX > swipeThreshold -> {
                                            // Swipe right - Like
                                            viewModel.onMovieSwipe(roomCode, username, currentMovie.imdbID, true)
                                            offsetX = 0f
                                        }
                                        offsetX < -swipeThreshold -> {
                                            // Swipe left - Dislike
                                            viewModel.onMovieSwipe(roomCode, username, currentMovie.imdbID, false)
                                            offsetX = 0f
                                        }
                                        else -> {
                                            // Return to center
                                            offsetX = 0f
                                        }
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

                        // Movie Title
                        Text(
                            text = currentMovie.title,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
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
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dislike",
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(32.dp)
                    )

                    // Like button
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Like",
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(32.dp)
                    )
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
