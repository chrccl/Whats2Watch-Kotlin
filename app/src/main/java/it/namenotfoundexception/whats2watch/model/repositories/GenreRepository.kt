package it.namenotfoundexception.whats2watch.model.repositories

import it.namenotfoundexception.whats2watch.api.TMDBService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GenreRepository @Inject constructor(
    private val api: TMDBService,
) {
    private var cache: Map<String, Int>? = null

    suspend fun getGenreMap(): Map<String, Int> {
        // se ho in memoria → restituisco
        cache?.let { return it }

        // altrimenti chiamo TMDB
        val list = api.getGenreList().genres
        val map = list.associate { it.name to it.id }
        cache = map
        return map
    }
}