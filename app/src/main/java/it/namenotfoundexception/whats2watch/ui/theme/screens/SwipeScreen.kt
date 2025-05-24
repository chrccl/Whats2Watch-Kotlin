package it.namenotfoundexception.whats2watch.ui.theme.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
    viewModel: RecommendationViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    onLogoutClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val backgroundColor = Color(0xFF1A1A1A)
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    var batchCount by remember { mutableIntStateOf(0) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var showMovieDetails by remember { mutableStateOf(false) }
    var showMatchesModal by remember { mutableStateOf(false) }
    val swipeThreshold = screenWidth.value * 0.3f

    val suggestions by viewModel.suggestions.collectAsState()
    val recError by viewModel.recError.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val roomMatches by viewModel.roomMatches.collectAsState()
    val userLikedMovies by viewModel.userLikedMovies.collectAsState()

    // Carica le raccomandazioni quando il componente viene montato
    LaunchedEffect(roomCode, username) {
        viewModel.loadNextBatch(roomCode, username)
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
                            .clickable {
                                if (!isDragging) {
                                    showMovieDetails = true
                                }
                            }
                            .pointerInput(suggestions[batchCount].imdbID) {
                                detectDragGestures(
                                    onDragStart = { isDragging = true },
                                    onDragEnd = {
                                        isDragging = false
                                        when {
                                            offsetX > swipeThreshold -> {
                                                // Swipe right - Like
                                                viewModel.onMovieSwipe(
                                                    roomCode,
                                                    username,
                                                    suggestions[batchCount],
                                                    true
                                                )
                                                batchCount++
                                                offsetX = 0f
                                            }

                                            offsetX < -swipeThreshold -> {
                                                // Swipe left - Dislike
                                                viewModel.onMovieSwipe(
                                                    roomCode,
                                                    username,
                                                    suggestions[batchCount],
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
                                        if (batchCount > suggestions.size - 10) {
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
                                painter = rememberAsyncImagePainter(suggestions[batchCount].poster),
                                contentDescription = suggestions[batchCount].title,
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
                                    text = suggestions[batchCount].title,
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
                                    Text(
                                        text = suggestions[batchCount].year,
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )

                                    suggestions[batchCount].imdbRating?.let { rating ->
                                        Text(
                                            text = "★ $rating",
                                            fontSize = 14.sp,
                                            color = Color(0xFFFFD700)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Tap for details",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
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
                                viewModel.onMovieSwipe(
                                    roomCode,
                                    username,
                                    suggestions[batchCount],
                                    false
                                )
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
                                viewModel.onMovieSwipe(
                                    roomCode,
                                    username,
                                    suggestions[batchCount],
                                    true
                                )
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

        // Floating Action Button per i matches
        FloatingActionButton(
            onClick = {
                viewModel.getRoomMatches(roomCode)
                viewModel.getLikedMoviesByUser(username, roomCode)
                showMatchesModal = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color(0xFFE53935)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "View Matches",
                tint = Color.White
            )
        }
    }

    // Modal per i dettagli del film
    if (showMovieDetails && suggestions.isNotEmpty()) {
        MovieDetailsModal(
            movie = suggestions[batchCount],
            onDismiss = { showMovieDetails = false }
        )
    }

    // Modal per i matches della stanza
    if (showMatchesModal) {
        RoomMatchesModal(
            roomMatches = roomMatches,
            userLikedMovies = userLikedMovies,
            onDismiss = { showMatchesModal = false }
        )
    }
}

@Composable
fun MovieDetailsModal(
    movie: Movie,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header con immagine e close button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(movie.poster),
                        contentDescription = movie.title,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                        contentScale = ContentScale.Crop
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    Color.Black.copy(alpha = 0.5f),
                                    CircleShape
                                )
                                .padding(6.dp)
                        )
                    }
                }

                // Dettagli del film
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = movie.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = movie.year,
                            fontSize = 16.sp,
                            color = Color.Gray
                        )

                        movie.imdbRating?.let { rating ->
                            Text(
                                text = "★ $rating",
                                fontSize = 16.sp,
                                color = Color(0xFFFFD700)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    movie.genre?.let { genre ->
                        DetailSection("Genre", genre)
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    movie.director?.let { director ->
                        DetailSection("Director", director)
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    movie.actors?.let { actors ->
                        DetailSection("Cast", actors)
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    movie.plot?.let { plot ->
                        DetailSection("Plot", plot)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DetailSection(title: String, content: String) {
    Column {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFE53935)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content,
            fontSize = 16.sp,
            color = Color.Black,
            lineHeight = 24.sp
        )
    }
}

@Composable
fun RoomMatchesModal(
    roomMatches: List<Movie>?,
    userLikedMovies: List<Movie>?,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Room Matches", "My Likes")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Movies",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Gray
                        )
                    }
                }

                // Tab Row
                TabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = Color.White,
                    contentColor = Color(0xFFE53935)
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }

                // Content
                when (selectedTab) {
                    0 -> MoviesList(
                        movies = roomMatches,
                        emptyMessage = "No room matches yet!\nWhen everyone likes the same movie, it will appear here."
                    )

                    1 -> MoviesList(
                        movies = userLikedMovies,
                        emptyMessage = "You haven't liked any movies yet.\nStart swiping to build your list!"
                    )
                }
            }
        }
    }
}

@Composable
fun MoviesList(
    movies: List<Movie>?,
    emptyMessage: String
) {
    if (movies == null || movies.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emptyMessage,
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(movies) { movie ->
                MovieListItem(movie = movie)
            }
        }
    }
}

@Composable
fun MovieListItem(movie: Movie) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(movie.poster),
                contentDescription = movie.title,
                modifier = Modifier
                    .size(80.dp, 120.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = movie.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = movie.year,
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                movie.imdbRating?.let { rating ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "★ $rating",
                        fontSize = 14.sp,
                        color = Color(0xFFFFD700)
                    )
                }

                movie.genre?.let { genre ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = genre,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }
            }
        }
    }
}