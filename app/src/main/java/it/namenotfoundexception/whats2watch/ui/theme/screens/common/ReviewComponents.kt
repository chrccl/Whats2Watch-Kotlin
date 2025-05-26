package it.namenotfoundexception.whats2watch.ui.theme.screens.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import it.namenotfoundexception.whats2watch.model.entities.Movie
import it.namenotfoundexception.whats2watch.model.entities.Review
import it.namenotfoundexception.whats2watch.viewmodels.ReviewViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ReviewSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        label = { Text("Search movies...", color = AppColors.Secondary) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = AppColors.Secondary
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch(searchQuery)
                keyboardController?.hide()
            }
        ),
        colors = TextFieldDefaults.colors(
            focusedTextColor = AppColors.OnBackground,
            unfocusedTextColor = AppColors.OnBackground,
            focusedContainerColor = AppColors.Surface,
            unfocusedContainerColor = AppColors.Surface,
            focusedIndicatorColor = AppColors.Primary,
            unfocusedIndicatorColor = AppColors.Secondary,
            cursorColor = AppColors.OnBackground
        ),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun ReviewContent(
    searchQuery: String,
    reviewViewModel: ReviewViewModel,
    onCommentClick: (Movie) -> Unit,
    onViewReviewsClick: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    val searchResults by reviewViewModel.searchResults.collectAsState()
    val isLoading by reviewViewModel.isLoading.collectAsState()

    Box(modifier = modifier.fillMaxWidth()) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    color = AppColors.Primary,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            searchResults.isEmpty() && searchQuery.isEmpty() -> {
                Text(
                    text = "Search for movies to review",
                    color = AppColors.Secondary,
                    fontSize = AppTextSizes.Body.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            searchResults.isEmpty() -> {
                Text(
                    text = "No movies found",
                    color = AppColors.Secondary,
                    fontSize = AppTextSizes.Body.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = AppDimensions.Spacing.dp)
                ) {
                    items(searchResults) { movie ->
                        MovieSearchItem(
                            movie = movie,
                            onCommentClick = { onCommentClick(movie) },
                            onViewReviewsClick = { onViewReviewsClick(movie) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MovieSearchItem(
    movie: Movie,
    onCommentClick: () -> Unit,
    onViewReviewsClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.Surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = AppDimensions.CardElevation.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Movie Poster
            AsyncImage(
                model = movie.poster,
                contentDescription = "${movie.title} poster",
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(AppDimensions.SmallSpacing.dp)),
                contentScale = ContentScale.Crop
            )

            // Movie Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = movie.title,
                    color = AppColors.OnSurface,
                    fontSize = AppTextSizes.Body.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = movie.year,
                    color = AppColors.Secondary,
                    fontSize = AppTextSizes.Small.sp
                )

                movie.genre?.let { genre ->
                    Text(
                        text = genre,
                        color = AppColors.Secondary,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                movie.imdbRating?.let { rating ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = rating,
                            color = AppColors.OnSurface,
                            fontSize = AppTextSizes.Small.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.SmallSpacing.dp)
                ) {
                    Button(
                        onClick = onCommentClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.Primary
                        ),
                        shape = RoundedCornerShape(AppDimensions.SmallSpacing.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp)
                    ) {
                        Text(
                            text = "Comment",
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = onViewReviewsClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        ),
                        shape = RoundedCornerShape(AppDimensions.SmallSpacing.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp)
                    ) {
                        Text(
                            text = "Reviews",
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationReview(
    modifier: Modifier = Modifier,
    onHomeClick: () -> Unit,
    onRoomsClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(AppDimensions.BottomNavHeight.dp)
            .background(Color.Black),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Home Icon
        Box(
            modifier = Modifier
                .size(AppDimensions.IconSize.dp)
                .clip(CircleShape)
                .background(AppColors.Primary)
                .clickable { onHomeClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Home",
                tint = AppColors.OnPrimary
            )
        }

        // Rooms Icon
        Box(
            modifier = Modifier
                .size(AppDimensions.IconSize.dp)
                .clip(CircleShape)
                .background(AppColors.Primary)
                .clickable { onRoomsClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Rooms",
                tint = AppColors.OnPrimary
            )
        }

        // Review Icon (Active)
        Box(
            modifier = Modifier
                .size(AppDimensions.IconSize.dp)
                .clip(CircleShape)
                .background(AppColors.OnPrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Reviews",
                tint = AppColors.Primary
            )
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun ReviewDialog(
    movie: Movie,
    currentUser: String,
    reviewViewModel: ReviewViewModel,
    onDismiss: () -> Unit
) {
    var rating by remember { mutableFloatStateOf(0f) }
    var comment by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    val reviewError by reviewViewModel.reviewError.collectAsState()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimensions.Spacing.dp),
            shape = RoundedCornerShape(AppDimensions.CardRadius.dp),
            colors = CardDefaults.cardColors(
                containerColor = AppColors.Surface
            )
        ) {
            Column(
                modifier = Modifier.padding(AppDimensions.LargeSpacing.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Review Movie",
                        color = AppColors.OnSurface,
                        fontSize = AppTextSizes.Subtitle.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = AppColors.Secondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(AppDimensions.Spacing.dp))

                // Movie info
                Text(
                    text = movie.title,
                    color = AppColors.OnSurface,
                    fontSize = AppTextSizes.Body.sp,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = movie.year,
                    color = AppColors.Secondary,
                    fontSize = AppTextSizes.Small.sp
                )

                Spacer(modifier = Modifier.height(AppDimensions.LargeSpacing.dp))

                // Rating stars
                Text(
                    text = "Rating",
                    color = AppColors.OnSurface,
                    fontSize = AppTextSizes.Body.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(AppDimensions.SmallSpacing.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.SmallSpacing.dp)
                ) {
                    for (i in 1..10) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star $i",
                            tint = if (i <= rating) Color(0xFFFFD700) else AppColors.Secondary,
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { rating = i.toFloat() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(AppDimensions.LargeSpacing.dp))

                // Comment field
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Your comment", color = AppColors.Secondary) },
                    minLines = 3,
                    maxLines = 5,
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = AppColors.OnSurface,
                        unfocusedTextColor = AppColors.OnSurface,
                        focusedContainerColor = AppColors.Transparent,
                        unfocusedContainerColor = AppColors.Transparent,
                        focusedIndicatorColor = AppColors.Primary,
                        unfocusedIndicatorColor = AppColors.Secondary,
                        cursorColor = AppColors.OnSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Error message
                ErrorText(error = reviewError)

                Spacer(modifier = Modifier.height(AppDimensions.LargeSpacing.dp))

                // Submit button
                PrimaryButton(
                    text = "Submit Review",
                    onClick = {
                        if (rating > 0 && comment.isNotEmpty()) {
                            isSubmitting = true
                            val review = Review(
                                user = currentUser,
                                movieId = movie.imdbID,
                                rating = rating,
                                comment = comment.trim()
                            )
                            reviewViewModel.submitReview(review, movie)

                            // Close dialog after a brief delay
                            GlobalScope.launch {
                                delay(1000)
                                isSubmitting = false
                                onDismiss()
                            }
                        }
                    },
                    enabled = rating > 0 && comment.isNotEmpty(),
                    isLoading = isSubmitting
                )
            }
        }
    }
}

@Composable
fun ReviewsDialog(
    movie: Movie,
    reviews: List<Review>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .padding(AppDimensions.Spacing.dp),
            shape = RoundedCornerShape(AppDimensions.CardRadius.dp),
            colors = CardDefaults.cardColors(
                containerColor = AppColors.Surface
            )
        ) {
            Column(
                modifier = Modifier.padding(AppDimensions.Spacing.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Reviews for ${movie.title}",
                        color = AppColors.OnSurface,
                        fontSize = AppTextSizes.Caption.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = AppColors.Secondary
                        )
                    }
                }

                // Reviews list
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = AppDimensions.SmallSpacing.dp)
                ) {
                    items(reviews) { review ->
                        ReviewItem(review = review)
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewItem(review: Review) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF3A3A3A)
        ),
        shape = RoundedCornerShape(AppDimensions.SmallSpacing.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = review.user,
                    color = AppColors.OnSurface,
                    fontWeight = FontWeight.Medium
                )

                if (review.rating > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = review.rating.toString(),
                            color = AppColors.OnSurface,
                            fontSize = AppTextSizes.Small.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(AppDimensions.SmallSpacing.dp))

            Text(
                text = review.comment,
                color = AppColors.Secondary,
                fontSize = AppTextSizes.Small.sp
            )
        }
    }
}