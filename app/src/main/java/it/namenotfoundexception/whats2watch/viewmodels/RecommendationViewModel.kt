package it.namenotfoundexception.whats2watch.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.namenotfoundexception.whats2watch.model.entities.Movie
import it.namenotfoundexception.whats2watch.model.entities.Preference
import it.namenotfoundexception.whats2watch.repositories.GenreRepository
import it.namenotfoundexception.whats2watch.repositories.MovieRepository
import it.namenotfoundexception.whats2watch.repositories.PreferenceRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class RecommendationViewModel @Inject constructor(
    private val movieRepo: MovieRepository,
    private val genreRepo: GenreRepository,
    private val prefRepo: PreferenceRepository
) : ViewModel() {

    private val _suggestions = MutableStateFlow<List<Movie>>(mutableListOf<Movie>())
    val suggestions: StateFlow<List<Movie>> = _suggestions

    private val _roomMatches = MutableStateFlow<List<Movie>?>(null)
    val roomMatches: StateFlow<List<Movie>?> = _roomMatches

    private val _userLikedMovies = MutableStateFlow<List<Movie>?>(null)
    val userLikedMovies: StateFlow<List<Movie>?> = _userLikedMovies

    private val _recError = MutableStateFlow<String?>(null)
    val recError: StateFlow<String?> = _recError

    // Cache per migliorare le performance - thread safe
    private val cachedSuggestions = mutableListOf<Movie>()
    private var currentBatchIndex = 0
    private var lastLoadTime = 0L
    private var cachedUserPrefs: List<Preference>? = null
    private val cacheValidityMs = 30000L // 30 secondi
    private var isLoadingNewBatch = false // per evitare chiamate multiple

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
                _recError.value = "Impossibile reperire i like dell'utente: ${e.message}"
            }
        }
    }

    fun onMovieSwipe(
        roomCode: String,
        username: String,
        movie: Movie,
        liked: Boolean
    ) {
        viewModelScope.launch {
            try {
                movieRepo.saveMovie(movie)
                prefRepo.savePreference(
                    Preference(
                        roomCode = roomCode,
                        participantName = username,
                        movieId = movie.imdbID,
                        liked = liked
                    )
                )

                // Invalida cache se l'utente ha fatto like (per migliorare future raccomandazioni)
                if (liked) {
                    cachedUserPrefs = null
                }

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
        // Evita chiamate multiple simultanee
        if (isLoadingNewBatch) return

        viewModelScope.launch {
            try {
                isLoadingNewBatch = true
                val currentTime = System.currentTimeMillis()

                // Thread-safe access alla cache
                synchronized(cachedSuggestions) {
                    // Se abbiamo ancora film nella cache, usali
                    if (cachedSuggestions.size > currentBatchIndex + pageSize) {
                        val nextBatch = cachedSuggestions.subList(
                            currentBatchIndex,
                            (currentBatchIndex + pageSize).coerceAtMost(cachedSuggestions.size)
                        ).toList() // Crea copia per thread safety

                        _suggestions.value = nextBatch
                        currentBatchIndex += pageSize
                        return@launch
                    }

                    // Carica nuovi suggerimenti solo se necessario
                    if (currentTime - lastLoadTime < cacheValidityMs && cachedSuggestions.isNotEmpty()) {
                        // Usa i film rimanenti dalla cache
                        val remainingMovies = cachedSuggestions.subList(
                            currentBatchIndex,
                            cachedSuggestions.size
                        ).toList()

                        if (remainingMovies.isNotEmpty()) {
                            _suggestions.value = remainingMovies.take(pageSize)
                            currentBatchIndex = cachedSuggestions.size
                            return@launch
                        }
                    }
                }

                // Carica nuovi suggerimenti
                loadNewSuggestions(roomCode, username)

            } catch (e: Exception) {
                _recError.value = "Errore caricamento batch: ${e.localizedMessage}"
            } finally {
                isLoadingNewBatch = false
            }
        }
    }


    private suspend fun loadNewSuggestions(
        roomCode: String,
        username: String
    ) {
        // 1) Ottieni preferenze utente (con cache)
        val userPrefs = cachedUserPrefs ?: run {
            val prefs = prefRepo.getPreferencesByUser(roomCode, username)
            cachedUserPrefs = prefs
            prefs
        }

        val likedPrefs = userPrefs.filter { it.liked }
        val allSeenIds = userPrefs.map { it.movieId }.toSet()

        val likedMovies = if (likedPrefs.isNotEmpty()) {
            movieRepo.getMoviesByIds(likedPrefs.map { it.movieId })
        } else emptyList()

        // 2) Strategia diversificata: mescola diversi approcci
        val allCandidates = mutableListOf<Movie>()

        // 3) Parallelizza le chiamate per migliorare le performance
        val results: List<List<Movie>> = coroutineScope {
            // Here we explicitly annotate each async as returning List<Movie>
            val deferredPersonal: Deferred<List<Movie>> = async<List<Movie>> {
                if (likedMovies.isNotEmpty())
                    getPersonalizedRecommendations(likedMovies, allSeenIds)
                else
                    emptyList()
            }
            val deferredPopular: Deferred<List<Movie>> = async<List<Movie>> {
                getPopularMovies(allSeenIds)
            }
            val deferredGenre: Deferred<List<Movie>> = async<List<Movie>> {
                getRandomGenreMovies(allSeenIds)
            }
            val deferredEra: Deferred<List<Movie>> = async<List<Movie>> {
                getDiverseEraMovies(allSeenIds)
            }

            // Now the compiler knows each is Deferred<List<Movie>>
            listOf(
                deferredPersonal,
                deferredPopular,
                deferredGenre,
                deferredEra
            ).awaitAll()
        }

        results.forEach { allCandidates.addAll(it) }

        // 4) Rimuovi duplicati e mescola
        val uniqueCandidates = allCandidates.distinctBy { it.imdbID }.toMutableList()

        // 5) Se l'utente ha dei like, usa scoring personalizzato, altrimenti randomizza
        val finalSuggestions = if (likedMovies.isNotEmpty()) {
            val scored = uniqueCandidates
                .map { it to calculateScore(likedMovies, it) }
                .sortedByDescending { it.second }
                .map { it.first }

            // Mescola i top scored per aggiungere varietà
            val topHalf = scored.take(scored.size / 2).shuffled()
            val bottomHalf = scored.drop(scored.size / 2).shuffled()
            topHalf + bottomHalf
        } else {
            uniqueCandidates.shuffled()
        }

        // 6) Aggiorna cache
        synchronized(cachedSuggestions) {
            cachedSuggestions.clear()
            cachedSuggestions.addAll(finalSuggestions)
            currentBatchIndex = 0
            lastLoadTime = System.currentTimeMillis()
        }

        // 7) Restituisci il primo batch
        val firstBatch = finalSuggestions.take(20)
        _suggestions.value = firstBatch

        synchronized(cachedSuggestions) {
            currentBatchIndex = 20
        }

        _recError.value = null
    }

    private suspend fun getPersonalizedRecommendations(
        likedMovies: List<Movie>,
        seenIds: Set<String>
    ): List<Movie> {
        try {
            // Calcola preferenze con maggiore flessibilità
            val topGenres = topN(likedMovies.flatMap { it.genre.orEmpty().split(", ") }, 2)

            // Range anni più ampio (±10 anni invece di ±5)
            val avgYear = likedMovies
                .mapNotNull { it.year.toIntOrNull() }
                .takeIf { it.isNotEmpty() }?.average()?.toInt()

            val (gteDate, lteDate) = avgYear?.let {
                "${(it - 10).coerceAtLeast(1950)}-01-01" to "${(it + 10).coerceAtMost(LocalDate.now().year)}-12-31"
            } ?: (null to null)

            val genreMap = genreRepo.getGenreMap()
            val genreIds = topGenres.mapNotNull { genreMap[it] }

            return movieRepo.discoverFilteredMovies(
                genreIds = genreIds,
                voteAverageGte = 5.5f, // Soglia più bassa
                releaseDateGte = gteDate,
                releaseDateLte = lteDate,
                sortBy = "vote_count.desc" // Ordina per numero voti invece che popolarità
            ).filter { it.imdbID !in seenIds }

        } catch (_: Exception) {
            return emptyList()
        }
    }

    private suspend fun getPopularMovies(seenIds: Set<String>): List<Movie> {
        return try {
            // Randomizza l'anno per evitare sempre gli stessi film
            val randomYear = Random.nextInt(1990, LocalDate.now().year - 1)
            val gteDate = "${randomYear - 5}-01-01"
            val lteDate = "${randomYear + 5}-12-31"

            movieRepo.discoverFilteredMovies(
                voteAverageGte = 7.0f,
                releaseDateGte = gteDate,
                releaseDateLte = lteDate,
                sortBy = "popularity.desc",
                page = Random.nextInt(1, 6) // Randomizza la pagina
            ).filter { it.imdbID !in seenIds }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private suspend fun getRandomGenreMovies(seenIds: Set<String>): List<Movie> {
        return try {
            val genreMap = genreRepo.getGenreMap()
            val randomGenres = genreMap.values.shuffled().take(2)

            movieRepo.discoverFilteredMovies(
                genreIds = randomGenres,
                voteAverageGte = 6.0f,
                sortBy = listOf("popularity.desc", "vote_average.desc", "release_date.desc").random(),
                page = Random.nextInt(1, 4)
            ).filter { it.imdbID !in seenIds }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private suspend fun getDiverseEraMovies(seenIds: Set<String>): List<Movie> {
        return try {
            // Scegli decade casuale
            val decades = listOf(
                1970 to 1979,
                1980 to 1989,
                1990 to 1999,
                2000 to 2009,
                2010 to 2019,
                2020 to LocalDate.now().year
            )

            val (startYear, endYear) = decades.random()

            movieRepo.discoverFilteredMovies(
                voteAverageGte = 6.5f,
                releaseDateGte = "$startYear-01-01",
                releaseDateLte = "$endYear-12-31",
                sortBy = "vote_count.desc",
                page = Random.nextInt(1, 3)
            ).filter { it.imdbID !in seenIds }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun getRoomMatches(code: String) {
        viewModelScope.launch {
            try {
                _roomMatches.value = prefRepo.getRoomMatches(code)
                _recError.value = null
            } catch (e: Exception) {
                _recError.value = "Impossibile caricare i match: ${e.message}"
            }
        }
    }

    /**
     * Scoring ottimizzato e più bilanciato
     */
    private fun calculateScore(liked: List<Movie>, candidate: Movie): Double {
        if (liked.isEmpty()) return Random.nextDouble(0.5, 1.0)

        val candGenres = candidate.genre?.split(", ")?.map { it.trim() } ?: emptyList()
        val candActors = candidate.actors?.split(", ")?.map { it.trim() } ?: emptyList()
        val candDir = candidate.director?.trim()
        val candRating = candidate.imdbRating?.toDoubleOrNull() ?: 6.0

        var totalScore = 0.0
        var matchCount = 0

        liked.forEach { likedMovie ->
            val likedGenres = likedMovie.genre?.split(", ")?.map { it.trim() } ?: emptyList()
            val likedActors = likedMovie.actors?.split(", ")?.map { it.trim() } ?: emptyList()
            val likedDir = likedMovie.director?.trim()

            // Scoring per generi (peso maggiore)
            val genreMatches = candGenres.count { it in likedGenres }
            totalScore += genreMatches * 2.0

            // Scoring per attori (peso medio)
            val actorMatches = candActors.count { it in likedActors }
            totalScore += actorMatches * 1.5

            // Scoring per regista (peso alto)
            if (candDir != null && candDir == likedDir) {
                totalScore += 3.0
            }

            if (genreMatches > 0 || actorMatches > 0 || candDir == likedDir) {
                matchCount++
            }
        }

        // Normalizza il punteggio e aggiungi bonus per rating
        val normalizedScore = if (matchCount > 0) totalScore / matchCount else 0.0
        val ratingBonus = (candRating / 10.0) * 0.5
        val randomFactor = Random.nextDouble(0.8, 1.2) // Aggiunge varietà

        return (normalizedScore + ratingBonus) * randomFactor
    }

    /** Calcola i N elementi più frequenti nella lista */
    private fun topN(list: List<String>, n: Int): List<String> =
        list.filter { it.isNotBlank() }
            .groupingBy { it.trim() }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .take(n)
            .map { it.key }
}