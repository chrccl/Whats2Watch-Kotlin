package it.namenotfoundexception.whats2watch.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.namenotfoundexception.whats2watch.R
import it.namenotfoundexception.whats2watch.model.ResourceProvider
import it.namenotfoundexception.whats2watch.model.entities.Movie
import it.namenotfoundexception.whats2watch.model.entities.Review
import it.namenotfoundexception.whats2watch.repositories.MovieRepository
import it.namenotfoundexception.whats2watch.repositories.ReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val reviewRepo: ReviewRepository,
    private val movieRepo: MovieRepository,
    private val res: ResourceProvider
) : ViewModel() {

    private val _reviewsForMovie = MutableStateFlow<List<Review>>(emptyList())
    val reviewsForMovie: StateFlow<List<Review>> = _reviewsForMovie

    private val _searchResults = MutableStateFlow<List<Movie>>(emptyList())
    val searchResults: StateFlow<List<Movie>> = _searchResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _reviewError = MutableStateFlow<String?>(null)
    val reviewError: StateFlow<String?> = _reviewError

    fun submitReview(review: Review, movie: Movie) {
        viewModelScope.launch {
            try {
                movieRepo.saveMovie(movie)
                reviewRepo.saveReview(review)
                _reviewError.value = null
            } catch (e: Exception) {
                _reviewError.value = res.getString(R.string.error_saving_review, e.message)
            }
        }
    }

    fun loadReviewsForMovie(movieId: String) {
        viewModelScope.launch {
            try {
                _reviewsForMovie.value = reviewRepo.getMovieReviews(movieId)
                _reviewError.value = null
            } catch (e: Exception) {
                _reviewError.value = res.getString(R.string.reviews_loading_failed, e.message)
            }
        }
    }

    fun searchMovies(query: String) {
        if (query.isEmpty()) {
            _searchResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true
                _reviewError.value = null

                val results = movieRepo.searchMovies(query)
                _searchResults.value = results

            } catch (e: Exception) {
                _reviewError.value = res.getString(R.string.searching_movie_error, e.message)
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

}