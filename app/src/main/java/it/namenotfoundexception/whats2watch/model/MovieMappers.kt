package it.namenotfoundexception.whats2watch.model

import it.namenotfoundexception.whats2watch.api.TmdbMovieDetailDto
import it.namenotfoundexception.whats2watch.api.TmdbMovieResultDto
import it.namenotfoundexception.whats2watch.model.entities.Movie

fun TmdbMovieResultDto.toMovieEntity(): Movie = Movie(
    imdbID    = id.toString(),
    title      = title,
    year       = release_date?.take(4) ?: "N/A",
    runtime    = null,
    genre      = null,
    director   = null,
    actors     = null,
    plot       = overview,
    awards     = null,
    poster     = poster_path?.let { "https://image.tmdb.org/t/p/w500$it" } ?: "",
    imdbRating = vote_average?.toString()
)

fun TmdbMovieDetailDto.toMovieEntity(director: String? = null, actors: String? = null): Movie =
    Movie(
    imdbID    = id.toString(),
    title      = title,
    year       = release_date?.take(4) ?: "N/A",
    runtime    = runtime?.let { "$it min" },
    genre      = genres.joinToString(", ") { it.name },
    director   = director,
    actors     = actors,
    plot       = overview,
    awards     = null,
    poster     = poster_path?.let { "https://image.tmdb.org/t/p/w500$it" } ?: "",
    imdbRating = vote_average?.toString()
)