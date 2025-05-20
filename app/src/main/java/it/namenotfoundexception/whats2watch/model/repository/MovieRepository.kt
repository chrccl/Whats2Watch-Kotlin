package it.namenotfoundexception.whats2watch.model.repository

import it.namenotfoundexception.whats2watch.model.dao.MovieDao
import it.namenotfoundexception.whats2watch.model.entities.Movie
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val movieDao : MovieDao
) {

    suspend fun saveMovie(movie: Movie){
        movieDao.insertMovie(movie)
    }

    suspend fun getMovieById(id: String) : Movie{
        return movieDao.getMovieById(id)
    }
}