package it.namenotfoundexception.whats2watch.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import it.namenotfoundexception.whats2watch.model.entities.Movie
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.AppColors
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.AppDimensions
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.AppTitle
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.BottomNavigationReview
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.ReviewContent
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.ReviewDialog
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.ReviewSearchBar
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.ReviewsDialog
import it.namenotfoundexception.whats2watch.ui.theme.screens.common.TopBar
import it.namenotfoundexception.whats2watch.viewmodels.AuthViewModel
import it.namenotfoundexception.whats2watch.viewmodels.ReviewViewModel

@Composable
fun ReviewScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    reviewViewModel: ReviewViewModel = hiltViewModel(),
    onLogoutClick: () -> Unit,
    onHomeClick: () -> Unit,
    onRoomsClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showReviewDialog by remember { mutableStateOf(false) }
    var selectedMovie by remember { mutableStateOf<Movie?>(null) }
    var showReviewsDialog by remember { mutableStateOf(false) }
    var selectedMovieForReviews by remember { mutableStateOf<Movie?>(null) }

    val currentUser by authViewModel.currentUser.collectAsState()
    val reviewsForMovie by reviewViewModel.reviewsForMovie.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = AppDimensions.BottomNavHeight.dp)
        ) {
            // Top Bar
            TopBar(
                title = { AppTitle(fontSize = 24) },
                subtitle = currentUser?.let { "Welcome, ${it.username}" },
                onLogoutClick = onLogoutClick,
                modifier = Modifier.padding(AppDimensions.Spacing.dp)
            )

            // Search Bar
            ReviewSearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onSearch = { query ->
                    if (query.isNotEmpty()) {
                        reviewViewModel.searchMovies(query.trim())
                    }
                },
                modifier = Modifier.padding(horizontal = AppDimensions.Spacing.dp, vertical = AppDimensions.SmallSpacing.dp)
            )

            // Content
            ReviewContent(
                searchQuery = searchQuery,
                reviewViewModel = reviewViewModel,
                onCommentClick = { movie ->
                    selectedMovie = movie
                    showReviewDialog = true
                },
                onViewReviewsClick = { movie ->
                    selectedMovieForReviews = movie
                    reviewViewModel.loadReviewsForMovie(movie.imdbID)
                    showReviewsDialog = true
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = AppDimensions.Spacing.dp)
            )
        }

        // Bottom Navigation
        BottomNavigationReview(
            modifier = Modifier.align(Alignment.BottomCenter),
            onHomeClick = onHomeClick,
            onRoomsClick = onRoomsClick
        )

        // Review Dialog
        if (showReviewDialog && selectedMovie != null && currentUser != null) {
            ReviewDialog(
                movie = selectedMovie!!,
                currentUser = currentUser!!.username,
                reviewViewModel = reviewViewModel,
                onDismiss = {
                    showReviewDialog = false
                    selectedMovie = null
                }
            )
        }

        // Reviews Dialog
        if (showReviewsDialog && selectedMovieForReviews != null) {
            ReviewsDialog(
                movie = selectedMovieForReviews!!,
                reviews = reviewsForMovie,
                onDismiss = {
                    showReviewsDialog = false
                    selectedMovieForReviews = null
                }
            )
        }
    }
}