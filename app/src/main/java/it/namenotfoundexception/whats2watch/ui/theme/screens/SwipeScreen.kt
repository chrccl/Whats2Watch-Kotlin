package it.namenotfoundexception.whats2watch.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.AppColors
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.AppTitle
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.BackButton
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.ErrorText
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.MovieDetailsModal
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.RoomMatchesModal
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.SwipeableMovieCard
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.TopBar
import it.namenotfoundexception.whats2watch.viewmodels.RecommendationViewModel

@Composable
fun SwipeScreen(
    roomCode: String,
    username: String,
    viewModel: RecommendationViewModel = hiltViewModel(),
    onLogoutClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var batchCount by remember { mutableIntStateOf(0) }
    var showMovieDetails by remember { mutableStateOf(false) }
    var showMatchesModal by remember { mutableStateOf(false) }

    val suggestions by viewModel.suggestions.collectAsState()
    val recError by viewModel.recError.collectAsState()
    val roomMatches by viewModel.roomMatches.collectAsState()
    val userLikedMovies by viewModel.userLikedMovies.collectAsState()

    LaunchedEffect(roomCode, username) {
        viewModel.loadNextBatch(roomCode, username)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            TopBar(
                title = {
                    AppTitle()
                },
                subtitle = "Room: $roomCode",
                onLogoutClick = onLogoutClick,
                modifier = Modifier.padding(16.dp)
            )

            // Back Button
            BackButton(
                onClick = onBackClick,
                modifier = Modifier.padding(start = 16.dp)
            )

            // Error Message
            ErrorText(
                error = recError,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // Movie Card Container
            SwipeableMovieCard(
                suggestions = suggestions,
                batchCount = batchCount,
                onBatchCountChange = { batchCount = it },
                onMovieClick = { showMovieDetails = true },
                onMovieSwipe = { movie, isLiked ->
                    viewModel.onMovieSwipe(roomCode, username, movie, isLiked)
                },
                onLoadNextBatch = {
                    viewModel.loadNextBatch(roomCode, username)
                },
                modifier = Modifier.weight(1f)
            )
        }

        // Floating Action Button for matches
        FloatingActionButton(
            onClick = {
                viewModel.getRoomMatches(roomCode)
                viewModel.getLikedMoviesByUser(username, roomCode)
                showMatchesModal = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = AppColors.Primary
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "View Matches",
                tint = AppColors.OnPrimary
            )
        }
    }

    // Modals
    if (showMovieDetails && suggestions.isNotEmpty()) {
        MovieDetailsModal(
            movie = suggestions[batchCount],
            onDismiss = { showMovieDetails = false }
        )
    }

    if (showMatchesModal) {
        RoomMatchesModal(
            roomMatches = roomMatches,
            userLikedMovies = userLikedMovies,
            onDismiss = { showMatchesModal = false }
        )
    }
}