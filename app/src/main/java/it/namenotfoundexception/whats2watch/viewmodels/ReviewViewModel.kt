package it.namenotfoundexception.whats2watch.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.namenotfoundexception.whats2watch.model.entities.Review
import it.namenotfoundexception.whats2watch.model.repositories.ReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val reviewRepo: ReviewRepository
) : ViewModel() {

    private val _reviewsForMovie = MutableStateFlow<List<Review>>(emptyList())
    val reviewsForMovie: StateFlow<List<Review>> = _reviewsForMovie

    private val _userReviews = MutableStateFlow<List<Review>>(emptyList())
    val userReviews: StateFlow<List<Review>> = _userReviews

    private val _reviewError = MutableStateFlow<String?>(null)
    val reviewError: StateFlow<String?> = _reviewError

    fun submitReview(review: Review) {
        viewModelScope.launch {
            try {
                reviewRepo.saveReview(review)
                _reviewError.value = null
            } catch (e: Exception) {
                _reviewError.value = "Errore salvataggio recensione: ${e.message}"
            }
        }
    }

    fun loadReviewsForMovie(movieId: String) {
        viewModelScope.launch {
            try {
                _reviewsForMovie.value = reviewRepo.getMovieReviews(movieId)
                _reviewError.value = null
            } catch (e: Exception) {
                _reviewError.value = "Errore caricamento recensioni: ${e.message}"
            }
        }
    }

    fun loadUserReviews(username: String) {
        viewModelScope.launch {
            try {
                _userReviews.value = reviewRepo.getReviewsByUser(username)
                _reviewError.value = null
            } catch (e: Exception) {
                _reviewError.value = "Errore caricamento recensioni utente: ${e.message}"
            }
        }
    }
}