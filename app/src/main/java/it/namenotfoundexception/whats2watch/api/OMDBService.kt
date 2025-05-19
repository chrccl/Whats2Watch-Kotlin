package it.namenotfoundexception.whats2watch.api

import it.namenotfoundexception.whats2watch.BuildConfig
import it.namenotfoundexception.whats2watch.model.entities.Movie
import retrofit2.http.GET
import retrofit2.http.Query

interface OMDBService {

    @GET("/")
    suspend fun getMovieById(
        @Query("i") imdbId: String,
        @Query("type") type: String = "movie",
        @Query("apikey") apiKey: String = BuildConfig.OMDB_API_KEY
    ): Movie

    @GET("/")
    suspend fun searchMovies(
        @Query("s") query: String,
        @Query("type") type: String = "movie",
        @Query("page") page: Int?,
        @Query("apikey") apiKey: String = BuildConfig.OMDB_API_KEY
    ): OMDBSearchResponse

}