package it.namenotfoundexception.whats2watch.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDBService {

    // ricerca per titolo
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US"
    ): TmdbSearchResponse

    // dettagli base del film
    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") id: Int,
        @Query("language") language: String = "en-US"
    ): TmdbMovieDetailDto

    // credits per director e cast
    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(
        @Path("movie_id") id: Int
    ): TmdbCreditsResponse

    @GET("movie/{movie_id}/reviews")
    suspend fun getMovieReviews(
        @Path("movie_id") id: Int,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US"
    ): TmdbReviewsResponse

    // discover con filtri
    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("with_genres") genres: String? = null,
        @Query("with_cast") cast: String? = null,
        @Query("with_crew") crew: String? = null,
        @Query("vote_average.gte") voteGte: Float? = null,
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("primary_release_date.gte") releaseDateGte: String? = null,  // yyyy-MM-dd
        @Query("primary_release_date.lte") releaseDateLte: String? = null,  // yyyy-MM-dd
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US"
    ): TmdbSearchResponse

    // ricerca persona per ottenere ID attori/registi
    @GET("search/person")
    suspend fun searchPerson(
        @Query("query") name: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US"
    ): TmdbPersonSearchResponse

    @GET("genre/movie/list")
    suspend fun getGenreList(
        @Query("language") language: String = "en-US"
    ): TmdbGenreListResponse

}