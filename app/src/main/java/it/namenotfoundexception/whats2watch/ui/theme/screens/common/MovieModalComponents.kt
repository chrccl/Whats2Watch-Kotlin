package it.namenotfoundexception.whats2watch.ui.theme.screens.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import it.namenotfoundexception.whats2watch.model.entities.Movie
import it.namenotfoundexception.whats2watch.R

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
                .padding(AppDimensions.Spacing.dp),
            shape = RoundedCornerShape(AppDimensions.CardRadius.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                MovieDetailHeader(
                    movie = movie,
                    onClose = onDismiss
                )

                MovieDetailContent(movie = movie)
            }
        }
    }
}

@Composable
private fun MovieDetailHeader(
    movie: Movie,
    onClose: () -> Unit
) {
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
                .clip(
                    RoundedCornerShape(
                        topStart = AppDimensions.CardRadius.dp,
                        topEnd = AppDimensions.CardRadius.dp
                    )
                ),
            contentScale = ContentScale.Crop
        )

        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(AppDimensions.SmallSpacing.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.close),
                tint = Color.White,
                modifier = Modifier
                    .size(32.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    .padding(6.dp)
            )
        }
    }
}

@Composable
private fun MovieDetailContent(movie: Movie) {
    Column(modifier = Modifier.padding(AppDimensions.Spacing.dp)) {
        Text(
            text = movie.title,
            fontSize = AppTextSizes.Title.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(AppDimensions.SmallSpacing.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(AppDimensions.Spacing.dp)) {
            Text(
                text = movie.year,
                fontSize = AppDimensions.Spacing.sp,
                color = AppColors.Secondary
            )

            movie.imdbRating?.let { rating ->
                Text(
                    text = stringResource(R.string.stars_icon),
                    fontSize = AppDimensions.Spacing.sp,
                    color = Color(0xFFFFD700)
                )
            }
        }

        Spacer(modifier = Modifier.height(AppDimensions.Spacing.dp))

        movie.genre?.let { genre ->
            DetailSection(stringResource(R.string.genre), genre)
            Spacer(modifier = Modifier.height(12.dp))
        }

        movie.director?.let { director ->
            DetailSection(stringResource(R.string.director), director)
            Spacer(modifier = Modifier.height(12.dp))
        }

        movie.actors?.let { actors ->
            DetailSection(stringResource(R.string.cast), actors)
            Spacer(modifier = Modifier.height(12.dp))
        }

        movie.plot?.let { plot ->
            DetailSection(stringResource(R.string.plot), plot)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun DetailSection(title: String, content: String) {
    Column {
        Text(
            text = title,
            fontSize = AppTextSizes.Body.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.Primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content,
            fontSize = AppDimensions.Spacing.sp,
            color = Color.Black,
            lineHeight = AppDimensions.LargeSpacing.sp
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
    val tabs = listOf(stringResource(R.string.room_matches), stringResource(R.string.my_likes))

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(AppDimensions.Spacing.dp),
            shape = RoundedCornerShape(AppDimensions.CardRadius.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                MatchesModalHeader(onDismiss = onDismiss)

                MatchesTabRow(
                    tabs = tabs,
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )

                when (selectedTab) {
                    0 -> MoviesList(
                        movies = roomMatches,
                        emptyMessage = stringResource(R.string.no_room_matches_yet_when_everyone_likes_the_same_movie_it_will_appear_here)
                    )
                    1 -> MoviesList(
                        movies = userLikedMovies,
                        emptyMessage = stringResource(R.string.you_haven_t_liked_any_movies_yet_start_swiping_to_build_your_list)
                    )
                }
            }
        }
    }
}

@Composable
private fun MatchesModalHeader(onDismiss: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(AppDimensions.Spacing.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.movies),
            fontSize = AppTextSizes.Title.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        IconButton(onClick = onDismiss) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.close),
                tint = AppColors.Secondary
            )
        }
    }
}

@Composable
private fun MatchesTabRow(
    tabs: List<String>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTab,
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color.White,
        contentColor = AppColors.Primary
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = title,
                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
        }
    }
}

@Composable
fun MoviesList(
    movies: List<Movie>?,
    emptyMessage: String
) {
    if (movies.isNullOrEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emptyMessage,
                fontSize = AppDimensions.Spacing.sp,
                color = AppColors.Secondary,
                textAlign = TextAlign.Center,
                lineHeight = AppDimensions.LargeSpacing.sp
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(AppDimensions.Spacing.dp),
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
                    .clip(RoundedCornerShape(AppDimensions.SmallSpacing.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movie.title,
                    fontSize = AppDimensions.Spacing.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = movie.year,
                    fontSize = AppTextSizes.Small.sp,
                    color = AppColors.Secondary
                )

                movie.imdbRating?.let { rating ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.stars_icon),
                        fontSize = AppTextSizes.Small.sp,
                        color = Color(0xFFFFD700)
                    )
                }

                movie.genre?.let { genre ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = genre,
                        fontSize = 12.sp,
                        color = AppColors.Secondary,
                        maxLines = 1
                    )
                }
            }
        }
    }
}