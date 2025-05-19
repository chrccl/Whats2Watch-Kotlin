package it.namenotfoundexception.whats2watch.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.namenotfoundexception.whats2watch.api.OMDBSearchResponse
import it.namenotfoundexception.whats2watch.api.OMDBService
import it.namenotfoundexception.whats2watch.model.entities.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OmdbViewModel @Inject constructor(
    private val api: OMDBService
) : ViewModel() {

    // --- STATE FLOWS per la UI ---
    private val _searchResults =
        MutableStateFlow<List<Movie>>(emptyList())
    val searchResults: StateFlow<List<Movie>> = _searchResults

    private val _searchError =
        MutableStateFlow<String?>(null)
    val searchError: StateFlow<String?> = _searchError

    private val _movieDetail =
        MutableStateFlow<Movie?>(null)
    val movieDetail: StateFlow<Movie?> = _movieDetail

    private val _detailError =
        MutableStateFlow<String?>(null)
    val detailError: StateFlow<String?> = _detailError

    fun searchMovies(query: String, page: Int = 1) {
        viewModelScope.launch {
            try {
                _searchError.value = null
                val response: OMDBSearchResponse = api.searchMovies(query = query, page = page)

                if (response.response == "True") {
                    _searchResults.value = response.results
                } else {
                    _searchResults.value = emptyList()
                    _searchError.value = "Nessun risultato per \"$query\""
                }
            } catch (e: Exception) {
                _searchResults.value = emptyList()
                _searchError.value = "Errore rete: ${e.localizedMessage}"
            }
        }
    }

    fun fetchMovieById(imdbId: String) {
        viewModelScope.launch {
            try {
                _movieDetail.value = api.getMovieById(imdbId = imdbId)
                _detailError.value = null
            } catch (e: Exception) {
                _movieDetail.value = null
                _detailError.value = "Errore rete: ${e.localizedMessage}"
            }
        }
    }
}