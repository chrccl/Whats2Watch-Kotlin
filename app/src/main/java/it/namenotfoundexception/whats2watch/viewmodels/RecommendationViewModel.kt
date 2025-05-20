package it.namenotfoundexception.whats2watch.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.namenotfoundexception.whats2watch.model.entities.Preference
import it.namenotfoundexception.whats2watch.model.entities.Movie
import it.namenotfoundexception.whats2watch.model.repositories.MovieRepository
import it.namenotfoundexception.whats2watch.model.repositories.PreferenceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecommendationViewModel @Inject constructor(
    private val movieRepo: MovieRepository,
    private val prefRepo: PreferenceRepository
) : ViewModel() {

    private val _suggestions = MutableStateFlow<List<Movie>>(emptyList())
    val suggestions: StateFlow<List<Movie>> = _suggestions

    private val _recError = MutableStateFlow<String?>(null)
    val recError: StateFlow<String?> = _recError

    /** Chiamato quando l’utente swipa (like/dislike) */
    fun swipeOnMovie(
        roomCode: String,
        username: String,
        movieId: String,
        liked: Boolean
    ) {
        viewModelScope.launch {
            try {
                prefRepo.savePreference(Preference(
                    roomCode = roomCode,
                    participantName = username,
                    movieId = movieId,
                    liked = liked
                ))
                _recError.value = null
                // ricarica suggerimenti dopo ogni swipe
                loadNextBatch(roomCode)
            } catch (e: Exception) {
                _recError.value = "Errore salvataggio preferenza: ${e.message}"
            }
        }
    }

    /** Carica il prossimo batch di film da proporre */
    fun loadNextBatch(
        roomCode: String,
        filterGenres: List<Int>? = null,
        filterActors: List<String>? = null,
        filterDirectors: List<String>? = null,
        voteGte: Float? = null
    ) {
        viewModelScope.launch {
            try {
                // usa discoverFilteredMovies oppure, se vuoi, getRoomMatches per film liked da tutti
                val list = movieRepo.discoverFilteredMovies(
                    genreIds      = filterGenres,
                    actorNames    = filterActors,
                    directorNames = filterDirectors,
                    voteAverageGte= voteGte
                )
                _suggestions.value = list
                _recError.value = null
            } catch (e: Exception) {
                _recError.value = "Errore caricamento suggerimenti: ${e.message}"
            }
        }
    }
}
