package it.namenotfoundexception.whats2watch.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.namenotfoundexception.whats2watch.model.entities.Movie
import it.namenotfoundexception.whats2watch.model.entities.Preference
import it.namenotfoundexception.whats2watch.repositories.GenreRepository
import it.namenotfoundexception.whats2watch.repositories.MovieRepository
import it.namenotfoundexception.whats2watch.repositories.PreferenceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecommendationViewModel @Inject constructor(
    private val movieRepo: MovieRepository,
    private val genreRepo: GenreRepository,
    private val prefRepo: PreferenceRepository
) : ViewModel() {

    private val _suggestions = MutableStateFlow<List<Movie>>(emptyList())
    val suggestions: StateFlow<List<Movie>> = _suggestions

    private val _roomMatches = MutableStateFlow<List<Movie>?>(null)
    val roomMatches: StateFlow<List<Movie>?> = _roomMatches

    private val _userLikedMovies = MutableStateFlow<List<Movie>?>(null)
    val userLikedMovies: StateFlow<List<Movie>?> = _userLikedMovies

    private val _recError = MutableStateFlow<String?>(null)
    val recError: StateFlow<String?> = _recError

    fun getLikedMoviesByUser(username: String, roomCode: String) {
        viewModelScope.launch {
            try {
                val likedPrefs = prefRepo
                    .getPreferencesByUser(roomCode, username)
                    .filter { it.liked }
                val likedIds = likedPrefs.map { it.movieId }
                val likedMovies = if (likedIds.isNotEmpty())
                    movieRepo.getMoviesByIds(likedIds) else emptyList()

                _userLikedMovies.value = likedMovies
                _recError.value = null
            } catch (e: Exception) {
                _recError.value =
                    "Impossibile reperire i like dell'utente per la stanza corrente: ${e.message}"
            }
        }
    }

    fun onMovieSwipe(
        roomCode: String,
        username: String,
        movieId: String,
        liked: Boolean
    ) {
        viewModelScope.launch {
            try {
                prefRepo.savePreference(
                    Preference(
                        roomCode = roomCode,
                        participantName = username,
                        movieId = movieId,
                        liked = liked
                    )
                )
                _recError.value = null
            } catch (e: Exception) {
                _recError.value = "Errore salvataggio preferenza: ${e.message}"
            }
        }
    }

    fun loadNextBatch(
        roomCode: String,
        username: String,
        pageSize: Int = 20
    ) {
        viewModelScope.launch {
            try {
                // 1) Prendi tutte le preference liked per utente e stanza
                val likedPrefs = prefRepo
                    .getPreferencesByUser(roomCode, username)
                    .filter { it.liked }
                val likedIds = likedPrefs.map { it.movieId }
                val likedMovies = if (likedIds.isNotEmpty())
                    movieRepo.getMoviesByIds(likedIds)
                else emptyList()

                // 2) Calcola anno medio → range ±5 anni
                val avgYear = likedMovies
                    .mapNotNull { it.year.toIntOrNull() }
                    .takeIf { it.isNotEmpty() }?.average()?.toInt()
                val (gteDate, lteDate) = avgYear?.let {
                    "${(it - 5).coerceAtLeast(1950)}-01-01" to "${it + 5}-12-31"
                } ?: (null to null)

                // 3) Estrai top 3 generi, attori e top regista
                val topGenres = topN(likedMovies.flatMap { it.genre.orEmpty().split(", ") }, 3)
                val topActors = topN(likedMovies.flatMap { it.actors.orEmpty().split(", ") }, 3)
                val topDirectors = topN(likedMovies.mapNotNull { it.director }, 1)

                // 4) Mappa nomi generi → ID TMDb usando la mappa completa
                val genreMap = genreRepo.getGenreMap()
                val genreIds = topGenres.mapNotNull { genreMap[it] }

                // 5) Chiamata discover con tutti i filtri
                val candidates = movieRepo.discoverFilteredMovies(
                    genreIds = genreIds,
                    actorNames = topActors,
                    directorNames = topDirectors,
                    voteAverageGte = 6.0f,
                    releaseDateGte = gteDate,
                    releaseDateLte = lteDate,
                    sortBy = "popularity.desc"
                )

                // 6) Filtra già visti e ordina con punteggio
                val unseen = candidates.filter { c ->
                    likedPrefs.none { it.movieId == c.imdbID }
                }
                val scored = unseen
                    .map { c -> c to calculateScore(likedMovies, c) }
                    .sortedByDescending { it.second }

                _suggestions.value = scored.take(pageSize).map { it.first }
                _recError.value = null

            } catch (e: Exception) {
                _recError.value = "Errore raccomandazione: ${e.localizedMessage}"
            }
        }
    }

    fun getRoomMatches(code: String) {
        viewModelScope.launch {
            try {
                _roomMatches.value = prefRepo.getRoomMatches(code)
                _recError.value = null
            } catch (e: Exception) {
                _recError.value =
                    "Impossibile caricare i match per la stanza corrente: ${e.message}"
            }
        }
    }

    /**
     * Scoring basato su likedMovies:
     * - +3 punti per genere in comune
     * - +2 * voto medio (imdbRating) * numero di attori in comune
     * - +2 punti se stesso regista
     * - moltiplica tutto per voto medio del candidato
     */
    private fun calculateScore(liked: List<Movie>, candidate: Movie): Double {
        val candGenres = candidate.genre?.split(", ") ?: emptyList()
        val candActors = candidate.actors?.split(", ") ?: emptyList()
        val candDir = candidate.director

        val baseScore = liked.sumOf { lm ->
            var s = 0
            // generi
            val commonG = lm.genre?.split(", ")?.count { it in candGenres } ?: 0
            s += 3 * commonG
            // attori
            val commonA = lm.actors?.split(", ")?.count { it in candActors } ?: 0
            s += (2 * commonA * (candidate.imdbRating?.toDoubleOrNull() ?: 1.0)).toInt()
            // regista
            if (lm.director == candDir) s += 2
            s
        }
        val vote = candidate.imdbRating?.toDoubleOrNull() ?: 1.0
        return baseScore * vote
    }

    /** Calcola i N elementi più frequenti nella lista */
    private fun <T> topN(list: List<T>, n: Int): List<T> =
        list.groupingBy { it }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .take(n)
            .map { it.key }

}