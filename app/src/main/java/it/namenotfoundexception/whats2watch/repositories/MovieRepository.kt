package it.namenotfoundexception.whats2watch.repositories

import it.namenotfoundexception.whats2watch.api.TMDBService
import it.namenotfoundexception.whats2watch.model.dao.MovieDao
import it.namenotfoundexception.whats2watch.model.entities.Movie
import it.namenotfoundexception.whats2watch.model.toMovieEntity
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val movieDao: MovieDao,
    private val api: TMDBService
) {

    suspend fun saveMovie(movie: Movie) {
        movieDao.insertMovie(movie)
    }

    suspend fun getMovieById(id: String): Movie {
        return movieDao.getMovieById(id)
    }

    suspend fun getMoviesByIds(ids: List<String>): List<Movie> = movieDao.getMoviesByIds(ids)

    suspend fun fetchAndMapMovie(movieId: Int): Movie {
        val detail = api.getMovieDetails(movieId)
        val credits = api.getMovieCredits(movieId)

        val director = credits.crew.firstOrNull { it.job == "Director" }?.name
        val actors = credits.cast
            .sortedBy { it.order }
            .take(5)
            .joinToString(", ") { it.name }

        return detail.toMovieEntity(director, actors)
    }

    suspend fun discoverFilteredMovies(
        genreIds: List<Int>? = null,
        actorNames: List<String>? = null,
        directorNames: List<String>? = null,
        voteAverageGte: Float? = null,
        releaseDateGte: String? = null,
        releaseDateLte: String? = null,
        sortBy: String = "popularity.desc",
        page: Int = 1
    ): List<Movie> {

        val genresParam = genreIds?.joinToString(",")
        val castParam = actorNames
            ?.mapNotNull { api.searchPerson(it).results.firstOrNull()?.id }
            ?.joinToString(",")
        val crewParam = directorNames
            ?.mapNotNull { name ->
                api.searchPerson(name).results.firstOrNull { it.name.equals(name, true) }?.id
            }
            ?.joinToString(",")

        val resp = api.discoverMovies(
            genres = genresParam,
            cast = castParam,
            crew = crewParam,
            voteGte = voteAverageGte,
            sortBy = sortBy,
            releaseDateGte = releaseDateGte,
            releaseDateLte = releaseDateLte,
            page = page
        )

        return resp.results.map { fetchAndMapMovie(it.id) }
    }

    suspend fun searchMovies(title: String, page: Int = 1): List<Movie> =
        api.searchMovies(title, page).results.map { it.toMovieEntity() }
}
