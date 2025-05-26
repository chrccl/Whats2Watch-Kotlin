package it.namenotfoundexception.whats2watch.ui.theme.screens.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import coil.compose.rememberAsyncImagePainter
import it.namenotfoundexception.whats2watch.model.entities.Movie
import kotlin.math.abs

@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(AppColors.Primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = AppColors.OnPrimary
            )
        }
    }
}

@Composable
fun SwipeableMovieCard(
    suggestions: List<Movie>,
    batchCount: Int,
    onBatchCountChange: (Int) -> Unit,
    onMovieClick: () -> Unit,
    onMovieSwipe: (Movie, Boolean) -> Unit,
    onLoadNextBatch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val swipeThreshold = screenWidth.value * 0.3f

    var offsetX by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = AppDimensions.ExtraLargeSpacing.dp, vertical = AppDimensions.Spacing.dp),
        contentAlignment = Alignment.Center
    ) {
        if (suggestions.isNotEmpty()) {
            // Movie Card
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .aspectRatio(0.75f)
                    .graphicsLayer {
                        translationX = offsetX
                        rotationZ = offsetX / 50f
                        alpha = if (abs(offsetX) > swipeThreshold) 0.7f else 1f
                    }
                    .clickable {
                        if (!isDragging) {
                            onMovieClick()
                        }
                    }
                    .pointerInput(suggestions[batchCount].imdbID) {
                        detectDragGestures(
                            onDragStart = { isDragging = true },
                            onDragEnd = {
                                isDragging = false
                                when {
                                    offsetX > swipeThreshold -> {
                                        onMovieSwipe(suggestions[batchCount], true)
                                        onBatchCountChange(batchCount + 1)
                                        offsetX = 0f
                                    }
                                    offsetX < -swipeThreshold -> {
                                        onMovieSwipe(suggestions[batchCount], false)
                                        onBatchCountChange(batchCount + 1)
                                        offsetX = 0f
                                    }
                                    else -> {
                                        offsetX = 0f
                                    }
                                }
                                if (batchCount > suggestions.size - 10) {
                                    onLoadNextBatch()
                                    onBatchCountChange(0)
                                }
                            }
                        ) { _, dragAmount ->
                            offsetX += dragAmount.x
                        }
                    },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(AppDimensions.LargeSpacing.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = AppDimensions.CardElevation.dp)
            ) {
                MovieCardContent(movie = suggestions[batchCount])
            }

            // Action Buttons
            SwipeActionButtons(
                onDislike = {
                    onMovieSwipe(suggestions[batchCount], false)
                    onLoadNextBatch()
                },
                onLike = {
                    onMovieSwipe(suggestions[batchCount], true)
                    onLoadNextBatch()
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        } else {
            LoadingIndicator()
        }
    }
}

@Composable
private fun MovieCardContent(movie: Movie) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Movie Poster
        Image(
            painter = rememberAsyncImagePainter(movie.poster),
            contentDescription = movie.title,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(topStart = AppDimensions.LargeSpacing.dp, topEnd = AppDimensions.LargeSpacing.dp)),
            contentScale = ContentScale.Crop
        )

        // Movie Info
        Column(modifier = Modifier.padding(AppDimensions.Spacing.dp)) {
            Text(
                text = movie.title,
                fontSize = AppTextSizes.Subtitle.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(AppDimensions.SmallSpacing.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = movie.year,
                    fontSize = AppTextSizes.Small.sp,
                    color = AppColors.Secondary
                )

                movie.imdbRating?.let { rating ->
                    Text(
                        text = "★ $rating",
                        fontSize = AppTextSizes.Small.sp,
                        color = Color(0xFFFFD700)
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppDimensions.SmallSpacing.dp))

            Text(
                text = "Tap for details",
                fontSize = 12.sp,
                color = AppColors.Secondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SwipeActionButtons(
    onDislike: () -> Unit,
    onLike: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = AppDimensions.ExtraLargeSpacing.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(onClick = onDislike) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Dislike",
                tint = AppColors.Primary,
                modifier = Modifier.size(32.dp)
            )
        }

        IconButton(onClick = onLike) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Like",
                tint = AppColors.Primary,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(
            color = AppColors.Primary,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(AppDimensions.Spacing.dp))
        Text(
            text = "Loading recommendations...",
            color = AppColors.OnBackground,
            fontSize = AppDimensions.Spacing.sp
        )
    }
}